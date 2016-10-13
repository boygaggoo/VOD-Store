/**
 * 
 */
package com.cookingshow.datacenter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author Administrator
 *
 */
public class UpdateDataProvider extends ContentObservable {
	public static final String TAG = "UpdateDataProvider";
	public static final String UPDATE_CONTENT_URI = "content://com.cookingshow.service.provider/data_update";
   
    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final Handler mUiHandler;
    private UpdateDataInfo mUpdateDataInfo = null;
    private final Object mLock;
    private ContentObserver mUpdateContentObserver;
    private boolean isQuery = false;

    public UpdateDataProvider(Context context, Handler uiHandler) {
        mContext = context;
        mUiHandler = uiHandler;

        mContentresolver = context.getContentResolver();
        mLock = new Object();
        
        registerObserver();
    }
 
    private int getVersionCode(Context context) {
    	int versionCode = 0;
    	
    	try {
    		versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
    	} catch (NameNotFoundException e) {
    		e.printStackTrace();
    	}
    	
    	return versionCode;
    }

    public void destroy() {
        Log.i(TAG, "destroy");
        unregisterObserver();

        synchronized (mLock) {
        	mUpdateDataInfo = null;
        }
    }

    private void getUpdateData() {
        synchronized (mLock) {
            mUpdateDataInfo = queryUpdateDataInfo();
            if(mUpdateDataInfo != null) {
                Log.i(TAG, mUpdateDataInfo.mVersion);
                Log.i(TAG, mUpdateDataInfo.mName);
                Log.i(TAG, mUpdateDataInfo.mLink);
                isQuery = true;

        	    int serviceCode = Integer.valueOf(mUpdateDataInfo.mVersion);
        	    if(serviceCode > getVersionCode(mContext)) {
        		    Message updateMsg = Message.obtain();
        		    Bundle bundle = new Bundle();
        		    bundle.putString("updateUrl", mUpdateDataInfo.mLink);
        		    bundle.putString("updateName", mUpdateDataInfo.mName);
        		    updateMsg.setData(bundle);
        		    updateMsg.what = 2;
        		    mUiHandler.sendMessage(updateMsg);                    		
        	    }
            }
        }
    }
 
    private UpdateDataInfo queryUpdateDataInfo() {
    	UpdateDataInfo updateDataInfo = null;
    	Cursor c = null;
 
        try {
            c = mContentresolver.query(Uri.parse(UPDATE_CONTENT_URI), null, "CONTENT_ID=?",
                    new String[] {
                        "update"
                    }, null);
            if (c != null && c.moveToNext()) {
            	updateDataInfo = new UpdateDataInfo(c.getString(2), c.getString(3), c.getString(4));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        
    	return updateDataInfo;
    }

    private void loadUpdateDataInfo() {
    	if(!isQuery) {
    	    getUpdateData();
    	}
    }

    private void registerObserver() {
        if (mUpdateContentObserver == null) {
        	mUpdateContentObserver = new ContentObserver(new Handler()) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);                    
                    loadUpdateDataInfo();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(UPDATE_CONTENT_URI), true,
            		mUpdateContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mUpdateContentObserver != null) {
            mContentresolver.unregisterContentObserver(mUpdateContentObserver);
            mUpdateContentObserver = null;
        }
    }
}

package com.cookingshow.datacenter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CookStepProvider extends ContentObservable{
    public static final String TAG = "CookStepProvider";
    public static final String COOK_STEP_DATA_CONTENT_URI = "content://com.cookingshow.service.provider/cook_step_data";
    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final Handler mUiHandler;
    private ContentObserver mCookStepDataContentObserver;
    
	public CookStepProvider(Context context, Handler uiHandler) {
		// TODO Auto-generated constructor stub
        mContext = context;
        mUiHandler = uiHandler;
        mContentresolver = context.getContentResolver();

        registerObserver();
	}

    public void destroy() {
        Log.i(TAG, "destroy");
        unregisterObserver();
    }
    
    public List<ListItem> getCookStepData(int dishId) {
    	List<ListItem> ads = new ArrayList<ListItem>();
            
        ads.addAll(queryCookStepDataInfoList(dishId));

        return ads;
    }

    private ArrayList<ListItem> queryCookStepDataInfoList(int id) {
        Log.i(TAG, "queryCookStepDataInfoList");
        ArrayList<ListItem> datas = new ArrayList<ListItem>();
        Cursor c = null;

        try {
            c = mContentresolver.query(Uri.parse(COOK_STEP_DATA_CONTENT_URI), null, "CONTENT_ID=? and DISH_ID=?",
                    new String[] {
                        "cookStepData",
                        String.valueOf(id)
                    }, null);

            while (c != null && c.moveToNext()) {
            	ListItem data = new ListItem();
            	data.setItemTitle1(String.valueOf(c.getInt(3)));
            	data.setItemText(c.getString(4));
            	datas.add(data);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return datas;
    }

    private void notifyRefreshData() {
        Message msg = Message.obtain();
        msg.what = 1;
        mUiHandler.sendMessage(msg);                    		
    }

    private void loadCookStepDataInfo() {
    	notifyRefreshData();
    }
    
    private void registerObserver() {
        if (mCookStepDataContentObserver == null) {
        	mCookStepDataContentObserver = new ContentObserver(new Handler()) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    loadCookStepDataInfo();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(COOK_STEP_DATA_CONTENT_URI), true,
            		mCookStepDataContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mCookStepDataContentObserver != null) {
            mContentresolver.unregisterContentObserver(mCookStepDataContentObserver);
            mCookStepDataContentObserver = null;
        }
    }

}

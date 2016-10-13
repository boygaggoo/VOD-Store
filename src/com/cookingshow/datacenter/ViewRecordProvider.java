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

public class ViewRecordProvider extends ContentObservable {

    public static final String TAG = "ViewRecordProvider";
    public static final String VIEW_RECORD_DATA_CONTENT_URI = "content://com.cookingshow.service.provider/my_view_record_data";
    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final Handler mUiHandler;
    private ContentObserver mViewRecordDataContentObserver;
    
	public ViewRecordProvider(Context context, Handler uiHandler) {
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
    
    public List<DishDataInfo> getViewRecordData() {
    	List<DishDataInfo> ads = new ArrayList<DishDataInfo>();
            
        ads.addAll(queryViewRecordDataInfoList());

        return ads;
    }

    private ArrayList<DishDataInfo> queryViewRecordDataInfoList() {
        Log.i(TAG, "queryViewRecordDataInfoList");
        ArrayList<DishDataInfo> datas = new ArrayList<DishDataInfo>();
        Cursor c = null;

        try {
            c = mContentresolver.query(Uri.parse(VIEW_RECORD_DATA_CONTENT_URI), null, "CONTENT_ID=?",
                    new String[] {
                        "viewRecordData"
                    }, null);

            while (c != null && c.moveToNext()) {
            	DishDataInfo data = new DishDataInfo();
            	data.setAlbumId(c.getInt(1));
            	data.setDishId(c.getInt(2));
            	data.setUploader(c.getString(3));
            	data.setTitle(c.getString(4));
            	data.setThumbUrl(c.getString(5));
            	data.setVideoUrl(c.getString(6));
            	data.setTips(c.getString(7));
            	data.setMaterials(c.getString(8));
            	data.setViewTimes(c.getInt(9));
            	data.setLikeTimes(c.getInt(10));

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
    	Log.e(TAG, "notifyRefreshData");
        Message msg = Message.obtain();
        msg.what = 3;
        mUiHandler.sendMessage(msg);                    		
    }

    private void notifyEmptyData() {
    	Log.e(TAG, "notifyEmptyData");
        Message msg = Message.obtain();
        msg.what = 4;
        mUiHandler.sendMessage(msg);                    		
    }

    private void loadViewRecordInfo() {
    	if(queryViewRecordDataInfoList().size() > 0) {
    		notifyRefreshData();
    	}
    	else {
    		notifyEmptyData();
    	}
    }
    
    private void registerObserver() {
        if (mViewRecordDataContentObserver == null) {
        	mViewRecordDataContentObserver = new ContentObserver(new Handler()) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    loadViewRecordInfo();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(VIEW_RECORD_DATA_CONTENT_URI), true,
            		mViewRecordDataContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mViewRecordDataContentObserver != null) {
            mContentresolver.unregisterContentObserver(mViewRecordDataContentObserver);
            mViewRecordDataContentObserver = null;
        }
    }

}

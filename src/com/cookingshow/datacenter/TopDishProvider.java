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
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

public class TopDishProvider extends ContentObservable {
    public static final String TAG = "TopDishProvider";
    public static final String TOP_DISH_DATA_CONTENT_URI = "content://com.cookingshow.service.provider/top_data";
    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final Handler mUiHandler;
    private final ArrayList<PageContent> mTopDishDataInfos;
    private final Object mLock;
    private ContentObserver mTopDishDataContentObserver;
    
	public TopDishProvider(Context context, Handler uiHandler) {
		// TODO Auto-generated constructor stub
        mContext = context;
        mUiHandler = uiHandler;
        mContentresolver = context.getContentResolver();
        mTopDishDataInfos = new ArrayList<PageContent>();
        mLock = new Object();

        registerObserver();
	}

    public void destroy() {
        Log.i(TAG, "destroy");
        unregisterObserver();

        synchronized (mLock) {
        	mTopDishDataInfos.clear();
        }
    }

    private void getTopDishDataList() {
        synchronized (mLock) {
            Log.i(TAG, "getTopDishDataList");
            mTopDishDataInfos.clear();
            mTopDishDataInfos.addAll(queryTopDataInfoList());
        }
    }
    
    public List<PageContent> getTopDishData() {
    	List<PageContent> ads = new ArrayList<PageContent>();
    	
        synchronized (mLock) {
            Log.i(TAG, "getTopDishData");
            if(mTopDishDataInfos.size() == 0) {
            	getTopDishDataList();
            }
            
            ads.addAll(mTopDishDataInfos);
        }

        return ads;
    }

    private ArrayList<PageContent> queryTopDataInfoList() {
        Log.i(TAG, "queryTopDataInfoList");
        ArrayList<PageContent> topDatas = new ArrayList<PageContent>();
        Cursor c = null;
        int i = 0;

        try {
            c = mContentresolver.query(Uri.parse(TOP_DISH_DATA_CONTENT_URI), null, "CONTENT_ID=?",
                    new String[] {
                        "topData"
                    }, null);

            while (c != null && c.moveToNext()) {
            	PageContent topData = new PageContent();
            	topData.setId(c.getInt(2));
            	topData.setUploader(c.getString(3));
            	topData.setName(c.getString(4));
            	topData.setThumbUrl(c.getString(5));
            	topData.setVideoUrl(c.getString(6));
            	topData.setTips(c.getString(7));
            	topData.setMaterials(c.getString(8));
            	topData.setPosition(++i);
            	topDatas.add(topData);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return topDatas;
    }

    private void notifyRefreshData() {
        Message msg = Message.obtain();
        msg.what = 1;
        mUiHandler.sendMessage(msg);                    		
    }

    private void loadTopDataInfo() {
    	notifyRefreshData();
    }
    
    private void registerObserver() {
        if (mTopDishDataContentObserver == null) {
        	mTopDishDataContentObserver = new ContentObserver(new Handler()) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    loadTopDataInfo();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(TOP_DISH_DATA_CONTENT_URI), true,
            		mTopDishDataContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mTopDishDataContentObserver != null) {
            mContentresolver.unregisterContentObserver(mTopDishDataContentObserver);
            mTopDishDataContentObserver = null;
        }
    }
}

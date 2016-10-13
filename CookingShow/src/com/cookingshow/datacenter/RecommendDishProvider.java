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

public class RecommendDishProvider extends ContentObservable{
    public static final String TAG = "RecommendDishProvider";
    public static final String RECOMMEND_DISH_DATA_CONTENT_URI = "content://com.cookingshow.service.provider/recommend_data";
    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final Handler mUiHandler;
    private final ArrayList<PageContent> mRecommendDishDataInfos;
    private final Object mLock;
    private ContentObserver mRecommendDishDataContentObserver;
    private boolean isQuery = false;

	public RecommendDishProvider(Context context, Handler uiHandler) {
		// TODO Auto-generated constructor stub
        mContext = context;
        mUiHandler = uiHandler;
        mContentresolver = context.getContentResolver();
        mRecommendDishDataInfos = new ArrayList<PageContent>();
        mLock = new Object();

        registerObserver();
	}

    public void destroy() {
        Log.i(TAG, "destroy");
        unregisterObserver();

        synchronized (mLock) {
        	mRecommendDishDataInfos.clear();
        }
    }

    private void getRecommendDishDataList() {
        synchronized (mLock) {
            Log.i(TAG, "getRecommendDishDataList");
            mRecommendDishDataInfos.clear();
            mRecommendDishDataInfos.addAll(queryRecommendDataInfoList());
        }
    }
    
    public List<PageContent> getRecommendDishData() {
    	List<PageContent> ads = new ArrayList<PageContent>();
    	
        synchronized (mLock) {
            Log.i(TAG, "getRecommendDishData");
            if(mRecommendDishDataInfos.size() == 0) {
            	getRecommendDishDataList();
            }
            
            ads.addAll(mRecommendDishDataInfos);
        }

        return ads;
    }

    private ArrayList<PageContent> queryRecommendDataInfoList() {
        Log.i(TAG, "queryRecommendDataInfoList");
        ArrayList<PageContent> recommendDatas = new ArrayList<PageContent>();
        Cursor c = null;
        int i = 0;

        try {
            c = mContentresolver.query(Uri.parse(RECOMMEND_DISH_DATA_CONTENT_URI), null, "CONTENT_ID=?",
                    new String[] {
                        "recommendData"
                    }, null);

            while (c != null && c.moveToNext()) {
            	PageContent recommendData = new PageContent();
            	recommendData.setId(c.getInt(2));
            	recommendData.setUploader(c.getString(3));
            	recommendData.setName(c.getString(4));
            	recommendData.setThumbUrl(c.getString(5));
            	recommendData.setVideoUrl(c.getString(6));
            	recommendData.setTips(c.getString(7));
            	recommendData.setMaterials(c.getString(8));
            	recommendData.setPosition(++i);
            	recommendDatas.add(recommendData);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return recommendDatas;
    }

    private void notifyRefreshData() {
        isQuery = true;
        Message msg = Message.obtain();
        msg.what = 1;
        mUiHandler.sendMessage(msg);                    		
    }
    
    private void loadRecommendDataInfo() {
    	//if(!isQuery) {
    	    notifyRefreshData();
    	//}
    }

    private void registerObserver() {
        if (mRecommendDishDataContentObserver == null) {
        	mRecommendDishDataContentObserver = new ContentObserver(new Handler()) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    loadRecommendDataInfo();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(RECOMMEND_DISH_DATA_CONTENT_URI), true,
            		mRecommendDishDataContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mRecommendDishDataContentObserver != null) {
            mContentresolver.unregisterContentObserver(mRecommendDishDataContentObserver);
            mRecommendDishDataContentObserver = null;
        }
    }
}

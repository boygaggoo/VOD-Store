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

public class DishDataProvider extends ContentObservable {
    public static final String TAG = "DishDataProvider";
    public static final String DISH_DATA_CONTENT_URI = "content://com.cookingshow.service.provider/dish_data";
    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final ArrayList<DishDataInfo> mDishDataInfos;
    private final Object mLock;
    private ContentObserver mDishDataContentObserver;
    private String currCode = null;
    
	public DishDataProvider(Context context) {
		// TODO Auto-generated constructor stub
        mContext = context;
        mContentresolver = context.getContentResolver();
        mDishDataInfos = new ArrayList<DishDataInfo>();
        mLock = new Object();

        registerObserver();
	}

    public void destroy() {
        Log.i(TAG, "destroy");
        unregisterObserver();

        synchronized (mLock) {
        	mDishDataInfos.clear();
        }
    }

    private void getDishDataByCode(String code) {
        synchronized (mLock) {
            Log.i(TAG, "getDishDataByCode");
            mDishDataInfos.clear();
            mDishDataInfos.addAll(queryDishDataInfoList(code));
            Log.i(TAG, Integer.toString(mDishDataInfos.size()));
        }
    }

    private ArrayList<DishDataInfo> getDishDataList(String code, int si, int count) {
    	ArrayList<DishDataInfo> dishDatas = new ArrayList<DishDataInfo>();
    	
    	if(mDishDataInfos.size() == 0) {
    		mDishDataInfos.addAll(queryDishDataInfoList(code));
    	}
    	else if (null!= currCode && !currCode.equals(code)){
    		mDishDataInfos.clear();
    		mDishDataInfos.addAll(queryDishDataInfoList(code));
    	}

    	currCode = code;
    	
    	for(int i = 0; i < count; i++, si++) {
    		if(si < mDishDataInfos.size()) {
    			dishDatas.add(mDishDataInfos.get(si));
    		}
    		else {
    			break;
    		}    		
    	}
    	
    	return dishDatas;
    }
    
    private int getDishDataSize() {
   	
    	return mDishDataInfos.size();
    }
    
    public  AppListResponse getAppListFromDb(int si, int c, String code) {
    	AppListResponse res = new AppListResponse();

    	ArrayList<DishDataInfo> dishDatas = null;
    	   	
    	dishDatas = getDishDataList(code, si, c);
    	if(null != dishDatas && dishDatas.size() > 0) {
    		res.setDishDataInfos(dishDatas);
    		res.setmIsSuccess(true);
        	res.setAllCount(getDishDataSize());
    	}

        return res;  	
    }
    
    private ArrayList<DishDataInfo> queryDishDataInfoList(String type) {
        Log.i(TAG, "queryDishDataInfoList");
        ArrayList<DishDataInfo> dishDatas = new ArrayList<DishDataInfo>();
        Cursor c = null;

        try {
            c = mContentresolver.query(Uri.parse(DISH_DATA_CONTENT_URI), null, "CONTENT_ID=? and TYPE=?",
                    new String[] {
                        "dishData",
                        type
                    }, "UPLOAD_TIME desc");
            while (c != null && c.moveToNext()) {
            	DishDataInfo dishData = new DishDataInfo();
            	dishData.setDishId(c.getInt(2));
            	dishData.setUploadTime(c.getString(3));
            	dishData.setUploader(c.getString(4));
            	dishData.setTitle(c.getString(5));
            	dishData.setType(c.getString(6));
            	dishData.setThumbUrl(c.getString(7));
            	dishData.setVideoUrl(c.getString(8));
            	dishData.setTips(c.getString(9));
            	dishData.setMaterials(c.getString(10));
            	dishDatas.add(dishData);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return dishDatas;
    }

    private void loadDishDataInfo() {
        if(null!= currCode) {
        	getDishDataByCode(currCode);
        }
    }
    
    private void registerObserver() {
        if (mDishDataContentObserver == null) {
        	mDishDataContentObserver = new ContentObserver(new Handler()) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    loadDishDataInfo();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(DISH_DATA_CONTENT_URI), true,
            		mDishDataContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mDishDataContentObserver != null) {
            mContentresolver.unregisterContentObserver(mDishDataContentObserver);
            mDishDataContentObserver = null;
        }
    }
}

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

public class ShareVideoProvider extends ContentObservable {

    public static final String TAG = "ShareVideoProvider";
    public static final String SHARE_VIDEO_DATA_CONTENT_URI = "content://com.cookingshow.service.provider/share_video_data";
    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final Handler mUiHandler;
    private ContentObserver mShareVideoDataContentObserver;
    
	public ShareVideoProvider(Context context, Handler uiHandler) {
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
    
    public List<ShareListItem> getShareVideoData() {
    	List<ShareListItem> ads = new ArrayList<ShareListItem>();
            
        ads.addAll(queryShareVideoDataInfoList());

        return ads;
    }

    private ArrayList<ShareListItem> queryShareVideoDataInfoList() {
        Log.i(TAG, "queryShareVideoDataInfoList");
        ArrayList<ShareListItem> datas = new ArrayList<ShareListItem>();
        Cursor c = null;

        try {
            c = mContentresolver.query(Uri.parse(SHARE_VIDEO_DATA_CONTENT_URI), null, "CONTENT_ID=?",
                    new String[] {
                        "shareVideoData"
                    }, null);

            while (c != null && c.moveToNext()) {
            	ShareListItem data = new ShareListItem();
            	
            	data.setShareId(c.getInt(1));
            	data.setDishName(c.getString(4));
            	data.setUploaderTime(c.getString(2));
            	data.setUploader(c.getString(3));
            	data.setUrl(c.getString(5));

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
        msg.what = 1;
        mUiHandler.sendMessage(msg);                    		
    }

    private void loadShareVideoInfo() {
    	if(queryShareVideoDataInfoList().size() > 0) {
    		notifyRefreshData();
    	}
    }
    
    private void registerObserver() {
        if (mShareVideoDataContentObserver == null) {
        	mShareVideoDataContentObserver = new ContentObserver(new Handler()) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    loadShareVideoInfo();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(SHARE_VIDEO_DATA_CONTENT_URI), true,
            		mShareVideoDataContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mShareVideoDataContentObserver != null) {
            mContentresolver.unregisterContentObserver(mShareVideoDataContentObserver);
            mShareVideoDataContentObserver = null;
        }
    }
}

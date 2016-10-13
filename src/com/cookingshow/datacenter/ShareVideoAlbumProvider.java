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

public class ShareVideoAlbumProvider extends ContentObservable {

    public static final String TAG = "TopDishProvider";
    public static final String VIDEO_ALBUM_DATA_CONTENT_URI = "content://com.cookingshow.service.provider/share_video_album_data";
    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final Handler mUiHandler;
    private final ArrayList<PageContent> mVideoAlbumDataInfos;
    private final Object mLock;
    private ContentObserver mVideoAlbumDataContentObserver;
    
	public ShareVideoAlbumProvider(Context context, Handler uiHandler) {
		// TODO Auto-generated constructor stub
        mContext = context;
        mUiHandler = uiHandler;
        mContentresolver = context.getContentResolver();
        mVideoAlbumDataInfos = new ArrayList<PageContent>();
        mLock = new Object();

        registerObserver();
	}

    public void destroy() {
        Log.i(TAG, "destroy");
        unregisterObserver();

        synchronized (mLock) {
        	mVideoAlbumDataInfos.clear();
        }
    }

    private void getVideoAlbumDataList() {
        synchronized (mLock) {
            Log.i(TAG, "getVideoAlbumDataList");
            mVideoAlbumDataInfos.clear();
            mVideoAlbumDataInfos.addAll(queryVideoAlbumInfoList());
        }
    }
    
    public List<PageContent> getVideoAlbumData() {
    	List<PageContent> ads = new ArrayList<PageContent>();
    	
        synchronized (mLock) {
            Log.i(TAG, "getVideoAlbumData");
            if(mVideoAlbumDataInfos.size() == 0) {
            	getVideoAlbumDataList();
            }
            
            ads.addAll(mVideoAlbumDataInfos);
        }

        return ads;
    }

    private ArrayList<PageContent> queryVideoAlbumInfoList() {
        Log.i(TAG, "queryVideoAlbumInfoList");
        ArrayList<PageContent> albumDatas = new ArrayList<PageContent>();
        Cursor c = null;
        int i = 0;

        try {
            c = mContentresolver.query(Uri.parse(VIDEO_ALBUM_DATA_CONTENT_URI), null, "CONTENT_ID=?",
                    new String[] {
                        "shareVideoAlbumData"
                    }, null);

            while (c != null && c.moveToNext()) {
            	PageContent albumData = new PageContent();
            	albumData.setId(c.getInt(2));
            	albumData.setUploader(c.getString(3));
            	albumData.setName(c.getString(4));
            	albumData.setThumbUrl(c.getString(5));
            	albumData.setTips(c.getString(6));
            	albumData.setPosition(++i);
            	albumDatas.add(albumData);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return albumDatas;
    }

    private void notifyRefreshData() {
        Message msg = Message.obtain();
        msg.what = 1;
        mUiHandler.sendMessage(msg);                    		
    }

    private void loadAlbumDataInfo() {
    	if(queryVideoAlbumInfoList().size() > 0) {
    		notifyRefreshData();
    	}    	
    }
    
    private void registerObserver() {
        if (mVideoAlbumDataContentObserver == null) {
        	mVideoAlbumDataContentObserver = new ContentObserver(new Handler()) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    loadAlbumDataInfo();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(VIDEO_ALBUM_DATA_CONTENT_URI), true,
            		mVideoAlbumDataContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mVideoAlbumDataContentObserver != null) {
            mContentresolver.unregisterContentObserver(mVideoAlbumDataContentObserver);
            mVideoAlbumDataContentObserver = null;
        }
    }

}

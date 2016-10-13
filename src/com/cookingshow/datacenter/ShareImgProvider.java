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

public class ShareImgProvider extends ContentObservable {

    public static final String TAG = "ShareImgProvider";
    public static final String SHARE_IMG_DATA_CONTENT_URI = "content://com.cookingshow.service.provider/share_img_data";
    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final Handler mUiHandler;
    private ContentObserver mShareImgDataContentObserver;
    
	public ShareImgProvider(Context context, Handler uiHandler) {
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
    
    public List<ShareListItem> getShareImgData() {
    	List<ShareListItem> ads = new ArrayList<ShareListItem>();
            
        ads.addAll(queryShareImgDataInfoList());

        return ads;
    }

    private ArrayList<ShareListItem> queryShareImgDataInfoList() {
        Log.i(TAG, "queryShareImgDataInfoList");
        ArrayList<ShareListItem> datas = new ArrayList<ShareListItem>();
        Cursor c = null;

        try {
            c = mContentresolver.query(Uri.parse(SHARE_IMG_DATA_CONTENT_URI), null, "CONTENT_ID=?",
                    new String[] {
                        "shareImgData"
                    }, null);

            while (c != null && c.moveToNext()) {
            	ShareListItem data = new ShareListItem();
            	
            	data.setShareId(c.getInt(1));
            	data.setDishName(c.getString(3));
            	data.setUploaderTime(c.getString(4));
            	data.setUploader(c.getString(5));
            	data.setUrl(c.getString(6));
            	data.setFeeling(c.getString(7));
            	data.setLikeTimes(c.getInt(8));
            	data.setPublished(c.getInt(9));

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

    private void loadShareImgInfo() {
    	if(queryShareImgDataInfoList().size() > 0) {
    		notifyRefreshData();
    	}
    }
    
    private void registerObserver() {
        if (mShareImgDataContentObserver == null) {
        	mShareImgDataContentObserver = new ContentObserver(new Handler()) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    loadShareImgInfo();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(SHARE_IMG_DATA_CONTENT_URI), true,
            		mShareImgDataContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mShareImgDataContentObserver != null) {
            mContentresolver.unregisterContentObserver(mShareImgDataContentObserver);
            mShareImgDataContentObserver = null;
        }
    }

}

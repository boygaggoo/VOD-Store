package com.cookingshow.datacenter;

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

public class BootScreenProvider extends ContentObservable {
	public static final String TAG = "BootScreenProvider";
	public static final String BOOT_SCREEN_CONTENT_URI = "content://com.cookingshow.service.provider/boot_screen";
    private static final int MSG_RELOAD_BOOT_SCREEN_ITEMS = 101;
    private static final long RELOAD_ITEMS_DELAY_MS = 1000;

    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final Handler mUiHandler;
    private BootScreenInfo mBootScreeInfo = null;
    private final HandlerThread mhandlerThread;
    private final Handler mHandler;
    private final Object mLock;
    private ContentObserver mBootScreenContentObserver;
    
    public BootScreenProvider(Context context, Handler uiHandler) {
        mContext = context;
        mUiHandler = uiHandler;

        mContentresolver = context.getContentResolver();
        mLock = new Object();
 
        mhandlerThread = new HandlerThread("boot_Screen_provider");
        mhandlerThread.start();
        mHandler = new Handler(mhandlerThread.getLooper()) {

            public void hanleMessage(Message msg) {
                if (msg.what == MSG_RELOAD_BOOT_SCREEN_ITEMS) {
                    Log.i(TAG, "handleMessage");
        		    Message bootScreenMsg = Message.obtain();
        		    bootScreenMsg.what = 2;
        		    mUiHandler.sendMessage(bootScreenMsg);  

                    notifyChange(false);
                }
            }

        };
        
        registerObserver();
    }
    
    public void destroy() {
        Log.i(TAG, "destroy");
        unregisterObserver();

        synchronized (mLock) {
        	mBootScreeInfo = null;
        }
    }

    public BootScreenInfo getBootScreen() {
        synchronized (mLock) {
        	mBootScreeInfo = queryUpdateDataInfo();
        }
        
        return mBootScreeInfo;
    }
    
    private BootScreenInfo queryUpdateDataInfo() {
    	BootScreenInfo bootScreenInfo = null;
    	Cursor c = null;
 
        try {
            c = mContentresolver.query(Uri.parse(BOOT_SCREEN_CONTENT_URI), null, "CONTENT_ID=?",
                    new String[] {
                        "bootScreen"
                    }, null);
            if (c != null && c.moveToNext()) {
            	bootScreenInfo = new BootScreenInfo(c.getString(3));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        
    	return bootScreenInfo;
    }
    
    private void loadBootScreenInfo() {
        mHandler.removeMessages(MSG_RELOAD_BOOT_SCREEN_ITEMS);
        mHandler.sendEmptyMessageAtTime(MSG_RELOAD_BOOT_SCREEN_ITEMS, RELOAD_ITEMS_DELAY_MS);
    }
    
    private void registerObserver() {
        if (mBootScreenContentObserver == null) {
        	mBootScreenContentObserver = new ContentObserver(new Handler()) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);                    
                    loadBootScreenInfo();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(BOOT_SCREEN_CONTENT_URI), true,
            		mBootScreenContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mBootScreenContentObserver != null) {
            mContentresolver.unregisterContentObserver(mBootScreenContentObserver);
            mBootScreenContentObserver = null;
        }
        
        mhandlerThread.quit();
    }
}

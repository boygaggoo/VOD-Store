package com.cookingshow.datacenter;

import java.util.ArrayList;

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

public class NavigationDataProvider extends ContentObservable {
    public static final String TAG = "NavigationDataProvider";
    public static final String NAVIGATION_CONTENT_URI = "content://com.cookingshow.service.provider/navigation_data";
    private static final int MSG_RELOAD_NAVIGATION_ITEMS = 101;
    private static final long RELOAD_ITEMS_DELAY_MS = 1000;
    
    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final ArrayList<MenuItem> mMenuItemInfos;
    private final HandlerThread mhandlerThread;
    private final Handler mHandler;
    private final Object mLock;
    private ContentObserver mNaviContentObserver;
    
	public NavigationDataProvider(Context context) {
		// TODO Auto-generated constructor stub
        mContext = context;
        mContentresolver = context.getContentResolver();
        mMenuItemInfos = new ArrayList<MenuItem>();
        mLock = new Object();
        
        getAllMenuItemData();
        
        mhandlerThread = new HandlerThread("navigation_data_provider");
        mhandlerThread.start();
        mHandler = new Handler(mhandlerThread.getLooper()) {

            public void hanleMessage(Message msg) {
                if (msg.what == MSG_RELOAD_NAVIGATION_ITEMS) {
                    Log.i(TAG, "handleMessage");
                    getAllMenuItemData();

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
        	mMenuItemInfos.clear();
        }
    }

    private void getAllMenuItemData() {
        synchronized (mLock) {
            Log.i(TAG, "getAllMenuItemData");
            mMenuItemInfos.clear();
            mMenuItemInfos.addAll(queryMenuItemInfoList());
        }
    }

    public ArrayList<MenuItem> getMainMenuItem() {
    	ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
    	synchronized (mLock) {
    		if(mMenuItemInfos.size() == 0) {
            	MenuItem menuItem = new MenuItem();
            	menuItem.setId(80000);
            	menuItem.setName("Recommend");
            	menuItem.setEnName("Daily");
            	menuItem.setCode("daily");
            	menuItem.setOrderNum(1);
            	menuItems.add(menuItem);

            	MenuItem menuItem2 = new MenuItem();
            	menuItem2.setId(80001);
            	menuItem2.setName("Top");
            	menuItem2.setEnName("Top");
            	menuItem2.setCode("top");
            	menuItem2.setOrderNum(2);
            	menuItems.add(menuItem2);

            	MenuItem menuItem3 = new MenuItem();
            	menuItem3.setId(80002);
            	menuItem3.setName("Share");
            	menuItem3.setEnName("Share");
            	menuItem3.setCode("share");
            	menuItem3.setOrderNum(3);
            	menuItems.add(menuItem3);
            	
            	MenuItem menuItem4 = new MenuItem();
            	menuItem4.setId(80003);
            	menuItem4.setName("All");
            	menuItem4.setEnName("Cookbook");
            	menuItem4.setCode("cookbook");
            	menuItem4.setOrderNum(4);
            	menuItems.add(menuItem4);
            	
            	MenuItem menuItem5 = new MenuItem();
            	menuItem5.setId(80004);
            	menuItem5.setName("Mine");
            	menuItem5.setEnName("Mine");
            	menuItem5.setCode("mine");
            	menuItem5.setOrderNum(5);
            	menuItems.add(menuItem5);    			
    		}
    		else {
        		for(int i = 0; i < mMenuItemInfos.size(); i++) {
        			if(mMenuItemInfos.get(i).getType().equals("main")) {
        				menuItems.add(mMenuItemInfos.get(i));
        			}
        		}    			
    		}
    	}

    	return menuItems;
    }
 
    public ArrayList<MenuItem> getSubMenuItem() {
    	ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
    	synchronized (mLock) {
    		if(mMenuItemInfos.size() == 0) {
    			MenuItem data = new MenuItem();
            	data.setName("Home");
            	data.setCode("home");
            	data.setId(80005);
            	data.setOrderNum(1);
            	menuItems.add(data);
            	
            	MenuItem data2 = new MenuItem();
            	data2.setName("Baby");
            	data2.setCode("baby");
            	data2.setId(80006);
            	data2.setOrderNum(2);
            	menuItems.add(data2);
            	
            	MenuItem data3 = new MenuItem();
            	data3.setName("Healthy");
            	data3.setCode("healthy");
            	data3.setId(80007);
            	data3.setOrderNum(3);
            	menuItems.add(data3);    			
    		}
    		else {
        		for(int i = 0; i < mMenuItemInfos.size(); i++) {
        			if(mMenuItemInfos.get(i).getType().equals("sub")) {
        				menuItems.add(mMenuItemInfos.get(i));
        			}
        		}    			
    		}

    	}

    	return menuItems;
    }

    private ArrayList<MenuItem> queryMenuItemInfoList() {
        Log.i(TAG, "queryMenuItemInfoList");
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
        Cursor c = null;

        try {
            c = mContentresolver.query(Uri.parse(NAVIGATION_CONTENT_URI), null, "CONTENT_ID=?",
                    new String[] {
                        "navigationData"
                    }, null);
            while (c != null && c.moveToNext()) {
            	MenuItem menuItem = new MenuItem();
            	menuItem.setId(c.getInt(2));
            	menuItem.setType(c.getString(3));
            	menuItem.setName(c.getString(4));
            	menuItem.setEnName(c.getString(5));
            	menuItem.setCode(c.getString(6));
            	menuItem.setOrderNum(c.getInt(7));
            	menuItems.add(menuItem);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return menuItems;
    }
    
    private void queueReloadNaviDataInfoItems() {
        mHandler.removeMessages(MSG_RELOAD_NAVIGATION_ITEMS);
        mHandler.sendEmptyMessageAtTime(MSG_RELOAD_NAVIGATION_ITEMS, RELOAD_ITEMS_DELAY_MS);
    }   
      
    private void registerObserver() {
        if (mNaviContentObserver == null) {
        	mNaviContentObserver = new ContentObserver(mHandler) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    queueReloadNaviDataInfoItems();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(NAVIGATION_CONTENT_URI), true,
            		mNaviContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mNaviContentObserver != null) {
            mContentresolver.unregisterContentObserver(mNaviContentObserver);
            mNaviContentObserver = null;
        }

        mhandlerThread.quit();
    }
}

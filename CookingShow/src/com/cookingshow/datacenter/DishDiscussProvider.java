package com.cookingshow.datacenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class DishDiscussProvider extends ContentObservable {
    public static final String TAG = "DishDiscussProvider";
    public static final String DISH_DISCUSS_DATA_CONTENT_URI = "content://com.cookingshow.service.provider/dish_discuss_data";
    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final Handler mUiHandler;
    private ContentObserver mDishDIscussDataContentObserver;
    
	public DishDiscussProvider(Context context, Handler uiHandler) {
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
    
    public List<ListItem> getDishDiscussData(int dishId) {
    	List<ListItem> ads = new ArrayList<ListItem>();
            
        ads.addAll(queryDisDiscussDataInfoList(dishId));

        return ads;
    }

    private ArrayList<ListItem> queryDisDiscussDataInfoList(int id) {
        Log.i(TAG, "queryDisDiscussDataInfoList");
        ArrayList<ListItem> datas = new ArrayList<ListItem>();
        Cursor c = null;
        
    	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date(System.currentTimeMillis());
        String current = df.format(d);

        try {
            c = mContentresolver.query(Uri.parse(DISH_DISCUSS_DATA_CONTENT_URI), null, "CONTENT_ID=? and DISH_ID=?",
                    new String[] {
                        "dishDiscussData",
                        String.valueOf(id)
                    }, null);

            while (c != null && c.moveToNext()) {
            	ListItem data = new ListItem();
            	
            	data.setItemId(c.getInt(1));
            	
            	String tmp = c.getString(3);

            	if(tmp.substring(0, 10).equals(current)) {
            		data.setItemTitle2(tmp.substring(11));
            	}
            	else {
            		data.setItemTitle2(tmp.substring(0, 10));
            	}
            	
            	data.setItemTitle1(c.getString(4));
            	
            	if(c.getString(5).equals("")) {
            		data.setItemText(c.getString(6));
            	}
            	else {
            		data.setItemText("@" + c.getString(5) + " " + c.getString(6));
            	}
            	
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
        msg.what = 2;
        mUiHandler.sendMessage(msg);                    		
    }

    private void loadDishDiscussDataInfo() {
    	notifyRefreshData();	
    }
    
    private void registerObserver() {
        if (mDishDIscussDataContentObserver == null) {
        	mDishDIscussDataContentObserver = new ContentObserver(new Handler()) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    loadDishDiscussDataInfo();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(DISH_DISCUSS_DATA_CONTENT_URI), true,
            		mDishDIscussDataContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mDishDIscussDataContentObserver != null) {
            mContentresolver.unregisterContentObserver(mDishDIscussDataContentObserver);
            mDishDIscussDataContentObserver = null;
        }
    }

}

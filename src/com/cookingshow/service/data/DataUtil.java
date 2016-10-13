package com.cookingshow.service.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.cookingshow.service.BaseClient;
import com.cookingshow.service.provider.ProviderSettings.BootScreenColumns;
import com.cookingshow.service.provider.ProviderSettings.CookStepDataColumns;
import com.cookingshow.service.provider.ProviderSettings.DataUpdateColumns;
import com.cookingshow.service.provider.ProviderSettings.DishDataColumns;
import com.cookingshow.service.provider.ProviderSettings.DishDiscussDataColumns;
import com.cookingshow.service.provider.ProviderSettings.NavigationDataColumns;
import com.cookingshow.service.provider.ProviderSettings.RecommendDataColumns;
import com.cookingshow.service.provider.ProviderSettings.ShareImgDataColumns;
import com.cookingshow.service.provider.ProviderSettings.ShareVideoAlbumDataColumns;
import com.cookingshow.service.provider.ProviderSettings.ShareVideoDataColumns;
import com.cookingshow.service.provider.ProviderSettings.TopDataColumns;
import com.cookingshow.service.provider.ProviderSettings.ViewRecordDataColumns;


public class DataUtil {
    private static final String TAG = "DataUtil";

    public static void setUpdateDataList(Context context, List<UpdateDataInfo> list, BaseClient client) {
    	removeUpdateDataList(context, client);

        if (list != null && list.size() > 0) {
            addUpdateDataList(context, list, client);
        }
    }

    public static List<UpdateDataInfo> getUpdateDataList(Context context, BaseClient client) {
        Log.i(TAG, "getUpdateDataList");
        List<UpdateDataInfo> retList = new ArrayList<UpdateDataInfo>();
        Cursor c = null;

        try {
            String selection = DataUpdateColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		DataUpdateColumns.DATA_UPDATE_CONTENT_URI,
            		DataUpdateColumns.DATA_Update_PROJECTION, selection, selectionArgs,
                    null);
            while (c != null && c.moveToNext()) {
            	UpdateDataInfo data = new UpdateDataInfo();
                data.setName(c.getString(c.getColumnIndex(DataUpdateColumns.NAME)));
                retList.add(data);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return retList;
    }

    public static boolean isExistUpdateData(Context context, BaseClient client) {
        boolean result = false;
        Cursor c = null;

        try {
            String selection = DataUpdateColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		DataUpdateColumns.DATA_UPDATE_CONTENT_URI, new String[] {
                        "count(content_id) AS contentid_num"
                    }, selection, selectionArgs, null);
            while (c != null && c.moveToNext()) {
                result = c.getInt(0) > 0 ? true : false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static void addUpdateDataList(Context context, List<UpdateDataInfo> addList,
            BaseClient client) {
        Log.i(TAG, "addUpdateDataList");
        int arraySize = addList.size();
        ContentValues[] valueArray = new ContentValues[arraySize];

        for (int i = 0; i < arraySize; i++) {
            ContentValues values = new ContentValues();
            values.put(DataUpdateColumns.CONTENT_ID, client.getContentId());
            values.put(DataUpdateColumns.VERSION, addList.get(i).getVersion());
            values.put(DataUpdateColumns.NAME, addList.get(i).getName());
            values.put(DataUpdateColumns.URL, addList.get(i).getUrl());

            valueArray[i] = values;
        }

        context.getContentResolver().bulkInsert(DataUpdateColumns.DATA_UPDATE_CONTENT_URI,
                valueArray);
    }

    public static void removeUpdateDataList(Context context, BaseClient client) {
        String selection = DataUpdateColumns.CONTENT_ID + "=?";
        String[] selectionArgs = {
            client.getContentId()
        };
        context.getContentResolver().delete(DataUpdateColumns.DATA_UPDATE_CONTENT_URI,
                selection, selectionArgs);
    }
    
    /*-------------------------------------BootScreen---------------------------------------------------*/
    public static void setBootScreen(Context context, BootScreenInfo list, BaseClient client) {
    	removeBootScreen(context, client);

        if (list != null) {
        	addBootScreen(context, list, client);
        }
    }

    public static BootScreenInfo getBootScreen(Context context, BaseClient client) {
        Log.i(TAG, "getBootScreen");
        BootScreenInfo ret = null;
        Cursor c = null;

        try {
            String selection = BootScreenColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		BootScreenColumns.BOOT_SCREEN_CONTENT_URI,
            		BootScreenColumns.BOOT_SCREEN_PROJECTION, selection, selectionArgs,
                    null);
            if (c != null && c.moveToNext()) {
            	BootScreenInfo data = new BootScreenInfo();
                data.setTitle(c.getString(c.getColumnIndex(BootScreenColumns.TITLE)));
                ret = data;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return ret;
    }

    public static boolean isExistBootScreen(Context context, BaseClient client) {
        boolean result = false;
        Cursor c = null;

        try {
            String selection = BootScreenColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		BootScreenColumns.BOOT_SCREEN_CONTENT_URI, new String[] {
                        "count(content_id) AS contentid_num"
                    }, selection, selectionArgs, null);
            if (c != null && c.moveToNext()) {
                result = c.getInt(0) > 0 ? true : false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static void addBootScreen(Context context, BootScreenInfo data,
            BaseClient client) {
        Log.i(TAG, "addBootScreen");
        ContentValues[] valueArray = new ContentValues[1];

        ContentValues values = new ContentValues();
        values.put(BootScreenColumns.CONTENT_ID, client.getContentId());
        values.put(BootScreenColumns.TITLE, data.getTitle());
        values.put(BootScreenColumns.THUMB_PATH, data.getThumbPath());
        values.put(BootScreenColumns.THUMB_URL, data.getThumbUrl());

        valueArray[0] = values;

        context.getContentResolver().bulkInsert(BootScreenColumns.BOOT_SCREEN_CONTENT_URI,
                valueArray);
    }

    public static void removeBootScreen(Context context, BaseClient client) {
        String selection = BootScreenColumns.CONTENT_ID + "=?";
        String[] selectionArgs = {
            client.getContentId()
        };
        context.getContentResolver().delete(BootScreenColumns.BOOT_SCREEN_CONTENT_URI,
                selection, selectionArgs);
    }

    /*-------------------------------------DishData---------------------------------------------------*/
    public static void setDishDataList(Context context, List<DishDataInfo> list, BaseClient client) {
    	//removeDishDataList(context, client);

        if (list != null && list.size() > 0) {
        	addDishDataList(context, list, client);
        }
    }

    public static void updateDishDataList(Context context, List<DishDataInfo> list, BaseClient client) {
        String selection = DishDataColumns.DISH_ID + "=?";
        
        if (list != null && list.size() > 0) {
            int arraySize = list.size();
            
            for (int i = 0; i < arraySize; i++) {
                ContentValues values = new ContentValues();
                values.put(DishDataColumns.TITLE, list.get(i).getTitle());
                values.put(DishDataColumns.TIPS, list.get(i).getTips());
                values.put(DishDataColumns.MATERIALS, list.get(i).getMaterials());
                values.put(DishDataColumns.THUMB_URL, list.get(i).getThumbUrl());
                
                String[] selectionArgs = {
                		String.valueOf(list.get(i).getDishId())
                    };
                
                context.getContentResolver().update(DishDataColumns.DISH_DATA_CONTENT_URI, values, selection, selectionArgs);
            }
           
        }
    }
 
    public static List<DishDataInfo> getDishDataList(Context context, BaseClient client) {
        Log.i(TAG, "getDishDataList");
        List<DishDataInfo> retList = new ArrayList<DishDataInfo>();
        Cursor c = null;

        try {
            String selection = DishDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		DishDataColumns.DISH_DATA_CONTENT_URI,
            		DishDataColumns.DISH_DATA_PROJECTION, selection, selectionArgs,
                    null);
            while (c != null && c.moveToNext()) {
            	DishDataInfo data = new DishDataInfo();
            	data.setDishId(c.getInt(c.getColumnIndex(DishDataColumns.DISH_ID)));
                data.setTitle(c.getString(c.getColumnIndex(DishDataColumns.TITLE)));
                data.setThumbUrl(c.getString(c.getColumnIndex(DishDataColumns.THUMB_URL)));
                data.setTips(c.getString(c.getColumnIndex(DishDataColumns.TIPS)));
                data.setMaterials(c.getString(c.getColumnIndex(DishDataColumns.MATERIALS)));
                retList.add(data);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return retList;
    }

    public static boolean isExistDishData(Context context, BaseClient client) {
        boolean result = false;
        Cursor c = null;

        try {
            String selection = DishDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		DishDataColumns.DISH_DATA_CONTENT_URI, new String[] {
                        "count(content_id) AS contentid_num"
                    }, selection, selectionArgs, null);
            while (c != null && c.moveToNext()) {
                result = c.getInt(0) > 0 ? true : false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static void addDishDataList(Context context, List<DishDataInfo> addList,
            BaseClient client) {
        Log.i(TAG, "addDishDataList");
        int arraySize = addList.size();
        ContentValues[] valueArray = new ContentValues[arraySize];

        for (int i = 0; i < arraySize; i++) {
            ContentValues values = new ContentValues();
            values.put(DishDataColumns.CONTENT_ID, client.getContentId());
            values.put(DishDataColumns.DISH_ID, addList.get(i).getDishId());
            values.put(DishDataColumns.UPLOAD_TIME, addList.get(i).getUploadTime());
            values.put(DishDataColumns.UPLOADER, addList.get(i).getUploader());
            values.put(DishDataColumns.TITLE, addList.get(i).getTitle());
            values.put(DishDataColumns.TYPE, addList.get(i).getType());
            values.put(DishDataColumns.THUMB_URL, addList.get(i).getThumbUrl());
            values.put(DishDataColumns.VIDEO_URL, addList.get(i).getVideoUrl());
            values.put(DishDataColumns.TIPS, addList.get(i).getTips());
            values.put(DishDataColumns.MATERIALS, addList.get(i).getMaterials());
            
            valueArray[i] = values;
        }

        context.getContentResolver().bulkInsert(DishDataColumns.DISH_DATA_CONTENT_URI,
                valueArray);
    }

    public static void removeDishDataList(Context context, BaseClient client) {
        String selection = DishDataColumns.CONTENT_ID + "=?";
        String[] selectionArgs = {
            client.getContentId()
        };
        context.getContentResolver().delete(DishDataColumns.DISH_DATA_CONTENT_URI,
                selection, selectionArgs);
    }

    /*-------------------------------------TopData---------------------------------------------------*/
    public static void setTopDataList(Context context, List<TopDataInfo> list, BaseClient client) {
    	removeTopDataList(context, client);

        if (list != null && list.size() > 0) {
        	addTopDataList(context, list, client);
        }
    }
 
    public static List<TopDataInfo> getTopDataList(Context context, BaseClient client) {
        Log.i(TAG, "getTopDataList");
        List<TopDataInfo> retList = new ArrayList<TopDataInfo>();
        Cursor c = null;

        try {
            String selection = TopDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		TopDataColumns.TOP_DATA_CONTENT_URI,
            		TopDataColumns.TOP_DATA_PROJECTION, selection, selectionArgs,
                    null);
            while (c != null && c.moveToNext()) {
            	TopDataInfo data = new TopDataInfo();
            	data.setDishId(c.getInt(c.getColumnIndex(TopDataColumns.DISH_ID)));
                retList.add(data);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return retList;
    }

    public static boolean isExistTopData(Context context, BaseClient client) {
        boolean result = false;
        Cursor c = null;

        try {
            String selection = TopDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		TopDataColumns.TOP_DATA_CONTENT_URI, new String[] {
                        "count(content_id) AS contentid_num"
                    }, selection, selectionArgs, null);
            while (c != null && c.moveToNext()) {
                result = c.getInt(0) > 0 ? true : false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static void addTopDataList(Context context, List<TopDataInfo> addList,
            BaseClient client) {
        Log.i(TAG, "addTopDataList");
        int arraySize = addList.size();
        ContentValues[] valueArray = new ContentValues[arraySize];

        for (int i = 0; i < arraySize; i++) {
            ContentValues values = new ContentValues();
            values.put(TopDataColumns.CONTENT_ID, client.getContentId());
            values.put(TopDataColumns.DISH_ID, addList.get(i).getDishId());
            values.put(TopDataColumns.UPLOADER, addList.get(i).getUploader());
            values.put(TopDataColumns.TITLE, addList.get(i).getTitle());
            values.put(TopDataColumns.THUMB_URL, addList.get(i).getThumbUrl());
            values.put(TopDataColumns.VIDEO_URL, addList.get(i).getVideoUrl());
            values.put(TopDataColumns.TIPS, addList.get(i).getTips());
            values.put(TopDataColumns.MATERIALS, addList.get(i).getMaterials());
            
            valueArray[i] = values;
        }

        context.getContentResolver().bulkInsert(TopDataColumns.TOP_DATA_CONTENT_URI,
                valueArray);
    }

    public static void removeTopDataList(Context context, BaseClient client) {
        String selection = TopDataColumns.CONTENT_ID + "=?";
        String[] selectionArgs = {
            client.getContentId()
        };
        context.getContentResolver().delete(TopDataColumns.TOP_DATA_CONTENT_URI,
                selection, selectionArgs);
    }

    /*-------------------------------------RecommendData---------------------------------------------------*/
    public static void setRecommendDataList(Context context, List<RecommendDataInfo> list, BaseClient client) {
    	removeRecommendDataList(context, client);

        if (list != null && list.size() > 0) {
        	addRecommendDataList(context, list, client);
        }
    }
 
    public static List<RecommendDataInfo> getRecommendDataList(Context context, BaseClient client) {
        Log.i(TAG, "getRecommendDataList");
        List<RecommendDataInfo> retList = new ArrayList<RecommendDataInfo>();
        Cursor c = null;

        try {
            String selection = RecommendDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		RecommendDataColumns.RECOMMEND_DATA_CONTENT_URI,
            		RecommendDataColumns.RECOMMEND_DATA_PROJECTION, selection, selectionArgs,
                    null);
            while (c != null && c.moveToNext()) {
            	RecommendDataInfo data = new RecommendDataInfo();
            	data.setDishId(c.getInt(c.getColumnIndex(RecommendDataColumns.DISH_ID)));
                retList.add(data);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return retList;
    }

    public static boolean isExistRecommendData(Context context, BaseClient client) {
        boolean result = false;
        Cursor c = null;

        try {
            String selection = RecommendDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		RecommendDataColumns.RECOMMEND_DATA_CONTENT_URI, new String[] {
                        "count(content_id) AS contentid_num"
                    }, selection, selectionArgs, null);
            while (c != null && c.moveToNext()) {
                result = c.getInt(0) > 0 ? true : false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static void addRecommendDataList(Context context, List<RecommendDataInfo> addList,
            BaseClient client) {
        Log.i(TAG, "addRecommendDataList");
        int arraySize = addList.size();
        ContentValues[] valueArray = new ContentValues[arraySize];

        for (int i = 0; i < arraySize; i++) {
            ContentValues values = new ContentValues();
            values.put(RecommendDataColumns.CONTENT_ID, client.getContentId());
            values.put(RecommendDataColumns.DISH_ID, addList.get(i).getDishId());
            values.put(RecommendDataColumns.UPLOADER, addList.get(i).getUploader());
            values.put(RecommendDataColumns.TITLE, addList.get(i).getTitle());
            values.put(RecommendDataColumns.THUMB_URL, addList.get(i).getThumbUrl());
            values.put(RecommendDataColumns.VIDEO_URL, addList.get(i).getVideoUrl());
            values.put(RecommendDataColumns.TIPS, addList.get(i).getTips());
            values.put(RecommendDataColumns.MATERIALS, addList.get(i).getMaterials());
            
            valueArray[i] = values;
        }

        context.getContentResolver().bulkInsert(RecommendDataColumns.RECOMMEND_DATA_CONTENT_URI,
                valueArray);
    }

    public static void removeRecommendDataList(Context context, BaseClient client) {
        String selection = RecommendDataColumns.CONTENT_ID + "=?";
        String[] selectionArgs = {
            client.getContentId()
        };
        context.getContentResolver().delete(RecommendDataColumns.RECOMMEND_DATA_CONTENT_URI,
                selection, selectionArgs);
    }

    /*-------------------------------------NavigationData---------------------------------------------------*/
    public static void setNavigationDataList(Context context, List<NavigationDataInfo> list, BaseClient client) {
    	removeNavigationDataList(context, client);

        if (list != null && list.size() > 0) {
            addNavigationDataList(context, list, client);
        }
    }

    public static List<NavigationDataInfo> getNavigationDataList(Context context, BaseClient client) {
        Log.i(TAG, "getNavigationDataList");
        List<NavigationDataInfo> retList = new ArrayList<NavigationDataInfo>();
        Cursor c = null;

        try {
            String selection = NavigationDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		NavigationDataColumns.NAVIGATION_DATA_CONTENT_URI,
            		NavigationDataColumns.NAVIGATION_DATA_PROJECTION, selection, selectionArgs,
                    null);
            while (c != null && c.moveToNext()) {
            	NavigationDataInfo data = new NavigationDataInfo();
                data.setCode(c.getString(c.getColumnIndex(NavigationDataColumns.CODE)));
                retList.add(data);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return retList;
    }

    public static boolean isExistNavigationData(Context context, BaseClient client) {
        boolean result = false;
        Cursor c = null;

        try {
            String selection = NavigationDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		NavigationDataColumns.NAVIGATION_DATA_CONTENT_URI, new String[] {
                        "count(content_id) AS contentid_num"
                    }, selection, selectionArgs, null);
            while (c != null && c.moveToNext()) {
                result = c.getInt(0) > 0 ? true : false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static void addNavigationDataList(Context context, List<NavigationDataInfo> addList,
            BaseClient client) {
        Log.i(TAG, "addNavigationDataList");
        int arraySize = addList.size();
        ContentValues[] valueArray = new ContentValues[arraySize];

        for (int i = 0; i < arraySize; i++) {
            ContentValues values = new ContentValues();
            values.put(NavigationDataColumns.CONTENT_ID, client.getContentId());
            values.put(NavigationDataColumns.MENU_ID, addList.get(i).getMenuId());
            values.put(NavigationDataColumns.TYPE, addList.get(i).getType());
            values.put(NavigationDataColumns.TITLE, addList.get(i).getTitle());
            values.put(NavigationDataColumns.EN_TITLE, addList.get(i).getEnTitle());
            values.put(NavigationDataColumns.CODE, addList.get(i).getCode());
            values.put(NavigationDataColumns.ORDER_NUM, addList.get(i).getOrderNum());
            
            valueArray[i] = values;
        }

        context.getContentResolver().bulkInsert(NavigationDataColumns.NAVIGATION_DATA_CONTENT_URI,
                valueArray);
    }

    public static void removeNavigationDataList(Context context, BaseClient client) {
        String selection = NavigationDataColumns.CONTENT_ID + "=?";
        String[] selectionArgs = {
            client.getContentId()
        };
        context.getContentResolver().delete(NavigationDataColumns.NAVIGATION_DATA_CONTENT_URI,
                selection, selectionArgs);
    }

    /*-------------------------------------CookStepData---------------------------------------------------*/
    public static void setCookStepDataList(Context context, List<CookStepInfo> list, BaseClient client) {

        if (list != null && list.size() > 0) {
        	addCookStepDataList(context, list, client);
        }
    }
    
    public static void addCookStepDataList(Context context, List<CookStepInfo> addList,
            BaseClient client) {
        Log.i(TAG, "addCookStepDataList");
        int arraySize = addList.size();
        ContentValues[] valueArray = new ContentValues[arraySize];

        for (int i = 0; i < arraySize; i++) {
            ContentValues values = new ContentValues();
            values.put(CookStepDataColumns.CONTENT_ID, client.getContentId());
            values.put(CookStepDataColumns.DISH_ID, addList.get(i).getDishId());
            values.put(CookStepDataColumns.COOK_SN, addList.get(i).getCookSn());
            values.put(CookStepDataColumns.COOK_TEXT, addList.get(i).getCookText());
            
            valueArray[i] = values;
        }

        context.getContentResolver().bulkInsert(CookStepDataColumns.COOK_STEP_DATA_CONTENT_URI,
                valueArray);
    }
    
    /*-------------------------------------DishDiscussData---------------------------------------------------*/
    public static void setDishDiscussDataList(Context context, List<DishDiscussInfo> list, BaseClient client) {
    	removeDishDiscussDataList(context, client);

        if (list != null && list.size() > 0) {
            addDishDiscussDataList(context, list, client);
        }
    }

    public static List<DishDiscussInfo> getDishDiscussDataList(Context context, BaseClient client) {
        Log.i(TAG, "getDishDiscussDataList");
        List<DishDiscussInfo> retList = new ArrayList<DishDiscussInfo>();
        Cursor c = null;

        try {
            String selection = DishDiscussDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		DishDiscussDataColumns.DISH_DISCUSS_DATA_CONTENT_URI,
            		DishDiscussDataColumns.DISH_DISCUSS_DATA_PROJECTION, selection, selectionArgs,
                    null);
            while (c != null && c.moveToNext()) {
            	DishDiscussInfo data = new DishDiscussInfo();
                data.setSender(c.getString(c.getColumnIndex(DishDiscussDataColumns.SENDER)));
                data.setReceiver(c.getString(c.getColumnIndex(DishDiscussDataColumns.RECEIVER)));
                data.setContent(c.getString(c.getColumnIndex(DishDiscussDataColumns.CONTENT)));
                data.setStatus(c.getString(c.getColumnIndex(DishDiscussDataColumns.STATUS)));
                retList.add(data);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return retList;
    }

    public static boolean isExistDishDiscussData(Context context, BaseClient client) {
        boolean result = false;
        Cursor c = null;

        try {
            String selection = DishDiscussDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		DishDiscussDataColumns.DISH_DISCUSS_DATA_CONTENT_URI, new String[] {
                        "count(content_id) AS contentid_num"
                    }, selection, selectionArgs, null);
            while (c != null && c.moveToNext()) {
                result = c.getInt(0) > 0 ? true : false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static void addDishDiscussDataList(Context context, List<DishDiscussInfo> addList,
            BaseClient client) {
        Log.i(TAG, "addDishDiscussDataList");
        int arraySize = addList.size();
        ContentValues[] valueArray = new ContentValues[arraySize];

        for (int i = 0; i < arraySize; i++) {
            ContentValues values = new ContentValues();
            values.put(DishDiscussDataColumns.CONTENT_ID, client.getContentId());
            values.put(DishDiscussDataColumns.DISCUSS_ID, addList.get(i).getDiscussId());
            values.put(DishDiscussDataColumns.DISH_ID, addList.get(i).getDishId());
            values.put(DishDiscussDataColumns.SEND_TIME, addList.get(i).getSendTime());
            values.put(DishDiscussDataColumns.SENDER, addList.get(i).getSender());
            values.put(DishDiscussDataColumns.RECEIVER, addList.get(i).getReceiver());
            values.put(DishDiscussDataColumns.CONTENT, addList.get(i).getContent());
            values.put(DishDiscussDataColumns.STATUS, addList.get(i).getStatus());
            
            valueArray[i] = values;
        }

        context.getContentResolver().bulkInsert(DishDiscussDataColumns.DISH_DISCUSS_DATA_CONTENT_URI,
                valueArray);
    }

    public static void removeDishDiscussDataList(Context context, BaseClient client) {
        String selection = DishDiscussDataColumns.CONTENT_ID + "=?";
        String[] selectionArgs = {
            client.getContentId()
        };
        context.getContentResolver().delete(DishDiscussDataColumns.DISH_DISCUSS_DATA_CONTENT_URI,
                selection, selectionArgs);
    }
    
    /*-------------------------------------ShareImgData---------------------------------------------------*/
    public static void setShareImgDataList(Context context, List<ShareImgInfo> list, BaseClient client) {
    	removeShareImgDataList(context, client);

        if (list != null && list.size() > 0) {
            addShareImgDataList(context, list, client);
        }
    }

    public static boolean isExistShareImgData(Context context, BaseClient client) {
        boolean result = false;
        Cursor c = null;

        try {
            String selection = ShareImgDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		ShareImgDataColumns.SHARE_IMG_DATA_CONTENT_URI, new String[] {
                        "count(content_id) AS contentid_num"
                    }, selection, selectionArgs, null);
            while (c != null && c.moveToNext()) {
                result = c.getInt(0) > 0 ? true : false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static void addShareImgDataList(Context context, List<ShareImgInfo> addList,
            BaseClient client) {
        Log.i(TAG, "addShareImgDataList");
        int arraySize = addList.size();
        ContentValues[] valueArray = new ContentValues[arraySize];

        for (int i = 0; i < arraySize; i++) {
            ContentValues values = new ContentValues();
            values.put(ShareImgDataColumns.CONTENT_ID, client.getContentId());
            values.put(ShareImgDataColumns.IMG_ID, addList.get(i).getImgId());
            values.put(ShareImgDataColumns.DISH_ID, addList.get(i).getDishId());
            values.put(ShareImgDataColumns.UPLOAD_TIME, addList.get(i).getUploaderTime());
            values.put(ShareImgDataColumns.UPLOADER, addList.get(i).getUploader());
            values.put(ShareImgDataColumns.IMG_URL, addList.get(i).getImgUrl());
            values.put(ShareImgDataColumns.FEELING, addList.get(i).getFeeling());
            values.put(ShareImgDataColumns.LIKE_TIMES, addList.get(i).getLikeTimes());
            
            valueArray[i] = values;
        }

        context.getContentResolver().bulkInsert(ShareImgDataColumns.SHARE_IMG_DATA_CONTENT_URI,
                valueArray);
    }

    public static void removeShareImgDataList(Context context, BaseClient client) {
        String selection = ShareImgDataColumns.CONTENT_ID + "=?";
        String[] selectionArgs = {
            client.getContentId()
        };
        context.getContentResolver().delete(ShareImgDataColumns.SHARE_IMG_DATA_CONTENT_URI,
                selection, selectionArgs);
    }
    
    /*-------------------------------------ViewRecordData---------------------------------------------------*/
    public static void setViewRecordDataList(Context context, List<ViewRecordDataInfo> list, BaseClient client) {
    	removeViewRecordDataList(context, client);

        if (list != null && list.size() > 0) {
            addViewRecordDataList(context, list, client);
        }
    }

    public static boolean isExistViewRecordData(Context context, BaseClient client) {
        boolean result = false;
        Cursor c = null;

        try {
            String selection = ViewRecordDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		ViewRecordDataColumns.VIEW_RECORD_DATA_CONTENT_URI, new String[] {
                        "count(content_id) AS contentid_num"
                    }, selection, selectionArgs, null);
            while (c != null && c.moveToNext()) {
                result = c.getInt(0) > 0 ? true : false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static void addViewRecordDataList(Context context, List<ViewRecordDataInfo> addList,
            BaseClient client) {
        Log.i(TAG, "addViewRecordDataList");
        int arraySize = addList.size();
        ContentValues[] valueArray = new ContentValues[arraySize];

        for (int i = 0; i < arraySize; i++) {
            ContentValues values = new ContentValues();
            values.put(ViewRecordDataColumns.CONTENT_ID, client.getContentId());
            values.put(ViewRecordDataColumns.ALBUM_ID, addList.get(i).getAlbumId());
            values.put(ViewRecordDataColumns.DISH_ID, addList.get(i).getDishId());
            values.put(ViewRecordDataColumns.UPLOADER, addList.get(i).getUploader());
            values.put(ViewRecordDataColumns.TITLE, addList.get(i).getTitle());
            values.put(ViewRecordDataColumns.THUMB_URL, addList.get(i).getThumbUrl());
            values.put(ViewRecordDataColumns.VIDEO_URL, addList.get(i).getVideoUrl());
            values.put(ViewRecordDataColumns.TIPS, addList.get(i).getTips());
            values.put(ViewRecordDataColumns.MATERIALS, addList.get(i).getMaterials());
            values.put(ViewRecordDataColumns.VIEW_TIMES, addList.get(i).getViewTimes());
            values.put(ViewRecordDataColumns.LIKE_TIMES, addList.get(i).getLikeTimes());
            
            valueArray[i] = values;
        }

        context.getContentResolver().bulkInsert(ViewRecordDataColumns.VIEW_RECORD_DATA_CONTENT_URI,
                valueArray);
    }

    public static void removeViewRecordDataList(Context context, BaseClient client) {
        String selection = ViewRecordDataColumns.CONTENT_ID + "=?";
        String[] selectionArgs = {
            client.getContentId()
        };
        context.getContentResolver().delete(ViewRecordDataColumns.VIEW_RECORD_DATA_CONTENT_URI,
                selection, selectionArgs);
    }
    
    /*-------------------------------------ShareVideoAlbumData---------------------------------------------------*/
    public static void setVideoAlbumDataList(Context context, List<ShareVideoAlbumInfo> list, BaseClient client) {
    	//removeVideoAlbumDataList(context, client);
    	
        if (list != null && list.size() > 0) {
        	addVideoAlbumDataList(context, list, client);
        }
    }

    public static void updateVideoAlbumDataList(Context context, List<ShareVideoAlbumInfo> list, BaseClient client) {
        String selection = ShareVideoAlbumDataColumns.ALBUM_ID + "=?";
        
        if (list != null && list.size() > 0) {
            int arraySize = list.size();
            
            for (int i = 0; i < arraySize; i++) {
                ContentValues values = new ContentValues();
                values.put(ShareVideoAlbumDataColumns.UPLOADER, list.get(i).getUploader());
                values.put(ShareVideoAlbumDataColumns.TITLE, list.get(i).getTitle());
                values.put(ShareVideoAlbumDataColumns.THUMB_URL, list.get(i).getThumbUrl());
                values.put(ShareVideoAlbumDataColumns.UPLOAD_TIME, list.get(i).getUploadTime());
            
                String[] selectionArgs = {
                		String.valueOf(list.get(i).getAlbumId())
                    };
                
                context.getContentResolver().update(ShareVideoAlbumDataColumns.SHARE_VIDEO_ALBUM_DATA_CONTENT_URI, values, selection, selectionArgs);
            }
           
        }
    }
 
    public static List<ShareVideoAlbumInfo> getVideoAlbumDataList(Context context, BaseClient client) {
        Log.i(TAG, "getVideoAlbumDataList");
        List<ShareVideoAlbumInfo> retList = new ArrayList<ShareVideoAlbumInfo>();
        Cursor c = null;

        try {
            String selection = ShareVideoAlbumDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		ShareVideoAlbumDataColumns.SHARE_VIDEO_ALBUM_DATA_CONTENT_URI,
            		ShareVideoAlbumDataColumns.SHARE_VIDEO_ALBUM_DATA_PROJECTION, selection, selectionArgs,
                    null);
            while (c != null && c.moveToNext()) {
            	ShareVideoAlbumInfo data = new ShareVideoAlbumInfo();
            	data.setAlbumId(c.getInt(c.getColumnIndex(ShareVideoAlbumDataColumns.ALBUM_ID)));
                data.setTitle(c.getString(c.getColumnIndex(ShareVideoAlbumDataColumns.TITLE)));
                data.setThumbUrl(c.getString(c.getColumnIndex(ShareVideoAlbumDataColumns.THUMB_URL)));
                data.setUploadTime(c.getString(c.getColumnIndex(ShareVideoAlbumDataColumns.UPLOAD_TIME)));
                retList.add(data);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return retList;
    }

    public static boolean isExistVideoAlbumData(Context context, BaseClient client) {
        boolean result = false;
        Cursor c = null;

        try {
            String selection = ShareVideoAlbumDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		ShareVideoAlbumDataColumns.SHARE_VIDEO_ALBUM_DATA_CONTENT_URI, new String[] {
                        "count(content_id) AS contentid_num"
                    }, selection, selectionArgs, null);
            while (c != null && c.moveToNext()) {
                result = c.getInt(0) > 0 ? true : false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static void addVideoAlbumDataList(Context context, List<ShareVideoAlbumInfo> addList,
            BaseClient client) {
        Log.i(TAG, "addVideoAlbumDataList");
        int arraySize = addList.size();
        ContentValues[] valueArray = new ContentValues[arraySize];

        for (int i = 0; i < arraySize; i++) {
            ContentValues values = new ContentValues();
            values.put(ShareVideoAlbumDataColumns.CONTENT_ID, client.getContentId());
            values.put(ShareVideoAlbumDataColumns.ALBUM_ID, addList.get(i).getAlbumId());
            values.put(ShareVideoAlbumDataColumns.UPLOADER, addList.get(i).getUploader());
            values.put(ShareVideoAlbumDataColumns.TITLE, addList.get(i).getTitle());
            values.put(ShareVideoAlbumDataColumns.THUMB_URL, addList.get(i).getThumbUrl());
            values.put(ShareVideoAlbumDataColumns.UPLOAD_TIME, addList.get(i).getUploadTime());
            valueArray[i] = values;
        }

        context.getContentResolver().bulkInsert(ShareVideoAlbumDataColumns.SHARE_VIDEO_ALBUM_DATA_CONTENT_URI,
                valueArray);
    }

    public static void removeVideoAlbumDataList(Context context, BaseClient client) {
        String selection = ShareVideoAlbumDataColumns.CONTENT_ID + "=?";
        String[] selectionArgs = {
            client.getContentId()
        };
        context.getContentResolver().delete(ShareVideoAlbumDataColumns.SHARE_VIDEO_ALBUM_DATA_CONTENT_URI,
                selection, selectionArgs);
    }
    
    /*-------------------------------------ShareVideoData---------------------------------------------------*/
    public static void setShareVideoDataList(Context context, List<ShareVideoInfo> list, BaseClient client) {
    	removeShareVideoDataList(context, client);

        if (list != null && list.size() > 0) {
            addShareVideoDataList(context, list, client);
        }
    }

    public static boolean isExistShareVideoData(Context context, BaseClient client) {
        boolean result = false;
        Cursor c = null;

        try {
            String selection = ShareVideoDataColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
            		ShareVideoDataColumns.SHARE_VIDEO_DATA_CONTENT_URI, new String[] {
                        "count(content_id) AS contentid_num"
                    }, selection, selectionArgs, null);
            while (c != null && c.moveToNext()) {
                result = c.getInt(0) > 0 ? true : false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static void addShareVideoDataList(Context context, List<ShareVideoInfo> addList,
            BaseClient client) {
        Log.i(TAG, "addShareVideoDataList");
        int arraySize = addList.size();
        ContentValues[] valueArray = new ContentValues[arraySize];

        for (int i = 0; i < arraySize; i++) {
            ContentValues values = new ContentValues();
            values.put(ShareVideoDataColumns.CONTENT_ID, client.getContentId());
            values.put(ShareVideoDataColumns.VIDEO_ID, addList.get(i).getVideoId());
            values.put(ShareVideoDataColumns.UPLOAD_TIME, addList.get(i).getUploadTime());
            values.put(ShareVideoDataColumns.UPLOADER, addList.get(i).getUploader());
            values.put(ShareVideoDataColumns.TITLE, addList.get(i).getTitle());
            values.put(ShareVideoDataColumns.VIDEO_URL, addList.get(i).getVideoUrl());
            
            valueArray[i] = values;
        }

        context.getContentResolver().bulkInsert(ShareVideoDataColumns.SHARE_VIDEO_DATA_CONTENT_URI,
                valueArray);
    }

    public static void removeShareVideoDataList(Context context, BaseClient client) {
        String selection = ShareVideoDataColumns.CONTENT_ID + "=?";
        String[] selectionArgs = {
            client.getContentId()
        };
        context.getContentResolver().delete(ShareVideoDataColumns.SHARE_VIDEO_DATA_CONTENT_URI,
                selection, selectionArgs);
    }
}

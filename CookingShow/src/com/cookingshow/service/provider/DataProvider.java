/**
 * 
 */
package com.cookingshow.service.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

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

/**
 * @author wei.ren
 *
 */
public class DataProvider extends ContentProvider {
    private static final String TAG = "DataProvider";
    private static final String DATABASE_NAME = "CookingshowService.db";

    private static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "com.cookingshow.service.provider";
    public static final String TABLE_DATA_UPDATE = "data_update";
    public static final String TABLE_BOOT_SCREEN = "boot_screen";
    public static final String TABLE_NAVIGATION_DATA = "navigation_data";
    public static final String TABLE_DISH_DATA = "dish_data";
    public static final String TABLE_TOP_DATA = "top_data";
    public static final String TABLE_RECOMMEND_DATA = "recommend_data";
    public static final String TABLE_COOK_STEP_DATA = "cook_step_data";
    public static final String TABLE_DISH_DISCUSS_DATA = "dish_discuss_data";
    public static final String TABLE_SHARE_IMG_DATA = "share_img_data";
    public static final String TABLE_SHARE_VIDEO_ALBUM_DATA = "share_video_album_data";
    public static final String TABLE_SHARE_VIDEO_DATA = "share_video_data";
    public static final String TABLE_MY_VIEW_RECORD_DATA = "my_view_record_data";

    public static final int URI_MATCH_TABLE_DATA_UPDATE = 1;
    public static final int URI_MATCH_TABLE_DATA_UPDATE_ID = 2;
    public static final int URI_MATCH_TABLE_BOOT_SCREEN = 3;
    public static final int URI_MATCH_TABLE_BOOT_SCREEN_ID = 4;
    public static final int URI_MATCH_TABLE_DISH_DATA = 5;
    public static final int URI_MATCH_TABLE_DISH_DATA_ID = 6;
    public static final int URI_MATCH_TABLE_NAVIGATION_DATA = 7;
    public static final int URI_MATCH_TABLE_NAVIGATION_DATA_ID = 8;
    public static final int URI_MATCH_TABLE_TOP_DATA = 9;
    public static final int URI_MATCH_TABLE_TOP_DATA_ID = 10;
    public static final int URI_MATCH_TABLE_RECOMMEND_DATA = 11;
    public static final int URI_MATCH_TABLE_RECOMMEND_DATA_ID = 12;
    public static final int URI_MATCH_TABLE_COOK_STEP_DATA = 13;
    public static final int URI_MATCH_TABLE_COOK_STEP_DATA_ID = 14;
    public static final int URI_MATCH_TABLE_DISH_DISCUSS_DATA = 15;
    public static final int URI_MATCH_TABLE_DISH_DISCUSS_DATA_ID = 16;
    public static final int URI_MATCH_TABLE_SHARE_IMG_DATA = 17;
    public static final int URI_MATCH_TABLE_SHARE_IMG_DATA_ID = 18;
    public static final int URI_MATCH_TABLE_VIEW_RECORD_DATA = 19;
    public static final int URI_MATCH_TABLE_VIEW_RECORD_DATA_ID = 20;
    public static final int URI_MATCH_TABLE_SHARE_VIDEO_ALBUM_DATA = 21;
    public static final int URI_MATCH_TABLE_SHARE_VIDEO_ALBUM_DATA_ID = 22;
    public static final int URI_MATCH_TABLE_SHARE_VIDEO_DATA = 23;
    public static final int URI_MATCH_TABLE_SHARE_VIDEO_DATA_ID = 24;
    private DatabaseHelper mDatabaseHelper;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    {
        URI_MATCHER.addURI(AUTHORITY, TABLE_DATA_UPDATE, URI_MATCH_TABLE_DATA_UPDATE);
        URI_MATCHER.addURI(AUTHORITY, TABLE_DATA_UPDATE + "/#",
        		URI_MATCH_TABLE_DATA_UPDATE_ID);
        
        URI_MATCHER.addURI(AUTHORITY, TABLE_BOOT_SCREEN, URI_MATCH_TABLE_BOOT_SCREEN);
        URI_MATCHER.addURI(AUTHORITY, TABLE_BOOT_SCREEN + "/#",
        		URI_MATCH_TABLE_BOOT_SCREEN_ID);
        
        URI_MATCHER.addURI(AUTHORITY, TABLE_DISH_DATA, URI_MATCH_TABLE_DISH_DATA);
        URI_MATCHER.addURI(AUTHORITY, TABLE_DISH_DATA + "/#",
        		URI_MATCH_TABLE_DISH_DATA_ID);
        
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAVIGATION_DATA, URI_MATCH_TABLE_NAVIGATION_DATA);
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAVIGATION_DATA + "/#",
        		URI_MATCH_TABLE_NAVIGATION_DATA_ID);
        
        URI_MATCHER.addURI(AUTHORITY, TABLE_TOP_DATA, URI_MATCH_TABLE_TOP_DATA);
        URI_MATCHER.addURI(AUTHORITY, TABLE_TOP_DATA + "/#",
        		URI_MATCH_TABLE_TOP_DATA_ID);
        
        URI_MATCHER.addURI(AUTHORITY, TABLE_RECOMMEND_DATA, URI_MATCH_TABLE_RECOMMEND_DATA);
        URI_MATCHER.addURI(AUTHORITY, TABLE_RECOMMEND_DATA + "/#",
        		URI_MATCH_TABLE_RECOMMEND_DATA_ID);
        
        URI_MATCHER.addURI(AUTHORITY, TABLE_COOK_STEP_DATA, URI_MATCH_TABLE_COOK_STEP_DATA);
        URI_MATCHER.addURI(AUTHORITY, TABLE_COOK_STEP_DATA + "/#",
        		URI_MATCH_TABLE_COOK_STEP_DATA_ID);

        URI_MATCHER.addURI(AUTHORITY, TABLE_DISH_DISCUSS_DATA, URI_MATCH_TABLE_DISH_DISCUSS_DATA);
        URI_MATCHER.addURI(AUTHORITY, TABLE_DISH_DISCUSS_DATA + "/#",
        		URI_MATCH_TABLE_DISH_DISCUSS_DATA_ID);
        
        URI_MATCHER.addURI(AUTHORITY, TABLE_SHARE_IMG_DATA, URI_MATCH_TABLE_SHARE_IMG_DATA);
        URI_MATCHER.addURI(AUTHORITY, TABLE_SHARE_IMG_DATA + "/#",
        		URI_MATCH_TABLE_SHARE_IMG_DATA_ID);
        
        URI_MATCHER.addURI(AUTHORITY, TABLE_MY_VIEW_RECORD_DATA, URI_MATCH_TABLE_VIEW_RECORD_DATA);
        URI_MATCHER.addURI(AUTHORITY, TABLE_MY_VIEW_RECORD_DATA + "/#",
        		URI_MATCH_TABLE_VIEW_RECORD_DATA_ID);
        
        URI_MATCHER.addURI(AUTHORITY, TABLE_SHARE_VIDEO_ALBUM_DATA, URI_MATCH_TABLE_SHARE_VIDEO_ALBUM_DATA);
        URI_MATCHER.addURI(AUTHORITY, TABLE_SHARE_VIDEO_ALBUM_DATA + "/#",
        		URI_MATCH_TABLE_SHARE_VIDEO_ALBUM_DATA_ID);
        
        URI_MATCHER.addURI(AUTHORITY, TABLE_SHARE_VIDEO_DATA, URI_MATCH_TABLE_SHARE_VIDEO_DATA);
        URI_MATCHER.addURI(AUTHORITY, TABLE_SHARE_VIDEO_DATA + "/#",
        		URI_MATCH_TABLE_SHARE_VIDEO_DATA_ID);
    }

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = null;

        try {
            int match = URI_MATCHER.match(uri);
            if (match != UriMatcher.NO_MATCH) {
                String tableName = getTableName(match);
                if (tableName == null) {
                    return 0;
                } else {
                    db = mDatabaseHelper.getWritableDatabase();
                    db.beginTransaction();
                    int count = db.delete(tableName, selection, selectionArgs);
                    db.setTransactionSuccessful();

                    getContext().getContentResolver().notifyChange(uri, null);
                    return count;
                }
            }

        } catch (Exception e) {
            ;
        } finally {
            try {
                if (db != null) {
                    db.endTransaction();
                }
            } catch (Exception e) {
                ;
            }
        }

        return 0;
    }

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        int match = URI_MATCHER.match(uri);

        switch (match)
        {
            case URI_MATCH_TABLE_DATA_UPDATE:
                return "vnd.android.cursor.dir/" + TABLE_DATA_UPDATE;
            case URI_MATCH_TABLE_DATA_UPDATE_ID:
                return "vnd.android.cursor.item/" + TABLE_DATA_UPDATE;
            case URI_MATCH_TABLE_BOOT_SCREEN:
                return "vnd.android.cursor.dir/" + TABLE_BOOT_SCREEN;
            case URI_MATCH_TABLE_BOOT_SCREEN_ID:
                return "vnd.android.cursor.item/" + TABLE_BOOT_SCREEN;
            case URI_MATCH_TABLE_DISH_DATA:
                return "vnd.android.cursor.dir/" + TABLE_DISH_DATA;
            case URI_MATCH_TABLE_DISH_DATA_ID:
                return "vnd.android.cursor.item/" + TABLE_DISH_DATA;
            case URI_MATCH_TABLE_NAVIGATION_DATA:
                return "vnd.android.cursor.dir/" + TABLE_NAVIGATION_DATA;
            case URI_MATCH_TABLE_NAVIGATION_DATA_ID:
                return "vnd.android.cursor.item/" + TABLE_NAVIGATION_DATA;    
            case URI_MATCH_TABLE_TOP_DATA:
                return "vnd.android.cursor.dir/" + TABLE_TOP_DATA;
            case URI_MATCH_TABLE_TOP_DATA_ID:
                return "vnd.android.cursor.item/" + TABLE_TOP_DATA;
            case URI_MATCH_TABLE_RECOMMEND_DATA:
                return "vnd.android.cursor.dir/" + TABLE_RECOMMEND_DATA;
            case URI_MATCH_TABLE_RECOMMEND_DATA_ID:
                return "vnd.android.cursor.item/" + TABLE_RECOMMEND_DATA; 
            case URI_MATCH_TABLE_COOK_STEP_DATA:
                return "vnd.android.cursor.dir/" + TABLE_COOK_STEP_DATA;
            case URI_MATCH_TABLE_COOK_STEP_DATA_ID:
                return "vnd.android.cursor.item/" + TABLE_COOK_STEP_DATA;
            case URI_MATCH_TABLE_DISH_DISCUSS_DATA:
                return "vnd.android.cursor.dir/" + TABLE_DISH_DISCUSS_DATA;
            case URI_MATCH_TABLE_DISH_DISCUSS_DATA_ID:
                return "vnd.android.cursor.item/" + TABLE_DISH_DISCUSS_DATA;
            case URI_MATCH_TABLE_SHARE_IMG_DATA:
                return "vnd.android.cursor.dir/" + TABLE_SHARE_IMG_DATA;
            case URI_MATCH_TABLE_SHARE_IMG_DATA_ID:
                return "vnd.android.cursor.item/" + TABLE_SHARE_IMG_DATA;
            case URI_MATCH_TABLE_VIEW_RECORD_DATA:
                return "vnd.android.cursor.dir/" + TABLE_MY_VIEW_RECORD_DATA;
            case URI_MATCH_TABLE_VIEW_RECORD_DATA_ID:
                return "vnd.android.cursor.item/" + TABLE_MY_VIEW_RECORD_DATA;
            case URI_MATCH_TABLE_SHARE_VIDEO_ALBUM_DATA:
                return "vnd.android.cursor.dir/" + TABLE_SHARE_VIDEO_ALBUM_DATA;
            case URI_MATCH_TABLE_SHARE_VIDEO_ALBUM_DATA_ID:
                return "vnd.android.cursor.item/" + TABLE_SHARE_VIDEO_ALBUM_DATA;
            case URI_MATCH_TABLE_SHARE_VIDEO_DATA:
                return "vnd.android.cursor.dir/" + TABLE_SHARE_VIDEO_DATA;
            case URI_MATCH_TABLE_SHARE_VIDEO_DATA_ID:
                return "vnd.android.cursor.item/" + TABLE_SHARE_VIDEO_DATA; 
            default:
                return null;
        }
    }

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        // TODO Auto-generated method stub
        int result = 0;
        SQLiteDatabase db = null;

        int match = URI_MATCHER.match(uri);
        if (match != UriMatcher.NO_MATCH) {
            String tableName = getTableName(match);

            if (tableName != null) {
                try {
                    db = mDatabaseHelper.getWritableDatabase();
                    db.beginTransaction();

                    for (int i = 0; i < values.length; i++) {
                        db.insert(tableName, null, values[i]);
                    }
                    db.setTransactionSuccessful();

                    result = values.length;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (db != null) {
                            db.endTransaction();
                        }
                        getContext().getContentResolver().notifyChange(uri, null);
                    } catch (Exception e) {
                        ;
                    }
                }
            }
        }

        return result;
    }

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
        Log.i(TAG, "onCreate");
        mDatabaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return false;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = null;

        int match = URI_MATCHER.match(uri);
        if (match != UriMatcher.NO_MATCH) {
            String tableName = getTableName(match);
            if (tableName == null) {
                return null;
            } else {
                db = mDatabaseHelper.getReadableDatabase();
                return db.query(tableName, projection, selection, selectionArgs, null, null,
                        sortOrder);
            }
        }

        return null;
    }

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = null;

        int match = URI_MATCHER.match(uri);
        if (match != UriMatcher.NO_MATCH) {
            String tableName = getTableName(match);
            if (tableName == null) {
                return 0;
            } else {
                db = mDatabaseHelper.getWritableDatabase();
                
                db.update(tableName, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return 0;
    }

    private String getTableName(int match) {
        switch (match) {
            case URI_MATCH_TABLE_DATA_UPDATE:
            case URI_MATCH_TABLE_DATA_UPDATE_ID:
                return TABLE_DATA_UPDATE;
            case URI_MATCH_TABLE_BOOT_SCREEN:
            case URI_MATCH_TABLE_BOOT_SCREEN_ID:
                return TABLE_BOOT_SCREEN;
            case URI_MATCH_TABLE_DISH_DATA:
            case URI_MATCH_TABLE_DISH_DATA_ID:
                return TABLE_DISH_DATA;
            case URI_MATCH_TABLE_NAVIGATION_DATA:
            case URI_MATCH_TABLE_NAVIGATION_DATA_ID:
                return TABLE_NAVIGATION_DATA;
            case URI_MATCH_TABLE_TOP_DATA:
            case URI_MATCH_TABLE_TOP_DATA_ID:
                return TABLE_TOP_DATA;
            case URI_MATCH_TABLE_RECOMMEND_DATA:
            case URI_MATCH_TABLE_RECOMMEND_DATA_ID:
                return TABLE_RECOMMEND_DATA;
            case URI_MATCH_TABLE_COOK_STEP_DATA:
            case URI_MATCH_TABLE_COOK_STEP_DATA_ID:
                return TABLE_COOK_STEP_DATA;
            case URI_MATCH_TABLE_DISH_DISCUSS_DATA:
            case URI_MATCH_TABLE_DISH_DISCUSS_DATA_ID:
                return TABLE_DISH_DISCUSS_DATA;
            case URI_MATCH_TABLE_SHARE_IMG_DATA:
            case URI_MATCH_TABLE_SHARE_IMG_DATA_ID:
                return TABLE_SHARE_IMG_DATA;
            case URI_MATCH_TABLE_VIEW_RECORD_DATA:
            case URI_MATCH_TABLE_VIEW_RECORD_DATA_ID:
                return TABLE_MY_VIEW_RECORD_DATA;
            case URI_MATCH_TABLE_SHARE_VIDEO_ALBUM_DATA:
            case URI_MATCH_TABLE_SHARE_VIDEO_ALBUM_DATA_ID:
                return TABLE_SHARE_VIDEO_ALBUM_DATA;
            case URI_MATCH_TABLE_SHARE_VIDEO_DATA:
            case URI_MATCH_TABLE_SHARE_VIDEO_DATA_ID:
                return TABLE_SHARE_VIDEO_DATA;
            default:
                return null;
        }
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_DATA_UPDATE + " ("
                    + DataUpdateColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + DataUpdateColumns.CONTENT_ID + " TEXT,"
                    + DataUpdateColumns.VERSION + " TEXT,"
                    + DataUpdateColumns.NAME + " TEXT,"
                    + DataUpdateColumns.URL + " TEXT"
                    + ")");
            
            db.execSQL("CREATE TABLE " + TABLE_BOOT_SCREEN + " ("
                    + BootScreenColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + BootScreenColumns.CONTENT_ID + " TEXT,"
                    + BootScreenColumns.TITLE + " TEXT,"
                    + BootScreenColumns.THUMB_PATH + " TEXT,"
                    + BootScreenColumns.THUMB_URL + " TEXT"
                    + ")");

            db.execSQL("CREATE TABLE " + TABLE_NAVIGATION_DATA + " ("
                    + NavigationDataColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + NavigationDataColumns.CONTENT_ID + " TEXT,"
                    + NavigationDataColumns.MENU_ID + " INTEGER,"
                    + NavigationDataColumns.TYPE + " VARCHAR(20),"
                    + NavigationDataColumns.TITLE + " VARCHAR(32),"
                    + NavigationDataColumns.EN_TITLE + " VARCHAR(32),"
                    + NavigationDataColumns.CODE + " VARCHAR(32),"
                    + NavigationDataColumns.ORDER_NUM + " INTEGER"
                    + ")");

            db.execSQL("CREATE TABLE " + TABLE_DISH_DATA + " ("
                    + DishDataColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + DishDataColumns.CONTENT_ID + " TEXT,"
                    + DishDataColumns.DISH_ID + " INTEGER,"
                    + DishDataColumns.UPLOAD_TIME + " TEXT,"
                    + DishDataColumns.UPLOADER + " VARCHAR(20),"
                    + DishDataColumns.TITLE + " VARCHAR(255),"
                    + DishDataColumns.TYPE + " VARCHAR(20),"
                    + DishDataColumns.THUMB_URL + " TEXT,"
                    + DishDataColumns.VIDEO_URL + " TEXT,"
                    + DishDataColumns.TIPS + " TEXT,"
                    + DishDataColumns.MATERIALS + " TEXT"
                    + ")");
            
            db.execSQL("CREATE TABLE " + TABLE_TOP_DATA + " ("
                    + TopDataColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TopDataColumns.CONTENT_ID + " TEXT,"
                    + TopDataColumns.DISH_ID + " INTEGER,"
                    + TopDataColumns.UPLOADER + " VARCHAR(20),"
                    + TopDataColumns.TITLE + " VARCHAR(255),"
                    + TopDataColumns.THUMB_URL + " TEXT,"
                    + TopDataColumns.VIDEO_URL + " TEXT,"
                    + TopDataColumns.TIPS + " TEXT,"
                    + TopDataColumns.MATERIALS + " TEXT"
                    + ")");
            
            db.execSQL("CREATE TABLE " + TABLE_RECOMMEND_DATA + " ("
                    + RecommendDataColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + RecommendDataColumns.CONTENT_ID + " TEXT,"
                    + RecommendDataColumns.DISH_ID + " INTEGER,"
                    + RecommendDataColumns.UPLOADER + " VARCHAR(20),"
                    + RecommendDataColumns.TITLE + " VARCHAR(255),"
                    + RecommendDataColumns.THUMB_URL + " TEXT,"
                    + RecommendDataColumns.VIDEO_URL + " TEXT,"
                    + RecommendDataColumns.TIPS + " TEXT,"
                    + RecommendDataColumns.MATERIALS + " TEXT"
                    + ")");
            
            db.execSQL("CREATE TABLE " + TABLE_COOK_STEP_DATA + " ("
                    + CookStepDataColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + CookStepDataColumns.CONTENT_ID + " TEXT,"
                    + CookStepDataColumns.DISH_ID + " INTEGER,"
                    + CookStepDataColumns.COOK_SN + " INTEGER,"
                    + CookStepDataColumns.COOK_TEXT + " TEXT"
                    + ")");
            
            db.execSQL("CREATE TABLE " + TABLE_DISH_DISCUSS_DATA + " ("
                    + DishDiscussDataColumns.CONTENT_ID + " TEXT,"
                    + DishDiscussDataColumns.DISCUSS_ID + " INTEGER,"
                    + DishDiscussDataColumns.DISH_ID + " INTEGER,"
                    + DishDiscussDataColumns.SEND_TIME + " DATETIME,"
                    + DishDiscussDataColumns.SENDER + " VARCHAR(20),"
                    + DishDiscussDataColumns.RECEIVER + " VARCHAR(20),"
                    + DishDiscussDataColumns.CONTENT + " TEXT,"
                    + DishDiscussDataColumns.STATUS + " VARCHAR(20)"
                    + ")");
            
            db.execSQL("CREATE TABLE " + TABLE_SHARE_IMG_DATA + " ("
                    + ShareImgDataColumns.CONTENT_ID + " TEXT,"
                    + ShareImgDataColumns.IMG_ID + " INTEGER,"
                    + ShareImgDataColumns.DISH_ID + " INTEGER,"
                    + ShareImgDataColumns.DISH_NAME + " TEXT,"
                    + ShareImgDataColumns.UPLOAD_TIME + " DATETIME,"
                    + ShareImgDataColumns.UPLOADER + " TEXT,"
                    + ShareImgDataColumns.IMG_URL + " TEXT,"
                    + ShareImgDataColumns.FEELING + " TEXT,"
                    + ShareImgDataColumns.LIKE_TIMES + " INTEGER,"
                    + ShareImgDataColumns.PUBLISHED + " INTEGER"
                    + ")");
            
            db.execSQL("CREATE TABLE " + TABLE_MY_VIEW_RECORD_DATA + " ("
                    + ViewRecordDataColumns.CONTENT_ID + " TEXT,"
                    + ViewRecordDataColumns.ALBUM_ID + " INTEGER,"
                    + ViewRecordDataColumns.DISH_ID + " INTEGER,"
                    + ViewRecordDataColumns.UPLOADER + " VARCHAR(20),"
                    + ViewRecordDataColumns.TITLE + " VARCHAR(255),"
                    + ViewRecordDataColumns.THUMB_URL + " TEXT,"
                    + ViewRecordDataColumns.VIDEO_URL + " TEXT,"
                    + ViewRecordDataColumns.TIPS + " TEXT,"
                    + ViewRecordDataColumns.MATERIALS + " TEXT,"
                    + ViewRecordDataColumns.VIEW_TIMES + " INTEGER,"
                    + ViewRecordDataColumns.LIKE_TIMES + " INTEGER"
                    + ")");
            
            db.execSQL("CREATE TABLE " + TABLE_SHARE_VIDEO_ALBUM_DATA + " ("
            		+ ShareVideoAlbumDataColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ShareVideoAlbumDataColumns.CONTENT_ID + " TEXT,"
                    + ShareVideoAlbumDataColumns.ALBUM_ID + " INTEGER,"
                    + ShareVideoAlbumDataColumns.UPLOADER + " VARCHAR(20),"
                    + ShareVideoAlbumDataColumns.TITLE + " VARCHAR(255),"
                    + ShareVideoAlbumDataColumns.THUMB_URL + " TEXT,"
                    + ShareVideoAlbumDataColumns.UPLOAD_TIME + " TEXT"
                    + ")");
            
            db.execSQL("CREATE TABLE " + TABLE_SHARE_VIDEO_DATA + " ("
                    + ShareVideoDataColumns.CONTENT_ID + " TEXT,"
                    + ShareVideoDataColumns.VIDEO_ID + " INTEGER,"
                    + ShareVideoDataColumns.UPLOAD_TIME + " TEXT,"
                    + ShareVideoDataColumns.UPLOADER + " VARCHAR(20),"
                    + ShareVideoDataColumns.TITLE + " VARCHAR(255),"
                    + ShareVideoDataColumns.VIDEO_URL + " TEXT"
                    + ")");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_DATA_UPDATE);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_BOOT_SCREEN);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_DISH_DATA);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAVIGATION_DATA);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_TOP_DATA);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_RECOMMEND_DATA);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_COOK_STEP_DATA);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_DISH_DISCUSS_DATA);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_SHARE_IMG_DATA);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_MY_VIEW_RECORD_DATA);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_SHARE_VIDEO_ALBUM_DATA);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_SHARE_VIDEO_DATA);
            onCreate(db);
        }
    }	

}

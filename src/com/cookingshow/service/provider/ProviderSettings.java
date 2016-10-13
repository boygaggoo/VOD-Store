/**
 * 
 */
package com.cookingshow.service.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author wei.ren
 *
 */
public class ProviderSettings {
    private ProviderSettings() {

    }
    
    public static final class DataUpdateColumns implements BaseColumns {
        private DataUpdateColumns() {
        	
        }
        
        public static final Uri DATA_UPDATE_CONTENT_URI =
                Uri.parse("content://" + DataProvider.AUTHORITY + "/"
                        + DataProvider.TABLE_DATA_UPDATE); 
        
        public static final String CONTENT_ID = "content_id";
        public static final String VERSION = "version";
        public static final String NAME = "name";
        public static final String URL = "url";
        
        public static String[] DATA_Update_PROJECTION = new String[] {
            _ID,
            CONTENT_ID,
            VERSION,
            NAME,
            URL
        };
    }
    
    public static final class BootScreenColumns implements BaseColumns {
        private BootScreenColumns() {
        	
        }
        
        public static final Uri BOOT_SCREEN_CONTENT_URI =
                Uri.parse("content://" + DataProvider.AUTHORITY + "/"
                        + DataProvider.TABLE_BOOT_SCREEN); 
        
        public static final String CONTENT_ID = "content_id";
        public static final String TITLE = "title";
        public static final String THUMB_PATH = "thumb_path";
        public static final String THUMB_URL = "thumb_url";
        
        public static String[] BOOT_SCREEN_PROJECTION = new String[] {
            _ID,
            CONTENT_ID,
            TITLE,
            THUMB_PATH,
            THUMB_URL
        };
    }
    
    public static final class DishDataColumns implements BaseColumns {
        private DishDataColumns() {
        	
        }
        
        public static final Uri DISH_DATA_CONTENT_URI =
                Uri.parse("content://" + DataProvider.AUTHORITY + "/"
                        + DataProvider.TABLE_DISH_DATA);
 
        public static final String CONTENT_ID = "content_id";
        public static final String DISH_ID = "dish_id";
        public static final String UPLOAD_TIME = "upload_time";
        public static final String UPLOADER = "uploader";
        public static final String TITLE = "title";
        public static final String TYPE = "type";
        public static final String THUMB_URL = "thumb_url";
        public static final String VIDEO_URL = "video_url";
        public static final String TIPS = "tips";
        public static final String MATERIALS = "materials";

        public static String[] DISH_DATA_PROJECTION = new String[] {
            _ID,
            CONTENT_ID,
            DISH_ID,
            UPLOAD_TIME,
            UPLOADER,
            TITLE,
            TYPE,
            THUMB_URL,
            VIDEO_URL,
            TIPS,
            MATERIALS
        };
    }

    public static final class TopDataColumns implements BaseColumns {
        private TopDataColumns() {
        	
        }
        
        public static final Uri TOP_DATA_CONTENT_URI =
                Uri.parse("content://" + DataProvider.AUTHORITY + "/"
                        + DataProvider.TABLE_TOP_DATA);
 
        public static final String CONTENT_ID = "content_id";
        public static final String DISH_ID = "dish_id";
        public static final String UPLOADER = "uploader";
        public static final String TITLE = "title";
        public static final String THUMB_URL = "thumb_url";
        public static final String VIDEO_URL = "video_url";
        public static final String TIPS = "tips";
        public static final String MATERIALS = "materials";

        public static String[] TOP_DATA_PROJECTION = new String[] {
            _ID,
            CONTENT_ID,
            DISH_ID,
            UPLOADER,
            TITLE,
            THUMB_URL,
            VIDEO_URL,
            TIPS,
            MATERIALS
        };
    }

    public static final class RecommendDataColumns implements BaseColumns {
        private RecommendDataColumns() {
        	
        }
        
        public static final Uri RECOMMEND_DATA_CONTENT_URI =
                Uri.parse("content://" + DataProvider.AUTHORITY + "/"
                        + DataProvider.TABLE_RECOMMEND_DATA);
 
        public static final String CONTENT_ID = "content_id";
        public static final String DISH_ID = "dish_id";
        public static final String UPLOADER = "uploader";
        public static final String TITLE = "title";
        public static final String THUMB_URL = "thumb_url";
        public static final String VIDEO_URL = "video_url";
        public static final String TIPS = "tips";
        public static final String MATERIALS = "materials";

        public static String[] RECOMMEND_DATA_PROJECTION = new String[] {
            _ID,
            CONTENT_ID,
            DISH_ID,
            UPLOADER,
            TITLE,
            THUMB_URL,
            VIDEO_URL,
            TIPS,
            MATERIALS
        };
    }

    public static final class NavigationDataColumns implements BaseColumns {
        private NavigationDataColumns() {
        	
        }
        
        public static final Uri NAVIGATION_DATA_CONTENT_URI =
                Uri.parse("content://" + DataProvider.AUTHORITY + "/"
                        + DataProvider.TABLE_NAVIGATION_DATA);
 
        public static final String CONTENT_ID = "content_id";
        public static final String MENU_ID = "menu_id";
        public static final String TYPE = "type";
        public static final String TITLE = "title";
        public static final String EN_TITLE = "en_title";
        public static final String CODE = "code";
        public static final String ORDER_NUM = "order_num";

        public static String[] NAVIGATION_DATA_PROJECTION = new String[] {
            _ID,
            CONTENT_ID,
            MENU_ID,
            TYPE,
            TITLE,
            EN_TITLE,
            CODE,
            ORDER_NUM
        };
    }
    
    public static final class CookStepDataColumns implements BaseColumns {
        private CookStepDataColumns() {
        	
        }
        
        public static final Uri COOK_STEP_DATA_CONTENT_URI =
                Uri.parse("content://" + DataProvider.AUTHORITY + "/"
                        + DataProvider.TABLE_COOK_STEP_DATA);
 
        public static final String CONTENT_ID = "content_id";
        public static final String DISH_ID = "dish_id";
        public static final String COOK_SN = "cook_sn";
        public static final String COOK_TEXT = "cook_text";

        public static String[] COOK_STEP_DATA_PROJECTION = new String[] {
            _ID,
            CONTENT_ID,
            DISH_ID,
            COOK_SN,
            COOK_TEXT
        };
    }
    
    public static final class DishDiscussDataColumns implements BaseColumns {
        private DishDiscussDataColumns() {
        	
        }
        
        public static final Uri DISH_DISCUSS_DATA_CONTENT_URI =
                Uri.parse("content://" + DataProvider.AUTHORITY + "/"
                        + DataProvider.TABLE_DISH_DISCUSS_DATA);
 
        public static final String CONTENT_ID = "content_id";
        public static final String DISCUSS_ID = "discuss_id";
        public static final String DISH_ID = "dish_id";
        public static final String SEND_TIME = "send_time";
        public static final String SENDER = "sender";
        public static final String RECEIVER = "receiver";
        public static final String CONTENT = "content";
        public static final String STATUS = "status";


        public static String[] DISH_DISCUSS_DATA_PROJECTION = new String[] {
            CONTENT_ID,
            DISCUSS_ID,
            DISH_ID,
            SEND_TIME,
            SENDER,
            RECEIVER,
            CONTENT,
            STATUS
        };
    }
    
    public static final class ShareImgDataColumns implements BaseColumns {
        private ShareImgDataColumns() {
        	
        }
        
        public static final Uri SHARE_IMG_DATA_CONTENT_URI =
                Uri.parse("content://" + DataProvider.AUTHORITY + "/"
                        + DataProvider.TABLE_SHARE_IMG_DATA);
 
        public static final String CONTENT_ID = "content_id";
        public static final String IMG_ID = "img_id";
        public static final String DISH_ID = "dish_id";
        public static final String DISH_NAME = "dish_name";
        public static final String UPLOAD_TIME = "upload_time";
        public static final String UPLOADER = "uploader";
        public static final String IMG_URL = "img_url";
        public static final String FEELING = "feeling";
        public static final String LIKE_TIMES = "like_times";
        public static final String PUBLISHED = "published";

        public static String[] SHARE_IMG_DATA_PROJECTION = new String[] {
            CONTENT_ID,
            IMG_ID,
            DISH_ID,
            DISH_NAME,
            UPLOAD_TIME,
            UPLOADER,
            IMG_URL,
            FEELING,
            LIKE_TIMES,
            PUBLISHED
        };
    }
    
    public static final class ViewRecordDataColumns implements BaseColumns {
        private ViewRecordDataColumns() {
        	
        }
        
        public static final Uri VIEW_RECORD_DATA_CONTENT_URI =
                Uri.parse("content://" + DataProvider.AUTHORITY + "/"
                        + DataProvider.TABLE_MY_VIEW_RECORD_DATA);
 
        public static final String CONTENT_ID = "content_id";
        public static final String ALBUM_ID = "album_id";
        public static final String DISH_ID = "dish_id";
        public static final String UPLOADER = "uploader";
        public static final String TITLE = "title";
        public static final String THUMB_URL = "thumb_url";
        public static final String VIDEO_URL = "video_url";
        public static final String TIPS = "tips";
        public static final String MATERIALS = "materials";
        public static final String VIEW_TIMES = "view_times";
        public static final String LIKE_TIMES = "like_times";
        
        public static String[] VIEW_RECORD_DATA_PROJECTION = new String[] {
            CONTENT_ID,
            ALBUM_ID,
            DISH_ID,
            UPLOADER,
            TITLE,
            THUMB_URL,
            VIDEO_URL,
            TIPS,
            MATERIALS,
            VIEW_TIMES,
            LIKE_TIMES
        };
    }
    
    public static final class ShareVideoAlbumDataColumns implements BaseColumns {
        private ShareVideoAlbumDataColumns() {
        	
        }
        
        public static final Uri SHARE_VIDEO_ALBUM_DATA_CONTENT_URI =
                Uri.parse("content://" + DataProvider.AUTHORITY + "/"
                        + DataProvider.TABLE_SHARE_VIDEO_ALBUM_DATA);
 
        public static final String CONTENT_ID = "content_id";
        public static final String ALBUM_ID = "album_id";
        public static final String UPLOADER = "uploader";
        public static final String TITLE = "title";
        public static final String THUMB_URL = "thumb_url";
        public static final String UPLOAD_TIME = "upload_time";
        
        public static String[] SHARE_VIDEO_ALBUM_DATA_PROJECTION = new String[] {
        	_ID,
            CONTENT_ID,
            ALBUM_ID,
            UPLOADER,
            TITLE,
            THUMB_URL,
            UPLOAD_TIME
        };
    }
    
    public static final class ShareVideoDataColumns implements BaseColumns {
        private ShareVideoDataColumns() {
        	
        }
        
        public static final Uri SHARE_VIDEO_DATA_CONTENT_URI =
                Uri.parse("content://" + DataProvider.AUTHORITY + "/"
                        + DataProvider.TABLE_SHARE_VIDEO_DATA);
 
        public static final String CONTENT_ID = "content_id";
        public static final String VIDEO_ID = "video_id";
        public static final String UPLOAD_TIME = "upload_time";
        public static final String UPLOADER = "uploader";
        public static final String TITLE = "title";
        public static final String VIDEO_URL = "video_url";
        
        public static String[] SHARE_VIDEO_DATA_PROJECTION = new String[] {
            CONTENT_ID,
            VIDEO_ID,
            UPLOAD_TIME,
            UPLOADER,
            TITLE,
            VIDEO_URL
        };
    }
}

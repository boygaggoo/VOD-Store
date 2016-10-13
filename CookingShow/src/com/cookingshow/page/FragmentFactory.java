package com.cookingshow.page;

import android.util.Log;

import com.cookingshow.all.AllListRecyclingFragment;
import com.cookingshow.mine.FirstMinePageFragment;
import com.cookingshow.recommend.FirstPageFragment;
import com.cookingshow.recommend.SecondPageFragment;
import com.cookingshow.share.SharePageFragment;
import com.cookingshow.top.TopPageFragment;
import com.cookingshow.userinfo.UserDiningPageFragment;
import com.cookingshow.userinfo.UserHistoryPageFragment;
import com.cookingshow.userinfo.UserPicPageFragment;
import com.cookingshow.userinfo.UserVideoPageFragment;

public class FragmentFactory {

    private static final String TAG = "FragmentFactory";
    public static final String APPLICATION_LIST_FRAGMENT = "application_fragment";
    public static final String USER_HISTORY_LIST_FRAGMENT = "user_history_fragment";
    public static final String USER_VIDEO_LIST_FRAGMENT = "user_video_fragment";
    public static final String FAVORITE_LIST_FRAGMENT = "favorite_fragment";
    public static final String USER_PIC_LIST_FRAGMENT = "user_pic_fragment";
    public static final String USER_DINING_LIST_FRAGMENT = "user_dining_fragment";
    public static final String RECOMMEND_FIRST_FRAGMENT = "recommend_first_fragment";
    public static final String RECOMMEND_OTHER_FRAGMENT = "recommend_other_fragment";
    public static final String TOP_LIST_FRAGMENT = "top_fragment";
    public static final String SHARE_LIST_FRAGMENT = "share_fragment";
    public static final String APP_LIST_FRAGMENT = "app_fragment";
    public static final String MINE_FIRST_FRAGMENT = "mine_first_fragment";
    public static final String MINE_OTHER_FRAGMENT = "mine_other_fragment";
    public static final String ALL_LIST_FRAGMENT = "all_list_fragment";

    public static CommonFragment newInstance(String fragmentType) {
        Log.i(TAG, "newInstance " + fragmentType);
        CommonFragment fragment = null;
        if (RECOMMEND_FIRST_FRAGMENT.equals(fragmentType)) {
            fragment = new FirstPageFragment();
        }else if (RECOMMEND_OTHER_FRAGMENT.equals(fragmentType)) {
            fragment = new SecondPageFragment();
        }else if (TOP_LIST_FRAGMENT.equals(fragmentType)) {
            fragment = new TopPageFragment();
        }else if (SHARE_LIST_FRAGMENT.equals(fragmentType)) {
            fragment = new SharePageFragment();
        }else if (ALL_LIST_FRAGMENT.equals(fragmentType)) {
            fragment = new AllListRecyclingFragment();
        }
        else if (MINE_FIRST_FRAGMENT.equals(fragmentType)) {
            fragment = new FirstMinePageFragment();
        }
        else if (USER_VIDEO_LIST_FRAGMENT.equals(fragmentType)) {
        	fragment = new UserVideoPageFragment();
        }
        else if (USER_PIC_LIST_FRAGMENT.equals(fragmentType)) {
        	fragment = new UserPicPageFragment();
        }
        else if (USER_DINING_LIST_FRAGMENT.equals(fragmentType)) {
        	fragment = new UserDiningPageFragment();
        }
        else if (USER_HISTORY_LIST_FRAGMENT.equals(fragmentType)) {
        	fragment = new UserHistoryPageFragment();
        }
        return fragment;
    }

}

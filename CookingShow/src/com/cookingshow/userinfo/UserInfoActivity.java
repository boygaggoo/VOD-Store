package com.cookingshow.userinfo;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cookingshow.BaseActivity;
import com.cookingshow.R;
import com.cookingshow.adapter.ManagerPageAdapter;
import com.cookingshow.category.CategoryInfo;
import com.cookingshow.category.UserDiningCategory;
import com.cookingshow.category.UserHistoryCategory;
import com.cookingshow.category.UserPicCategory;
import com.cookingshow.category.UserVideoCategory;
import com.cookingshow.navigation.NavigationBar;
import com.cookingshow.navigation.NavigationData;
import com.cookingshow.navigation.NavigationItemView;
import com.cookingshow.navigation.UserInfoNavigationItem;
import com.cookingshow.network.cache.ACache;
import com.cookingshow.page.ActionEventMgr;
import com.cookingshow.page.IPageNotifyListener;
import com.cookingshow.page.PagerDataHelper;
import com.cookingshow.page.ViewPagerState;
import com.cookingshow.view.BigButton;
import com.cookingshow.view.CustomViewPager;
import com.cookingshow.view.ItemCursorView;
import com.cookingshow.view.PageNumView;

public class UserInfoActivity extends BaseActivity
        implements IPageNotifyListener, PagerDataHelper.OnPageCategoryChangedListener {

    private static final String TAG = "UserInfoActivity";
    public static final String ACTION_UI_RECORD_CNT = "com.cookingshow.ui.record.cnt";
    private NavigationBar mNavBar = null;
    private CustomViewPager mViewPager = null;
    private Uri uri;
    private int mNaviIndex;
    private ManagerPageAdapter mViewPagerAdapter = null;
    private boolean isNeedNotifyUpdate;
    private List<NavigationData> mNaviDatas = null;
    private PagerDataHelper mPagerDataHelper = null;
    private ViewPagerState mState = null;
    private NavigationItemView.OnNavigationItemFocusListener mNaviBarListener = null;
    private UserInfoNavigationItem mCurrentMenuItem = null;
    private UserInfoNavigationItem mHistoryMenuItem = null;
    private UserInfoNavigationItem mVideoMenuItem = null;
    private UserInfoNavigationItem mPicMenuItem = null;
    private UserInfoNavigationItem mdiningMenuItem = null;
    private View previewPageView;
    private View nextPageView;
    private RelativeLayout mPagerParentView = null;
    private PageNumView mPageNumView = null;
    private ItemCursorView mCursorView = null;
    private BigButton mBtnOrder = null;
    private BigButton mBtnCoin = null;
    private TextView mTextView = null;
    private ACache mCache = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        mCache = ACache.get(UserInfoActivity.this);

        initUi();
        setListener();
        registerReceiver();
        initViewPager();
        refreshPageViewState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    private void initUi() {
    	mTextView = (TextView) findViewById(R.id.user_title);
        mViewPager = (CustomViewPager) findViewById(R.id.view_pager);
        mNavBar = (NavigationBar) findViewById(R.id.navigation);
        previewPageView = findViewById(R.id.page_preview_view);
        nextPageView = findViewById(R.id.page_next_view);

        mCursorView = (ItemCursorView) findViewById(R.id.cursor_view);
        mPageNumView = (PageNumView) findViewById(R.id.page_num_view);

        mBtnOrder = (BigButton) findViewById(R.id.btn_order);
        mBtnCoin = (BigButton) findViewById(R.id.btn_coin);

		mTextView.setText(mCache.getAsString("nickname"));
		
        mPagerParentView = (RelativeLayout) findViewById(R.id.app_content_view);
        mViewPager.setParentView(mPagerParentView);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageMargin(-4);
        mNaviBarListener = new NavigationItemView.OnNavigationItemFocusListener() {
            @Override
            public boolean onFocusDown() {
                ActionEventMgr.getInstance().notifyOnKeyMode(mState);
                return true;
            }

            @Override
            public boolean onFocusUp() {
                return false;
            }
        };
        initNavigation();
        
        mBtnCoin.setButtonText(getResources().getString(R.string.mine_coin) + mCache.getAsString("myCoin") + getResources().getString(R.string.coin_unit));
    }

    private void initNavigation() {
        List<Integer> menusId = new ArrayList<Integer>();
        menusId.add(R.string.history_menu_user);
        menusId.add(R.string.video_menu_user);
        menusId.add(R.string.pic_menu_user);
        menusId.add(R.string.dining_menu_user);

        mNaviIndex = 0;

        mNaviDatas = new ArrayList<NavigationData>();
        for (int i = 0; i < menusId.size(); i++) {
            NavigationData naviData = new NavigationData();
            naviData.title = getResources().getText(menusId.get(i)).toString();
            naviData.id = menusId.get(i);
            naviData.tag = menusId.get(i);
            mNaviDatas.add(naviData);
        }
        mNavBar.setItemOnClickListener(mNaviItemClickListener);
        mNavBar.inflateItems(mNaviDatas, R.layout.user_info_navigation_item, 17);
        mNavBar.setClickedItem(menusId.get(mNaviIndex));
        mCurrentMenuItem = (UserInfoNavigationItem) mNavBar.getClickedItem();
        mCurrentMenuItem.setOnNavigationItemListener(mNaviBarListener);

        mHistoryMenuItem = (UserInfoNavigationItem) mNavBar.findViewById(R.string.history_menu_user);
        mVideoMenuItem = (UserInfoNavigationItem) mNavBar.findViewById(R.string.video_menu_user);
        mPicMenuItem = (UserInfoNavigationItem) mNavBar.findViewById(R.string.pic_menu_user);
        mdiningMenuItem = (UserInfoNavigationItem) mNavBar.findViewById(R.string.dining_menu_user);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UI_RECORD_CNT);
        registerReceiver(mUpdateCntReciever, intentFilter);
    }

    private void initViewPager() {
        mState = new ViewPagerState();
        List<CategoryInfo> categoryInfoList = new ArrayList<CategoryInfo>();
        for (NavigationData nav : mNaviDatas) {
            CategoryInfo category = null;
            switch (nav.id) {
                case R.string.history_menu_user:
                    category = new UserHistoryCategory();
                break;
                case R.string.video_menu_user:
                    category = new UserVideoCategory();
                    break;
                case R.string.pic_menu_user:
                    category = new UserPicCategory();
                    break;
                case R.string.dining_menu_user:
                	category = new UserDiningCategory();
                	break;
            }
            category.mCategoryName = String.valueOf(nav.id);
            category.mOtherData = nav.title;
            categoryInfoList.add(category);
        }
        Log.i(TAG, "categoryInfoList " + categoryInfoList.toString());
        mPagerDataHelper = new PagerDataHelper(categoryInfoList);
        mPagerDataHelper.setOnCategoryChangedListener(this);
        mPagerDataHelper.setCurrentCategory(mNaviIndex);
        mViewPagerAdapter = new ManagerPageAdapter(getSupportFragmentManager(), mPagerDataHelper);
        mViewPagerAdapter.initViewPager(mViewPager);
        mViewPagerAdapter.setItemCurseView(mCursorView);
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.setCurrentItem(mViewPagerAdapter.getInitPageIndex(), true);
        refreshPageViewState();
    }

    private void setListener() {
        mViewPager.setCustomOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected " + position);
                mState.setCurPageIndex(position);
//                mViewPagerAdapter.setSelectedPos(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG, "onPageScrollStateChanged " + state);
                mState.setScrollState(state);
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    previewPageView.setVisibility(View.INVISIBLE);
                    nextPageView.setVisibility(View.INVISIBLE);
                    mCursorView.setVisibility(View.INVISIBLE);
                }
                if (state == 0 && isNeedNotifyUpdate) {
                    isNeedNotifyUpdate = false;
                    mViewPagerAdapter.notifyDataSetChanged();
                }
                if (state == 0) {
                    mViewPagerAdapter.setSelectedPos(mViewPager.getCurrentItem());
                    refreshPageNumView();
                    ActionEventMgr.getInstance().notifyOnKeyMode(mState);
                    refreshPageViewState();
                    mCursorView.setVisibility(View.VISIBLE);
                }
                ActionEventMgr.getInstance().notifyOnScrollState(mState);
            }
        });
        mViewPager.setFocusable(false);
        mNavBar.setOnClickListener(mNaviItemClickListener);
        nextPageView.setOnClickListener(pageClickListener);
        previewPageView.setOnClickListener(pageClickListener);
        nextPageView.setOnHoverListener(pageHoverListener);
        previewPageView.setOnHoverListener(pageHoverListener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getData() != null) {
            setIntent(intent);
            uri = getIntent().getData();
        }
        super.onNewIntent(intent);
    }

    @Override
    public void onPageCategoryChanged(CategoryInfo category) {
        Log.i(TAG, "category:" + category.mCategoryName);
        int rid = Integer.valueOf(category.mCategoryName);
        mCurrentMenuItem = (UserInfoNavigationItem) mNavBar.findViewById(rid);
        mCurrentMenuItem.setOnNavigationItemListener(mNaviBarListener);
        mNavBar.setClickedItem(rid);

        if(rid == R.string.dining_menu_user) {
        	mBtnOrder.setVisibility(View.VISIBLE);
        }
        else {
        	mBtnOrder.setVisibility(View.INVISIBLE);
        }

        if (category.isInit) {
            if (category.mPages > 0 && category.mItemCounts > 0) {
                mPageNumView.setSumPageNum(String.valueOf(category.mPages));
                refreshPageNumView();
            } else {
                mPageNumView.setSumPageNum(String.valueOf(category.mPages));
            }
            Log.d(TAG, "counts " + category.mItemCounts);
        } else {
            mPageNumView.setSumPageNum(String.valueOf(category.mPages));
        }
    }

    private void refreshPageNumView() {
        int cPageIndex = mPagerDataHelper.getCurrentCate().mCurse;
        mPageNumView.setCurPageNum(String.valueOf(cPageIndex + 1));
        if (mPageNumView.getVisibility() != View.VISIBLE) {
            mPageNumView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNextPage(int cPageIndex) {
        Log.i(TAG, "onNextPage " + cPageIndex);
        if (!mViewPagerAdapter.isTheLastPage()) {
            if (mState.getScrollState() == 0) {
                int focusPos = mState.getFocusPosInPage();
                int i = focusPos / 3;
                mState.setFocusPosInPage(i * 3);
                mViewPager.setCurrentItem(cPageIndex + 1, true);
            }
        }
    }

    @Override
    public void onBeforePage(int cPageIndex) {
        Log.i(TAG, "onBeforePage " + cPageIndex);
        if (!mViewPagerAdapter.isTheFirstPage()) {
            if (mState.getScrollState() == 0) {
                int focusPos = mState.getFocusPosInPage();
                int i = focusPos / 3;
                mState.setFocusPosInPage(i * 3 + 2);
                mViewPager.setCurrentItem(cPageIndex - 1, true);
            }
        }
    }

    private View.OnClickListener mNaviItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String itemId = String.valueOf(v.getTag());
            Log.i(TAG, "view id:" + v.getId());
            if (mViewPagerAdapter != null) {
                isNeedNotifyUpdate = mViewPagerAdapter.switchCategoryByName(itemId);
            }
        }
    };

    private View.OnClickListener pageClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.page_next_view:
                    onNextPage(mViewPager.getCurrentItem());
                    break;
                case R.id.page_preview_view:
                    onBeforePage(mViewPager.getCurrentItem());
                    break;
            }
        }
    };

    private View.OnHoverListener pageHoverListener = new View.OnHoverListener() {
        @Override
        public boolean onHover(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_HOVER_ENTER:
                case MotionEvent.ACTION_HOVER_EXIT:
                    mViewPager.refreshShadow();
                    break;
            }
            return false;
        }
    };

    private void refreshPageViewState() {
        if (!mViewPagerAdapter.isTheFirstPage()) {
            previewPageView.setVisibility(View.VISIBLE);
        }
        if (!mViewPagerAdapter.isTheLastPage()) {
            nextPageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFocusToTop() {
        mCurrentMenuItem.requestFocus();
    }

    @Override
    public void onItemFocusChange(int cPageIndex, int position) {
        mState.setFocusPosInPage(position);
    }

    @Override
    public void onItemClick(Object object) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUpdateCntReciever);
    }

    private BroadcastReceiver mUpdateCntReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if(intent.getAction().equals(ACTION_UI_RECORD_CNT)) {
        		mHistoryMenuItem.updateNum(intent.getExtras().getInt("recordCnt"));
        	}
        }
    };

    private SpannableStringBuilder getSizeStyle(String str) {
        SpannableStringBuilder style= new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(0xFFFFCC66), 0, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return style;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
/*
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (!mViewPagerAdapter.isTheFirstPage()) {
                    if (mState.getScrollState() == 0) {
                        int cPageIndex = mViewPager.getCurrentItem();
                        mViewPager.setCurrentItem(cPageIndex - 1, true);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (!mViewPagerAdapter.isTheLastPage()) {
                    if (mState.getScrollState() == 0) {
                        int cPageIndex = mViewPager.getCurrentItem();
                        mViewPager.setCurrentItem(cPageIndex + 1, true);
                    }
                }
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }
*/

	@Override
	public void onFocusToBottom() {
		// TODO Auto-generated method stub
		if(mBtnOrder.getVisibility() == View.VISIBLE) {
			mBtnOrder.requestFocus();
		}		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {		
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			UserHistoryPageFragment.isRequest = false;
			UserHistoryPageFragment.sortedList = null;
			return super.onKeyDown(keyCode, event);
		}
		
		return false ;
	}
}

package com.cookingshow.all;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cookingshow.BaseMainFragment;
import com.cookingshow.R;
import com.cookingshow.adapter.MultiRecyclePageAdapter;
import com.cookingshow.category.AllTypeCategory;
import com.cookingshow.category.CategoryInfo;
import com.cookingshow.datacenter.MenuItem;
import com.cookingshow.datacenter.NavigationDataProvider;
import com.cookingshow.navigation.NavigationBar;
import com.cookingshow.navigation.NavigationData;
import com.cookingshow.navigation.NavigationItemView;
import com.cookingshow.page.ActionEventMgr;
import com.cookingshow.page.IPageNotifyListener;
import com.cookingshow.page.PagerDataHelper;
import com.cookingshow.page.ViewPagerState;
import com.cookingshow.view.CustomViewPager;
import com.cookingshow.view.ItemCursorView;

public class AllTypeFragment extends BaseMainFragment implements
        IPageNotifyListener, PagerDataHelper.OnPageCategoryChangedListener {

    private static final String TAG = "AllTypeFragment";
    private View previewPageView;
    private View nextPageView;
    private MenuItem menuItem;
    private CustomViewPager mViewPager = null;
    private MultiRecyclePageAdapter mViewPagerAdapter = null;
    private ViewPagerState mState = null;
    private PagerDataHelper mPagerDataHelper = null;
    private boolean isPause = false;
    private boolean isLoadFinish = false;
    private View mLoadingView = null;
    private RelativeLayout mPagerParentView = null;
    private int mCurPageNum;
    private int mSumPageNum;
    private ItemCursorView mCursorView = null;
    private static final int MSG_LOAD_UI = 1;
    private static NavigationDataProvider mNavigationDataProvider;
    private NavigationItemView.OnNavigationItemFocusListener mSubNaviItemListener = null;
    private NavigationBar mSubNavBar = null;
    private List<MenuItem> mSubMenuList = null;
    private boolean isInitSubMenus = false;
    private boolean isNeedNotifyUpdate;
    private CategoryInfo mCurCategory = null;
    
    private final Handler mRefreshHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_UI:
                    initViewPagerData();
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionEventMgr.getInstance();
        mState = new ViewPagerState();
        Bundle bundle = getArguments();
        if (bundle != null) {
            menuItem = (MenuItem) bundle.getSerializable(ARGUMENT_DATA_KEY);
        }
        
        mNavigationDataProvider = new NavigationDataProvider(AllTypeFragment.this.getActivity());
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.cooking_main_fragment, container, false);
        initUI(view);
        setListener();
        loadSubMenuData();
        return view;
    }

    private void initUI(View view) {
        mViewPager = (CustomViewPager) view.findViewById(R.id.view_pager);
        previewPageView = view.findViewById(R.id.page_preview_view);
        nextPageView = view.findViewById(R.id.page_next_view);
        mPagerParentView = (RelativeLayout) view.findViewById(R.id.app_content_view);
        mLoadingView = view.findViewById(R.id.tv_loading);
        mSubNavBar = (NavigationBar) view.findViewById(R.id.sub_navigation);
        mCursorView = (ItemCursorView) view.findViewById(R.id.cursor_view);

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageMargin(-30);
        mViewPager.setParentView(mPagerParentView);
        //view.findViewById(R.id.progress_text).requestFocus();
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
                mViewPagerAdapter.setSelectedPos(position);
                refreshPageNumView();
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
                ActionEventMgr.getInstance().notifyOnScrollState(mState);
                if (state == 0) {
                    ActionEventMgr.getInstance().notifyOnKeyMode(mState);
                    refreshPageViewState();
                    mCursorView.setVisibility(View.VISIBLE);
                }
            }
        });

        mSubNaviItemListener = new NavigationItemView.OnNavigationItemFocusListener() {
            @Override
            public boolean onFocusDown() {
                ActionEventMgr.getInstance().notifyOnKeyMode(mState);
                return true;
            }

            @Override
            public boolean onFocusUp() {
                if (mFragmentActionListener != null) {
                    mFragmentActionListener.onFocusUpAction();
                }
                return true;
            }
        };

        mViewPager.setFocusable(false);
        nextPageView.setOnClickListener(pageClickListener);
        previewPageView.setOnClickListener(pageClickListener);
        nextPageView.setOnHoverListener(pageHoverListener);
        previewPageView.setOnHoverListener(pageHoverListener);
    }

    private void loadSubMenuData() {
    	Log.d(TAG, "loadSubMenuData");
        mLoadingView.setVisibility(View.VISIBLE);
        new LoadContentTask().execute();
    }

    private void initSubNavigation() {
        if (mSubMenuList != null && !mSubMenuList.isEmpty()) {

            List<NavigationData> datas = new ArrayList<NavigationData>();
            for (int i = 0; i < mSubMenuList.size(); i++) {
                NavigationData naviData = new NavigationData();
                naviData.title = mSubMenuList.get(i).getName();
                naviData.id = mSubMenuList.get(i).getId();
                naviData.tag = mSubMenuList.get(i);
                datas.add(naviData);
            }
            mSubNavBar.setItemOnClickListener(mSubNaviItemClickListener);
            mSubNavBar.inflateItems(datas, R.layout.sub_navigation_item, 35);
            mSubNavBar.setClickedItem(mSubMenuList.get(0).getId());
            mSubNavBar.setItemFocusListener(mSubNaviItemListener);
            mSubNavBar.setVisibility(View.VISIBLE);
            isInitSubMenus = true;
            if (isLoadFinish) {
                initViewPagerData();
            }
        } else {
            mLoadingView.setVisibility(View.INVISIBLE);
            mPagerParentView.setVisibility(View.INVISIBLE);
//            mNoDataView.setVisibility(View.VISIBLE);
            if (mFragmentActionListener != null) {
                mFragmentActionListener.onFocusUpAction();
            }
        }
    }

    private void initViewPagerData() {
        List<CategoryInfo> categoryInfoList = new ArrayList<CategoryInfo>();
        for (MenuItem menuItem : mSubMenuList) {
            CategoryInfo category = new AllTypeCategory();
            category.mCategoryName = menuItem.getCode();
            category.mOtherData = menuItem;
            categoryInfoList.add(category);
        }
        mPagerDataHelper = new PagerDataHelper(categoryInfoList);
        mPagerDataHelper.setOnCategoryChangedListener(this);
        mViewPagerAdapter = new MultiRecyclePageAdapter(getChildFragmentManager(), mPagerDataHelper);
        mViewPagerAdapter.initViewPager(mViewPager);
        mViewPagerAdapter.setPageNotifyListener(this);
        mViewPagerAdapter.setItemCurseView(mCursorView);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(mViewPagerAdapter.getInitPageIndex(), true);
        isLoadFinish = true;
        refreshPageViewState();
    }

    private void refreshPageViewState() {
        if (mViewPagerAdapter == null) {
            return;
        }
        if (!mViewPagerAdapter.isTheFirstPage() || !isFirstMenu()) {
            previewPageView.setVisibility(View.VISIBLE);
        }

        if (!mViewPagerAdapter.isTheLastPage() || !isLastMenu()) {
            nextPageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNextPage(int cPage) {
        Log.i(TAG, "onNextPage " + cPage);
        if (!mViewPagerAdapter.isTheLastPage()) {
            if (mState.getScrollState() == 0) {
                int focusPos = mState.getFocusPosInPage();
                int i = focusPos / 4;
                mState.setFocusPosInPage(i * 4);
                mViewPager.setCurrentItem(cPage + 1, true);
            }
        } else {
            switchMainNavi(false);
        }
    }

    @Override
    public void onBeforePage(int cPage) {
        Log.i(TAG, "onBeforePage " + cPage);
        if (!mViewPagerAdapter.isTheFirstPage()) {
            if (mState.getScrollState() == 0) {
                int focusPos = mState.getFocusPosInPage();
                int i = focusPos / 4;
                mState.setFocusPosInPage(i * 4 + 3);
                mViewPager.setCurrentItem(cPage - 1, true);
            }
        } else {
            switchMainNavi(true);
        }
    }

    @Override
    public void onFocusToTop() {
        mSubNavBar.requestFocusForSelected();
    }

    @Override
    public void onItemFocusChange(int cPageIndex, int position) {
        mState.setFocusPosInPage(position);
        mCurPageNum = cPageIndex;
    }

    @Override
    public void onItemClick(Object object) {

    }

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

                    break;
                case MotionEvent.ACTION_HOVER_EXIT:
                    mViewPager.refreshShadow();
                    break;
            }
            return false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (isPause) {
            mViewPager.requestLayout();
            isPause = false;
        }
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
        Log.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy");

        if (mNavigationDataProvider != null)
        {
        	mNavigationDataProvider.destroy();
        }
	}

    @Override
    public void onPageCategoryChanged(CategoryInfo category) {
        Log.i(TAG, "category: " + category.mCategoryName);
        MenuItem menuItem = (MenuItem) category.mOtherData;
        mSubNavBar.setClickedItem(mSubNavBar.findViewById(menuItem.getId()));
        if (category.isInit) {
            mSumPageNum = category.mPages;
            if (mFragmentActionListener != null) {
                mFragmentActionListener.onProgressMaxAction(mSumPageNum);
                refreshPageNumView();
            }
        } else {
            if (mFragmentActionListener != null) {
                mFragmentActionListener.onProgressMaxAction(1);
            }
        }

        mCurCategory = category;
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    private void refreshPageNumView() {
        if (!isHidden) {
            mCurPageNum = mPagerDataHelper.getCurrentCate().mCurse;
            if (mFragmentActionListener != null) {
                mFragmentActionListener.onProgressChangeAction(mCurPageNum);
            }
        }
    }

    @Override
    protected void onAnimStart(boolean enter) {
        if (mPagerParentView != null) {
            mPagerParentView.setClipChildren(true);
            mViewPager.refreshShadow();
        }
        previewPageView.setVisibility(View.INVISIBLE);
        nextPageView.setVisibility(View.INVISIBLE);
        Log.d(TAG, "onAnimStart");
    }

    @Override
    protected void onAnimEnd(boolean enter) {
        if (enter) {
            if (mPagerParentView != null) {
                mPagerParentView.setClipChildren(false);
                mViewPager.refreshShadow();
            }
            if (!isLoadFinish) {
                if (isInitSubMenus) {
                    mRefreshHandler.sendEmptyMessageDelayed(MSG_LOAD_UI, 50);
                }
                isLoadFinish = true;
            } else {
                refreshPageViewState();
                if (mFragmentActionListener != null) {
                    mFragmentActionListener.onProgressMaxAction(mSumPageNum);
                    mFragmentActionListener.onProgressChangeAction(mCurPageNum);
                }
            }
        }
        Log.d(TAG, "onAnimEnd");
    }

    @Override
    public void onHiddenForFragment() {
        if (mViewPagerAdapter != null) {
            mViewPagerAdapter.onHiddenForFragment(true);
        }
    }

    @Override
    public void onDisplayForFragment() {
        if (mViewPagerAdapter != null) {
            mViewPagerAdapter.onHiddenForFragment(false);
        }
        ActionEventMgr.getInstance().notifyOnKeyMode(mState);
    }

    @Override
    public void onNaviFocusDown() {
        if (mSubNavBar != null) {
            mSubNavBar.requestFocusForSelected();
        }
    }

    @Override
    public void onFlipperNextPage() {
        if (mViewPagerAdapter != null) {
            if (!mViewPagerAdapter.isTheLastPage()) {
                if (mState.getScrollState() == 0) {
                    int cPage = mViewPager.getCurrentItem();
                    mViewPager.setCurrentItem(cPage + 1, true);
                }
            } else {
                switchMainNavi(false);
            }
        }
    }

    @Override
    public void onFlipperBeforePage() {
        if (mViewPagerAdapter != null) {
            if (!mViewPagerAdapter.isTheFirstPage()) {
                if (mState.getScrollState() == 0) {
                    int cPage = mViewPager.getCurrentItem();
                    mViewPager.setCurrentItem(cPage - 1, true);
                }
            } else {
                switchMainNavi(true);
            }
        }
    }

    private View.OnClickListener mSubNaviItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	MenuItem menuItem = (MenuItem) v.getTag();
            Log.i(TAG, "view id:" + v.getId() + " menuItem: " + menuItem.toString());
            if (mViewPagerAdapter != null) {
                isNeedNotifyUpdate = mViewPagerAdapter.switchCategoryByName(menuItem.getCode());
            }
        }
    };

    protected class LoadContentTask extends AsyncTask<String, Void, Boolean> {

        public LoadContentTask() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                if (mSubMenuList == null) {
                	if(mNavigationDataProvider != null) {
                		mSubMenuList = mNavigationDataProvider.getSubMenuItem();
                	}
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            initSubNavigation();
        }
    }

	@Override
	public void onFocusToBottom() {
		// TODO Auto-generated method stub
		
	}
}

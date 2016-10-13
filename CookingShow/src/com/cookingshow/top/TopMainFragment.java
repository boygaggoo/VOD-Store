package com.cookingshow.top;

import java.util.ArrayList;
import java.util.List;

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
import com.cookingshow.adapter.SinglePageAdapter;
import com.cookingshow.category.CategoryInfo;
import com.cookingshow.category.TopCategory;
import com.cookingshow.datacenter.MenuItem;
import com.cookingshow.datacenter.PageContent;
import com.cookingshow.datacenter.TopDishProvider;
import com.cookingshow.page.ActionEventMgr;
import com.cookingshow.page.IPageNotifyListener;
import com.cookingshow.page.PagerDataHelper;
import com.cookingshow.page.ViewPagerState;
import com.cookingshow.view.CustomViewPager;
import com.cookingshow.view.ItemCursorView;

public class TopMainFragment extends BaseMainFragment implements
        IPageNotifyListener, PagerDataHelper.OnPageCategoryChangedListener {

    private static final String TAG = "TopMainFragment";
    private MenuItem menuItem;
    private CustomViewPager mViewPager = null;
    private SinglePageAdapter mViewPagerAdapter = null;
    private ViewPagerState mState = null;
    private PagerDataHelper mPagerDataHelper = null;
    private View previewPageView;
    private View nextPageView;
    private boolean isPause = false;
    private boolean isLoadFinish = false;
    private View mLoadingView = null;
    private RelativeLayout mPagerParentView = null;
    private int mCurPageNum;
    private int mSumPageNum;
    private ItemCursorView mCursorView = null;
    private static final int MSG_LOAD_UI = 1;
    private static TopDishProvider mTopDishDataProvider;

    private final Handler mRefreshHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_UI:
                	processData();
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTopDishDataProvider = new TopDishProvider(TopMainFragment.this.getActivity().getApplicationContext(), mRefreshHandler);

        ActionEventMgr.getInstance();
        mState = new ViewPagerState();
        Bundle bundle = getArguments();
        
        if (bundle != null) {
            menuItem = (MenuItem) bundle.getSerializable(ARGUMENT_DATA_KEY);
            pageName = menuItem.getCode();
        }
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_main_fragment, container, false);
        initUI(view);
        setListener();
        Log.d(TAG, "onCreateView");
        return view;
    }

    private void initUI(View view) {
        mViewPager = (CustomViewPager) view.findViewById(R.id.view_pager);
        previewPageView = view.findViewById(R.id.page_preview_view);
        nextPageView = view.findViewById(R.id.page_next_view);
        mLoadingView = view.findViewById(R.id.tv_loading);
        mPagerParentView = (RelativeLayout) view.findViewById(R.id.app_content_view);
        mCursorView = (ItemCursorView) view.findViewById(R.id.cursor_view);

        mViewPager.setParentView(mPagerParentView);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageMargin(-10);
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
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    previewPageView.setVisibility(View.INVISIBLE);
                    nextPageView.setVisibility(View.INVISIBLE);
                    mCursorView.setVisibility(View.INVISIBLE);
                }
                mState.setScrollState(state);
                ActionEventMgr.getInstance().notifyOnScrollState(mState);
                if (state == 0) {
                    ActionEventMgr.getInstance().notifyOnKeyMode(mState);
                    refreshPageViewState();
                    mCursorView.setVisibility(View.VISIBLE);
                }
            }
        });
        mViewPager.setFocusable(false);
        nextPageView.setOnClickListener(pageClickListener);
        previewPageView.setOnClickListener(pageClickListener);
        nextPageView.setOnHoverListener(pageHoverListener);
        previewPageView.setOnHoverListener(pageHoverListener);
    }

    private void processData() {
        List<CategoryInfo> categoryInfoList = new ArrayList<CategoryInfo>();
        TopCategory category = new TopCategory();
        category.mCategoryName = menuItem.getName();
        category.initPageInfos(getPageContent());
        categoryInfoList.add(category);
        mPagerDataHelper = new PagerDataHelper(categoryInfoList);
        mPagerDataHelper.setOnCategoryChangedListener(this);
        mViewPagerAdapter = new SinglePageAdapter(getChildFragmentManager(), mPagerDataHelper);
        mViewPagerAdapter.setPageNotifyListener(this);
        mViewPagerAdapter.setItemCurseView(mCursorView);
        mViewPager.setAdapter(mViewPagerAdapter);
        onPageCategoryChanged(category);
        refreshPageViewState();
    }

    private List<PageContent> getPageContent() {  	
        return mTopDishDataProvider.getTopDishData();
    }

    @Override
    public void onNextPage(int cPage) {
        Log.i(TAG, "onNextPage " + cPage);
        if (!mViewPagerAdapter.isTheLastPage()) {
            if (mState.getScrollState() == 0) {
                int focusPos = mState.getFocusPosInPage();
                int i = focusPos / 3;
                mState.setFocusPosInPage(i * 3);
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
                int i = focusPos / 3;
                mState.setFocusPosInPage(i * 3 + 2);
                mViewPager.setCurrentItem(cPage - 1, true);
            }
        } else {
            switchMainNavi(true);
        }
    }

    @Override
    public void onFocusToTop() {
        if (mFragmentActionListener != null) {
            mFragmentActionListener.onFocusUpAction();
        }
    }

    @Override
    public void onItemFocusChange(int cPageNum, int position) {
        Log.d(TAG, "cPage " + cPageNum + " pos " + position);
        mState.setFocusPosInPage(position);
        mCurPageNum = cPageNum;
    }

    @Override
    public void onItemClick(Object object) {

    }

    private void refreshPageViewState() {
        if (!mViewPagerAdapter.isTheFirstPage() || !isFirstMenu()) {
            previewPageView.setVisibility(View.VISIBLE);
        }

        if (!mViewPagerAdapter.isTheLastPage() || !isLastMenu()) {
            nextPageView.setVisibility(View.VISIBLE);
        }
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
            isPause = false;
            mViewPager.requestLayout();
        }
        Log.i(TAG, "end");
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
        Log.i(TAG, "end");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "end");
    }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(mTopDishDataProvider != null) {
			mTopDishDataProvider.destroy();
		}
	}

    @Override
    public void onPageCategoryChanged(CategoryInfo category) {
        Log.i(TAG, "category: " + category.mCategoryName);
        if (category.isInit) {
            mSumPageNum = category.mPages;
            if (mFragmentActionListener != null) {
                mFragmentActionListener.onProgressMaxAction(mSumPageNum);
                refreshPageNumView();
            }
        } else {
            if (mFragmentActionListener != null) {
                mFragmentActionListener.onProgressMaxAction(0);
            }
        }
        mLoadingView.setVisibility(View.GONE);
        isLoadFinish = true;
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
        Log.d(TAG, "end");
    }

    @Override
    protected void onAnimEnd(boolean enter) {
        if (enter) {
            if (!isLoadFinish) {
            	mRefreshHandler.sendEmptyMessageDelayed(MSG_LOAD_UI, 0);
            } else {
                refreshPageViewState();
                if (mFragmentActionListener != null) {
                    mFragmentActionListener.onProgressMaxAction(mSumPageNum);
                    mFragmentActionListener.onProgressChangeAction(mCurPageNum);
                }
            }
            if (mPagerParentView != null) {
                mPagerParentView.setClipChildren(false);
                mViewPager.refreshShadow();
            }
        }
        Log.d(TAG, "end");
    }
    
    @Override
    public void onHiddenForFragment() {
        Log.d(TAG, "start");
        if (mViewPagerAdapter != null) {
            mViewPagerAdapter.onHiddenForFragment(true);
        }
    }

    @Override
    public void onDisplayForFragment() {
        Log.d(TAG, "start");
        if (mViewPagerAdapter != null) {
            mViewPagerAdapter.onHiddenForFragment(false);
        }
        ActionEventMgr.getInstance().notifyOnKeyMode(mState);
    }

    @Override
    public void onNaviFocusDown() {
        ActionEventMgr.getInstance().notifyOnKeyMode(mState);
    }

    @Override
    public void onFlipperNextPage() {
        if (!mViewPagerAdapter.isTheLastPage()) {
            int cPage = mViewPager.getCurrentItem();
            if (mState.getScrollState() == 0) {
                mViewPager.setCurrentItem(cPage + 1, true);
            }
        } else {
            switchMainNavi(false);
        }
    }

    @Override
    public void onFlipperBeforePage() {
        if (!mViewPagerAdapter.isTheFirstPage()) {
            if (mState.getScrollState() == 0) {
                int cPage = mViewPager.getCurrentItem();
                mViewPager.setCurrentItem(cPage - 1, true);
            }
        } else {
            switchMainNavi(true);
        }
    }

	@Override
	public void onFocusToBottom() {
		// TODO Auto-generated method stub
		
	}

}

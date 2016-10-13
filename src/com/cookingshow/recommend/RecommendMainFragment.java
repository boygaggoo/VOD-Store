package com.cookingshow.recommend;

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
import com.cookingshow.MainActivity;
import com.cookingshow.R;
import com.cookingshow.adapter.SinglePageAdapter;
import com.cookingshow.category.CategoryInfo;
import com.cookingshow.category.RecommendCategory;
import com.cookingshow.datacenter.MenuItem;
import com.cookingshow.datacenter.PageContent;
import com.cookingshow.datacenter.RecommendDishProvider;
import com.cookingshow.page.ActionEventMgr;
import com.cookingshow.page.IPageNotifyListener;
import com.cookingshow.page.PagerDataHelper;
import com.cookingshow.page.ViewPagerState;
import com.cookingshow.view.CustomViewPager;
import com.cookingshow.view.ItemCursorView;

public class RecommendMainFragment extends BaseMainFragment implements
        IPageNotifyListener, PagerDataHelper.OnPageCategoryChangedListener{
    private static final String TAG = "RecommendMainFragment";
    private MenuItem menuItem;
    private boolean isGetData = false;
    private boolean isLoadFinish = false;
    private CustomViewPager mViewPager = null;
    private SinglePageAdapter mViewPagerAdapter = null;
    private View previewPageView;
    private View nextPageView;
    private RelativeLayout mPagerParentView = null;
    private ItemCursorView mCursorView = null;
    private static final int MSG_LOAD_UI = 1;
    private ViewPagerState mState = null;
    private PagerDataHelper mPagerDataHelper = null;
    private int mCurPageNum;
    private int mSumPageNum;
    private boolean isPause = false;
    private static RecommendDishProvider mRecommendDishDataProvider;

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

        ActionEventMgr.getInstance();
        mState = new ViewPagerState();
        
        Bundle bundle = getArguments();
        if (bundle != null) {
            menuItem = (MenuItem) bundle.getSerializable(ARGUMENT_DATA_KEY);
            pageName = menuItem.getCode();
        }
        
        mRecommendDishDataProvider = new RecommendDishProvider(RecommendMainFragment.this.getActivity().getApplicationContext(), mRefreshHandler);
        Log.d(TAG, "onCreate");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommend_main_fragment, container, false);
        initUI(view);
        setListener();
        Log.d(TAG, "onCreateView");
        return view;
    }

    private void initUI(View view) {
        mViewPager = (CustomViewPager) view.findViewById(R.id.view_pager);
        previewPageView = view.findViewById(R.id.page_preview_view);
        nextPageView = view.findViewById(R.id.page_next_view);
        mPagerParentView = (RelativeLayout) view.findViewById(R.id.app_content_view);
        mCursorView = (ItemCursorView) view.findViewById(R.id.cursor_view);

        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageMargin(-6);
        mViewPager.setParentView(mPagerParentView);
    }

    private void setListener() {

        mViewPager.setCustomOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            	//Toast.makeText(getActivity(), "scroll", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected " + position);
                mState.setCurPageIndex(position);
                mViewPagerAdapter.setSelectedPos(position);
                refreshPageView();
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
                    mCursorView.setVisibility(View.VISIBLE);
                    refreshPageViewState();
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
        RecommendCategory category = new RecommendCategory();
        category.mCategoryName = menuItem.getName();
        category.initPageInfos(getPageContent());
        categoryInfoList.add(category);
        mPagerDataHelper = new PagerDataHelper(categoryInfoList);
        mPagerDataHelper.setOnCategoryChangedListener(this);
        mViewPagerAdapter = new SinglePageAdapter(getChildFragmentManager(), mPagerDataHelper);
        mViewPagerAdapter.setPageNotifyListener(this);
        mViewPagerAdapter.setItemCurseView(mCursorView);
        mViewPager.setAdapter(mViewPagerAdapter);
        refreshPageViewState();
    }
    
    private List<PageContent> getPageContent() {  
    	List<PageContent> recommendDatas = mRecommendDishDataProvider.getRecommendDishData();
    	
    	if(recommendDatas.size() > 0) {
    		isGetData = true;
    	}
    	
        return recommendDatas;
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
    
    private void refreshPageViewState() {
        if (!mViewPagerAdapter.isTheFirstPage() || !isFirstMenu()) {
            previewPageView.setVisibility(View.VISIBLE);
        }

        if (!mViewPagerAdapter.isTheLastPage() || !isLastMenu()) {
            nextPageView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (isPause) {
            isPause = false;
            mViewPager.requestLayout();
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
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }
 
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(mRecommendDishDataProvider != null) {
			mRecommendDishDataProvider.destroy();
		}
	}

    @Override
    public void onPageCategoryChanged(CategoryInfo category) {
        Log.i(TAG, "category: " + category.mCategoryName);
        if (category.isInit) {
            mSumPageNum = category.mPages;
            if (mFragmentActionListener != null) {
                mFragmentActionListener.onProgressMaxAction(category.mPages);
                refreshPageView();
            }
        } else {
            if (mFragmentActionListener != null) {
                mFragmentActionListener.onProgressMaxAction(0);
            }
        }
        
        isLoadFinish = true;
        ((MainActivity)getActivity()).loadContentFinished();
    }

    private void refreshPageView() {
        if (!isHidden) {
            mCurPageNum = mPagerDataHelper.getCurrentCate().mCurse;
            if (mFragmentActionListener != null) {
                mFragmentActionListener.
                        onProgressChangeAction(mCurPageNum);
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
            if (!isLoadFinish || !isGetData) {
            	mRefreshHandler.sendEmptyMessageDelayed(MSG_LOAD_UI, 50);
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
            ActionEventMgr.getInstance().notifyOnKeyMode(mState);
        }
    }

    @Override
    public void onNaviFocusDown() {
        ActionEventMgr.getInstance().notifyOnKeyMode(mState);
    }

    @Override
    public void onFlipperNextPage() {
        if (mViewPagerAdapter == null) {
            return;
        }
        if (!mViewPagerAdapter.isTheLastPage()) {
            if (mState.getScrollState() == 0) {
                int cPage = mViewPager.getCurrentItem();
                mViewPager.setCurrentItem(cPage + 1, true);
            }
        } else {
            switchMainNavi(false);
        }
    }

    @Override
    public void onFlipperBeforePage() {
        if (mViewPagerAdapter == null) {
            return;
        }
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

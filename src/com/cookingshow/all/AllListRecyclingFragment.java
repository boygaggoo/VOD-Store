package com.cookingshow.all;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cookingshow.LauncherFragment;
import com.cookingshow.R;
import com.cookingshow.adapter.AppListAdapter;
import com.cookingshow.category.PageInfo;
import com.cookingshow.datacenter.AppInfo;
import com.cookingshow.datacenter.AppListResponse;
import com.cookingshow.datacenter.DishDataInfo;
import com.cookingshow.datacenter.DishDataProvider;
import com.cookingshow.datacenter.MenuItem;
import com.cookingshow.page.CommonFragment;
import com.cookingshow.page.ViewPagerState;
import com.cookingshow.view.AppGridLayout;
import com.cookingshow.view.AppListView;

public class AllListRecyclingFragment extends CommonFragment implements
        AppGridLayout.ItemOnBoundaryListener{

    private static final String TAG = "AppListRecyclingFragment";
    private List<DishDataInfo> mAppData = null;
    private AppListView mListView = null;
    private int mItemCount = 0;
    private AppListResponse applistResponse;
    private MenuItem mSubMenu = null;
    private int mPageNum;
    private ItemOnFocusChangeListener mItemFocusChangeListener = null;
    private ViewPagerState mState = null;
    private boolean isLoadSuccess = false;
    private PageInfo mPageInfo = null;
    private static final int MSG_REFRESH_IMAGE = 1;
    private static final int MSG_REFRESH_UI = 2;
    private int mCurrentPageIndex;
    private TextView mNoDataView = null;
    private int mFocusIndex = 0;
    private AppListAdapter mAdapter = null;
    private ArrayList<View> mSiteView = new ArrayList<View>();
    private ViewGroup mContentView = null;
    private boolean isNeedRefreshUi = false;
    private static DishDataProvider mDishDataProvider;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_IMAGE:
                    ArrayList<Integer> poss = (ArrayList<Integer>) msg.obj;
                    mAdapter.loadImageValue(poss);
                    break;
                case MSG_REFRESH_UI:
                    String name = msg.obj.toString();
                    int si = msg.arg1;
                    updateUiAfterLoad(name, si);
                    break;
            }
            return false;
        }
    });


    public AllListRecyclingFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDishDataProvider = new DishDataProvider(AllListRecyclingFragment.this.getActivity().getApplicationContext());
        
        mItemFocusChangeListener = new ItemOnFocusChangeListener();
        mAppData = new ArrayList<DishDataInfo>();
        mPageIndex = getArguments().getInt("pageIndex", -1);
        mCurrentPageIndex = getArguments().getInt("currentPageIndex", 0);
        mPageInfo = (PageInfo) getArguments().getSerializable("pageInfo");
        if (mPageInfo != null) {
            mPageNum = mPageInfo.mPageNum;
            mItemCount = mPageInfo.mItemCount;
            mSubMenu = (MenuItem) mPageInfo.mOtherData;
            if (mPageInfo.isGetSuccess && mPageInfo.mPageData != null) {
                mAppData = mPageInfo.mPageData;            	
                isLoadSuccess = true;
                Log.i(TAG, "get data from cache");
            }
        }
        if (!isLoadSuccess) {
            mAppData = new ArrayList<DishDataInfo>();
        }
    	
        mAdapter = new AppListAdapter(getActivity(), mAppData);
        mAdapter.setSpace(10, 25);
        mAdapter.setRowNum(2);
        mAdapter.setColumnNum(4);
        //mAdapter.setBlockSize(400, 345);
        mAdapter.setBlockSize((int)this.getResources().getDimension(R.dimen.grid_item_layout_width), (int)this.getResources().getDimension(R.dimen.grid_item_layout_height));
        Log.i(TAG, "onCreate " + mPageIndex);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView " + mPageIndex);
        mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_common_applist, container, false);
        if (mRecycleView == null) {
            mListView = new AppListView(inflater.getContext());
            mListView.setClipChildren(false);
        } else {
            Log.d(TAG, "reuse recycle view");
            mListView = (AppListView) mRecycleView;
        }
        mListView.setAdapter(mAdapter);
        mNoDataView = (TextView) mContentView.findViewById(R.id.tv_no_data_view);
        mNoDataView.setOnKeyListener(mOnNoDataKeyListener);
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setItemBoundaryListener(this);
        mListView.setVisibility(View.VISIBLE);
        if (mRecycleView == null) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(1618, -1);
            mContentView.addView(mListView, params);
        } else {
            mContentView.addView(mListView);
        }
        mRecycleView = mListView;
        if (!isLoadSuccess) {
        	Log.i(TAG, "onCreateView getDataFromDB");
        	getDataFromDB();
        } else {
        	Log.i(TAG, "onCreateView loadContent");
            loadContent();
        }
        setFocusView();
        refreshImageView();
    }

    private void loadContent() {
        if (!isLoadSuccess && mItemCount != 0) {
            loadGridLayout();
        } else {
            if (mAppData != null && !mAppData.isEmpty()) {
                loadGridLayout();
                mListView.setVisibility(View.VISIBLE);
                mNoDataView.setVisibility(View.GONE);
            } else {
                mListView.setVisibility(View.INVISIBLE);
                mNoDataView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void loadGridLayout() {
        Log.d(TAG, "mItemCount " + mItemCount);
        int cnt = mItemCount;
        if (mListView.getChildCount() > cnt) {
            cnt = mListView.getChildCount();
        }
        int w = mAdapter.getBlockWidth();
        int h = mAdapter.getBlockHeight();
        int columnNum = mAdapter.getCloumnNum();

        int horizontalSpacing = mAdapter.getHorizontalSpacing();
        int verticalSpacing = mAdapter.getVerticalSpacing();

        for (int i = 0; i < cnt; i++) {
            View itemView = mListView.getChildAt(i);
            if (itemView == null) {
                itemView = mAdapter.createViewFromResource(i, null);
                FrameLayout.LayoutParams lyp = new FrameLayout.LayoutParams(w, h);
                int row = i / columnNum;
                int clo = i % columnNum;
                int left = 0;
                int top = 0;

                if (clo > 0) {
                    left = (horizontalSpacing + w) * clo;
                }
                if (row > 0) {
                    top = (verticalSpacing + h) * row;
                }
                lyp.setMargins(left, top, 0, 0);
                mListView.addView(itemView, lyp);
            } else {
                itemView.setVisibility(View.VISIBLE);
            }
            itemView.setOnFocusChangeListener(mItemFocusChangeListener);
            if (mAppData != null && i < mAppData.size()) {
                mAdapter.createViewFromResource(i, itemView);
            } else {
                if (isLoadSuccess) {
                    itemView.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (isLoadSuccess) {
            mListView.setVisibleChildCount(mItemCount);
        }
    }

    private void updateUiAfterLoad(String categoryName, int si) {
        Log.i(TAG, "updateUiAfterLoad " + mPageIndex);
        if (!categoryName.equals(mPageInfo.mCategoryName)
                || si != mPageInfo.mStartIndex) {
            return;
        }
        int allCnt = 0;
        if (applistResponse != null) {
            isLoadSuccess = applistResponse.getIsSuccess();
            mPageInfo.isGetSuccess = isLoadSuccess;
            mAppData = applistResponse.getDishDataInfos();
            allCnt = applistResponse.getAllCount();
            if (mAppData == null || mAppData.isEmpty()) {
                mItemCount = 0;
                if (mState == null || mState.getScrollState() == 0) {
                    loadContent();
                } else {
                    isNeedRefreshUi = true;
                }
            } else {
                mAdapter.resetData(mAppData);
                mPageInfo.mPageData = mAppData;
                mItemCount = mPageInfo.mItemCount = mAppData.size();
                if (mState == null || mState.getScrollState() == 0) {
                    loadContent();
                    //setDownloadState();
                    refreshImageView();
                } else {
                    isNeedRefreshUi = true;
                }
            }
        } else {
            mItemCount = 0;
            if (mState == null || mState.getScrollState() == 0) {
                loadContent();
            } else {
                isNeedRefreshUi = true;
            }
        }
        if (mPageDataLoadListener != null) {
            mPageDataLoadListener.onPageDataLoadFinish(mPageInfo, allCnt, mPageIndex);
        }
        setFocusView();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "pos " + mPageIndex);
        super.onDestroyView();
        mAdapter.onDestroy();
        mSiteView.clear();
        mAppData = null;
        applistResponse = null;
    }

    @Override
    public boolean focusOnBoundaryTop() {
        Log.i(TAG, "focusOnBoundaryTop");
        if (mPageNotifyListener != null) {
            mPageNotifyListener.onFocusToTop();
            return true;
        }
        return false;
    }

    @Override
    public boolean focusOnBoundaryRight() {
        Log.i(TAG, "focusOnBoundaryRight");
        if (mPageNotifyListener != null) {
            mPageNotifyListener.onNextPage(mPageIndex);
            return true;
        }
        return false;
    }

    @Override
    public boolean focusOnBoundaryBottom() {
        return false;
    }

    @Override
    public boolean focusOnBoundaryLeft() {
        Log.i(TAG, "focusOnBoundaryLeft");
        if (mPageNotifyListener != null) {
            mPageNotifyListener.onBeforePage(mPageIndex);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(mDishDataProvider != null) {
			mDishDataProvider.destroy();
		}
	}
	
    @Override
    public void onKeyMode(ViewPagerState state) {
        if (state != null) {
            this.mState = state;
            mFocusIndex = state.getFocusPosInPage();
            mCurrentPageIndex = state.getCurPageIndex();
            Log.d(TAG, "current pageIndex " + mCurrentPageIndex + " mPageIndex " + this.mPageIndex);
        }
        setFocusView();
    }

    private void setFocusView() {
        if (mCurrentPageIndex == this.mPageIndex && !isHidden) {
            if (mItemCount > 0) {
                mFocusIndex = (mFocusIndex >= mItemCount) ? 0 : mFocusIndex;
                if (mListView.getChildAt(mFocusIndex) != null) {
                    mListView.getChildAt(mFocusIndex).requestFocus();
                }
                Log.d(TAG, "child request focus " + mFocusIndex);
            } else {
                mNoDataView.requestFocus();
            }
        }
    }

    @Override
    public void onAirMouseMode(ViewPagerState state) {
        this.mState = state;

    }

    @Override
    public void onPageScrollStateChanged(ViewPagerState state) {
        this.mState = state;
        Log.i(TAG, "state: " + state.toString());
        if (state.getScrollState() == 0) {
            mCurrentPageIndex = state.getCurPageIndex();
            if (isNeedRefreshUi) {
                isNeedRefreshUi = false;
                loadContent();
            }
            refreshImageView();
        } else {
            mHandler.removeMessages(MSG_REFRESH_IMAGE);
        }

    }

    private void refreshImageView() {
        ArrayList<Integer> poss = new ArrayList<Integer>();
        int all = mAdapter.getCount();
        int delay = 600;
        for (int i = 0; i < all; i++) {
            if (mPageIndex == mCurrentPageIndex) {
                poss.add(i);
            } else if (mPageIndex == (mCurrentPageIndex -1)) {
                if ((i + 1) % 6 == 0) {
                    poss.add(i);
                    mSiteView.add(mListView.getChildAt(i));
                }
            } else if (mPageIndex == (mCurrentPageIndex + 1)) {
                if (i % 6 == 0) {
                    poss.add(i);
                    mSiteView.add(mListView.getChildAt(i));
                }
            }
        }
        Message msg = Message.obtain(mHandler, MSG_REFRESH_IMAGE);
        msg.obj = poss;
        if (mPageIndex == mCurrentPageIndex) {
            clearStateForSiteView();
            mHandler.sendMessage(msg);
        } else {
            setStateForSiteView();
            mHandler.sendMessageDelayed(msg, delay);
        }
    }

    private void setStateForSiteView() {
        for (View view : mSiteView) {
            if (view != null) {
                view.setClickable(false);
                view.setEnabled(false);
            }
        }
    }

    private void clearStateForSiteView() {
        for (View view : mSiteView) {
            if (view != null) {
                view.setClickable(true);
                view.setEnabled(true);
            }
        }
        mSiteView.clear();
    }

    protected class LoadContentTask extends AsyncTask<String, Void, Boolean> {
        private String mCategoryName;
        private int si;

        public LoadContentTask(String categoryName, int si) {
            this.mCategoryName = categoryName;
            this.si = si;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                if (mSubMenu != null) {
                    Log.i(TAG, "getAppListFromDb " + mCategoryName + " " + si + ";" + mItemCount);
                    applistResponse = mDishDataProvider.getAppListFromDb(si, mItemCount, mSubMenu.getCode());
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Message msg = mHandler.obtainMessage(MSG_REFRESH_UI);
            msg.obj = mCategoryName;
            msg.arg1 = si;
            mHandler.sendMessageDelayed(msg, 30);
        }

    }

    private void getDataFromDB() {
        new LoadContentTask(mPageInfo.mCategoryName, mPageInfo.mStartIndex).execute();
    }

    public PageInfo getPageInfo() {
        return mPageInfo;
    }

    class ItemOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int focusPos = Integer.valueOf(v.getTag().toString());
            if (hasFocus) {
                if (v.getTag() != null && mPageNotifyListener != null) {
                    mFocusIndex = focusPos;
                    mPageNotifyListener.onItemFocusChange(mPageNum, mFocusIndex);
                }
                mCursorView.setFocusView(mAdapter.getNeedFocusView(focusPos));
            } else {
                mCursorView.setUnFocusWithSameFocusView(mAdapter.getNeedFocusView(focusPos));
            }
        }
    }

    private View.OnKeyListener mOnNoDataKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            int action = event.getAction();
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (action == KeyEvent.ACTION_DOWN) {
                        mPageNotifyListener.onFocusToTop();
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (action == KeyEvent.ACTION_UP) {
                        mPageNotifyListener.onBeforePage(mPageIndex);
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (action == KeyEvent.ACTION_UP) {
                        mPageNotifyListener.onNextPage(mPageIndex);
                    }
                    return true;
            }
            return false;
        }
    };

    public void clearAllValues() {
        mAppData = new ArrayList<DishDataInfo>();
        mPageInfo = null;
        int cnt = mListView.getChildCount();
        for (int i = 0; i < cnt; i++) {
            //View view = mGridLayout.getChildAt(i);
            //setChildViewValue(view, i);
        }
    }

    @Override
    public void hideAllWidget() {
        clearAllValues();
        mListView.setVisibility(View.GONE);
    }

    @Override
    public void refreshPageData(PageInfo pageInfo) {
    	Log.i(TAG, "refreshPageData");
        mAdapter.onClear();
        isLoadSuccess = false;
        if (mPageInfo != null) {
            this.mPageInfo = pageInfo;
            mPageNum = mPageInfo.mPageNum;
            mItemCount = mPageInfo.mItemCount;
            mSubMenu = (MenuItem) mPageInfo.mOtherData;
            if (mPageInfo.isGetSuccess && mPageInfo.mPageData != null) {
                mAppData = mPageInfo.mPageData;
                mAdapter.resetData(mAppData);
                isLoadSuccess = true;
                loadContent();
                Log.i(TAG, "get data from cache");
            } else {
                mAppData = new ArrayList<DishDataInfo>();
                mAdapter.resetData(mAppData);
                getDataFromDB();
            }
        }
        mListView.setVisibility(View.VISIBLE);
    }

    @Override
    public View getRecycleView() {
        mAdapter.onClear();
        clearStateForSiteView();
        mContentView.removeView(mRecycleView);
        return mRecycleView;
    }
}
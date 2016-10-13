package com.cookingshow.userinfo;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cookingshow.R;
import com.cookingshow.adapter.AppListAdapter2;
import com.cookingshow.category.PageInfo;
import com.cookingshow.datacenter.DishDataInfo;
import com.cookingshow.datacenter.ViewRecordProvider;
import com.cookingshow.network.cache.ACache;
import com.cookingshow.page.CommonFragment;
import com.cookingshow.page.ViewPagerState;
import com.cookingshow.push.PushAlbumDetailActivity;
import com.cookingshow.push.PushAppDetailActivity;
import com.cookingshow.view.AppGridLayout;
import com.cookingshow.view.AppListView;

public class UserHistoryPageFragment extends CommonFragment implements
        AppGridLayout.ItemOnBoundaryListener{

    private static final String TAG = "UserHistoryPageFragment";
    private List<DishDataInfo> mAppData = null;
    private AppListView mListView = null;
    private int mStartIndex = -1;
    private int mItemCount = 0;
    private int mPageNum;
    private ItemOnFocusChangeListener mItemFocusChangeListener = null;
    private ItemOnClickListener mItemOnClickListener = null;
    private ViewPagerState mState = null;
    private TextView mNoDataView = null;
    private View mLoadingView = null;

    private PageInfo mPageInfo = null;
    private static final int MSG_REFRESH_IMAGE = 1;
    private static final int MSG_REFRESH_UI = 2;
    private int mCurrentPageIndex;
    private BroadcastReceiver mReceiver;
    private int mAllCount;
    private static final int ITEMS_PER_PAGE = 9;
    private int mFocusIndex = 0;
    private ArrayList<View> mSiteView = new ArrayList<View>();
    private AppListAdapter2 mAdapter = null;
    private ViewGroup mContentView = null;
    private boolean isNeedRefreshUi = false;
    private ViewRecordProvider mViewRecordProvider = null;
    public static List<DishDataInfo> sortedList = null;
    private LoadContentTask task = null;
    private boolean isDataLoaded = false;
    public static boolean isRequest = false;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_IMAGE:
                    ArrayList<Integer> poss = (ArrayList<Integer>) msg.obj;
                    mAdapter.loadImageValue(poss);
                    break;
                case MSG_REFRESH_UI:
                    updateUiAfterLoad();
                    break;
                case 3:
                	loadData();
                    break;
                case 4:
                	loadContent();
                	break;
            }
            return false;
        }
    });


    public UserHistoryPageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  
        mItemFocusChangeListener = new ItemOnFocusChangeListener();
        mItemOnClickListener = new ItemOnClickListener();
        mPageIndex = getArguments().getInt("pageIndex", -1);
        mCurrentPageIndex = getArguments().getInt("currentPageIndex", 0);
        mPageInfo = (PageInfo) getArguments().getSerializable("pageInfo");
        if (mPageInfo != null) {
            mPageNum = mPageInfo.mPageNum;
            mStartIndex = mPageInfo.mStartIndex;
            if (mPageInfo.isGetSuccess && mPageInfo.mPageData != null) {
                mAppData = mPageInfo.mPageData;
                Log.i(TAG, "get data from cache " + mItemCount);
            }
        }

        // 监听广播，改变appnum
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadData();
            }
        };
        registerReceiver();
        mAppData = new ArrayList<DishDataInfo>();
        mAdapter = new AppListAdapter2(getActivity(), mAppData);
        mAdapter.setSpace(20, 50);
        mAdapter.setRowNum(3);
        mAdapter.setColumnNum(3);
        //mAdapter.setBlockSize(510, 200);
        mAdapter.setBlockSize((int)this.getResources().getDimension(R.dimen.user_app_item_layout_width), (int)this.getResources().getDimension(R.dimen.user_app_item_layout_height));
        Log.i(TAG, "onCreate " + mPageIndex);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView " + mPageIndex);
        mContentView = (ViewGroup) inflater.inflate(R.layout.user_video_page_layout, container, false);
        if (mRecycleView == null) {
            mListView = new AppListView(inflater.getContext());
            mListView.setClipChildren(false);
        } else {
            Log.d(TAG, "reuse recycle view");
            mListView = (AppListView) mRecycleView;
        }
        mListView.setAdapter(mAdapter);
        mListView.setVisibility(View.INVISIBLE);
        mLoadingView = mContentView.findViewById(R.id.tv_loading);
        mLoadingView.setVisibility(View.VISIBLE);
        mNoDataView = (TextView) mContentView.findViewById(R.id.tv_no_data_view);
        mNoDataView.setOnKeyListener(mOnNoDataKeyListener);
        mNoDataView.setText(R.string.tv_nodata);
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup rootView= (ViewGroup) view;
        rootView.setClipChildren(false);
        mListView.setItemBoundaryListener(this);
        if (mRecycleView == null) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-2, -2);
            mContentView.addView(mListView, params);
        } else {
            mContentView.addView(mListView);
        }
        mRecycleView = mListView;
        //loadData();
    }

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "onActivityCreated");

		mViewRecordProvider = new ViewRecordProvider(UserHistoryPageFragment.this.getActivity().getApplicationContext(), mHandler);
		
		if(!isRequest) {
            Intent intent = new Intent();
            intent.setAction("com.cookingshow.service.ui.request.viewrecord");
            intent.putExtra("deviceId", ACache.get(getActivity()).getAsString("deviceId"));
            getActivity().sendBroadcast(intent);
            
            isRequest = true;
		}
		else {
			loadData();
		}
	}
	
    private void loadData() {
    	if(!isDataLoaded && mViewRecordProvider != null) {
    		if(sortedList == null) {
    	    	Log.d(TAG, "get from db ");
    			sortedList = mViewRecordProvider.getViewRecordData();
    			
                Intent intent = new Intent();
                intent.setAction(UserInfoActivity.ACTION_UI_RECORD_CNT);
                intent.putExtra("recordCnt", sortedList.size());
                getActivity().sendBroadcast(intent);
    		}

            isDataLoaded = true;
            mAllCount = sortedList.size();
            task = new LoadContentTask();
            task.execute();
    	}
    }
    
    private void loadContent() {
        if (mAppData != null && !mAppData.isEmpty()) {
            loadGridLayout();
            mListView.setVisibility(View.VISIBLE);
            mNoDataView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.INVISIBLE);
            mNoDataView.setVisibility(View.VISIBLE);
        }
        mLoadingView.setVisibility(View.GONE);
        requestDefaultFocus();
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
            itemView.setOnClickListener(mItemOnClickListener);
            if (mAppData != null && i < mAppData.size()) {
                mAdapter.createViewFromResource(i, itemView);
            } else {
                itemView.setVisibility(View.INVISIBLE);
            }
        }
        mListView.setVisibleChildCount(mItemCount);
    }

    private void updateUiAfterLoad() {
        Log.d(TAG, "updateUiAfterLoad " + mPageIndex);
        if (mAppData == null) {
            mAppData = new ArrayList<DishDataInfo>();
        }
        mItemCount = mAppData.size();
        if (mPageDataLoadListener != null) {
            Log.i(TAG, mPageInfo.toString() + ";" + mAllCount);
            mPageDataLoadListener.onPageDataLoadFinish(mPageInfo, mAllCount, mPageIndex);
        }
        mAdapter.resetData(mAppData);
        if (mState == null || mState.getScrollState() == 0) {
            loadContent();
            refreshImageView();
        } else {
            isNeedRefreshUi = true;
        }
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
        //getActivity().unregisterReceiver(mReceiver);
        mSiteView.clear();
        mAdapter.onDestroy();
        mAppData = null;
        isDataLoaded = false;
        
        if(task != null) {
        	task.cancel(true);
        }
        
    	if(mViewRecordProvider != null) {
	    	mViewRecordProvider.destroy();
	    	mViewRecordProvider = null;
    	}
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
    public boolean focusOnBoundaryLeft() {
        Log.i(TAG, "focusOnBoundaryLeft");
        if (mPageNotifyListener != null) {
            mPageNotifyListener.onBeforePage(mPageIndex);
            return true;
        }
        return false;
    }

    @Override
    public boolean focusOnBoundaryTop() {
        if (mPageNotifyListener != null) {
            mPageNotifyListener.onFocusToTop();
            return true;
        }
        return false;
    }

    @Override
    public boolean focusOnBoundaryBottom() {
        return false;
    }

    @Override
    public void onKeyMode(ViewPagerState state) {
        if (state != null) {
            this.mState = state;
            mFocusIndex = state.getFocusPosInPage();
            mCurrentPageIndex = state.getCurPageIndex();
            Log.d(TAG, "current pageIndex " + mCurrentPageIndex + " mPageIndex " + this.mPageIndex);
        }
        requestDefaultFocus();
    }

    private void requestDefaultFocus() {
        if (mCurrentPageIndex == this.mPageIndex) {
            if (mItemCount > 0) {
                mFocusIndex = (mFocusIndex >= mItemCount) ? (mItemCount - 1) : mFocusIndex;
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
                if ((i + 1) % 3 == 0) {
                    poss.add(i);
                    mSiteView.add(mListView.getChildAt(i));
                }
            } else if (mPageIndex == (mCurrentPageIndex + 1)) {
                if (i % 3 == 0) {
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

    protected class LoadContentTask extends AsyncTask<String, Integer, Boolean> {

        public LoadContentTask() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
            	if(isCancelled()) {
            		publishProgress(1);
            		return false;
            	}
            	getAppInfoData();
            	
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return true;
        }

        protected void onCancelled() {
            super.onCancelled();
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
        	if(result){
                super.onPostExecute(result);
                mHandler.sendEmptyMessageDelayed(MSG_REFRESH_UI, 100);        		
        	}
        }

        protected void onProgressUpdate(Integer... errorCode) {
        	switch (errorCode[0]) {
        	    case 1:
        		break;
        	}
        }
    }

    private void getAppInfoData() {
        Log.i(TAG, mStartIndex + "-" + (mStartIndex + ITEMS_PER_PAGE - 1));
      
        if(null != mAppData && mAppData.size() > 0) {
        	mAppData.clear();
        }

        int end = mStartIndex + ITEMS_PER_PAGE;
        if(end < mAllCount) {
        	mAppData.addAll(sortedList.subList(mStartIndex, end));
        }
        else {
        	mAppData.addAll(sortedList.subList(mStartIndex, mAllCount));
        }
    }

    public PageInfo getPageInfo() {
        return mPageInfo;
    }

    class ItemOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int pos = Integer.valueOf(v.getTag().toString());
            if (hasFocus) {
                mFocusIndex = pos;
                if (mPageNotifyListener != null) {
                    mPageNotifyListener.onItemFocusChange(mPageNum, mFocusIndex);
                }
                mCursorView.setFocusView(mAdapter.getNeedFocusView(pos));
            } else {
                mCursorView.setUnFocusWithSameFocusView(mAdapter.getNeedFocusView(pos));
            }
        }
    }

    class ItemOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                int clickPos = Integer.valueOf(v.getTag().toString());
                Log.d(TAG, "item focus " + clickPos);
                if (mAppData != null && clickPos < mAppData.size()) {
                	DishDataInfo appInfo = mAppData.get(clickPos);
                	Intent intent = new Intent(Intent.ACTION_VIEW);
                	
                	if(appInfo.getAlbumId() == 0) {
                        intent.putExtra("appId", appInfo.getDishId());
                        intent.putExtra("appName", appInfo.getTitle());
                        intent.putExtra("appUrl", appInfo.getVideoUrl());
                        intent.putExtra("uploader", appInfo.getUploader());
                        intent.putExtra("tips", appInfo.getTips());
                        intent.putExtra("material", appInfo.getMaterials());
                        
                        intent.setClass(v.getContext(), PushAppDetailActivity.class);                		
                	}
                	else {
                        intent.putExtra("albumId", appInfo.getAlbumId());
                        intent.putExtra("appId", appInfo.getDishId());
                        intent.putExtra("albumName", appInfo.getTips());
                        intent.putExtra("uploader", appInfo.getUploader());
                        
                        intent.setClass(getActivity(), PushAlbumDetailActivity.class);
                	}

                    v.getContext().startActivity(intent);
                }
            }
        }
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public void clearAllValues() {
        mAppData = new ArrayList<DishDataInfo>();
        mPageInfo = null;
    }

    @Override
    public void hideAllWidget() {
        clearAllValues();
        mListView.setVisibility(View.GONE);
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

    @Override
    public View getRecycleView() {
        mAdapter.onClear();
        clearStateForSiteView();
        mContentView.removeView(mRecycleView);
        return mRecycleView;
    }
}

package com.cookingshow.recommend;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.cookingshow.R;
import com.cookingshow.category.PageInfo;
import com.cookingshow.datacenter.PageContent;
import com.cookingshow.network.controller.CommonImageLoader;
import com.cookingshow.page.CommonFragment;
import com.cookingshow.page.DisplayImageMgr;
import com.cookingshow.page.ViewPagerState;
import com.cookingshow.push.PushAlbumDetailActivity;
import com.cookingshow.push.PushAppDetailActivity;
import com.cookingshow.view.AdItemLayout;
import com.cookingshow.view.AppGridLayout;

public class SecondPageFragment extends CommonFragment implements
        AppGridLayout.ItemOnBoundaryListener {

    protected static final String TAG = "SecondPageFragmentV4";
    private static final String LOCATION = "http://182.92.198.90";
    private AppGridLayout mGridLayout = null;
    private int mStartIndex = -1;
    private int mItemCount = -1;
    private int mPageNum;
    private ItemOnFocusChangeListener mItemFocusChangeListener = null;
    private ItemOnClickListener mItemOnClickListener = null;
    private List<ImageView> mLoadImageList = new ArrayList<ImageView>();
    private ViewPagerState mState = null;
    private PageInfo mPageInfo = null;
    private static final int MSG_REFRESH_IMAGE = 1;
    private int mCurrentPageIndex;
    private List<PageContent> mAdData = null;
    private CommonImageLoader mImageLoader = null;
    private int mFocusIndex = 0;
    private ArrayList<View> mSiteView = new ArrayList<View>();

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_IMAGE:
                    ArrayList<Integer> poss = (ArrayList<Integer>) msg.obj;
                    loadImageValue(poss);
                    break;
            }
            return false;
        }
    });


    public SecondPageFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemFocusChangeListener = new ItemOnFocusChangeListener();
        mItemOnClickListener = new ItemOnClickListener();
        mImageLoader = new CommonImageLoader();
        mPageIndex = getArguments().getInt("pageIndex", -1);
        mCurrentPageIndex = getArguments().getInt("currentPageIndex", 0);
        mPageInfo = (PageInfo) getArguments().getSerializable("pageInfo");
        if (mPageInfo != null) {
            mPageNum = mPageInfo.mPageNum;
            mItemCount = mPageInfo.mItemCount;
            if (mPageInfo.isGetSuccess && mPageInfo.mAdData != null) {
                mAdData = mPageInfo.mAdData;
                Log.i(TAG, "get data from cache " + mAdData.toString());
            }
        }
        if (mAdData == null) {
            mAdData = new ArrayList<PageContent>();
        }
        Log.i(TAG, "onCreate " + mPageIndex);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView " + mPageIndex);
        mGridLayout = (AppGridLayout) inflater.inflate(R.layout.app_grid_layout, container, false);
        mGridLayout.setOrientation(GridLayout.HORIZONTAL);
        mGridLayout.setColumnCount(3);
        mGridLayout.setRowCount(2);
        mGridLayout.setItemBoundaryListener(this);
        mGridLayout.setFocusable(true);
        int marginRight, marginBottom;
        int rowCnt = mGridLayout.getRowCount();
        int colCnt = mGridLayout.getColumnCount();
        int columnIndex;
        int rowIndex;
        for (int i = 0; i < mAdData.size(); i++) {
            //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(470, 328);
        	ViewGroup.LayoutParams params = new ViewGroup.LayoutParams((int)this.getResources().getDimension(R.dimen.ad_item_layout_width), (int)this.getResources().getDimension(R.dimen.ad_item_layout_height));
            GridLayout.LayoutParams params1 = new GridLayout.LayoutParams(params);
            rowIndex = i / colCnt;
            columnIndex = i % colCnt;
            GridLayout.Spec rowSpec = GridLayout.spec(rowIndex);
            GridLayout.Spec columnSpec = GridLayout.spec(columnIndex);
            marginRight = 34;
            marginBottom = 30;
            if ((i+1) % colCnt == 0) {
                marginRight = 0;
            }
            if (i / colCnt == (rowCnt -1)) {
                marginBottom = 0;
            }
            params1.setMargins(0, 0, marginRight, marginBottom);
            params1.rowSpec = rowSpec;
            params1.columnSpec = columnSpec;
            mGridLayout.addView(getChildView(inflater, i), params1);
        }
        setFocusView();
        refreshImageView();
        return mGridLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup frameLayout = (ViewGroup) getView();
        frameLayout.setClipChildren(false);
        Log.d(TAG, "end");
    }

    private View getChildView(LayoutInflater inflater, int index) {
        AdItemLayout child = (AdItemLayout) inflater.inflate(R.layout.second_recommend_item, null);
        initAdLayout(child, index);
        child.setOnFocusChangeListener(mItemFocusChangeListener);
        child.setOnClickListener(mItemOnClickListener);
        return child;
    }

    private void initAdLayout(AdItemLayout mAdLayout, int pos) {
        if (pos < mAdData.size()) {
            ImageView adImg = mAdLayout.getAdImageView();
            String iconUrl = LOCATION + mAdData.get(pos).getThumbUrl();
            adImg.setTag(iconUrl);
            DisplayImageMgr.getInstance().setDefaultBitmap(adImg, 470, 328);
            if (!mLoadImageList.contains(adImg)) {
                mLoadImageList.add(adImg);
            }
            mAdLayout.setTag(pos);
            mAdLayout.setAdName(mAdData.get(pos).getName());
            mAdLayout.setVisibility(View.VISIBLE);
        } else {

        }
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "pos " + mPageIndex);
        super.onDestroyView();
        mLoadImageList.clear();
        mSiteView.clear();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
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
    public boolean focusOnBoundaryLeft() {
        Log.i(TAG, "focusOnBoundaryLeft");
        if (mPageNotifyListener != null) {
            mPageNotifyListener.onBeforePage(mPageIndex);
            return true;
        }
        return false;
    }

    @Override
    public boolean focusOnBoundaryBottom() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onKeyMode(ViewPagerState state) {
        if (state != null) {
            this.mState = state;
            Log.d(TAG, "mState:" + mState.toString());
            mFocusIndex = state.getFocusPosInPage();
            mCurrentPageIndex = state.getCurPageIndex();
            Log.d(TAG, "current pageIndex " + mCurrentPageIndex + " mPageIndex " + this.mPageIndex);
        }
        setFocusView();
    }

    private void setFocusView() {
        if (mCurrentPageIndex == this.mPageIndex) {
            int childCnt = mGridLayout.getChildCount();
            if (childCnt != 0) {
                mFocusIndex = (mFocusIndex >= childCnt) ? (childCnt - 1) : mFocusIndex;
                mGridLayout.getChildAt(mFocusIndex).requestFocus();
                Log.i(TAG, "child request focus " + mFocusIndex);
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
            refreshImageView();
        } else {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void refreshImageView() {
        ArrayList<Integer> poss = new ArrayList<Integer>();
        int all = mLoadImageList.size();
        int delay = 600;
        for (int i = 0; i < all; i++) {
            if (mPageIndex == mCurrentPageIndex) {
                poss.add(i);
            } else if (mPageIndex == (mCurrentPageIndex -1)) {
                if ((i + 1) % 3 == 0) {
                    poss.add(i);
                    mSiteView.add(mGridLayout.getChildAt(i));
                }
            } else if (mPageIndex == (mCurrentPageIndex + 1)) {
                if (i % 3 == 0) {
                    poss.add(i);
                    mSiteView.add(mGridLayout.getChildAt(i));
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
                Log.d(TAG, "view pos " + view.getTag().toString());
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

    private void loadImageValue(ArrayList<Integer> poss) {
        for (int index : poss) {
            final ImageView imageView = mLoadImageList.get(index);
            if (imageView != null) {
                String iconUrl = imageView.getTag().toString();
                mImageLoader.loadImageWithManager(iconUrl, imageView);
            }
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
                if (v.getTag() != null && mPageNotifyListener != null) {
                    mFocusIndex = pos;
                    mPageNotifyListener.onItemFocusChange(mPageNum, mFocusIndex);
                }
                mCursorView.setFocusView(v);
            } else {
                mCursorView.setUnFocusWithSameFocusView(v);
            }
        }
    }

    class ItemOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                int clickPos = Integer.valueOf(v.getTag().toString());
                if (mAdData != null && clickPos < mAdData.size()) {
                    PageContent pageContent = mAdData.get(clickPos);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    
                    if(pageContent.getUploader().equals("official")) {
                        intent.putExtra("appId", pageContent.getId());
                        intent.putExtra("appName", pageContent.getName());
                        intent.putExtra("appUrl", pageContent.getVideoUrl());
                        intent.putExtra("uploader", pageContent.getUploader());
                        intent.putExtra("tips", pageContent.getTips());
                        intent.putExtra("material", pageContent.getMaterials());
                        
                        intent.setClass(getActivity(), PushAppDetailActivity.class);                    	
                    }
                    else {
                        intent.putExtra("albumId", pageContent.getId());
                        intent.putExtra("appId", 0);
                        intent.putExtra("albumName", pageContent.getName());
                        intent.putExtra("uploader", pageContent.getUploader());
                        
                        intent.setClass(getActivity(), PushAlbumDetailActivity.class);                    	
                    }

                    v.getContext().startActivity(intent);
                }
            }
        }
    }

    public void clearAllValues() {
        mAdData = new ArrayList<PageContent>();
        mLoadImageList.clear();
        mPageInfo = null;
    }

    @Override
    public void hideAllWidget() {
        clearAllValues();
        mGridLayout.setVisibility(View.GONE);
    }

}

package com.cookingshow.share;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cookingshow.R;
import com.cookingshow.category.PageInfo;
import com.cookingshow.datacenter.PageContent;
import com.cookingshow.network.controller.CommonImageLoader;
import com.cookingshow.page.CommonFragment;
import com.cookingshow.page.DisplayImageMgr;
import com.cookingshow.page.ViewPagerState;
import com.cookingshow.push.PushAlbumDetailActivity;
import com.cookingshow.view.AdItemLayout;
import com.cookingshow.view.AppGridLayout;

public class SharePageFragment extends CommonFragment implements
        AppGridLayout.ItemOnBoundaryListener{

    private static final String TAG = "TopPageFragment";
    private static final String LOCATION = "http://182.92.198.90";
    private PageInfo mPageInfo = null;
    private ViewPagerState mState = null;
    private int mCurrentPageIndex;
    private int mPageNum;
    private List<PageContent> mAdData = null;
    private List<ImageView> mLoadImageList = null;
    private static final int MSG_REFRESH_IMAGE = 1;
    private static final int MSG_INIT_FOCUS = 2;
    private ViewGroup mContentView = null;
    private ItemOnClickListener mItemOnClickListener = null;
    private ItemOnFocusChangeListener mItemOnFocusChangeListener = null;
    private View mLoadingView = null;
    private TextView mNoDataView = null;
    private CommonImageLoader mImageLoader = null;
    private int mFocusIndex = 0;
    private AppGridLayout mGridLayout = null;
    private static final int ITEMS_PER_PAGE = 6;
    private ArrayList<View> mSiteView = new ArrayList<View>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = getArguments().getInt("pageIndex", -1);
        mCurrentPageIndex = getArguments().getInt("currentPageIndex", 0);
        mPageInfo = (PageInfo) getArguments().getSerializable("pageInfo");
        mAdData = new ArrayList<PageContent>();
        if (mPageInfo != null) {
            mPageNum = mPageInfo.mPageNum;
            if (mPageInfo.isGetSuccess && mPageInfo.mAdData != null) {
                mAdData = mPageInfo.mAdData;
                Log.i(TAG, "get data from cache " + mAdData.toString());
            }
        }
        mLoadImageList = new ArrayList<ImageView>();
        mItemOnClickListener = new ItemOnClickListener();
        mItemOnFocusChangeListener = new ItemOnFocusChangeListener();
        mImageLoader = new CommonImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_top_first, container, false);
        mGridLayout = (AppGridLayout) mContentView.findViewById(R.id.app_grid_layout);
        mLoadingView = mContentView.findViewById(R.id.tv_loading);
        mLoadingView.setVisibility(View.VISIBLE);
        mNoDataView = (TextView) mContentView.findViewById(R.id.tv_no_data_view);
        mGridLayout.setOrientation(GridLayout.HORIZONTAL);
        mGridLayout.setColumnCount(3);
        mGridLayout.setRowCount(2);
        mGridLayout.setItemBoundaryListener(this);
        mGridLayout.setFocusable(true);
        initContent(inflater);
        refreshImageView();
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup frameLayout = (ViewGroup) getView();
        frameLayout.setClipChildren(false);
        mHandler.sendEmptyMessageDelayed(MSG_INIT_FOCUS, 20);
        Log.d(TAG, "onViewCreated");
    }

    private void initContent(LayoutInflater inflater) {
        if (mAdData != null && !mAdData.isEmpty()) {
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
                if ((i + 1) % colCnt == 0) {
                    marginRight = 0;
                }
                if (i / colCnt == (rowCnt - 1)) {
                    marginBottom = 0;
                }
                params1.setMargins(0, 0, marginRight, marginBottom);
                params1.rowSpec = rowSpec;
                params1.columnSpec = columnSpec;
                mGridLayout.addView(getChildView(inflater, i), params1);
            }
        } else {
            mGridLayout.setVisibility(View.INVISIBLE);
            mNoDataView.setVisibility(View.VISIBLE);
            mNoDataView.setOnKeyListener(mOnNoDataKeyListener);
        }
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    private View getChildView(LayoutInflater inflater, int index) {
        AdItemLayout child = (AdItemLayout) inflater.inflate(R.layout.share_ad_item, null);
        initAdLayout(child, index);
        child.setOnFocusChangeListener(mItemOnFocusChangeListener);
        child.setOnClickListener(mItemOnClickListener);
        return child;
    }

    private void initAdLayout(AdItemLayout adLayout, int pos) {
        if (pos < mAdData.size()) {
            ImageView adImg = adLayout.getAdImageView();
            String iconUrl = LOCATION + mAdData.get(pos).getThumbUrl();
            adImg.setTag(iconUrl);
            DisplayImageMgr.getInstance().setDefaultBitmap(adImg, 470, 328);
            if (!mLoadImageList.contains(adImg)) {
                mLoadImageList.add(adImg);
            }
            adLayout.setTag(pos);
            adLayout.setAdName(mAdData.get(pos).getName());
            adLayout.setAdSubName(getActivity().getString(R.string.update_time) + mAdData.get(pos).getTips());
            adLayout.setVisibility(View.VISIBLE);
        } else {

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

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_IMAGE:
                    ArrayList<Integer> poss = (ArrayList<Integer>) msg.obj;
                    loadImageValue(poss);
                    break;
                case MSG_INIT_FOCUS:
                    setFocusView();
                    break;
            }
            return false;
        }
    });

    private void loadImageValue(ArrayList<Integer> poss) {
        for (int index : poss) {
            final ImageView imageView = mLoadImageList.get(index);
            if (imageView != null) {
                String iconUrl = imageView.getTag().toString();
                mImageLoader.loadImageWithManager(iconUrl, imageView);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        if (mCurrentPageIndex == this.mPageIndex) {
            int childCnt = mGridLayout.getChildCount();
            if (childCnt != 0) {
                mFocusIndex = (mFocusIndex >= childCnt) ? (childCnt - 1) : mFocusIndex;
                mGridLayout.getChildAt(mFocusIndex).requestFocus();
                Log.i(TAG, "child request focus");
            } else {
                mNoDataView.requestFocus();
            }
        }
    }

    @Override
    public void onAirMouseMode(ViewPagerState state) {

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

    class ItemOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (v.getTag() != null && mPageNotifyListener != null) {
                    mFocusIndex = Integer.valueOf(v.getTag().toString());
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
                Log.d(TAG, "item focus " + clickPos);
                if (mAdData != null && clickPos < mAdData.size()) {
                    PageContent pageContent = mAdData.get(clickPos);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.putExtra("albumId", pageContent.getId());
                    intent.putExtra("appId", 0);
                    intent.putExtra("albumName", pageContent.getName());
                    intent.putExtra("uploader", pageContent.getUploader());

                    intent.setClass(getActivity(), PushAlbumDetailActivity.class);
                    v.getContext().startActivity(intent);
                }
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

    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        private int downId;

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            int pos = Integer.valueOf(v.getTag().toString());
            int action = event.getAction();
            if (action == KeyEvent.ACTION_DOWN) {
                downId = v.getId();
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (pos == 0 || pos == 1 || pos == 2) {
                        if (action == KeyEvent.ACTION_DOWN) {
                            mPageNotifyListener.onFocusToTop();
                            return true;
                        }
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (pos == 0) {
                        if (action == KeyEvent.ACTION_UP && downId == v.getId()) {
                            mPageNotifyListener.onBeforePage(mPageIndex);
                        }
                        return true;
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (pos == (mAdData.size() - 1)) {
                        if (action == KeyEvent.ACTION_UP && downId == v.getId()) {
                            mPageNotifyListener.onNextPage(mPageIndex);
                        }
                        return true;
                    }
                    break;
            }
            return false;
        }
    };

    @Override
    public PageInfo getPageInfo() {
        return mPageInfo;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoadImageList.clear();
        mSiteView.clear();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}

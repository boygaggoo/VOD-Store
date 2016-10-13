package com.cookingshow.recommend;

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
import android.widget.ImageView;
import android.widget.TextView;
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

public class FirstPageFragment extends CommonFragment {

    protected static final String TAG = "FirstPageFragment";
    private static final String LOCATION = "http://182.92.198.90";
    private PageInfo mPageInfo = null;
    private ViewPagerState mState = null;
    private int mCurrentPageIndex;
    private int mPageNum;
    private List<PageContent> mAdData = null;
    private AdItemLayout mAdLayout1 = null;
    private AdItemLayout mAdLayout2 = null;
    private AdItemLayout mAdLayout3 = null;
    private AdItemLayout mAdLayout4 = null;
    private AdItemLayout mAdLayout5 = null;
    private List<AdItemLayout> mAdLayoutList = null;
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
    private ArrayList<View> mSiteView = new ArrayList<View>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = getArguments().getInt("pageIndex", -1);
        mCurrentPageIndex = getArguments().getInt("currentPageIndex", 0);
        mPageInfo = (PageInfo) getArguments().getSerializable("pageInfo");
        if (mPageInfo != null) {
            mPageNum = mPageInfo.mPageNum;
            if (mPageInfo.isGetSuccess && mPageInfo.mAdData != null) {
                mAdData = mPageInfo.mAdData;
                Log.i(TAG, "get data from cache ");
            }
        }
        mAdLayoutList = new ArrayList<AdItemLayout>();
        mLoadImageList = new ArrayList<ImageView>();
        mItemOnClickListener = new ItemOnClickListener();
        mItemOnFocusChangeListener = new ItemOnFocusChangeListener();
        mImageLoader = new CommonImageLoader();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_recommend_first, container, false);
        mLoadingView = mContentView.findViewById(R.id.tv_loading);
        mLoadingView.setVisibility(View.VISIBLE);
        mNoDataView = (TextView) mContentView.findViewById(R.id.tv_no_data_view);
        mAdLayout1 = (AdItemLayout) mContentView.findViewById(R.id.ad_layout_1);
        mAdLayout2 = (AdItemLayout) mContentView.findViewById(R.id.ad_layout_2);
        mAdLayout3 = (AdItemLayout) mContentView.findViewById(R.id.ad_layout_3);
        mAdLayout4 = (AdItemLayout) mContentView.findViewById(R.id.ad_layout_4);
        mAdLayout5 = (AdItemLayout) mContentView.findViewById(R.id.ad_layout_5);
        mAdLayoutList.add(mAdLayout1);
        mAdLayoutList.add(mAdLayout2);
        mAdLayoutList.add(mAdLayout3);
        mAdLayoutList.add(mAdLayout4);
        mAdLayoutList.add(mAdLayout5);
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup frameLayout = (ViewGroup) getView();
        frameLayout.setClipChildren(false);
        initAdLayout();
        refreshImageView();
        if (mPageDataLoadListener != null) {
            mPageDataLoadListener.onPageDataLoadFinish(mPageInfo, 0, mPageIndex);
        }
        mHandler.sendEmptyMessageDelayed(MSG_INIT_FOCUS, 20);
        Log.d(TAG, "onViewCreated");
    }

    private void initAdLayout() {
        if (mAdData != null && !mAdData.isEmpty()) {
            for (int i = 0; i < mAdLayoutList.size(); i++) {
                if (i < mAdData.size()) {
                    AdItemLayout mAdLayout = mAdLayoutList.get(i);
                    ImageView adImg = mAdLayout.getAdImageView();
                    String iconUrl = LOCATION + mAdData.get(i).getThumbUrl();
                    adImg.setTag(iconUrl);
                    setDefaultAdImage(adImg, i);
                    if (!mLoadImageList.contains(adImg)) {
                        mLoadImageList.add(adImg);
                    }
                    mAdLayout.setTag(i);
                    String adName = mAdData.get(i).getName();
                    String[] strs = adName.split(":");
                    mAdLayout.setAdName(strs[0]);
                    if (strs.length > 1) {
                        mAdLayout.setAdSubName(strs[strs.length - 1]);
                    }
                    mAdLayout.setVisibility(View.VISIBLE);
                    mAdLayout.setOnFocusChangeListener(mItemOnFocusChangeListener);
                    mAdLayout.setOnClickListener(mItemOnClickListener);
                    mAdLayout.setOnKeyListener(mOnKeyListener);
                } else {

                }
            }
        } else {
            mAdLayoutList.clear();
            mNoDataView.setVisibility(View.VISIBLE);
            mNoDataView.setOnKeyListener(mOnNoDataKeyListener);
        }
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    private void setDefaultAdImage(ImageView imageView, int index) {
        switch (index) {
            case 0:
                DisplayImageMgr.getInstance().setDefaultBitmap(imageView, 466, 681);
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                DisplayImageMgr.getInstance().setDefaultBitmap(imageView, 470, 328);
                break;
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
                if (i == 2 || i == 4) {
                    poss.add(i);
                    mSiteView.add(mAdLayoutList.get(i));
                }
            } else if (mPageIndex == (mCurrentPageIndex + 1)) {
                if (i == 0) {
                    poss.add(i);
                    mSiteView.add(mAdLayoutList.get(i));
                }
            }
        }
        setStateForSiteView();
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
                view.setEnabled(false);
                view.setClickable(false);
            }
        }
    }

    private void clearStateForSiteView() {
        for (View view : mSiteView) {
            if (view != null) {
                view.setEnabled(true);
                view.setClickable(true);
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
        if (poss == null) {
            return;
        }
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
            Log.d(TAG, "mState:" + mState.toString());
            mFocusIndex = state.getFocusPosInPage();
            mCurrentPageIndex = state.getCurPageIndex();
            Log.d(TAG, "mPageIndex " + this.mPageIndex);
        }
        setFocusView();
    }

    private void setFocusView() {
        if (mCurrentPageIndex == this.mPageIndex) {
            if (mAdData != null && mAdData.size() != 0) {
                int childCnt = mAdData.size();
                mFocusIndex = (mFocusIndex >= childCnt) ? (childCnt - 1) : mFocusIndex;
                mAdLayoutList.get(mFocusIndex).requestFocus();
                Log.i(TAG, "child request focus " + mFocusIndex);
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

    class ItemOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int pos = Integer.valueOf(v.getTag().toString());
            if (hasFocus) {
                if (v.getTag() != null && mPageNotifyListener != null) {
                    mFocusIndex = pos;
                    if (mFocusIndex == 3 || mFocusIndex == 4) {
                        mFocusIndex += 1;
                    }
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

    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {
        private int downId;

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            View focusView = v;
            int pos = Integer.valueOf(focusView.getTag().toString());
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
                    if (isTheRightItem(pos)) {
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

    private boolean isTheRightItem(int pos) {
        int diff = 0;
        switch (pos) {
            case 0:
                diff = 1;
                break;
            case 1:
                diff = 1;
                break;
            case 3:
                diff = 1;
                break;
            default:
                diff = 3;
                break;
        }
        if (pos + diff > (mAdData.size() - 1)) {
            return true;
        }
        return false;
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

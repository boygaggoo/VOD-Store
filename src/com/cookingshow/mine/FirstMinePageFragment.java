package com.cookingshow.mine;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.cookingshow.R;
import com.cookingshow.category.PageInfo;
import com.cookingshow.network.cache.ACache;
import com.cookingshow.network.controller.CommonStringLoader;
import com.cookingshow.network.exception.CommonException;
import com.cookingshow.network.model.BaseStringRequest;
import com.cookingshow.page.CommonFragment;
import com.cookingshow.page.ViewPagerState;
import com.cookingshow.push.PushTicketActivity;
import com.cookingshow.userinfo.UserInfoActivity;

public class FirstMinePageFragment extends CommonFragment {

    private static final String TAG = "FirstMinePageFragment";
    private PageInfo mPageInfo = null;
    private ViewPagerState mState = null;
    private int mCurrentPageIndex;
    private int mPageNum;
    private List<View> mLayoutList = null;
    private ViewGroup mContentView = null;
    private ItemOnClickListener mItemOnClickListener = null;
    private ItemOnFocusChangeListener mItemOnFocusChangeListener = null;
    private View mLoadingView = null;
    private TextView mNoDataView = null;
    private int mFocusIndex = 0;
    private ACache mCache = null;
    private CommonStringLoader mStringLoader = null;
    private TextView mUpdateCnt = null;
    private TextView messageCnt = null;

    private View mCenterLayout = null;
    private View mUploadLayout = null;
    private View mMessageLayout = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = getArguments().getInt("pageIndex", -1);
        mCurrentPageIndex = getArguments().getInt("currentPageIndex", 0);
        mPageInfo = (PageInfo) getArguments().getSerializable("pageInfo");

        if (mPageInfo != null) {
            mPageNum = mPageInfo.mPageNum;
            if (mPageInfo.isGetSuccess) {
                Log.i(TAG, "get data from cache ");
            }
        }
        mLayoutList = new ArrayList<View>();
        mItemOnClickListener = new ItemOnClickListener();
        mItemOnFocusChangeListener = new ItemOnFocusChangeListener();
        registerReceiver();
        
        mCache = ACache.get(getActivity());
        mStringLoader = CommonStringLoader.getInstance();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        getActivity().registerReceiver(mUpdateCntReciever, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_mine_first, container, false);
        mLoadingView = mContentView.findViewById(R.id.tv_loading);
        mLoadingView.setVisibility(View.VISIBLE);

        mCenterLayout = mContentView.findViewById(R.id.mine_item_center);
        mUploadLayout = mContentView.findViewById(R.id.mine_item_upload);
        mMessageLayout = mContentView.findViewById(R.id.mine_item_message);

        mLayoutList.add(mCenterLayout);
        mLayoutList.add(mUploadLayout);
        mLayoutList.add(mMessageLayout);

        mUpdateCnt = (TextView) mContentView.findViewById(R.id.update_cnt);
        messageCnt = (TextView) mContentView.findViewById(R.id.mine_message_cnt);

        initAdLayout();
        setListener();
        setFocusView();
        refreshCntView();
        if (mPageDataLoadListener != null) {
            mPageDataLoadListener.onPageDataLoadFinish(mPageInfo, 0, mPageIndex);
        }
        return mContentView;
    }

    private void initAdLayout() {
        mLoadingView.setVisibility(View.INVISIBLE);
    }

    private void setListener() {
        for (int i = 0; i < mLayoutList.size(); i++) {
            View view = mLayoutList.get(i);
            view.setOnClickListener(mItemOnClickListener);
            view.setOnFocusChangeListener(mItemOnFocusChangeListener);
            view.setOnKeyListener(mOnKeyListener);
            view.setTag(i);
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
            int childCnt = mLayoutList.size();
            Log.i(TAG, "child count " + childCnt);
            if (childCnt != 0) {
                mFocusIndex = (mFocusIndex >= childCnt) ? (childCnt - 1) : mFocusIndex;
                mLayoutList.get(mFocusIndex).requestFocus();
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
        } else {

        }
    }

    class ItemOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (v.getTag() != null && mPageNotifyListener != null) {
                    mFocusIndex = Integer.valueOf(v.getTag().toString());
                    mPageNotifyListener.onItemFocusChange(mPageNum, mFocusIndex);
                }
            }
        }
    }

    class ItemOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.mine_item_center:
                    Intent updateIntent = new Intent();
                    updateIntent.setClass(getActivity(), UserInfoActivity.class);
                    startActivity(updateIntent);
                    break;
                case R.id.mine_item_upload:
                	Toast.makeText(FirstMinePageFragment.this.getActivity().getApplicationContext(), getResources().getString(R.string.penging_notice), Toast.LENGTH_LONG).show();
                    break;

                case R.id.mine_item_message:
                    break;
            }

        }
    }

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
                    if (pos == (mLayoutList.size() - 1)) {
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

    private BroadcastReceiver mUpdateCntReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshCntView();
        }
    };

    private void refreshCntView() {
    	String params = "type=coin&user=" + mCache.getAsString("deviceId"); 
    	mStringLoader.addRequest(new BaseStringRequest(Method.GET, "http://182.92.198.90/test/transaction/getUserServiceInfo.php" + "?" + params, new BaseStringRequest.StringResponseListener() {
			
			@Override
			public void onResponse(String str) {
				// TODO Auto-generated method stub
				mCache.put("myCoin", str);
				mUpdateCnt.setText(getResources().getString(R.string.mine_coin) + str + getResources().getString(R.string.coin_unit));
				//Toast.makeText(LauncherFragment.this.getActivity().getApplicationContext(), str, Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onErrorResponse(CommonException exception) {
				// TODO Auto-generated method stub
				
			}
		}));
    	
    	messageCnt.setText("0");
    }

    @Override
    public PageInfo getPageInfo() {
        return mPageInfo;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mUpdateCntReciever);
    }
}

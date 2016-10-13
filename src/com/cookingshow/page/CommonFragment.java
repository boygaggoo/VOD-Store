package com.cookingshow.page;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.cookingshow.category.PageInfo;
import com.cookingshow.view.ItemCursorView;

public abstract class CommonFragment extends Fragment implements IPageViewAware {

    protected IPageNotifyListener mPageNotifyListener = null;
    protected OnPageDataLoadListener mPageDataLoadListener = null;
    protected static final String TAG = "CommonFragment";
    protected int mPageIndex;
    protected ItemCursorView mCursorView = null;

    public void setOnPageDataLoadListener(OnPageDataLoadListener listener) {
        this.mPageDataLoadListener = listener;
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public PageInfo getPageInfo() {
        return null;
    }

    public void clearAllValues() {

    }

    public void hideAllWidget() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState " + mPageIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG, "");
        super.onAttach(activity);
        try {
            if (mPageNotifyListener == null) {
                mPageNotifyListener = (IPageNotifyListener) activity;
            }
        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " null pageNotifyListener");
        }
    }

    public void setPageNotifyListener(IPageNotifyListener listener) {
        this.mPageNotifyListener = listener;
    }

    @Override
    public void onResume() {
        Log.i(TAG, "pos " + mPageIndex + " " + isHidden);
        super.onResume();
        if (!isHidden) {
            ActionEventMgr.getInstance().addObserver(this);
        }
    }

    @Override
    public void onPause() {
        Log.i(TAG, "pos " + mPageIndex);
        super.onPause();
        ActionEventMgr.getInstance().removeObserver(this);
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "pos " + mPageIndex);
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "pos " + mPageIndex);
        super.onDestroy();
    }

    public void refreshAlphaState(boolean isAlpha) {

    }

    public interface OnPageDataLoadListener {
        public void onPageDataLoadFinish(PageInfo pageInfo, int allNum, int pos);

    }

    protected boolean isHidden = false;

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
        if (isHidden) {
            ActionEventMgr.getInstance().removeObserver(this);
        } else {
            ActionEventMgr.getInstance().addObserver(this);
        }
    }

    public void setItemCursorView(ItemCursorView cursorView) {
        this.mCursorView = cursorView;
    }

    protected View mRecycleView = null;

    public void setRecycleView(View view) {
        this.mRecycleView = view;
    }

    public View getRecycleView() {
        return mRecycleView;
    }

    public void refreshPageData(PageInfo pageInfo) {
        
    }

}

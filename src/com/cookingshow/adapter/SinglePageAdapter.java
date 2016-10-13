package com.cookingshow.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ViewGroup;

import com.cookingshow.category.CategoryInfo;
import com.cookingshow.category.PageInfo;
import com.cookingshow.page.BlankFragment;
import com.cookingshow.page.CommonFragment;
import com.cookingshow.page.FragmentFactory;
import com.cookingshow.page.IPageNotifyListener;
import com.cookingshow.page.PagerDataHelper;
import com.cookingshow.view.ItemCursorView;

public class SinglePageAdapter extends android.support.v4.app.FragmentStatePagerAdapter
        implements CommonFragment.OnPageDataLoadListener {

    private static final String TAG = "SinglePageAdapterV13";
    private PagerDataHelper mDataHelper = null;
    private int mCurrentPos = 0;
    private Map<Integer, CommonFragment> mPageReferenceMap = null;
    private List<Integer> mRefreshIndexs = null;
    private int mMaxPage = 1;


    public SinglePageAdapter(FragmentManager fm, PagerDataHelper helper) {
        super(fm);
        this.mDataHelper = helper;
        initPagerData(mDataHelper.getCurrentCate());
        mPageReferenceMap = new HashMap<Integer, CommonFragment>();
        mRefreshIndexs = new ArrayList<Integer>();
    }

    public void initPagerData(CategoryInfo cateInfo) {
        mMaxPage = cateInfo.mPages;
    }

    @Override
    public Fragment getItem(int position) {
        return newInstance(position);
    }

    @Override
    public int getCount() {
        return mMaxPage;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.i(TAG, "destroyItem pos: " + position);
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        if (!(object instanceof CommonFragment)) {
            return POSITION_NONE;
        }
        int pageIndex = ((CommonFragment)object).getPageIndex();
        Log.d(TAG, "pos " + pageIndex);
        if (mRefreshIndexs.contains(pageIndex)) {
            return POSITION_NONE;
        }
        return POSITION_UNCHANGED;
    }

    public void setSelectedPos(int pos) {
        Log.i(TAG, "pos:" + pos);
        this.mCurrentPos = pos;
        CommonFragment fragment = mPageReferenceMap.get(pos);
        if (fragment != null) {
            PageInfo pageInfo = fragment.getPageInfo();
            if (pageInfo != null) {
                mDataHelper.updateCurrentState(pageInfo);
            }
        }
    }

    private Fragment newInstance(int position) {
        Log.i(TAG, "pos: " + position + ";" + mCurrentPos);
        CommonFragment fragment = null;
        PageInfo pageInfo = mDataHelper.getPageData(position - mCurrentPos);
        if (pageInfo == null) {
            fragment = new BlankFragment();
        } else {
            fragment = FragmentFactory.newInstance(pageInfo.mFragmentType);
        }
        Bundle args = new Bundle();
        args.putSerializable("pageInfo", pageInfo);
        args.putInt("pageIndex", position);
        args.putInt("currentPageIndex", mCurrentPos);
        fragment.setArguments(args);
        fragment.setOnPageDataLoadListener(this);
        fragment.setPageNotifyListener(mPageNotifyListener);
        fragment.setItemCursorView(mCursorView);
        mPageReferenceMap.put(position, fragment);
        return fragment;
    }

    public boolean isTheLastPage() {
        return mDataHelper.isTheLastPage();
    }

    public boolean isTheFirstPage() {
        return mDataHelper.isTheFirstPage();
    }

    @Override
    public void onPageDataLoadFinish(PageInfo pageInfo, int allCount, int pos) {
        Log.i(TAG, "pos " + pos + " " + mCurrentPos);
        if (mDataHelper.isRefreshPageView(pageInfo, allCount)) {
            Log.i(TAG, "category:" + pageInfo.mCategoryName + ";" + allCount);
            boolean isRefreshNow = true;
            if (!mRefreshIndexs.isEmpty()) {
                isRefreshNow = false;
            }
            if (pos == mCurrentPos) {
                setRefreshPageIndexs(pos, 1);
                mRefreshIndexs.remove((Integer)pos);
                mMaxPage = mDataHelper.getCurrentCate().mPages;
            } else if (pos > mCurrentPos){
                setRefreshPageIndexs(pos, 1);
                mRefreshIndexs.remove((Integer)pos);
            } else {
                setRefreshPageIndexs(pos, -1);
            }
            if (isRefreshNow) {
                notifyDataSetChanged();
            }
        }
    }

    public void notifyDataSetChanged() {
        Log.i(TAG, "start " + mRefreshIndexs.toString());
        super.notifyDataSetChanged();
        mRefreshIndexs.clear();
        Log.i(TAG, "end");
    }

    private void setRefreshPageIndexs(int cPos, int flag) {
        Log.i(TAG, "flag:" + flag + " cpos:" + cPos);
        if (flag == 0) { //add all
            mRefreshIndexs.clear();
            mRefreshIndexs.addAll(mPageReferenceMap.keySet());
        } else if (flag == 2) {
            if (!mRefreshIndexs.contains(cPos)) {
                mRefreshIndexs.add(cPos);
            }
        } else {
            for (int key : mPageReferenceMap.keySet()) {
                if (flag == 1 && key >= cPos) {
                    if (!mRefreshIndexs.contains(key)) {
                        mRefreshIndexs.add(key);
                    }
                } else if (flag == -1 && key <= cPos) {
                    if (!mRefreshIndexs.contains(key)) {
                        mRefreshIndexs.add(key);
                    }
                }
            }
        }
        Log.i(TAG, mRefreshIndexs.toString());
    }

    public void refreshAlphaState(int pagePos, boolean isAlpha) {
        CommonFragment fragment = mPageReferenceMap.get(pagePos);
        if (fragment != null) {
            fragment.refreshAlphaState(isAlpha);
        }
    }

    private IPageNotifyListener mPageNotifyListener = null;

    public void setPageNotifyListener(IPageNotifyListener listener) {
        this.mPageNotifyListener = listener;
    }

    public void onHiddenForFragment(boolean flag) {
        Log.d(TAG, "flag " + flag + " size " + mPageReferenceMap.size());
        for (int key : mPageReferenceMap.keySet()) {
            mPageReferenceMap.get(key).setHidden(flag);
        }
    }

    private ItemCursorView mCursorView = null;
    public void setItemCurseView(ItemCursorView cursorView) {
        this.mCursorView = cursorView;
    }


}

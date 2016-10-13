package com.cookingshow.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.cookingshow.category.CategoryInfo;
import com.cookingshow.category.PageInfo;
import com.cookingshow.page.BlankFragment;
import com.cookingshow.page.CommonFragment;
import com.cookingshow.page.FragmentFactory;
import com.cookingshow.page.IPageNotifyListener;
import com.cookingshow.page.PagerDataHelper;
import com.cookingshow.view.ItemCursorView;

public class MultiRecyclePageAdapter extends android.support.v4.app.FragmentStatePagerAdapter
        implements CommonFragment.OnPageDataLoadListener {

    private static final String TAG = "MultiRecyclePageAdapter";

    private IPageNotifyListener mPageNotifyListener = null;
    private List<Integer> mPageIndex = null;
    private static final int MAX_PAGE_INDEX = 400;
    private PagerDataHelper mDataHelper = null;
    private int mCurrentPos = 0;
    private Map<Integer, CommonFragment> mPageReferenceMap = null;
    private ViewPager mViewPager = null;
    private List<Integer> mRefreshIndexs = null;
    private final RecycleBin mRecycleBin;
    private static final int IGNORE_ITEM_VIEW_TYPE = AdapterView.ITEM_VIEW_TYPE_IGNORE;
    private ItemCursorView mCursorView = null;

    public MultiRecyclePageAdapter(FragmentManager fm, PagerDataHelper dataHelper) {
        super(fm);
        mRecycleBin = new RecycleBin();
        mRecycleBin.setViewTypeCount(1);
        mPageIndex = new ArrayList<Integer>();
        for (int i = 0; i <= MAX_PAGE_INDEX; i++) {
            mPageIndex.add(i - MAX_PAGE_INDEX / 2);
        }
        mPageReferenceMap = new HashMap<Integer, CommonFragment>();
        this.mDataHelper = dataHelper;
        mRefreshIndexs = new ArrayList<Integer>();
        mCurrentPos = MAX_PAGE_INDEX / 2;
    }

    public void setPageNotifyListener(IPageNotifyListener onPageNotifyListener) {
        this.mPageNotifyListener = onPageNotifyListener;
    }

    public void initPagerData(List<CategoryInfo> cateList) {
        mDataHelper = new PagerDataHelper(cateList);
    }

    public void initViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
    }

    public int getInitPageIndex() {
        return mCurrentPos;
    }


    @Override
    public Fragment getItem(int position) {
        return newInstance(position);
    }

    @Override
    public int getCount() {
        return mPageIndex.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.i(TAG, "destroyItem pos: " + position);
        int viewType = getItemViewType(position);
        if (viewType != IGNORE_ITEM_VIEW_TYPE) {
            View view = null;
            if (object instanceof CommonFragment) {
                view = ((CommonFragment)object).getRecycleView();
            }
            if (view != null) {
                mRecycleBin.addScrapView(view, position, viewType);
            }
        }
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.i(TAG, "instantiateItem pos: " + position);
        return super.instantiateItem(container, position);
    }

    @Override
    public int getItemPosition(Object object) {
        if (!(object instanceof CommonFragment)) {
            return POSITION_NONE;
        }
        int pageIndex = ((CommonFragment)object).getPageIndex();
        if (mRefreshIndexs.contains(pageIndex)) {
            Log.i(TAG, "refresh pos " + pageIndex);
            refreshPageData(pageIndex);
            return POSITION_UNCHANGED;
        }
        return POSITION_UNCHANGED;
    }

    public void refreshPageData(int refreshPos) {
        if (mPageReferenceMap.get(refreshPos) != null) {
            int diff = refreshPos - mCurrentPos;
            if (Math.abs(diff) <= 2) {
                PageInfo pageInfo = mDataHelper.getPageData(diff);
                CommonFragment fragment = mPageReferenceMap.get(refreshPos);
                fragment.refreshPageData(pageInfo);
            }
        }
    }

    public void setSelectedPos(int pos) {
        Log.i(TAG, "setSelectedPos");
        this.mCurrentPos = pos;
        CommonFragment fragment = mPageReferenceMap.get(pos);
        if (fragment != null) {
            PageInfo pageInfo = fragment.getPageInfo();
            if (pageInfo != null) {
                Log.i(TAG, "pageinfo " + pageInfo.mCategoryName);
                mDataHelper.updateCurrentState(pageInfo);
            }
        }
    }

    private Fragment newInstance(int position) {
        Log.i(TAG, "newInstance pos: " + position + ";" + mCurrentPos);
        CommonFragment fragment = null;
        int diff = position - mCurrentPos;
        PageInfo pageInfo = mDataHelper.getPageData(diff);
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

        View view = null;
        int viewType = getItemViewType(position);
        if (viewType != IGNORE_ITEM_VIEW_TYPE) {
            view = mRecycleBin.getScrapView(position, viewType);
        }
        fragment.setRecycleView(view);
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
        mRecycleBin.scrapActiveViews();
        super.notifyDataSetChanged();
        mRefreshIndexs.clear();
        Log.i(TAG, "end");
    }

    public boolean switchCategoryByName(String name) {
        int targetPos = mCurrentPos;
        boolean isNeedUpdate = true;
        for (int key : mPageReferenceMap.keySet()) {
            CommonFragment fragment = mPageReferenceMap.get(key);
            if (fragment != null) {
                PageInfo pageInfo = fragment.getPageInfo();
                if (pageInfo != null &&
                        pageInfo.mCategoryName.equals(name) && pageInfo.mPageNum == 0) {
                    targetPos = key;
                    isNeedUpdate = false;
                }
            }
        }
        if (isNeedUpdate) {
            int cIndex = mDataHelper.getCurrentCategoryIndex();
            int tIndex = mDataHelper.findCategoryIndexByName(name);
            boolean isToRightFlag = true;
            if (tIndex > cIndex) {
                mCurrentPos = mCurrentPos + 2;
                setRefreshPageIndexs(mCurrentPos, 2);
            } else {
                isToRightFlag = false;
                mCurrentPos = mCurrentPos - 2;
                setRefreshPageIndexs(mCurrentPos, 2);
            }
            mDataHelper.setCurrentCategory(name);
            notifyDataSetChanged();
            if (isToRightFlag) {
                setRefreshPageIndexs(mCurrentPos, -1);
            } else {
                setRefreshPageIndexs(mCurrentPos, 1);
            }
            mRefreshIndexs.remove((Integer)mCurrentPos);
//            mRefreshIndexs.add(targetPos);
            targetPos = mCurrentPos;
        }
        mViewPager.setCurrentItem(targetPos, true);
        return isNeedUpdate;

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

    public void onPauseForFragment() {
        for (int key : mPageReferenceMap.keySet()) {
            mPageReferenceMap.get(key).onPause();
        }
    }

    public void onResumeForFragment() {
        for (int key : mPageReferenceMap.keySet()) {
            mPageReferenceMap.get(key).onResume();
        }
    }

    public void onHiddenForFragment(boolean flag) {
        Log.d(TAG, "flag:" + flag);
        for (int key : mPageReferenceMap.keySet()) {
            mPageReferenceMap.get(key).setHidden(flag);
        }
    }

    public void setItemCurseView(ItemCursorView cursorView) {
        this.mCursorView = cursorView;
    }

}

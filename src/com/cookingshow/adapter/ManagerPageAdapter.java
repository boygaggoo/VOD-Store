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
import com.cookingshow.page.PagerDataHelper;
import com.cookingshow.view.ItemCursorView;

public class ManagerPageAdapter extends android.support.v4.app.FragmentStatePagerAdapter
        implements CommonFragment.OnPageDataLoadListener {

    private static final String TAG = "ManagerPageAdapter";

    private List<Integer> mPageIndex = null;
    private static final int MAX_PAGE_INDEX = 400;

    private PagerDataHelper mDataHelper = null;
    private int mCurrentPos = 0;
    private Map<Integer, CommonFragment> mPageReferenceMap = null;
    private ViewPager mViewPager = null;
    private List<Integer> mRefreshIndexs = null;
    private final RecycleBin mRecycleBin;
    private static final int IGNORE_ITEM_VIEW_TYPE = AdapterView.ITEM_VIEW_TYPE_IGNORE;


    public ManagerPageAdapter(FragmentManager fm, PagerDataHelper dataHelper) {
        super(fm);
        mPageIndex = new ArrayList<Integer>();
        mRecycleBin = new RecycleBin();
        mRecycleBin.setViewTypeCount(2);
        for (int i = 0; i <= MAX_PAGE_INDEX; i++) {
            mPageIndex.add(i - MAX_PAGE_INDEX / 2);
        }
        mPageReferenceMap = new HashMap<Integer, CommonFragment>();
        this.mDataHelper = dataHelper;
        mRefreshIndexs = new ArrayList<Integer>();
        mCurrentPos = MAX_PAGE_INDEX / 2;
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
        View view = null;
        int viewType = IGNORE_ITEM_VIEW_TYPE;
        if (object instanceof CommonFragment) {
            viewType = getItemViewType(((CommonFragment)object).getPageInfo());
            if (viewType != IGNORE_ITEM_VIEW_TYPE) {
                view = ((CommonFragment)object).getRecycleView();
            }
        }
        if (view != null && viewType != IGNORE_ITEM_VIEW_TYPE) {
            mRecycleBin.addScrapView(view, position, viewType);
        }
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    public int getItemViewType(PageInfo pageInfo) {
        if (pageInfo == null) {
            return IGNORE_ITEM_VIEW_TYPE;
        }
        String cateName = pageInfo.mFragmentType;
        if (FragmentFactory.USER_VIDEO_LIST_FRAGMENT.equals(cateName)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.i(TAG, "instantiateItem pos: " + position);
        return super.instantiateItem(container, position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        Log.i(TAG, "getItemPosition ");
        if (!(object instanceof CommonFragment)) {
            return POSITION_NONE;
        }
        int pageIndex = ((CommonFragment)object).getPageIndex();
        Log.i(TAG, "pos " + pageIndex);
        if (mRefreshIndexs.contains(pageIndex)) {
            return POSITION_NONE;
        }
        return POSITION_UNCHANGED;
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
        fragment.setItemCursorView(mCursorView);
        mPageReferenceMap.put(position, fragment);

        View view = null;
        int viewType = getItemViewType(pageInfo);
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
            int cIndex = mDataHelper.getCurrentCategoryIndex();
            int refreshIndex = mDataHelper.findCategoryIndexByName(pageInfo.mCategoryName);
            if (refreshIndex != cIndex) {
                setRefreshPageIndexsByCategory(pageInfo.mCategoryName);
            } else {
                int firstPos = findFirstPageByCategory(pageInfo.mCategoryName);
                if (firstPos == mCurrentPos) {
                    setRefreshPageIndexs(firstPos, 1);
                    mRefreshIndexs.remove((Integer)firstPos);
                } else {
                    setRefreshPageIndexs(pos, 0);
                }
            }
            if (isRefreshNow) {
                notifyDataSetChanged();
            }

            /*if (pos == mCurrentPos) {
                setRefreshPageIndexs(pos, 1);
                mRefreshIndexs.remove((Integer)pos);
            } else if (pos > mCurrentPos){
                setRefreshPageIndexs(pos, 1);
                mRefreshIndexs.remove((Integer)pos);
            } else {
                setRefreshPageIndexs(pos, -1);
            }*/

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

    private void setRefreshPageIndexsByCategory(String categoryName) {
        Log.i(TAG, "categoryName:" + categoryName);
        for (int key : mPageReferenceMap.keySet()) {
            CommonFragment fragment = mPageReferenceMap.get(key);
            PageInfo pageInfo = fragment.getPageInfo();
            if (pageInfo != null) {
                String category = pageInfo.mCategoryName;
                if (category.equals(categoryName)) {
                    if (!mRefreshIndexs.contains(key)) {
                        mRefreshIndexs.add(key);
                    }
                }
            }
        }
        Log.i(TAG, mRefreshIndexs.toString());
    }

    private int findFirstPageByCategory(String categoryName) {
        int firstPos = Integer.MAX_VALUE;
        for (int key : mPageReferenceMap.keySet()) {
            CommonFragment fragment = mPageReferenceMap.get(key);
            if (fragment != null) {
                PageInfo pageInfo = fragment.getPageInfo();
                if (pageInfo != null &&
                        pageInfo.mCategoryName.equals(categoryName)) {
                    if (key <= firstPos) {
                        firstPos = key;
                    }
                }
            }
        }
        return firstPos;
    }

    public void refreshAlphaState(int pagePos, boolean isAlpha) {
        CommonFragment fragment = mPageReferenceMap.get(pagePos);
        if (fragment != null) {
            fragment.refreshAlphaState(isAlpha);
        }
    }

    private ItemCursorView mCursorView = null;
    public void setItemCurseView(ItemCursorView cursorView) {
        this.mCursorView = cursorView;
    }

}

package com.cookingshow.category;

import java.util.Comparator;

import android.util.Log;
import android.util.SparseArray;

import com.cookingshow.datacenter.PageContent;
import com.cookingshow.page.FragmentFactory;

public class CategoryInfo {

    public String mCategoryName;
    public int mItemCounts;
    public int mColumns;
    public int mRows;
    public int mPages;
    public int mCurse;
    public SparseArray<PageInfo> mDatas;
    public int ITEMS_PER_PAGE;
    public boolean isInit;
    public Object mOtherData;
    private static final String TAG = "CategoryInfo";

    public CategoryInfo() {
        mPages = 1;
        mCurse = -1;
        mColumns = 6;
        mRows = 2;
        mDatas = new SparseArray<PageInfo>();
        ITEMS_PER_PAGE = mRows * mColumns;
    }

    public void savePageInfo(PageInfo pageInfo) {
        if (pageInfo != null) {
            int pageNum = pageInfo.mPageNum;
            Log.i(TAG, "pagenum:" + pageNum);
            if (pageInfo.mCategoryName.equals(mCategoryName)) {
                mDatas.put(pageNum, pageInfo);
            }
        }
    }

    public boolean initCategoryInfo(int itemCounts) {
        Log.i(TAG, "" + mCategoryName);
        if (!isInit || mItemCounts == 0) {
            isInit = true;
            this.mItemCounts = itemCounts;
            if (itemCounts != 0) {
                this.mPages = mItemCounts % ITEMS_PER_PAGE == 0 ?
                        mItemCounts / ITEMS_PER_PAGE : mItemCounts / ITEMS_PER_PAGE + 1;
            } else {
                this.mPages = 1;
            }
            return true;
        }
        return false;
    }

    public boolean isRefreshPageView(int itemCounts) {
        if (mPages == 1) {
            return false;
        }
        return true;
    }


    public void setmCurse(int curse) {
        this.mCurse = curse;
    }

    public PageInfo getPageInfo(int pageNum) {
        if (pageNum < 0 || pageNum > (mPages - 1)) {
            return null;
        }
        PageInfo pageInfo = mDatas.get(pageNum);
        if (pageInfo == null) {
            pageInfo = new PageInfo();
            pageInfo.mPageNum = pageNum;
            pageInfo.mCategoryName = mCategoryName;
            pageInfo.mStartIndex = pageNum * ITEMS_PER_PAGE;
            pageInfo.mOtherData = mOtherData;
            pageInfo.mFragmentType = getPageFragmentType(pageNum);
            int items = ITEMS_PER_PAGE;
            if (pageNum == (mPages - 1)) {
                items = (mItemCounts % ITEMS_PER_PAGE == 0) ? items: mItemCounts % ITEMS_PER_PAGE;
            }
            pageInfo.mItemCount = items;
        }
        return pageInfo;
    }

    public PageInfo getPageInfoByDiff(int diff) {
        int pageNum = mCurse + diff;
        if (pageNum > (mPages - 1)) {
            return null;
        }
        PageInfo pageInfo = mDatas.get(pageNum);
        if (pageInfo == null) {
            pageInfo = new PageInfo();
            pageInfo.mPageNum = pageNum;
            pageInfo.mCategoryName = mCategoryName;
            pageInfo.mStartIndex = pageNum * ITEMS_PER_PAGE;
            pageInfo.mOtherData = mOtherData;
            pageInfo.mFragmentType = getPageFragmentType(pageNum);
            int items = ITEMS_PER_PAGE;
            if (pageNum == (mPages - 1)) {
                items = (mItemCounts % ITEMS_PER_PAGE == 0) ? items: mItemCounts % ITEMS_PER_PAGE;
            }
            pageInfo.mItemCount = items;
        }
        return pageInfo;
    }

    protected String getPageFragmentType (int pageNum) {
        return FragmentFactory.ALL_LIST_FRAGMENT;
    }

    @Override
    public String toString() {
        return "CategoryInfo{" +
                "mCategoryName='" + mCategoryName + '\'' +
                ", mItemCounts=" + mItemCounts +
                ", mColumns=" + mColumns +
                ", mRows=" + mRows +
                ", mPages=" + mPages +
                ", mCurse=" + mCurse +
                ", mDatas=" + mDatas +
                ", ITEMS_PER_PAGE=" + ITEMS_PER_PAGE +
                ", isInit=" + isInit +
                ", mOtherData=" + mOtherData +
                '}';
    }

    public class SortComparator implements Comparator<PageContent> {

        @Override
        public int compare(PageContent lhs, PageContent rhs) {
            return lhs.getPosition() - rhs.getPosition();
        }
    }
}

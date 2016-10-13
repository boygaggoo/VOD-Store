package com.cookingshow.category;

import android.util.Log;

import com.cookingshow.page.FragmentFactory;

public class UserDiningCategory extends CategoryInfo {

	public UserDiningCategory() {
        super();
        mRows = 3;
        mColumns = 3;
        ITEMS_PER_PAGE = mRows * mColumns;
    }

    @Override
    protected String getPageFragmentType(int pageNum) {
        return FragmentFactory.USER_DINING_LIST_FRAGMENT;
    }

    @Override
    public boolean initCategoryInfo(int itemCounts) {
        boolean flag = false;
        if (!isInit || itemCounts != mItemCounts) {
            flag = true;
            isInit = true;
            if (itemCounts == 0) {
                this.mPages = 1;
            } else {
                this.mPages = itemCounts % ITEMS_PER_PAGE == 0 ?
                        itemCounts / ITEMS_PER_PAGE : itemCounts / ITEMS_PER_PAGE + 1;
            }
        }
        return flag;
    }

    @Override
    public boolean isRefreshPageView(int allCount) {
        boolean flag = true;
        int oldPages;
        if (mItemCounts == 0) {
            oldPages = 1;
        } else {
            oldPages = mItemCounts % ITEMS_PER_PAGE == 0 ?
                    mItemCounts / ITEMS_PER_PAGE : mItemCounts / ITEMS_PER_PAGE + 1;
        }
        if (oldPages == mPages) {
            flag = false;
        } else {
            if (mPages < oldPages) {
                Log.d("pagechanged", "mCurse " + mCurse);
                if (mCurse == (oldPages - 1)) {
                    mCurse = mPages - 1;
                }
            }
        }
        this.mItemCounts = allCount;
        return flag;
    }

}

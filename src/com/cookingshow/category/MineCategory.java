package com.cookingshow.category;

import java.util.List;

import com.cookingshow.page.FragmentFactory;

public class MineCategory extends CategoryInfo{


    public MineCategory() {
        super();
        mRows = 1;
        mColumns = 3;
        ITEMS_PER_PAGE = mRows * mColumns;
    }

    public void initPageInfos(List<Integer> datas) {
        if (datas == null || datas.isEmpty()) {
            this.isInit = true;
            return;
        }
        this.mItemCounts = datas.size();
        int pageNum = 0;
        int dataCnt = mItemCounts;
        int pageItemCount = ITEMS_PER_PAGE;
        PageInfo pageInfo = getNewPageInfo(pageNum);
        pageInfo.mItemCount = pageItemCount;
        for (int i = 0; i< dataCnt; i++) {
            if (pageItemCount <= 0) {
                pageInfo.mAdData = null;
                this.mDatas.put(pageInfo.mPageNum, pageInfo);
                pageItemCount = ITEMS_PER_PAGE;
                pageNum += 1;
                pageInfo = getNewPageInfo(pageNum);
                pageInfo.mItemCount = pageItemCount;
            }
            pageItemCount -= 1;
        }
        this.mDatas.put(pageInfo.mPageNum, pageInfo);
        this.mPages = pageNum + 1;
        this.isInit = true;
    }

    private PageInfo getNewPageInfo(int pageNum) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.isGetSuccess = true;
        pageInfo.mFragmentType = getPageFragmentType(pageNum);
        pageInfo.mCategoryName = this.mCategoryName;
        pageInfo.mPageNum = pageNum;
        return pageInfo;
    }

    @Override
    protected String getPageFragmentType(int pageNum) {
        if (pageNum == 0) {
            return FragmentFactory.MINE_FIRST_FRAGMENT;
        } else {
            return FragmentFactory.MINE_OTHER_FRAGMENT;
        }
    }

    @Override
    public boolean initCategoryInfo(int itemCounts) {
        return true;
    }

    @Override
    public boolean isRefreshPageView(int itemCounts) {
        return false;
    }

}

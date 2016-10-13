package com.cookingshow.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.cookingshow.datacenter.PageContent;
import com.cookingshow.page.FragmentFactory;

public class RecommendCategory extends CategoryInfo{


    public RecommendCategory() {
        super();
        mRows = 2;
        mColumns = 3;
        ITEMS_PER_PAGE = mRows * mColumns;
    }

    public void initPageInfos(List<PageContent> datas) {
        if (datas == null || datas.isEmpty()) {
            this.isInit = true;
            return;
        }
        List<PageContent> sortData = datas;
        Collections.sort(sortData, new SortComparator());
        this.mItemCounts = datas.size();
        int pageNum = 0;
        int dataCnt = mItemCounts;
        int pageItemCount = 5;
        PageInfo pageInfo = getNewPageInfo(pageNum);
        pageInfo.mItemCount = pageItemCount;
        
        Log.i("renwei", "onNextPage " + dataCnt);
        
        List<PageContent> ads = new ArrayList<PageContent>();
//        String adGroupId = "";
        int position = -1;
        for (int i = 0; i< dataCnt; i++) {
//            String tmpGroupId = datas.get(i).getAdGroupId();
            int tmpPosition = datas.get(i).getPosition();
            if (pageItemCount <= 0) {
                pageInfo.mAdData = ads;
                this.mDatas.put(pageInfo.mPageNum, pageInfo);
                pageItemCount = ITEMS_PER_PAGE;
                pageNum += 1;
                pageInfo = getNewPageInfo(pageNum);
                pageInfo.mItemCount = pageItemCount;
                ads = new ArrayList<PageContent>();
            }
            /*if (adGroupId.equals("") || adGroupId.equals(tmpGroupId)) {
//            if (!adGroupId.equals(tmpGroupId)) {
                ads.add(datas.get(i));
                pageItemCount -= 1;
            }
            adGroupId = tmpGroupId;*/

            if (position != tmpPosition) {
                ads.add(datas.get(i));
                pageItemCount -= 1;
            }
            position = tmpPosition;

        }
        pageInfo.mAdData = ads;
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
            return FragmentFactory.RECOMMEND_FIRST_FRAGMENT;
        } else {
            return FragmentFactory.RECOMMEND_OTHER_FRAGMENT;
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

package com.cookingshow.category;

import com.cookingshow.page.FragmentFactory;

public class AllTypeCategory extends CategoryInfo{

    public AllTypeCategory() {
        super();
        mRows = 2;
        mColumns = 4;
        ITEMS_PER_PAGE = mRows * mColumns;
    }

    @Override
    protected String getPageFragmentType(int pageNum) {
        return FragmentFactory.ALL_LIST_FRAGMENT;
    }

}

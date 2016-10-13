package com.cookingshow.page;

import android.os.Bundle;

public class BlankFragment extends CommonFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = getArguments().getInt("pageIndex", -1);
    }

    @Override
    public void onKeyMode(ViewPagerState state) {

    }

    @Override
    public void onAirMouseMode(ViewPagerState state) {

    }

    @Override
    public void onPageScrollStateChanged(ViewPagerState state) {

    }

}
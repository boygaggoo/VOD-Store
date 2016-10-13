package com.cookingshow.page;

public interface IPageViewAware {

    public void onKeyMode(ViewPagerState state);

    public void onAirMouseMode(ViewPagerState state);

    public void onPageScrollStateChanged(ViewPagerState state);

}

package com.cookingshow.datacenter;

import java.util.List;


public class AppListResponse {
    private List<DishDataInfo> mDishDataInfos;
    private boolean mIsSuccess = false;
    private int allCount = 0;
    
	public AppListResponse() {
		// TODO Auto-generated constructor stub
	}

    public void setDishDataInfos(List<DishDataInfo> mDishDataInfos) {
        this.mDishDataInfos = mDishDataInfos;
    }

    public List<DishDataInfo> getDishDataInfos() {
        return mDishDataInfos;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public void setmIsSuccess(boolean mIsSuccess) {
        this.mIsSuccess = mIsSuccess;
    }
    
    public boolean getIsSuccess() {
        return mIsSuccess;
    }
}

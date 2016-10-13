package com.cookingshow.category;

import java.io.Serializable;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.cookingshow.datacenter.DishDataInfo;
import com.cookingshow.datacenter.PageContent;

public class PageInfo implements Serializable, Parcelable{

    public int mPageNum;
    public String mCategoryName;
    public int mStartIndex;
    public int mItemCount;
    public List<DishDataInfo> mPageData;
    public boolean isGetSuccess;
    public Object mOtherData;
    public List<PageContent> mAdData;
    public String mFragmentType = "";


    public PageInfo() {
        isGetSuccess = false;
        mPageData = null;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "mPageNum=" + mPageNum +
                ", mCategoryName='" + mCategoryName + '\'' +
                ", mStartIndex=" + mStartIndex +
                ", mItemCount=" + mItemCount +
                ", mPageData=" + mPageData +
                ", isGetSuccess=" + isGetSuccess +
                ", mOtherData=" + mOtherData +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPageNum);
        dest.writeString(mCategoryName);
        dest.writeInt(mStartIndex);
        dest.writeInt(mItemCount);
        dest.writeList(mPageData);
        if (isGetSuccess) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
    }

    public PageInfo(Parcel in) {
        mPageNum = in.readInt();
        mCategoryName = in.readString();
        mStartIndex = in.readInt();
        mItemCount = in.readInt();
        mPageData = in.readArrayList(DishDataInfo.class.getClassLoader());
        isGetSuccess = true;
        if (in.readInt() == 0) {
            isGetSuccess = false;
        }
        mOtherData = null;
    }


    public static final Parcelable.Creator<PageInfo> CREATOR = new Parcelable.Creator<PageInfo>() {
        public PageInfo createFromParcel(Parcel in) {
            return new PageInfo(in);
        }

        public PageInfo[] newArray(int size) {
            return new PageInfo[size];
        }
    };
}

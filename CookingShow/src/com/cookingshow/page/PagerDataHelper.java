package com.cookingshow.page;

import java.util.List;

import android.util.Log;

import com.cookingshow.category.CategoryInfo;
import com.cookingshow.category.PageInfo;

public class PagerDataHelper {

    private List<CategoryInfo> mCateList;
    private CategoryInfo mCurrentCate;
    private int mCateIndex;
    private static final String TAG = "PagerDataHelper";

    public PagerDataHelper(List<CategoryInfo> cateList) {
        this.mCateList = cateList;
        if (cateList.size() > 0) {
            setCurrentCategory(0);
            setCurrentCategoryCurse(0);
        }
    }
    
    public void resetCurrentCurse() {
        if (mCurrentCate != null) {
            mCurrentCate.mCurse = 0;
        }
    }

    public void setCurrentCategory(String categoryName) {
        for (int i = 0; i < mCateList.size(); i++) {
            if (mCateList.get(i).mCategoryName.equals(categoryName)) {
                if (mCurrentCate != null) {
                    mCurrentCate.mCurse = 0;
                }
                this.mCurrentCate = mCateList.get(i);
                this.mCurrentCate.mCurse = 0;
                this.mCateIndex = i;
                if (mCategoryChangedListener != null) {
                    mCategoryChangedListener.onPageCategoryChanged(mCurrentCate);
                }
                break;
            }
        }
    }

    public boolean isRefreshPageView(PageInfo pageInfo, int allCount) {
        boolean flag = false;
        CategoryInfo categoryInfo = findCategoryByName(pageInfo.mCategoryName);
        if (categoryInfo != null) {
            flag = categoryInfo.initCategoryInfo(allCount);
            categoryInfo.savePageInfo(pageInfo);
            if (flag) {
                flag = categoryInfo.isRefreshPageView(allCount);
                if (categoryInfo.mCategoryName.equals(mCurrentCate.mCategoryName)) {
                    if (mCategoryChangedListener != null) {
                        mCategoryChangedListener.onPageCategoryChanged(mCurrentCate);
                    }
                }
            }
        }
        return flag;
    }

    private CategoryInfo findCategoryByName (String name) {
        CategoryInfo categoryInfo = null;
        for (CategoryInfo info : mCateList) {
            if (info.mCategoryName.equals(name)) {
                categoryInfo = info;
                break;
            }
        }
        return categoryInfo;
    }

    public void updateCurrentState(PageInfo pageInfo) {
        if (!mCurrentCate.mCategoryName.equals(pageInfo.mCategoryName)) {
            int index = findCategoryIndexByName(pageInfo.mCategoryName);
            if (index != -1) {
                if (mCurrentCate != null) {
                    mCurrentCate.mCurse = 0;
                }
                mCurrentCate = mCateList.get(index);
                mCateIndex = index;
                if (mCategoryChangedListener != null) {
                    mCategoryChangedListener.onPageCategoryChanged(mCurrentCate);
                }
            }
        }
        if (mCurrentCate != null) {
            mCurrentCate.mCurse = pageInfo.mPageNum;
        }
    }

    public int findCategoryIndexByName(String cateName) {
        for (int i = 0; i < mCateList.size(); i++) {
            if (cateName.equals(mCateList.get(i).mCategoryName)) {
                return i;
            }
        }
        return -1;
    }

    public int getCurrentCategoryIndex() {
        return mCateIndex;
    }

    public void setCurrentCategory(int index) {
        this.mCurrentCate = mCateList.get(index);
        this.mCateIndex = index;
        this.mCurrentCate.mCurse = 0;
    }


    public void setCurrentCategoryCurse(int curse) {
        mCurrentCate.mCurse = curse;
    }

    public PageInfo getPageData(int diff) {
        if (mCurrentCate == null) {
            return null;
        }
        return findPageInfoByDiff(diff);
    }

    private PageInfo findPageInfoByDiff(int diff) {
        Log.i(TAG, "diff:" + diff);
        CategoryInfo category = mCurrentCate;
        PageInfo pageInfo = null;
        int index = mCateList.indexOf(mCurrentCate);
        int step = 0;
        if (diff < 0) {
            step = -1;
        } else if (diff > 0) {
            step = 1;
        }
        boolean flag = false;
        int targetPageNum = category.mCurse + diff;
        if (targetPageNum > (category.mPages - 1)) {
            targetPageNum = targetPageNum - (category.mPages - 1);
            flag = true;
        } else if (targetPageNum < 0) {
            targetPageNum = Math.abs(targetPageNum);
            flag = true;
        }
        if (flag) {
            index += step;
            if (index > mCateList.size() - 1 || index < 0) {
                category = null;
            } else {
                category = mCateList.get(index);
            }
            while (category != null && targetPageNum > category.mPages) {
                targetPageNum -= category.mPages;
                index += step;
                if (index > mCateList.size() - 1 || index < 0) {
                    category = null;
                } else {
                    category = mCateList.get(index);
                }
            }
            if (category != null) {
                if (step > 0) {
                    pageInfo = category.getPageInfo(targetPageNum - 1);
                } else {
                    pageInfo = category.getPageInfo(category.mPages - targetPageNum);
                }
            }
        } else {
            pageInfo = category.getPageInfoByDiff(diff);
        }
        if (pageInfo != null) {
            Log.i(TAG, "pageinfo: " + pageInfo.toString());
        }
        return pageInfo;
    }

    public boolean isTheLast(int pageNum) {
        if (mCateIndex == mCateList.size() - 1 &&
                pageNum >= (mCurrentCate.mPages - 1)) {
            return true;
        }
        return false;
    }

    public boolean isTheFirst(int pageNum) {
        if (mCateIndex == 0 &&
                pageNum <= 0) {
            return true;
        }
        return false;
    }

    public CategoryInfo getCurrentCate() {
        return mCurrentCate;
    }

    public boolean isTheLastPage() {
        Log.d(TAG, "curse " + mCurrentCate.mCurse + " pages " + mCurrentCate.mPages);
        if (mCateIndex == mCateList.size() - 1 &&
                mCurrentCate.mCurse >= (mCurrentCate.mPages - 1)) {
            return true;
        }
        return false;
    }

    public boolean isTheFirstPage() {
        if (mCateIndex == 0 &&
                mCurrentCate.mCurse <= 0) {
            return true;
        }
        return false;
    }

    public void setOnCategoryChangedListener (OnPageCategoryChangedListener listener) {
        this.mCategoryChangedListener = listener;
    }

    private OnPageCategoryChangedListener mCategoryChangedListener = null;

    public interface OnPageCategoryChangedListener {
        public void onPageCategoryChanged(CategoryInfo category);
    }

}

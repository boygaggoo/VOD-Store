package com.cookingshow.page;

public class ViewPagerState {

    public int mCurPageIndex;
    public int mFocusPosInPage;
    private boolean isFling;
    private int mScrollState;

    public int getCurPageIndex() {
        return mCurPageIndex;
    }

    public void setCurPageIndex(int cPage) {
        this.mCurPageIndex = cPage;
    }

    public int getFocusPosInPage() {
        return mFocusPosInPage;
    }

    public void setFocusPosInPage(int mFocusPosInPage) {
        this.mFocusPosInPage = mFocusPosInPage;
    }

    public boolean isFling() {
        return isFling;
    }

    public void setFling(boolean isFling) {
        this.isFling = isFling;
    }

    public int getScrollState() {
        return mScrollState;
    }

    public void setScrollState(int mScrollState) {
        this.mScrollState = mScrollState;
    }

    @Override
    public String toString() {
        return "ViewPagerState{" +
                "mCurPageIndex=" + mCurPageIndex +
                ", mFocusPosInPage=" + mFocusPosInPage +
                ", isFling=" + isFling +
                ", mScrollState=" + mScrollState +
                '}';
    }
}

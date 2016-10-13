package com.cookingshow.page;

import android.view.View;

public interface IPageNotifyListener {

    public void onNextPage(int cPageIndex);
    public void onBeforePage(int cPageIndex);
    public void onFocusToTop();
    public void onFocusToBottom();
    public void onItemFocusChange(int cPageIndex, int position);
    public void onItemClick(Object object);
}

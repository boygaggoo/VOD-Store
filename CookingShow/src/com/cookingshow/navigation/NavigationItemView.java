package com.cookingshow.navigation;

import com.cookingshow.view.CommonRelativeLayout;

import android.content.Context;
import android.util.AttributeSet;

public abstract class NavigationItemView extends CommonRelativeLayout {

    public NavigationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void setItemTitle(String text);

    public void setItemIcon(int rid) {

    }

    public boolean onFocusDown() {
        if (mNaviBarListener != null) {
            return mNaviBarListener.onFocusDown();
        }
        return false;
    }

    public boolean onFocusUp() {
        if (mNaviBarListener != null) {
            return mNaviBarListener.onFocusUp();
        }
        return false;
    }

    protected OnNavigationItemFocusListener mNaviBarListener = null;

    public void setOnNavigationItemListener(OnNavigationItemFocusListener listener) {
        this.mNaviBarListener = listener;
    }

    public interface OnNavigationItemFocusListener {
        public boolean onFocusDown();
        public boolean onFocusUp();
    }

}

package com.cookingshow.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.cookingshow.R;

public class UserInfoNavigationItem extends NavigationItemView {

    private TextView mNaviName;
    private TextView mNaviNum = null;

    public UserInfoNavigationItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setItemTitle(String text) {
        mNaviName.setText(text);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNaviName = (TextView) findViewById(R.id.navi_name);
        mNaviNum = (TextView) findViewById(R.id.navi_num);
        this.setFocusable(true);
        this.setClickable(true);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }

    public void updateNum(int num) {
        if (num <= 0) {
            mNaviNum.setVisibility(View.INVISIBLE);
        } else {
            mNaviNum.setText(String.valueOf(num));
            mNaviNum.setVisibility(View.VISIBLE);
        }
    }

}

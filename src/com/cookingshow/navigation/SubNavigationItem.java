package com.cookingshow.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cookingshow.R;

public class SubNavigationItem extends NavigationItemView{

    private TextView mNaviName;

    public SubNavigationItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setItemTitle(String text) {
        mNaviName.setText(text);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNaviName = (TextView) findViewById(R.id.sub_navi_name);
    }

}

package com.cookingshow.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cookingshow.R;

public class MainNavigationItem extends NavigationItemView {

    private TextView mNaviName;
    private TextView mNaviIcon;

    public MainNavigationItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setItemTitle(String text) {
        mNaviName.setText(text);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initWidget();
    }

    private void initWidget() {
        mNaviName = (TextView) findViewById(R.id.navi_name);
        mNaviIcon = (TextView) findViewById(R.id.navi_icon);
    }

    @Override
    public void setItemIcon(int rid) {
//        mNaviIcon.setImageResource(rid);
        mNaviIcon.setBackgroundResource(rid);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
/*        if (selected) {
            mNaviName.setTextSize(60);
        } else {
            mNaviName.setTextSize(48);
        }*/
        if (selected) {
/*            ObjectAnimator animX = ObjectAnimator.ofFloat(mNaviName, "textSize", 60f);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.playTogether(animX);
            animSetXY.setDuration(500);
            animSetXY.start();*/
//            mNaviName.setTextSize(60);
        } else {
//            mNaviName.setTextSize(48);
        }

    }

}

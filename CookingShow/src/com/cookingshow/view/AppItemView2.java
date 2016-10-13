package com.cookingshow.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cookingshow.R;
import com.cookingshow.datacenter.DishDataInfo;
import com.cookingshow.page.ActionEventMgr;
import com.cookingshow.page.DisplayImageMgr;

public class AppItemView2 extends CommonRelativeLayout {

    private static final String TAG = "AppItemView2";
    private static final String LOCATION = "http://182.92.198.90";
    public RoundedImageView appIcon;
    public TextView appName;
    public TextView appLike;
    public TextView appView;
    public ImageView appChoice;
    public View iconLayout;
    private AnimationSet mFocusAnimatorSet = null;
    private AnimationSet mNoFocusAnimatorSet = null;
    public String iconUrl = null;

    public AppItemView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initContentView(context);
    }

    public AppItemView2(Context context) {
        super(context);
        initContentView(context);
    }

    protected void initHoverListener() {
        this.setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        ActionEventMgr.getInstance().setFocusOrHoverView(AppItemView2.this);
                        showOnFocusAnimation();
                        if (getOnFocusChangeListener() != null) {
                            getOnFocusChangeListener().onFocusChange(AppItemView2.this, true);
                        }
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        showNoFocusAnimation();
                        if (getOnFocusChangeListener() != null) {
                            getOnFocusChangeListener().onFocusChange(AppItemView2.this, false);
                        }
                        break;
                }
                return false;
            }
        });
    }


    private void initContentView (Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.applist_item2, this);
        appIcon = (RoundedImageView) view.findViewById(R.id.app_icon);
        appName = (TextView) view.findViewById(R.id.app_name);
        appLike = (TextView) view.findViewById(R.id.app_like);
        appView = (TextView) view.findViewById(R.id.app_view);
        appChoice = (ImageView) view.findViewById(R.id.flag_choice);
        iconLayout = appIcon;
        this.setClipChildren(false);
        this.setClickable(true);
        this.setFocusable(true);
    }

    public void refreshData(DishDataInfo app) {
        if (app != null) {
            appName.setText(app.getTitle());
            String newUrl = LOCATION + app.getThumbUrl();
            if (iconUrl != null && !newUrl.equals(iconUrl)) {
                appIcon.clearUrl();
            }
            iconUrl = newUrl;
            appLike.setText(getContext().getString(R.string.thumber) + app.getLikeTimes());
            appView.setText(getContext().getString(R.string.view_times) + app.getViewTimes());
        } else {
        	DisplayImageMgr.getInstance().setDefaultBitmap(appIcon);
            appName.setText(R.string.list_app_loading_text);
        }
    }

    private String getResourceStr(int rid, String str) {
        String ret = "";
        if (!TextUtils.isEmpty(str)) {
            ret = getContext().getString(rid, str);
        }
        return ret;
    }

    public void resetToDefaultValue() {
        appName.setText(R.string.list_app_loading_text);
        appLike.setText("");
        appView.setText("");
        appIcon.clearUrl();
        iconUrl = null;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            showOnFocusAnimation();
        } else {
            showNoFocusAnimation();
        }
    }

    public void showOnFocusAnimation() {
        if (mFocusAnimatorSet == null) {
            mFocusAnimatorSet = new AnimationSet(true);
            ScaleAnimation scaleAnimation1 = new ScaleAnimation(1, 1.1f, 1, 1.1f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation1.setDuration(150);
            mFocusAnimatorSet.addAnimation(scaleAnimation1);
            mFocusAnimatorSet.setFillAfter(true);
            mFocusAnimatorSet.setFillBefore(false);
        }
        iconLayout.startAnimation(mFocusAnimatorSet);
    }

    public void showNoFocusAnimation() {
        if (mNoFocusAnimatorSet == null) {
            mNoFocusAnimatorSet = new AnimationSet(true);
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(30);
            mNoFocusAnimatorSet.addAnimation(scaleAnimation);
            mNoFocusAnimatorSet.setFillAfter(false);
            mNoFocusAnimatorSet.setFillBefore(true);
        }
        if (mFocusAnimatorSet != null && mFocusAnimatorSet.hasStarted()) {
            mFocusAnimatorSet.cancel();
        }
        iconLayout.startAnimation(mNoFocusAnimatorSet);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.d("test", "end " + packageName);
//        int height = getMeasuredHeight();
//        int width = getMeasuredWidth();
//        setMeasuredDimension(width, height);
    }
    
    public void setChioce() {
    	appChoice.setVisibility(View.VISIBLE);
    }
    
    public void resetChioce() {
    	appChoice.setVisibility(View.GONE);
    }

    public int getChioceStatue() {
    	return appChoice.getVisibility();
    }
}

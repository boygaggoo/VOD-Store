package com.cookingshow.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cookingshow.R;
import com.cookingshow.datacenter.DishDataInfo;
import com.cookingshow.page.ActionEventMgr;
import com.cookingshow.page.DisplayImageMgr;

public class AppItemView extends CommonRelativeLayout {

    private static final String TAG = "AppItemView";
    private static final String LOCATION = "http://182.92.198.90";
    public RoundedImageView appIcon;
    public TextView appName;
    public TextView uploadTime;
    public ImageView newFlagView;
    public View iconLayout;
    public boolean isNewApp;
    private AnimatorSet mAnimatorSet;
    private boolean isShowNewFlag = true;
    private static final int NEW_DURATION_DAY = 6;
    private AnimationSet mFocusAnimatorSet = null;
    private AnimationSet mNoFocusAnimatorSet = null;
    public String iconUrl = null;

    public AppItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initContentView(context);
    }

    public AppItemView(Context context) {
        super(context);
        initContentView(context);
    }

    public void setIsShowNewFlag(boolean isShow) {
        this.isShowNewFlag = isShow;
    }

    protected void initHoverListener() {
        this.setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        ActionEventMgr.getInstance().setFocusOrHoverView(AppItemView.this);
                        showOnFocusAnimation();
                        if (getOnFocusChangeListener() != null) {
                            getOnFocusChangeListener().onFocusChange(AppItemView.this, true);
                        }
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        showNoFocusAnimation();
                        if (getOnFocusChangeListener() != null) {
                            getOnFocusChangeListener().onFocusChange(AppItemView.this, false);
                        }
                        break;
                }
                return false;
            }
        });
    }


    private void initContentView (Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.alllist_item_layout, this);
        appIcon = (RoundedImageView) view.findViewById(R.id.app_icon);
        appName = (TextView) view.findViewById(R.id.app_name);
        uploadTime = (TextView) view.findViewById(R.id.app_upload_time);
        iconLayout = view.findViewById(R.id.icon_layout);
        newFlagView = (ImageView) view.findViewById(R.id.flag_new);
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
            uploadTime.setText(String.valueOf(app.getUploadTime()));
            
            if (isShowNewFlag && isNewFlagByDate(app)) {
                newFlagView.setVisibility(View.VISIBLE);
            } else {
                newFlagView.setVisibility(View.GONE);
            }
        } else {
            DisplayImageMgr.getInstance().setDefaultBitmap(appIcon);
            appName.setText(R.string.list_app_loading_text);
            uploadTime.setText("");
        }
    }

    private boolean isNewFlagByDate(DishDataInfo app) {
        String upload = app.getUploadTime();

        if (TextUtils.isEmpty(upload)) {
            return false;
        }
        try {
        	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
            Date d = new Date(System.currentTimeMillis());
            String current = df.format(d);
            
            Date d1 = df.parse(upload);
            Date d2 = df.parse(current);
            
            long diff = d2.getTime() - d1.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            //Log.i(TAG, "diff days " + diffDays);
            if (diffDays <= NEW_DURATION_DAY) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void resetToDefaultValue() {
        appName.setText(R.string.list_app_loading_text);
        uploadTime.setText("");
        appIcon.clearUrl();
        iconUrl = null;

        if (newFlagView.getVisibility() == View.VISIBLE) {
            newFlagView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
//            startAnimation();
            showOnFocusAnimation();
        } else {
            showNoFocusAnimation();
//            reverseAnimation();
        }
    }

    public void showOnFocusAnimation() {
        //View parentView = (View) view.getParent();// 解决遮盖问题
//        parentView.requestLayout();
//        parentView.invalidate();
//        view.bringToFront();
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


    private void startAnimation() {
        if (mAnimatorSet == null) {
            bindAnimations();
        } else {
            mAnimatorSet.cancel();
        }
/*        View parentView = (View) this.getParent();// 解决遮盖问题
        parentView.requestLayout();
        parentView.invalidate();
        this.bringToFront();*/
        mAnimatorSet.start();
    }

    private void reverseAnimation() {
        if (mAnimatorSet == null) {
            return;
        }

        mAnimatorSet.cancel();
        for (Animator animator : mAnimatorSet.getChildAnimations()) {
            ValueAnimator valueAnimator = (ValueAnimator) animator;
            valueAnimator.reverse();
        }
    }

    private void bindAnimations() {
        mAnimatorSet = new AnimatorSet();
        List<ValueAnimator> animators = getAnimators(iconLayout);
        for (ValueAnimator animator : animators) {
            mAnimatorSet.play(animator);
        }
    }

    private List<ValueAnimator> getAnimators(View view) {
        ScaleAnimatorModel scaleAnimatorModel = new ScaleAnimatorModel(view);
        scaleAnimatorModel.setInterpolator(new AccelerateInterpolator());
        scaleAnimatorModel.setDuration(200);
        scaleAnimatorModel.setStartDelay(0);
        scaleAnimatorModel.setPivotX(view.getWidth() / 2);
        scaleAnimatorModel.setPivotY(view.getHeight() / 2);
        scaleAnimatorModel.setScale(1.1f);
        return scaleAnimatorModel.toAnimators();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int height = getMeasuredHeight();
//        int width = getMeasuredWidth();
//        setMeasuredDimension(width, height);
    }

}

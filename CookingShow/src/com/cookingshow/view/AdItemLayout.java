package com.cookingshow.view;

import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
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
import com.cookingshow.page.ActionEventMgr;

public class AdItemLayout extends CommonRelativeLayout{

    private static final String TAG = "AdItemLayout";
    private int mAdType;
    private ImageView mAdImg = null;
    private TextView mAdName = null;
    private TextView mAdSubName = null;
    private View mTextLayout = null;
    private TextView mAdOrder = null;
    private AnimatorSet mAnimatorSet;
    private AnimationSet mFocusAnimatorSet = null;
    private AnimationSet mNoFocusAnimatorSet = null;

    public AdItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributeSet(context, attrs);
    }

    public AdItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public AdItemLayout(Context context) {
        super(context);
    }

    protected void initHoverListener() {
        this.setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        ActionEventMgr.getInstance().setFocusOrHoverView(AdItemLayout.this);
                        showOnFocusAnimation();
                        if (getOnFocusChangeListener() != null) {
                            getOnFocusChangeListener().onFocusChange(AdItemLayout.this, true);
                        }
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        showNoFocusAnimation();
                        if (getOnFocusChangeListener() != null) {
                            getOnFocusChangeListener().onFocusChange(AdItemLayout.this, false);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AdItemLayout);
        mAdType = a.getInt(R.styleable.AdItemLayout_adType, 0);
        a.recycle();
        initContentView();
        this.setVisibility(View.GONE);
        this.setFocusable(true);
        this.setClickable(true);
    }

    private void initContentView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.ad_item, this, true);
        mAdImg = (ImageView) view.findViewById(R.id.ad_img);
        switch (mAdType) {
            case 0:
                mTextLayout = view.findViewById(R.id.ad_index_type1);
                mAdName = (TextView) view.findViewById(R.id.ad_type1_name);
                mAdSubName = (TextView) view.findViewById(R.id.ad_type1_subname);
                break;

            case 1:
                mAdName = (TextView) view.findViewById(R.id.ad_index_type2);
                mTextLayout = mAdName;
//                mAdSubName = (TextView) view.findViewById(R.id.ad_type2_subname);
                break;

            case 2:
                mAdName = (TextView) view.findViewById(R.id.ad_top_text);
                mTextLayout = mAdName;
                mAdOrder = (TextView) view.findViewById(R.id.ad_top_order);
                mAdOrder.setVisibility(View.VISIBLE);
                break;
        }
        mTextLayout.setVisibility(View.VISIBLE);
    }

    public void setAdName(String adName) {
        this.mAdName.setText(adName);
    }

    public void setAdSubName(String adSubName) {
        if (mAdSubName != null) {
            this.mAdSubName.setText(adSubName);
        }
    }

    public ImageView getAdImageView() {
        return mAdImg;
    }

    public void setAdOrderText(String order) {
        this.mAdOrder.setText(order);
    }

    public void setAdOrderTextColor(int color) {
        this.mAdOrder.setTextColor(color);
    }

    public void setAdOrderTextSize(float size) {
        this.mAdOrder.setTextSize(size);
    }

    public void setAdOderY(int y) {
        if (mAdOrder != null) {
            this.mAdOrder.setY(y);
        }
    }

    public void setAdOderBg(int id) {
        if (id == -1) {
            this.mAdOrder.setBackgroundDrawable(null);
            return ;
        }
        if (mAdOrder != null) {
            this.mAdOrder.setBackgroundResource(id);
        }
    }


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            showOnFocusAnimation();
//            showOnFocusImageAnimation(mAdImg);
//            startAnimation();
        } else {
//            reverseAnimation();
            showNoFocusAnimation();
//            showNoFocusImageAnimation(mAdImg);
        }
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
        List<ValueAnimator> animators = getAnimators(this);
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
        this.startAnimation(mFocusAnimatorSet);
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
        this.startAnimation(mNoFocusAnimatorSet);
    }

    private void showOnFocusImageAnimation(View view) {
        ScaleAnimation scaleAnimation1 = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.15f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f);

        AnimationSet animationSet = new AnimationSet(true);
        scaleAnimation1.setDuration(200);
        animationSet.addAnimation(scaleAnimation1);
        animationSet.setFillAfter(true);
        animationSet.setFillBefore(false);
        view.startAnimation(animationSet);
    }

    private void showNoFocusImageAnimation(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, 1.15f, 1.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f);

        AnimationSet animationSet = new AnimationSet(true);
        scaleAnimation.setDuration(50);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setFillAfter(false);
        animationSet.setFillBefore(true);
        view.startAnimation(animationSet);
    }


}

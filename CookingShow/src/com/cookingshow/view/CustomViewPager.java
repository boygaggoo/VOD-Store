package com.cookingshow.view;

import java.lang.reflect.Field;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

public class CustomViewPager extends ViewPager{

    private static final String TAG = "CustomViewPager";
    private CustomDurationScroller mCustomScroller;
    private ScrollAnimatorListenerAdapter mAnimListener = null;
    protected long mMotionBeginTime;
    protected float mLastMotionX;
    private float mLastMotionY;
    private long DURATION_PER_PAGE = 230;
    private OnPageChangeListener mOutPageChangeListener = null;
    private boolean isByAnimalToScroll = true;
    private int mToItemPos;
    private View mParent = null;
    private boolean isAnimating = false;
    private boolean isViewPagerScrolling = false;
    private static final int MSG_OPEN_HARDWARE = 1;
    private static final int MSG_CLOSE_HARDWARE = 0;
    private static final int MSG_CAN_SCROLL = 2;

    private Handler msgHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CLOSE_HARDWARE:
                    if (mParent != null && !msgHandler.hasMessages(MSG_OPEN_HARDWARE)) {
                        mParent.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                        refreshShadow();
                    }
                    break;
                case MSG_OPEN_HARDWARE:
                    if (mParent != null) {
                        mParent.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }
                    break;
                case MSG_CAN_SCROLL:
                    isViewPagerScrolling = false;
                    break;
            }
            return false;
        }
    });

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initParams();
    }

    private void init() {
        mAnimListener = new ScrollAnimatorListenerAdapter();
        setMotionX(0);
        setViewPagerScroller();
        this.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mOutPageChangeListener != null) {
                    mOutPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
                refreshShadow();
            }

            @Override
            public void onPageSelected(int position) {
                if (mOutPageChangeListener != null) {
                    mOutPageChangeListener.onPageSelected(position);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_DRAGGING) {
                    isViewPagerScrolling = true;
                }
                if (mOutPageChangeListener != null) {
                    mOutPageChangeListener.onPageScrollStateChanged(state);
                }
                if (state == SCROLL_STATE_DRAGGING) {
//                    onOpenHDForDeviceCompatible();
                }
                if (state == SCROLL_STATE_IDLE) {
                    msgHandler.sendEmptyMessageDelayed(MSG_CAN_SCROLL, 100);
                }
            }
        });
    }

    private void initParams() {
//        setDrawingCacheEnabled(true);
        setPersistentDrawingCache(ViewPager.PERSISTENT_ANIMATION_CACHE);
    }

    public void setParentView(View view) {
        this.mParent = view;
    }

    public void refreshShadow() {
        if (mParent != null) {
            mParent.invalidate(0, 0, 120, mParent.getHeight());
            mParent.invalidate(mParent.getWidth() - 120, 0, mParent.getWidth(), mParent.getHeight());
        }
    }

    private void setViewPagerScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            mCustomScroller = new CustomDurationScroller(getContext(), null);
/*            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);
            mCustomScroller = new CustomDurationScroller(getContext(), (Interpolator)interpolatorField.get(null));*/
            mCustomScroller.setScrollDurationTime(300);
            mCustomScroller.setScrollDurationFactor(1.0);
            scrollerField.set(this, mCustomScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (isByAnimalToScroll) {
            int diff = item - getCurrentItem();
            if (diff == 0 || Math.abs(diff) > 2) {
                super.setCurrentItem(item, smoothScroll);
            } else {
                animScrollToDiff(diff);
            }
        } else {
            int diff = Math.abs(getCurrentItem() - item);
            double factor = 1.0;
            if (diff != 0 && diff > 1) {
                factor = 1.0 + 1.0 / diff;
            }
            setDurationFactor(factor);
            super.setCurrentItem(item, smoothScroll);
        }
    }

    public void setDurationFactor (double factor) {
        mCustomScroller.setScrollDurationFactor(factor);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isViewPagerScrolling) {
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (isViewPagerScrolling && event.getKeyCode() != KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void setCustomOnPageChangeListener(OnPageChangeListener listener) {
        this.mOutPageChangeListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void animScrollToDiff(int diff) {
        Log.d(TAG, "diff " + diff);
        if (!isViewPagerScrolling) {
            mToItemPos = getCurrentItem() + diff;
            int beginX;
            int beginY;
            int endX;
            long duration;
            if (diff > 0) {
                beginX = getRight() - 300;
                beginY = getHeight() / 2;
                endX = beginX - (getWidth() - 300) * diff;
                duration = diff * DURATION_PER_PAGE;
            } else {
                beginX = getLeft() + 200;
                beginY = getHeight() / 2;
                endX = beginX - (getWidth() - 200) * diff;
                duration = - diff * DURATION_PER_PAGE;
            }
            animalAction(beginX, endX, beginY, duration);
        }
    }

    private void animalAction(int beginX, int endX, int beginY, long duration) {
        Log.d(TAG, beginX + "," + endX + "," + beginY + "," + duration);
        mLastMotionX = beginX;
        mLastMotionY = beginY;
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "motionX", beginX, endX);
        anim.setInterpolator(new LinearInterpolator());
        anim.addListener(mAnimListener);
        anim.setDuration(duration);
        anim.start();
    }

    public void setMotionX(float motionX) {
//        Log.d(TAG, "motionx " + motionX);
        if (!isAnimating) {
            return;
        }
        mLastMotionX = motionX;
        final long time = SystemClock.uptimeMillis();
        simulate(MotionEvent.ACTION_MOVE, mMotionBeginTime, time);
    }

    private void simulate(int action, long startTime, long endTime) {
        // specify the property for the two touch points
        MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[1];
        MotionEvent.PointerProperties pp1 = new MotionEvent.PointerProperties();
        pp1.id = 0;
        pp1.toolType = MotionEvent.TOOL_TYPE_FINGER;
        properties[0] = pp1;

        // specify the coordinations of the two touch points
        // NOTE: you MUST set the pressure and size value, or it doesn't work
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[1];
        MotionEvent.PointerCoords pc1 = new MotionEvent.PointerCoords();
        pc1.x = mLastMotionX;
        pc1.y = mLastMotionY;
        pc1.pressure = 1;
        pc1.size = 1;
        pointerCoords[0] = pc1;

        MotionEvent ev = MotionEvent.obtain(
                startTime, endTime, action, 1, properties,
                pointerCoords, 0,  0, 1, 1, 0, 0, 0, 0 );

//        Log.d(TAG, "action " + action + " x:" + mLastMotionX);
//        dispatchTouchEvent(ev);
        onTouchEvent(ev);
        if (ev != null) {
            ev.recycle();
        }
    }

    class ScrollAnimatorListenerAdapter extends AnimatorListenerAdapter {

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            Log.d(TAG, "to pos " + mToItemPos);
            final long time = SystemClock.uptimeMillis();
            simulate(MotionEvent.ACTION_UP, mMotionBeginTime, time);
            CustomViewPager.this.setCurrentItem(mToItemPos);
            isAnimating = false;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationStart(Animator animation) {
            isAnimating = true;
            final long time = SystemClock.uptimeMillis();
            simulate(MotionEvent.ACTION_DOWN, time, time);
            mMotionBeginTime = time;
        }

    }



    public class CustomDurationScroller extends Scroller {

        private double scrollFactor = 1;
        private int mDuration;

        public CustomDurationScroller(Context context) {
            super(context);
        }

        public CustomDurationScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        /**
         * not exist in android 2.3
         *
         * @param context
         * @param interpolator
         * @param flywheel
         */
        @SuppressLint("NewApi")
        public CustomDurationScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        /**
         * Set the factor by which the duration will change
         */
        public void setScrollDurationFactor(double scrollFactor) {
            this.scrollFactor = scrollFactor;
        }

        public void setScrollDurationTime(int durationTime) {
            this.mDuration = durationTime;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, (int) (mDuration * scrollFactor));
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        msgHandler.removeCallbacksAndMessages(null);
    }
}

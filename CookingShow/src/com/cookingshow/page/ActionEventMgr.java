package com.cookingshow.page;

import java.util.ArrayList;
import java.util.List;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cookingshow.view.AdItemLayout;
import com.cookingshow.view.AppItemView;

public class ActionEventMgr {

    private static final String TAG = "ActionEventMgr";

    private List<IPageViewAware> observers = null;
    private static ActionEventMgr instance = null;
    private IPageViewAware currentPage = null;
    private Boolean isInAirMode = false;
    private View mLastFocusOrHoverView = null;

    private ActionEventMgr() {
        observers = new ArrayList<IPageViewAware>();
    }

    public static ActionEventMgr getInstance() {
        if (instance == null) {
            instance = new ActionEventMgr();
        }
        return instance;
    }

    public void addObserver(IPageViewAware observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public boolean removeObserver(IPageViewAware observer) {
        return observers.remove(observer);
    }

    public void clearObservers() {
        observers.clear();
    }

    public void notifyOnRefresh(IPageViewAware pageViewAware) {
        for (IPageViewAware observer : observers) {
            if (pageViewAware == observer) {
//                ((IPageViewAware)fragment).onKeyMode();
            }
        }
    }

    public void notifyOnKeyMode(IPageViewAware pageViewAware, ViewPagerState state) {
        for (IPageViewAware observer : observers) {
            if (pageViewAware == observer) {
                observer.onKeyMode(state);
                break;
            }
        }
    }

    public void notifyOnKeyMode(ViewPagerState state) {
        Log.d(TAG, "aware size " + observers.size());
        for (IPageViewAware observer : observers) {
            observer.onKeyMode(state);
        }
    }

    public void notifyOnScrollState(ViewPagerState state) {
        for (IPageViewAware observer : observers) {
            observer.onPageScrollStateChanged(state);
        }
    }

    public boolean isInAirMode() {
        return isInAirMode;
    }

    public synchronized void setInAirMode(boolean isInAirMode) {
        this.isInAirMode = isInAirMode;
    }

    public void setFocusOrHoverView(View view) {
        this.mLastFocusOrHoverView = view;
    }

    public void switchAirMode() {
        /*if (!DeviceCompatible.isK82orA21Device()) {
            synchronized (isInAirMode) {
                isInAirMode = true;
                simulateOnTouch();
            }
        }*/
    }

    public boolean switchKeyMode() {
        synchronized (isInAirMode) {
            if (isInAirMode) {
                isInAirMode = false;
                if (mLastFocusOrHoverView != null) {
                    mLastFocusOrHoverView.setHovered(false);
//                    mLastFocusOrHoverView.requestFocusFromTouch();
//                    mLastFocusOrHoverView.requestFocus();
                    unHoverdForView();
                    return true;
                }
            }
        }
        return false;
    }

    private void unHoverdForView() {
        if (mLastFocusOrHoverView instanceof AdItemLayout) {
            ((AdItemLayout)mLastFocusOrHoverView).showNoFocusAnimation();
        } else if (mLastFocusOrHoverView instanceof AppItemView) {
            ((AppItemView)mLastFocusOrHoverView).showNoFocusAnimation();
        } 

        if (mLastFocusOrHoverView.getOnFocusChangeListener() != null) {
            mLastFocusOrHoverView.getOnFocusChangeListener().onFocusChange(mLastFocusOrHoverView, false);
        }
    }

    protected void simulateOnTouch() {
        Log.d(TAG, "touch");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MotionEvent meDown = null;
                MotionEvent meUp = null;
                try {
                    Instrumentation inst=new Instrumentation();
                    meDown = MotionEvent.obtain(SystemClock.uptimeMillis(),
                            SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
                            30, 30, 0);
                    inst.sendPointerSync(meDown);
                    meUp = MotionEvent.obtain(SystemClock.uptimeMillis(),
                            SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
                            30, 30, 0);
                    inst.sendPointerSync(meUp);
                } catch (SecurityException e) {
                    setInAirMode(false);
                } finally {
                    if (meDown != null) {
                        meDown.recycle();
                    }
                    if (meUp != null) {
                        meUp.recycle();
                    }
                }
            }
        }).start();

    }

    public void setOnHoveredListener(View view) {
        view.setOnHoverListener(mOnHoverListener);
    }

    private View.OnHoverListener mOnHoverListener = new View.OnHoverListener() {
        @Override
        public boolean onHover(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_HOVER_ENTER:
                    mLastFocusOrHoverView = v;
                    break;
            }
            return false;
        }
    };

}

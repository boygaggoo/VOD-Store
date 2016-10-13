package com.cookingshow.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridLayout;

public class AppGridLayout extends GridLayout {

    private static final String TAG = "AppGridLayout";
    private ItemOnBoundaryListener mItemBoundaryListener = null;
    private View mDownView;

    public AppGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
//        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        View focusView = getFocusedChild();
        if (action == KeyEvent.ACTION_DOWN) {
            if (focusView != null) {
                mDownView = focusView;
            }
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (isItemInLeft()) {
                    if (KeyEvent.ACTION_UP == action && mDownView == focusView) {
                        if (mItemBoundaryListener != null && mItemBoundaryListener.focusOnBoundaryLeft()) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(isItemInRight()) {
                    if (KeyEvent.ACTION_UP == action && mDownView == focusView) {
                        if (mItemBoundaryListener != null && mItemBoundaryListener.focusOnBoundaryRight()) {
                            return true;
                        } else {
                            return super.dispatchKeyEvent(event);
                        }
                    }
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_DPAD_UP:
                if (isItemInTop()) {
                    if (KeyEvent.ACTION_DOWN == action) {
                        if (mItemBoundaryListener != null && mItemBoundaryListener.focusOnBoundaryTop()) {
                            return true;
                        } else {
                            return super.dispatchKeyEvent(event);
                        }
                    }
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                if(isItemInBottom()) {
                    if (KeyEvent.ACTION_UP == action && mDownView == focusView) {
                        if (mItemBoundaryListener != null && mItemBoundaryListener.focusOnBoundaryBottom()) {
                            return true;
                        } else {
                            return super.dispatchKeyEvent(event);
                        }
                    }
                    return true;
                }
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public View focusSearch(int direction) {
        Log.i(TAG, "focusSearch " + direction);
        return super.focusSearch(direction);
    }

    protected boolean isItemInTop () {
        View view = getFocusedChild();
        if (view != null && view.getTag() != null) {
            int pos = Integer.valueOf(view.getTag().toString());
            if (getOrientation() == VERTICAL) {
                int rowCnt = getRowCount();
                if (pos % rowCnt == 0) {
                    return true;
                }
            } else {
                int colCnt = getColumnCount();
                if (pos < colCnt) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isItemInRight() {
        View view = getFocusedChild();
        if (view != null && view.getTag() != null) {
            int pos = Integer.valueOf(view.getTag().toString());
            int childCnt = getVisibleChildCount();
            if (getOrientation() == VERTICAL) {
                int rowCnt = getRowCount();
                if (pos + rowCnt >= childCnt) {
                    return true;
                }
            } else {
                int colCnt = getColumnCount();
                if ((pos + 1) % colCnt == 0 || pos == (childCnt -1)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isItemInLeft() {
        View view = getFocusedChild();
        if (view != null && view.getTag() != null) {
            int pos = Integer.valueOf(view.getTag().toString());
            if (getOrientation() == VERTICAL) {
                int rowCnt = getRowCount();
                if (pos < rowCnt) {
                    return true;
                }
            } else {
                int colCnt = getColumnCount();
                if (pos % colCnt == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isItemInBottom() {
        View view = getFocusedChild();
        if (view != null && view.getTag() != null) {
            int pos = Integer.valueOf(view.getTag().toString());
            if (getOrientation() == VERTICAL) {

            } else {
                int rowCnt = getRowCount();
                int colCnt = getColumnCount();
                if (pos / colCnt == (rowCnt -1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setItemBoundaryListener(ItemOnBoundaryListener mItemBoundaryListener) {
        this.mItemBoundaryListener = mItemBoundaryListener;
    }

    public interface ItemOnBoundaryListener {
        public boolean focusOnBoundaryTop();
        public boolean focusOnBoundaryRight();
        public boolean focusOnBoundaryLeft();
        public boolean focusOnBoundaryBottom();
    }

    public void setVisibleChildCount(int cnt) {
        this.mVisibleChildCount = cnt;
    }

    private int mVisibleChildCount = -1;

    public int getVisibleChildCount() {
        if (mVisibleChildCount == -1) {
            return getChildCount();
        } else {
            return mVisibleChildCount;
        }
    }

/*    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        Log.d(TAG, "childcount " + childCount + " i " + i);
        View view = getFocusedChild();
        if (view != null && view.getTag() != null) {
            int pos = Integer.valueOf(view.getTag().toString());
            int position = pos - 0;
            if(position<0){
                return i;
            }else{
                if (i == childCount - 1) {//ËøôÊòØÊú?êé‰∏?∏™Èú?¶ÅÂà∑Êñ∞ÁöÑitem
                    if (position > i) {
                        position = i;
                    }
                    return position;
                }
                if (i == position) {//ËøôÊòØÂéüÊú¨Ë¶ÅÂú®Êú?êé‰∏?∏™Âà∑Êñ∞ÁöÑitem
                    return childCount - 1;
                }
            }
            return i;
        }
        return super.getChildDrawingOrder(childCount, i);
    }*/
}

package com.cookingshow.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cookingshow.adapter.BaseListAdapter;

public class AppListView extends FrameLayout {

    private static final String TAG = "AppListView";
    private AppGridLayout.ItemOnBoundaryListener mItemBoundaryListener = null;
    private View mDownView;
    private int orientation = 0;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private static final int INDEX_TAG = 0x04 << 24;

    private BaseListAdapter<?> mBlockListAdapter;

    private LayoutInflater mLayoutInflater;

    private OnItemClickListener mOnItemClickListener;

    public AppListView(Context context) {
        this(context, null, 0);
    }

    public AppListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setAdapter(BaseListAdapter<?> adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("adapter should not be null");
        }
        mBlockListAdapter = adapter;
        adapter.registerView(this);
    }

    public void setItemBoundaryListener(AppGridLayout.ItemOnBoundaryListener mItemBoundaryListener) {
        this.mItemBoundaryListener = mItemBoundaryListener;
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mBlockListAdapter) {
            mBlockListAdapter.registerView(null);
        }
//        Log.d("test", "--------end");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (null != mBlockListAdapter) {
            mBlockListAdapter.registerView(this);
        }
//        Log.d("test", "----------end");
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
            if (orientation == 0) { //HORIZON
                int colCnt = mBlockListAdapter.getCloumnNum();
                if (pos < colCnt) {
                    return true;
                }
            } else {

            }
        }
        return false;
    }

    protected boolean isItemInRight() {
        View view = getFocusedChild();
        if (view != null && view.getTag() != null) {
            int pos = Integer.valueOf(view.getTag().toString());
            int childCnt = getVisibleChildCount();
            if (orientation == 0) {
                int colCnt = mBlockListAdapter.getCloumnNum();
                if ((pos + 1) % colCnt == 0 || pos == (childCnt -1)) {
                    return true;
                }
            } else {

            }
        }
        return false;
    }

    protected boolean isItemInLeft() {
        View view = getFocusedChild();
        if (view != null && view.getTag() != null) {
            int pos = Integer.valueOf(view.getTag().toString());
            if (orientation == 0) {
                int colCnt = mBlockListAdapter.getCloumnNum();
                if (pos % colCnt == 0) {
                    return true;
                }
            } else {

            }
        }
        return false;
    }

    protected boolean isItemInBottom() {
        View view = getFocusedChild();
        if (view != null && view.getTag() != null) {
            int pos = Integer.valueOf(view.getTag().toString());
            if (orientation == 0) {
                int rowCnt = mBlockListAdapter.getRowNum();
                int colCnt = mBlockListAdapter.getCloumnNum();
                if (pos / colCnt == (rowCnt -1)) {
                    return true;
                }
            } else {

            }
        }
        return false;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int index = (Integer) v.getTag(INDEX_TAG);
            if (null != mOnItemClickListener) {
                mOnItemClickListener.onItemClick(v, index);
            }
        }
    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.d("test", "listview end");
    }

    public void onDataListChange() {

        removeAllViews();

        int len = mBlockListAdapter.getCount();
        int w = mBlockListAdapter.getBlockWidth();
        int h = mBlockListAdapter.getBlockHeight();
        int columnNum = mBlockListAdapter.getCloumnNum();

        int horizontalSpacing = mBlockListAdapter.getHorizontalSpacing();
        int verticalSpacing = mBlockListAdapter.getVerticalSpacing();

        boolean blockDescendant = getDescendantFocusability() == ViewGroup.FOCUS_BLOCK_DESCENDANTS;

        for (int i = 0; i < len; i++) {

            LayoutParams lyp = new LayoutParams(w, h);
            int row = i / columnNum;
            int clo = i % columnNum;
            int left = 0;
            int top = 0;

            if (clo > 0) {
                left = (horizontalSpacing + w) * clo;
            }
            if (row > 0) {
                top = (verticalSpacing + h) * row;
            }
            lyp.setMargins(left, top, 0, 0);
            View view = mBlockListAdapter.getView(mLayoutInflater, i);
            if (!blockDescendant) {
                view.setOnClickListener(mOnClickListener);
            }
            view.setTag(INDEX_TAG, i);
            addView(view, lyp);
        }
        requestLayout();
    }
}

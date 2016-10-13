package com.cookingshow.navigation;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class NavigationBar extends LinearLayout {

    private static final String TAG = "NavigationBar";
    private List<NavigationData> mNaviData = null;
    private boolean initialized;
    private List<NavigationItemView> mNaviItems = null;
    private OnClickListener mItemClickListener = null;
    private View mClickedItem = null;

    public NavigationBar(Context context) {
        super(context);
        inflate();
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate();
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate();
    }

    private void inflate() {
        this.setVisibility(View.INVISIBLE);
        this.setOrientation(LinearLayout.HORIZONTAL);
    }

    public void inflateItems(List<NavigationData> naviData, int childResId, int itemMargin) {
        if (naviData == null) {
            return;
        }
        this.mNaviData = naviData;
        this.removeAllViews();
        mNaviItems = new ArrayList<NavigationItemView>();
        int count = mNaviData.size();
        NavigationItemView beforeItemView = null;
        for (int i = 0; i < count; i++) {
            NavigationData data = mNaviData.get(i);
            NavigationItemView item = (NavigationItemView) LayoutInflater.from(getContext()).inflate(childResId, null);
            item.setItemTitle(data.title);
            item.setItemIcon(data.iconId);
            item.setId(data.id);
            item.setTag(data.tag);

            item.setClickable(true);
            item.setFocusable(true);
            item.setOnClickListener(mInternalClickListener);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT);
            params.rightMargin = itemMargin;
            if (i == (count -1)) {
                params.rightMargin = 0;
            }
            if (beforeItemView != null) {
                beforeItemView.setNextFocusRightId(data.id);
                item.setNextFocusLeftId(beforeItemView.getId());
            }
            beforeItemView = item;
            addView(item, params);
            mNaviItems.add(item);
        }
        initialized = true;
        this.setVisibility(View.VISIBLE);
    }

    public void onClickItemById(int id) {
        int cnt = getChildCount();
        View clickView = null;
        for (int i = 0; i < cnt; i++) {
            View view = getChildAt(i);
            if (view.getId() == id) {
                clickView = view;
                break;
            }
        }
        if (clickView != null) {
            mInternalClickListener.onClick(clickView);
        }
    }


    public void setItemOnClickListener(OnClickListener listener) {
        this.mItemClickListener = listener;
    }

    private OnClickListener mInternalClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setClickedItem(v);
            if (mItemClickListener != null) {
                mItemClickListener.onClick(v);
            }
        }
    };

    public void setClickedItem(View view) {
        if (view == null) {
            return;
        }
        if (mClickedItem != null && view.getId() == mClickedItem.getId()) {
            return;
        }
        view.setSelected(true);
        if (mClickedItem != null) {
            mClickedItem.setSelected(false);
        }
        mClickedItem = view;
    }

    public View getClickedItem() {
        return mClickedItem;
    }

    public void setClickedItem(int id) {
        if (mClickedItem == null || id != mClickedItem.getId()) {
            setClickedItem(findViewById(id));
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.ACTION_DOWN == event.getAction()) {
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (mClickedItem != null) {
                        if (((NavigationItemView)mClickedItem).onFocusDown()) {
                            return true;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (mClickedItem != null) {
                        if (((NavigationItemView)mClickedItem).onFocusUp()) {
                            return true;
                        }
                    }
                    break;

            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void requestFocusForSelected() {
        if (mClickedItem != null) {
            mClickedItem.requestFocus();
        }
    }

    private View mFocusDownView = null;
    private View mFocusUpView = null;

    public void setNextFocusDownView(View view) {
        this.mFocusDownView = view;
    }

    public void setNextFocusUpView(View view) {
        this.mFocusUpView = view;
    }

    public void setItemFocusListener(NavigationItemView.OnNavigationItemFocusListener listener) {
        for (NavigationItemView item : mNaviItems) {
            item.setOnNavigationItemListener(listener);
        }
    }

}

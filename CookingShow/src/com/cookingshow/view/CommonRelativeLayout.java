package com.cookingshow.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cookingshow.page.ActionEventMgr;

public class CommonRelativeLayout extends RelativeLayout{

    public CommonRelativeLayout(Context context) {
        super(context);
        initHoverListener();
    }

    public CommonRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHoverListener();
    }

    public CommonRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHoverListener();
    }

    protected void initHoverListener() {
        this.setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        ActionEventMgr.getInstance().setFocusOrHoverView(CommonRelativeLayout.this);
                        if (CommonRelativeLayout.this.getParent() instanceof GridView
                                || CommonRelativeLayout.this.getParent() instanceof ListView) {
                            CommonRelativeLayout.this.setHovered(true);
                        }
                        if (getOnFocusChangeListener() != null) {
                            getOnFocusChangeListener().onFocusChange(CommonRelativeLayout.this, true);
                        }
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        if (CommonRelativeLayout.this.getParent() instanceof GridView
                                || CommonRelativeLayout.this.getParent() instanceof ListView) {
                            CommonRelativeLayout.this.setHovered(false);
                        }
                        if (getOnFocusChangeListener() != null) {
                            getOnFocusChangeListener().onFocusChange(CommonRelativeLayout.this, false);
                        }
                }
                return false;
            }
        });
    }


}

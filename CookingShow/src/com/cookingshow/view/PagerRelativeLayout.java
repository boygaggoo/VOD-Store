package com.cookingshow.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class PagerRelativeLayout extends RelativeLayout{

    private static final String TAG = "PagerRelativeLayout";

    public PagerRelativeLayout(Context context) {
        super(context);
        initHoverListener();
    }

    public PagerRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHoverListener();
    }

    public PagerRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHoverListener();
    }

    protected void initHoverListener() {        
//            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

}

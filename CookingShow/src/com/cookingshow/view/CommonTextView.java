package com.cookingshow.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class CommonTextView extends TextView{


    public CommonTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes();
    }

    public CommonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes();
    }

    public CommonTextView(Context context) {
        super(context);
        initAttributes();
    }

    private void initAttributes() {
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }


}

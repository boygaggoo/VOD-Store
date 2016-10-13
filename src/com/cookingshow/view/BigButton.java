package com.cookingshow.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cookingshow.R;

public class BigButton extends LinearLayout{

    private ImageView mButtonIcon;
    private TextView mButtonText;
    private int mIconRid;
    private int mButtonTextRid;
    private float mTextFont;
    private int mTextColorRid;

    public BigButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BigButton);
        mTextFont = a.getDimensionPixelSize(R.styleable.BigButton_text_font, 30);
        mIconRid = a.getResourceId(R.styleable.BigButton_button_icon, -1);
        mButtonTextRid = a.getResourceId(R.styleable.BigButton_button_text, -1);
//        mTextColorRid = a.getColor(R.styleable.BigButton_text_color, R.color.common_normal_white);
        a.recycle();
        initContent(context);
        this.setFocusable(true);
        this.setClickable(true);
    }

    private void initContent(Context context) {
        LayoutInflater.from(context).inflate(R.layout.big_button_layout, this);
        mButtonText = (TextView) findViewById(R.id.button_text);
        mButtonIcon = (ImageView) findViewById(R.id.button_icon);
        setButtonText(mButtonTextRid);
        if (mIconRid == -1) {
            mButtonIcon.setVisibility(View.GONE);
        } else {
            setButtonIcon(mIconRid);
        }
//        mButtonText.setTextColor(getResources().getColorStateList(mTextColorRid));
        mButtonText.setTextSize(mTextFont);
    }

    public TextView getTextView() {
        return mButtonText;
    }

    public void setButtonIcon(int rid) {
        mButtonIcon.setImageResource(rid);
    }

    public void setButtonText(int rid) {
        mButtonText.setText(rid);
    }

    public void setButtonText(String str) {
        mButtonText.setText(str);
    }
}

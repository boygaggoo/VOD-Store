package com.cookingshow.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cookingshow.R;

public class PageHelpView extends LinearLayout{

    private ImageView mHelpIcon;
    private TextView mHelpText;

    public PageHelpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.page_help_layout, this);
        mHelpText = (TextView) findViewById(R.id.help_text);
        mHelpIcon = (ImageView) findViewById(R.id.help_icon);
    }

    public void setHelpIcon(int rid) {
        mHelpIcon.setImageResource(rid);
    }

    public void setHelpText(int rid) {
        mHelpText.setText(rid);
    }

}

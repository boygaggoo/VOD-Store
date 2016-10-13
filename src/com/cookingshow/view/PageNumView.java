package com.cookingshow.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cookingshow.R;

public class PageNumView extends LinearLayout{

    private TextView mCurPageNum;
    private TextView mSumPageNum;

    public PageNumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.page_num_layout, this);
        mCurPageNum = (TextView) findViewById(R.id.cur_page_num);
        mSumPageNum = (TextView) findViewById(R.id.sum_page_num);
    }

    public void setCurPageNum(String curPageNum) {
        mCurPageNum.setText(curPageNum);
    }

    public void setSumPageNum(String sumPageNum) {
        mSumPageNum.setText(sumPageNum);
    }

}

package com.cookingshow.push;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.cookingshow.BaseActivity;
import com.cookingshow.R;
import com.cookingshow.view.PageHelpView;


public class PushTicketActivity extends BaseActivity {

    private static final String TAG = "PushTicketActivity";
    public static final int FLAG_INCLUDE_STOPPED_PACKAGES = 32;
    private PageHelpView mPageHelpView = null;
    private TextView mTextView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pushmain);
        //mPageHelpView = (PageHelpView) findViewById(R.id.page_help_view);
        mTextView = (TextView) findViewById(R.id.app_title);
        mTextView.setText(R.string.btn_weixin);

        switchFragment(new BeforeRegister());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean isStringIsNumber(String key) {
        if (key == null || key.equals("")) {
            return false;
        }
        try {
            Long.parseLong(key);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // 切换fragment
    public void switchFragment(Fragment f) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, f);
        ft.commit();
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

}
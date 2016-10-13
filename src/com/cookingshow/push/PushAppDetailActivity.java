package com.cookingshow.push;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.cookingshow.BaseActivity;
import com.cookingshow.R;
import com.cookingshow.view.PageHelpView;

public class PushAppDetailActivity extends BaseActivity {

    private static final String TAG = "PushAppDetailActivity";
    private PageHelpView mPageHelpView = null;
    private TextView mTextView = null;
    private AppDetailInfo mAppDetailInfo = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pushmain);
        //mPageHelpView = (PageHelpView) findViewById(R.id.page_help_view);
        mTextView = (TextView) findViewById(R.id.app_title);

        Intent intent = getIntent();
        int appId = intent.getIntExtra("appId", 1);
        String appName = intent.getStringExtra("appName");
        String appUrl = intent.getStringExtra("appUrl");
        String uploader = intent.getStringExtra("uploader");
        String tips = intent.getStringExtra("tips");
        String material = intent.getStringExtra("material");
        
        mTextView.setText(appName);
       // mPageHelpView.setHelpText(R.string.page_help_weixin);

        mAppDetailInfo = new AppDetailInfo(appId, appName, appUrl, uploader, tips, material);
        switchFragment(mAppDetailInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

	public boolean onKeyDown(int keyCode, KeyEvent event) {		
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(!mAppDetailInfo.onBackPressedFragment()) {
				return super.onKeyDown(keyCode, event);
			}
		}
		else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			return mAppDetailInfo.onLeftDownFragment();
			//Toast.makeText(this.getApplicationContext(), "press:" + "KEYCODE_DPAD_LEFT", Toast.LENGTH_LONG).show();
		}
		else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			return mAppDetailInfo.onRightDownFragment();
			//Toast.makeText(this.getApplicationContext(), "press:" + "KEYCODE_DPAD_RIGHT", Toast.LENGTH_LONG).show();
		}
		
		return false ;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {	
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(!mAppDetailInfo.onBackPressedFragment()) {
				return super.onKeyUp(keyCode, event);
			}
		}
		else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			return mAppDetailInfo.onKeyUpFragment();
			//Toast.makeText(this.getApplicationContext(), "press:" + "KEYCODE_DPAD_LEFT", Toast.LENGTH_LONG).show();
		}
		else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			return mAppDetailInfo.onKeyUpFragment();
			//Toast.makeText(this.getApplicationContext(), "press:" + "KEYCODE_DPAD_RIGHT", Toast.LENGTH_LONG).show();
		}
		
		return false ;
	}
	
    public void switchFragment(Fragment f) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, f);
        ft.commit();
    }
}

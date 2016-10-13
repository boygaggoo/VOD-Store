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
import com.cookingshow.push.AlbumDetailInfo.OnSetTitleListener;
import com.cookingshow.view.PageHelpView;

public class PushAlbumDetailActivity extends BaseActivity implements OnSetTitleListener{

    private static final String TAG = "PushAlbumDetailActivity";
    private PageHelpView mPageHelpView = null;
    private TextView mTextView = null;
    private AlbumDetailInfo mAlbumDetailInfo = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pushmain);
        //mPageHelpView = (PageHelpView) findViewById(R.id.page_help_view);
        mTextView = (TextView) findViewById(R.id.app_title);

        Intent intent = getIntent();
        int albumId = intent.getIntExtra("albumId", 1);
        int appId = intent.getIntExtra("appId", 0);
        String albumName = intent.getStringExtra("albumName");
        String uploader = intent.getStringExtra("uploader");
        
        mTextView.setText(albumName);
        //mPageHelpView.setHelpText(R.string.page_help_weixin);

        mAlbumDetailInfo = new AlbumDetailInfo(albumId, appId, uploader);
        switchFragment(mAlbumDetailInfo);
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
			if(!mAlbumDetailInfo.onBackPressedFragment()) {
				return super.onKeyDown(keyCode, event);
			}
		}
		else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			return mAlbumDetailInfo.onLeftDownFragment();
			//Toast.makeText(this.getApplicationContext(), "press:" + "KEYCODE_DPAD_LEFT", Toast.LENGTH_LONG).show();
		}
		else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			return mAlbumDetailInfo.onRightDownFragment();
			//Toast.makeText(this.getApplicationContext(), "press:" + "KEYCODE_DPAD_RIGHT", Toast.LENGTH_LONG).show();
		}
		
		return false ;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {	
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(!mAlbumDetailInfo.onBackPressedFragment()) {
				return super.onKeyUp(keyCode, event);
			}
		}
		else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			return mAlbumDetailInfo.onKeyUpFragment();
			//Toast.makeText(this.getApplicationContext(), "press:" + "KEYCODE_DPAD_LEFT", Toast.LENGTH_LONG).show();
		}
		else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			return mAlbumDetailInfo.onKeyUpFragment();
			//Toast.makeText(this.getApplicationContext(), "press:" + "KEYCODE_DPAD_RIGHT", Toast.LENGTH_LONG).show();
		}
		
		return false ;
	}

    public void switchFragment(Fragment f) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, f);
        ft.commit();
    }

	@Override
	public void setTitle(String title) {
		// TODO Auto-generated method stub
		mTextView.setText(title);
	}
}

package com.cookingshow;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.cookingshow.datacenter.BootScreenInfo;
import com.cookingshow.datacenter.BootScreenProvider;
import com.cookingshow.network.cache.ACache;
import com.cookingshow.page.DisplayImageMgr;

public class LauncherFragment extends BaseMainFragment{

    private static final String TAG = "LauncherFragment";
    private ImageView mLoadingImage = null;
    private View root = null;
    private Bitmap mLoadBitmap = null;
    private ACache mCache = null;
    private static final long DELAY_TIME = 3000;
    private boolean isDestroyFromSelf = false;
    private boolean isDestroyFromMain = false;
    private static BootScreenProvider mBootScreenProvider;
    private BootScreenInfo mBootScreeInfo = null;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
                isDestroyFromSelf = true;
                if (isDestroyFromMain) {
                    nodifyMainToDestroyThis();
                }
                break;
            case 1:
                isDestroyFromMain = true;
                if (isDestroyFromSelf) {
                    nodifyMainToDestroyThis();
                }
                break;
            case 2:
            	Toast.makeText(LauncherFragment.this.getActivity().getApplicationContext(), (String)getResources().getText(R.string.boot_screen_info), Toast.LENGTH_SHORT).show();
            	break;
            case 3:
            	break;
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mCache = ACache.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.launcher_layout, null);
        mLoadingImage = (ImageView) root.findViewById(R.id.loading_image);
        new LoadContentTask().execute();
        Log.d(TAG, "onCreateView");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
        if (mLoadBitmap != null && !mLoadBitmap.isRecycled()) {
            mLoadBitmap.recycle();
            Log.d(TAG, "recycle the bitmap");
        }
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();
    }
	
    protected class LoadContentTask extends AsyncTask<Object, Void, Boolean> {

        public LoadContentTask() {
        	mBootScreenProvider = new BootScreenProvider(LauncherFragment.this.getActivity().getApplicationContext(), mHandler);
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                if (mBootScreenProvider != null) {
                	mBootScreeInfo = mBootScreenProvider.getBootScreen();
                	if(null != mBootScreeInfo) {
                		String path = mBootScreeInfo.getThumb();
                		if (!TextUtils.isEmpty(path)) {
                			mLoadBitmap = mCache.getAsBitmap("boot_img");
                			return true;
                		}
                		else {
                			return false;
                		}
                	}
                }
            
            } catch (Exception e) {
                //e.printStackTrace();
            }
            
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                updateUiAfterLoad();
            } catch (Exception e) {
                //e.printStackTrace();
            }
            super.onPostExecute(result);
        }

    }

    private void updateUiAfterLoad() {
    	if(mLoadBitmap != null) {
			mLoadingImage.setImageBitmap(mLoadBitmap);
		}
		else {
            mLoadingImage.setImageBitmap(getImageFromAssetsFile("default_loading_bg.jpg"));
        }

        mLoadingImage.setVisibility(View.VISIBLE);

        mHandler.sendEmptyMessage(3);
        mHandler.sendEmptyMessageDelayed(0, DELAY_TIME);
    }

    public void setDestroyFromMain() {
        mHandler.sendEmptyMessage(1);
    }

    private void nodifyMainToDestroyThis() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.top_out);
        ft.hide(this);
        ft.commitAllowingStateLoss();
    }

    private Bitmap getImageFromAssetsFile(String fileName) {
    	Bitmap image = null;
    	AssetManager am = getResources().getAssets();
    	
    	try {
    		InputStream is = am.open(fileName);
    		image = BitmapFactory.decodeStream(is);
    		is.close();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return image;
    }

    @Override
    protected void onAnimStart(boolean enter) {
        if (!enter) {
            DisplayImageMgr.getInstance().pauseDisplay();
        }
    }

    @Override
    protected void onAnimEnd(boolean enter) {
        if (!enter) {
            DisplayImageMgr.getInstance().resumeDisplay();
            if (getActivity() instanceof  MainActivity) {
                ((MainActivity)getActivity()).deleteLauncherFragment();
            }
        }
    }
}

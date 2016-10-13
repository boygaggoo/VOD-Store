package com.cookingshow.push;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cookingshow.R;

public class AppShareImgActivity extends Activity {
    
	protected static final String TAG = "AppShareImgActivity";
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.camera_layout);
		
		initUI();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

    private void initUI() {
    	mSurfaceView=(SurfaceView) findViewById(R.id.surfaceView);
    	
    	mSurfaceView.setOnClickListener(mCameraClickListener);
    	
    	mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(new SurfaceCallback());    	
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
    	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {  
            // SurfaceView的大小改变  
        }

        @Override  
        public void surfaceCreated(SurfaceHolder holder) {   
            mCamera = Camera.open();
            
            if(mCamera == null) {
            	showToast("downoad mobile");
            	return;
            }
            
            try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mCamera.release();
			}
            
            Camera.Parameters parameters = mCamera.getParameters();
            if(parameters.getFocusMode() != Camera.Parameters.FOCUS_MODE_AUTO) {
            	parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
            }

            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setFlashMode(Parameters.FLASH_MODE_ON);

            mCamera.cancelAutoFocus();
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
				
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					// TODO Auto-generated method stub
					//showToast("onAutoFocus");
					if (camera.getParameters().getFocusMode() != Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
						Parameters parameters = camera.getParameters();
						parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
						camera.setParameters(parameters);
					}
				}
			});
        }
        
        @Override  
        public void surfaceDestroyed(SurfaceHolder holder) {
        	if(null != mCamera) {
            	mCamera.stopPreview();
            	mCamera.release();
            	mCamera = null;        		
        	}
        } 
    }

    private View.OnClickListener mCameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //showToast("camera test");
        	if(null != mCamera) {
        		mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        	}

        }
    };

    ShutterCallback mShutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
			
		}
    	
    };
    
    PictureCallback mRawCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			
		}
    	
    };
 
    PictureCallback mJpegPictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Bitmap b = null;
			if(null != data) {
				b = BitmapFactory.decodeByteArray(data, 0, data.length);
				mCamera.stopPreview();
				
				if(null != b) {
					//showToast("get camera jpeg");
				}
				
				mCamera.startPreview();
			}

		}
    	
    };

    private void showToast(String str) {
    	Toast.makeText(this.getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }
}

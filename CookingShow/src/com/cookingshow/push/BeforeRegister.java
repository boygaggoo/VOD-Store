package com.cookingshow.push;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cookingshow.R;
import com.cookingshow.network.cache.ACache;
import com.cookingshow.network.controller.CommonImageLoader;


public class BeforeRegister extends Fragment {

    protected static final String TAG = "BeforeRegister";
    private TextView creatQrTxt;
    private ImageView qrImg;
    private ACache mCache = null;
    private CommonImageLoader imageLoader = null;
    private static final String QR_URL_PRE = "http://api.cli.im/generate/?key=S91m01&logo=http://182.92.198.90/test/dish/asset/icon_weixin.png&level=M&size=400&data=";
    private static final String QR_URL_DATA = "http://182.92.198.90/tv/update/CookingShow.apk";    
    private static final int REFRESH_QR = 0;
    private static final long REFRESH_QR_DELAY = 30 * 60 * 1000;
    private static final String PAGE_NAME = "bindingPage";

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_QR:
                    ConnectToQrServer();
                    break;
            }
            return false;
        }
    });

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //switchFragment(new AfterWeixinRegister());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache = ACache.get(getActivity());
        imageLoader = new CommonImageLoader();
        imageLoader.setDefaultAndErrorImgId(0, 0);
        registerUserBindBroadcast();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beforepushregister, container, false);
        qrImg = (ImageView) view.findViewById(R.id.qrimg);
        creatQrTxt = (TextView) view.findViewById(R.id.weixin_creat_qr);
        creatQrTxt.setText(R.string.weixin_create_qr);
        handler.sendEmptyMessage(REFRESH_QR);
        return view;
    }

    private void registerUserBindBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PushUtils.ACTION_PUSH_USER_BIND);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    private OnClickListener clickSwitchFragment = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //switchFragment(new AfterWeixinRegister());
        }
    };

    // 切换fragment
    public void switchFragment(Fragment f) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, f);
        ft.commit();
    }
    
    private void ConnectToQrServer() {    	
    	String QR_URL = QR_URL_PRE + QR_URL_DATA/* + mCache.getAsString("deviceId")*/;
        
    	downLoadImage(QR_URL);
    }

    protected void downLoadImage(final String url) {
        Log.d(TAG, "qrcode url:" + url);
        creatQrTxt.setText(R.string.weixin_create_qr);
        creatQrTxt.setVisibility(View.VISIBLE);
        imageLoader.loadImage(url, qrImg, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    creatQrTxt.setVisibility(View.INVISIBLE);
                    qrImg.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                    creatQrTxt.setText(R.string.weixin_error_text);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        handler.removeMessages(REFRESH_QR);
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
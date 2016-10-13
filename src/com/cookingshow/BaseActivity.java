package com.cookingshow;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public abstract class BaseActivity extends FragmentActivity{
    final static String TAG = "BaseActivity";
    private boolean isRunning = false;
    protected int duration = 700;
    protected boolean isAnimating = false;
    protected NetworkChangeReceiver networkChangeReceiver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkChangeReceiver = new NetworkChangeReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    @Override
    protected void onResume() {
        isRunning = true;
        super.onResume();
        Log.i(TAG, "onResume");
        IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, networkFilter);
    }
    
    protected void requestFocus(View view){
        if(view != null && isRunning){
            view.requestFocus();
        }
    }

    @Override
    protected void onPause() {
        isRunning = false;
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (isAnimating
//                && event.getKeyCode() != KeyEvent.KEYCODE_BACK
                ) {
            Log.d(TAG, "ignore keycode");
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            onMenuKeyEvent();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void animationStart() {
        isAnimating = true;
    }

    public void animationEnd() {
        isAnimating = false;
    }

    public void onMenuKeyEvent() {

    }

    private NetworkInfo.State oldState = NetworkInfo.State.DISCONNECTED;

    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "action " + intent.getAction());
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            Log.i(TAG, "network info:" + info.toString());
            if (oldState == info.getState()) {
                return;
            } else {
                oldState = info.getState();
            }
            ConnectivityManager conMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
            if (networkInfo != null) {
                boolean isConnected = networkInfo.isConnectedOrConnecting();
                if (isConnected) {
                    mHandler.obtainMessage(2).sendToTarget();
                } else {
                    mHandler.obtainMessage(1).sendToTarget();
                }
            } else {
                mHandler.obtainMessage(1).sendToTarget();
            }
        }
    };

    protected Dialog networkDialog = null;

    public Dialog getNetworkErrorDialog() {
        if (networkDialog == null) {
            View view = LayoutInflater.from(BaseActivity.this).inflate(R.layout.network_tip_dialog, null);
            networkDialog = new Dialog(BaseActivity.this, R.style.network_dialog);
            networkDialog.setCanceledOnTouchOutside(false);
            networkDialog.setContentView(view);
        }
        return networkDialog;
    }

    private Handler mHandler = new Handler() {
        private boolean showStatus = false;
        private Dialog dialog;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dialog = getNetworkErrorDialog();
                    if (dialog != null && !dialog.isShowing()) {
                        showStatus = true;
                        Button okBtn = (Button) dialog.findViewById(R.id.dialog_button_ok);
                        Button cancelBtn = (Button) dialog.findViewById(R.id.dialog_button_cancel);
                        cancelBtn.setText(R.string.tv_dialog_network_continue);
                        okBtn.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mHandler.sendEmptyMessage(2);
                                BaseActivity.this.finish();
                            	System.exit(0);
                            }
                        });

                        cancelBtn.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                mHandler.sendEmptyMessage(2);
                            }
                        });
                        dialog.show();
                    }
                    break;
                case 2:
                    if (dialog != null && showStatus) {
                        showStatus = false;
                        dialog.dismiss();
                    }
                    mHandler.removeMessages(2);
                    break;
                default:
                    break;
            }
        }
    };
}

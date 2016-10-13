package com.cookingshow.service;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.cookingshow.service.data.DataUtil;
import com.cookingshow.service.data.ShareVideoInfo;
import com.cookingshow.service.parser.ParserUtil;
import com.cookingshow.service.parser.ShareVideoParser;
import com.cookingshow.service.util.ConnectionManager.NetworkListener;
import com.cookingshow.service.util.NetworkCheckTask;

public class ShareVideoClient extends BaseClient {

    private static final String TAG = "ShareVideoClient";

    private final int defautMsg = 0;
    private ShareVideoTask task = null;

    private HandlerThread handlerThread;
    private Handler handler;
    
	@Override
	public void refreshData(long timer) {
		// TODO Auto-generated method stub
		//Toast.makeText(context, "focus up", Toast.LENGTH_LONG).show();
		
        if (DataUtil.isExistShareVideoData(context, this)) {
        	DataUtil.removeShareVideoDataList(context, this);
        }
        
        if (handler != null && conManager.isConnected()) {
            handler.removeMessages(defautMsg);
            handler.sendMessageDelayed(handler.obtainMessage(defautMsg), timer);
        }
	}

	@Override
	public void gatheringData() {
		// TODO Auto-generated method stub
        handlerThread = new HandlerThread("ShareVideoClient");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper()) {
            private int nRetryCnt = 1;

            public void handleMessage(Message msg) {
                if (task != null) {
                    if (task.getStatus() != Status.FINISHED) {
                        task.cancel(true);
                    }
                    task = null;
                }

                Log.i(TAG, "HttpURLConnection");
                HttpURLConnection conn = ParserUtil.getHttpURLConnection(api + "?" + apiParam);
                if (conn != null) {
                    task = new ShareVideoTask(conn);
                    task.execute();

                    nRetryCnt = 1;
                } else {
                    if (nRetryCnt <= 0) {
                        // check network
                        new NetworkCheckTask() {
                            protected void onPostExecute(Boolean result) {
                                if (result) {
                                    // service is not available!
                                    DataUtil.setShareVideoDataList(context, null, ShareVideoClient.this);
                                } else {
                                    // network connection is failed.
                                    conManager.broadcastNotifyNetworkSettingsError();
                                }
                            }
                        }.execute();
                    } else {
                        refreshData(60000);
                        nRetryCnt--;
                    }
                }
            }
        };
	}

	@Override
	public void cancelGathering() {
		// TODO Auto-generated method stub
        if (handler != null) {
            handler.removeMessages(defautMsg);
            handler = null;
        }

        if (handlerThread != null) {
            handlerThread.quit();
            handlerThread = null;
        }

        if (task != null) {
            task.cancel(true);
            task = null;
        }
	}

    private void refreshData() {
        if (handler != null) {
            handler.removeMessages(defautMsg);
            handler.sendMessageDelayed(handler.obtainMessage(defautMsg), timer);
        }
    }

    private void stopRefreshData() {
        if (handler != null) {
            handler.removeMessages(defautMsg);
        }
    }

    class ShareVideoTask extends AsyncTask<Object, Void, List<ShareVideoInfo>> {
        private HttpURLConnection conn;

        public ShareVideoTask(HttpURLConnection conn) {
            this.conn = conn;
        }

        @Override
        protected List<ShareVideoInfo> doInBackground(Object... params) {
            // TODO Auto-generated method stub
        	ShareVideoParser parser = new ShareVideoParser();
            List<ShareVideoInfo> dataList = new ArrayList<ShareVideoInfo>();
            dataList = parser.getVideoDataList(conn);

            return dataList;
        }

        protected void onCancelled() {
            super.onCancelled();

            if (conn != null) {
                try {
                    conn.disconnect();
                    conn = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        protected void onPostExecute(List<ShareVideoInfo> result) {
            storeData(result);
            conn.disconnect();
            conn = null;
            //refreshData();
        }
    }

    private void storeData(List<ShareVideoInfo> dataList) {
        Log.i(TAG, "storeData");        
        DataUtil.setShareVideoDataList(context, dataList, this);
    }
    
	@Override
	public void setNetworkListener() {
		// TODO Auto-generated method stub
        conManager.addListener(new NetworkListener() {

            @Override
            public void onNetworkConnectionChanged(boolean isConnected) {
                // TODO Auto-generated method stub
                if (isConnected) {
				    Log.i(TAG, "listen: network connected");
                    refreshData(defautMsg);
                } else {
				    Log.i(TAG, "listen: network disconnected");
                    stopRefreshData();
                    DataUtil.setShareVideoDataList(context, null, ShareVideoClient.this);
                }
            }

            @Override
            public void onNotifyNetworkSettingsError() {
                // TODO Auto-generated method stub
                stopRefreshData();
                DataUtil.setShareVideoDataList(context, null, ShareVideoClient.this);
            }

        });
	}

}

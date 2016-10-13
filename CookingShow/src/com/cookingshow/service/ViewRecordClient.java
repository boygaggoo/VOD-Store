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
import com.cookingshow.service.data.ViewRecordDataInfo;
import com.cookingshow.service.parser.ParserUtil;
import com.cookingshow.service.parser.ViewRecordParser;
import com.cookingshow.service.util.ConnectionManager.NetworkListener;
import com.cookingshow.service.util.NetworkCheckTask;

public class ViewRecordClient extends BaseClient {
    private static final String TAG = "ViewRecordClient";

    private final int defautMsg = 0;
    private ViewRecordTask task = null;

    private HandlerThread handlerThread;
    private Handler handler;
    
	@Override
	public void refreshData(long timer) {
		// TODO Auto-generated method stub		
        if (DataUtil.isExistViewRecordData(context, this)) {
        	DataUtil.removeViewRecordDataList(context, this);
        }
        
        if (handler != null && conManager.isConnected()) {
            handler.removeMessages(defautMsg);
            handler.sendMessageDelayed(handler.obtainMessage(defautMsg), timer);
        }
	}

	@Override
	public void gatheringData() {
		// TODO Auto-generated method stub
        handlerThread = new HandlerThread("ViewRecordClient");
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
                    task = new ViewRecordTask(conn);
                    task.execute();

                    nRetryCnt = 1;
                } else {
                    if (nRetryCnt <= 0) {
                        // check network
                        new NetworkCheckTask() {
                            protected void onPostExecute(Boolean result) {
                                if (result) {
                                    // service is not available!
                                    DataUtil.setViewRecordDataList(context, null, ViewRecordClient.this);
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

    class ViewRecordTask extends AsyncTask<Object, Void, List<ViewRecordDataInfo>> {
        private HttpURLConnection conn;

        public ViewRecordTask(HttpURLConnection conn) {
            this.conn = conn;
        }

        @Override
        protected List<ViewRecordDataInfo> doInBackground(Object... params) {
            // TODO Auto-generated method stub
        	ViewRecordParser parser = new ViewRecordParser();
            List<ViewRecordDataInfo> dataList = new ArrayList<ViewRecordDataInfo>();
            dataList = parser.getViewRecordDataList(conn);

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

        protected void onPostExecute(List<ViewRecordDataInfo> result) {
            storeData(result);
            conn.disconnect();
            conn = null;
            //refreshData();
        }
    }

    private void storeData(List<ViewRecordDataInfo> dataList) {
        Log.i(TAG, "storeData");        
        DataUtil.setViewRecordDataList(context, dataList, this);
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
                    DataUtil.setViewRecordDataList(context, null, ViewRecordClient.this);
                }
            }

            @Override
            public void onNotifyNetworkSettingsError() {
                // TODO Auto-generated method stub
                stopRefreshData();
                DataUtil.setViewRecordDataList(context, null, ViewRecordClient.this);
            }

        });
	}
}

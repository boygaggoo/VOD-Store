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
import com.cookingshow.service.data.TopDataInfo;
import com.cookingshow.service.parser.ParserUtil;
import com.cookingshow.service.parser.TopDataParser;
import com.cookingshow.service.util.ConnectionManager.NetworkListener;
import com.cookingshow.service.util.NetworkCheckTask;

public class TopDataClient extends BaseClient {
    private static final String TAG = "TopDataClient";

    private final int defautMsg = 0;
    private TopDataTask task = null;

    private HandlerThread handlerThread;
    private Handler handler;
    
	@Override
	public void refreshData(long timer) {
		// TODO Auto-generated method stub
        if (handler != null && conManager.isConnected()) {
            handler.removeMessages(defautMsg);
            handler.sendMessageDelayed(handler.obtainMessage(defautMsg), timer);
        }
	}

	@Override
	public void gatheringData() {
		// TODO Auto-generated method stub
        handlerThread = new HandlerThread("TopDataClient");
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
                HttpURLConnection conn = ParserUtil.getHttpURLConnection(api);
                if (conn != null) {
                    task = new TopDataTask(conn);
                    task.execute();

                    nRetryCnt = 1;
                } else {
                    if (nRetryCnt <= 0) {
                        // check network
                        new NetworkCheckTask() {
                            protected void onPostExecute(Boolean result) {
                                if (result) {
                                    // service is not available!
                                    DataUtil.setTopDataList(context, null, TopDataClient.this);
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

    class TopDataTask extends AsyncTask<Object, Void, List<TopDataInfo>> {
        private HttpURLConnection conn;

        public TopDataTask(HttpURLConnection conn) {
            this.conn = conn;
        }

        @Override
        protected List<TopDataInfo> doInBackground(Object... params) {
            // TODO Auto-generated method stub
        	TopDataParser parser = new TopDataParser();
            List<TopDataInfo> dataList = new ArrayList<TopDataInfo>();
            dataList = parser.getTopDataList(conn);

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

        protected void onPostExecute(List<TopDataInfo> result) {
            storeData(result);
            conn.disconnect();
            conn = null;
            refreshData();
        }
    }

    class MakeBlobTask extends AsyncTask<List<TopDataInfo>, Void, List<TopDataInfo>> {

        @Override
        protected List<TopDataInfo> doInBackground(List<TopDataInfo>... params) {
            // TODO Auto-generated method stub
            List<TopDataInfo> dataList = (List<TopDataInfo>) params[0];

            return dataList;
        }

        protected void onPostExecute(List<TopDataInfo> result) {
            executeTransaction(result);
        }
    }

    private void storeData(List<TopDataInfo> topDataList) {
        Log.i(TAG, "storeData");
        boolean isDatachanged = false;
        List<TopDataInfo> storedDataList = DataUtil.getTopDataList(context, this);
        int i = 0;

        if(storedDataList.size() != topDataList.size()) {
        	isDatachanged = true;
        }
        else {
        	for(i = 0; i < topDataList.size(); i++) {
        		if(topDataList.get(i).getDishId() != storedDataList.get(i).getDishId()) {
        			isDatachanged = true;
        			break;
        		}
        	}
        }

        if (storedDataList.size() == 0 || isDatachanged) {
            Log.i(TAG, "storeData insert");
            MakeBlobTask blobTask = new MakeBlobTask();
            blobTask.execute(topDataList);
        }
    }
    
    private void executeTransaction(List<TopDataInfo> list) {
        Log.i(TAG, "executeTransaction");
        DataUtil.setTopDataList(context, list, this);
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
                    DataUtil.setTopDataList(context, null, TopDataClient.this);
                }
            }

            @Override
            public void onNotifyNetworkSettingsError() {
                // TODO Auto-generated method stub
                stopRefreshData();
                DataUtil.setTopDataList(context, null, TopDataClient.this);
            }

        });
	}
}

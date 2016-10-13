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
import com.cookingshow.service.data.NavigationDataInfo;
import com.cookingshow.service.parser.NavigationDataParser;
import com.cookingshow.service.parser.ParserUtil;
import com.cookingshow.service.util.ConnectionManager.NetworkListener;
import com.cookingshow.service.util.NetworkCheckTask;

public class NavigationDataClient extends BaseClient {
    private static final String TAG = "NavigationDataClient";

    private final int defautMsg = 0;
    private NavigationDataTask task = null;

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
        handlerThread = new HandlerThread("NavigationDataClient");
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
                    task = new NavigationDataTask(conn);
                    task.execute();

                    nRetryCnt = 1;
                } else {
                    if (nRetryCnt <= 0) {
                        // check network
                        new NetworkCheckTask() {
                            protected void onPostExecute(Boolean result) {
                                if (result) {
                                    // service is not available!
                                    DataUtil.setNavigationDataList(context, null, NavigationDataClient.this);
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

    class NavigationDataTask extends AsyncTask<Object, Void, List<NavigationDataInfo>> {
        private HttpURLConnection conn;

        public NavigationDataTask(HttpURLConnection conn) {
            this.conn = conn;
        }

        @Override
        protected List<NavigationDataInfo> doInBackground(Object... params) {
            // TODO Auto-generated method stub
        	NavigationDataParser parser = new NavigationDataParser();
            List<NavigationDataInfo> dataList = new ArrayList<NavigationDataInfo>();
            dataList = parser.getNavigationDataList(conn);

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

        protected void onPostExecute(List<NavigationDataInfo> result) {
            storeData(result);
            conn.disconnect();
            conn = null;
            //refreshData();
        }
    }

    class MakeBlobTask extends AsyncTask<List<NavigationDataInfo>, Void, List<NavigationDataInfo>> {

        @Override
        protected List<NavigationDataInfo> doInBackground(List<NavigationDataInfo>... params) {
            // TODO Auto-generated method stub
            List<NavigationDataInfo> dataList = (List<NavigationDataInfo>) params[0];

            return dataList;
        }

        protected void onPostExecute(List<NavigationDataInfo> result) {
            executeTransaction(result);
        }
    }

    private void storeData(List<NavigationDataInfo> navigationDataList) {
        Log.i(TAG, "storeData");
        boolean isDatachanged = false;
        List<NavigationDataInfo> storedDataList = DataUtil.getNavigationDataList(context, this);
        int i, j;

        for (i = 0; !isDatachanged && i < storedDataList.size(); i++) {
            String storedDataCode = storedDataList.get(i).getCode();

            for (j = 0; j < navigationDataList.size(); j++) {
                String naviDataCode = navigationDataList.get(j).getCode();

                if (storedDataCode.equals(naviDataCode)) {
                    break;
                }
            }

            if (j == navigationDataList.size()) {
                isDatachanged = true;
            }
        }

        if (storedDataList.size() == 0 || isDatachanged) {
            Log.i(TAG, "storeData insert");
            MakeBlobTask blobTask = new MakeBlobTask();
            blobTask.execute(navigationDataList);
        }

    }

    private void executeTransaction(List<NavigationDataInfo> list) {
        Log.i(TAG, "executeTransaction");
        DataUtil.setNavigationDataList(context, list, this);
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
                    DataUtil.setNavigationDataList(context, null, NavigationDataClient.this);
                }
            }

            @Override
            public void onNotifyNetworkSettingsError() {
                // TODO Auto-generated method stub
                stopRefreshData();
                DataUtil.setNavigationDataList(context, null, NavigationDataClient.this);
            }

        });
    }
}

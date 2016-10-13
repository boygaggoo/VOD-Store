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
import com.cookingshow.service.data.UpdateDataInfo;
import com.cookingshow.service.parser.ParserUtil;
import com.cookingshow.service.parser.UpdateParser;
import com.cookingshow.service.util.ConnectionManager.NetworkListener;
import com.cookingshow.service.util.NetworkCheckTask;

public class UpdateClient extends BaseClient {
    private static final String TAG = "UpdateClient";

    private final int defautMsg = 0;
    private UpdateTask task = null;

    private HandlerThread handlerThread;
    private Handler handler;

    @Override
    public void refreshData(long timer) {
        // TODO Auto-generated method stub
        if (DataUtil.isExistUpdateData(context, this)) {
        	DataUtil.removeUpdateDataList(context, this);
        }

        if (handler != null && conManager.isConnected()) {
            handler.removeMessages(defautMsg);
            handler.sendMessageDelayed(handler.obtainMessage(defautMsg), timer);
        }
    }

    @Override
    public void gatheringData() {
        // TODO Auto-generated method stub
        handlerThread = new HandlerThread("UpdateClient");
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
                    task = new UpdateTask(conn);
                    task.execute();

                    nRetryCnt = 1;
                } else {
                    if (nRetryCnt <= 0) {
                        // check network
                        new NetworkCheckTask() {
                            protected void onPostExecute(Boolean result) {
                                if (result) {
                                    // service is not available!
                                    DataUtil.setUpdateDataList(context, null, UpdateClient.this);
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

    class UpdateTask extends AsyncTask<Object, Void, List<UpdateDataInfo>> {
        private HttpURLConnection conn;

        public UpdateTask(HttpURLConnection conn) {
            this.conn = conn;
        }

        @Override
        protected List<UpdateDataInfo> doInBackground(Object... params) {
            // TODO Auto-generated method stub
            UpdateParser parser = new UpdateParser();
            List<UpdateDataInfo> dataList = new ArrayList<UpdateDataInfo>();
            dataList = parser.getUpdateDataList(conn);

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

        protected void onPostExecute(List<UpdateDataInfo> result) {
            storeData(result);
            conn.disconnect();
            conn = null;
            //refreshData();
        }
    }

    class MakeBlobTask extends AsyncTask<List<UpdateDataInfo>, Void, List<UpdateDataInfo>> {

        @Override
        protected List<UpdateDataInfo> doInBackground(List<UpdateDataInfo>... params) {
            // TODO Auto-generated method stub
            List<UpdateDataInfo> dataList = (List<UpdateDataInfo>) params[0];

            return dataList;
        }

        protected void onPostExecute(List<UpdateDataInfo> result) {
            executeTransaction(result);
        }
    }

    private void storeData(List<UpdateDataInfo> updateDataList) {
        Log.i(TAG, "storeData");
        boolean isDatachanged = false;
        List<UpdateDataInfo> storedDataList = DataUtil.getUpdateDataList(context, this);
        int i, j;

        for (i = 0; !isDatachanged && i < storedDataList.size(); i++) {
            String storedDataName = storedDataList.get(i).getName();

            for (j = 0; j < updateDataList.size(); j++) {
                String updateDataName = updateDataList.get(j).getName();

                if (storedDataName.equals(updateDataName)) {
                    break;
                }
            }

            if (j == updateDataList.size()) {
                isDatachanged = true;
            }
        }

        if (storedDataList.size() == 0 || isDatachanged) {
            Log.i(TAG, "storeData insert");
            MakeBlobTask blobTask = new MakeBlobTask();
            blobTask.execute(updateDataList);
        }

    }

    private void executeTransaction(List<UpdateDataInfo> list) {
        Log.i(TAG, "executeTransaction");
        DataUtil.setUpdateDataList(context, list, this);
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
                    DataUtil.setUpdateDataList(context, null, UpdateClient.this);
                }
            }

            @Override
            public void onNotifyNetworkSettingsError() {
                // TODO Auto-generated method stub
                stopRefreshData();
                DataUtil.setUpdateDataList(context, null, UpdateClient.this);
            }

        });
    }

}

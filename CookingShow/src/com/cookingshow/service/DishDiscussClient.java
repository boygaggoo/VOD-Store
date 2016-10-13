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
import android.widget.Toast;

import com.cookingshow.service.data.DataUtil;
import com.cookingshow.service.data.DishDiscussInfo;
import com.cookingshow.service.parser.DishDiscussParser;
import com.cookingshow.service.parser.ParserUtil;
import com.cookingshow.service.util.ConnectionManager.NetworkListener;
import com.cookingshow.service.util.NetworkCheckTask;

public class DishDiscussClient extends BaseClient {

    private static final String TAG = "DishDiscussClient";

    private final int defautMsg = 0;
    private DishDiscussTask task = null;

    private HandlerThread handlerThread;
    private Handler handler;
    
	@Override
	public void refreshData(long timer) {
		// TODO Auto-generated method stub
        if (DataUtil.isExistDishDiscussData(context, this)) {
        	DataUtil.removeDishDiscussDataList(context, this);
        }
        
        if (handler != null && conManager.isConnected()) {
            handler.removeMessages(defautMsg);
            handler.sendMessageDelayed(handler.obtainMessage(defautMsg), timer);
        }
	}

	@Override
	public void gatheringData() {
		// TODO Auto-generated method stub
        handlerThread = new HandlerThread("DishDiscussClient");
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
                    task = new DishDiscussTask(conn);
                    task.execute();

                    nRetryCnt = 1;
                } else {
                    if (nRetryCnt <= 0) {
                        // check network
                        new NetworkCheckTask() {
                            protected void onPostExecute(Boolean result) {
                                if (result) {
                                    // service is not available!
                                    DataUtil.setDishDiscussDataList(context, null, DishDiscussClient.this);
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

    class DishDiscussTask extends AsyncTask<Object, Void, List<DishDiscussInfo>> {
        private HttpURLConnection conn;

        public DishDiscussTask(HttpURLConnection conn) {
            this.conn = conn;
        }

        @Override
        protected List<DishDiscussInfo> doInBackground(Object... params) {
            // TODO Auto-generated method stub
        	DishDiscussParser parser = new DishDiscussParser();
            List<DishDiscussInfo> dataList = new ArrayList<DishDiscussInfo>();
            dataList = parser.getDishDiscussList(conn);

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

        protected void onPostExecute(List<DishDiscussInfo> result) {
            storeData(result);
            conn.disconnect();
            conn = null;
            //refreshData();
        }
    }

    private void storeData(List<DishDiscussInfo> dataList) {
        Log.i(TAG, "storeData");        
        //Toast.makeText(context, String.valueOf(dataList.size()), Toast.LENGTH_LONG).show();
        
        boolean isDatachanged = false;
        List<DishDiscussInfo> storedDataList = DataUtil.getDishDiscussDataList(context, this);
        int i = 0;

        if(storedDataList.size() != dataList.size()) {
        	isDatachanged = true;
        }
        else {
        	for(i = 0; i < dataList.size(); i++) {
        		if(dataList.get(i).getReceiver() != storedDataList.get(i).getReceiver() ||
        		   dataList.get(i).getSender() != storedDataList.get(i).getSender() ||
        		   dataList.get(i).getContent() != storedDataList.get(i).getContent()) {
        			isDatachanged = true;
        			break;
        		}
        	}
        }

        if (storedDataList.size() == 0 || isDatachanged) {
            Log.i(TAG, "storeData insert");
            DataUtil.setDishDiscussDataList(context, dataList, this);
        }
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
                    DataUtil.setDishDiscussDataList(context, null, DishDiscussClient.this);
                }
            }

            @Override
            public void onNotifyNetworkSettingsError() {
                // TODO Auto-generated method stub
                stopRefreshData();
                DataUtil.setDishDiscussDataList(context, null, DishDiscussClient.this);
            }

        });
	}

}

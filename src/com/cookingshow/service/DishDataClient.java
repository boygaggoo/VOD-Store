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
import com.cookingshow.service.data.DishDataInfo;
import com.cookingshow.service.parser.DishDataParser;
import com.cookingshow.service.parser.ParserUtil;
import com.cookingshow.service.util.ConnectionManager.NetworkListener;
import com.cookingshow.service.util.NetworkCheckTask;

public class DishDataClient extends BaseClient {
    private static final String TAG = "DishDataClient";

    private final int defautMsg = 0;
    private DishDataTask task = null;

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
        handlerThread = new HandlerThread("DishDataClient");
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
                    task = new DishDataTask(conn);
                    task.execute();

                    nRetryCnt = 1;
                } else {
                    if (nRetryCnt <= 0) {
                        // check network
                        new NetworkCheckTask() {
                            protected void onPostExecute(Boolean result) {
                                if (result) {
                                    // service is not available!
                                    DataUtil.setDishDataList(context, null, DishDataClient.this);
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

    class DishDataTask extends AsyncTask<Object, Void, List<DishDataInfo>> {
        private HttpURLConnection conn;

        public DishDataTask(HttpURLConnection conn) {
            this.conn = conn;
        }

        @Override
        protected List<DishDataInfo> doInBackground(Object... params) {
            // TODO Auto-generated method stub
        	DishDataParser parser = new DishDataParser();
            List<DishDataInfo> dataList = new ArrayList<DishDataInfo>();
            dataList = parser.getDishDataList(conn);

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

        protected void onPostExecute(List<DishDataInfo> result) {
            storeData(result);
            if (conn != null) {
                try {
                    conn.disconnect();
                    conn = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            refreshData();
        }
    }

    class MakeBlobTask extends AsyncTask<List<DishDataInfo>, Void, List<DishDataInfo>> {

        @Override
        protected List<DishDataInfo> doInBackground(List<DishDataInfo>... params) {
            // TODO Auto-generated method stub
            List<DishDataInfo> dataList = (List<DishDataInfo>) params[0];
            /*for (DishDataInfo dishDataInfo : dataList) {
            	dishDataInfo.setThumbBlob(makePngIconBlob(getBitmapFromURL(dishDataInfo
                        .getThumbUrl())));
            }*/
            return dataList;
        }

        protected void onPostExecute(List<DishDataInfo> result) {
            executeTransaction(result);
        }
    }

    class MakeTask extends AsyncTask<List<DishDataInfo>, Void, List<DishDataInfo>> {

        @Override
        protected List<DishDataInfo> doInBackground(List<DishDataInfo>... params) {
            // TODO Auto-generated method stub
            List<DishDataInfo> dataList = (List<DishDataInfo>) params[0];

            return dataList;
        }

        protected void onPostExecute(List<DishDataInfo> result) {
            executeTransaction2(result);
        }
    }

    private void storeData(List<DishDataInfo> dishDataList) {
        Log.i(TAG, "storeData");
        boolean isNewData = false;
        List<DishDataInfo> pendingAddDataList = new ArrayList<DishDataInfo>();
        List<DishDataInfo> pendingUpdateDataList = new ArrayList<DishDataInfo>();
        List<DishDataInfo> storedDataList = DataUtil.getDishDataList(context, this);
        int i = 0, j = 0;

        for(i = 0; i < dishDataList.size(); i++) {
        	isNewData = true;
        	int dishId = dishDataList.get(i).getDishId();
        	
        	for (j = 0; j < storedDataList.size(); j++) {
        		int storeDishId = storedDataList.get(j).getDishId();
        		//Log.i(TAG, "dishId:" + dishId + " storeDishId:" + storeDishId);
        		if(dishId == storeDishId) {
        			if(!dishDataList.get(i).getTitle().equals(storedDataList.get(j).getTitle()) ||
        					!dishDataList.get(i).getTips().equals(storedDataList.get(j).getTips()) ||
        					!dishDataList.get(i).getMaterials().equals(storedDataList.get(j).getMaterials()) ||
        					!dishDataList.get(i).getThumbUrl().equals(storedDataList.get(j).getThumbUrl())) {
        				pendingUpdateDataList.add(dishDataList.get(i));
        			}
        			
        			isNewData = false;
        			break;
        		}
        	}
        	
        	if(isNewData) {
        		pendingAddDataList.add(dishDataList.get(i));
        	}
        }
        
        if (pendingAddDataList.size() > 0) {
            Log.i(TAG, "storeData insert");
            MakeBlobTask blobTask = new MakeBlobTask();
            blobTask.execute(pendingAddDataList);
        }
        
        if (pendingUpdateDataList.size() > 0) {
        	Log.i(TAG, "storeData update");
        	MakeTask task = new MakeTask();
        	task.execute(pendingUpdateDataList);
        }
    }
    
    private void executeTransaction(List<DishDataInfo> list) {
        Log.i(TAG, "executeTransaction");
        DataUtil.setDishDataList(context, list, this);
    }

    private void executeTransaction2(List<DishDataInfo> list) {
        Log.i(TAG, "executeTransaction2");
        DataUtil.updateDishDataList(context, list, this);
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
                    DataUtil.setDishDataList(context, null, DishDataClient.this);
                }
            }

            @Override
            public void onNotifyNetworkSettingsError() {
                // TODO Auto-generated method stub
                stopRefreshData();
                DataUtil.setDishDataList(context, null, DishDataClient.this);
            }

        });
	}

}

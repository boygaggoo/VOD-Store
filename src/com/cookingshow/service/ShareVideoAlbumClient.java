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
import com.cookingshow.service.data.ShareVideoAlbumInfo;
import com.cookingshow.service.parser.ParserUtil;
import com.cookingshow.service.parser.ShareVideoAlbumParser;
import com.cookingshow.service.util.ConnectionManager.NetworkListener;
import com.cookingshow.service.util.NetworkCheckTask;

public class ShareVideoAlbumClient extends BaseClient {
    private static final String TAG = "ShareVideoAlbumClient";

    private final int defautMsg = 0;
    private VideoAlbumTask task = null;

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
        handlerThread = new HandlerThread("ShareVideoAlbumClient");
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
                    task = new VideoAlbumTask(conn);
                    task.execute();

                    nRetryCnt = 1;
                } else {
                    if (nRetryCnt <= 0) {
                        // check network
                        new NetworkCheckTask() {
                            protected void onPostExecute(Boolean result) {
                                if (result) {
                                    // service is not available!
                                    DataUtil.setVideoAlbumDataList(context, null, ShareVideoAlbumClient.this);
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

    class VideoAlbumTask extends AsyncTask<Object, Void, List<ShareVideoAlbumInfo>> {
        private HttpURLConnection conn;

        public VideoAlbumTask(HttpURLConnection conn) {
            this.conn = conn;
        }

        @Override
        protected List<ShareVideoAlbumInfo> doInBackground(Object... params) {
            // TODO Auto-generated method stub
        	ShareVideoAlbumParser parser = new ShareVideoAlbumParser();
            List<ShareVideoAlbumInfo> dataList = new ArrayList<ShareVideoAlbumInfo>();
            dataList = parser.getVideoAlbumDataList(conn);

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

        protected void onPostExecute(List<ShareVideoAlbumInfo> result) {
            storeData(result);
            conn.disconnect();
            conn = null;
            refreshData();
        }
    }

    class MakeBlobTask extends AsyncTask<List<ShareVideoAlbumInfo>, Void, List<ShareVideoAlbumInfo>> {

        @Override
        protected List<ShareVideoAlbumInfo> doInBackground(List<ShareVideoAlbumInfo>... params) {
            // TODO Auto-generated method stub
            List<ShareVideoAlbumInfo> dataList = (List<ShareVideoAlbumInfo>) params[0];

            return dataList;
        }

        protected void onPostExecute(List<ShareVideoAlbumInfo> result) {
            executeTransaction(result);
        }
    }

    class MakeTask extends AsyncTask<List<ShareVideoAlbumInfo>, Void, List<ShareVideoAlbumInfo>> {

        @Override
        protected List<ShareVideoAlbumInfo> doInBackground(List<ShareVideoAlbumInfo>... params) {
            // TODO Auto-generated method stub
            List<ShareVideoAlbumInfo> dataList = (List<ShareVideoAlbumInfo>) params[0];

            return dataList;
        }

        protected void onPostExecute(List<ShareVideoAlbumInfo> result) {
            executeTransaction2(result);
        }
    }

    private void storeData(List<ShareVideoAlbumInfo> albumDataList) {
        Log.i(TAG, "storeData");
        boolean isNewData = false;
        List<ShareVideoAlbumInfo> pendingAddDataList = new ArrayList<ShareVideoAlbumInfo>();
        List<ShareVideoAlbumInfo> pendingUpdateDataList = new ArrayList<ShareVideoAlbumInfo>();
        List<ShareVideoAlbumInfo> storedDataList = DataUtil.getVideoAlbumDataList(context, this);
        int i = 0, j = 0;

        for(i = 0; i < albumDataList.size(); i++) {
        	isNewData = true;
        	int albumId = albumDataList.get(i).getAlbumId();
        	
        	for (j = 0; j < storedDataList.size(); j++) {
        		int storeDishId = storedDataList.get(j).getAlbumId();
        		//Log.i(TAG, "dishId:" + dishId + " storeDishId:" + storeDishId);
        		if(albumId == storeDishId) {
        			if(!albumDataList.get(i).getTitle().equals(storedDataList.get(j).getTitle()) ||
        					!albumDataList.get(i).getThumbUrl().equals(storedDataList.get(j).getThumbUrl()) ||
        					!albumDataList.get(i).getUploadTime().equals(storedDataList.get(j).getUploadTime())) {
        				pendingUpdateDataList.add(albumDataList.get(i));
        			}
        			
        			isNewData = false;
        			break;
        		}
        	}
        	
        	if(isNewData) {
        		pendingAddDataList.add(albumDataList.get(i));
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
    
    private void executeTransaction(List<ShareVideoAlbumInfo> list) {
        Log.i(TAG, "executeTransaction");
        DataUtil.setVideoAlbumDataList(context, list, this);
    }

    private void executeTransaction2(List<ShareVideoAlbumInfo> list) {
        Log.i(TAG, "executeTransaction2");
        DataUtil.updateVideoAlbumDataList(context, list, this);
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
                    DataUtil.setVideoAlbumDataList(context, null, ShareVideoAlbumClient.this);
                }
            }

            @Override
            public void onNotifyNetworkSettingsError() {
                // TODO Auto-generated method stub
                stopRefreshData();
                DataUtil.setVideoAlbumDataList(context, null, ShareVideoAlbumClient.this);
            }

        });
	}
}

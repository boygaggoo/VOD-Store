package com.cookingshow.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.cookingshow.service.util.ConnectionManager;

public class DataService extends Service {
    private static final String TAG = "DataService";

    public static final String ACTION_COOK_STEP = "com.cookingshow.service.ui.request.cookstep";
    public static final String ACTION_DISH_DISCUSS = "com.cookingshow.service.ui.request.dishdiscuss";
    public static final String ACTION_SHARE_VIDEO = "com.cookingshow.service.ui.request.sharevideo";
    public static final String ACTION_SHARE_IMG = "com.cookingshow.service.ui.request.shareimg";
    public static final String ACTION_SHARE_IMG_USER = "com.cookingshow.service.ui.request.shareimg.user";
    public static final String ACTION_VIEW_RECORD = "com.cookingshow.service.ui.request.viewrecord";

    private ArrayList<BaseClient> mClientList = new ArrayList<BaseClient>();
    private BaseClient mCookStepClient = null;
    private BaseClient mDishDiscussClient = null;
    private BaseClient mShareVideoClient = null;
    private BaseClient mShareImgClient = null;
    private BaseClient mViewRecordClient = null;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();

        registerUiBroadcastReceiver();

        startService();
    }

    public void onDestroy() {
        ConnectionManager.getConnectionManager(getApplicationContext())
                .unregisterNetworkBroadcastReceiver();
        destroyAllClient();

        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        requestRefreshAllClient();

        return START_STICKY;
    }

    private void requestRefreshAllClient() {
        for (BaseClient client : mClientList) {
            client.refreshData(0);
        }
    }

    private void destroyAllClient() {
        for (int i = mClientList.size() - 1; i >= 0; i--) {
            BaseClient client = mClientList.get(i);
            mClientList.remove(i);

            client.cancelGathering();
            client = null;
        }
        
        unregisterNetworkBroadcastReceiver();
    }

    private void startService() {
        BaseClient client = null;

        client = createNavigationDataModel(NavigationDataClient.class);
        if (client != null) {
            client.gatheringData();
            client.setNetworkListener();
            mClientList.add(client);
        }

        client = createRecommendDataModel(RecommendDataClient.class);
        if (client != null) {
            client.gatheringData();
            client.setNetworkListener();
            mClientList.add(client);
        }
        
        client = createTopDataModel(TopDataClient.class);
        if (client != null) {
            client.gatheringData();
            client.setNetworkListener();
            mClientList.add(client);
        }

        client = createShareVideoAlbumDataModel(ShareVideoAlbumClient.class);
        if (client != null) {
            client.gatheringData();
            client.setNetworkListener();
            mClientList.add(client);
        }

        client = createDishDataModel(DishDataClient.class);
        if (client != null) {
            client.gatheringData();
            client.setNetworkListener();
            mClientList.add(client);
        }

        client = createUpdateModel(UpdateClient.class);
        if (client != null) {
            client.gatheringData();
            client.setNetworkListener();
            mClientList.add(client);
        }

        client = createBootScreenModel(BootScreenClient.class);
        if (client != null) {
            client.gatheringData();
            client.setNetworkListener();
            mClientList.add(client);
        }

        mCookStepClient = createCookStepDataModel(CookStepClient.class);
        if (mCookStepClient != null) {
        	mCookStepClient.gatheringData();
        	mCookStepClient.setNetworkListener();
        }
        
        mDishDiscussClient = createDishDiscussDataModel(DishDiscussClient.class);
        if (mDishDiscussClient != null) {
        	mDishDiscussClient.gatheringData();
        	mDishDiscussClient.setNetworkListener();
        }

        mShareVideoClient = createShareVideoDataModel(ShareVideoClient.class);
        if (mShareVideoClient != null) {
        	mShareVideoClient.gatheringData();
        	mShareVideoClient.setNetworkListener();
        }

        mShareImgClient = createShareImgDataModel(ShareImgClient.class);
        if (mShareImgClient != null) {
        	mShareImgClient.gatheringData();
        	mShareImgClient.setNetworkListener();
        }
        
        mViewRecordClient = createViewRecordDataModel(ViewRecordClient.class);
        if (mViewRecordClient != null) {
        	mViewRecordClient.gatheringData();
        	mViewRecordClient.setNetworkListener();
        }
    }

    private BaseClient createUpdateModel(Class<UpdateClient> clientClass) {
        BaseClient client = null;

        String contentId = "update";
        String title = "Update";
        String className = "UpdateClient";
        String api = "http://182.92.198.90/tv/update/apkInfo_cookingShow_dangbei.xml";
        int timer = 300000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }
    
    private BaseClient createBootScreenModel(Class<BootScreenClient> clientClass) {
        BaseClient client = null;

        String contentId = "bootScreen";
        String title = "BootScreen";
        String className = "BootScreenClient";
        String api = "http://182.92.198.90/tv/bootScreen/bootScreenInfo_cookingShow.xml";
        int timer = 1800000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }
    
    private BaseClient createDishDataModel(Class<DishDataClient> clientClass) {
        BaseClient client = null;

        String contentId = "dishData";
        String title = "DishData";
        String className = "DishDataClient";
        String api = "http://182.92.198.90/test/transaction/getDishesInfo.php";
        int timer = 1800000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }

    private BaseClient createTopDataModel(Class<TopDataClient> clientClass) {
        BaseClient client = null;

        String contentId = "topData";
        String title = "TopData";
        String className = "TopDataClient";
        String api = "http://182.92.198.90/test/transaction/getTopDishesInfo.php";
        int timer = 1800000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }

    private BaseClient createRecommendDataModel(Class<RecommendDataClient> clientClass) {
        BaseClient client = null;

        String contentId = "recommendData";
        String title = "RecommendData";
        String className = "RecommendDataClient";
        String api = "http://182.92.198.90/test/transaction/getRecommendDishesInfo.php";
        int timer = 1800000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }
    
    private BaseClient createNavigationDataModel(Class<NavigationDataClient> clientClass) {
        BaseClient client = null;

        String contentId = "navigationData";
        String title = "NavigationData";
        String className = "NavigationDataClient";
        String api = "http://182.92.198.90/test/transaction/getNavigationInfo.php";
        int timer = 1800000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }

    private BaseClient createCookStepDataModel(Class<CookStepClient> clientClass) {
        BaseClient client = null;

        String contentId = "cookStepData";
        String title = "CookStepData";
        String className = "CookStepClient";
        String api = "http://182.92.198.90/test/transaction/getCookStep.php";
        int timer = 1800000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }

    private BaseClient createDishDiscussDataModel(Class<DishDiscussClient> clientClass) {
        BaseClient client = null;

        String contentId = "dishDiscussData";
        String title = "DishDiscussData";
        String className = "DishDiscussClient";
        String api = "http://182.92.198.90/test/transaction/getDiscussInfo.php";
        int timer = 1800000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }

    private BaseClient createShareImgDataModel(Class<ShareImgClient> clientClass) {
        BaseClient client = null;

        String contentId = "shareImgData";
        String title = "ShareImgData";
        String className = "ShareImgClient";
        String api = "http://182.92.198.90/test/transaction/getShareImgInfo.php";
        int timer = 60000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }

    private BaseClient createViewRecordDataModel(Class<ViewRecordClient> clientClass) {
        BaseClient client = null;

        String contentId = "viewRecordData";
        String title = "ViewRecordData";
        String className = "ViewRecordClient";
        String api = "http://182.92.198.90/test/transaction/getUserViewRecord.php";
        int timer = 60000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }

    private BaseClient createShareVideoAlbumDataModel(Class<ShareVideoAlbumClient> clientClass) {
        BaseClient client = null;

        String contentId = "shareVideoAlbumData";
        String title = "ShareVideoAlbumData";
        String className = "ShareVideoAlbumClient";
        String api = "http://182.92.198.90/test/transaction/getShareVideoInfo.php";
        int timer = 1800000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            client.setApiParam("type=album");
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }

    private BaseClient createShareVideoDataModel(Class<ShareVideoClient> clientClass) {
        BaseClient client = null;

        String contentId = "shareVideoData";
        String title = "ShareVideoData";
        String className = "ShareVideoClient";
        String api = "http://182.92.198.90/test/transaction/getShareVideoInfo.php";
        int timer = 60000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            //client.setApiParam("type=album");
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }

    private BroadcastReceiver uiBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        	int id = 0;
        	String deviceId = null;
        	String account = null;
        	
        	if(intent.getAction().equals(ACTION_COOK_STEP)) {
        		id = intent.getExtras().getInt("appId");
            	mCookStepClient.setApiParam("dish_id=" + id);
            	mCookStepClient.refreshData(0);       		
        	}
        	else if(intent.getAction().equals(ACTION_DISH_DISCUSS)) {
        		id = intent.getExtras().getInt("appId");
        		mDishDiscussClient.setApiParam("dish_id=" + id);
        		mDishDiscussClient.refreshData(0);       		
        	}
        	else if(intent.getAction().equals(ACTION_SHARE_VIDEO)) {
        		id = intent.getExtras().getInt("albumId");
        		mShareVideoClient.setApiParam("type=detail_album&album_id=" + id);
        		mShareVideoClient.refreshData(0);      		
        	}
        	else if(intent.getAction().equals(ACTION_SHARE_IMG)) {
        		id = intent.getExtras().getInt("appId");
        		mShareImgClient.setApiParam("type=dish&dish_id=" + id);
        		mShareImgClient.refreshData(0);          		
        	}
        	else if(intent.getAction().equals(ACTION_SHARE_IMG_USER)) {
        		account = intent.getExtras().getString("account");
        		mShareImgClient.setApiParam("type=user&user_account=" + account);
        		mShareImgClient.refreshData(0);          		
        	}
        	else if(intent.getAction().equals(ACTION_VIEW_RECORD)) {
        		deviceId = intent.getExtras().getString("deviceId");
        		mViewRecordClient.setApiParam("user=" + deviceId);
        		mViewRecordClient.refreshData(0);          		
        	}
        }
    };
 
    private void registerUiBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_COOK_STEP);
        filter.addAction(ACTION_DISH_DISCUSS);
        filter.addAction(ACTION_SHARE_VIDEO);
        filter.addAction(ACTION_SHARE_IMG);
        filter.addAction(ACTION_SHARE_IMG_USER);
        filter.addAction(ACTION_VIEW_RECORD);
        getApplicationContext().registerReceiver(uiBroadcastReceiver, filter);    	
    }

    public void unregisterNetworkBroadcastReceiver() {
        if (uiBroadcastReceiver != null) {
        	getApplicationContext().unregisterReceiver(uiBroadcastReceiver);
            uiBroadcastReceiver = null;
        }
    }
}

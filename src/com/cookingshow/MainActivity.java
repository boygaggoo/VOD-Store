package com.cookingshow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.cookingshow.all.AllTypeFragment;
import com.cookingshow.datacenter.MenuItem;
import com.cookingshow.datacenter.NavigationDataProvider;
import com.cookingshow.datacenter.UpdateDataProvider;
import com.cookingshow.mine.MineMainFragment;
import com.cookingshow.navigation.MainNavigationItem;
import com.cookingshow.navigation.NavigationBar;
import com.cookingshow.navigation.NavigationData;
import com.cookingshow.navigation.NavigationItemView;
import com.cookingshow.network.cache.ACache;
import com.cookingshow.network.controller.CommonStringLoader;
import com.cookingshow.network.controller.CommonVolley;
import com.cookingshow.network.exception.CommonException;
import com.cookingshow.network.model.BaseStringRequest;
import com.cookingshow.page.ActionEventMgr;
import com.cookingshow.recommend.RecommendMainFragment;
import com.cookingshow.share.ShareMainFragment;
import com.cookingshow.top.TopMainFragment;
import com.cookingshow.view.BigButton;
import com.cookingshow.view.PageNumView;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseActivity implements BaseMainFragment.OnFragmentActionListener {
    private static final String TAG = "CookingMain";
    private LinearLayout loadingView;
    private BigButton bindWeixinBtn = null;
    //private BigButton searchButton;
    private TextView mUpdateCntView = null;
    private TextView mAppVers = null;
    private PageNumView mPageNumView = null;
    private List<NavigationData> naviData = null;
    private NavigationBar navigationBar;
    private MainNavigationItem mCurNavItem = null;
    private String curFragmentTag = "";
    private boolean isDestroyed;
    private long lastClickTime;
    private BaseMainFragment mRecommendFragment = null;
    private BaseMainFragment mTopFragment = null;
    private BaseMainFragment mShareFragment = null;
    private BaseMainFragment mAllFragment = null;
    private BaseMainFragment mMineFragment = null;
    public static final int NAVI_ITEM_ID = 80000;
    private static final String FRAGMENT_TAG_RECOMMEND = "fragment_recommend";
    private static final String FRAGMENT_TAG_TOP = "fragment_top";
    private static final String FRAGMENT_TAG_SHARE = "fragment_share";
    private static final String FRAGMENT_TAG_ALL = "fragment_all";
    private static final String FRAGMENT_TAG_MINE = "fragment_mine";

    private static UpdateDataProvider mUpdateDataProvider;
    private static NavigationDataProvider mNavigationDataProvider;
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private boolean cancelUpdate = false;
    private int progress;
    private String mSavePath;
    private String mUpdateUrl;
    private String mUpdateName;
    private ACache mCache = null;
    private String mDeviceId = null;
    private CommonStringLoader mStringLoader = null;
    private Handler mHandler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startService(getApplicationContext());

		addLauncherFragment();
		setContentView(R.layout.activity_main);

        mUpdateDataProvider = new UpdateDataProvider(MainActivity.this, mMainHandler);
        mNavigationDataProvider = new NavigationDataProvider(MainActivity.this);
        
        CommonVolley.getInstance().init(MainActivity.this);
        mCache = ACache.get(MainActivity.this);
        mStringLoader = CommonStringLoader.getInstance();

        MobclickAgent.updateOnlineConfig(this);
        AnalyticsConfig.enableEncrypt(true);
        MobclickAgent.setDebugMode(false);
        AnalyticsConfig.setChannel("tv_dangbei");

        initWidget();
        setListener();
        registerReceiver();

        mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
            	initNetwork();
            }
        });
	}
  
    public void deleteLauncherFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.launcher_layout);
        if (fragment != null) {
            ft.remove(fragment);
            ft.commitAllowingStateLoss();
        }
        Log.d(TAG, "deleteLauncherFragment");
    }

    public void addLauncherFragment() {
        Fragment launcherFragment = new LauncherFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.launcher_layout, launcherFragment);
        ft.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
        Log.d(TAG, "addLauncherFragment");
    }

    public void loadContentFinished() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.launcher_layout);
        if (fragment != null) {
            ((LauncherFragment)fragment).setDestroyFromMain();
        }
    }
    
    private void initWidget() {
    	loadingView = (LinearLayout) findViewById(R.id.tv_loading);
    	navigationBar = (NavigationBar) findViewById(R.id.main_navigation);
        //searchButton = (BigButton) findViewById(R.id.btn_search);
        bindWeixinBtn = (BigButton) findViewById(R.id.btn_weixin);
        mUpdateCntView = (TextView) findViewById(R.id.update_cnt);
    	mAppVers = (TextView) findViewById(R.id.app_vers);
    	mPageNumView = (PageNumView) findViewById(R.id.page_num_view);
    }

    private void setListener() {
        //ActionEventMgr.getInstance().setOnHoveredListener(searchButton);
        ActionEventMgr.getInstance().setOnHoveredListener(bindWeixinBtn);
        /*searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                //intent.setClass(mContext, TvSearchActivity.class);
                //mContext.startActivity(intent);
            }
        });*/
        bindWeixinBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent();
                //intent.setClass(MainActivity.this, PushTicketActivity.class);
                //startActivity(intent);
            	Toast.makeText(getApplicationContext(), R.string.penging_notice, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction(NotificationUtil.LOCAL_MANAGE_UPDATE_INIT_COMPLETE_ACTION);
        //intentFilter.addAction(LeApp.Constant.Intent.ACTION_CAN_UPDATE);
        registerReceiver(mUpdateAppReciever, intentFilter);
    }

    private boolean isConnected() {
        ConnectivityManager mgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethernetInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        NetworkInfo wifiInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if(ethernetInfo != null && ethernetInfo.isConnected()) {
        	return true;
        }
        else if(wifiInfo != null && wifiInfo.isConnected()) {
        	return true;
        }
        else if(mobileInfo != null && mobileInfo.isConnected()) {
        	return true;
        }
        
        return false;
    }

    private void initNetwork() {
    	if(!isConnected()) {
    		mMainHandler.sendEmptyMessage(6);
    	}
    	else {
            mCache.put("deviceId", getDeviceId());
            mCache.put("deviceDpi", String.valueOf(getDeviceDpi()));

    		mMainHandler.sendEmptyMessage(1);
    		mMainHandler.sendEmptyMessage(5);
    	}    	
    }

    private void loadPageList() {
    	List<MenuItem> menus = filterMenuItems();
        initNavigationBar(menus);
        loadingView.setVisibility(View.GONE);
    }

    private void initNavigationBar(List<MenuItem> menus) {
        naviData = new ArrayList<NavigationData>();
        
        for (int i = 0; i < menus.size(); i++) {
            NavigationData data = new NavigationData();
            data.title = menus.get(i).getName();
            data.id = menus.get(i).getId();
            data.iconId = getNaviIcon(menus.get(i).getCode());
            data.tag = menus.get(i);
            naviData.add(data);
        }
        
        navigationBar.setItemOnClickListener(mNaviItemClickListener);
        if(mCache.getAsString("device").indexOf("mobile") != -1) {
            navigationBar.inflateItems(naviData, R.layout.main_navigation_item, -125);        	
        }
        else {
        	navigationBar.inflateItems(naviData, R.layout.main_navigation_item, 10); 
        }

        navigationBar.setItemFocusListener(new NavigationItemView.OnNavigationItemFocusListener() {
            @Override
            public boolean onFocusDown() {            	
                BaseMainFragment curFragment = (BaseMainFragment) getSupportFragmentManager().
                        findFragmentByTag(curFragmentTag);
                if (curFragment != null) {
                    curFragment.onNaviFocusDown();
                }
                return true;
            }

            @Override
            public boolean onFocusUp() {
                return false;
            }
        });

        navigationBar.onClickItemById(naviData.get(0).getId());
        if (navigationBar.getClickedItem() != null) {
            navigationBar.getClickedItem().requestFocus();
        }

        //refreshUpdateCntView();
    }
    
    private int getNaviIcon(String code) {
        if (code.equals("daily")) {
            return R.drawable.recommend_icon_selector;
        } else if (code.equals("top")) {
            return R.drawable.top_icon_selector;
        } else if (code.equals("cookbook")) {
            return R.drawable.app_icon_selector;
        } else if (code.equals("mine")) {
            return R.drawable.mine_icon_selector;
        } else if (code.equals("share")) {
            return R.drawable.game_icon_selector;
        } else {
            return R.drawable.recommend_icon_selector;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        mStringLoader = null;
        super.onDestroy();
        unregisterReceiver(mUpdateAppReciever);
        
        if (mUpdateDataProvider != null)
        {
        	mUpdateDataProvider.destroy();
        }
 
        if (mNavigationDataProvider != null)
        {
        	mNavigationDataProvider.destroy();
        }
        
        stopService(getApplicationContext());
        MobclickAgent.onKillProcess(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                try {
                    exitConfirm();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void exitConfirm() {
        long now = System.currentTimeMillis();
        if (now - lastClickTime < 2000) {
            new Thread(new Runnable() {

                @Override
                public void run() {                
                	mMainHandler.sendEmptyMessage(0);
                }
            }).start();
        } else {
            lastClickTime = now;          
            Toast.makeText(getApplicationContext(), R.string.main_exit_config_notice, Toast.LENGTH_LONG).show();
        }
    }

    public List<MenuItem> filterMenuItems() {
    	List<MenuItem> menuItemList = null;
    	
    	if (mNavigationDataProvider != null) {
    		menuItemList = mNavigationDataProvider.getMainMenuItem();
    	}

    	return menuItemList;
    }
    
    private OnClickListener mNaviItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCurNavItem != null && mCurNavItem.getId() != v.getId()) {
                onProgressMaxAction(0);
            }
            mCurNavItem = (MainNavigationItem) v;
            MenuItem menuItem = (MenuItem)v.getTag();

            if ("daily".equals(menuItem.getCode())) {
                switchFragment(v, FRAGMENT_TAG_RECOMMEND);                
            } else if("top".equals(menuItem.getCode())) {
            	switchFragment(v, FRAGMENT_TAG_TOP);
            } else if("share".equals(menuItem.getCode())) {
            	switchFragment(v, FRAGMENT_TAG_SHARE);
            } else if("cookbook".equals(menuItem.getCode())) {
            	switchFragment(v, FRAGMENT_TAG_ALL);
            } else if ("mine".equals(menuItem.getCode())) {
                switchFragment(v, FRAGMENT_TAG_MINE);
            }
        }
    };
    
    private void switchFragment(View view, String tag) {
        String idTag = String.valueOf(view.getId());

        if (curFragmentTag.equals(idTag)) {
            return;
        }
        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!TextUtils.isEmpty(curFragmentTag)) {
            int animEnter = R.anim.right_in;
            int animExit = R.anim.left_out;
            if (curFragmentTag != null && curFragmentTag.compareTo(idTag) > 0) {
                animEnter = R.anim.left_in;
                animExit = R.anim.right_out;
            }
            ft.setCustomAnimations(animEnter, animExit);
        } else {
            ft.setCustomAnimations(R.anim.fade_in, 0);
        }
        BaseMainFragment fragment = (BaseMainFragment) getSupportFragmentManager().findFragmentByTag(idTag);
        BaseMainFragment curFragment = (BaseMainFragment) getSupportFragmentManager().findFragmentByTag(curFragmentTag);
        if (curFragment != null) {
            curFragment.setOnFragmentActionListener(null);
            curFragment.onPause();
            curFragment.onHiddenChanged(true);
            ft.hide(curFragment);
        }
        if (fragment == null) {
            if (tag.equals(FRAGMENT_TAG_RECOMMEND)) {
                mRecommendFragment = new RecommendMainFragment();
                fragment = mRecommendFragment;
            } else if (tag.equals(FRAGMENT_TAG_TOP)) {
                mTopFragment = new TopMainFragment();
                fragment = mTopFragment;            	
            } else if (tag.equals(FRAGMENT_TAG_SHARE)) {
            	mShareFragment = new ShareMainFragment();
                fragment = mShareFragment;            	
            } else if (tag.equals(FRAGMENT_TAG_ALL)) {
                mAllFragment = new AllTypeFragment();
                fragment = mAllFragment;
            } else if (tag.equals(FRAGMENT_TAG_MINE)) {
                mMineFragment = new MineMainFragment();
                fragment = mMineFragment;
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(BaseMainFragment.ARGUMENT_DATA_KEY, (MenuItem)view.getTag());
            fragment.setArguments(bundle);
        }

        fragment.setOnFragmentActionListener(this);
        if (!fragment.isAdded()) {
//            fragment.onHiddenChanged(false);
            ft.add(R.id.tv_main_content, fragment, idTag);
        } else {
            fragment.onHiddenChanged(false);
            fragment.onResume();
            ft.show(fragment);
        }
        curFragmentTag = idTag;
        if (!isDestroyed) {
            ft.commitAllowingStateLoss();
        }
        Log.d(TAG, "switchFragment");        
    }

    private BroadcastReceiver mUpdateAppReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshUpdateCntView();
        }
    };

    private void refreshUpdateCntView() {
        //List<Application> apps = AbstractLocalManager.getCanUpdateList();
        //if (apps != null && !apps.isEmpty()) {
            mUpdateCntView.setText("2");
            mUpdateCntView.setVisibility(View.VISIBLE);
        /*} else {
            mUpdateCntView.setVisibility(View.INVISIBLE);
            mUpdateCntView.setText("");
        }*/
    }

    public void switchNavigation(boolean isLeftArrow) {
        if (mCurNavItem != null) {
            int menuId = mCurNavItem.getId();
            if (isLeftArrow) {
                menuId -= 1;
            } else {
                menuId += 1;
            }
            navigationBar.onClickItemById(menuId);
        }
    }

    public boolean isTheFirstMenu() {
        if (mCurNavItem != null && naviData != null) {
            int cId = mCurNavItem.getId();
            if (naviData.get(0).id == cId) {
                return true;
            }
        }
        return false;
    }

    public boolean isTheLastMenu() {
        if (mCurNavItem != null && naviData != null) {
            int cId = mCurNavItem.getId();
            if (naviData.get(naviData.size() - 1).id == cId) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void onProgressChangeAction(int progress) {
        mPageNumView.setCurPageNum(String.valueOf(progress + 1));
        if (mPageNumView.getVisibility() != View.VISIBLE) {
            mPageNumView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProgressMaxAction(int max) {
        if (max > 0) {
            mPageNumView.setSumPageNum(String.valueOf(max));
        } else {
            mPageNumView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFocusUpAction() {
        if (mCurNavItem != null) {
            mCurNavItem.requestFocus();
        }
    }

	private void showUpdateDownloadDialog() {
    	AlertDialog.Builder builder = new Builder(MainActivity.this);
    	builder.setCancelable(false);
    	builder.setTitle(R.string.update_downloading);
    	builder.setMessage(R.string.update_info);

        final LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        
        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
        		dialog.dismiss();
        		cancelUpdate = true;				
			}
		});

        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        
        downloadApk();
    }

    private void downloadApk() {
    	new downloadApkThread().start();
    }

    private class downloadApkThread extends Thread {
    	public void run() {
    		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    			String sdpath = Environment.getExternalStorageDirectory() + "/";
    			mSavePath = sdpath + "Download";
    			try {
					URL url = new URL(mUpdateUrl);
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					conn.connect();
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();
					File file = new File(mSavePath);
					if (!file.exists()) {
						file.mkdir();
					}
					
					File apkFile = new File(mSavePath, mUpdateName + ".apk");
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					byte buf[] = new byte[1024];
					
					do {
						int numread = is.read(buf);
						count += numread;
						progress = (int) (((float) count / length) * 100);

			        	mMainHandler.sendEmptyMessage(3);
			        	
			        	if (numread <= 0) {
			        		mMainHandler.sendEmptyMessage(4);
			        		break;
			        	}
			        	
			        	fos.write(buf, 0, numread);
					}while(!cancelUpdate);
					
                        fos.close();
                        is.close();
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    		}
    		
    		mDownloadDialog.dismiss();
    	}
    }

    private void installApk() {
    	File apkfile = new File(mSavePath, mUpdateName + ".apk");
    	if (!apkfile.exists()) {
    		return;
    	}
    	
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
    	MainActivity.this.startActivity(i);
    }

    private void showNetworkErrorDialog() {
        final Dialog dialog = getNetworkErrorDialog();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        Button okBtn = (Button) dialog.findViewById(R.id.dialog_button_ok);
        Button cancelBtn = (Button) dialog.findViewById(R.id.dialog_button_cancel);
        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mMainHandler.sendEmptyMessage(0);
            }
        });

        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        initNetwork();
                    }
                }, 300);
            }
        });

        dialog.show();
    }

    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isDestroyed) {
                return;
            }
            switch (msg.what) {
                case 0:
                	MainActivity.this.finish();
                	System.exit(0);
                    break;
                case 1:
                	loadPageList();
                	break;
                case 2:
        			Bundle bundle = msg.getData();
        			mUpdateUrl = bundle.getString("updateUrl");
        			mUpdateName = bundle.getString("updateName");
        			showUpdateDownloadDialog();
                	break;
        		case 3:
        			mProgress.setProgress(progress);
        			break;
        		case 4:
        			installApk();
        			break;
        		case 5:
                	String params = "type=setActiveDays&user=" + mCache.getAsString("deviceId") + "&channel=" + mCache.getAsString("device"); 
                	mStringLoader.addRequest(new BaseStringRequest(Method.GET, "http://182.92.198.90/test/transaction/stringRequest.php" + "?" + params, new BaseStringRequest.StringResponseListener() {
            			
            			@Override
            			public void onResponse(String str) {
            				// TODO Auto-generated method stub
            				mCache.put("nickname", str);
            				//Toast.makeText(LauncherFragment.this.getActivity().getApplicationContext(), str, Toast.LENGTH_LONG).show();
            			}
            			
            			@Override
            			public void onErrorResponse(CommonException exception) {
            				// TODO Auto-generated method stub
            				
            			}
            		}));
        			break;
        		case 6:
        			showNetworkErrorDialog();
        			break;
            }
        }

    };

    public String getDeviceId() {    	
    	if(mDeviceId != null ) {
    		return mDeviceId;
    	}
    	else {
    		TelephonyManager telephonyManager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    		String imei=telephonyManager.getDeviceId();
    		if(imei != null && imei.length() == 15) {
    			mDeviceId = imei;
    			mCache.put("device", "mobile_dangbei");
    		}
    		else {
    			mDeviceId = getMacAddress("eth0");
        		if(mDeviceId == null) {
        			mDeviceId = getMacAddress("wlan0");
        			if(mDeviceId == null) {
        				mDeviceId = "other";
        			}
        		}

        		mCache.put("device", "tv_dangbei");
    		}
    	}
    	
    	return mDeviceId;
    }

    private static String getMacAddress(String name) {   	 
        String macAddr = null;
        try {    	 
    	    Enumeration localEnumeration = NetworkInterface.getNetworkInterfaces();
    	  
    	    while (localEnumeration.hasMoreElements()) {
                NetworkInterface localNetworkInterface = (NetworkInterface) localEnumeration.nextElement();
    	        String interfaceName = localNetworkInterface.getDisplayName();
    	        if (interfaceName == null) {
    	            continue;
    	        }
    	        if (interfaceName.equals(name)) {
    	            macAddr = byte2hex(localNetworkInterface.getHardwareAddress());
                    if (macAddr != null && macAddr.startsWith("0:")) {
    	                macAddr = "0" + macAddr;
    	            }
    	            break;
    	        }
    	     }
    	} catch (SocketException e) {
    	     e.printStackTrace();
    	}
    	 
        return macAddr;
    }

    private static String byte2hex(byte[] byteArray) {    	
        StringBuilder sb = new StringBuilder(byteArray.length);
        String stmp = "";
        
    	for (int i = 0; i < byteArray.length; i++) {
    		stmp = Integer.toHexString(byteArray[i] & 0xFF);
    		if(stmp.length() == 1) {
    			sb = sb.append("0").append(stmp);
    		}
    		else {
    			sb = sb.append(stmp);
    		}
            /*
    	    if (i != byteArray.length - 1) {
    	        sb.append(":");
    	    }
    	    */
        }
    	//Log.e("MAC", sb.toString());
        return sb.toString();
    }

    private int getDeviceDpi() {
    	DisplayMetrics dm = new DisplayMetrics();
    	dm = getResources().getDisplayMetrics();
    	
    	//Toast.makeText(LauncherFragment.this.getActivity().getApplicationContext(), "densityDpi:" + dm.densityDpi + " width:" + dm.widthPixels + " height:" + dm.heightPixels, Toast.LENGTH_LONG).show();
    	
    	return dm.densityDpi;
    }

    private void startService(Context context) {
        Log.i(TAG, "startService");
        final Intent serviceIntent = new Intent();
        serviceIntent.setAction("com.cookingshow.service.DataService");
        context.startService(serviceIntent);
    }

    private void stopService(Context context) {
        Log.i(TAG, "stopService");
        final Intent serviceIntent = new Intent();
        serviceIntent.setAction("com.cookingshow.service.DataService");
        context.stopService(serviceIntent);
    }	
}

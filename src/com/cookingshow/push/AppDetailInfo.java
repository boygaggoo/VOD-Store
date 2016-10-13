package com.cookingshow.push;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cookingshow.R;
import com.cookingshow.adapter.AppDetailItemAdapter;
import com.cookingshow.adapter.ListItemClickHelp;
import com.cookingshow.datacenter.CookStepProvider;
import com.cookingshow.datacenter.DishDiscussProvider;
import com.cookingshow.datacenter.ListItem;
import com.cookingshow.datacenter.MenuItem;
import com.cookingshow.navigation.NavigationBar;
import com.cookingshow.navigation.NavigationData;
import com.cookingshow.network.cache.ACache;
import com.cookingshow.network.controller.CommonImageLoader;
import com.cookingshow.network.controller.CommonStringLoader;
import com.cookingshow.network.exception.CommonException;
import com.cookingshow.network.model.BaseStringRequest;
import com.cookingshow.view.BigButton;

public class AppDetailInfo extends Fragment implements ListItemClickHelp {

    protected static final String TAG = "AppDetailInfo";
    private static final String LOCATION = "http://182.92.198.90";
    private static final String QR_URL_PRE = "http://api.cli.im/generate/?key=S91m01&logo=http://182.92.198.90/test/dish/asset/icon_weixin.png&level=M&size=400&data=";
    private static final String QR_URL_DATA = LOCATION + "/test/mobile/discuss.html";
    private SurfaceView   mSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;
    private ImageView mVodplayerPlay = null;
    private View mVodplayer = null;
    private View mView = null;
    private View mView2 = null;
    private View mView3 = null;
    private TextView mVodplayerCurrTime = null;
    private TextView mVodplayerTotalTime = null;
    private SeekBar mSeekBar = null;
    private BigButton mLikeBtn = null;
    private BigButton makeBtn = null;
    private BigButton discussBtn = null;
    private BigButton mShareBtn = null;
    private BigButton mMyShareBtn = null;
    private ProgressBar mProgressBar = null;
    private ProgressBar mProgressBar2 = null;    
    private ListView mListView = null;
	private List<ListItem> mItemsData = new ArrayList<ListItem>();
	private AppDetailItemAdapter adapter = null;
    private NavigationBar mSubNavBar = null;
    private List<MenuItem> mItemList = null;
    private MediaPlayer mMediaPlayer = null;
    private static int postion = -1;
    private MediaPlayer.OnInfoListener mInfoListener = null;
    private MediaPlayer.OnCompletionListener mCompletionListener = null;
    private MediaPlayer.OnPreparedListener mPreparedListener = null;
    private MediaPlayer.OnErrorListener mErrorListener = null;
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = null;
    private MediaPlayer.OnSeekCompleteListener mSeekCompleteListener =  null;
    private int appId = 0;
    private int appTotalDuration = 0;
    private String appName = null;
    private String appUrl = null;
    private String uploader = null;
    private String tips = null;
    private String material = null;
    private static final int MSG_LOAD_UI_MAKE = 1;
    private static final int MSG_LOAD_UI_DISCUSS = 2;
    private static final int MSG_REFRESH_SEEK_BAR = 3;
    private static final int MSG_HIDE_SEEK_BAR = 4;
    private int currListView = 0; // 0: make; 1: discuss
    private int discussBtnPressed = 0;
    private static CookStepProvider mCookStepProvider;
    private static DishDiscussProvider mDishDiscussProvider;
    private CommonStringLoader mStringLoader = null;
    private CommonImageLoader imageLoader = null;
    private boolean isFullScreen = false;
    private Dialog mDialog = null;
    private ACache mCache = null;
    private boolean flag = false;
    private boolean isPlayError = false;
    private HandlerThread handlerThread = null;
    private Handler handler = null;
    private int pos = 0;

    private final Handler mRefreshHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_UI_MAKE:
                	if(currListView == 0 && isAdded()) {
                		refreshMakeData();
                	}
                    break;
                case MSG_LOAD_UI_DISCUSS:
                	if(currListView == 1 && isAdded()) {
                		refreshDiscussData();
                	}
                    break;
                case MSG_REFRESH_SEEK_BAR:
                	if(isFullScreen) {
                        mSeekBar.setProgress(msg.getData().getInt("position"));
                        mVodplayerCurrTime.setText(getTime(msg.getData().getInt("position")));                		
                	}               		
                	break;
                case MSG_HIDE_SEEK_BAR:
                	mVodplayer.setVisibility(View.GONE);
                	break;
                case 5:
                	if(mMediaPlayer.isPlaying())
                	    mMediaPlayer.pause();
                	break;
            }
            return false;
        }
    });

    public AppDetailInfo () {

    }

    AppDetailInfo (int appId, String appName, String appUrl, String uploader, String tips, String material) {
    	this.appId = appId;
    	this.appName = appName;
    	this.appUrl = appUrl;
    	this.uploader = uploader;
    	this.tips = tips;
    	this.material = material;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	Log.d(TAG, "onCreate");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.app_detail_layout, container, false);
        
        initUI(view);

        return view;
    } 

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "onActivityCreated");
		
        mCookStepProvider = new CookStepProvider(AppDetailInfo.this.getActivity().getApplicationContext(), mRefreshHandler);
        mDishDiscussProvider = new DishDiscussProvider(AppDetailInfo.this.getActivity().getApplicationContext(), mRefreshHandler);
        
        mStringLoader = CommonStringLoader.getInstance();
        imageLoader = new CommonImageLoader();
        imageLoader.setDefaultAndErrorImgId(0, 0);

        mCache = ACache.get(AppDetailInfo.this.getActivity());
        
        loadSubMenuData();
        loadListViewData();
	}

    private void initUI(View view) {
    	mView = (View)view.findViewById(R.id.app_control_1);
    	mView2 = (View)view.findViewById(R.id.app_control_2);
    	mView3 = (View)view.findViewById(R.id.app_control_3);
    	mSurfaceView = (SurfaceView) view.findViewById(R.id.video_display);
        mSubNavBar = (NavigationBar) view.findViewById(R.id.sub_navigation);
        mLikeBtn = (BigButton) view.findViewById(R.id.btn_like);
        makeBtn = (BigButton) view.findViewById(R.id.btn_make);
        discussBtn = (BigButton) view.findViewById(R.id.btn_comment);
        mShareBtn = (BigButton) view.findViewById(R.id.btn_share);
        mMyShareBtn = (BigButton) view.findViewById(R.id.btn_my_share);
        mListView = (ListView) view.findViewById(R.id.app_listView);
        mProgressBar = (ProgressBar)view.findViewById(R.id.progress);
        mProgressBar2 = (ProgressBar)view.findViewById(R.id.progress2);
        mVodplayer = (View)view.findViewById(R.id.vodplayer);
        mVodplayerPlay = (ImageView) view.findViewById(R.id.vodplayer_play);
        mVodplayerCurrTime = (TextView) view.findViewById(R.id.vodplayer_time_label_curr);
        mVodplayerTotalTime = (TextView) view.findViewById(R.id.vodplayer_time_label_total);
        mSeekBar = (SeekBar) view.findViewById(R.id.vodplayer_seek_bar);
        
        mVodplayerPlay.setOnClickListener(mPlayerClickListener);
        mLikeBtn.setOnClickListener(mLikeClickListener);
        makeBtn.setOnClickListener(mMakeClickListener);
        discussBtn.setOnClickListener(mDiscussClickListener);
        mShareBtn.setOnClickListener(mShareClickListener);
        mMyShareBtn.setOnClickListener(mMyShareClickListener);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        mSurfaceView.setOnClickListener(mVideoClickListener);
        //mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.setSelected(true);

        mSurfaceHolder = mSurfaceView.getHolder(); 
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(new SurfaceCallback());
    }

    private void loadListViewData() {
    	Log.d(TAG, "loadListViewData");

    	ListItem data = new ListItem();
    	data.setItemTitle1(getResources().getString(R.string.dish_material));
    	data.setItemText(material);
    	mItemsData.add(data);
    	
    	mItemsData.addAll(mCookStepProvider.getCookStepData(appId));

    	ListItem data2 = new ListItem();
    	data2.setItemTitle1(getResources().getString(R.string.video_tips));
    	data2.setItemText(tips);
    	mItemsData.add(data2);

    	if(mItemsData.size() == 2) {
        	Intent intent = new Intent();
        	intent.setAction("com.cookingshow.service.ui.request.cookstep");
        	intent.putExtra("appId", appId);
        	this.getActivity().sendBroadcast(intent);    		
    	}
    	
    	adapter = new AppDetailItemAdapter(this.getActivity().getApplicationContext(), mItemsData, this);
    	mListView.setAdapter(adapter);
    	mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				if(currListView == 0) {
					showToast(mItemsData.get(position).getItemText());					
				}
				else if(currListView == 1) {
					String qr_url = null;
					String sender = mCache.getAsString("nickname");
					String receiver = mItemsData.get(position).getItemTitle1();
					int origId = mItemsData.get(position).getItemId();
					
					try {
						qr_url = QR_URL_PRE + QR_URL_DATA + "?appId=" + appId + "%26sender=" + URLEncoder.encode(sender, "UTF-8") + "%26receiver=" + URLEncoder.encode(receiver, "UTF-8") + "%26origId=" + origId;
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					showScanQrDialog(qr_url, position);
				}
			}
		});
    }

    private void refreshMakeData() {
    	Log.d(TAG, "refreshMakeData");
    	
    	if(mItemsData.size() > 0)
    	{
    		mItemsData.clear();
    	}
    	
    	ListItem data = new ListItem();
    	data.setItemTitle1(getResources().getString(R.string.dish_material));
    	data.setItemText(material);
    	mItemsData.add(data);
    	
		mItemsData.addAll(mCookStepProvider.getCookStepData(appId));
		
    	ListItem data2 = new ListItem();
    	data2.setItemTitle1(getResources().getString(R.string.video_tips));
    	data2.setItemText(tips);
    	mItemsData.add(data2);
    	
		adapter.notifyDataSetChanged();
    }
    
    private void refreshDiscussData() {
    	Log.d(TAG, "refreshDiscussData");

    	if(mItemsData.size() > 0)
    	{
    		mItemsData.clear();
    	}
    	
    	ListItem data = new ListItem();
    	data.setItemText(getResources().getString(R.string.discuss_tips));
    	mItemsData.add(data);
    	mItemsData.addAll(mDishDiscussProvider.getDishDiscussData(appId));
    	adapter.notifyDataSetChanged();
    }
    
    private void loadSubMenuData() {
    	Log.d(TAG, "loadSubMenuData");
        new LoadContentTask().execute();
    }

    private void initSubNavigation() {
        if (mItemList != null && !mItemList.isEmpty()) {

            List<NavigationData> datas = new ArrayList<NavigationData>();
            for (int i = 0; i < mItemList.size(); i++) {
                NavigationData naviData = new NavigationData();
                naviData.title = mItemList.get(i).getName();
                naviData.id = mItemList.get(i).getId();
                naviData.tag = mItemList.get(i);
                datas.add(naviData);
            }
            mSubNavBar.setItemOnClickListener(mSubNaviItemClickListener);
            mSubNavBar.inflateItems(datas, R.layout.sub_navigation_item, 35);
            //mSubNavBar.setClickedItem(mTypeList.get(0).getOrderNum());
            mSubNavBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        
        if(handler != null) {
        	handler.removeCallbacks(mRunnable);
        	handlerThread.quit();
        	handlerThread = null;
        }

    	if (null != mMediaPlayer) {
    		if(mMediaPlayer.isPlaying()) {
    			mMediaPlayer.stop();
    		}

    		mMediaPlayer.release();  
    		mMediaPlayer = null;
        }

    	flag = false;
		postion = -1;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        
        if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
        	postion = mMediaPlayer.getCurrentPosition();
        	mMediaPlayer.pause();
        }
    }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy");
        if (mCookStepProvider != null)
        {
        	mCookStepProvider.destroy();
        }
        
        if (mDishDiscussProvider != null)
        {
        	mDishDiscussProvider.destroy();
        }
	}

    private View.OnClickListener mLikeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	String params = "type=setVideoLikeTimes&user=" + mCache.getAsString("deviceId") + "&dish_id=" + appId; 
        	mStringLoader.addRequest(new BaseStringRequest(Method.GET, "http://182.92.198.90/test/transaction/stringRequest.php" + "?" + params, new BaseStringRequest.StringResponseListener() {
    			
    			@Override
    			public void onResponse(String str) {
    				// TODO Auto-generated method stub
    				if(Integer.parseInt(str) == -1 || Integer.parseInt(str) == -2) {
    					Toast.makeText(AppDetailInfo.this.getActivity().getApplicationContext(), getResources().getString(R.string.like_error), Toast.LENGTH_LONG).show();
    				}
    				else if(Integer.parseInt(str) == 0) {
    					Toast.makeText(AppDetailInfo.this.getActivity().getApplicationContext(), getResources().getString(R.string.like_duplicate), Toast.LENGTH_LONG).show();
    				}
    				else {
    					mLikeBtn.setButtonText(str);
    				}
    			}
    			
    			@Override
    			public void onErrorResponse(CommonException exception) {
    				// TODO Auto-generated method stub
    				
    			}
    		}));
        }
    };

    private View.OnClickListener mMakeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	currListView = 0;
        	refreshMakeData();
        }
    };
 
    private View.OnClickListener mDiscussClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	currListView = 1;
        	
        	if(mItemsData.size() > 0)
        	{
        		mItemsData.clear();
        	}
        	
        	if(discussBtnPressed == 0) {
        		discussBtnPressed = 1;
                Intent intent = new Intent();
                intent.setAction("com.cookingshow.service.ui.request.dishdiscuss");
                intent.putExtra("appId", appId);
                getActivity().sendBroadcast(intent);    		
        	}
        	else {
            	ListItem data = new ListItem();
            	data.setItemText(getResources().getString(R.string.discuss_tips));
            	mItemsData.add(data);
        		mItemsData.addAll(mDishDiscussProvider.getDishDiscussData(appId));
        		adapter.notifyDataSetChanged();
        	}	
        }
    };

    private View.OnClickListener mShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("appId", appId);
            intent.putExtra("appName", appName);
            
            intent.setClass(AppDetailInfo.this.getActivity(), AppShareInfoActivity.class);
            startActivity(intent);        	
        }
    };

    private View.OnClickListener mMyShareClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
            /*Intent intent = new Intent();
            intent.setClass(AppDetailInfo.this.getActivity(), AppShareImgActivity.class);
            startActivity(intent);*/
			showToast(String.valueOf(getResources().getString(R.string.penging_notice)));
		}
	};

    private View.OnClickListener mSubNaviItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	MenuItem item = (MenuItem) v.getTag();
        	
        	if(item.getCode().equals("uploader")) {
        		//showToast(String.valueOf(appId));
        	}
        	else if(item.getCode().equals("follow")) {
        		
        	}            
        }
    };

    private View.OnClickListener mPlayerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
    		if(mMediaPlayer.isPlaying()) {
			    mMediaPlayer.pause();
			    mVodplayerPlay.setImageResource(R.drawable.vodplayer_controller_play_pressed);
			    //mVodplayerCurrTime.setText(getTime(mMediaPlayer.getCurrentPosition()));
		    }
		    else {
			    mMediaPlayer.start();
			    mVodplayerPlay.setImageResource(R.drawable.vodplayer_controller_pause_pressed);
		    }
        }
    };

    private View.OnClickListener mVideoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //showToast("video");
        	if(!isFullScreen) {
        		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);      
        		mSurfaceView.setLayoutParams(lp);
        		isFullScreen = true;

        		mView.setVisibility(View.GONE);
        		mView2.setVisibility(View.GONE);
        		mView3.setVisibility(View.GONE);
        		
			    if(mProgressBar.getVisibility() == View.VISIBLE) {
			    	mProgressBar.setVisibility(View.GONE);
			    	mProgressBar2.setVisibility(View.VISIBLE);
			    }				
        	}
        	else {
            	if(null != mMediaPlayer) {
                    if(mVodplayer.getVisibility() == View.GONE) {
                    	if(mMediaPlayer.isPlaying()) {
                    		mVodplayerPlay.setImageResource(R.drawable.vodplayer_controller_pause_pressed);
                    	}
                    	else {
                    		mVodplayerPlay.setImageResource(R.drawable.vodplayer_controller_play_pressed);
                    	}
                    	mVodplayer.setVisibility(View.VISIBLE);
                    	mVodplayerPlay.requestFocus();
                    	mVodplayerPlay.requestFocusFromTouch();
                    	hideSeekbar();
                    }
            	}        		
        	}

        }
    };
    
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			//Toast.makeText(AlbumDetailInfo.this.getActivity().getApplicationContext(), "2222", Toast.LENGTH_LONG).show();
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			//Toast.makeText(AlbumDetailInfo.this.getActivity().getApplicationContext(), "1111", Toast.LENGTH_LONG).show();
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if(fromUser && mMediaPlayer != null) {				
				mMediaPlayer.seekTo(progress);
				//mSeekBar.setProgress(progress);
				mVodplayerCurrTime.setText(getTime(mMediaPlayer.getCurrentPosition()));
				//Toast.makeText(AlbumDetailInfo.this.getActivity().getApplicationContext(), "ccceee", Toast.LENGTH_LONG).show();
			}
			else {
				
			}
		}
	};
	
    protected class LoadContentTask extends AsyncTask<String, Void, Boolean> {

        public LoadContentTask() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                if (mItemList == null) {
                	mItemList = new ArrayList<MenuItem>();
                    /*mTypeResponse = new CategoryDataProvidor5().getAllAppAndGameType7(getActivity());
                    if (mTypeResponse.getIsSuccess()) {
                        mTypeList = mTypeResponse.getGameTypeList();
                    }*/
                	MenuItem data = new MenuItem();
                	data.setName(getResources().getString(R.string.uploader) + uploader);
                	data.setCode("uploader");
                	//data.setId("1");
                	data.setOrderNum(1);
                	mItemList.add(data);
                	
                	MenuItem data2 = new MenuItem();
                	data2.setName(getResources().getString(R.string.follow_ta));
                	data2.setCode("follow");
                	//data2.setId("2");
                	data2.setOrderNum(2);
                	mItemList.add(data2);
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            initSubNavigation();
        }
    }
    
    private void showToast(String str) {
    	Toast.makeText(this.getActivity().getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }
 
    public void playVideo() {
    	if(!isFullScreen)
    		mProgressBar.setVisibility(View.VISIBLE);
    	else
    		mProgressBar2.setVisibility(View.VISIBLE);

    	if(mMediaPlayer == null){
    	    mMediaPlayer = new MediaPlayer();

        	String params = "type=setViewTimes&user=" + mCache.getAsString("deviceId") + "&album_id=0" + "&dish_id=" + appId;
        	mStringLoader.addRequest(new BaseStringRequest(Method.GET, "http://182.92.198.90/test/transaction/stringRequest.php" + "?" + params, new BaseStringRequest.StringResponseListener() {
    			
    			@Override
    			public void onResponse(String str) {
    				// TODO Auto-generated method stub
    				if(Integer.parseInt(str) > 0) {
    				    mLikeBtn.setButtonText(str);
    				}
    				else {
    					mLikeBtn.setButtonText("");
    				}
    				//Toast.makeText(AppDetailInfo.this.getActivity().getApplicationContext(), str, Toast.LENGTH_LONG).show();
    			}
    			
    			@Override
    			public void onErrorResponse(CommonException exception) {
    				// TODO Auto-generated method stub
    			}
    		}));
    	}
    	else {
    		//showToast("play error, will try again!");
    		if(mMediaPlayer.isPlaying()) {
    			mMediaPlayer.stop();
    		}
    		mMediaPlayer.reset();
    	}

	    isPlayError = false;

    	Uri uri = null;
    	if(appUrl.indexOf("http") >= 0) {
    	    uri = Uri.parse(appUrl);
    	}
    	else {
        	uri = Uri.parse(LOCATION + appUrl);
    	}

    	setListener();

    	try {
    		mMediaPlayer.setDataSource(getActivity(), uri);
        	mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        	mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
        	mMediaPlayer.setOnInfoListener(mInfoListener);
        	mMediaPlayer.setOnCompletionListener(mCompletionListener);
        	mMediaPlayer.setOnPreparedListener(mPreparedListener);
        	mMediaPlayer.setOnErrorListener(mErrorListener);
        	mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
    		mMediaPlayer.prepareAsync();
    	} catch (IllegalArgumentException e) {  
            e.printStackTrace();
        } catch (SecurityException e) {
        	e.printStackTrace();
        } catch (IllegalStateException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }

    private void setListener() {
    	mSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
			
			@Override
			public void onSeekComplete(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mp.start();
				handler.post(mRunnable);
				
			    if(mProgressBar2.getVisibility() == View.VISIBLE) {
			    	mProgressBar2.setVisibility(View.GONE);
			    }
			    
    			if(mVodplayer.getVisibility() == View.VISIBLE) {
    				hideSeekbar();
    			}
			}
		};

    	mInfoListener = new MediaPlayer.OnInfoListener() {
			
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				switch (what) {
				case MediaPlayer.MEDIA_INFO_BUFFERING_START:				
					if(!isFullScreen)
					    mProgressBar.setVisibility(View.VISIBLE);
					else
						mProgressBar2.setVisibility(View.VISIBLE);

					break;
				case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    if(mMediaPlayer.isPlaying()) {
						if(!isFullScreen)
						    mProgressBar.setVisibility(View.GONE);
						else
							mProgressBar2.setVisibility(View.GONE);                 	
                    }

					break;
				}
				return false;
			}
		};

        mCompletionListener = new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				if(!isPlayError) {
					showToast("play finish!");
				}
				else {					
					playVideo();
				}
			}
		};
        
        mPreparedListener = new MediaPlayer.OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				Log.e(TAG, "onPrepared-->");
				/*if(postion >= 0) {
					mMediaPlayer.seekTo(postion);
					postion = -1;
				}
				*/
				if(!isFullScreen)
				    mProgressBar.setVisibility(View.GONE);
				else
					mProgressBar2.setVisibility(View.GONE);

				appTotalDuration = mMediaPlayer.getDuration();
    			mVodplayerTotalTime.setText(getTime(appTotalDuration));
    			mSeekBar.setMax(appTotalDuration);

				mMediaPlayer.start(); 
				mMediaPlayer.setDisplay(mSurfaceHolder);
				mMediaPlayer.setScreenOnWhilePlaying(true);

    			if(handlerThread == null) {
    				refreshSeekBar();
    			}
    			else {
    				handler.post(mRunnable);
    			}

    			if(mVodplayer.getVisibility() == View.VISIBLE) {
    				hideSeekbar();
    			}
			}
		};
        
        mErrorListener = new MediaPlayer.OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				isPlayError = true;

			    switch (what){  
		            case MediaPlayer.MEDIA_ERROR_UNKNOWN:  
		                Log.e(TAG,"发生未知错误");  
		  
		                break;  
		            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:  
		                Log.e(TAG,"媒体服务器死机");  
		                break;  
		            default:  
		                Log.e(TAG,"what onError+"+what);  
		                break;  
		        }  
		        switch (extra){  
		            case MediaPlayer.MEDIA_ERROR_IO:  
		                //io读写错误  
		                Log.e("TAG","文件或网络相关的IO操作错误");  
		                break;  
		            case MediaPlayer.MEDIA_ERROR_MALFORMED:  
		                //文件格式不支持  
		                Log.e("TAG","比特流编码标准或文件不符合相关规范");  
		                break;  
		            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:  
		                //一些操作需要太长时间来完成,通常超过3 - 5秒。  
		                Log.e("TAG","操作超时");  
		                break;  
		            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:  
		            //比特流编码标准或文件符合相关规范,但媒体框架不支持该功能  
		                Log.e("TAG","比特流编码标准或文件符合相关规范,但媒体框架不支持该功能");  
		                break;  
		            default:  
		                Log.e("TAG","extra onError+"+extra);  
		                break;  
		        } 
				return false;
			}
		};
		
		mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
			
			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onBufferingUpdate-->" + percent); 
			}
		};
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
    	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {  
            // SurfaceView的大小改变  
    		if(mMediaPlayer != null) {
    		    mMediaPlayer.setDisplay(holder);
    		}
        }

        @Override  
        public void surfaceCreated(SurfaceHolder holder) {   
            try {
        	    if(mMediaPlayer != null) {    					
        		    mMediaPlayer.setDisplay(mSurfaceHolder);
        		    mMediaPlayer.start();
        	    }
        	    else {
        	    	playVideo();
        	    }

            } catch (IllegalArgumentException e) {  
                    // TODO Auto-generated catch block  
                e.printStackTrace();  
            } catch (SecurityException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } catch (IllegalStateException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }
        }
        
        @Override  
        public void surfaceDestroyed(SurfaceHolder holder) { 
        	Log.d(TAG, "surfaceDestroyed");    
        } 
    }

	@Override
	public void onClick(View item, int position) {
		// TODO Auto-generated method stub
		if(currListView == 0){
			//showToast("item click: " + mItemsData.get(position).getItemText());		
		}
		else if(currListView == 1) {
			String qr_url = null;
			String sender = mCache.getAsString("nickname");
			String receiver = mItemsData.get(position).getItemTitle1();
			int origId = mItemsData.get(position).getItemId();

			try {
				qr_url = QR_URL_PRE + QR_URL_DATA + "?appId=" + appId + "%26sender=" + URLEncoder.encode(sender, "UTF-8") + "%26receiver=" + URLEncoder.encode(receiver, "UTF-8") + "%26origId=" + origId;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			showScanQrDialog(qr_url, position);
		}		
	}
	
	public boolean onBackPressedFragment() {
		if(isFullScreen) {
			int w = (int)getResources().getDimension(R.dimen.app_detail_video_layout_margin_width);
			int h = (int)getResources().getDimension(R.dimen.app_detail_video_layout_margin_height);
			int left = (int)getResources().getDimension(R.dimen.app_detail_layout_margin_left);
			int right = (int)getResources().getDimension(R.dimen.app_detail_video_layout_margin_right);
			int top = (int)getResources().getDimension(R.dimen.app_detail_video_layout_margin_top);
			
    		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
    		lp.setMargins(left, top, right, 0);
    		mSurfaceView.setLayoutParams(lp);

    		mVodplayer.setVisibility(View.GONE);
    		mView.setVisibility(View.VISIBLE);
    		mView2.setVisibility(View.VISIBLE);
    		mView3.setVisibility(View.VISIBLE);
    		
		    if(mProgressBar2.getVisibility() == View.VISIBLE) {
		    	mProgressBar2.setVisibility(View.GONE);
		    	mProgressBar.setVisibility(View.VISIBLE);
		    }
    		
			isFullScreen = false;
			return true;
		}
		else {
			//showToast("2222222222");
		}
		
		return false;
	}

	public boolean onRightDownFragment() {
		if(isFullScreen) {
            if(mVodplayer.getVisibility() == View.GONE) {
            	mVodplayer.setVisibility(View.VISIBLE);
            }

            if(handler != null) {
                handler.removeCallbacks(mRunnable);

                pos = mSeekBar.getProgress();
                pos += 10000;
            
			    Message msg = new Message();
			    msg.what = MSG_REFRESH_SEEK_BAR;
			    Bundle bundle = new Bundle();
			    bundle.putInt("position", pos);
			    msg.setData(bundle);			 
			    mRefreshHandler.sendMessage(msg);

			    Message msg2 = new Message();
			    msg2.what = 5;            
			    mRefreshHandler.sendMessage(msg2);            	
            }

			return true;
		}
		
		return false;
	}

	public boolean onLeftDownFragment() {
		if(isFullScreen) {
            if(mVodplayer.getVisibility() == View.GONE) {
            	mVodplayer.setVisibility(View.VISIBLE);
            }

            if(handler != null) {
                handler.removeCallbacks(mRunnable);

                pos = mSeekBar.getProgress();
                pos -= 10000;
                if(pos < 0) {
            	    pos = 0;
                }

			    Message msg = new Message();
			    msg.what = MSG_REFRESH_SEEK_BAR;
			    Bundle bundle = new Bundle();
			    bundle.putInt("position", pos);
			    msg.setData(bundle);			 
			    mRefreshHandler.sendMessage(msg);

			    Message msg2 = new Message();
			    msg2.what = 5;            
			    mRefreshHandler.sendMessage(msg2);            	
            }

			return true;            
		}
		
		return false;
	}

	public boolean onKeyUpFragment() {
		if(isFullScreen) {
            if(mVodplayer.getVisibility() == View.GONE) {
            	mVodplayer.setVisibility(View.VISIBLE);
            }

		    if(handler != null) {
			    if(mProgressBar2.getVisibility() == View.GONE) {
			    	mProgressBar2.setVisibility(View.VISIBLE);
			    }
		    	mMediaPlayer.seekTo(pos);
		    }
			
			return true;
		}
		
		return false;
	}
	
    public Dialog setScanQrDialog() {
    	Dialog dialog = null;
    	
        View view = LayoutInflater.from(AppDetailInfo.this.getActivity()).inflate(R.layout.qr_tip_dialog, null);
        dialog = new Dialog(AppDetailInfo.this.getActivity(), R.style.tv_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);

        return dialog;
    }
    
    public void showScanQrDialog(String url, int position) {
		if(mDialog == null) {
			mDialog = setScanQrDialog();
		}
		
		if (!mDialog.isShowing()) {
			final ImageView qrImg = (ImageView)mDialog.findViewById(R.id.qrimg);
			final TextView qrTxt = (TextView)mDialog.findViewById(R.id.reply);
			mDialog.show();
		
			final int ps = position;
	        imageLoader.loadImage(url, qrImg, new ImageLoader.ImageListener() {
	            @Override
	            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
	                if (response.getBitmap() != null) {
	                    qrImg.setImageBitmap(response.getBitmap());

	                    if(ps == 0)
	                        qrTxt.setText(getResources().getString(R.string.weixin_review));
	                    else 
	                    	qrTxt.setText(getResources().getString(R.string.weixin_reply) + " " + mItemsData.get(ps).getItemTitle1());
	                }
	            }

	            @Override
	            public void onErrorResponse(VolleyError error) {
	            	qrTxt.setText(getResources().getString(R.string.weixin_error_text));
	            }
	        });
		}    	
    }
    
    private String getTime(int millisUntilFinished) {
    	long millis = (long)millisUntilFinished;
        String hms = String.format("%02d:%02d:%02d", 
        		TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    	
    	return hms;
    }

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
				int currentPosition = mMediaPlayer.getCurrentPosition();
				Message msg = new Message();
				msg.what = MSG_REFRESH_SEEK_BAR;
				Bundle bundle = new Bundle();
				bundle.putInt("position", currentPosition);
				msg.setData(bundle);
				
				mRefreshHandler.sendMessage(msg);				
			}
			
			handler.postDelayed(mRunnable, 1000);
		}
		
	};
	
    private void refreshSeekBar() {   	
        handlerThread = new HandlerThread("RefreshSeekBar");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.post(mRunnable);
    }

    private void hideSeekbar() {
    	flag = true;
        final long show = System.currentTimeMillis();
        
        new Thread(new Runnable() {
            @Override
            public void run() { 
                while(flag) {
                	if(System.currentTimeMillis() - show > 4000) {
                		mRefreshHandler.sendEmptyMessage(MSG_HIDE_SEEK_BAR);
                		flag = false;
                    }
                }
            }
        }).start();
    }
}

/**
 * 
 */
package com.cookingshow.push;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.cookingshow.R;
import com.cookingshow.adapter.AppShareItemAdapter;
import com.cookingshow.adapter.ListItemClickHelp;
import com.cookingshow.datacenter.ShareImgProvider;
import com.cookingshow.datacenter.ShareListItem;
import com.cookingshow.network.cache.ACache;
import com.cookingshow.network.controller.CommonImageLoader;
import com.cookingshow.network.controller.CommonStringLoader;
import com.cookingshow.network.exception.CommonException;
import com.cookingshow.network.model.BaseStringRequest;
import com.cookingshow.view.BigButton;

public class AppShareInfoActivity extends Activity implements ListItemClickHelp {

    protected static final String TAG = "AppShareInfoActivity";
    private static final String LOCATION = "http://182.92.198.90";
    private static final int MSG_LOAD_UI = 1;
	private List<ShareListItem> mShareItemsData = new ArrayList<ShareListItem>();
    private ImageView mImageView = null;
    private BigButton mLikeBtn = null;
    private TextView mShareTitle = null;
    private TextView mUploader = null;
    private TextView mUploadTime = null;
    private TextView mfeeling = null;
    private ListView mListView = null;
    private CommonImageLoader mImageLoader = null;
	private AppShareItemAdapter adapter = null;
    private ShareImgProvider mShareImgProvider;
    private int appId = 0;
    private int index = 0;
    private String appName = "";
    private CommonStringLoader mStringLoader = null;
    
    private final Handler mRefreshHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_UI:
                	refreshData();
                    break;
            }
            return false;
        }
    });

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.app_share_pic_layout);
		
        mStringLoader = CommonStringLoader.getInstance();
		mImageLoader = new CommonImageLoader();
		mShareImgProvider = new ShareImgProvider(AppShareInfoActivity.this.getApplicationContext(), mRefreshHandler);
		
        Intent intent = getIntent();
        appId = intent.getIntExtra("appId", 1);
        appName = intent.getStringExtra("appName");
        
		initUI();
        loadListViewData();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
    private void initUI() {
        mShareTitle = (TextView) findViewById(R.id.share_title);
        mListView = (ListView) findViewById(R.id.app_share_listView);       
        mImageView = (ImageView) findViewById(R.id.share_img);
        
        mUploader = (TextView) findViewById(R.id.uploader);
        mUploadTime = (TextView) findViewById(R.id.upload_time);
        mfeeling = (TextView) findViewById(R.id.uploader_feel);
        mLikeBtn = (BigButton) findViewById(R.id.btn_share_like);

        mLikeBtn.setOnClickListener(mShareLikeClickListener);
        
        mShareTitle.setText(appName);
    }

    private void loadListViewData() {
    	Log.d(TAG, "loadListViewData");

    	//Toast.makeText(AppShareInfoActivity.this.getApplicationContext(), "dish_id=" + appId, Toast.LENGTH_LONG).show();
    	Intent intent = new Intent();
    	intent.setAction("com.cookingshow.service.ui.request.shareimg");
    	intent.putExtra("appId", appId);
    	this.sendBroadcast(intent); 
    	
    	adapter = new AppShareItemAdapter(this.getApplicationContext(), mShareItemsData, this);
    	mListView.setAdapter(adapter);
    	
    	mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mUploader.setText(getResources().getString(R.string.uploader) + mShareItemsData.get(position).getUploader());
				mUploadTime.setText(getResources().getString(R.string.upload_time) + mShareItemsData.get(position).getUploaderTime());
				mfeeling.setText(mShareItemsData.get(position).getFeeling());
				
				if(mShareItemsData.get(position).getLikeTimes() > 0) {
					mLikeBtn.setButtonText(String.valueOf(mShareItemsData.get(position).getLikeTimes()));
				}
				
		        if (!TextUtils.isEmpty(mShareItemsData.get(position).getUrl())) {
		            mImageLoader.loadImageWithManager(LOCATION + mShareItemsData.get(position).getUrl(), mImageView);
		        }
		        
		        index = position;
			}
		});
    }

    private void refreshData() {
    	Log.i(TAG, "refreshData");
    	
    	if(mShareItemsData.size() > 0) {
    		mShareItemsData.clear();
    	}
    	
      	mShareItemsData.addAll(mShareImgProvider.getShareImgData());
		adapter.notifyDataSetChanged();
		
	    mListView.setSelection(0);
	    mListView.setItemChecked(0, true);
	
	    new Handler().post(new Runnable() {

		    @Override
		    public void run() {
			    // TODO Auto-generated method stub
			    View itemView = (View) mListView.getAdapter().getView(0, null, null);
			    mListView.performItemClick(itemView, 0, mListView.getAdapter().getItemId(0));
		    }
		
	    });
    }

    private View.OnClickListener mShareLikeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	//Toast.makeText(AppShareInfoActivity.this.getApplicationContext(), "test", Toast.LENGTH_LONG).show();
        	if(mShareItemsData.size() > 0) {
            	String params = "type=setImgLikeTimes&user=" + ACache.get(AppShareInfoActivity.this).getAsString("deviceId") + "&img_id=" + mShareItemsData.get(index).getShareId(); 
            	mStringLoader.addRequest(new BaseStringRequest(Method.GET, "http://182.92.198.90/test/transaction/stringRequest.php" + "?" + params, new BaseStringRequest.StringResponseListener() {
        			
        			@Override
        			public void onResponse(String str) {
        				// TODO Auto-generated method stub
        				if(Integer.parseInt(str) == -1 || Integer.parseInt(str) == -2) {
        					Toast.makeText(AppShareInfoActivity.this.getApplicationContext(), getResources().getString(R.string.like_error), Toast.LENGTH_LONG).show();
        				}
        				else if(Integer.parseInt(str) == 0) {
        					Toast.makeText(AppShareInfoActivity.this.getApplicationContext(), getResources().getString(R.string.like_duplicate), Toast.LENGTH_LONG).show();
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
        }
    };

	@Override
	public void onClick(View item, int position) {
		// TODO Auto-generated method stub
		//ShareListItem mItem = mShareItemsData.get(position);
		mUploader.setText(getResources().getString(R.string.uploader) + mShareItemsData.get(position).getUploader());
		mUploadTime.setText(getResources().getString(R.string.upload_time) + mShareItemsData.get(position).getUploaderTime());
		mfeeling.setText(mShareItemsData.get(position).getFeeling());
		
		if(mShareItemsData.get(position).getLikeTimes() > 0) {
			mLikeBtn.setButtonText(String.valueOf(mShareItemsData.get(position).getLikeTimes()));
		}
		
        if (!TextUtils.isEmpty(mShareItemsData.get(position).getUrl())) {
            mImageLoader.loadImageWithManager(LOCATION + mShareItemsData.get(position).getUrl(), mImageView);
        }

        index = position;
	}

}

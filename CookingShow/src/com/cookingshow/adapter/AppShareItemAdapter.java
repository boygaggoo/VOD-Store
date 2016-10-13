package com.cookingshow.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cookingshow.R;
import com.cookingshow.datacenter.ShareListItem;
import com.cookingshow.network.controller.CommonImageLoader;


public class AppShareItemAdapter extends BaseAdapter {

    private static final String LOCATION = "http://182.92.198.90";
	private Context mContext = null;
	private List<ShareListItem> mShareItemsData = null;
	private ListItemClickHelp mCallback = null;
    private CommonImageLoader mImageLoader = null;

	public AppShareItemAdapter(Context context, List<ShareListItem> listItems, ListItemClickHelp callback) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mShareItemsData = listItems;
		mCallback = callback;
		mImageLoader = new CommonImageLoader();
	}

    public void setItemsData(List<ShareListItem> listItems)
    {
    	mShareItemsData = listItems;
    }
 
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int count = 0;
		
		if (null != mShareItemsData) {
			count = mShareItemsData.size();
		}
		
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		ShareListItem item = null;

		if (null != mShareItemsData) {
			item = mShareItemsData.get(position);
		}
		
		return item;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(null == convertView) {
			viewHolder = new ViewHolder();
			LayoutInflater mInflater = LayoutInflater.from(mContext);
			convertView = mInflater.inflate(R.layout.app_share_pic_list_item, parent, false);
			viewHolder.item = (View)convertView.findViewById(R.id.share_list_item);
			viewHolder.itemImg = (ImageView)convertView.findViewById(R.id.item_img);
			viewHolder.itemUploader = (TextView)convertView.findViewById(R.id.item_index_uploader);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		ShareListItem itemData = (ShareListItem)getItem(position);
		if(null != itemData) {
			viewHolder.itemUploader.setText(itemData.getUploader());
			//viewHolder.itemLike.setText(String.valueOf(itemData.getLikeTimes()));
            if (!TextUtils.isEmpty(itemData.getUrl())) {
                mImageLoader.loadImageWithManager(LOCATION + itemData.getUrl(), viewHolder.itemImg);
            }
		}
			
		final View view = convertView;
		final int pt = position;
		viewHolder.item.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCallback.onClick(view, pt);
			}
		});
		
		return convertView;
	}
    
    private static class ViewHolder
    {
    	View item;
    	ImageView itemImg;
        TextView itemUploader;
    }

}

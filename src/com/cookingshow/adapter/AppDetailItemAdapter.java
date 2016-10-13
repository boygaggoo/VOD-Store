package com.cookingshow.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cookingshow.R;
import com.cookingshow.datacenter.ListItem;

public class AppDetailItemAdapter extends BaseAdapter {

    protected static final String TAG = "AppDetailItemAdapter";
    
	private Context mContext = null;
	private List<ListItem> mItemsData = null;
	private ListItemClickHelp mCallback = null;
	
	public AppDetailItemAdapter(Context context, List<ListItem> listItems, ListItemClickHelp callback) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mItemsData = listItems;
		mCallback = callback;
	}

    public void setItemsData(List<ListItem> listItems)
    {
    	mItemsData = listItems;
    }
 
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int count = 0;
		
		if (null != mItemsData) {
			count = mItemsData.size();
		}
		
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		ListItem item = null;

		if (null != mItemsData) {
			item = mItemsData.get(position);
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
			convertView = mInflater.inflate(R.layout.app_detail_list_item, parent, false);
			viewHolder.item = (View) convertView.findViewById(R.id.list_item);
			viewHolder.itemTitle1 = (TextView)convertView.findViewById(R.id.itemTitle1);
			viewHolder.itemTitle2 = (TextView)convertView.findViewById(R.id.itemTitle2);
			viewHolder.itemText = (TextView)convertView.findViewById(R.id.itemText);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		ListItem itemData = (ListItem)getItem(position);
		if(null != itemData) {
			viewHolder.itemTitle1.setText(itemData.getItemTitle1());
			viewHolder.itemTitle2.setText(itemData.getItemTitle2());
			viewHolder.itemText.setText(itemData.getItemText());
		}
		
		final View view = convertView;
		final int pt = position;
		viewHolder.item.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "item click" + pt);
				mCallback.onClick(view, pt);
			}
		});
		
		return convertView;
	}
	
    private static class ViewHolder
    {
    	View item;
        TextView itemTitle1;
        TextView itemTitle2;
        TextView itemText;
    }

}

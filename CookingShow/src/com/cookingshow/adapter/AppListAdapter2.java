package com.cookingshow.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.cookingshow.datacenter.DishDataInfo;
import com.cookingshow.network.controller.CommonImageLoader;
import com.cookingshow.view.AppItemView2;

public class AppListAdapter2 extends BaseListAdapter<DishDataInfo> {

    private static final String TAG = "AppListAdapter2";
    private SparseArray<AppItemView2> mViewHolers = null;
    private CommonImageLoader mImageLoader = null;
    protected List<DishDataInfo> mAppResult;
    private Context mContext;

    public AppListAdapter2(Context context, List<DishDataInfo> appResult) {
        mContext = context;
        mViewHolers = new SparseArray<AppItemView2>();
        mImageLoader = new CommonImageLoader();
        mAppResult = appResult;
    }

    public View createViewFromResource(final int position, View convertView) {
    	AppItemView2 viewHolder;
        if (convertView != null) {
            viewHolder = (AppItemView2) convertView;
        } else {
            viewHolder = new AppItemView2(mContext);
        }
        viewHolder.setTag(position);
        if (mAppResult != null && position < mAppResult.size()) {
            viewHolder.refreshData(mAppResult.get(position));
        } else {
            viewHolder.refreshData(null);
        }
        mViewHolers.put(position, viewHolder);
        return viewHolder;
    }

    public void resetData(List<DishDataInfo> data) {
        this.mAppResult = data;
        mViewHolers.clear();
    }

    public void loadImageValue(ArrayList<Integer> poss) {
        for (int index : poss) {
        	AppItemView2 viewHolder = mViewHolers.get(index);
            if (viewHolder != null) {
                final ImageView imageView = viewHolder.appIcon;
                final String uri = viewHolder.iconUrl;
                
                if (!TextUtils.isEmpty(uri)) {
                    mImageLoader.loadImageWithManager(uri, imageView);
                }
            }
        }
    }

    public boolean addData(List<DishDataInfo> datas) {
        try {
            mAppResult.addAll(datas);
            return true;
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return false;
    }

    @Override
    public View getView(LayoutInflater layoutInflater, int position) {
        return null;
    }

    //    @Override
    public int getCount() {
        if (mAppResult == null) {
            return 0;
        }
        return mAppResult.size();
    }

    public View getNeedFocusView(int pos) {
        if (mViewHolers.get(pos) != null) {
            return mViewHolers.get(pos).iconLayout;
        }
        return null;
    }

    public void onClear() {
        resetViewForDefaultValue();
        mViewHolers.clear();
    }

    public void resetViewForDefaultValue() {
        int size = mViewHolers.size();
        for (int i = 0; i < size; i++) {
        	AppItemView2 holder = mViewHolers.valueAt(i);
            holder.resetToDefaultValue();
            holder.setOnClickListener(null);
            holder.setOnLongClickListener(null);
            holder.setOnFocusChangeListener(null);
        }
    }

    public void onDestroy() {
        onClear();
    }
    
    public void setViewForChoice(int i) {
    	AppItemView2 holder = mViewHolers.valueAt(i);
    	holder.setChioce();
    }
    
    public void resetViewForChoice(int i) {
    	AppItemView2 holder = mViewHolers.valueAt(i);
    	holder.resetChioce();
    }
    
    public int getViewForChoice(int i) {
    	AppItemView2 holder = mViewHolers.valueAt(i);
    	return holder.getChioceStatue();
    }

}

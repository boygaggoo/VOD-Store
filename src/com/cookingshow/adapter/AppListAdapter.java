package com.cookingshow.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.cookingshow.datacenter.DishDataInfo;
import com.cookingshow.network.controller.CommonImageLoader;
import com.cookingshow.push.PushAppDetailActivity;
import com.cookingshow.view.AppItemView;

public class AppListAdapter extends BaseListAdapter<DishDataInfo> {

    private static final String TAG = "AppListAdapter";
    private SparseArray<AppItemView> mViewHolers = null;
    private CommonImageLoader mImageLoader = null;
    protected List<DishDataInfo> mAppResult;
    private Context mContext;
    private boolean isRegisterReceiver = false;

    public AppListAdapter(Context context, List<DishDataInfo> appResult) {
        mContext = context;
        mViewHolers = new SparseArray<AppItemView>();
        mImageLoader = new CommonImageLoader();
        mAppResult = appResult;
    }

    public View createViewFromResource(final int position, View convertView) {
        AppItemView viewHolder;
        if (convertView != null) {
            viewHolder = (AppItemView) convertView;
        } else {
            viewHolder = new AppItemView(mContext);
        }
        viewHolder.setOnClickListener(onItemClickListener);
        viewHolder.setOnLongClickListener(onItemLongClickListener);
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
    }

    public void loadImageValue(int start, int end) {
        Log.i(TAG, "start-end:" + start + "-" + end);
        int size = mViewHolers.size();
        for (int i = start; i <= end; i++) {
            if (i >= 0 && i < size) {
                AppItemView viewHolder = mViewHolers.get(i);
                if (viewHolder != null) {
                    ImageView imageView = viewHolder.appIcon;
                    if (imageView.getTag() != null) {
                        String uri = imageView.getTag().toString();
                        if (!TextUtils.isEmpty(uri)) {
                            mImageLoader.loadImageWithManager(uri, imageView);
                        }
                    }
                }
            }
        }
    }

    public void loadImageValue(ArrayList<Integer> poss) {
        for (int index : poss) {
            AppItemView viewHolder = mViewHolers.get(index);
            if (viewHolder != null) {
                String url = viewHolder.iconUrl;
                if (!TextUtils.isEmpty(url)) {
                    if (!TextUtils.isEmpty(url)) {
                        mImageLoader.loadImageWithManager(url, viewHolder.appIcon);
                    }
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
            return mViewHolers.get(pos);
        }
        return null;
    }

    private View.OnLongClickListener onItemLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int pos = Integer.valueOf(v.getTag().toString());
            if (mAppResult == null || pos >= mAppResult.size()) {
                return true;
            } else {
                final DishDataInfo mDishDataInfo = mAppResult.get(pos);
                Log.i(TAG, "longClick:" + mDishDataInfo.getTitle());
            }
            return true;
        }
    };

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            int pos = Integer.valueOf(v.getTag().toString());
            Log.i(TAG, "pos:" + pos);
            if (mAppResult == null || pos >= mAppResult.size()) {
                return;
            }
            final DishDataInfo mDishDataInfo = mAppResult.get(pos);
            if (mDishDataInfo != null) {
            	Log.i(TAG, "click:" + mDishDataInfo.getDishId());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtra("appId", mDishDataInfo.getDishId());
                intent.putExtra("appName", mDishDataInfo.getTitle());
                intent.putExtra("appUrl", mDishDataInfo.getVideoUrl());
                intent.putExtra("uploader", mDishDataInfo.getUploader());
                intent.putExtra("tips", mDishDataInfo.getTips());
                intent.putExtra("material", mDishDataInfo.getMaterials());
                
                intent.setClass(v.getContext(), PushAppDetailActivity.class);
                v.getContext().startActivity(intent);
            }

        }
    };

    public void onClear() {
        resetViewForDefaultValue();
        mViewHolers.clear();
    }

    public void resetViewForDefaultValue() {
        int size = mViewHolers.size();
        for (int i = 0; i < size; i++) {
            AppItemView holder = mViewHolers.valueAt(i);
            holder.resetToDefaultValue();
            holder.setOnClickListener(null);
            holder.setOnLongClickListener(null);
            holder.setOnFocusChangeListener(null);
        }
    }


    public void onDestroy() {
        onClear();
    }

}

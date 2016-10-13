package com.cookingshow.network.controller;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.cookingshow.R;
import com.cookingshow.network.cache.imp.LRUCountLimitedMemoryCache;
import com.cookingshow.network.model.NetworkImageView;
import com.cookingshow.page.DisplayImageMgr;

public class CommonImageLoader {
    
    private static final String TAG = "CommonImageLoader";
    private ImageLoader mImageLoader = null;
    private int mDefaultImgResId = R.drawable.store_default_img;
    private int mErrorImgResId = R.drawable.store_default_img;
    private int mMaxImageWidth = 0;
    private int mMaxImageHeight = 0;
    private ImageLoader.ImageCache mImageCache = null;
    
    public CommonImageLoader() {
        mImageLoader = CommonVolley.getInstance().getImageLoader();
        mImageCache = CommonVolley.getInstance().getImageCache();
    }

    public void removeCache(String uri) {
        if (mImageCache!= null && mImageCache instanceof LRUCountLimitedMemoryCache) {
            ((LRUCountLimitedMemoryCache)mImageCache).remove(uri);
        }
    }
    
    public void setDefaultAndErrorImgId(int defaultId, int errorId) {
        this.mDefaultImgResId = defaultId;
        this.mErrorImgResId = errorId;
    }
    
    public void setMaxImageWidthAndHeight(int maxImageWidth, int maxImageHeight) {
        this.mMaxImageWidth = maxImageWidth;
        this.mMaxImageHeight = maxImageHeight;
    }

    public void loadImageWithManager(String url, ImageView view) {
        loadImage(url, view, DisplayImageMgr.getInstance().getImageListener(view));
    }
    
    public void loadImage(String url, ImageView view) {
        loadImage(url, view, null, mDefaultImgResId, mErrorImgResId);
    }
    
    public void loadImage(String url, ImageView view, int defaultImageId, int errorImageId) {
        loadImage(url, view, null, defaultImageId, errorImageId);
    }
    
    public void loadImage(String url, ImageView view, ImageListener imageListener) {
        loadImage(url, view, imageListener, mDefaultImgResId, mErrorImgResId);
    }
    
    private void loadImage(String url, ImageView view, ImageListener imageListener,
            int defaultImageId, int errorImageId) {
        //Log.d(TAG, "loadImage url " + url);
        if (view instanceof NetworkImageView) {
            loadNetworkImage(url, (NetworkImageView)view, imageListener, defaultImageId, errorImageId);
        } else {
            if (imageListener == null) {
                imageListener = ImageLoader.getImageListener(view, defaultImageId, errorImageId);
            }
            mImageLoader.get(url, imageListener, mMaxImageWidth, mMaxImageHeight);
        }
    }
    
    private void loadNetworkImage(String url, NetworkImageView view, ImageListener imageListener,
            int defaultImageId, int errorImageId) {
        view.setImageListener(imageListener);
        view.setDefaultImageResId(defaultImageId);
        view.setErrorImageResId(errorImageId);
        view.setMaxImageWidthAndHeight(mMaxImageWidth, mMaxImageHeight);
        view.setImageUrl(url, mImageLoader);
    }

    public void loadLocalAppDrawable(Context context, ImageView v, String pkgName) {
        if (v == null) {
            return;
        }
        try {
            if (!TextUtils.isEmpty(pkgName)) {
                PackageManager pm = context.getPackageManager();
                Drawable cacheDraw = pm.getApplicationInfo(pkgName, 0).loadIcon(pm);
                if (cacheDraw != null) {
                    v.setImageDrawable(cacheDraw);
                }
                return;
            }
        } catch (Exception e) {

        }
    }
    
}

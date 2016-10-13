package com.cookingshow.network.controller;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.cookingshow.network.cache.imp.BaseLruCache;
import com.cookingshow.network.cache.imp.LRUCountLimitedMemoryCache;

public class CommonVolley {

    private static final String TAG = "CommonVolley";
    private ImageLoader imageLoader = null;
    private ImageCache imageCache= null;
    private static CommonVolley instance = null;
    private RequestQueue mRequestQueue = null;
    
    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = ".store";
    private static final int DEFAULT_DISK_USAGE_BYTES = 50 * 1024 * 1024;
    
    public static CommonVolley getInstance() {
        if (instance == null) {
            instance = new CommonVolley();
        }
        return instance;
    }
    
    private CommonVolley() {
        //imageCache = new BaseLruCache();
        imageCache = new LRUCountLimitedMemoryCache(40);
    }

    public void init(Context context) {
        Log.d(TAG, "init");
        if (mRequestQueue == null) {
            mRequestQueue = CommonVolley.newRequestQueue(context);
            imageLoader = new ImageLoader(mRequestQueue, imageCache);
        } else {
            mRequestQueue.start();
        }
    }
    
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
    
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public ImageCache getImageCache() {
        return imageCache;
    }

    public void stopRequestQueue() {
        if (mRequestQueue != null) {
            mRequestQueue.stop();
        }
    }
    
    
    /**
     * Creates a default instance of the worker pool and calls {@link com.android.volley.RequestQueue#start()} on it.
     *
     * @param context A {@link android.content.Context} to use for creating the cache dir.
     * @param stack An {@link com.android.volley.toolbox.HttpStack} to use for the network, or null for default.
     * @return A started {@link com.android.volley.RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
//        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        File cacheDir = new File(getCacheDir(context), DEFAULT_CACHE_DIR);
        
        String userAgent = "appstore/volley";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
//                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        Network network = new BasicNetwork(stack);

        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir, DEFAULT_DISK_USAGE_BYTES), network);
        queue.start();

        return queue;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link com.android.volley.RequestQueue#start()} on it.
     *
     * @param context A {@link android.content.Context} to use for creating the cache dir.
     * @return A started {@link com.android.volley.RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }
    
    private static String getCacheDir(Context context) {
        String cacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            cacheDir = context.getCacheDir().getAbsolutePath();
        }
        Log.i(TAG, "newRequestQueue cacheDir " + cacheDir);
        return cacheDir;
    }
}

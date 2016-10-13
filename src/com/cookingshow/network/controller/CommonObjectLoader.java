package com.cookingshow.network.controller;

import java.util.Map;

import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.cookingshow.network.model.BaseModelRequest;
import com.cookingshow.network.model.IData;

public class CommonObjectLoader {

    private static final String TAG = "CommonObjectLoader";

    private static CommonObjectLoader instance = null;
    private RequestQueue mRequestQueue = null;

    private CommonObjectLoader() {
        mRequestQueue = CommonVolley.getInstance().getRequestQueue();
    }

    public static CommonObjectLoader getInstance() {
        if (instance == null) {
            instance = new CommonObjectLoader();
        }
        return instance;
    }
    
    public <T extends IData> void addRequest(Class<T> mClass, String url,
            BaseModelRequest.ModelResonseListener<T> baseResponseListener) {
        addRequest(mClass, url, null, baseResponseListener);
    }

    public <T extends IData> void addRequest(Class<T> mClass, String url,
            Map<String, String> requestHeaders,
            BaseModelRequest.ModelResonseListener<T> baseResponseListener) {
        addRequest(mClass, url, requestHeaders, null, baseResponseListener);
    }
    
    public <T extends IData> void addRequest(Class<T> mClass, String url,
            Map<String, String> requestHeaders, byte[] body,
            BaseModelRequest.ModelResonseListener<T> baseResponseListener) {
        Log.d(TAG, "addRequest " + url);
        if (mRequestQueue != null) {
            BaseModelRequest<T> request = new BaseModelRequest<T>(mClass, Method.POST,
                    url, baseResponseListener);
            request.setRequestParams(requestHeaders);
            request.setBody(body);
            mRequestQueue.add(request);
        }
    }
    
    public <T extends IData> void addRequest(BaseModelRequest<T> request) {
        if (mRequestQueue != null) {
            mRequestQueue.add(request);
        }
    }

}

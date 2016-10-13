package com.cookingshow.network.controller;

import java.util.Map;

import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.cookingshow.network.model.BaseStringRequest;

public class CommonStringLoader {

    private static final String TAG = "CommonStringLoader";
    private static CommonStringLoader instance = null;
    private RequestQueue mRequestQueue = null;
    
    private CommonStringLoader() {
        mRequestQueue = CommonVolley.getInstance().getRequestQueue();
    }
    
    public static CommonStringLoader getInstance() {
        if (instance == null) {
            instance = new CommonStringLoader();
        }
        return instance;
    }
    
    public void addRequest(String url, Map<String, String> params,
            final BaseStringRequest.StringResponseListener clientResponseListener) {
        addRequest(url, params, null, clientResponseListener);
    }
    
    public void addRequest(String url, Map<String, String> params, byte[] body,
            final BaseStringRequest.StringResponseListener clientResponseListener) {
        Log.i(TAG, "addRequest " + url);
        if (mRequestQueue != null) {
            BaseStringRequest request = new BaseStringRequest(Method.POST, url, clientResponseListener);
            request.setRequestParams(params);
            request.setRequestBody(body);
            mRequestQueue.add(request);
        }
    }
    
    public void addRequest(BaseStringRequest request) {
        Log.i(TAG, "addRequest " + request.getUrl());
        if (mRequestQueue != null) {
            mRequestQueue.add(request);
        }
    }
    
}

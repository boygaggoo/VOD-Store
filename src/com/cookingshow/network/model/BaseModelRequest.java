package com.cookingshow.network.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cookingshow.network.exception.BackendException;
import com.cookingshow.network.exception.CommonException;
import com.cookingshow.network.exception.NoConnException;
import com.cookingshow.network.exception.TimeoutException;

public class BaseModelRequest<T extends IData> extends Request<List<IData>> {

    private static final String TAG = "BaseModelRequest";
    
    /** Socket timeout in milliseconds for json requests */
    private static final int INIT_SOCKET_TIMEOUT_MS = 5000;

    /** Default number of retries for json requests */
    private static final int SOCKET_MAX_RETRIES = 1;

    /** Default backoff multiplier for json requests */
    private static final float SOCKET_BACKOFF_MULT = 1.0f;
    
    private Map<String, String> mRequestParams = null;
    private byte[] mBody = null;
    private Class<T> mModelClass = null;
    private ModelResonseListener<T> mModelResponseListener = null;

    public BaseModelRequest(final Class<T> modelClass, String url,
            final ModelResonseListener<T> listener) {
        this(modelClass, Method.POST, url, listener);
    }

    public BaseModelRequest(final Class<T> modelClass, int method, String url,
            final ModelResonseListener<T> listener) {
        super(method, url, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null) {
                    listener.onErrorResponse(switchEpgException(error));
                }
            }
        });
        this.mModelClass = modelClass;
        this.mModelResponseListener = listener;
        setRetryPolicy(
                new DefaultRetryPolicy(INIT_SOCKET_TIMEOUT_MS, SOCKET_MAX_RETRIES, SOCKET_BACKOFF_MULT));
        setShouldCache(false);
    }

    public void setRequestParams(Map<String, String> requestHeaders) {
        mRequestParams = requestHeaders;
    }
    
    public Map<String, String> getRequestParams() {
        return mRequestParams;
    }

    public void setBody(byte[] body) {
        this.mBody = body;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mRequestParams;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        if (mRequestParams != null) {
            for (String key : mRequestParams.keySet()) {
                try {
                    headers.put(key, URLEncoder.encode(mRequestParams.get(key), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mBody != null) {
            return mBody;
        }
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            JSONObject body = new JSONObject();
            for (String key : params.keySet()) {
                Log.d(TAG, "key=" + key + " value=" + params.get(key));
                try {
                    body.put(key, params.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return body.toString().getBytes();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Response<List<IData>> parseNetworkResponse(NetworkResponse response) {
        Map<String, String> headers = response.headers;
        for(String key : headers.keySet()){
            Log.d(TAG, key + "->>>>>" + headers.get(key));
        }
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            IData instance = getNewInstance(mModelClass);
            List<IData> list = null;
            if (instance != null) {
                list = (List<IData>) instance.parseJsonArray(jsonString);
                if (list == null) {
                    list = new ArrayList<IData>();
                    IData object = instance.parseJsonObject(jsonString);
                    if (object != null) {
                        list.add(object);
                    }
                }
            }
            return Response.success(list, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException ue) {
            return Response.error(new ParseError(ue));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void deliverResponse(List<IData> response) {
        if (mModelResponseListener != null) {
            mModelResponseListener.onResponse((List<T>) response);
        }
    }

    private static IData getNewInstance(Class<? extends IData> mIDataClass) {
        try {
            return mIDataClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static CommonException switchEpgException(VolleyError error) {
        int code = 0;
        String msg = error.getMessage();
        if (error.networkResponse != null) {
            code = error.networkResponse.statusCode;
        }
        if (error instanceof ServerError) {
            return new BackendException(code, msg);
        } else if (error instanceof TimeoutError) {
            return new TimeoutException(code, msg);
        } else if (error instanceof NoConnectionError) {
            return new NoConnException(code, msg);
        }
        return new CommonException(code, msg);
    }

    public interface ModelResonseListener<T extends IData> {

        public void onResponse(List<T> datas);
        public void onErrorResponse(CommonException exception);

    }

}

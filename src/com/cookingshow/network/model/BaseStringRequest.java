package com.cookingshow.network.model;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.cookingshow.network.exception.BackendException;
import com.cookingshow.network.exception.CommonException;
import com.cookingshow.network.exception.NoConnException;
import com.cookingshow.network.exception.TimeoutException;

public class BaseStringRequest extends StringRequest {

    /** Socket timeout in milliseconds for json requests */
    private static final int INIT_SOCKET_TIMEOUT_MS = 5000;

    /** Default number of retries for json requests */
    private static final int SOCKET_MAX_RETRIES = 1;

    /** Default backoff multiplier for json requests */
    private static final float SOCKET_BACKOFF_MULT = 1.0f;
    
    protected Map<String, String> mParams = null;
    private byte[] mBody = null;

    public BaseStringRequest(int method, String url, final StringResponseListener clientResponse) {
        this(method, url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (clientResponse != null) {
                    clientResponse.onResponse(response);
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (clientResponse != null) {
                    clientResponse.onErrorResponse(switchEpgException(error));
                }
            }
        });

    }

    public BaseStringRequest(int method, String url, Listener<String> listener,
            ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        setRetryPolicy(
                new DefaultRetryPolicy(INIT_SOCKET_TIMEOUT_MS, SOCKET_MAX_RETRIES, SOCKET_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        if (mParams != null) {
            for (String key : mParams.keySet()) {
                headers.put(key, mParams.get(key));
            }
        }
        return headers;
    }

    public void setRequestParams(Map<String, String> params) {
        this.mParams = params;
    }

    public void setRequestBody(byte[] body) {
        this.mBody = body;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
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
                Log.d("BaseStringRequest", "key=" + key + " value=" + params.get(key));
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

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        Log.d("BaseStringRequest", "parseNetworkResponse code=" + response.statusCode);
        String parsed;
        try {
            //parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        	parsed = new String(response.data, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
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
            return new TimeoutException(msg);
        } else if (error instanceof NoConnectionError) {
            return new NoConnException(msg);
        }
        return new CommonException(msg);
    }

    public interface StringResponseListener {

        public void onResponse(String str);
        public void onErrorResponse(CommonException exception);

    }

}

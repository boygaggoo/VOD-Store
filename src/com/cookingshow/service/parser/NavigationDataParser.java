package com.cookingshow.service.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.cookingshow.service.data.NavigationDataInfo;

public class NavigationDataParser {
	private static final String TAG = "NavigationDataParser";
	
	public NavigationDataParser() {
		// TODO Auto-generated constructor stub
		Log.i(TAG, "NavigationDataParser");
	}

	public List<NavigationDataInfo> getNavigationDataList(HttpURLConnection conn) {
        List<NavigationDataInfo> dataList = new ArrayList<NavigationDataInfo>();

        InputStream is = null;
        
        try {
            is = conn.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] bytes = readStream(is);
        String sDatas = new String(bytes);
        
        try {
			JSONObject jsonObject = new JSONObject(sDatas);
			
			String mStatus=jsonObject.getString("status");
			
			if(mStatus.equals("success")) {
				JSONArray jsonArray = jsonObject.getJSONArray("info");
				if (jsonArray.length() != 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject item = jsonArray.getJSONObject(i);
						NavigationDataInfo naviData = new NavigationDataInfo();
						naviData.setMenuId(Integer.parseInt(item.getString("item_id")));
						naviData.setType(item.getString("type"));
						naviData.setTitle(item.getString("title"));
						naviData.setEnTitle(item.getString("en_title"));
						naviData.setCode(item.getString("code"));
						naviData.setOrderNum(Integer.parseInt(item.getString("order_num")));
						
						dataList.add(naviData);
					}
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return dataList;
	}
	
	public static byte[] readStream(InputStream inputStream) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		
		try {
			while ((len = inputStream.read(buffer)) != -1) {
				bout.write(buffer, 0, len);
			}
			
			bout.close();
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bout.toByteArray(); 
	}
}

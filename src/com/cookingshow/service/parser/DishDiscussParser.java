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

import com.cookingshow.service.data.DishDiscussInfo;

public class DishDiscussParser {

	private static final String TAG = "DishDiscussParser";

	public DishDiscussParser() {
		// TODO Auto-generated constructor stub
		Log.i(TAG, "DishDiscussParser");
	}

	public List<DishDiscussInfo> getDishDiscussList(HttpURLConnection conn) {
        List<DishDiscussInfo> dataList = new ArrayList<DishDiscussInfo>();

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
						DishDiscussInfo discussData = new DishDiscussInfo();
						discussData.setDiscussId(Integer.parseInt(item.getString("discuss_id")));
						discussData.setDishId(Integer.parseInt(item.getString("dish_id")));
						discussData.setSendTime(item.getString("send_time"));
						discussData.setSender(item.getString("sender"));						
						discussData.setReceiver(item.getString("receiver"));
						discussData.setContent(item.getString("content"));
						discussData.setStatus(item.getString("status"));
						
						dataList.add(discussData);
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
		byte[] buffer = new byte[2048];
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

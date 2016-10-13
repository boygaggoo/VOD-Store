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

import com.cookingshow.service.data.DishDataInfo;

public class DishDataParser {
	private static final String TAG = "DishDataParser";

	public DishDataParser() {
		// TODO Auto-generated constructor stub
		Log.i(TAG, "DishDataParser");
	}

	public List<DishDataInfo> getDishDataList(HttpURLConnection conn) {
        List<DishDataInfo> dataList = new ArrayList<DishDataInfo>();

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
						DishDataInfo dishData = new DishDataInfo();
						dishData.setDishId(Integer.parseInt(item.getString("dish_id")));
						dishData.setUploadTime(item.getString("upload_time"));
						dishData.setUploader(item.getString("uploader"));
						dishData.setTitle(item.getString("name"));
						dishData.setType(item.getString("type"));
						dishData.setThumbUrl(item.getString("thumbnail_url"));
						dishData.setVideoUrl(item.getString("video_url"));
						dishData.setTips(item.getString("tips"));
						dishData.setMaterials(item.getString("materials"));
						dataList.add(dishData);
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

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

import com.cookingshow.service.data.TopDataInfo;

public class TopDataParser {
	private static final String TAG = "TopDataParser";

	public TopDataParser() {
		// TODO Auto-generated constructor stub
		Log.i(TAG, "TopDataParser");
	}

	public List<TopDataInfo> getTopDataList(HttpURLConnection conn) {
        List<TopDataInfo> dataList = new ArrayList<TopDataInfo>();

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
						TopDataInfo topData = new TopDataInfo();
						topData.setDishId(Integer.parseInt(item.getString("dish_id")));
						topData.setUploader(item.getString("uploader"));
						topData.setTitle(item.getString("name"));
						topData.setThumbUrl(item.getString("thumbnail_url"));
						topData.setVideoUrl(item.getString("video_url"));
						topData.setTips(item.getString("tips"));
						topData.setMaterials(item.getString("materials"));
						dataList.add(topData);
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

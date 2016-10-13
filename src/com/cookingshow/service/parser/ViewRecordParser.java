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

import com.cookingshow.service.data.ViewRecordDataInfo;

public class ViewRecordParser {
	private static final String TAG = "ViewRecordParser";

	public ViewRecordParser() {
		// TODO Auto-generated constructor stub
		Log.i(TAG, "ViewRecordParser");
	}

	public List<ViewRecordDataInfo> getViewRecordDataList(HttpURLConnection conn) {
        List<ViewRecordDataInfo> dataList = new ArrayList<ViewRecordDataInfo>();

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
						ViewRecordDataInfo recordData = new ViewRecordDataInfo();
						recordData.setAlbumId(Integer.parseInt(item.getString("album_id")));
						recordData.setDishId(Integer.parseInt(item.getString("dish_id")));
						recordData.setUploader(item.getString("uploader"));
						recordData.setTitle(item.getString("name"));
						recordData.setThumbUrl(item.getString("thumbnail_url"));
						recordData.setVideoUrl(item.getString("video_url"));
						recordData.setTips(item.getString("tips"));
						recordData.setMaterials(item.getString("materials"));
						recordData.setViewTimes(Integer.parseInt(item.getString("view_times")));
						recordData.setLikeTimes(Integer.parseInt(item.getString("like_times")));
						dataList.add(recordData);
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

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

import com.cookingshow.service.data.ShareImgInfo;

public class ShareImgParser {

	private static final String TAG = "ShareImgParser";

	public ShareImgParser() {
		// TODO Auto-generated constructor stub
		Log.i(TAG, "ShareImgParser");
	}

	public List<ShareImgInfo> getShareImgList(HttpURLConnection conn) {
        List<ShareImgInfo> dataList = new ArrayList<ShareImgInfo>();

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
						ShareImgInfo shareImgData = new ShareImgInfo();
						shareImgData.setImgId(Integer.parseInt(item.getString("img_id")));
						shareImgData.setDishId(Integer.parseInt(item.getString("dish_id")));
						shareImgData.setDishName(item.getString("dish_name"));
						shareImgData.setUploaderTime(item.getString("upload_time"));
						shareImgData.setUploader(item.getString("uploader"));
						shareImgData.setImgUrl(item.getString("img_url"));
						shareImgData.setFeeling(item.getString("feeling"));
						shareImgData.setLikeTimes(Integer.parseInt(item.getString("like_times")));
						shareImgData.setPublished(Integer.parseInt(item.getString("published")));
						
						dataList.add(shareImgData);
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

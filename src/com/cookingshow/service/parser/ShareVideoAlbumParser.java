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

import com.cookingshow.service.data.ShareVideoAlbumInfo;

public class ShareVideoAlbumParser {

	private static final String TAG = "ShareVideoAlbumParser";

	public ShareVideoAlbumParser() {
		// TODO Auto-generated constructor stub
		Log.i(TAG, "ShareVideoAlbumParser");
	}

	public List<ShareVideoAlbumInfo> getVideoAlbumDataList(HttpURLConnection conn) {
        List<ShareVideoAlbumInfo> dataList = new ArrayList<ShareVideoAlbumInfo>();

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
						ShareVideoAlbumInfo dishData = new ShareVideoAlbumInfo();
						dishData.setAlbumId(Integer.parseInt(item.getString("album_id")));
						dishData.setUploader(item.getString("uploader"));
						dishData.setTitle(item.getString("name"));
						dishData.setThumbUrl(item.getString("thumbnail_url"));
						dishData.setUploadTime(item.getString("upload_time"));
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

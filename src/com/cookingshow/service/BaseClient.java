package com.cookingshow.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cookingshow.service.util.ConnectionManager;


public abstract class BaseClient {
    protected String contentId;
    protected String title;
    protected String className;
    protected String api;
    protected String apiParam;
    protected int timer;
    protected Context context;
    protected ConnectionManager conManager;

    public abstract void refreshData(long timer);

    public abstract void gatheringData();

    public abstract void cancelGathering();

    public abstract void setNetworkListener();

    protected byte[] makeJpgIconBlob(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (bitmap != null)
        {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }

        return null;
    }

    protected byte[] makePngIconBlob(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (bitmap != null)
        {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }

        return null;
    }
    
    protected String saveBitmap2file(Bitmap bitmap, String path, String name) {
    	String ret = "";
		String mSavePath = path + "/" + "ACache";
		File dir = new File(mSavePath);
		if (!dir.exists()) {
			dir.mkdir();
		}
	
	    if (bitmap != null)
	    {
	        FileOutputStream fos;
			try {
				File mSaveFile = new File(mSavePath, name.hashCode() + "");
				if(mSaveFile.exists()) {
					mSaveFile.delete();
				}
					
				fos = new FileOutputStream(mSaveFile);
		        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		        fos.flush();
		        fos.close();

		        ret = mSaveFile.getPath();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        bitmap.recycle();
	        bitmap = null;
	    }

    	return ret;
    }

    protected Bitmap getBitmapFromURL(String url) {
        Bitmap bitmap = null;
        InputStream is = null;

        if (url == null || "".equals(url)) {
            return null;
        }

        try {
            is = new URL(url).openConnection().getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    public void setContentId(String contendId) {
        this.contentId = contendId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public void setApiParam(String apiParam) {
        this.apiParam = apiParam;
    }
    
    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setConnectionManager(ConnectionManager mgr) {
        this.conManager = mgr;
    }

    public String getApi() {
        return api;
    }

    public String getApiParam() {
        return apiParam;
    }
    
    public String getClassName() {
        return className;
    }

    public String getContentId() {
        return contentId;
    }

    public String getTitle() {
        return title;
    }
}

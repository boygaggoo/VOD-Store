package com.cookingshow.service.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.cookingshow.service.data.UpdateDataInfo;

public class UpdateParser {
    private static final String TAG = "UpdateParser";

    public UpdateParser() {
        Log.i(TAG, "UpdateParser");
    }

    private UpdateDataInfo updateData = new UpdateDataInfo();

    public List<UpdateDataInfo> getUpdateDataList(HttpURLConnection conn) {
        List<UpdateDataInfo> dataList = new ArrayList<UpdateDataInfo>();

        InputStream is = null;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            try {
                is = conn.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            xpp.setInput(is, "utf-8");

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {

                } else if (eventType == XmlPullParser.END_DOCUMENT) {
                	updateData = null;
                } else if (eventType == XmlPullParser.START_TAG) {
                    processStartElement(xpp);
                } else if (eventType == XmlPullParser.END_TAG) {
                    String entry = xpp.getName();
                    if ("update".equals(entry)) {
                        if (updateData != null) {
                            dataList.add(updateData);
                            updateData = null;
                            updateData = new UpdateDataInfo();
                        }

                    }
                } else if (eventType == XmlPullParser.TEXT) {

                }

                eventType = xpp.next();

            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
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

        return dataList;
    }

    private void processStartElement(XmlPullParser xpp) throws XmlPullParserException {
        String name = xpp.getName();

        try {
            if ("version".equals(name)) {
            	updateData.setVersion(xpp.nextText());
            } else if ("name".equals(name)) {
            	updateData.setName(xpp.nextText());
            } else if ("url".equals(name)) {
            	updateData.setUrl(xpp.nextText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

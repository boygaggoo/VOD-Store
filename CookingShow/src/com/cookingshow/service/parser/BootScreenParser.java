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

import com.cookingshow.service.data.BootScreenInfo;

public class BootScreenParser {
    private static final String TAG = "BootScreenParser";

    public BootScreenParser() {
        Log.i(TAG, "BootScreenParser");
    }
    
    private BootScreenInfo bootScreen = new BootScreenInfo();
    
    public List<BootScreenInfo> getBootScreenList(HttpURLConnection conn) {
        List<BootScreenInfo> dataList = new ArrayList<BootScreenInfo>();

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
                	bootScreen = null;
                } else if (eventType == XmlPullParser.START_TAG) {
                    processStartElement(xpp);
                } else if (eventType == XmlPullParser.END_TAG) {
                    String entry = xpp.getName();
                    if ("item".equals(entry)) {
                        if (bootScreen != null) {
                            dataList.add(bootScreen);
                            bootScreen = null;
                            bootScreen = new BootScreenInfo();
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
            if ("title".equals(name)) {
            	bootScreen.setTitle(xpp.nextText());
            } else if ("img".equals(name)) {
            	bootScreen.setThumbUrl(xpp.nextText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

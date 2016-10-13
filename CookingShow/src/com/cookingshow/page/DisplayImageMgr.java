package com.cookingshow.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cookingshow.R;

public class DisplayImageMgr {

    private static final String TAG = "DisplayImageMgr";
    private static DisplayImageMgr instance = null;
    private Handler mImageHandler = null;
    private boolean isDisplay = true;
    private Map<String, DisplayImageView> mDisplaysView = null;
    public static final int MSG_START = 1;
    public static final int MSG_PAUSE = 0;
    public int DURATION_PER_ONE = 100;
    public int CNT_PER_ONE = 2;
    private boolean isDisplaying = false;
    private HashMap<String, Bitmap> mDefaultBitmap = null;
    public static int DEFAULT_DRAWABLE_ID = R.drawable.store_default_img;
    public static int DEFAULT_DRAWABLE_WIDTH = 215;
    public static int DEFAULT_DRAWABLE_HEIGHT = 215;

    private DisplayImageMgr() {
        mDisplaysView = new ConcurrentHashMap<String, DisplayImageView>();
        mDefaultBitmap = new HashMap<String, Bitmap>();
        mImageHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_START:
                        isDisplaying = true;
                        if (!isDisplay) {
                            isDisplaying = false;
                            return;
                        }
                        List<DisplayImageView> lists = getDisplayView();
                        if (!lists.isEmpty()) {
                            int displayedCnt = 0;
                            for (DisplayImageView item : lists) {
                                if (isDisplay) {
                                    if (isToDisplay(item)) {
                                        item.mImageView.setImageBitmap(item.mBitmap);
                                        displayedCnt += 1;
                                    }
                                } else {
                                    mDisplaysView.put(item.url, item);
                                }
                            }
                            lists.clear();
                            mImageHandler.sendEmptyMessageDelayed(MSG_START, displayedCnt * DURATION_PER_ONE);
                        }
                        isDisplaying = false;
                        break;

                    case MSG_PAUSE:
                        mImageHandler.removeMessages(MSG_START);
                        break;
                }

                super.handleMessage(msg);
            }

        };
    }

    private List<DisplayImageView> getDisplayView() {
        List<DisplayImageView> rets = new ArrayList<DisplayImageView>();
        int i = 1;
        for (String url : mDisplaysView.keySet()) {
            if (i <= CNT_PER_ONE) {
                rets.add(mDisplaysView.remove(url));
            } else {
                break;
            }
            i++;
        }
        return rets;
    }

    public static DisplayImageMgr getInstance() {
        if (instance == null) {
            instance = new DisplayImageMgr();
        }
        return instance;
    }

    public ImageLoader.ImageListener getImageListener(final ImageView imageView) {
        return new MyImageListener(imageView);
    }

    public void pauseDisplay() {
        Log.d(TAG, "");
        this.isDisplay = false;
        mImageHandler.sendEmptyMessage(MSG_PAUSE);
    }

    public void resumeDisplay() {
        Log.d(TAG, "");
        this.isDisplay = true;
        mImageHandler.sendEmptyMessageDelayed(MSG_START, 500);
    }

    public void cancelDisplay(String url) {
        DisplayImageView item = mDisplaysView.get(url);
        if (item == null) {
            item = new DisplayImageView();
        }
        item.isShow = false;
        mDisplaysView.put(url, item);
    }

    private boolean isToDisplay(DisplayImageView displayImageView) {
        if (!displayImageView.isShow || displayImageView.mImageView == null
                ) {
            return false;
        }
        return true;
    }

    private void addDisplayItem(String url, DisplayImageView displayItem) {
//        Log.d(TAG, "url:" + url);
        mDisplaysView.put(url, displayItem);
        if (isDisplay && !mImageHandler.hasMessages(MSG_START) && !isDisplaying) {
            mImageHandler.sendEmptyMessage(MSG_START);
        }
    }

    class MyImageListener implements ImageLoader.ImageListener {

        private final ImageView imageView;

        public MyImageListener(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
            if (response.getBitmap() != null) {
                String requestUrl = response.getRequestUrl();
                DisplayImageView displayItem = mDisplaysView.get(requestUrl);
                if (displayItem == null) {
                    displayItem = new DisplayImageView();
                }
                displayItem.mImageView = imageView;
                displayItem.mBitmap = response.getBitmap();
                displayItem.url = requestUrl;
                addDisplayItem(requestUrl, displayItem);

            } else {
                setDefaultBitmap(imageView);
            }

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, "" + error.getMessage() + " " + error.getLocalizedMessage());
            setDefaultBitmap(imageView);
        }

    }

    class DisplayImageView {
        public ImageView mImageView;
        public Bitmap mBitmap;
        public boolean isShow;
        public String url;

        public DisplayImageView() {
            isShow = true;
        }
    }

    public Bitmap getNinePatch(int width, int height, Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), DEFAULT_DRAWABLE_ID);

        byte[] chunk = bitmap.getNinePatchChunk();
        NinePatchDrawable np_drawable = new NinePatchDrawable(bitmap,
                chunk, new Rect(), null);
        np_drawable.setBounds(0, 0, width, height);

        Bitmap output_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output_bitmap);
        np_drawable.draw(canvas);
        return output_bitmap;
    }

    public void setDefaultBitmap(ImageView imageView, int width, int height) {
        if (width <= 0 || height <= 0) {
            imageView.setImageResource(DEFAULT_DRAWABLE_ID);
            return;
        }
        width = width < DEFAULT_DRAWABLE_WIDTH ? DEFAULT_DRAWABLE_WIDTH : width;
        height = height < DEFAULT_DRAWABLE_HEIGHT ? DEFAULT_DRAWABLE_HEIGHT : height;
        String key = width + "_" + height;
//        Log.d(TAG, "key:" + key);
        Bitmap bitmap = mDefaultBitmap.get(key);
        if (bitmap == null) {
            bitmap = getNinePatch(width, height, imageView.getContext());
            if (bitmap != null) {
                mDefaultBitmap.put(key, bitmap);
            }
        }
        imageView.setImageBitmap(bitmap);
    }

    public void setDefaultBitmap(ImageView imageView) {
        setDefaultBitmap(imageView, imageView.getWidth(), imageView.getHeight());
    }

}

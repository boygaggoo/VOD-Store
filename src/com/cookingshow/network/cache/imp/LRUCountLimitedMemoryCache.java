package com.cookingshow.network.cache.imp;

import android.graphics.Bitmap;


public class LRUCountLimitedMemoryCache extends LRULimitedMemoryCache{

    public LRUCountLimitedMemoryCache(int countLimit) {
        super(countLimit);
    }
    
    @Override
    protected int getSize(Bitmap value) {
        return 1;
    }

}

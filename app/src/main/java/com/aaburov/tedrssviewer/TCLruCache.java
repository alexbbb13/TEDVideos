package com.aaburov.tedrssviewer;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Giorgio on 18.05.2015.
 */
public class TCLruCache extends LruCache<String, Bitmap> {

    public TCLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        int kbOfBitmap = value.getByteCount() / 1024;
        return kbOfBitmap;
    }
}
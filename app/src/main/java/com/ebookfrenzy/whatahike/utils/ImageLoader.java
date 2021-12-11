package com.ebookfrenzy.whatahike.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static final LruCache<String, Bitmap> sMemoryCache;
    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();


    static {
        final int cacheSize = 1024 * 256;
        sMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            }
        };

    }

    public static void loadImage(String url, Listener<Bitmap> listener) {
        if (getBitmapFromMemCache(url) != null) {
            listener.onSuccess(getBitmapFromMemCache(url));
            return;
        }
        Listener<Bitmap> mainThreadListener = new MainThreadListener(listener);
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = BitmapUtil.createBitmap(url);
                    addBitmapToMemoryCache(url, bitmap);
                    mainThreadListener.onSuccess(bitmap);
                } catch (Exception e) {
                    mainThreadListener.onFailed(e);
                }
            }
        });
    }

    private static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            sMemoryCache.put(key, bitmap);
        }
    }

    private static Bitmap getBitmapFromMemCache(String key) {
        return sMemoryCache.get(key);
    }
}

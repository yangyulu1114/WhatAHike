package com.ebookfrenzy.whatahike.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.util.LruCache;

import com.ebookfrenzy.whatahike.MyApplication;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static final LruCache<String, Bitmap> sMemoryCache;
    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();

    static {
        final int cacheSize = 1024 * 256;
        Log.v("bush", "cacheSize " + cacheSize);
        sMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                Log.v("bush", "bitmap size " + bitmap.getByteCount() / 1024);
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void loadImage(String url, Listener<Bitmap> listener) {
        if (getBitmapFromMemCache(url) != null) {
            Log.v("bush", "cache load");
            listener.onSuccess(getBitmapFromMemCache(url));
            return;
        }
        Listener<Bitmap> mainThreadListener = new MainThreadListener(listener);
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                try {
                    in = createInputStream(url);
                    int degree = getBitmapDegree(url);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(degree);
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    addBitmapToMemoryCache(url, bitmap);
                    Log.v("bush", "add new cache");
                    mainThreadListener.onSuccess(bitmap);
                } catch (Exception e) {
                    mainThreadListener.onFailed(e);
                } finally {
                    try {
                        in.close();
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    private static InputStream createInputStreamFromUrl(String url) throws Exception {
        return new URL(url).openStream();
    }

    private static InputStream createInputStreamFromUri(String uri) throws Exception {
        return MyApplication.getAppContext().getContentResolver().openInputStream(Uri.parse(uri));
    }

    private static int getBitmapDegree(String url) throws Exception {
        InputStream inputStream = createInputStream(url);
        int degree = 0;
        ExifInterface exifInterface = new ExifInterface(inputStream);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        inputStream.close();
        return degree;
    }

    private static InputStream createInputStream(String url) throws Exception {
        if (url.startsWith("http")) {
            return createInputStreamFromUrl(url);
        } else if (url.startsWith("content") || url.startsWith("file")) {
            return createInputStreamFromUri(url);
        } else {
            throw new IllegalArgumentException(url);
        }
    }

    private static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            Log.v("bush", "put cache key " + key);

            sMemoryCache.put(key, bitmap);
        }
    }

    private static Bitmap getBitmapFromMemCache(String key) {
        return sMemoryCache.get(key);
    }
}

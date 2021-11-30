package com.ebookfrenzy.whatahike.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

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

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                Log.v("bush", "entryRemoved " + key);
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
                    int degree = getBitmapDegree(url);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(degree);
                    int[] screenSize = DisplayUtil.getScreenSize();
                    Log.v("bush", "screen width " + screenSize[0] + "screen height " + screenSize[1]);
                    Bitmap bitmap = decodeBitmap(url, screenSize[0]/2, screenSize[1]/2);
                    Log.v("bush", "bitmap size before rotate" + bitmap.getWidth() + " " + bitmap.getHeight());
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    Log.v("bush", "bitmap size after rotate" + bitmap.getWidth() + " " + bitmap.getHeight());
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

    public static Bitmap decodeBitmap(String url, int reqWidth, int reqHeight) throws Exception {
        InputStream inputStream = createInputStream(url);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        options.inSampleSize = calculateInSampleSize(url, options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(createInputStream(url), null, options);
    }

    public static int calculateInSampleSize(String url, BitmapFactory.Options options, int reqWidth, int reqHeight) throws Exception {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        int degree = getBitmapDegree(url) % 180;

        if (height > reqHeight || width > reqWidth) {

            final int needHeight = degree != 90 ? height : width;
            final int needWeight = degree != 90 ? width : height;

            while ((needHeight / inSampleSize) > reqHeight
                    || (needWeight / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
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

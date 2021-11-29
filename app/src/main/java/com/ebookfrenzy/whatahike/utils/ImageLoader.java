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
    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();

    public static void loadImage(String url, Listener<Bitmap> listener) {
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
}

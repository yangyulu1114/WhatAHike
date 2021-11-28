package com.ebookfrenzy.whatahike.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();

    public static void loadImage(String url, Listener<Bitmap> listener) {
        Listener<Bitmap> mainThreadListener = new MainThreadListener(listener);
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                InputStream in = null;
                try {
                    in = new java.net.URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    mainThreadListener.onSuccess(bitmap);
                } catch (Exception e) {
                    mainThreadListener.onFailed(e);
                }finally {
                    try {
                        in.close();
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
}

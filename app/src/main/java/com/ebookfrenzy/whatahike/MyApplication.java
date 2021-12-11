package com.ebookfrenzy.whatahike;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.ebookfrenzy.whatahike.notification.NotificationWorker;
import com.ebookfrenzy.whatahike.utils.SharedPrefUtil;

public class MyApplication extends Application {

    private static MyApplication sInstance;

    private Handler mMainHandler;
    private Handler mBgHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mMainHandler = new Handler();
        HandlerThread h = new HandlerThread("bg");
        h.start();
        mBgHandler = new Handler(h.getLooper());
        SharedPrefUtil.init(this);

        NotificationWorker.start();
    }

    public static void post(Runnable r, long delayMs) {
        sInstance.mMainHandler.postDelayed(r, delayMs);
    }

    public static void execute(Runnable r, long delayMs) {
        sInstance.mBgHandler.postDelayed(r, delayMs);
    }

    public static Looper getBgLooper() {
        return sInstance.mBgHandler.getLooper();
    }

    public static Context getAppContext() {
        return sInstance;
    }
}

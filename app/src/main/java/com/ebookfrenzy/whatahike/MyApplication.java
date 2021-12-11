package com.ebookfrenzy.whatahike;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ebookfrenzy.whatahike.notification.NotificationWorker;
import com.ebookfrenzy.whatahike.utils.SharedPrefUtil;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefUtil.init(this);

        MyApplication.context = getApplicationContext();
        NotificationWorker.start();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}

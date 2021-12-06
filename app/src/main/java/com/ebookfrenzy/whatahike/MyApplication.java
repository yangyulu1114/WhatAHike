package com.ebookfrenzy.whatahike;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ebookfrenzy.whatahike.model.Preference;
import com.ebookfrenzy.whatahike.notification.NotificationWorker;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.ebookfrenzy.whatahike.utils.SharedPrefUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.util.Arrays;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefUtil.init(this);

        MyApplication.context = getApplicationContext();
        registerActivityLifecycleCallbacks(this);
        NotificationWorker.start();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Log.v("bush", "onCreate : " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.v("bush", "onDestroy : " + activity.getClass().getSimpleName());
    }
}

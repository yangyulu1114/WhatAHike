package com.ebookfrenzy.whatahike;

import android.app.Application;
import android.content.Context;

import com.ebookfrenzy.whatahike.utils.SharedPrefUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefUtil.init(this);

        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}

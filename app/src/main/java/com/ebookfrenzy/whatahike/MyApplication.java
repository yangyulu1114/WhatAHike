package com.ebookfrenzy.whatahike;

import android.app.Application;

import com.ebookfrenzy.whatahike.utils.SharedPrefUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefUtil.init(this);
    }
}

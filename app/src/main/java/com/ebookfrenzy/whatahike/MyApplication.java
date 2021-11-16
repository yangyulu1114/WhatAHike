package com.ebookfrenzy.whatahike;

import android.app.Application;

import com.ebookfrenzy.whatahike.utils.SharedPrefUtil;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefUtil.init(this);
    }
}

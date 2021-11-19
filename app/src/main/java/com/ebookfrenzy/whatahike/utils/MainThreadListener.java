package com.ebookfrenzy.whatahike.utils;

import android.os.Handler;
import android.os.Looper;

public class MainThreadListener<T> implements Listener<T> {

    private Listener<T> mListener;
    private Handler mHandler;

    public MainThreadListener(Listener listener) {
        mListener = listener;
        mHandler = new Handler(Looper.getMainLooper());
    }


    @Override
    public void onSuccess(T data) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onSuccess(data);
            }
        });
    }

    @Override
    public void onFailed(Exception e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailed(e);
            }
        });
    }
}

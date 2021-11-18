package com.ebookfrenzy.whatahike.utils;

public interface UploadListener {
    void onSucceess(String url);
    void onFailed(Exception e);
}

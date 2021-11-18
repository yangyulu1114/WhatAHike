package com.ebookfrenzy.whatahike.utils;

public interface UploadListener {
    void onSucceess(String url);
    void onUploadProgress(float progress);
    void onFailed(Exception e);
}

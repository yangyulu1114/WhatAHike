package com.ebookfrenzy.whatahike.utils;

public interface Listener<T> {
    void onSuccess(T data);
    void onFailed(Exception e);
}

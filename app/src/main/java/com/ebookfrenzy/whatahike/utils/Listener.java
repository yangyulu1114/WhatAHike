package com.ebookfrenzy.whatahike.utils;

public interface Listener<T> {
    void onSucceess(T data);
    void onFailed(Exception e);
}

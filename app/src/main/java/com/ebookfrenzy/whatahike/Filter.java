package com.ebookfrenzy.whatahike;

public interface Filter<T> {
    boolean pass(T trail);
}

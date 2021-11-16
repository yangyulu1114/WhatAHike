package com.ebookfrenzy.whatahike;

import com.ebookfrenzy.whatahike.model.Trail;

public interface Filter<T> {
    boolean pass(T trail);
}

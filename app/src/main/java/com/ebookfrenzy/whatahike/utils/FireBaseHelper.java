package com.ebookfrenzy.whatahike.utils;

import com.ebookfrenzy.whatahike.model.FireBaseModel;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FireBaseHelper<T extends FireBaseModel> {

    public void insert(T data) throws Exception {
        String name = data.getModelName();
    }

    public void update(T data) throws Exception {

    }

    public List<T> query(Map<String, String> map) {
        return Collections.emptyList();
    }

    public static String upload(File file) {
        return "";
    }
}

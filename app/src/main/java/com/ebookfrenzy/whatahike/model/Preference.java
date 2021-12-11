package com.ebookfrenzy.whatahike.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Preference {

    private static final String KEY_KEYS = "keys";

    final List<String> mKeys = new ArrayList<>();

    public Preference(List<String> keys) {
        if (keys != null && !keys.isEmpty()) {
            mKeys.addAll(keys);
        }
    }

    public List<String> getKeys() {
        return mKeys;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        StringBuilder sb = new StringBuilder();
        for (String key : mKeys) {
            sb.append(key).append(",");
        }
        try {
            jsonObject.put(KEY_KEYS, sb.toString());
        } catch (JSONException e) {
        }
        return jsonObject;
    }

    public static Preference fromJson(JSONObject jsonObject) {
        String keys = jsonObject.optString(KEY_KEYS);
        List<String> list = new ArrayList<>();
        for (String s : keys.split(",")) {
            if (!s.isEmpty()) {
                list.add(s);
            }
        }
        return new Preference(list);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}

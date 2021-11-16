package com.ebookfrenzy.whatahike.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtil {
    private static SharedPreferences sPreferences;
    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

    private static SharedPreferences getPreference() {
        if (sPreferences == null) {
            sPreferences = sContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        }
        return sPreferences;
    }

    public static synchronized void setValue(String key, String value) {
        getPreference().edit().putString(key, value).commit();
    }

    public static synchronized String getValue(String key) {
        return getPreference().getString(key, "");
    }
}

package com.ebookfrenzy.whatahike.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.ebookfrenzy.whatahike.MyApplication;

public class DisplayUtil {
    public static int[] getScreenSize() {
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        return new int[]{width, height};
    }

}

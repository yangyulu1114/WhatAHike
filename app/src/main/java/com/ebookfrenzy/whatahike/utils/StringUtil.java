package com.ebookfrenzy.whatahike.utils;

import com.ebookfrenzy.whatahike.MyApplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class StringUtil {
    public static String createMD5(String s) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(s.getBytes());
            return bytes2HexString(bytes);
        } catch (NoSuchAlgorithmException e) {
            return s;
        }
    }

    public static String bytes2HexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format(Locale.ROOT, "%02X", b));
        }
        return sb.toString();
    }

    public static String get(int id, Object... args) {
        return MyApplication.getAppContext().getString(id, args);
    }
}

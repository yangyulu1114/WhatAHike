package com.ebookfrenzy.whatahike;

import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.HttpUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RestAPI {
    public static List<Trail> getTrails(Filter<Trail> filter, Comparator<Trail> comparator) {
        List<Trail> trails = new ArrayList<>();
        for (Trail trail : readCSVTrails()) {
            if (filter.pass(trail)) {
                trails.add(trail);
            }
        }
        Collections.sort(trails, comparator);
        return trails;
    }

    private static List<Trail> readCSVTrails() {
        return Collections.emptyList();
    }

    public static List<Comment> getComments(String trailId) {
        return Collections.emptyList();
    }

    public static boolean postComment(Comment comment, List<File> images) {
        List<String> urls = new ArrayList<>();
        for(File image : images) {
            String url = HttpUtil.uploadImage(image);
            urls.add(url);
        }
        comment.setImages(urls);
        return true;
    }

    public static boolean register(String userName, String password) {
        // check inputs not null, format is correct
        return true;
    }

    public static boolean login(String userName, String password) {
        return true;
    }

    public static boolean updateHeadImage(String userId, File file) {
        String headUrl = HttpUtil.uploadImage(file);
        return true;
    }

    public static void setPref(String userName, String pref) {

    }

    public static String getPref(String userName) {
        return null;
    }
}

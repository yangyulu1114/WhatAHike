package com.ebookfrenzy.whatahike;

import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.FireBaseUtil;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.ebookfrenzy.whatahike.utils.MainThreadListener;
import com.ebookfrenzy.whatahike.utils.TrailsReadingUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestAPI {
    private static Map<String, Trail> trails;

    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();

    /**
     * @return distance in miles
     */
    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static Trail getTrailById(String trailId) {
        return trails.get(trailId);
    }

    public static List<Trail> getTrails(Filter<Trail> filter, Comparator<Trail> comparator)
        throws IllegalArgumentException {

        if (filter == null || comparator == null) {
            throw new IllegalArgumentException("Filter and Comparator can not be null.");
        }

        if (trails == null) {
            readCSVTrails();
        }

        List<Trail> filterTrails = new ArrayList<>();
        for (Trail trail : trails.values()) {
            if (filter.pass(trail)) {
                filterTrails.add(trail);
            }
        }
        Collections.sort(filterTrails, comparator);
        return filterTrails;
    }

    private static void readCSVTrails() {
        trails = TrailsReadingUtil.readCSVTrails();
    }

    public static void getComments(String trailId, Listener<List<Comment>> listener) {
        Listener<List<Comment>> mainThreadListener = new MainThreadListener(listener);
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Comment.DUMMY.query(Arrays.asList(trailId), mainThreadListener);
            }
        });
    }

    public static void postComment(Comment comment, List<File> images, Listener<Void> listener) {
        Listener<Void> mainThreadListener = new MainThreadListener(listener);
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<String> imageUrls = new ArrayList<>();
                for (File file : images) {
                    try {
                        String imageUrl = FireBaseUtil.uploadSync(file);
                        imageUrls.add(imageUrl);
                    } catch (Exception e) {
                        mainThreadListener.onFailed(e);
                        return;
                    }
                }
                comment.setImages(imageUrls);
                comment.insert(mainThreadListener);
            }
        });
    }


}

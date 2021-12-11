package com.ebookfrenzy.whatahike;

import android.net.Uri;
import android.util.Log;

import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.Preference;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.model.User;
import com.ebookfrenzy.whatahike.utils.FireBaseUtil;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.ebookfrenzy.whatahike.utils.MainThreadListener;
import com.ebookfrenzy.whatahike.utils.SharedPrefUtil;
import com.ebookfrenzy.whatahike.utils.TrailsReadingUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestAPI {
    private static final String KEY_USER_PREFERENCE = "key_user_preference";
    private static Map<String, Trail> trails;
    private static Set<String> activities;

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
        if (trails == null) {
            readCSVTrails();
        }
        return trails.get(trailId);
    }

    public static Set<String> getActivities() {
        if (trails == null) {
            readCSVTrails();
        }
        if (activities == null) {
            activities = new HashSet<>();
            for (Trail trail : trails.values()) {
                activities.addAll(trail.getActivities());
            }
        }
        return activities;
    }

    public static void getTrails(Filter<Trail> filter, Comparator<Trail> comparator, Listener<List<Trail>> listener)
        throws IllegalArgumentException {

        if (filter == null || comparator == null) {
            throw new IllegalArgumentException("Filter and Comparator can not be null.");
        }

        Listener<List<Trail>> mainThreadListener = new MainThreadListener(listener);
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
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
                mainThreadListener.onSuccess(filterTrails);
            }
        });
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

    public static void postComment(Comment comment, List<Uri> imageUris, Listener<Void> listener) {
        Listener<Void> mainThreadListener = new MainThreadListener(listener);
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<String> imageUrls = new ArrayList<>();
                for (Uri uri : imageUris) {
                    try {
                        String imageUrl = FireBaseUtil.uploadSync(uri);
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

    public static void getUserPreference(Listener<Preference> listener) {
        Listener<Preference> mainThreadListener = new MainThreadListener(listener);
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                User user = User.getCurrentUser();
                if (user == null) {
                    mainThreadListener.onFailed(new IllegalStateException("user not login?"));
                } else {
                    String key = String.format("%s-%s", KEY_USER_PREFERENCE, user.getUid());
                    String s = SharedPrefUtil.getValue(key);
                    Preference preference = new Preference(Collections.EMPTY_LIST);
                    try {
                        preference = Preference.fromJson(new JSONObject(s));
                    } catch (Exception e) {
                    }
                    mainThreadListener.onSuccess(preference);
                }
            }
        });
    }

    public static void setUserPreference(Preference preference) {
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                User user = User.getCurrentUser();
                if (user != null) {
                    String key = String.format("%s-%s", KEY_USER_PREFERENCE, user.getUid());
                    SharedPrefUtil.setValue(key, preference.toJson().toString());
                }
            }
        });
    }
}

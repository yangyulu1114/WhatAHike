package com.ebookfrenzy.whatahike.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.ebookfrenzy.whatahike.MyApplication;

import java.util.concurrent.CountDownLatch;

public class LocationUtil {

    private static LocationManager sLocationManager;
    private static Handler sHandler;

    @SuppressLint("MissingPermission")
    public static synchronized boolean getCurrentLocation(LocationListener listener) {
        if (sLocationManager == null) {
            Context context = MyApplication.getAppContext();
            sLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        if (sHandler == null) {
            HandlerThread h = new HandlerThread("location");
            h.start();
            sHandler = new Handler(h.getLooper());
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);

        String provider = sLocationManager.getBestProvider(criteria, false);
        if (provider != null) {
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    sLocationManager.requestSingleUpdate(provider, listener, sHandler.getLooper());
                }
            });
        }
        return provider != null;
    }

    // return distance in miles
    public static double getDistance(double[] loc1, double[] loc2) {
        double theta = loc1[1] - loc2[1];
        double dist = Math.sin(deg2rad(loc1[0])) * Math.sin(deg2rad(loc2[0]))
                + Math.cos(deg2rad(loc1[0])) * Math.cos(deg2rad(loc2[0])) * Math.cos(deg2rad(theta));
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
}

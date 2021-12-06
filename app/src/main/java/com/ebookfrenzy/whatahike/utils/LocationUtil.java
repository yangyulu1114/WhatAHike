package com.ebookfrenzy.whatahike.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.ebookfrenzy.whatahike.MyApplication;

import java.util.concurrent.CountDownLatch;

public class LocationUtil {
    private static LocationManager sLocationManager;

    @SuppressLint("MissingPermission")
    public static synchronized double[] getCurrentLocation() {
        if (sLocationManager == null) {
            Context context = MyApplication.getAppContext();
            sLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);

        String provider = sLocationManager.getBestProvider(criteria, false);
        CountDownLatch latch = new CountDownLatch(1);
        Location[] locations = new Location[1];
        sLocationManager.requestLocationUpdates(provider, 0, 0, new LocationListener() {

            @Override
            public void onLocationChanged(@NonNull Location location) {
                locations[0] = location;
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
        }
        return new double[]{locations[0].getLatitude(), locations[0].getLongitude()};
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

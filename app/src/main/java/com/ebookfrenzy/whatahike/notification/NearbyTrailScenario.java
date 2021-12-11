package com.ebookfrenzy.whatahike.notification;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ebookfrenzy.whatahike.Filter;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.ebookfrenzy.whatahike.utils.LocationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public abstract class NearbyTrailScenario implements NotificationScenario, LocationListener {

    private final CountDownLatch mLatch = new CountDownLatch(1);
    private Trail mTrail;

    public abstract NotificationScenarioType getScenarioType();

    public abstract double getDistanceThresholdKm();

    public abstract String createNotificationMsg(Trail trail);

    @Nullable
    @Override
    public NotificationResult checkAvailableResult() {

        if (LocationUtil.getCurrentLocation(this)) {
            try {
                mLatch.await();
            } catch (InterruptedException e) {
            }
        }

        if (mTrail != null) {
            return createNotificationResult(mTrail);
        }

        return null;
    }

    private NotificationResult createNotificationResult(Trail trail) {
        String msg = createNotificationMsg(trail);
        JSONObject extras = new JSONObject();
        try {
            extras.put("trailId", trail.getId());
        } catch (JSONException e) {
        }
        return new NotificationResult(msg, extras);
    }

    private final class TrailHandler implements Filter<Trail>, Comparator<Trail> {

        private final double[] mCurrentLocation;

        public TrailHandler(Location location) {
            mCurrentLocation = new double[] {location.getLatitude(), location.getLongitude()};
        }

        @Override
        public boolean pass(Trail trail) {
            return LocationUtil.getDistance(mCurrentLocation, trail.getLocation()) < getDistanceThresholdKm();
        }

        @Override
        public int compare(Trail o1, Trail o2) {
            double dis1 = LocationUtil.getDistance(mCurrentLocation, o1.getLocation());
            double dis2 = LocationUtil.getDistance(mCurrentLocation, o2.getLocation());
            return Double.compare(dis1, dis2);
        }
    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        TrailHandler trailHandler = new TrailHandler(location);
        RestAPI.getTrails(trailHandler, trailHandler, new Listener<List<Trail>>() {
            @Override
            public void onSuccess(List<Trail> data) {
                mTrail = data.size() > 0 ? data.get(0) : null;
                mLatch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                mLatch.countDown();
            }
        });
    }
}

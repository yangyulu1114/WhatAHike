package com.ebookfrenzy.whatahike.notification;

import androidx.annotation.Nullable;

import com.ebookfrenzy.whatahike.Filter;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.ebookfrenzy.whatahike.utils.LocationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public abstract class NearbyTrailScenario implements NotificationScenario {

    public abstract NotificationScenarioType getScenarioType();

    public abstract double getDistanceThresholdKm();

    public abstract String createNotificationMsg(Trail trail);

    @Nullable
    @Override
    public NotificationResult checkAvailableResult() {
        final Trail[] trail = new Trail[1];
        double[] curLocation = LocationUtil.getCurrentLocation();
        if (curLocation != null) {
            CountDownLatch latch = new CountDownLatch(1);
            TrailHandler trailHandler = new TrailHandler(curLocation);
            RestAPI.getTrails(trailHandler, trailHandler, new Listener<List<Trail>>() {
                @Override
                public void onSuccess(List<Trail> data) {
                    trail[0] = data.size() > 0 ? data.get(0) : null;
                    latch.countDown();
                }

                @Override
                public void onFailed(Exception e) {
                    latch.countDown();
                }
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
            }
            if (trail[0] != null) {
                return createNotificationResult(trail[0]);
            }
        }
        return null;
    }

    private NotificationResult createNotificationResult(Trail trail) {
        String msg = createNotificationMsg(trail);
        JSONObject extras = new JSONObject();
        try {
            extras.put("trailId", trail.getId());
        } catch (JSONException e) {
            // ignore
        }
        return new NotificationResult(msg, extras);
    }

    private final class TrailHandler implements Filter<Trail>, Comparator<Trail> {

        private final double[] mCurrentLocation;

        public TrailHandler(double[] currentLocation) {
            mCurrentLocation = currentLocation;
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
}

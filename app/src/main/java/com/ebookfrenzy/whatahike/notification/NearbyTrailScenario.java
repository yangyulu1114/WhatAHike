package com.ebookfrenzy.whatahike.notification;

import androidx.annotation.Nullable;

import com.ebookfrenzy.whatahike.Filter;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.LocationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.List;

public abstract class NearbyTrailScenario implements NotificationScenario {

    public abstract NotificationScenarioType getScenarioType();

    public abstract double getDistanceThresholdKm();

    public abstract String createNotificationMsg(Trail trail);

    @Nullable
    @Override
    public NotificationResult checkAvailableResult() {
        double[] curLocation = LocationUtil.getCurrentLocation();
        if (curLocation != null) {
            TrailHandler trailHandler = new TrailHandler(curLocation);
            List<Trail> trails = RestAPI.getTrails(trailHandler, trailHandler);
            if (!trails.isEmpty()) {
                return createNotificationResult(trails.get(0));
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

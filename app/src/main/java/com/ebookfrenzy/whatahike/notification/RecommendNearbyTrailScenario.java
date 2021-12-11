package com.ebookfrenzy.whatahike.notification;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.StringUtil;

public class RecommendNearbyTrailScenario extends NearbyTrailScenario {

    @Override
    public NotificationScenarioType getScenarioType() {
        return NotificationScenarioType.RECOMMEND_NEARBY_TRAIL;
    }

    @Override
    public double getDistanceThresholdKm() {
        return 20;
    }

    @Override
    public String createNotificationMsg(Trail trail) {
        return StringUtil.get(R.string.recommend_nearby_trail, trail.getName());
    }
}

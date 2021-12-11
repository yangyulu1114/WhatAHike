package com.ebookfrenzy.whatahike.notification;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.StringUtil;

public class CommentNearbyTrailScenario extends NearbyTrailScenario {

    @Override
    public NotificationScenarioType getScenarioType() {
        return NotificationScenarioType.COMMENT_NEARBY_TRAIL;
    }

    @Override
    public double getDistanceThresholdKm() {
        return 1;
    }

    @Override
    public String createNotificationMsg(Trail trail) {
        return StringUtil.get(R.string.comment_nearby_trail, trail.getName());
    }
}

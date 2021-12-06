package com.ebookfrenzy.whatahike.notification;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.ebookfrenzy.whatahike.Filter;
import com.ebookfrenzy.whatahike.MyApplication;
import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.LocationUtil;
import com.ebookfrenzy.whatahike.utils.StringUtil;

import java.util.Comparator;
import java.util.List;

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

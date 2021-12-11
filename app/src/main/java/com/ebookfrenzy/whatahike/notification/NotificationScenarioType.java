package com.ebookfrenzy.whatahike.notification;

import java.util.Locale;

public enum NotificationScenarioType {
    RECOMMEND_NEARBY_TRAIL,
    COMMENT_NEARBY_TRAIL,
    SUGGEST_HIKING_TRAIL;

    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}

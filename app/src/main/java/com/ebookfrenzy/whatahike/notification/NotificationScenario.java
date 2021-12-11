package com.ebookfrenzy.whatahike.notification;

import javax.annotation.Nullable;

public interface NotificationScenario {
    NotificationScenarioType getScenarioType();

    @Nullable NotificationResult checkAvailableResult();
}

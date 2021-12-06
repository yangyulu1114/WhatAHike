package com.ebookfrenzy.whatahike.notification;

import android.os.Bundle;

import javax.annotation.Nullable;

public interface NotificationScenario {
    NotificationScenarioType getScenarioType();

    @Nullable NotificationResult checkAvailableResult();
}

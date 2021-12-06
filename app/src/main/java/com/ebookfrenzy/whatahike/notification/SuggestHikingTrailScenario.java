package com.ebookfrenzy.whatahike.notification;

import androidx.annotation.Nullable;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.utils.StringUtil;

import java.util.Calendar;

public class SuggestHikingTrailScenario implements NotificationScenario {

    private static final Calendar CALENDAR = Calendar.getInstance();

    @Override
    public NotificationScenarioType getScenarioType() {
        return NotificationScenarioType.SUGGEST_HIKING_TRAIL;
    }

    @Nullable
    @Override
    public NotificationResult checkAvailableResult() {
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        int day = CALENDAR.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            String msg = StringUtil.get(R.string.suggest_hiking_trail);
            return new NotificationResult(msg);
        }
        return null;
    }
}

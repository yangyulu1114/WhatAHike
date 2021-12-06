package com.ebookfrenzy.whatahike.utils;

import android.os.Bundle;

import com.ebookfrenzy.whatahike.notification.NotificationScenarioType;

import org.json.JSONObject;

public class NotificationUtil {

    public static void sendNotification(String type, String message, JSONObject extras) {

        if (type.equals(NotificationScenarioType.RECOMMEND_NEARBY_TRAIL)) {

        } else if (type.equals(NotificationScenarioType.COMMENT_NEARBY_TRAIL)) {

        } else if (type.equals(NotificationScenarioType.SUGGEST_HIKING_TRAIL)) {

        }
    }

    private static void recommendTrail() {

    }

    private static void commentTrail() {

    }

    private static void suggestTrail() {

    }
}

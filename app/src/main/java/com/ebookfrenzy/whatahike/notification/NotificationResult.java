package com.ebookfrenzy.whatahike.notification;

import org.json.JSONObject;

public class NotificationResult {
    private final String message;
    private final
    JSONObject extras;

    public NotificationResult(String message) {
        this(message, new JSONObject());
    }

    public NotificationResult(String message, JSONObject extras) {
        this.message = message;
        this.extras = extras;
    }

    public String getMessage() {
        return message;
    }

    public JSONObject getExtras() {
        return extras;
    }
}

package com.ebookfrenzy.whatahike.notification;

import org.json.JSONObject;

import javax.annotation.Nullable;

public class NotificationResult {
    private final String message;
    private final @Nullable
    JSONObject extras;

    public NotificationResult(String message) {
        this(message, null);
    }

    public NotificationResult(String message, @Nullable JSONObject extras) {
        this.message = message;
        this.extras = extras;
    }

    public String getMessage() {
        return message;
    }

    @Nullable
    public JSONObject getExtras() {
        return extras;
    }
}

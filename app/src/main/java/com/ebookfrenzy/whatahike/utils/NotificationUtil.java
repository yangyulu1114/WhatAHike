package com.ebookfrenzy.whatahike.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ebookfrenzy.whatahike.MyApplication;
import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.notification.NotificationScenarioType;
import com.ebookfrenzy.whatahike.ui.activity.DetailedTrailActivity;
import com.ebookfrenzy.whatahike.ui.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationUtil {

    private static Context mContext;
    private static int notification_id;

    @SuppressLint("NewApi")
    public static void sendNotification(String type, String message, JSONObject extras) {
        Log.v("bush", String.format("send notification: type=%s, message=%s, extras=%s", type, message, extras.toString()));
        if (mContext == null) {
            mContext = MyApplication.getAppContext();
            notification_id = 0;
        }

        // using type as noti channel id
        NotificationChannel channel = createNotificationChannel(type);

        Intent intent;
        String hintMsg = "Click to check all suggestion trails.";
        if (type.equals(NotificationScenarioType.SUGGEST_HIKING_TRAIL.name())) {
            intent = new Intent(mContext, MainActivity.class);
        } else {
            intent = new Intent(mContext, DetailedTrailActivity.class);
            try {
                String trailId = extras.getString("trailId");
                intent.putExtra("trailId", trailId);
                hintMsg = "Click to check the trail information.";
            } catch (JSONException e) {
                Log.e("sendNotification: ", "fail to get trail id");
            }
        }
        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent, 0);

        NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification notify = new NotificationCompat.Builder(mContext, type)
                .setSmallIcon(R.drawable.ic_baseline_person_24)
                .setContentTitle(message)
                .setContentText(hintMsg)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // hide the notification after its selected
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .build();

        NotificationManagerCompat notification = NotificationManagerCompat.from(mContext);
        notification.notify(notification_id++, notify);
    }

    private static NotificationChannel createNotificationChannel(String type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(type, type, importance);
            channel.setDescription(type);

            return channel;
        }
        return null;
    }
}

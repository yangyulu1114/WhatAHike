package com.ebookfrenzy.whatahike.notification;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.impl.utils.PreferenceUtils;

import com.ebookfrenzy.whatahike.MyApplication;
import com.ebookfrenzy.whatahike.model.User;
import com.ebookfrenzy.whatahike.utils.NotificationUtil;
import com.ebookfrenzy.whatahike.utils.SharedPrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class NotificationWorker extends Worker {

    private static final int REPEAT_INTERVAL = 1;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
    private static final String KEY_NOTIFICATION = "notification";

    private static final AtomicBoolean FLAG = new AtomicBoolean();

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.v("bush", "doWork");
        startRun("WorkManager");
        return Result.success();
    }

    private static void startRun(String from) {
        Log.e("bush", String.format("Start run from %s", from));

        if (!FLAG.compareAndSet(false, true)) {
            return;
        }

        if (User.getCurrentUser() == null) {
            return;
        }

        final NotificationScenario[] SCENARIOS = {
                new CommentNearbyTrailScenario(),
                new RecommendNearbyTrailScenario(),
                new SuggestHikingTrailScenario()
        };

        Map<String, Long> history = getNotificationHistory();
        for (NotificationScenario scenario : SCENARIOS) {
            NotificationResult result = scenario.checkAvailableResult();
            if (result != null) {
                if (!isNotificationExist(scenario, history)) {
                    refreshNotificationHistory(scenario, history);
                    NotificationUtil.sendNotification(scenario.getScenarioType().name().toLowerCase(Locale.ROOT),
                            result.getMessage(), result.getExtras());
                    break;
                } else {
                    Log.v("bush", String.format("Notification exist: %s", scenario.getScenarioType().getName()));
                }
            }
        }

        FLAG.set(false);
    }

    private static void refreshNotificationHistory(NotificationScenario scenario, Map<String, Long> history) {
        history.put(scenario.getScenarioType().getName(), System.currentTimeMillis());
        SharedPrefUtil.setValue(KEY_NOTIFICATION, new JSONObject(history).toString());
    }

    private static boolean isNotificationExist(NotificationScenario scenario, Map<String, Long> history) {
        Long time = history.get(scenario.getScenarioType().getName());
        return time != null && System.currentTimeMillis() - time < 24 * 3600 * 1000;
    }

    private static Map<String, Long> getNotificationHistory() {
        Map<String, Long> map = new HashMap<>();
        String history = SharedPrefUtil.getValue(KEY_NOTIFICATION);
        if (!TextUtils.isEmpty(history)) {
            try {
                JSONObject jsonObject = new JSONObject(history);
                Iterator<String> iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    map.put(key, jsonObject.getLong(key));
                }
            } catch (JSONException e) {
            }
        }
        Log.v("bush", String.format("get notification history"));
        for (String key : map.keySet()) {
            Timestamp ts = new Timestamp(map.get(key));
            Date date = new Date(ts.getTime());
            Log.v("bush", String.format("key=%s, time=%s", key, date.toString()));
        }
        return map;
    }

    private static Constraints createConstraints() {
        return new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();
    }

    private static PeriodicWorkRequest createWorkRequest() {
        return new PeriodicWorkRequest.Builder(NotificationWorker.class,
                REPEAT_INTERVAL, TIME_UNIT)
                .setConstraints(createConstraints())
                .build();
    }

    public static void start() {
        WorkManager.getInstance().enqueueUniquePeriodicWork("notification",
                ExistingPeriodicWorkPolicy.KEEP, createWorkRequest());
        MyApplication.execute(new Runnable() {
            @Override
            public void run() {
                startRun("MyApplication");
            }
        }, 5000);
    }
}

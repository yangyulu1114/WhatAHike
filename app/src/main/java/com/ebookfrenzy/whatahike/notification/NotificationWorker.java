package com.ebookfrenzy.whatahike.notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ebookfrenzy.whatahike.utils.NotificationUtil;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationWorker extends Worker {

    private static final int REPEAT_INTERVAL = 1;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    private final NotificationScenario[] SCENARIOS = {
            new CommentNearbyTrailScenario(),
            new RecommendNearbyTrailScenario(),
            new SuggestHikingTrailScenario()
    };

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        for (NotificationScenario scenario : SCENARIOS) {
            NotificationResult result = scenario.checkAvailableResult();
            if (result != null) {
                NotificationUtil.sendNotification(scenario.getScenarioType().name().toLowerCase(Locale.ROOT),
                        result.getMessage(), result.getExtras());
                break;
            }
        }
        return Result.success();
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
    }
}

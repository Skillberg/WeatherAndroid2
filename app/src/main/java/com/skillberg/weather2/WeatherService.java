package com.skillberg.weather2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Сервис, получающий погоду
 */
public class WeatherService extends Service {

    private static final String TAG = "WeatherService";

    public static final String EXTRA_COUNT_TO = "count_to";

    private final Messenger messenger = new Messenger(new SignalHandler());

    private int countTo;
    private int currentNumber = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Notification notification = createNotification();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        countTo = intent.getIntExtra(EXTRA_COUNT_TO, 0);

        startCount();

        return START_REDELIVER_INTENT;
    }

    /**
     * Начинаем считать
     */
    private void startCount() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                currentNumber++;

                Log.i(TAG, "Current number: " + currentNumber);

                Notification notification = createNotification();
                startForeground(1, notification);

                if (currentNumber < countTo) {
                    handler.postDelayed(this, 1000);
                } else {
                    stopForeground(true);
                }
            }
        });
    }

    /**
     * Создаём уведомление
     */
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle("Counter is running");
        builder.setContentText("Current number: " + currentNumber);
        builder.setOngoing(true);

        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder.setContentIntent(pendingIntent);

        return builder.build();
    }


    /**
     * Handler для передачи сигнала в сервис
     */
    private class SignalHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "Got signal");
        }
    }

}

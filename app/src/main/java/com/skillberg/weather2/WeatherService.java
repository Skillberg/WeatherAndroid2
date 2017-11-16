package com.skillberg.weather2;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.skillberg.weather2.api.Api;
import com.skillberg.weather2.api.ApiFactory;
import com.skillberg.weather2.api.Constants;
import com.skillberg.weather2.api.models.CurrentWeather;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Сервис, получающий погоду
 */
public class WeatherService extends Service {

    private static final String TAG = "WeatherService";

    private LocationManager locationManager;

    private final Api api = ApiFactory.createApi();

    @Nullable
    private CurrentWeather currentWeather;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Notification notification = createNotification();
        startForeground(1, notification);

        setupLocation();
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * Запрашиваем погоду
     */
    private void getCurrentWeather(double latitude, double longitude) {
        Call<CurrentWeather> call = api.getCurrentWeather(
                latitude,
                longitude,
                Constants.API_KEY,
                Constants.DEFAULT_UNITS
        );

        call.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(@NonNull Call<CurrentWeather> call, @NonNull Response<CurrentWeather> response) {
                if (response.isSuccessful()) {
                    CurrentWeather currentWeather = response.body();
                    Log.i(TAG, "Got weather: " + currentWeather);

                    WeatherService.this.currentWeather = currentWeather;

                    setCurrentWeather();
                } else {
                    Log.e(TAG, "Failed to get current weather. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CurrentWeather> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to get current weather: " + t.getMessage());
            }
        });
    }

    /**
     * Получили погоду
     */
    private void setCurrentWeather() {
        Notification notification = createNotification();
        startForeground(1, notification);

        Intent intent = new Intent(MainActivity.ACTION_GOT_WEATHER);
        intent.putExtra(MainActivity.EXTRA_CURRENT_WEATHER, currentWeather);

        sendBroadcast(intent);

        SharedPreferences sharedPreferences = getSharedPreferences("weather", MODE_PRIVATE);
        sharedPreferences
                .edit()
                .putLong("last_update", System.currentTimeMillis())
                .apply();
    }

    /**
     * Создаём уведомление
     */
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notification);

        if (currentWeather == null) {
            builder.setContentTitle(getString(R.string.title_updating_weather));
            builder.setContentText(getString(R.string.text_updating_weather));
        } else {
            builder.setContentTitle(getString(
                    R.string.title_current_weather,
                    (int) currentWeather.getMain().getTemp(),
                    currentWeather.getCityName()));
            builder.setContentText(getString(
                    R.string.text_current_weather,
                    (int) currentWeather.getMain().getMinTemp(),
                    (int) currentWeather.getMain().getMaxTemp()
            ));
        }

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
     * Подписываемся на обновления гео
     */
    private void setupLocation() {
        // Получаем LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Получаем лучший провайдер
        Criteria criteria = new Criteria();

        String bestProvider = locationManager.getBestProvider(criteria, true);

        Log.v(TAG, "Best provider: " + bestProvider);


        if (bestProvider != null) {

            // На всякий случай проверим, не убрал ли пользователь разрешение на ГЕО
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED

                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            // Получаем последнюю доступную позицию
            Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);

            Log.v(TAG, "Last location: " + lastKnownLocation);

            if (lastKnownLocation != null) {
                getCurrentWeather(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            }

            // Подписываемся на обновления
            locationManager.requestLocationUpdates(
                    bestProvider, // провайдер
                    TimeUnit.HOURS.toMillis(1), // мин. время
                    10000, // мин. расстояние
                    locationListener
            );
        }
    }


    /**
     * Слушатель для обновления гео
     */
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.v(TAG, "Location changed: " + location);

            getCurrentWeather(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.v(TAG, "Status changed: " + provider + ", status: " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.v(TAG, "Provider enabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.v(TAG, "Provider disabled: " + provider);
        }
    };
}
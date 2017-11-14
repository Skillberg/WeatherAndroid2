package com.skillberg.weather2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Weather/MainActivity";

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 0;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkAndRequestGeoPermission();
    }

    @Override
    protected void onDestroy() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }

        super.onDestroy();
    }

    /**
     * Проверяем, есть ли разрешение и запрашиваем его, если нет
     */
    private void checkAndRequestGeoPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);

        } else {
            setupLocation();
        }
    }

    /**
     * Подписываемся на обновления гео
     */
    @SuppressLint("MissingPermission")
    private void setupLocation() {
        // Получаем LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Получаем лучший провайдер
        Criteria criteria = new Criteria();

        String bestProvider = locationManager.getBestProvider(criteria, true);

        Log.v(TAG, "Best provider: " + bestProvider);


        if (bestProvider != null) {
            // Получаем последнюю доступную позицию
            Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);

            Log.v(TAG, "Last location: " + lastKnownLocation);


            // Подписываемся на обновления
            locationManager.requestLocationUpdates(
                    bestProvider, // провайдер
                    0, // мин. время
                    0, // мин. расстояние
                    locationListener
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                setupLocation();
            } else {
                // Нет гео
                // Попробуем показать ещё раз

                checkAndRequestGeoPermission();
            }
        }
    }

    /**
     * Слушатель для обновления гео
     */
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.v(TAG, "Location changed: " + location);

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

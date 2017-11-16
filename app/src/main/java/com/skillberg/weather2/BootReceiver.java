package com.skillberg.weather2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Будет вызван, когда система загрузится
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            context.startService(new Intent(context, WeatherService.class));
        }
    }

}
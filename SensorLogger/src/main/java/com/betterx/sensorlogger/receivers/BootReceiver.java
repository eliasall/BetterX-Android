package com.betterx.sensorlogger.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.betterx.sensorlogger.service.LoggerService;
import com.betterx.sensorlogger.utils.CommSharedPreff;

public class BootReceiver extends BroadcastReceiver {

    static final String TAG = "BootReceiver";

    // handle boot event of the system
    public void onReceive(Context context, Intent intent) {
        // Check for the allowed state of app to run
        boolean isAllowedRunning = CommSharedPreff.loadBooleanSavedPreferences(CommSharedPreff.spKey_isLoggerStarted, context);
        if (isAllowedRunning) {
            // start system for sensor logging
            startSensorLogger(context);
        }
    }

    // start sensor logger to start sensor logging system
    public static ComponentName startSensorLogger(Context ctx) {
        Intent serviceIntent = new Intent(ctx, LoggerService.class);
        ComponentName name = ctx.startService(serviceIntent);
        return name;
    }

    // stop sensor logger to cancel sensor logging on file system
    public static boolean stopSensorLogger(Context ctx) {
        Intent serviceIntent = new Intent(ctx, LoggerService.class);
        boolean state = ctx.stopService(serviceIntent);
        return state;
    }

    // function to check whether some service is running or not
    public static boolean isServiceRunning(String serviceClassName, Context ctx) {
        String appPckgName = ctx.getApplicationContext().getPackageName();
        ActivityManager manager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClassName.equals(service.service.getClassName())
                    && appPckgName.equals(service.service.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}

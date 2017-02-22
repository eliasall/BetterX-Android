package com.betterx.featureslogger.data;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.betterx.featureslogger.datamodel.DeviceInfoStats;
import com.betterx.featureslogger.datamodel.ForegroundAppStats;
import com.betterx.featureslogger.services.ForegroundAppCheckService;
import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeatureLogger {

    private static final String PERSISTENT_PREFERENCES = "persistent_prefs";
    public static final String KEY_LAST_DETECTED_APP_VERSION = "key_app_version";

    public static void startLogging(Context context) {
        if(!isServiceRunning(context)) {
            final Intent intent = new Intent(context, ForegroundAppCheckService.class);
            context.startService(intent);
        }
    }

    public static void stopLogging(Context context) {
        final Intent intent = new Intent(context, ForegroundAppCheckService.class);
        context.stopService(intent);
    }

    public static String getLastDetectedAppVersion(Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(PERSISTENT_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(KEY_LAST_DETECTED_APP_VERSION, null);
    }

    public static void saveAppVersion(Context context, String string) {
        final SharedPreferences preferences = context.getSharedPreferences(PERSISTENT_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_LAST_DETECTED_APP_VERSION, string).apply();
    }

    public static DeviceInfoStats getDeviceInfoStats(Context context) {
        try {
            final File file = new File(FeatureLogger.getStatsDir(), getDeviceStatsFileName(context));
            final String json = Files.toString(file, Charset.forName("UTF-8"));
            final Gson gson = new Gson();
            return gson.fromJson(json, DeviceInfoStats.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ForegroundAppStats> getForegroundAppStats(Context context) {
        try {
            final File file = new File(FeatureLogger.getStatsDir(), getForegroundAppStatsFileName(context));
            final String json = Files.toString(file, Charset.forName("UTF-8"));
            final Type type = new TypeToken<ArrayList<ForegroundAppStats>>(){}.getType();
            final Gson gson = new Gson();
            final List<ForegroundAppStats> result = gson.fromJson(json, type);
            return result == null ? new ArrayList<ForegroundAppStats>() : result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static String getDeviceStatsFileName(Context context) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        final String uid = UIDGenerator.getUID(context);
        return String.format("%s_features_%s", uid, dateFormat.format(new Date(System.currentTimeMillis())));
    }

    public static String getForegroundAppStatsFileName(Context context) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        final String uid = UIDGenerator.getUID(context);
        return String.format("%s_apps_%s", uid, dateFormat.format(new Date(System.currentTimeMillis())));
    }

    public static String getStatsDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/BetterX/";
    }

    private static boolean isServiceRunning(Context context) {
        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ForegroundAppCheckService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}

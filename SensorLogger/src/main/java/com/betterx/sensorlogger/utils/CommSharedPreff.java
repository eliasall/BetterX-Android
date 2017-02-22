package com.betterx.sensorlogger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/*
    // Class to handle shared preference information about the app
 */
public class CommSharedPreff {
    // Shared preference file name
    private static final String sharedPrefsFile = "CommSharedPreff";

    // key to check whether service is started or stopped
    public static final String spKey_isLoggerStarted = "isLoggerStarted";

    public static final String spKey_isKeysSetup = "isKeySetup";
    public static final String spKey_filePathValue = "filePathValue";
    public static final String spKey_uidValue = "uidValue";
    public static final String spKey_deviceIdValue = "deviceIdValue";
    public static final String spKey_timeZoneValue = "timeZoneValue";
    public static final String spKey_awsAccessKeyValue = "awsAccessKeyValue";
    public static final String spKey_awsSecretKeyValue = "awsSecretKeyValue";
    public static final String spKey_awsUserNameValue = "awsUserNameValue";
    public static final String spKey_awsBucketNameValue = "awsBucketNameValue";
    public static final String spKey_pubKeyValue = "pubKeyValue";

    public static final String spKey_pullTimerAfter = "pullTimerAfter";
    public static final String spKey_uploadTriesCount = "uploadTriesCount";
    public static final String spKey_uploadTriesAfter = "uploadTriesAfter";

    public static void saveStringPreferences(String key, String value,
                                             Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static String loadStringSavedPreferences(String key, Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void saveIntPreferences(String key, int value, Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public static int loadIntSavedPreferences(String key, Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                Context.MODE_PRIVATE);
        return sp.getInt(key, -1);
    }

    public static void saveLongPreferences(String key, long value, Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    public static long loadLongSavedPreferences(String key, Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                Context.MODE_PRIVATE);
        return sp.getLong(key, -1);
    }

    public static void saveBooleanPreferences(String key, boolean value,
                                              Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public static boolean loadBooleanSavedPreferences(String key, Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

}
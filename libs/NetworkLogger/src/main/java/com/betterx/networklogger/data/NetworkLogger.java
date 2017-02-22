package com.betterx.networklogger.data;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.betterx.networklogger.dataModel.AdvancedNetworkState;
import com.betterx.networklogger.dataModel.NetworkState;
import com.betterx.networklogger.servers.NetworkStateCheckService;
import com.betterx.networklogger.utils.UIDGenerator;
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

public class NetworkLogger {

    public static final String PREFERENCES_NAME = "network_logger_preferences";
    private static final String KEY_LOGGING_ENABLED = "network_logging_enabled";

    public static void startLogging(Context context) {
        if(!isServiceRunning(context)) {
            final Intent intent = new Intent(context, NetworkStateCheckService.class);
            context.startService(intent);
            saveLoggingState(context, true);
        }
    }

    public static void stopLogging(Context context) {
        final Intent intent = new Intent(context, NetworkStateCheckService.class);
        context.stopService(intent);
        saveLoggingState(context, false);
    }

    public static boolean isLoggingEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_LOGGING_ENABLED, false);
    }

    private static void saveLoggingState(Context context, boolean state) {
        final SharedPreferences preferences = getPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_LOGGING_ENABLED, state);
        editor.apply();
    }

    /**
     * @param context - context
     * @return connectivity status shared preferences
     */
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(NetworkLogger.PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static List<NetworkState> getNetworkStats(Context context) {
        try {
            final File file = new File(getStatsDir(), getNetworkStatsFileName(context));
            final String json = Files.toString(file, Charset.forName("UTF-8"));
            final Type type = new TypeToken<List<NetworkState>>(){}.getType();
            final Gson gson = new Gson();
            final List<NetworkState> result = gson.fromJson(json, type);
            return result == null ? new ArrayList<NetworkState>() : result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<AdvancedNetworkState> getAdvancedNetworkState(Context context) {
        try {
            final File file = new File(getStatsDir(), getNetworkStatsFileName(context));
            final String json = Files.toString(file, Charset.forName("UTF-8"));
            final Type type = new TypeToken<List<AdvancedNetworkState>>(){}.getType();
            final Gson gson = new Gson();
            final List<AdvancedNetworkState> result = gson.fromJson(json, type);
            return result == null ? new ArrayList<AdvancedNetworkState>() : result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static String getNetworkStatsFileName(Context context) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        final String uid = UIDGenerator.getUID(context);
        return String.format("%s_network_%s", uid, dateFormat.format(new Date(System.currentTimeMillis())));
    }

    public static String getStatsDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/BetterX/";
    }

    private static boolean isServiceRunning(Context context) {
        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NetworkStateCheckService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}

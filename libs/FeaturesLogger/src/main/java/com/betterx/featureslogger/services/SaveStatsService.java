package com.betterx.featureslogger.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.betterx.featureslogger.data.FeatureLogger;
import com.betterx.featureslogger.datamodel.DeviceInfoStats;
import com.betterx.featureslogger.datamodel.ForegroundAppStats;
import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class SaveStatsService extends IntentService {

    private static final String ACTION_SAVE_DEVICE_STATS = "com.betterx.featureslogger.services.action.SAVE_DEVICE_STATS";
    private static final String ACTION_SAVE_FOREGROUND_APP_STATS = "com.betterx.featureslogger.services.action.SAVE_FOREGROUND_APP_STATS";

    private static final String EXTRA_DEVICE_INFO_STATS = "com.betterx.featureslogger.services.extra.DEVICE_INFO_STATS";
    private static final String EXTRA_FOREGROUND_APP_STATS = "com.betterx.featureslogger.services.extra.FOREGROUND_APP_STATS";

    public SaveStatsService() {
        super("SaveStatsService");
    }

    public static void saveDeviceStats(Context context, DeviceInfoStats stats) {
        final Intent intent = new Intent(context, SaveStatsService.class);
        intent.setAction(ACTION_SAVE_DEVICE_STATS);
        intent.putExtra(EXTRA_DEVICE_INFO_STATS, stats);
        context.startService(intent);
    }

    public static void saveForegroundAppStats(Context context, ForegroundAppStats stats) {
        final Intent intent = new Intent(context, SaveStatsService.class);
        intent.setAction(ACTION_SAVE_FOREGROUND_APP_STATS);
        intent.putExtra(EXTRA_FOREGROUND_APP_STATS, stats);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SAVE_DEVICE_STATS.equals(action)) {
                final DeviceInfoStats deviceInfoStats = (DeviceInfoStats) intent.getSerializableExtra(EXTRA_DEVICE_INFO_STATS);
                saveDeviceStatsToFile(deviceInfoStats);
            } else if (ACTION_SAVE_FOREGROUND_APP_STATS.equals(action)) {
                final ForegroundAppStats foregroundAppStats = (ForegroundAppStats) intent.getSerializableExtra(EXTRA_FOREGROUND_APP_STATS);
                saveForegroundAppStatsToFile(foregroundAppStats);
            }
        }
    }

    private void saveDeviceStatsToFile(DeviceInfoStats stats) {
        Log.d("BatterxStats", "Device app Stats: " + stats.toString());
        final String filename = FeatureLogger.getDeviceStatsFileName(this);
        final Gson gson = new Gson();
        saveStats(String.format("[%s]", gson.toJson(stats)), filename);
    }

    private void saveForegroundAppStatsToFile(ForegroundAppStats stats) {
        Log.d("BatterxStats", "Foreground app Stats: " + stats.toString());
        List<ForegroundAppStats> statsList = FeatureLogger.getForegroundAppStats(this);
        if(statsList == null) {
            statsList = new ArrayList<>();
        }

        statsList.add(stats);
        final String filename = FeatureLogger.getForegroundAppStatsFileName(this);
        final Gson gson = new Gson();
        saveStats(gson.toJson(statsList), filename);
    }

    private void saveStats(String statsJson, String fileName) {
        try {
            final File statsDir = new File (FeatureLogger.getStatsDir());
            if(!statsDir.exists()) {
                statsDir.mkdir();
            }
            final File file = new File(FeatureLogger.getStatsDir(), fileName);
            if(!file.exists()) {
                file.createNewFile();
            }
            Files.write(statsJson, file, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

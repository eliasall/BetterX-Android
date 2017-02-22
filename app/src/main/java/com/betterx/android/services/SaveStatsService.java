package com.betterx.android.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.betterx.featureslogger.data.FeatureLogger;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class SaveStatsService extends IntentService {

    private static final String ACTION_SAVE_STATS = "com.betterx.featureslogger.services.action.ACTION_SAVE_STATS";

    private static final String EXTRA_STATS_JSON = "com.betterx.featureslogger.services.extra.EXTRA_STATS_JSON";
    private static final String EXTRA_STATS_FILENAME = "com.betterx.featureslogger.services.extra.EXTRA_STATS_FILENAME";


    public SaveStatsService() {
        super("SaveStatsService");
    }

    /**
     * save some logs to the file
     * @param jsonToSave - json, which we should save
     * @param filename - filename
     */
    public static void saveStats(Context context, String jsonToSave, String filename) {
        final Intent intent = new Intent(context, SaveStatsService.class);
        intent.setAction(ACTION_SAVE_STATS);
        intent.putExtra(EXTRA_STATS_JSON, jsonToSave);
        intent.putExtra(EXTRA_STATS_FILENAME, filename);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SAVE_STATS.equals(action)) {
                final String statsJson = intent.getStringExtra(EXTRA_STATS_JSON);
                final String filename = intent.getStringExtra(EXTRA_STATS_FILENAME);
                saveStats(statsJson, filename);
            }
        }
    }

    /**
     * save some logs to the file
     * @param statsJson - json, which we should save
     * @param fileName - filename
     */
    private void saveStats(String statsJson, String fileName) {
        try {
            final File statsDir = new File(FeatureLogger.getStatsDir());
            if (!statsDir.exists()) {
                statsDir.mkdir();
            }
            final File file = new File(FeatureLogger.getStatsDir(), fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            Files.write(statsJson, file, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

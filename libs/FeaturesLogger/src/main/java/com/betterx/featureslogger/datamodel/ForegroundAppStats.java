package com.betterx.featureslogger.datamodel;

import android.content.Context;

import com.betterx.featureslogger.data.UIDGenerator;

import java.io.Serializable;

public class ForegroundAppStats implements Serializable {

    public long timestamp;
    public String uid;
    public String app;

    @Override
    public String toString() {
        return "ForegroundAppStats{" +
                "timestamp=" + timestamp +
                ", uid='" + uid + '\'' +
                ", app='" + app + '\'' +
                '}';
    }

    public static ForegroundAppStats generate(Context context, String version) {
        final ForegroundAppStats stats = new ForegroundAppStats();
        stats.timestamp = System.currentTimeMillis();
        stats.uid = UIDGenerator.getUID(context);
        stats.app = version;
        return stats;
    }

}

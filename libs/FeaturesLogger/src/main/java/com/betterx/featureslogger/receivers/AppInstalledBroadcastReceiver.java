package com.betterx.featureslogger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.betterx.featureslogger.datamodel.DeviceInfoStats;
import com.betterx.featureslogger.services.SaveStatsService;

public class AppInstalledBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context.getApplicationContext().getPackageName().equalsIgnoreCase(intent.getDataString())) {
            try {
                final PackageInfo pInfo = context.getPackageManager().getPackageInfo(intent.getDataString(), 0);
                SaveStatsService.saveDeviceStats(context, DeviceInfoStats.generate(context, pInfo.versionName));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}

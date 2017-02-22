package com.betterx.networklogger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.betterx.networklogger.servers.SaveLogService;

public class WifiSignalStrengthChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // We need to start WiFi scan after receiving an Intent
        // in order to get update with fresh data as soon as possible
        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        Log.d("NetworkLogeTag", "On wifi signal strength state changed!");
        //Save network state
        SaveLogService.saveNetworkInfo(context);
    }

}

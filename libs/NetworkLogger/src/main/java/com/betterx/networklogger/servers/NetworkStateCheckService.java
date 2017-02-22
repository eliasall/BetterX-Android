package com.betterx.networklogger.servers;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.betterx.networklogger.receivers.NetworkConnectionChangeReceiver;
import com.betterx.networklogger.receivers.WifiSignalStrengthChangeReceiver;

import java.util.Timer;
import java.util.TimerTask;

public class NetworkStateCheckService extends Service {

    private static final int UPDATE_TIME = 5000;

    private NetworkConnectionChangeReceiver networkConnectionChangeReceiver;
    private WifiSignalStrengthChangeReceiver wifiSignalStrengthChangeReceiver;

    private Timer timer;
    private NetworkCapabilities previousCapabilities;
    private LinkProperties previousLinkProperties;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wifiSignalStrengthChangeReceiver = new WifiSignalStrengthChangeReceiver();
        networkConnectionChangeReceiver = new NetworkConnectionChangeReceiver();
        registerWifiSignalStrengthChangeReceiver();
        registerNetworkConnectionChangeReceiver();
        Log.d("NetworkLogeTag", "Register receivers");

        // start WiFi scan in order to refresh access point list
        // if this won't be called WifiSignalStrengthChanged may never occur
        final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        //if device os version >= 23, check network Capabilities and linkProperties
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("NetworkLogeTag", "Start Capabilities and linkProperties listening");
            startChecking();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTimer();
        unregister();
    }

    /**
     * Unregister receivers
     */
    public void unregister() {
        unregisterReceiver(networkConnectionChangeReceiver);
        unregisterReceiver(wifiSignalStrengthChangeReceiver);
        Log.d("NetworkLogeTag", "Unregister receivers");
    }

    /**
     * Register network connection change eceiver
     */
    private void registerNetworkConnectionChangeReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(networkConnectionChangeReceiver, filter);
    }

    /**
     * Register Wifi Signal Strength Change Receiver
     */
    private void registerWifiSignalStrengthChangeReceiver() {
        final IntentFilter filter = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
        registerReceiver(wifiSignalStrengthChangeReceiver, filter);
    }

    /**
     * Start timer, to check network capabilities and linkProperties
     * if device os version >= 23
     */
    private void startChecking() {
        cancelTimer();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void run() {
                final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wifiManager.startScan();

                //Check current network capabilities and linkProperties
                final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                final Network network = cm.getActiveNetwork();
                final NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                final LinkProperties linkProperties = cm.getLinkProperties(network);
                //If current network capabilities and linkProperties are not equals with previous detected,
                //log new network state
                if (previousCapabilities != null && !previousCapabilities.equals(capabilities)
                        || previousCapabilities != null && !previousLinkProperties.equals(linkProperties)) {
                    SaveLogService.saveNetworkInfo(NetworkStateCheckService.this);

                    if(!previousCapabilities.equals(capabilities)) {
                        Log.d("NetworkLogeTag", "On capability state changed!");
                    } else {
                        Log.d("NetworkLogeTag", "On Link properties state changed!");
                    }
                    previousLinkProperties = linkProperties;
                    previousCapabilities = capabilities;

                }
            }
        }, 0, UPDATE_TIME);
    }

    /**
     * Stop timer, if it is running
     */
    private void cancelTimer() {
        if(timer != null) {
            Log.d("NetworkLogeTag", "Stop Capabilities and linkProperties listening");
            timer.cancel();
            timer.purge();
        }
    }

}

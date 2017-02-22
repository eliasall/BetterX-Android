package com.betterx.networklogger.servers;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.betterx.networklogger.data.NetworkLogger;
import com.betterx.networklogger.dataModel.AdvancedNetworkState;
import com.betterx.networklogger.dataModel.AvailableNetwork;
import com.betterx.networklogger.dataModel.ConnectionStatus;
import com.betterx.networklogger.dataModel.NetworkState;
import com.betterx.networklogger.utils.NetworkUtils;
import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class SaveLogService extends IntentService {

    private static final String ACTION_SAVE_NETWORK_STATS = "com.betterx.featureslogger.services.action.ACTION_SAVE_NETWORK_STATS";

    public SaveLogService() {
        super("SaveStatsService");
    }

    public static void saveNetworkInfo(Context context) {
        final Intent intent = new Intent(context, SaveLogService.class);
        intent.setAction(ACTION_SAVE_NETWORK_STATS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SAVE_NETWORK_STATS.equals(action)) {

                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    saveAdvancedStatsToFile(createAdvancedNetworkState());
                } else {
                    saveNetworkStatsToFile(createNetworkState());
                }
            }
        }
    }

    private void saveNetworkStatsToFile(NetworkState stats) {
        List<NetworkState> statsMap = NetworkLogger.getNetworkStats(this);
        if(statsMap == null) {
            statsMap = new ArrayList<>();
        }
        statsMap.add(stats);
        final String filename = NetworkLogger.getNetworkStatsFileName(this);
        final Gson gson = new Gson();
        saveStats(gson.toJson(statsMap), filename);
    }

    private void saveAdvancedStatsToFile(AdvancedNetworkState stats) {
         List<AdvancedNetworkState> statsMap = NetworkLogger.getAdvancedNetworkState(this);
        if(statsMap == null) {
            statsMap = new ArrayList<>();
        }
        statsMap.add(stats);
        final String filename = NetworkLogger.getNetworkStatsFileName(this);
        final Gson gson = new Gson();
        saveStats(gson.toJson(statsMap), filename);
    }

    /**
     * save some logs to the file
     * @param statsJson - json, which we should save
     * @param fileName - filename
     */
    private void saveStats(String statsJson, String fileName) {
        try {
            final File statsDir = new File(NetworkLogger.getStatsDir());
            if(!statsDir.exists()) {
                statsDir.mkdir();
            }
            final File file = new File(NetworkLogger.getStatsDir(), fileName);
            if(!file.exists()) {
                file.createNewFile();
            }
            Files.write(statsJson, file, Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * collect network state logs. Put all logs to the networkState object
     */
    private NetworkState createNetworkState() {
        final ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        final NetworkState networkState = new NetworkState();
        networkState.timestamp = System.currentTimeMillis();
        final NetworkInfo info =  connManager.getActiveNetworkInfo();
        networkState.wifiStatus = getNetworkStatus(info, ConnectivityManager.TYPE_WIFI) ? ConnectionStatus.CONNECTED : ConnectionStatus.NOT_CONNECTED;
        networkState.mobileStatus = getNetworkStatus(info, ConnectivityManager.TYPE_MOBILE) ? ConnectionStatus.CONNECTED : ConnectionStatus.NOT_CONNECTED;
        networkState.wiMaxStatus = getNetworkStatus(info, ConnectivityManager.TYPE_WIMAX) ? ConnectionStatus.CONNECTED : ConnectionStatus.NOT_CONNECTED;
        networkState.hasInternet = NetworkUtils.isOnline(this);
        networkState.detailedState = info != null ? info.getDetailedState() : null;
        networkState.extraInfo = info != null ? info.getExtraInfo() : null;
        networkState.availableNetworks = new ArrayList<>();

        if(networkState.wifiStatus == ConnectionStatus.CONNECTED) {
            final WifiInfo wifiInfo = wifiMan.getConnectionInfo();
            networkState.BSSID = wifiInfo.getBSSID();
            networkState.SSID = wifiInfo.getSSID();
            networkState.RSSI = wifiInfo.getRssi();
            networkState.signalStrength = WifiManager.calculateSignalLevel(networkState.RSSI, 5);
            networkState.MAC = wifiInfo.getMacAddress();
            networkState.linkSpeed = wifiInfo.getLinkSpeed();
            networkState.IP = wifiIpAddress(wifiInfo);
            networkState.netID = wifiInfo.getNetworkId();

            if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                networkState.frequency = wifiInfo.getFrequency();
            }
        }

        final List<ScanResult> scanResults = wifiMan.getScanResults();
        if(scanResults != null) {
            for(ScanResult result : wifiMan.getScanResults()) {
                final AvailableNetwork availableNetwork = new AvailableNetwork();
                availableNetwork.capabilities = result.capabilities;
                availableNetwork.BSSID = result.BSSID;
                availableNetwork.SSID = result.SSID;
                networkState.availableNetworks.add(availableNetwork);
            }
        }

        return networkState;
    }

    /**
     * collect network state logs. Put all logs to the networkState object
     * add some logs, which available only on newest android devices
     */
    private AdvancedNetworkState createAdvancedNetworkState() {
        final ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final AdvancedNetworkState networkState = new AdvancedNetworkState(createNetworkState());
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final Network network = connManager.getActiveNetwork();
            networkState.capabilities = new ArrayList<>();
            networkState.capabilities.add(connManager.getNetworkCapabilities(network));
            networkState.linkProperties = new ArrayList<>();
            networkState.linkProperties.add(connManager.getLinkProperties(network));
        }
        return networkState;
    }

    private boolean getNetworkStatus(NetworkInfo info, int networkType) {
        return info != null && info.getType() == networkType && info.isConnected();
    }

    protected String wifiIpAddress(WifiInfo wifiInfo) {
        int ipAddress = wifiInfo.getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }

}

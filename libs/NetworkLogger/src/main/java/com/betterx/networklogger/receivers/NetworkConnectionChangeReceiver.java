package com.betterx.networklogger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.betterx.networklogger.data.NetworkLogger;
import com.betterx.networklogger.dataModel.ConnectivityStatus;
import com.betterx.networklogger.servers.SaveLogService;
import com.betterx.networklogger.utils.NetworkUtils;

public class NetworkConnectionChangeReceiver extends BroadcastReceiver {

    private static final String KEY_LAST_STATUS = "last_status";

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityStatus connectivityStatus = getConnectivityStatus(context);
        if (isStatusChanged(context, connectivityStatus)) {
            Log.d("NetworkLogeTag", "On connection state changed!");
            saveNewStatus(context, connectivityStatus);
            SaveLogService.saveNetworkInfo(context);
        }
    }

    /**
     * @param context - context
     * @return current connectivity status
     */
    private ConnectivityStatus getConnectivityStatus(Context context) {
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            //if networkInfo is null, the device is offline
            return ConnectivityStatus.OFFLINE;
        }

        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            //Check is the wifi network has internet access
            final boolean isOnline = NetworkUtils.isOnline(context);
            return isOnline ? ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET
                    : ConnectivityStatus.WIFI_CONNECTED_HAS_NO_INTERNET;
        }

        if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return ConnectivityStatus.MOBILE_CONNECTED;
        }

        return ConnectivityStatus.UNKNOWN;
    }

    /**
     * @param context - context
     * @param status  - current connectivity status
     * @return true if current and last saved connectivity statuses are different
     */
    private boolean isStatusChanged(Context context, ConnectivityStatus status) {
        final ConnectivityStatus lastSavedStatus = getLastStatus(context);
        return lastSavedStatus != status;
    }

    /**
     * Save new connectivity status to shared preferences
     *
     * @param context - context
     * @param status  - current connectivity status
     */
    private void saveNewStatus(Context context, ConnectivityStatus status) {
        final SharedPreferences preferences = getPreferences(context);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_LAST_STATUS, status.ordinal());
        editor.apply();
    }

    /**
     * @param context - context
     * @return last saved connectivity status
     */
    private ConnectivityStatus getLastStatus(Context context) {
        final SharedPreferences preferences = getPreferences(context);
        final int statusId = preferences.getInt(KEY_LAST_STATUS, ConnectivityStatus.UNKNOWN.ordinal());
        return ConnectivityStatus.values()[statusId];
    }

    /**
     * @param context - context
     * @return connectivity status shared preferences
     */
    private SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(NetworkLogger.PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

}

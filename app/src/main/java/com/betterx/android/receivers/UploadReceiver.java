package com.betterx.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;

import com.betterx.android.app.BetterxApp;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.dataModel.TransmissionSettings;
import com.betterx.android.services.DataBackupService;

import javax.inject.Inject;

import timber.log.Timber;

public class UploadReceiver extends BroadcastReceiver {

    static final String TAG = "UploadReceiver";

    @Inject
    PersistentDataStore dataStore;

    @Override
    public void onReceive(Context context, Intent intent) {
        BetterxApp.component(context).inject(this);
        //get transmission settings
        final TransmissionSettings settings = dataStore.getTransmissionSettings();
        //check, should we upload data to the server or not
        final boolean upload = (!settings.isWifiConnected || isConnectedToWifi(context)) && (!settings.isCharging || isCharging(context));
        Timber.d("Upload " + upload + "; isWifiConnected " + isConnectedToWifi(context) + "; isCharging " + isCharging(context));
        if(upload) {
            //start data uploading
            DataBackupService.uploadStats(context);
        }
    }

    /**
     * @return true, if device connected to wifi
     */
    private boolean isConnectedToWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&  networkInfo.isConnected();
    }

    /**
     * @return true, if device charging
     */
    private boolean isCharging(Context context) {
        try {
            Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

}

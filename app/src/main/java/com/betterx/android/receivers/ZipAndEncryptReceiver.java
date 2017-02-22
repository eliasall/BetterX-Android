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
import com.betterx.android.services.ZipAndEncryptService;

import javax.inject.Inject;

import timber.log.Timber;

public class ZipAndEncryptReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ZipAndEncryptService.zipAndEncryptStats(context);
    }

}

package com.betterx.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.betterx.android.app.BetterxApp;
import com.betterx.android.data.PersistentDataStore;
import com.betterx.android.utils.AlarmUtils;
import com.betterx.featureslogger.data.FeatureLogger;
import com.betterx.networklogger.data.NetworkLogger;
import com.betterx.sensorlogger.receivers.BootReceiver;
import com.betterx.sensorlogger.utils.CommSharedPreff;

import javax.inject.Inject;

/**
 * Restart loggers after phone reboot
 */
public class PhoneBootReceiver extends BroadcastReceiver {

    @Inject
    PersistentDataStore dataStore;

    @Override
    public void onReceive(Context context, Intent intent) {
        BetterxApp.component(context).inject(this);

        try {
            // restart loggers after phone reboot
            NetworkLogger.startLogging(context);
            FeatureLogger.startLogging(context);
            BootReceiver.startSensorLogger(context);
            AlarmUtils.startFileZippingAlarm(context);
            AlarmUtils.startUploadAlarm(context, dataStore.getTransmissionSettings());
            CommSharedPreff.saveBooleanPreferences(CommSharedPreff.spKey_isLoggerStarted, true, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

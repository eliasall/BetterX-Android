package com.betterx.sensorlogger.sensors;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;

import com.ubhave.dataformatter.DataFormatter;
import com.ubhave.dataformatter.json.JSONFormatter;
import com.ubhave.datahandler.except.DataHandlerException;
import com.ubhave.datahandler.loggertypes.AbstractDataLogger;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.SensorDataListener;
import com.ubhave.sensormanager.config.GlobalConfig;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorUtils;

public abstract class SubscribeTask implements SensorDataListener {
    private final static String LOG_TAG = "SubscribeTask";

    private final Context context;
    protected final SparseIntArray subscriptions;
    protected ESSensorManager sensorManager;
    private AbstractDataLogger dataLogger;

    public SubscribeTask(final Context context, final AbstractDataLogger logger) {
        this.context = context;
        this.dataLogger = logger;

        subscriptions = new SparseIntArray();
        try {
            sensorManager = ESSensorManager.getSensorManager(context);
            sensorManager.setGlobalConfig(GlobalConfig.PRINT_LOG_D_MESSAGES, false);
        } catch (ESException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe() {
        // remove registered sensors
        try {
            for (int i = 0; i < subscriptions.size(); i++) {
                try {
                    int sensorType = subscriptions.keyAt(i);
                    int subscriptionId = subscriptions.get(sensorType);
                    sensorManager.unsubscribeFromSensorData(subscriptionId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataSensed(SensorData data) {
        // interface callback function with the data from sensor
        // log the data of the sensor
        try {
            int sensorType = data.getSensorType();
            Log.i(LOG_TAG, "Received from: " + SensorUtils.getSensorName(sensorType));

            // To format/store your data, check out the SensorDataManager library
            JSONFormatter formatter = DataFormatter.getJSONFormatter(context, data.getSensorType());
            Log.i(LOG_TAG, formatter.toJSON(data).toString());

            dataLogger.logSensorData(data);
        } catch (ESException | DataHandlerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCrossingLowBatteryThreshold(boolean isBelowThreshold) {
        // Nothing in this example
    }
}

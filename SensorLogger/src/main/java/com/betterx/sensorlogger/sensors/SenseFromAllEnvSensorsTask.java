package com.betterx.sensorlogger.sensors;

import android.content.Context;
import android.util.Log;

import com.ubhave.datahandler.loggertypes.AbstractDataLogger;
import com.ubhave.sensormanager.sensors.SensorEnum;
import com.ubhave.sensormanager.sensors.SensorUtils;

import java.util.ArrayList;
import java.util.List;

public class SenseFromAllEnvSensorsTask extends SubscribeTask {

    private final static String LOG_TAG = "AllEnvSensorsTask";

    public SenseFromAllEnvSensorsTask(final Context context, final AbstractDataLogger logger) {
        super(context, logger);
    }

    public void subscribe() {
        for (SensorEnum s : SensorEnum.values()) {
            if (s.isEnvironment() && getNecessarySensorsList().contains(s)) {
                try {
                    int subscriptionId = sensorManager.subscribeToSensorData(s.getType(), SenseFromAllEnvSensorsTask.this);
                    subscriptions.put(s.getType(), subscriptionId);
                    Log.i(LOG_TAG, "Subscribe to: " + SensorUtils.getSensorName(s.getType()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<SensorEnum> getNecessarySensorsList() {
        final List<SensorEnum> sensors = new ArrayList<>();
        sensors.add(SensorEnum.AMBIENT_TEMPERATURE);
        sensors.add(SensorEnum.HUMIDITY);
        sensors.add(SensorEnum.LIGHT);
//        sensors.add(SensorEnum.PRESSURE);
        return sensors;
    }

}

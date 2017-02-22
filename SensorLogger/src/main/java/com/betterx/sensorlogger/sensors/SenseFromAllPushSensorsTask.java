package com.betterx.sensorlogger.sensors;

import android.content.Context;
import android.util.Log;

import com.ubhave.datahandler.loggertypes.AbstractDataLogger;
import com.ubhave.sensormanager.SensorDataListener;
import com.ubhave.sensormanager.sensors.SensorEnum;
import com.ubhave.sensormanager.sensors.SensorUtils;

import java.util.ArrayList;
import java.util.List;

public class SenseFromAllPushSensorsTask extends SubscribeTask implements SensorDataListener {
    private final static String LOG_TAG = "AllPushSensorsTask";

    public SenseFromAllPushSensorsTask(Context context, final AbstractDataLogger logger) {
        super(context, logger);
    }

    public void subscribe() {
        for (SensorEnum s : SensorEnum.values()) {
            if (s.isPush() && getNecessarySensorsList().contains(s)) {

                try {
                    int subscriptionId = sensorManager.subscribeToSensorData(s.getType(), SenseFromAllPushSensorsTask.this);
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
        sensors.add(SensorEnum.CONNECTION_STRENGTH);
        sensors.add(SensorEnum.CONNECTION_STATE);
        sensors.add(SensorEnum.PASSIVE_LOCATION);
        sensors.add(SensorEnum.PHONE_STATE);
        sensors.add(SensorEnum.BATTERY);
        sensors.add(SensorEnum.SCREEN);
        return sensors;
    }

}

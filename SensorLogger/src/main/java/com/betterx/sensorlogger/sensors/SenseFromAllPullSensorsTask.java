package com.betterx.sensorlogger.sensors;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.ubhave.datahandler.loggertypes.AbstractDataLogger;
import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.SensorDataListener;
import com.ubhave.sensormanager.classifier.AccelerometerDataClassifier;
import com.ubhave.sensormanager.classifier.GyroscopeDataClassifier;
import com.ubhave.sensormanager.classifier.LocationDataClassifier;
import com.ubhave.sensormanager.classifier.MagneticFieldDataClassifier;
import com.ubhave.sensormanager.classifier.StepCounterDataClassifier;
import com.ubhave.sensormanager.classifier.WifiDataClassifier;
import com.ubhave.sensormanager.config.GlobalConfig;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.dutycyling.AdaptiveSensing;
import com.ubhave.sensormanager.sensors.SensorEnum;
import com.ubhave.sensormanager.sensors.SensorUtils;
import com.ubhave.sensormanager.sensors.pull.AccelerometerSensor;
import com.ubhave.sensormanager.sensors.pull.GyroscopeSensor;
import com.ubhave.sensormanager.sensors.pull.LocationSensor;
import com.ubhave.sensormanager.sensors.pull.MagneticFieldSensor;
import com.ubhave.sensormanager.sensors.pull.StepCounterSensor;
import com.ubhave.sensormanager.sensors.pull.WifiSensor;

import java.util.ArrayList;
import java.util.List;

public class SenseFromAllPullSensorsTask implements SensorDataListener {
    private final static String LOG_TAG = "AllPullSensorsTask";

    private final Context context;
    protected ESSensorManager sensorManager;
    private AbstractDataLogger dataLogger;
    private final List<SensorEnum> necessarySensors;

    public SenseFromAllPullSensorsTask(final Context context, final AbstractDataLogger logger) {
        this.context = context;
        this.dataLogger = logger;
        necessarySensors = getNecessarySensorsList();

        try {
            sensorManager = ESSensorManager.getSensorManager(context);
            sensorManager.setGlobalConfig(GlobalConfig.PRINT_LOG_D_MESSAGES, false);
        } catch (ESException e) {
            e.printStackTrace();
        }
    }

    public void subscribe() {
            for (SensorEnum s : SensorEnum.values()) {
                if (s.isPull() && necessarySensors.contains(s)) {
                    try {

                        if (s.getType() == SensorEnum.ACCELEROMETER.getType()) {
                            AdaptiveSensing.getAdaptiveSensing().registerSensor(sensorManager, AccelerometerSensor.getSensor(this.context), new AccelerometerDataClassifier(), AccelerometerSensor.getSensor(this.context), this);
                        } else if (s == SensorEnum.WIFI) {
                            AdaptiveSensing.getAdaptiveSensing().registerSensor(sensorManager, WifiSensor.getSensor(this.context), new WifiDataClassifier(), WifiSensor.getSensor(this.context), this);
                        } else if (s == SensorEnum.LOCATION) {
                            AdaptiveSensing.getAdaptiveSensing().registerSensor(sensorManager, LocationSensor.getSensor(this.context), new LocationDataClassifier(), LocationSensor.getSensor(this.context), this);
                        } else if (s == SensorEnum.GYROSCOPE) {
                            AdaptiveSensing.getAdaptiveSensing().registerSensor(sensorManager, GyroscopeSensor.getSensor(this.context), new GyroscopeDataClassifier(), GyroscopeSensor.getSensor(this.context), this);
                        } else if (s == SensorEnum.MAGNETIC_FIELD) {
                            AdaptiveSensing.getAdaptiveSensing().registerSensor(sensorManager, MagneticFieldSensor.getSensor(this.context), new MagneticFieldDataClassifier(), StepCounterSensor.getSensor(this.context), this);
                        } else if (s == SensorEnum.STEP_COUNTER) {
                            AdaptiveSensing.getAdaptiveSensing().registerSensor(sensorManager, StepCounterSensor.getSensor(this.context), new StepCounterDataClassifier(), StepCounterSensor.getSensor(this.context), this);
                        }
                        Log.i(LOG_TAG, "Subscribe to: " + SensorUtils.getSensorName(s.getType()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    public void unsubscribe() {
        for (SensorEnum s : SensorEnum.values()) {
            if (s.isPull() && necessarySensors.contains(s)) {
                try {
                    if (s == SensorEnum.ACCELEROMETER) {
                        AdaptiveSensing.getAdaptiveSensing().unregisterSensor(sensorManager, AccelerometerSensor.getSensor(this.context));
                    } else if (s == SensorEnum.LOCATION) {
                        AdaptiveSensing.getAdaptiveSensing().unregisterSensor(sensorManager, LocationSensor.getSensor(this.context));
                    } else if (s == SensorEnum.WIFI) {
                        AdaptiveSensing.getAdaptiveSensing().unregisterSensor(sensorManager, WifiSensor.getSensor(this.context));
                    } else if (s == SensorEnum.GYROSCOPE) {
                        AdaptiveSensing.getAdaptiveSensing().unregisterSensor(sensorManager, GyroscopeSensor.getSensor(this.context));
                    } else if (s == SensorEnum.MAGNETIC_FIELD) {
                        AdaptiveSensing.getAdaptiveSensing().unregisterSensor(sensorManager, MagneticFieldSensor.getSensor(this.context));
                    } else if (s == SensorEnum.STEP_COUNTER) {
                        AdaptiveSensing.getAdaptiveSensing().unregisterSensor(sensorManager, StepCounterSensor.getSensor(this.context));
                    }
                    Log.i(LOG_TAG, "UnSubscribe to: " + SensorUtils.getSensorName(s.getType()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDataSensed(SensorData data) {
        try {
            dataLogger.logSensorData(data);
            Log.i(LOG_TAG, new Gson().toJson(data));
            Log.i(LOG_TAG, "Received from: " + SensorUtils.getSensorName(data.getSensorType()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCrossingLowBatteryThreshold(boolean isBelowThreshold) {

    }

    private List<SensorEnum> getNecessarySensorsList() {
        final List<SensorEnum> sensors = new ArrayList<>();
        sensors.add(SensorEnum.ACCELEROMETER);
        sensors.add(SensorEnum.GYROSCOPE);
        sensors.add(SensorEnum.LOCATION);
        sensors.add(SensorEnum.MAGNETIC_FIELD);
        sensors.add(SensorEnum.WIFI);
        sensors.add(SensorEnum.STEP_COUNTER);
        return sensors;
    }

}

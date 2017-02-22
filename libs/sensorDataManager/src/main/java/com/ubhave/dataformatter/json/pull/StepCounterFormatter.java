package com.ubhave.dataformatter.json.pull;

import android.content.Context;

import com.ubhave.dataformatter.json.PullSensorJSONFormatter;
import com.ubhave.datahandler.except.DataHandlerException;
import com.ubhave.sensormanager.config.SensorConfig;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pull.StepCounterData;
import com.ubhave.sensormanager.process.AbstractProcessor;
import com.ubhave.sensormanager.process.pull.StepCounterProcessor;
import com.ubhave.sensormanager.sensors.SensorUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class StepCounterFormatter extends PullSensorJSONFormatter
{
    private final static String NUM_STEPS = "stepCount";
    private final static String LAST_BOOT_MILLIS = "lastBootMillis";
    private final static String LAST_BOOT = "lastBoot";

    public StepCounterFormatter(final Context context)
    {
        super(context, SensorUtils.SENSOR_TYPE_STEP_COUNTER);
    }

    @Override
    protected void addSensorSpecificData(JSONObject json, SensorData data) throws JSONException, DataHandlerException
    {
        StepCounterData stepData = (StepCounterData) data;
        long lastBoot = stepData.getLastBoot();
        if (lastBoot == 0)
        {
            throw new DataHandlerException(DataHandlerException.NO_DATA);
        }
        json.put(NUM_STEPS, stepData.getNumSteps());
//        json.put(LAST_BOOT_MILLIS, lastBoot);
//        json.put(LAST_BOOT, lastBoot);
    }

    @Override
    protected void addSensorSpecificConfig(JSONObject json, SensorConfig config) throws JSONException
    {}

    @Override
    public SensorData toSensorData(final String jsonString)
    {
        try
        {
            JSONObject jsonData = super.parseData(jsonString);
            long senseStartTimestamp = super.parseTimeStamp(jsonData);
            SensorConfig sensorConfig = super.getGenericConfig(jsonData);
            float numSteps = jsonData.getInt(NUM_STEPS);
            long lastBoot = jsonData.getLong(LAST_BOOT_MILLIS);

            boolean setRawData = true;
            boolean setProcessedData = false;

            StepCounterProcessor processor = (StepCounterProcessor) AbstractProcessor.getProcessor(applicationContext, sensorType, setRawData, setProcessedData);
            return processor.process(senseStartTimestamp, numSteps, lastBoot, sensorConfig);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

}

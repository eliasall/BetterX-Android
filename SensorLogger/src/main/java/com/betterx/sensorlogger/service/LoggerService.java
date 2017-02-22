package com.betterx.sensorlogger.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.betterx.sensorlogger.logging.DataStoreLogger;
import com.betterx.sensorlogger.sensors.SenseFromAllEnvSensorsTask;
import com.betterx.sensorlogger.sensors.SenseFromAllPullSensorsTask;
import com.betterx.sensorlogger.sensors.SenseFromAllPushSensorsTask;
import com.ubhave.datahandler.config.DataTransferConfig;
import com.ubhave.datahandler.except.DataHandlerException;
import com.ubhave.datahandler.loggertypes.AbstractDataLogger;
import com.ubhave.sensormanager.ESException;

/**
 * Created by mac on 04/06/15.
 */
public class LoggerService extends Service {

    private SenseFromAllEnvSensorsTask envSensorMgr = null;
    private SenseFromAllPushSensorsTask pushSensorMgr = null;
    private SenseFromAllPullSensorsTask pullSensorMgr = null;
//    private Timer pullSensorTimer = null;

    private AbstractDataLogger dataLogger;

    private Context mContext = null;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        LoggerService getService() {
            return LoggerService.this;
        }
    }

    @Override
    public void onCreate() {
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;

        // initialize logging and transfer policies
        int transferPolicy = DataTransferConfig.STORE_ONLY;
        DataStoreLogger.mContext = this.getApplicationContext();
        dataLogger = getDataLoggerForPolicy(transferPolicy);

        // initialize push sensors
        pushSensorMgr = new SenseFromAllPushSensorsTask(mContext, dataLogger);
        pushSensorMgr.subscribe();

        // initialize environment sensors
        envSensorMgr = new SenseFromAllEnvSensorsTask(mContext, dataLogger);
        envSensorMgr.subscribe();

        // initialize pull sensors
        pullSensorMgr = new SenseFromAllPullSensorsTask(mContext, dataLogger);
        pullSensorMgr.subscribe();
//        int pullTimerAfter = CommSharedPreff.loadIntSavedPreferences(CommSharedPreff.spKey_pullTimerAfter, getApplicationContext());
//        pullSensorTimer = new Timer();
//        pullSensorTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                new SenseFromAllPullSensorsTask(mContext, dataLogger).execute();
//            }
//        }, 1 * 1000);//, pullTimerAfter * 60 * 1000);


        // start service as sticky
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // unsubcribe push sensor, pull sensor and environment sensor
        if (pushSensorMgr != null) {
            pushSensorMgr.unsubscribe();
            pushSensorMgr = null;
        }

        if (envSensorMgr != null) {
            envSensorMgr.unsubscribe();
            envSensorMgr = null;
        }

        if (pullSensorMgr != null) {
            pullSensorMgr.unsubscribe();
            pullSensorMgr = null;
        }

//        if (pullSensorTimer != null) {
//            pullSensorTimer.cancel();
//            pullSensorTimer.purge();
//            pullSensorTimer = null;
//        }
    }

    // function to data logging on file system
    private AbstractDataLogger getDataLoggerForPolicy(int transferPolicy) {
        try {
            // check for transfer policy to store-only mode
            if (transferPolicy == DataTransferConfig.STORE_ONLY) {
                // create new data storeage logger
                return new DataStoreLogger(this);
            }
        } catch (ESException e) {
            return null;
        } catch (DataHandlerException e) {
            return null;
        }

        return null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();
}

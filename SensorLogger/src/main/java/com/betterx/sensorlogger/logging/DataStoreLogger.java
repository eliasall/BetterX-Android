package com.betterx.sensorlogger.logging;

import android.content.Context;
import android.os.Environment;

import com.betterx.sensorlogger.utils.CommSharedPreff;
import com.ubhave.datahandler.except.DataHandlerException;
import com.ubhave.datahandler.loggertypes.AbstractStoreOnlyLogger;
import com.ubhave.sensormanager.ESException;

// class to handle file logging info
// data to use for file logging
public class DataStoreLogger extends AbstractStoreOnlyLogger {

    public static Context mContext = null;

    public DataStoreLogger(final Context context) throws DataHandlerException, ESException {
        super(context);
        mContext = context;
    }

    @Override
    protected String getLocalStorageDirectoryName() {
        // gather the directory name where to save log
//        String filePathValue = CommSharedPreff.loadStringSavedPreferences(CommSharedPreff.spKey_filePathValue, mContext);
//        String baseDir = filePathValue;
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    @Override
    protected String getUniqueUserId() {
        /*
         * Note: Should be unique to this user, not a static string
		 */
        String uid = CommSharedPreff.loadStringSavedPreferences(CommSharedPreff.spKey_uidValue, mContext);
        return uid;
    }

    @Override
    protected String getDeviceId() {
        /*
         * Note: Should be unique to this device, not a static string
		 */
        String did = CommSharedPreff.loadStringSavedPreferences(CommSharedPreff.spKey_deviceIdValue, mContext);
        return did;
    }

    @Override
    protected boolean shouldPrintLogMessages() {
        /*
         * Turn on/off Log.d messages
		 */
        return true;
    }
}


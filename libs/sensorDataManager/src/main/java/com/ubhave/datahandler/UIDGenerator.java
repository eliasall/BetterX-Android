package com.ubhave.datahandler;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class UIDGenerator {

    public static String getUID(Context context) {
        final TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        final String simSerial = telephonyManager.getSimSerialNumber();
        //this method return IMEI or  MEID in depends on phone type: PHONE_TYPE_GSM --> return IMEI; PHONE_TYPE_CDMA --> MEID
        final String deviceId = telephonyManager.getDeviceId();
        return generateUID(deviceId, simSerial, androidId);
    }

    private static String generateUID(String... uidParts) {
        String uid = "";
        for(String part : uidParts) {
            if(part != null) {
                uid += part;
            }
        }
        return uid;
    }

}

package com.betterx.android.dataModel;

import android.util.ArrayMap;

import java.util.HashMap;
import java.util.Map;

public class RegistrationData {

    public String regId;
    public String imei;
    public String meid;
    public String simserial;
    public String androidid;

    public Map<String, String> getRegistrationRequestBody() {
        final Map<String, String> map = new HashMap<>();
        map.put("androidid", androidid);
        map.put("meid", meid);
        map.put("imei", imei);
        map.put("simserial", simserial);
        map.put("regId", regId);
        return map;
    }

}

package com.betterx.android.dataModel;

import android.content.Context;

import com.betterx.featureslogger.data.UIDGenerator;
import com.google.gson.annotations.SerializedName;

public class SetupLog {

    public String uid;
    public long timestamp;
    public String gender;
    public String age;
    public String education;
    public String timezone;
    public double latitude;
    public double longitude;
    public String city;
    public String country;
    @SerializedName("phoneusefrequency")
    public Frequency phoneUseFrequency;
    @SerializedName("webusefrequency")
    public Frequency webUseFrequency;
    @SerializedName("datatransmit_charging")
    public boolean isCharging;
    @SerializedName("datatransmit_wifi")
    public boolean isWifiConnected;
    @SerializedName("datatransmit_time")
    public TransmissionTime dataTransmitTime;

    public SetupLog(Context context, UserData userData, TransmissionSettings transmissionSettings) {
        uid = UIDGenerator.getUID(context);
        timestamp = System.currentTimeMillis();
        gender = userData.gender;
        age = userData.age;
        education = userData.education;
        timezone = userData.timezone;
        latitude = userData.latitude;
        longitude = userData.longitude;
        city = userData.city;
        country = userData.country;
        phoneUseFrequency = userData.phoneUseFrequency;
        webUseFrequency = userData.webOnPhoneUseFrequency;
        isCharging = transmissionSettings.isCharging;
        isWifiConnected = transmissionSettings.isWifiConnected;
        dataTransmitTime = transmissionSettings.transmissionTime;
    }

}

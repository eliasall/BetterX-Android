package com.betterx.android.dataModel;

import com.google.gson.annotations.SerializedName;

public enum TransmissionTime {

    @SerializedName("Anytime")
    ANYTIME(1),
    @SerializedName("Morning")
    MORNING(8),
    @SerializedName("Afternoon")
    AFTERNOON(16),
    @SerializedName("Night")
    NIGHT(1);

    private int time;

    TransmissionTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

}

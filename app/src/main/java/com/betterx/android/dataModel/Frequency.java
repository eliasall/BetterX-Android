package com.betterx.android.dataModel;

import com.google.gson.annotations.SerializedName;

public enum Frequency {

    @SerializedName("Rarely")
    RARELY,
    @SerializedName("Not so often")
    NOT_OFTEN,
    @SerializedName("Often")
    OFTEN,
    @SerializedName("Very Often")
    VERY_OFTEN,
    @SerializedName("I'm addicted")
    ADDICTED

}

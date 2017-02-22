package com.betterx.networklogger.dataModel;

import com.google.gson.annotations.SerializedName;

public enum ConnectionStatus {

    @SerializedName("Connected")
    CONNECTED,
    @SerializedName("Not_Connected")
    NOT_CONNECTED

}

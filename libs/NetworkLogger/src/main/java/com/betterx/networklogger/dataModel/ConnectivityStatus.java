package com.betterx.networklogger.dataModel;

import android.content.Context;

import com.betterx.networklogger.R;

public enum ConnectivityStatus {

    UNKNOWN(R.string.state_unknown),
    WIFI_CONNECTED(R.string.state_connecting_wifi),
    WIFI_CONNECTED_HAS_INTERNET(R.string.state_connecting_wifi_with_i),
    WIFI_CONNECTED_HAS_NO_INTERNET(R.string.state_connecting_wifi_without_i),
    MOBILE_CONNECTED(R.string.state_connecting_mobile),
    OFFLINE(R.string.state_offline);

    private final int statusId;

    ConnectivityStatus(int status) {
        this.statusId = status;
    }

    public String toString(Context context) {
        return context.getString(statusId);
    }

}

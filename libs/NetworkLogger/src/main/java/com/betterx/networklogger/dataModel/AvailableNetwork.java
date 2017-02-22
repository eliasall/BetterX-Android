package com.betterx.networklogger.dataModel;

public class AvailableNetwork {

    public String BSSID;			//  <address of the access point>
    public String SSID;			//  <network capabilities>
    public String capabilities;			// network id

    @Override
    public String toString() {
        return "AvailableNetwork{" +
                "BSSID='" + BSSID + '\'' +
                ", SSID='" + SSID + '\'' +
                ", capabilities=" + capabilities +
                '}';
    }
}

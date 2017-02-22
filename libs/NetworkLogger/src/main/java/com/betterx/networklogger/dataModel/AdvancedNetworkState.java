package com.betterx.networklogger.dataModel;

import android.net.LinkProperties;
import android.net.NetworkCapabilities;

import java.util.List;

public class AdvancedNetworkState extends NetworkState {

    public List<NetworkCapabilities> capabilities;	//		<authentication, key management, and encryption schemes supported by the access point>  See full list here
    public List<LinkProperties> linkProperties;  //  IP of DNS Servers, HTTPProxy, InterfaceName, LinkAddresses, RouteInfo.  See full list here

    public AdvancedNetworkState(NetworkState state) {
        timestamp = state.timestamp;
        wifiStatus = state.wifiStatus;	//	Connected / Not Connected
        mobileStatus = state.mobileStatus;	//		Connected  /Not Connected
        wiMaxStatus = state.wiMaxStatus;		//	Connected  /Not Connected
        hasInternet = state.hasInternet;		//	Yes / No
        detailedState = state.detailedState;	//		<get all detailed state attributes>.  See full list here
        extraInfo = state.extraInfo;		//	get extra info here
        BSSID = state.BSSID;			//  <address of the access point>
        SSID = state.SSID;			//  <network capabilities>
        frequency = state.frequency;		//  <frequency in Mhz>
        signalStrength = state.signalStrength;  //  calculatesignallevel here
        IP = state.IP;
        linkSpeed = state.linkSpeed;		// in Mbps
        MAC = state.MAC;			    // mac address
        netID = state.netID;			// network id
        RSSI = state.RSSI;			// rssi level
        availableNetworks = state.availableNetworks;
    }

    @Override
    public String toString() {
        return "AdvancedNetworkState{" +
                "capabilities=" + capabilities +
                ", linkProperties=" + linkProperties +
                '}';
    }

}

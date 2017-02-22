package com.betterx.networklogger.dataModel;

import android.net.NetworkInfo;

import java.util.List;

public class NetworkState {

    public long timestamp;		//	Timestamp in milliseconds
    public ConnectionStatus wifiStatus;		//	Connected / Not Connected
    public ConnectionStatus mobileStatus;	//		Connected  /Not Connected
    public ConnectionStatus wiMaxStatus;		//	Connected  /Not Connected
    public boolean hasInternet;		//	Yes / No
    public NetworkInfo.DetailedState detailedState;	//		<get all detailed state attributes>.  See full list here
    public String extraInfo;		//	get extra info here
//    public NetworkCapabilities capabilities;	//		<authentication, key management, and encryption schemes supported by the access point>  See full list here
//    public LinkProperties linkProperties;  //  IP of DNS Servers, HTTPProxy, InterfaceName, LinkAddresses, RouteInfo.  See full list here
    public String BSSID;			//  <address of the access point>
    public String SSID;			//  <network capabilities>
    public int frequency;		//  <frequency in Mhz>
    public int signalStrength;  //  calculatesignallevel here
    public String IP;
    public int linkSpeed;		// in Mbps
    public String MAC;			    // mac address
    public int netID;			// network id
    public int RSSI;			// rssi level
    public List<AvailableNetwork> availableNetworks;  //		<list of available networks - NetworkID, SSID, BSSID>

    @Override
    public String toString() {
        return "NetworkState{" +
                "timestamp=" + timestamp +
                ", wifiStatus=" + wifiStatus +
                ", mobileStatus=" + mobileStatus +
                ", wiMaxStatus=" + wiMaxStatus +
                ", hasInternet=" + hasInternet +
                ", detailedState=" + detailedState +
                ", extraInfo='" + extraInfo + '\'' +
                ", BSSID='" + BSSID + '\'' +
                ", SSID='" + SSID + '\'' +
                ", frequency=" + frequency +
                ", signalStrength=" + signalStrength +
                ", IP=" + IP +
                ", linkSpeed=" + linkSpeed +
                ", MAC='" + MAC + '\'' +
                ", capabilities=" + netID +
                ", RSSI=" + RSSI +
                ", availableNetworks=" + availableNetworks +
                '}';
    }

}

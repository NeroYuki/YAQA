package com.example.yaqa.model;

import android.net.wifi.p2p.WifiP2pDevice;

public class Player {
    public String name;
    public String deviceName;
    public String status;
    public WifiP2pDevice connectionInfo = null;

    public Player(String name, String deviceName, String status, WifiP2pDevice connectionInfo) {
        this.name = name;
        this.deviceName = deviceName;
        this.status = status;
        this.connectionInfo = connectionInfo;
    }
}

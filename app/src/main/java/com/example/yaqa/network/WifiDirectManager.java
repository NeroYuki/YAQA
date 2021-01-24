package com.example.yaqa.network;

import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.example.yaqa.Config;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class WifiDirectManager {

    public static List<WifiP2pDevice> peersList = new ArrayList<>();

    public static String[] deviceNameArray = {};
    public static WifiP2pDevice[] deviceArray = {};

    public static WifiP2pManager manager;
    public static WifiP2pManager.Channel channel;
    public static WifiDirectBroadcastReceiver receiver;
    public static IntentFilter intentFilter;

    public static Boolean allowOpeningSocket = false;
    public static Boolean isOwner = false;
    public static InetAddress ownerAddress = null;

    public static final WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                Log.i("[WIFIDIRECT]","You are host");
                WifiDirectManager.allowOpeningSocket = true;
                WifiDirectManager.isOwner = true;
                GeneralSocketManager.initializeCommunicationSocket();

            }
            else if (wifiP2pInfo.groupFormed) {
                Log.i("[WIFIDIRECT]","You are client");
                WifiDirectManager.allowOpeningSocket = true;
                WifiDirectManager.isOwner = false;
                WifiDirectManager.ownerAddress = groupOwnerAddress;
                GeneralSocketManager.initializeCommunicationSocket();
            }
            else {
                Log.e("[WIFIDIRECT]","Failed to create user group");
            }
        }
    };

    public static void onLostConnection() {
        Log.e("[WIFIDIRECT]","Connection lost");
        WifiDirectManager.allowOpeningSocket = false;
        Config.isMultiplayerSession = false;
        Config.currentPlayerRoom.clear();

        //TODO: disable existing connection
        GeneralSocketManager.disposeCommunicationSocket();
        MessageBroadcaster.onReceive("Socket", "Disconnected");
    }

    public static final WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {

            //((TextView) findViewById(R.id.textView2)).setText("Found " + wifiP2pDeviceList.getDeviceList().size() + " device(s)");
            if (!wifiP2pDeviceList.getDeviceList().equals(peersList)) {
                //clear the peer list and populate it with updated list

                int deviceCount = wifiP2pDeviceList.getDeviceList().size();
                deviceArray = new WifiP2pDevice[deviceCount];
                deviceNameArray = new String[deviceCount];
                int i = 0;
                for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                    deviceNameArray[i] = device.deviceName;
                    deviceArray[i] = device;
                    i++;
                }

            }
            if (wifiP2pDeviceList.getDeviceList().size() == 0) {
                Log.i("[WIFIDIRECT]","Can't find any peer");
                deviceArray = new WifiP2pDevice[0];
                deviceNameArray = new String[0];
            }
        }
    };
}


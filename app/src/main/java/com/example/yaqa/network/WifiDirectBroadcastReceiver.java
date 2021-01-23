package com.example.yaqa.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.PreferenceManager;
import com.example.yaqa.MainActivity;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Context activity;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       Context activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(activity, "Wi-fi Direct is enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Wi-fi Direct is not enabled", Toast.LENGTH_SHORT).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (!(context instanceof MainActivity)) return;
            MainActivity mainActivity = (MainActivity) activity;
            if (manager != null) {
                manager.requestPeers(channel, WifiDirectManager.peerListListener);
                mainActivity.notifyPeerChange();
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }
            NetworkInfo connectionInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (connectionInfo.isConnected()) {
                manager.requestConnectionInfo(channel, WifiDirectManager.connectionInfoListener);
            }
            else {
                WifiDirectManager.onLostConnection();
            }
            if (context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) activity;
                mainActivity.notifyConnectionChange();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}

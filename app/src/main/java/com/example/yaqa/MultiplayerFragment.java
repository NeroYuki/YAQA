package com.example.yaqa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yaqa.adapter.PlayerAdapter;
import com.example.yaqa.adapter.QuestionSetAdapter;
import com.example.yaqa.model.Player;
import com.example.yaqa.model.QuestionSet;
import com.example.yaqa.network.GeneralSocketManager;
import com.example.yaqa.network.WifiDirectManager;

import java.util.ArrayList;

public class MultiplayerFragment extends Fragment{
    private RecyclerView roomRecyclerView;
    private RecyclerView discoverRecyclerView;
    private PlayerAdapter roomAdapter;
    private ArrayList<Player> roomPlayerList;
    private PlayerAdapter discoverAdapter;
    private ArrayList<Player> discoverPlayerList;
    private CardView statusView;
    private TextView statusTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_multiplayer, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
        roomRecyclerView = (RecyclerView) getView().findViewById(R.id.room_recycler_view);
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        discoverRecyclerView = (RecyclerView) getView().findViewById(R.id.discover_recycler_view);
        discoverRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        roomPlayerList = new ArrayList<>();
        roomAdapter = new PlayerAdapter(getActivity(), roomPlayerList);
        roomRecyclerView.setAdapter(roomAdapter);
        discoverPlayerList = new ArrayList<>();
        discoverAdapter = new PlayerAdapter(getActivity(), discoverPlayerList);
        discoverRecyclerView.setAdapter(discoverAdapter);
        statusTextView = getView().findViewById(R.id.textView19);
        statusView = getView().findViewById(R.id.statusView);

        // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        createListData();
    }

    private void createListData() {
        roomPlayerList.clear();
        Player entry = new Player("You (" + PreferenceManager.getDefaultSharedPreferences(this.getContext()).getString("username", "") + ")", android.os.Build.MODEL, "Ready", null);
        roomPlayerList.add(entry);
        for (int i = 0; i < Config.currentPlayerRoom.size(); i++) {
            roomPlayerList.add(Config.currentPlayerRoom.get(i));
        }
        roomAdapter.notifyDataSetChanged();
        if (Config.isMultiplayerSession) {
            statusView.setCardBackgroundColor(getResources().getColor(R.color.allowColor));
            statusTextView.setText("Multiplayer is now available");
        }
        else {
            statusView.setCardBackgroundColor(getResources().getColor(R.color.disallowColor));
            statusTextView.setText("Multiplayer is currently not available");
        }
        //discoverAdapter.notifyDataSetChanged();
    }

    public void notifyConnectionChange() {
        roomPlayerList.clear();
        Player entry = new Player("You (" + PreferenceManager.getDefaultSharedPreferences(this.getContext()).getString("username", "") + ")", android.os.Build.MODEL, "Ready", null);
        roomPlayerList.add(entry);
        for (int i = 0; i < Config.currentPlayerRoom.size(); i++) {
            roomPlayerList.add(Config.currentPlayerRoom.get(i));
        }
        if (Config.isMultiplayerSession) {
            statusView.setCardBackgroundColor(getResources().getColor(R.color.allowColor));
            statusTextView.setText("Multiplayer is now available");
        }
        else {
            statusView.setCardBackgroundColor(getResources().getColor(R.color.disallowColor));
            statusTextView.setText("Multiplayer is currently not available");
        }
    }

    public void notifyPeerChange() {
        discoverPlayerList.clear();
        String[] bufferDeviceNameArray = WifiDirectManager.deviceNameArray;
        WifiP2pDevice[] bufferDeviceArray = WifiDirectManager.deviceArray;
        for (int i = 0; i < Math.min(bufferDeviceArray.length, bufferDeviceNameArray.length); i++) {
            discoverPlayerList.add(new Player("Player", bufferDeviceNameArray[i], "Not connected", bufferDeviceArray[i]));
        }
        discoverAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.connection_menu, menu);
    }

    public boolean checkLocationMode() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationModeOn = false;
        try {
            isLocationModeOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (!isLocationModeOn) {
            Toast.makeText(getContext(), "Location mode is not enable, please enable it in setting", Toast.LENGTH_SHORT).show();
            startActivity( new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) ;
            return false;
        }
        return true;
    }

    public void requestDisconnect() {
        GeneralSocketManager.disposeCommunicationSocket();
        WifiDirectManager.manager.removeGroup(WifiDirectManager.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "User group disbanded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(getContext(), "User group disbanding failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestDiscoverPeer() {
        WifiDirectManager.manager.discoverPeers(WifiDirectManager.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Discovering peers...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(getContext(), "Failed to discover peers " + i, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.btnDiscover:
                if (checkLocationMode()) {
                    requestDiscoverPeer();
                }
                break;
            case R.id.btnDisconnect:
                requestDisconnect();
                break;
        }
        return true;
    }


}

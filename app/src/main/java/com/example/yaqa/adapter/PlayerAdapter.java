package com.example.yaqa.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yaqa.Config;
import com.example.yaqa.EditQuestionActivity;
import com.example.yaqa.R;
import com.example.yaqa.model.Player;
import com.example.yaqa.model.QuestionSet;
import com.example.yaqa.network.WifiDirectManager;

import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerHolder> {

    private Context context;
    private ArrayList<Player> playerList;
    private RecyclerView bindedRecycler = null;

    public PlayerAdapter(Context context, ArrayList<Player> playerList) {
        this.context = context;
        this.playerList = playerList;
    }

    @NonNull
    @Override
    public PlayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.player_device_item, parent, false);
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = bindedRecycler.getChildLayoutPosition(view);
                final Player item = playerList.get(itemPosition);
                Toast.makeText(context, item.name, Toast.LENGTH_LONG).show();
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = item.connectionInfo.deviceAddress;
                WifiDirectManager.manager.connect(WifiDirectManager.channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(bindedRecycler.getContext(), "Initializing connection to " + item.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(bindedRecycler.getContext(), "Initializing connection failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        view.setOnClickListener(mOnClickListener);
        return new PlayerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerHolder holder, int position) {
        Player p = playerList.get(position);
        holder.setDetails(p);
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        bindedRecycler = recyclerView;
    }

    public class PlayerHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtDevName, txtStatus;
        public PlayerHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.textView20);
            txtDevName = itemView.findViewById(R.id.textView21);
            txtStatus = itemView.findViewById(R.id.textView22);
        }

        void setDetails(Player p) {
            txtName.setText(p.name);
            txtDevName.setText(p.deviceName);
            txtStatus.setText(p.status) ;
        }
    }
}

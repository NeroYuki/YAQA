package com.example.yaqa;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.example.yaqa.model.Result;
import com.example.yaqa.network.MessageBroadcaster;
import com.example.yaqa.network.MessageReceiver;

import java.lang.reflect.Array;
import java.util.*;

public class MultiplayerResultActivity extends AppCompatActivity implements MessageReceiver {

    ListView bindedListView = null;
    ArrayAdapter<String> adapter = null;
    ArrayList<String> displayResultList = new ArrayList<>();
    ArrayList<DisplayResult> resultList = new ArrayList<>();

    public class DisplayResult {
        public String name = "";
        public int score = 0;
        public int correct = 0;
        public int total = 0;

        public DisplayResult(String name, int score, int correct, int total) {
            this.name = name;
            this.score = score;
            this.correct = correct;
            this.total = total;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result_multi);
        MessageBroadcaster.subscribe(this);
        bindedListView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, displayResultList);
        bindedListView.setAdapter(adapter);
        refreshList();
    }

    @Override
    public void onReceive(String key, String value) {
        if (key.equals("Result")) {
            String[] component = value.split("/");
            if (component.length == 4) {
                Config.currentResult.put(component[0], new Result("", new Date(), Integer.parseInt(component[1]), Integer.parseInt(component[2]), Integer.parseInt(component[3])));
                refreshList();
            }
        }
    }

    private void refreshList() {
        resultList.clear();
        Iterator it = Config.currentResult.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Result x = (Result)(pair.getValue());
            resultList.add(new DisplayResult(pair.getKey().toString(), x.score, x.correct_count, x.total_count));
        }

        Collections.sort(resultList, new Comparator<DisplayResult>() {
            @Override
            public int compare(DisplayResult t0, DisplayResult t1) {
                return t1.score - t0.score;
            }
        });

        displayResultList.clear();
        for (DisplayResult dr : resultList) {
            displayResultList.add(String.format(dr.name + " - %d (%d / %d)", dr.score, dr.correct, dr.total));
        }
        adapter.notifyDataSetChanged();

        //check if all players submit their score
        if (displayResultList.size() == Config.currentPlayerRoom.size() + 1) {
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).name.equals(PreferenceManager.getDefaultSharedPreferences((this)).getString("username", "Unknown"))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("You are in place number " + Integer.toString(i+1));
                    builder.create().show();
                    if (i == 0) {
                        MediaPlayer.create(this, R.raw.winning).start();
                    }
                    break;
                }
            }
            TextView txtWaiting = findViewById(R.id.textView38);
            txtWaiting.setText("All score has been submitted, you can escape this screen");
        }
    }
}

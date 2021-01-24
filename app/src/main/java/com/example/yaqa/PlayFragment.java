package com.example.yaqa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.example.yaqa.database.ResultDatabase;
import com.example.yaqa.model.Result;

import java.text.SimpleDateFormat;

public class PlayFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
        int resultCount = ResultDatabase.getResultCount();
        int resultHighest = ResultDatabase.getResultHighest();
        int resultTotalCorrect = ResultDatabase.getTotalCorrect();
        TextView txtCount = getView().findViewById(R.id.textView34);
        TextView txtHighest = getView().findViewById(R.id.textView35);
        TextView txtTotalCorrect = getView().findViewById(R.id.textView36);
        TextView txtWelcome = getView().findViewById(R.id.textView31);
        TextView txtRecent = getView().findViewById(R.id.textView40);

        txtCount.setText(Integer.toString(resultCount));
        txtHighest.setText(Integer.toString(resultHighest));
        txtTotalCorrect.setText(Integer.toString(resultTotalCorrect));
        txtWelcome.setText("Welcome back, " + PreferenceManager.getDefaultSharedPreferences(getContext()).getString("username", "Player"));

        Result res = ResultDatabase.getMostRecentResult();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (res != null) {
            txtRecent.setText(String.format(sdf.format(res.playTime) + " - %d (%d / %d)", res.score, res.correct_count, res.total_count));
        }
    }
}

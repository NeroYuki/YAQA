package com.example.yaqa;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yaqa.database.ResultDatabase;
import com.example.yaqa.model.Result;

import java.util.Date;
import java.util.UUID;

public class ResultActivity extends AppCompatActivity {

    MediaPlayer md = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int score = getIntent().getIntExtra("com.example.yaqa.score", 0);
        int correct_count = getIntent().getIntExtra("com.example.yaqa.correct", 0);
        int total_count = getIntent().getIntExtra("com.example.yaqa.total", 0);
        boolean isWin = getIntent().getBooleanExtra("com.example.yaqa.isWin", true);

        if (!isWin) {
            TextView resultText = findViewById(R.id.textView);
            ImageView resultImage = findViewById(R.id.imageView7);

            resultText.setText("Game Over");
            resultImage.setImageResource(R.drawable.ic_sadface_dark);

            md = MediaPlayer.create(this, R.raw.losing);
            md.start();
        }
        else {
            md = MediaPlayer.create(this, R.raw.winning);
            md.start();
        }

        TextView txtScore = findViewById(R.id.textView24);
        TextView txtCount = findViewById(R.id.textView25);

        txtScore.setText(String.format("You scored a total of %d points", score));
        txtCount.setText(String.format("With %d/%d correct answers", correct_count, total_count));

        System.out.println(new Date().toString());
        ResultDatabase.addNewResult(new Result(UUID.randomUUID().toString(), new Date(), score, correct_count, total_count));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (md != null) {
            md.release();
            md = null;
        }
    }
}

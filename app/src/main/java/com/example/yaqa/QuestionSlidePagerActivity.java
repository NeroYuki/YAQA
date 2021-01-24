package com.example.yaqa;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.example.yaqa.custom.CustomViewPager;
import com.example.yaqa.database.QuestionSetMetadataDatabase;
import com.example.yaqa.helper.FileHelper;
import com.example.yaqa.helper.JSONDataHelper;
import com.example.yaqa.helper.UtilsHelper;
import com.example.yaqa.model.Question;
import com.example.yaqa.model.QuestionSet;
import com.example.yaqa.model.Result;
import com.example.yaqa.model.Session;
import com.example.yaqa.network.AsyncMessageSendingTask;
import com.example.yaqa.network.MessageBroadcaster;
import com.example.yaqa.network.MessageReceiver;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class QuestionSlidePagerActivity extends FragmentActivity implements MessageReceiver {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static int num_page = 5;
    private static boolean isBackTraceable = false;
    private int max_lives = 1;
    private boolean free_mode = false;
    private Session currentSession = null;
    private QuestionSlidePagerActivity self = null;
    private int timeLimit = 0;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private CustomViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;

    TextView timerTextView;
    long endTime = 0;
    MediaPlayer md = null;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = endTime - System.currentTimeMillis();

            if (millis < 0) {
                onQuestionNotify(0); return;
            }

            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MessageBroadcaster.subscribe(this);
        self = this;
        currentSession = new Session();
        currentSession.setRemainingLife(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("lives_count", "1")));
        timeLimit = UtilsHelper.parseTime(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("time_limit", "01:00")) * 1000;
        int questionLimit = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("max_question_count", "10"));
        boolean allowShuffle = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("allow_shuffle", false);

        Set<String> selectedSetUUID = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getStringSet("selected_sets", null);

        if (this.getIntent().getBooleanExtra("com.example.yaqa.isMultiplayer", false)) {
            currentSession.initializeMultiplayerSession(Config.currentPlayerRoom);
            Config.currentResult.clear();
        }

        max_lives = currentSession.getRemainingLife();
        if (max_lives == 0) free_mode = true;
        try {
            //TODO: change this to take in different set
            for (String uuid : selectedSetUUID) {
                System.out.println("UUID: " + uuid);
                QuestionSet entry_metadata = QuestionSetMetadataDatabase.getQuestionSetByUUID(uuid);
                QuestionSet entry = JSONDataHelper.questionParsing(FileHelper.readFromFile(Config.getCorePath() + entry_metadata.file_path + "data.json"));
                currentSession.addAllQuestion(entry.questions);
                currentSession.shuffleQuestion();
                currentSession.setQuestion_count(Math.min(currentSession.getQuestion_count(), questionLimit));
            }
            num_page = currentSession.getQuestion_count();
        }
        catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        setContentView(R.layout.activity_game);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (CustomViewPager) findViewById(R.id.question_viewPager);
        pagerAdapter = new ScreenSlidePagerAdapter( getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        timerTextView = findViewById(R.id.textView12);
        resetTimer();
        TextView txtLives = (TextView) findViewById(R.id.textView10);
        txtLives.setText(String.format("%d/%d", currentSession.getRemainingLife(), max_lives));
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(num_page);
        progressBar.setProgress(0);
    }

    public void resetTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        endTime = System.currentTimeMillis() + timeLimit;
        timerHandler.postDelayed(timerRunnable, 0);
    }


    public void onQuestionNotify(int result) {
        if (result == 1) {
            currentSession.setScore(currentSession.getScore() + 100);
            currentSession.setCorrect_answer(currentSession.getCorrect_answer() + 1);
            md = MediaPlayer.create(this, R.raw.correct);
        }
        else {
            currentSession.setScore(currentSession.getScore() - 10);
            if (!free_mode) currentSession.setRemainingLife(currentSession.getRemainingLife() - 1);
            md = MediaPlayer.create(this, R.raw.wrong);
        }
        md.start();
        resetTimer();
        //System.out.println(currentSession.getScore());
        TextView txtScore = (TextView) findViewById(R.id.textView8);
        txtScore.setText(String.valueOf(currentSession.getScore()));

        TextView txtLives = (TextView) findViewById(R.id.textView10);
        txtLives.setText(String.format("%d/%d", currentSession.getRemainingLife(), max_lives));

        txtLives.setText(String.format("%d/%d", currentSession.getRemainingLife(), max_lives));
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(progressBar.getProgress() + 1);

        if (currentSession.getRemainingLife() <= 0 && !free_mode) {
            timerHandler.removeCallbacks(timerRunnable);
            Toast.makeText(getApplicationContext(), "Game is over, you lose :(", Toast.LENGTH_SHORT).show();
            //Call intent for result screen
            if (currentSession.isMultiplayer) {
                Intent intent = new Intent(this, MultiplayerResultActivity.class);
                String username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "Unknown");
                Config.currentResult.put(username, new Result("", new Date(), currentSession.getScore(), currentSession.getCorrect_answer(), currentSession.getQuestion_count()));
                new AsyncMessageSendingTask().execute(String.format("Result : " + username + "-%d-%d-%d", currentSession.getScore(), currentSession.getCorrect_answer(), currentSession.getQuestion_count()));
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("com.example.yaqa.score", currentSession.getScore());
                intent.putExtra("com.example.yaqa.correct", currentSession.getCorrect_answer());
                intent.putExtra("com.example.yaqa.total", currentSession.getQuestion_count());
                intent.putExtra("com.example.yaqa.isWin", false);
                startActivity(intent);
            }
            this.finish();
            return;
        }

        if (mPager.getCurrentItem() < mPager.getAdapter().getCount() - 1)
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        else {
            timerHandler.removeCallbacks(timerRunnable);
            Toast.makeText(getApplicationContext(), "Game is over", Toast.LENGTH_SHORT).show();
            //Call intent for result screen
            if (currentSession.isMultiplayer) {
                Intent intent = new Intent(this, MultiplayerResultActivity.class);
                String username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", "Unknown");
                Config.currentResult.put(username, new Result("", new Date(), currentSession.getScore(), currentSession.getCorrect_answer(), currentSession.getQuestion_count()));
                new AsyncMessageSendingTask().execute(String.format("Result : " + username + "/%d/%d/%d", currentSession.getScore(), currentSession.getCorrect_answer(), currentSession.getQuestion_count()));
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("com.example.yaqa.score", currentSession.getScore());
                intent.putExtra("com.example.yaqa.correct", currentSession.getCorrect_answer());
                intent.putExtra("com.example.yaqa.total", currentSession.getQuestion_count());
                intent.putExtra("com.example.yaqa.isWin", true);
                startActivity(intent);
            }
            this.finish();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0 || !isBackTraceable) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    timerHandler.removeCallbacks(timerRunnable);
                    QuestionSlidePagerActivity.super.onBackPressed();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.setTitle("Quit?").setMessage("Want to quit? your result will not be counted if you quit now.");
            builder.create().show();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (md != null) {
            md.release();
            md = null;
        }
    }

    @Override
    public void onReceive(String key, String value) {
        if (key.equals("Result")) {
            String[] component = value.split("/");
            if (component.length == 4) {
                Config.currentResult.put(component[0], new Result("", new Date(), Integer.parseInt(component[1]), Integer.parseInt(component[2]), Integer.parseInt(component[3])));
            }
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<QuestionPageFragment> pageList;
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            //int a = 4;
            pageList = new ArrayList<>(num_page);
            for (int i = 0; i < num_page; i++) {
                QuestionPageFragment x = new QuestionPageFragment(currentSession.getQuestion(i));
                x.setParent(self);
                pageList.add(x);
            }
        }

        @Override
        public Fragment getItem(int position) {
            return pageList.get(position);
        }

        @Override
        public int getCount() {
            return num_page;
        }
    }

}

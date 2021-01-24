package com.example.yaqa;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.MenuItem;
import android.view.SurfaceControl;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import com.example.yaqa.database.AppDbHelper;
import com.example.yaqa.database.QuestionSetMetadataDatabase;
import com.example.yaqa.database.ResultDatabase;
import com.example.yaqa.helper.FileHelper;
import com.example.yaqa.helper.JSONDataHelper;
import com.example.yaqa.model.Player;
import com.example.yaqa.model.QuestionSet;
import com.example.yaqa.network.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.content.PermissionChecker;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements MessageReceiver {

    private MediaPlayer md = null;
    AppDbHelper dbHelper = null;
    private Fragment selectedFragment = null;
    private Set<String> receivedSelectedSet = new HashSet<>();
    private boolean isPeerReady = false;
    private AlertDialog readyPrompt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new AppDbHelper(getApplicationContext());
        QuestionSetMetadataDatabase.setDbHelper(dbHelper);
        ResultDatabase.setDbHelper(dbHelper);

        if (checkStoragePermission()) {
            boolean result = initialGameDirectory();
        }
        else System.out.println("Error on requesting permission, waiting for user granting permission");
        if (checkLocationPermission())
            Config.isGrantedLocationPerm = true;
        else Config.isGrantedLocationPerm = false;
        setupWifiDirect();

        setContentView(R.layout.activity_main);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher_drawable_small);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Waiting for host player to start the game");
        readyPrompt = builder.create();
        MessageBroadcaster.subscribe(this);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navItemListener);
        bottomNav.setSelectedItemId(R.id.nav_play);


        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlayFragment()).commit();
    }


    private boolean initialGameDirectory() {
        File dir = new File(Config.getCorePath());
        // Creating directory if it doesn't exist
        if (!dir.exists()) {
            System.out.println("Directory not exist, creating");
            if (!dir.mkdirs()) {
                System.out.println("Directory creation failed");
                return false;
            }

            final File nomedia = new File(dir.getAbsoluteFile(), ".nomedia");
            try {
                nomedia.createNewFile();
                //return true;
            } catch (final IOException e) {
                System.out.println("LibraryManager: " + e.getMessage());
                return false;
            }

            //add example
            File example_dir = new File(Config.getCorePath() + "/Example_set/");
            example_dir.mkdirs();
            try {
                String sample_data = FileHelper.readFromInputStream(getApplicationContext().getAssets().open("ExampleSet.json"));
                FileHelper.writeToFile(example_dir.getAbsolutePath() + "/data.json", sample_data);
                QuestionSetMetadataDatabase.deleteAllSet();
                QuestionSetMetadataDatabase.addInitialData();

                Toast.makeText(getApplicationContext(), "Created example files", Toast.LENGTH_SHORT).show();
                return true;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 99;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 98;

    public boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_storage_permission)
                        .setMessage(R.string.text_storage_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_STORAGE);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public void notifyPeerChange() {
        if (!(selectedFragment instanceof MultiplayerFragment)) return;
        ((MultiplayerFragment)selectedFragment).notifyPeerChange();
    }

    public void notifyConnectionChange() {
//        if (!(selectedFragment instanceof MultiplayerFragment)) return;
//        ((MultiplayerFragment)selectedFragment).notifyConnectionChange();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Config.isGrantedLocationPerm = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Location permission is not granted, app will not function as intended", Toast.LENGTH_LONG).show();
                    Config.isGrantedLocationPerm = false;
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialGameDirectory();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Storage permission is not granted, app will not function as intended", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void onPlayButtonClicked(View view) {

        md = MediaPlayer.create(this, R.raw.correct);
        md.start();
        if (Config.isMultiplayerSession) {
            if (!WifiDirectManager.isOwner) {

                Set<String> currentSelectedSet = PreferenceManager.getDefaultSharedPreferences(this).getStringSet("selected_sets", null);
                new AsyncMessageSendingTask().execute("Selected Set : Start");
                for (String entry : currentSelectedSet) {
                    new AsyncMessageSendingTask().execute("Selected Set : " + entry);
                }
                new AsyncMessageSendingTask().execute("Selected Set : End");

                readyPrompt.show();
            }
            else {
                if (isPeerReady) {
                    new AsyncMessageSendingTask().execute("Session : Game Start");
                    Intent intent = new Intent(this, QuestionSlidePagerActivity.class);
                    //intent.putExtra("Options", SharedPreferences.class);
                    intent.putExtra("com.example.yaqa.isMultiplayer", true);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "Other players is not ready, please wait", Toast.LENGTH_LONG).show();
                }
            }
        }
        else {
            Intent intent = new Intent(this, QuestionSlidePagerActivity.class);
            //intent.putExtra("Options", SharedPreferences.class);
            intent.putExtra("com.example.yaqa.isMultiplayer", false);
            startActivity(intent);

        }
    }

    public void onAddButtonClicked(View view) {
        Intent intent = new Intent(this, EditQuestionActivity.class);
        startActivity(intent);
    }

    private void setupWifiDirect() {
        WifiDirectManager.manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        WifiDirectManager.channel = WifiDirectManager.manager.initialize(this, getMainLooper(), null);
        WifiDirectManager.receiver = new WifiDirectBroadcastReceiver(WifiDirectManager.manager, WifiDirectManager.channel, this);

        WifiDirectManager.intentFilter = new IntentFilter();
        WifiDirectManager.intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        WifiDirectManager.intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        WifiDirectManager.intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        WifiDirectManager.intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (md != null) {
            md.release();
            md = null;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navItemListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_questions:
                            selectedFragment = new QuestionsFragment(QuestionSetMetadataDatabase.getAllSetMetadata());
                            break;
                        case R.id.nav_gamerules:
                            selectedFragment = new GamerulesFragment();
                            break;
                        case R.id.nav_play:
                            selectedFragment = new PlayFragment();
                            break;
                        case R.id.nav_multiplayer:
                            selectedFragment = new MultiplayerFragment();
                            break;
                        case R.id.nav_setting:
                            selectedFragment = new SettingsFragment();
                            break;
                    }

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    transaction.replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(WifiDirectManager.receiver, WifiDirectManager.intentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(WifiDirectManager.receiver);
    }


    @Override
    public void onReceive(String key, String value) {
        if (value.equals("Handshaking complete")) {
            Config.isMultiplayerSession = true;
            Config.currentPlayerRoom.clear();
            new AsyncMessageSendingTask().execute("username : "+ PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("username", "Unknown"));
        }

        if (key.equals("Selected Set")) {
            if (value.equals("End")) {
                Set<String> currentSelectedSet = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getStringSet("selected_sets", null);
                if (receivedSelectedSet.equals(currentSelectedSet)) {
                    Toast.makeText(this, "Sets are matched, you can start the game", Toast.LENGTH_LONG).show();
                    isPeerReady = true;
                }
            }
            if (value.equals("Start")) {
                receivedSelectedSet.clear();
            }
            else {
                receivedSelectedSet.add(value);
            }
        }

        if (key.equals("username")) {
            Config.currentPlayerRoom.add(new Player(value, "", "Not Ready", null));
            if (selectedFragment instanceof MultiplayerFragment) {
                ((MultiplayerFragment) selectedFragment).notifyConnectionChange();
            }
        }
        if (key.equals("Socket") && value.equals("Disconnected")) {
            if (selectedFragment instanceof MultiplayerFragment) {
                ((MultiplayerFragment) selectedFragment).notifyConnectionChange();
            }
        }

        if (key.equals("Session") && value.equals("Game Start")) {
            Intent intent = new Intent(this, QuestionSlidePagerActivity.class);
            //intent.putExtra("Options", SharedPreferences.class);
            intent.putExtra("com.example.yaqa.isMultiplayer", true);
            if (readyPrompt != null) readyPrompt.hide();
            startActivity(intent);
        }
    }
}

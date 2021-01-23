package com.example.yaqa;
import android.os.Environment;
import com.example.yaqa.model.Player;
import com.example.yaqa.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static String corePath = Environment.getExternalStorageDirectory() + "/YAQAFiles/";
    public static boolean isGrantedLocationPerm = false;
    public static boolean isMultiplayerSession = false;

    public static ArrayList<Player> currentPlayerRoom = new ArrayList<>();
    public static HashMap<String, Result> currentResult = new HashMap<>();

    public static String getCorePath() {
        return corePath;
    }

    public static void setCorePath(String s) {
    }
}

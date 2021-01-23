package com.example.yaqa.network;

import android.app.Activity;
import com.example.yaqa.MainActivity;

import java.util.ArrayList;

public class MessageDispatcher implements Runnable {
    private String key = "";
    private String value = "";

    public MessageDispatcher(String input) {
        System.out.println(input);
        String[] component = input.split(" : ");
        if (component.length == 2) {
            key = component[0];
            value = component[1];
        }
    }

    // Print message on screen
    @Override
    public void run() {
        MessageBroadcaster.onReceive(key, value);
    }
}
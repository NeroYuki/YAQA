package com.example.yaqa.network;

import java.util.ArrayList;

public class MessageBroadcaster {
    public static ArrayList<MessageReceiver> receiverList = new ArrayList<>();

    public static void onReceive(String key, String value) {
        for (MessageReceiver i: receiverList) {
            i.onReceive(key, value);
        }
    }

    public static void subscribe(MessageReceiver mr) {
        receiverList.add(mr);
    }
}

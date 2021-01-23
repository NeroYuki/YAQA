package com.example.yaqa.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class AsyncMessageSendingTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... param) {
        if (GeneralSocketManager.getSocket() == null || !GeneralSocketManager.getSocket().isConnected()) {
            Log.e("[MessageSendingTask]", "Socket is not available");
            return null;
        }
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(GeneralSocketManager.getSocket().getOutputStream())), true);
            out.println(param[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}

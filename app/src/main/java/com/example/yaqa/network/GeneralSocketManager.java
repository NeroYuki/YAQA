package com.example.yaqa.network;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class GeneralSocketManager {
    private static ServerSocket serverSocket = null;
    private static Socket socket = null;
    private static Handler messageHandler;
    private static ServerThread serverThread = null;
    private static ClientThread clientThread = null;
    private static CommunicationThread comThread = null;

    public static Socket getSocket() {return socket;}

    public static boolean initializeCommunicationSocket() {
        if (WifiDirectManager.allowOpeningSocket) {
            if (WifiDirectManager.isOwner) {
                serverThread = new ServerThread();
                serverThread.start();
            }
            else {
                clientThread = new ClientThread();
                clientThread.start();
            }
            messageHandler = new Handler();
        }
        else {
            Log.e("[Socket]","Opening socket is not allowed");
            return false;
        }
        return true;
    }

    public static boolean disposeCommunicationSocket() {
        try {
            if (comThread != null && comThread.isAlive()) {
                comThread.interrupt();
                comThread.join();
                comThread = null;
            }
            if (serverThread != null && serverThread.isAlive()) {
                serverThread.join();
                serverThread = null;
            }
            if (clientThread != null && clientThread.isAlive()) {
                clientThread.interrupt();
                clientThread.join();
                clientThread = null;
            }
            if (socket != null) {
                if (socket.isConnected())
                    socket.close();
                socket = null;
            }

            if (serverSocket != null) {
                if (!serverSocket.isClosed())
                    serverSocket.close();
                serverSocket = null;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    //server host class
    static class ServerThread extends Thread {
        @Override
        public void run() {
            //Socket socket = null;
            try {
                serverSocket = new ServerSocket(8887);
            } catch (IOException e) {
                e.printStackTrace();
            }
            boolean waitingForConnection = true;
            while (waitingForConnection) {
                try {
                    // Start listening for messages
                    socket = serverSocket.accept();
                    waitingForConnection = false;
                    Log.i("Server thread", "Connection accepted");
                    comThread = new CommunicationThread(socket);
                    comThread.start();
                } catch (SocketException se) {
                    se.printStackTrace();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    break;
                }

            }
        }
    }

    //server client class

    static class ClientThread extends Thread {
        @Override
        public void run() {
            try {
                InetAddress serverAddress = WifiDirectManager.ownerAddress;
                socket = new Socket(serverAddress, 8887);
                Log.i("Client thread", "Connection requested accepted");
                CommunicationThread commThread = new CommunicationThread(socket);
                commThread.start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //communication thread

    static class CommunicationThread extends Thread {
        private Socket clientSocket;
        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                // read received data
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                AsyncMessageSendingTask task = new AsyncMessageSendingTask();
                task.execute("Socket : Handshaking complete");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    String read = input.readLine();
//                    conversation.add(read);
//                    adapter.notifyDataSetChanged();
                    if (read != null) {
                        //TODO: Dispatch message
                        messageHandler.post(new MessageDispatcher(read));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("Communication Thread is closing");
        }
    }

}

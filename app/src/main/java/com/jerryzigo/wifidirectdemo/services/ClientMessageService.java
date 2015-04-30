package com.jerryzigo.wifidirectdemo.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by jerry on 2015-04-24.
 */
public class ClientMessageService extends IntentService {

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    public static final String EXTRAS_CLIENT_IP_ADDRESS = "client_ip_address";

    private static final String TAG = "ClientMessageService";

    public ClientMessageService() {
        super("ClientMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "onHandleIntent");
        Context context = getApplicationContext();
        String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
        int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
        Socket socket = new Socket();

        try {
            Log.d(TAG, "Opening client socket - ");
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
            Log.d(TAG, "Client socket - " + socket.isConnected());

            String messageToSend = intent.getExtras().getString(EXTRAS_CLIENT_IP_ADDRESS);

            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = null;
            try {
                outputStream.write(messageToSend.getBytes());
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            Log.d(TAG, "Client: Data written");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // Give up
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

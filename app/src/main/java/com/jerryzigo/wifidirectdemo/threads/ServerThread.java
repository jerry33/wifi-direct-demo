package com.jerryzigo.wifidirectdemo.threads;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.jerryzigo.wifidirectdemo.listeners.MessageListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jerry on 2015-04-30.
 */
public class ServerThread implements Runnable {

    private static final String TAG = "ServerThread";
    private static final int PORT = 8888;
    private Handler handler = new Handler();
    private MessageListener mMessageListener;

    public ServerThread(MessageListener messageListener) {
        mMessageListener = messageListener;
    }

    @Override
    public void run() {

        try {

            ServerSocket serverSocket = new ServerSocket(8888);

            while (true) {
                Socket client = serverSocket.accept();
                InputStream inputstream = client.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    Log.d(TAG, line);
                    mMessageListener.onMessageReceived(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

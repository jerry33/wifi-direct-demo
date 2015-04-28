package com.jerryzigo.wifidirectdemo.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jerry on 2015-04-24.
 */
public class ServerMessageAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "ServerMessageAsyncTask";
    private Context mContext;

    public ServerMessageAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Log.d(TAG, "doInBackground, SERVER SIDE ON");

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();
            /**
             * If this code is reached, a client has connected and transferred data
             * Display the input stream from the client
             */
            InputStream inputstream = client.getInputStream();
//            Toast.makeText(mContext, "doInBackground", Toast.LENGTH_SHORT).show();
//            serverSocket.close();
            return inputstream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        Log.d(TAG, "onPostExecute = " + s);
        Toast.makeText(mContext, "onPostExecute = " + s, Toast.LENGTH_SHORT).show();
    }
}

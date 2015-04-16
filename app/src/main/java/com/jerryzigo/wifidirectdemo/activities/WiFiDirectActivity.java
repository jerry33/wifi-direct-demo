package com.jerryzigo.wifidirectdemo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jerryzigo.wifidirectdemo.R;
import com.jerryzigo.wifidirectdemo.adapters.WiFiPeerListAdapter;
import com.jerryzigo.wifidirectdemo.receivers.WiFiDirectBroadcastReceiver;
import com.jerryzigo.wifidirectdemo.abs.BaseActivity;

import java.util.List;


public class WiFiDirectActivity extends BaseActivity implements WifiP2pManager.ChannelListener, WifiP2pManager.PeerListListener {

    public static final String TAG = "MainActivity";
    private WifiP2pManager mManager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private IntentFilter intentFilter;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;

    private Button mRequestPeersButton;
    private ListView mPeersListView;
    private List<WifiP2pDevice> peersList;
    private WiFiPeerListAdapter mPeerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mRequestPeersButton = (Button) findViewById(R.id.request_peers_button);
        mPeersListView = (ListView) findViewById(android.R.id.list);
        mPeerListAdapter = new WiFiPeerListAdapter(this);
        mPeersListView.setAdapter(mPeerListAdapter);
        mPeersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d(TAG, "onItemClicked = " + mPeerListAdapter.getItem(position));
                WifiP2pDevice device = (WifiP2pDevice)mPeerListAdapter.getItem(position);
                Log.d(TAG, "onItemClicked = " + device.deviceName);
            }
        });

        mRequestPeersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "discovery process successful");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d(TAG, "discovery process unsuccessful");
                    }
                });
            }
        });

    }

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public void onChannelDisconnected() {

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        Log.d(TAG, String.valueOf(peers.getDeviceList().size()));
        Log.d(TAG, "onPeersAvailable");
        mPeerListAdapter.addAllPeers(peers);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

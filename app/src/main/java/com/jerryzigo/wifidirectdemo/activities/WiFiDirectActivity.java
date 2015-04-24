package com.jerryzigo.wifidirectdemo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jerryzigo.wifidirectdemo.R;
import com.jerryzigo.wifidirectdemo.adapters.WiFiPeerListAdapter;
import com.jerryzigo.wifidirectdemo.receivers.WiFiDirectBroadcastReceiver;
import com.jerryzigo.wifidirectdemo.abs.BaseActivity;
import com.jerryzigo.wifidirectdemo.services.ClientMessageService;
import com.jerryzigo.wifidirectdemo.tasks.ServerMessageAsyncTask;

import java.util.List;


public class WiFiDirectActivity extends BaseActivity implements WifiP2pManager.ChannelListener, WifiP2pManager.PeerListListener, View.OnClickListener, WifiP2pManager.ConnectionInfoListener {

    public static final String TAG = "WiFiDirectActivity";
    private WifiP2pManager mManager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private IntentFilter intentFilter;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pInfo mInfo;
    private BroadcastReceiver mReceiver;

    private Button mRequestPeersButton, mSendMessageButton;
    private TextView isGroupOwnerTextView;
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
        mRequestPeersButton.setOnClickListener(this);
        mSendMessageButton = (Button) findViewById(R.id.send_message_button);
        mSendMessageButton.setOnClickListener(this);
        isGroupOwnerTextView = (TextView) findViewById(R.id.is_group_owner);

        mPeersListView = (ListView) findViewById(android.R.id.list);
        mPeerListAdapter = new WiFiPeerListAdapter(this);
        mPeersListView.setAdapter(mPeerListAdapter);
        mPeersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final WifiP2pDevice device = (WifiP2pDevice)mPeerListAdapter.getItem(position);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
//                        Toast.makeText(WiFiDirectActivity.this, "success", Toast.LENGTH_SHORT).show();
//                        new ServerMessageAsyncTask(WiFiDirectActivity.this).execute();
                        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
                        String myIpAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                        toast("My address is: " + myIpAddress);
                        toast("Opposite address is: " + device.deviceAddress);
                        mManager.requestConnectionInfo(mChannel, WiFiDirectActivity.this);
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(WiFiDirectActivity.this, "failure", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.request_peers_button:
                new ServerMessageAsyncTask(WiFiDirectActivity.this).execute();
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
                break;
            case R.id.send_message_button:
                Log.d(TAG, "sendMessageButton clicked");
//                mManager.requestConnectionInfo(mChannel, this);
                Intent serviceIntent = new Intent(this, ClientMessageService.class);
                serviceIntent.putExtra(ClientMessageService.EXTRAS_GROUP_OWNER_ADDRESS,
                        mInfo.groupOwnerAddress.getHostAddress()); // mInfo.groupOwnerAddress.getHostAddress();
                Log.d(TAG, "mInfo.groupOwnerAddress.getHostAddress() = " + mInfo.groupOwnerAddress.getHostAddress());
                serviceIntent.putExtra(ClientMessageService.EXTRAS_GROUP_OWNER_PORT, 8888);
                Log.d(TAG, "service about to start");
                startService(serviceIntent);
                break;
        }

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.d(TAG, "onConnectionInfoAvailable");
        mInfo = info;
        Log.d(TAG, "info = " + info.toString());
        toast("onConnectionInfoAvailable");
        if (info != null) {
            Log.d(TAG, info.toString());
            if (info.isGroupOwner) {
                new ServerMessageAsyncTask(WiFiDirectActivity.this).execute();
//                isGroupOwnerTextView.setText("Group owner: true");
//                Log.d(TAG, "address = " + info.groupOwnerAddress.getCanonicalHostName());
            } else if (info.groupOwnerAddress != null) {
                Log.d(TAG, "info.groupOwnerAddress = " + info.groupOwnerAddress);
            }
        }
    }
}

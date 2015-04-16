package com.jerryzigo.wifidirectdemo.adapters;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jerryzigo.wifidirectdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry on 2015-04-16.
 */
public class WiFiPeerListAdapter extends BaseAdapter {

    private static final String TAG = "WiFiPeerListAdapter";
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List mPeersList = new ArrayList<WifiP2pDevice>();

    public WiFiPeerListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mPeersList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPeersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        WifiP2pDevice device = (WifiP2pDevice)mPeersList.get(position);
        Log.d(TAG, device.deviceName);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_device_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.device_name);
            viewHolder.details = (TextView) convertView.findViewById(R.id.device_details);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(device.deviceName);
        viewHolder.details.setText(getDeviceStatus(device.status));

        return convertView;
    }

    public void addAllPeers(WifiP2pDeviceList peersList) {
        clearPeers();
        Log.d(TAG, "peers have been removed");
        for (WifiP2pDevice device : peersList.getDeviceList()) {
            mPeersList.add(device);
        }
        notifyDataSetChanged();
    }

    public void clearPeers() {
        mPeersList.clear();
        notifyDataSetChanged();
    }

    public String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }

    private static class ViewHolder {
        TextView name;
        TextView details;
    }

}

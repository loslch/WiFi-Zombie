package com.wifi_zombie;

import java.util.Observable;

import com.data.WifiInfoData;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Message;

public class WifiInfoManager extends AbstractService{
	
	private static WifiInfoManager instance = null;
	// WifiManager variable
	private	WifiManager wifimanager;
	// Wifi Data
	private WifiInfoData wifiInfiData;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				getWIFIScanResult(); // get WIFISCanResult
				wifimanager.startScan(); // for refresh
			} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {	
				sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
			}
		}
	};
	
	public WifiInfoManager()
	{
		wifiInfiData = new WifiInfoData();
	}
	
	public static WifiInfoManager getInstance()
	{
		if(instance == null)
			instance = new WifiInfoManager();
		return instance;
	}
	
	public void getWIFIScanResult() {

		wifiInfiData.setWifiInfoData(wifimanager.getScanResults()); // ScanResult

	}
	@Override 
    public void onStartService() {
    }
    
   @Override
    public void onStopService() {
    }   

    @Override
    public void onReceiveMessage(Message msg) {
    }
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}

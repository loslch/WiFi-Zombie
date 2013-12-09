package com.wifi_zombie;

import com.data.WifiInfoData;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class WifiService extends Service {
	public static final int MSG_REGISTER_CLIENT = 9991;
	public static final int MSG_UNREGISTER_CLIENT = 9992;
	public static final int MSG_UPDATE_INFO = 9993;
	public static final int MSG_REFRESH = 9994;
	public static final int MSG_INTERVAL_CHANED = 9995;
	
	// Interval var default 10sec
	private int interval = 5000;	// millisec 단위
	private boolean isInterval = false;
	private boolean isRefresh = false;
	// WifiManager variable
	private WifiManager wifimanager = null;
	// Wifi Data
	private WifiInfoData wifiInfoData;
	private WifiInfo info = null;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			Log.i("wifi zombie", "service sucess");
			final String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) && (isInterval || isRefresh)) {
				getWIFIScanResult(); // get WIFISCanResult
				isInterval = false;
				isRefresh = false;
				

			} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) && (isInterval || isRefresh)) {
				sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
			}
		}
	};
	private Handler intervalHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			isInterval = true;
			wifimanager.startScan(); // for refresh
			intervalHandler.sendEmptyMessageDelayed(interval, interval);
		}
	};
	private Messenger FromActivityMessenger = new Messenger(new Handler() { // activity에서  보낸 메세지 처리 handler포함하는 messenger
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
//					Log.i("wifi zombie", "messenger resister??");
					switch (msg.what) {
					case MSG_REGISTER_CLIENT:
						ToActivityMessenger = msg.replyTo;
						wifimanager.startScan();
						interval = msg.arg1;
						intervalHandler.sendEmptyMessage(interval);
						break;
					case MSG_REFRESH:
						isRefresh = true;
						wifimanager.startScan(); // for refresh
						break;
					}
				}
			});
	private Messenger ToActivityMessenger;// activity로 보낼 메세지 처리 handler포함하는
											// messenger

	@Override
	public void onCreate() {
		super.onCreate();
		// TODO Auto-generated method stub

		wifiInfoData = WifiInfoData.getInstance();
		wifimanager = (WifiManager) getSystemService(WIFI_SERVICE);
		info = wifimanager.getConnectionInfo();
		// Setup WIFI
		IntentFilter filter = new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(mReceiver, filter);
		
//		if(info == null)
//			Log.i("wifi zombie", "service create failed");
//		Log.i("wifi zombie", "service create");

	}

	// public WifiService(Handler main)
	// {
	// mActivity = main;
	// }

	public void getWIFIScanResult() {
		// Log.i("wifi zombie", "getWIFIScanResult()");
		wifiInfoData.setWifiInfoData(wifimanager.getScanResults(),
				info.getSSID(), info.getBSSID()); // ScanResult

		// main activity로 정보 변경 사실을 보냄!
		try {
			// if(ToActivityMessenger == null)
			// Log.i("wifi zombie", "ToActivityMessenger == null");
			ToActivityMessenger.send(Message
					.obtain(null, MSG_UPDATE_INFO, 0, 0));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return FromActivityMessenger.getBinder();
	}
	
	class IntervalThread extends Thread
	{
		
	}

}

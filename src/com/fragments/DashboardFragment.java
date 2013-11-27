package com.fragments;

import com.data.WifiDataItem;
import com.wifi_zombie.R;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DashboardFragment extends MyFragment{
	
	private View thisView;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.dashboard, null);
        return thisView;
    }
	 public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        
	    }

	public void updateWifiData() {
		super.updateWifiData();
		TextView txt = (TextView) thisView.findViewById(R.id.txt_db_connected);
		ImageView img = (ImageView) thisView.findViewById(R.id.dashboard_issecured);
		// Log.i("wifi zombie",
		// "DashboardFragment Update data : "+txt.getText()+"  @@");
		// txt.setText(super.wifidata.getWifiInfoData(0).SSID);
//		int i=0;
//		String result = "";
//		result += "SSID : ";
//		result += super.wifidata.getWifiInfoData(i).getSSID();
//		result += "\nBSSID : ";
//		result += super.wifidata.getWifiInfoData(i).getBSSID();
//		result += "\ncapabilities : ";
//		result += super.wifidata.getWifiInfoData(i).getCapabilities();
//		result += "\nfrequency : ";
//		result += super.wifidata.getWifiInfoData(i).getFrequency();
//		result += "\nlevel : ";
//		result += super.wifidata.getWifiInfoData(i).getStrength();
		WifiDataItem item = null;
		for(int i=0 ; i<super.wifidata.getSize() ; i++)
		{
			if(super.wifidata.getWifiInfoData(i).isConnected())
				item = super.wifidata.getWifiInfoData(i);
		}
		WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		
		String result = "";
		if(item != null)
		{
			result += info.getSSID();
			result += "\nBSSID : ";
			result += info.getBSSID();
			result += "\nMAC Addr : ";
			result += info.getMacAddress();
			result += "\nIP : ";
			result += info.getIpAddress();
			result += "\nLink speed : ";
			result += info.getLinkSpeed();
			result += "\nNetwork ID : ";
			result += info.getNetworkId();
			result += "\n===================";
			result += "\nSignal : ";
			result += item.getStrength();
			result += "\nSecurity Mode : ";
			result += item.getSecurityMode();
			result += "\nFrequency : ";
			result += item.getFrequency()+"Mhz";
			result += "\nBandwidth : ";
			result += item.getBandWidth()+"G";
			
			img.setVisibility(item.isSecured());
		}
		else
			result += "\nConnected nothing";

		txt.setText(result);
	}
}

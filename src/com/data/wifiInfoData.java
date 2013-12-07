package com.data;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;

import com.wifi_zombie.WifiZombieProto.WifiInfo;
import com.wifi_zombie.WifiZombieProto.WifiInfo.SecurityType;

public class WifiInfoData {
	// singletone var
	private static WifiInfoData instance = null;
	
	private ArrayList<WifiDataItem> wifiInfoList;
	
	private WifiInfoData()
	{
		wifiInfoList = new ArrayList<WifiDataItem>();
	}
	
	public static WifiInfoData getInstance()
	{
		if(instance == null)
			instance = new WifiInfoData();
		return instance;
	}
	public ArrayList<WifiDataItem> getWifiInfoData()
	{
		return wifiInfoList;
	}
	public WifiDataItem getWifiInfoData(int i)
	{
		return wifiInfoList.get(i);
	}
	public void setWifiInfoData(List<ScanResult> result, String connectedSSID, String connectedBSSID)
	{
		wifiInfoList.clear();
		for(int i=0 ; i<result.size() ; i++)
			wifiInfoList.add(new WifiDataItem(result.get(i), (result.get(i).SSID.equals(connectedSSID)) && (result.get(i).BSSID.equals(connectedBSSID))));
	}
	public int getSize()
	{
		return wifiInfoList.size();
	}
	public WifiInfo getWifiInfo() {
		WifiInfo.Builder wifiInfo = WifiInfo.newBuilder();
		
		for (WifiDataItem data : wifiInfoList) {
			wifiInfo.addWifiData(
				WifiInfo.WifiData.newBuilder()
					.setSsid(data.getSSID())
					.setBssid(data.getBSSID())
					.setStrength(data.getStrength())
					.setChannel(data.getChannel())
					.setBandwidth(data.getBandWidth())
					.setSecurity(SecurityType.valueOf(data.getSecurityMode()))
			);
		}
		
		return wifiInfo.build();
	}
}

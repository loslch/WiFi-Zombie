package com.data;

import java.util.List;

import android.net.wifi.ScanResult;

public class WifiInfoData {
	private List<ScanResult> wifiInfoList;
	
	public ScanResult getWifiInfoData(int i)
	{
		return wifiInfoList.get(i);
	}
	public void setWifiInfoData(List<ScanResult> result)
	{
		wifiInfoList = result;
	}
}

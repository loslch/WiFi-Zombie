package com.fragments;

import com.data.WifiInfoData;

import android.support.v4.app.Fragment;

public class MyFragment extends Fragment{
	
	protected WifiInfoData wifidata = WifiInfoData.getInstance();
	
	public void updateWifiData()
	{
		wifidata = WifiInfoData.getInstance();
	}

}

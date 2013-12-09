package com.fragments;

import java.util.Calendar;

import source.MyFragment;
import source.MyView;

import com.data.WifiDataItem;
import com.todddavies.components.progressbar.ProgressWheel;
import com.wifi_zombie.R;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DashboardFragment extends MyFragment{
	
	private View thisView;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.dashboard, null);
		TextView date = (TextView) thisView.findViewById(R.id.dashboard_date);
		Calendar cal = Calendar.getInstance();
		date.setText(cal.get(Calendar.YEAR)+"."+(cal.get(Calendar.MONTH)+1)+"."+cal.get(Calendar.DAY_OF_MONTH));
		
        return thisView;
    }
	 public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        
	    }

	public void updateWifiData() {
		super.updateWifiData();
		ProgressWheel st = (ProgressWheel)thisView.findViewById(R.id.dashboard_strength);
		TextView bssid = (TextView) thisView.findViewById(R.id.dashboard_bssid);
//		TextView band = (TextView) thisView.findViewById(R.id.dashboard_band);
		TextView freq = (TextView) thisView.findViewById(R.id.dashboard_freq);
		TextView ip = (TextView) thisView.findViewById(R.id.dashboard_ip);
		TextView linkspeed = (TextView) thisView.findViewById(R.id.dashboard_linkspeed);
		TextView mac = (TextView) thisView.findViewById(R.id.dashboard_mac);
		TextView networkid = (TextView) thisView.findViewById(R.id.dashboard_networkid);
		TextView securitymode = (TextView) thisView.findViewById(R.id.dashboard_securitymode);
		ImageView img = (ImageView) thisView.findViewById(R.id.dashboard_issecured);

		WifiDataItem item = null;
		for(int i=0 ; i<super.wifidata.getSize() ; i++)
		{
			if(super.wifidata.getWifiInfoData(i).isConnected())
				item = super.wifidata.getWifiInfoData(i);
		}
		WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		
		// Progresswheel이 최대값이 360이당
		st.setProgress((int)((100+item.getStrength())*3.6));
		st.setText(info.getSSID()+"\n"+"CH "+item.getChannel()+"\n"+item.getBandWidth()+" G"+"\n"+item.getStrength()+" dbm");
//		st.setText(item.getStrength()+"dbm");
//		ssid.setText(Html.fromHtml("<b>"+info.getSSID()+"</b>"));
		
		
		if(item != null)
		{
			bssid.setText(info.getBSSID());
//			band.setText(item.getBandWidth()+"G");
			freq.setText(item.getFrequency()+"Mhz");
			ip.setText(info.getIpAddress()+"");
			linkspeed.setText(info.getLinkSpeed()+"");
			mac.setText(info.getMacAddress()+"");
			networkid.setText(info.getNetworkId()+"");
			securitymode.setText(item.getSecurityMode());
			if(item.isSecured())
				img.setBackgroundResource(R.drawable.security);
			else
				img.setBackgroundResource(R.drawable.no_security);
		}
	}
}

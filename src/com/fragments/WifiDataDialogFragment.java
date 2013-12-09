package com.fragments;

import com.data.WifiInfoData;
import com.wifi_zombie.R;
import com.wifi_zombie.WifiZombieProto.WifiInfo.WifiData;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WifiDataDialogFragment extends DialogFragment {
	private final int maxProgress = 100;
	private int position;
	private View thisView;
	private View titleView;
	private LayoutInflater layoutInflater;
	private WifiData wifiData;
	@SuppressLint("ValidFragment")
	public WifiDataDialogFragment()
	{}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		layoutInflater = getActivity().getLayoutInflater();
		thisView = layoutInflater.inflate(R.layout.dialog_apinfo, null);
		titleView = layoutInflater.inflate(R.layout.dialog_title_apinfo, null);
		builder.setView(thisView);
		
		Button btn_OK = (Button)thisView.findViewById(R.id.dialog_apinfon_btnOK);
		btn_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onClickOK();				
			}
		});
		builder.setCustomTitle(titleView);
		TextView tv_title = (TextView)titleView.findViewById(R.id.dialog_apinfo_title);
		TextView tv_bssid = (TextView)thisView.findViewById(R.id.dialog_apinfon_bssid);
		TextView tv_channel = (TextView)thisView.findViewById(R.id.dialog_apinfon_channel);
		TextView tv_frequency = (TextView)thisView.findViewById(R.id.dialog_apinfon_frequency);
		TextView tv_security = (TextView)thisView.findViewById(R.id.dialog_apinfon_security);
		TextView tv_strength = (TextView)thisView.findViewById(R.id.dialog_apinfon_strength);
		ProgressBar pb_strength = (ProgressBar)thisView.findViewById(R.id.dialog_apinfon_strengthProgress);
		// progress bar setting
		pb_strength.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.progress_horizontal_aplist));
		
		tv_title.setText(wifiData.getSsid());

		tv_bssid.setText(wifiData.getBssid());
		tv_channel.setText(wifiData.getChannel()+"");
		tv_frequency.setText(wifiData.getBandwidth()+"");
		tv_strength.setText(wifiData.getStrength()+"");
		tv_security.setText(wifiData.getSecurity().toString());
		pb_strength.setProgress(maxProgress+wifiData.getStrength());
		return builder.create();
	}
	public void setWifiData(WifiData wifiData) {
		this.wifiData = wifiData;
	}
	@Override	
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	public void onClickOK()
	{
		this.onStop();
	}
}

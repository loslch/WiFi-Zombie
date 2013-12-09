package com.fragments;

import com.data.WifiInfoData;
import com.wifi_zombie.R;

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

public class APInfoDialogFragment extends DialogFragment {
	private final int maxProgress = 100;
	private int position;
	private View thisView;
	private View titleView;
	private LayoutInflater layoutInflater;
	private WifiInfoData wifiInfodata;
	@SuppressLint("ValidFragment")
	public APInfoDialogFragment()
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
		
		wifiInfodata = WifiInfoData.getInstance();
		tv_title.setText(wifiInfodata.getWifiInfoData(position).getSSID());

		tv_bssid.setText(wifiInfodata.getWifiInfoData(position).getBSSID());
		tv_channel.setText(wifiInfodata.getWifiInfoData(position).getChannel()+"");
		tv_frequency.setText(wifiInfodata.getWifiInfoData(position).getFrequency()+"");
		tv_strength.setText(wifiInfodata.getWifiInfoData(position).getStrength()+"");
		tv_security.setText(wifiInfodata.getWifiInfoData(position).getCapabilities());
		pb_strength.setProgress(maxProgress+wifiInfodata.getWifiInfoData(position).getStrength());
		return builder.create();
	}
	public void setPosition(int i)
	{
		position = i;	
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

package com.wifi_zombie;

import java.util.ArrayList;
import java.util.List;

import com.data.DiaryData;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<DiaryData>{
	private final int maxProgress = 100;
	private Context mContext;
    private int mResource;
    private ArrayList<DiaryData> mList;
    private LayoutInflater mInflater;
    
	public CustomAdapter(Context context, int textViewResourceId, ArrayList<DiaryData> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mResource = textViewResourceId;
		this.mList = objects;
		this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
		DiaryData item = mList.get(position);
 
        if(convertView == null)
        {
            convertView = mInflater.inflate(mResource, null);
        }
 
        if(item != null)
        {
            TextView ssid = (TextView) convertView.findViewById(R.id.aplist_item_ssid);
            TextView frequency = (TextView) convertView.findViewById(R.id.aplist_item_frequency);
            TextView channel = (TextView) convertView.findViewById(R.id.aplist_item_channel);
            TextView security = (TextView) convertView.findViewById(R.id.aplist_item_security);
            TextView bandwidth = (TextView) convertView.findViewById(R.id.aplist_item_bandwidth);
            ImageView isSecured = (ImageView) convertView.findViewById(R.id.aplist_item_issecured);
            ProgressBar strength = (ProgressBar) convertView.findViewById(R.id.aplist_item_strength);
 
            ssid.setText(item.getSSID()+" ("+item.getBSSID()+")");
            channel.setText("CH "+item.getChannel()+" ");	// 채널은 frequency 가지고 계산한당
            frequency.setText(item.getFrequency() + "MHz ");
            security.setText("Secured by ["+item.getSecurityMode()+"]");
            bandwidth.setText(item.getBandWidth()+"G");
            isSecured.setVisibility(item.isSecured());
            strength.setProgress(maxProgress + item.getStrength());       
        }
 
        return convertView;
    }
}

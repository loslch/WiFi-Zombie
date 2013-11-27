package com.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.data.DiaryData;
import com.data.WifiInfoData;
import com.wifi_zombie.CustomAdapter;
import com.wifi_zombie.R;

public class AccessPointListFragment extends MyFragment{
	private View thisView;
	private ListView aplist;
	private APInfoDialogFragment dialog;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.accesspointlist, null);
		aplist = (ListView)thisView.findViewById(R.id.aplist);
        return thisView;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	public void updateWifiData() {
		super.updateWifiData();
//		ArrayList<WifiDataItem> list = new ArrayList<WifiDataItem>();
//		for (int i = 0; i < wifidata.getSize(); i++)
//			list.add(super.wifidata.getWifiInfoData(i));
		CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.aplist_item, super.wifidata.getWifiInfoData());
		aplist.setAdapter(adapter);
		aplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dialog = new APInfoDialogFragment();
				dialog.setPosition(position);
				dialog.show(getFragmentManager(), "Detail");
				
			}
			
		});
	}
}

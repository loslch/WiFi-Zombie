package com.fragments;

import com.wifi_zombie.CustomAdapter;
import com.wifi_zombie.GraphCanvasView;
import com.wifi_zombie.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class ChannelGraphFragment extends MyFragment {
	public static final int BANDWIDTH_2_4G = 2;
    public static final int BANDWIDTH_5G = 5;
    
    private View thisView;
    private Button changeBtn;
    private GraphCanvasView graphView;
    private boolean is5G;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		is5G = false;
		thisView = thisView = inflater.inflate(R.layout.channelgraph, null); 
		changeBtn = (Button)thisView.findViewById(R.id.channelgraph_btn1);
		graphView = (GraphCanvasView)thisView.findViewById(R.id.channelgraph_canvas);
		
		changeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(is5G)	// 5G¿¡¼­ 2.4G·Î ¹Ù²Þ
				{
					changeBtn.setText("2.4G");
					is5G = false;
					graphView.setIs5G(is5G);
					graphView.invalidate();
				}
				else	// 2.4G¿¡¼­ 5G·Î ¹Ù²Þ
				{
					changeBtn.setText("5G");
					is5G = true;
					graphView.setIs5G(is5G);
					graphView.draw5Ggraph();
				}
			}
		});
        return thisView;
	}
	
	public void updateWifiData() {
		super.updateWifiData();

	}
}

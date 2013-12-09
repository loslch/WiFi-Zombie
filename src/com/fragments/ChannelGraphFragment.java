package com.fragments;

import source.APListAdapter;
import source.GraphCanvasView2_4G;
import source.GraphCanvasView5G;
import source.GraphCanvasView5G_Axis;
import source.MyFragment;
import source.MyView;

import com.wifi_zombie.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ChannelGraphFragment extends MyFragment {
	public static final int BANDWIDTH_2_4G = 2;
    public static final int BANDWIDTH_5G = 5;
    
    // layout var
    private View thisView;
    private TextView changeBtn;
    private GraphCanvasView2_4G graphView2_4G;
    private GraphCanvasView5G graphView5G;
    private GraphCanvasView5G_Axis graphView5G_axis;
    private TextView title;
    private SeekBar seekbar5G;
    
    private boolean is5G = false;
    private int screenWidth, screenHeight;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
				
		thisView = inflater.inflate(R.layout.channelgraph, null);
		Display dis = ((WindowManager)getActivity().getSystemService(getActivity().WINDOW_SERVICE)).getDefaultDisplay();
		screenWidth = dis.getWidth();
		screenHeight = dis.getHeight();
		
		// layout var 초기화
		changeBtn = (TextView)thisView.findViewById(R.id.channelgraph_btn1);
		graphView2_4G = (GraphCanvasView2_4G)thisView.findViewById(R.id.channelgraph_canvas);
		graphView5G = (GraphCanvasView5G)thisView.findViewById(R.id.channelgraph_canvas5G);
		graphView5G_axis = (GraphCanvasView5G_Axis)thisView.findViewById(R.id.channelgraph_canvas5G_axis);
		seekbar5G = (SeekBar)thisView.findViewById(R.id.channelgraph_seekbar);
		title = (TextView)thisView.findViewById(R.id.channelgraph_title);
		// canvas size setting
		graphView5G_axis.setLayoutParams(new RelativeLayout.LayoutParams(graphView5G_axis.leftAndBottomMargin+2, LayoutParams.FILL_PARENT));
		graphView5G.setLayoutParams(new RelativeLayout.LayoutParams((screenWidth-graphView5G.leftAndBottomMargin)*(MyView.chNum5G/MyView.chInScreen5G)+graphView5G.leftAndBottomMargin, LayoutParams.FILL_PARENT));
		
		seekbar5G.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.progress_horizontal_seekbar));
		seekbar5G.setThumb(getActivity().getResources().getDrawable(R.drawable.seek_mythumb));
		seekbar5G.setMax((MyView.chNum5G-MyView.chInScreen5G)/2);
		seekbar5G.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(progress < (MyView.chNum5G-MyView.chInScreen5G)/2)
				{
					graphView5G.setMargin(progress*(graphView5G.getLayoutParams().width/MyView.chNum5G)*(-2));
					Log.i("wifi zombie", seekBar.getMax()+" progress : "+progress+" "+progress*(graphView5G.getLayoutParams().width/MyView.chNum5G)*(-2));
				}
				
			}
		});
		//button onclick listener setting
		changeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(is5G)	// 5G에서 2.4G로 바꿈
				{
					changeBtn.setText("2.4G");
					is5G = false;
					graphView2_4G.setVisibility(View.VISIBLE);
					graphView5G.setVisibility(View.INVISIBLE);
					graphView5G_axis.setVisibility(View.INVISIBLE);
					seekbar5G.setVisibility(View.INVISIBLE);
					graphView2_4G.invalidate();
					title.setText("2.4G Channel Graph");
					changeBtn.setText("5G");
				}
				else	// 2.4G에서 5G로 바꿈
				{
					changeBtn.setText("5G");
					is5G = true;
					graphView2_4G.setVisibility(View.INVISIBLE);
					graphView5G.setVisibility(View.VISIBLE);
					graphView5G_axis.setVisibility(View.VISIBLE);
					seekbar5G.setVisibility(View.VISIBLE);
					graphView5G.invalidate();
					title.setText("5G Channel Graph");
					changeBtn.setText("2.4G");
					
				}
			}
		});
	
        return thisView;
	}
	
	public void updateWifiData() {
		super.updateWifiData();
		graphView2_4G.updateData(super.wifidata);
		graphView5G.updateData(super.wifidata);

	}
}

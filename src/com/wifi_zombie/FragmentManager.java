package com.wifi_zombie;

import com.fragments.*;

import android.support.v4.app.Fragment;

public class FragmentManager {
	
	public static FragmentManager instance = null;
	
	private Fragment Dashboard;
	// OntheSpotAnalysis
	private Fragment AccessPointList;
	private Fragment ChannelGraph;
	private Fragment RatingChannel;
	// Coverage
	private Fragment IndoorSurvey;
	private Fragment OutdoorSurvey;
	
	private Fragment Settings;
	private Fragment Help;
	
	public static FragmentManager getInstance()
	{
		if(instance == null)
			instance = new FragmentManager();
		return instance;
	}
	
	FragmentManager()
	{
		Dashboard = new DashboardFragment();
		AccessPointList = new AccessPointListFragment();
		ChannelGraph = new ChannelGraphFragment();
		RatingChannel = new DashboardFragment();
		IndoorSurvey = new DashboardFragment();
		OutdoorSurvey = new DashboardFragment();
		Settings = new DashboardFragment();
		Help = new DashboardFragment();
	}
	
	public Fragment getFragmentByMenu(String menu)
	{
		if(menu.equals("Dashboard")) return Dashboard;
		else if(menu.equals("Access Point List")) return AccessPointList;
		else if(menu.equals("Channel Graph")) return ChannelGraph;
		else if(menu.equals("Rating Channel")) return RatingChannel;
		else if(menu.equals("Indoor Survey")) return IndoorSurvey;
		else if(menu.equals("Outdoor Survey")) return OutdoorSurvey;
		else if(menu.equals("Settings")) return Settings;
		else if(menu.equals("Help")) return Help;
		else return null;
	}
	
}

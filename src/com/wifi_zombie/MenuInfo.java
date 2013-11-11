package com.wifi_zombie;

import android.content.Context;



public class MenuInfo {

	static MenuInfo instance = null;
	private String[] slide_menu = {"Dashboard", "On the Spot Analysis", "Coverage Analysis", "Settings", "Help"};
	private String[] slide_menu1 = {"Access Point List", "Strength Graph", "Channel Graph", "Rating Channel"}; 
	private String[] slide_menu2 = {"Indoor Survey", "Outdoor Survey"};

	
	public static MenuInfo getInstance()
	{
		if(instance == null)
			instance = new MenuInfo();
		return instance;
	}
	
	public MenuInfo()
	{}
	
//	public SlideMenuAdapter getAdapter(int i)
//	{
//		switch(i)
//		{
//		case 0: return adapter;
//		case 1: return adapter1;
//		case 2: return adapter2;
//		}
//		return null;
//	}
	public void setAdapter(Context con)
	{
		
	}
	public String getSlideMenu(int i)
	{
		return slide_menu[i];
	}
	public String getSlideMenu1(int i)
	{
		return slide_menu1[i];
	}
	public String getSlideMenu2(int i)
	{
		return slide_menu2[i];
	}
	
}

package com.data;

import android.net.wifi.ScanResult;

public class DiaryData {
	private ScanResult APInfo;
	private int channel=0;
	private int isSecured;
	private float bandWidth;
	private String securityMode;
	private boolean isConnected;
	public static final String PSK = "PSK";
    public static final String WEP = "WEP";
    public static final String EAP = "EAP";
	private final String[] securityModes = { WEP, PSK, EAP };
	
	public DiaryData(ScanResult ap, boolean isCon)
	{
		APInfo = ap;
		channel = calcChannel();
		isSecured = calcSecure();
		isConnected = isCon;
	}
	public boolean isConnected()
	{
		return isConnected;
	}
	public int isSecured()
	{
		return isSecured;
	}
	public int getChannel()
	{
		return channel;
	}
	public String getSSID()
	{
		return APInfo.SSID;
	}
	public String getBSSID()
	{
		return APInfo.BSSID;
	}
	public String getCapabilities()
	{
		return APInfo.capabilities;
	}
	public String getSecurityMode()
	{
		return securityMode;
	}
	public int getFrequency()
	{
		return APInfo.frequency;
	}
	public int getStrength()
	{
		return APInfo.level;
	} 
	public float getBandWidth()
	{
		return bandWidth;
	}
	public int calcChannel()
	{
		// channel 계산 함수
		int fq =  APInfo.frequency;
		if(fq/1000 == 2)
		{
			bandWidth = (float)2.4;
			if(fq == 2401 || fq == 2412 || fq  == 2423)
				return 1;
			else if(fq == 2404 || fq == 2417 || fq  == 2428)
				return 2;
			else if(fq == 2411 || fq == 2422 || fq  == 2433)
				return 3;
			else if(fq == 2416 || fq == 2427 || fq  == 2438)
				return 4;
			else if(fq == 2421 || fq == 2432 || fq  == 2443)
				return 5;
			else if(fq == 2426 || fq == 2437 || fq  == 2448)
				return 6;
			else if(fq == 2431 || fq == 2442 || fq  == 2453)
				return 7;
			else if(fq == 2436 || fq == 2447 || fq  == 2458)
				return 8;
			else if(fq == 2441 || fq == 2452 || fq  == 2463)
				return 9;
			else if(fq == 2451 || fq == 2457 || fq  == 2468)
				return 10;
			else if(fq == 2451 || fq == 2462 || fq  == 2473)
				return 11;
			else if(fq == 2456 || fq == 2467 || fq  == 2478)
				return 12;
			else if(fq == 2461 || fq == 2472 || fq  == 2483)
				return 13;
			else if(fq == 2473 || fq == 2484 || fq  == 2495)
				return 14;
		}
		else if(fq/1000 == 5)
		{
			bandWidth = 5;
			switch(fq)
			{
			case 5180:
				return 36;
			case 5200:
				return 40;
			case 5220:
				return 44;
			case 5240:
				return 48;
			case 5260:
				return 52;
			case 5280:
				return 56;
			case 5300:
				return 60;
			case 5320:
				return 64;
			case 5500:
				return 100;
			case 5520:
				return 104;
			case 5540:
				return 108;
			case 5560:
				return 112;
			case 5580:
				return 116;
			case 5600:
				return 120;
			case 5620:
				return 124;
			case 5640:
				return 128;
			case 5660:
				return 132;
			case 5680:
				return 136;
			case 5700:
				return 140;
			case 5745:
				return 149;
			case 5765:
				return 153;
			case 5785:
				return 157;
			case 5805:
				return 161;
			case 5825:
				return 165;
			}
		}
		return 0;
	}
	public int calcSecure()
	{
		String cap = APInfo.capabilities;
		for (int i = securityModes.length - 1; i >= 0; i--) {
            if (cap.contains(securityModes[i])) {
                securityMode =  securityModes[i];
                return android.view.View.VISIBLE;
            }
		}
		securityMode = "";
		return android.view.View.INVISIBLE;
	}

}

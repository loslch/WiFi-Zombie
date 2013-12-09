package source;

import com.data.WifiInfoData;

import android.support.v4.app.Fragment;

public class MyFragment extends Fragment{
	
	protected WifiInfoData wifidata = WifiInfoData.getInstance();
	protected int chNum2_4G = 14;
	protected int chNum5G = 24;
	protected int[] ch2_4G = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
	protected int[] ch5G = { 36, 40, 44, 48, 52, 56, 60, 64, 100, 104, 108,
			112, 116, 120, 124, 128, 132, 136, 140, 149, 153, 157, 161, 165 };
	
	protected int[] intervals = {5, 10, 20, 30};
	protected int[] intervalsms = {5000, 10000, 20000, 30000};
	protected int intervalNum = 4;
	public void updateWifiData()
	{
		wifidata = WifiInfoData.getInstance();
	}

}

package source;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SettingDataPref {
	
	private final String PREF_NAME = "wifi_zombie.pref";
	private final String IntervalKey ="wifi_zombie.interval";
	
	private Context context;
	
	public SettingDataPref(Context con)
	{
		context = con;
	}
	
	public void putIntervalData(int value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
 
        editor.putInt(IntervalKey, value);
        editor.commit();
    }
	public int getIntervalData() {
		SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
 
        try {
            return pref.getInt(IntervalKey, 5000);
        } catch (Exception e) {
            return 5000;
        }
    }
//	 public void put(String key, String value) {
//	        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
//	                Activity.MODE_PRIVATE);
//	        SharedPreferences.Editor editor = pref.edit();
//	 
//	        editor.putString(key, value);
//	        editor.commit();
//	    }
//	 
//	    public void put(String key, boolean value) {
//	        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
//	                Activity.MODE_PRIVATE);
//	        SharedPreferences.Editor editor = pref.edit();
//	 
//	        editor.putBoolean(key, value);
//	        editor.commit();
//	    }
//	 
//	    public void put(String key, int value) {
//	        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
//	                Activity.MODE_PRIVATE);
//	        SharedPreferences.Editor editor = pref.edit();
//	 
//	        editor.putInt(key, value);
//	        editor.commit();
//	    }
}

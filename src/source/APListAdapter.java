package source;

import java.util.ArrayList;
import java.util.List;

import com.data.WifiDataItem;
import com.wifi_zombie.R;
import com.wifi_zombie.R.id;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class APListAdapter extends ArrayAdapter<WifiDataItem>{
	private final int maxProgress = 100;
	private Context mContext;
    private int mResource;
    private ArrayList<WifiDataItem> mList;
    private LayoutInflater mInflater;
    
	public APListAdapter(Context context, int textViewResourceId, ArrayList<WifiDataItem> objects) {
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
		WifiDataItem item = mList.get(position);
 
        if(convertView == null)
        {
            convertView = mInflater.inflate(mResource, null);
        }
 
        if(item != null)
        {
            TextView ssid = (TextView) convertView.findViewById(R.id.aplist_item_ssid);
            TextView bssid = (TextView) convertView.findViewById(R.id.aplist_item_bssid);
            TextView signal = (TextView) convertView.findViewById(R.id.aplist_item_signal);
            TextView channel = (TextView) convertView.findViewById(R.id.aplist_item_channel);
            TextView security = (TextView) convertView.findViewById(R.id.aplist_item_security);
            TextView bandwidth = (TextView) convertView.findViewById(R.id.aplist_item_band);
            ImageView isSecured = (ImageView) convertView.findViewById(R.id.aplist_item_issecured);
            ProgressBar strength = (ProgressBar) convertView.findViewById(R.id.aplist_item_strength);
           
            strength.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_horizontal_aplist));
            
            ssid.setText(Html.fromHtml("<b>"+item.getSSID()+"</b>"));
            bssid.setText("("+item.getBSSID()+")");
            channel.setText("CH "+item.getChannel()+" ");	// 채널은 frequency 가지고 계산한당
            signal.setText(item.getStrength() + "dbm");
            security.setText("Secured by ["+item.getSecurityMode()+"]");
            bandwidth.setText(item.getBandWidth()+"G");
            if(item.isSecured())
            	isSecured.setBackgroundResource(R.drawable.security);
            else
            	isSecured.setBackgroundResource(R.drawable.no_security);
            strength.setProgress(maxProgress + item.getStrength());       
        }
 
        return convertView;
    }
}

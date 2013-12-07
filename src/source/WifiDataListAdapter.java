package source;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wifi_zombie.R;
import com.wifi_zombie.WifiZombieProto.WifiInfo.SecurityType;
import com.wifi_zombie.WifiZombieProto.WifiInfo.WifiData;

public class WifiDataListAdapter extends ArrayAdapter<WifiData>{
	private final int maxProgress = 100;
	private Context mContext;
    private int mResource;
    private List<WifiData> mList;
    private LayoutInflater mInflater;
    
	public WifiDataListAdapter(Context context, int textViewResourceId, List<WifiData> list) {
		super(context, textViewResourceId, list);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mResource = textViewResourceId;
		this.mList = list;
		this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
		WifiData item = mList.get(position);
 
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
//            TextView security = (TextView) convertView.findViewById(R.id.aplist_item_security);
            TextView bandwidth = (TextView) convertView.findViewById(R.id.aplist_item_band);
            ImageView isSecured = (ImageView) convertView.findViewById(R.id.aplist_item_issecured);
            ProgressBar strength = (ProgressBar) convertView.findViewById(R.id.aplist_item_strength);
           
            strength.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_horizontal_aplist));
            
            ssid.setText(Html.fromHtml("<b>"+item.getSsid()+"</b>"));
            bssid.setText("("+item.getBssid()+")");
            channel.setText("CH "+item.getChannel()+" ");	// 채널은 frequency 가지고 계산한당
            signal.setText(item.getStrength() + "dbm");
//            security.setText("Secured by ["+item.getSecurity().toString()+"]");
            bandwidth.setText(item.getBandwidth()+"G");
            if(item.getSecurity() != SecurityType.NONE)
            	isSecured.setBackgroundResource(R.drawable.security);
            else
            	isSecured.setBackgroundResource(R.drawable.no_security);
            strength.setProgress(maxProgress + item.getStrength());       
        }
 
        return convertView;
    }
}

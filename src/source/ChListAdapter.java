package source;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.data.WifiDataItem;
import com.wifi_zombie.R;

public class ChListAdapter extends ArrayAdapter<int[]> {
	private final int maxProgress = 100;
	private Context mContext;
    private int mResource;
    private ArrayList<int[]> mList;
    private LayoutInflater mInflater;
    
	public ChListAdapter(Context context, int textViewResourceId, ArrayList<int[]> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mResource = textViewResourceId;
		this.mList = objects;
		this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// channel °è
	}
	@Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
		int[] item = mList.get(position);
		 
        if(convertView == null)
        {
            convertView = mInflater.inflate(mResource, null);
        }
 
        if(item != null)
        {
            TextView channel = (TextView) convertView.findViewById(R.id.chlist_item_channel);
            TextView percent = (TextView) convertView.findViewById(R.id.chlist_item_percent);
            TextView aps = (TextView) convertView.findViewById(R.id.chlist_item_aps);
            TextView overlapping = (TextView) convertView.findViewById(R.id.chlist_item_overlapping);
            ProgressBar rating = (ProgressBar) convertView.findViewById(R.id.chlist_item_progressbar);
            
            rating.setMax(100);
            
            channel.setText(item[0]+"");	// Ã¤³Î
            percent.setText(item[1]+"%");
            aps.setText(item[2]+" Aps");
            overlapping.setText(item[3]+" Overlapping Aps");
            rating.setProgress(item[1]);
        }
		
		return convertView;
    }
	
}

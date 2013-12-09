package com.fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.data.WifiDataItem;
import com.data.WifiInfoData;
import com.wifi_zombie.R;

import source.APListAdapter;
import source.MyFragment;
import source.SettingDataPref;

@SuppressLint("ValidFragment")
public class SettingFragment  extends MyFragment{
	private int interval = 5000;
	private View thisView;
	private ListView list;
	private ArrayList<SettingListItem> settinglist;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.setting, null);
		list = (ListView)thisView.findViewById(R.id.setting_list);
		
		settinglist = new ArrayList<SettingListItem>();
		
		 SettingDataPref sPref = new SettingDataPref(getActivity()); 
	     interval = sPref.getIntervalData();
	     
	     settinglist.add(new SettingListItem("Scan settings", "Scan interval", (interval/1000)+"(sec)", true));
	     
	     
	     SettingListAdapter adapter = new SettingListAdapter(getActivity(), R.layout.settinglist_item, settinglist);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					IntervalDialogFragment dialog = new IntervalDialogFragment();
					dialog.show(getFragmentManager(), "Interval");
				}
				
			});
		return thisView;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
	public void setList()
	{
		settinglist = new ArrayList<SettingListItem>();
		
		 SettingDataPref sPref = new SettingDataPref(getActivity()); 
	     interval = sPref.getIntervalData();
	     
	     settinglist.add(new SettingListItem("Scan settings", "Scan interval", (interval/1000)+"(sec)", true));
	     
	     
	     SettingListAdapter adapter = new SettingListAdapter(getActivity(), R.layout.settinglist_item, settinglist);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					IntervalDialogFragment dialog = new IntervalDialogFragment();
					dialog.show(getFragmentManager(), "Interval");
					
				}
				
			});
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		setList();
		
		super.onResume();
	}
	
	public class SettingListAdapter extends ArrayAdapter<SettingListItem>{
		private Context mContext;
	    private int mResource;
	    private ArrayList<SettingListItem> mList;
	    private LayoutInflater mInflater;
		public SettingListAdapter(Context context, int textViewResourceId, ArrayList<SettingListItem> objects) {
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
			SettingListItem item = mList.get(position);
			
			 if(convertView == null)
		        {
		            convertView = mInflater.inflate(mResource, null);
		        }
		 
		        if(item != null)
		        {
		            TextView title = (TextView) convertView.findViewById(R.id.settinglist_item_title);
		            TextView content = (TextView) convertView.findViewById(R.id.settinglist_item_content);
		            TextView value = (TextView) convertView.findViewById(R.id.settinglist_item_value);
		            
		            title.setVisibility(item.isTitle ? View.VISIBLE : View.INVISIBLE);
		            	
		            title.setText(item.getTitle());
		          content.setText(item.getContent());
		          value.setText(item.getValue());
		        }
		 
		        return convertView;
	    }
	}

	public class SettingListItem
	{
		public SettingListItem(String title, String content, String value,
				boolean isTitle) {
			super();
			this.title = title;
			this.content = content;
			this.value = value;
			this.isTitle = isTitle;
		}
		public SettingListItem(String content, String value,
				boolean isTitle) {
			super();
			this.title = "";
			this.content = content;
			this.value = value;
			this.isTitle = isTitle;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public boolean isTitle() {
			return isTitle;
		}
		public void setTitle(boolean isTitle) {
			this.isTitle = isTitle;
		}
		private String title;
		private String content;
		private String value;
		private boolean isTitle;
		
	}
	
	public class IntervalDialogFragment extends DialogFragment {
		private int iv;
		private View thisDialogView;
		private View titleView;
		private LayoutInflater layoutInflater;
		private WifiInfoData wifiInfodata;
		
		public IntervalDialogFragment()
		{
			iv = interval;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			layoutInflater = getActivity().getLayoutInflater();
			thisDialogView = layoutInflater.inflate(R.layout.dialog_setting_interval, null);
			titleView = layoutInflater.inflate(R.layout.dialog_title_apinfo, null);
			Button btn_OK = (Button)thisDialogView.findViewById(R.id.dialog_setting_interval_btnOK);
			btn_OK.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					SettingDataPref sPref = new SettingDataPref(getActivity()); 
			        sPref.putIntervalData(iv);
			        setList();
					onClickOK();	
				}
			});

			((TextView)titleView.findViewById(R.id.dialog_apinfo_title)).setText("set interval");
			builder.setView(thisDialogView);
			
			builder.setCustomTitle(titleView);
			
			RadioGroup rg = (RadioGroup)thisDialogView.findViewById(R.id.dialog_setting_interval_radioGroup);
			final RadioButton rb5 = (RadioButton)thisDialogView.findViewById(R.id.dialog_setting_interval_radio5);
			final RadioButton rb10 = (RadioButton)thisDialogView.findViewById(R.id.dialog_setting_interval_radio10);
			final RadioButton rb20 = (RadioButton)thisDialogView.findViewById(R.id.dialog_setting_interval_radio20);
			final RadioButton rb30 = (RadioButton)thisDialogView.findViewById(R.id.dialog_setting_interval_radio30);
			
			rb5.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
			rb10.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
			rb20.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
			rb30.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
			
			switch(interval)
			{
			case 5000:
				rb5.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_checked));
				break;
			case 10000:
				rb10.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_checked));
				break;
			case 20000:
				rb20.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_checked));
				break;
			case 30000:
				rb30.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_checked));
				break;
			}
			rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					int id = group.getCheckedRadioButtonId();
					switch(id)
					{
					case R.id.dialog_setting_interval_radio5:
						iv = 5000;
						rb5.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_checked));
						rb10.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
						rb20.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
						rb30.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
						break;
					case R.id.dialog_setting_interval_radio10:
						iv = 10000;
						rb5.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
						rb10.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_checked));
						rb20.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
						rb30.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
						break;
					case R.id.dialog_setting_interval_radio20:
						iv = 20000;
						rb5.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
						rb10.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
						rb20.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_checked));
						rb30.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
						break;
					case R.id.dialog_setting_interval_radio30:
						iv = 30000;
						rb5.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
						rb10.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
						rb20.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
						rb30.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_checked));
						break;
							
					}
				}
			});
			
			return builder.create();
		}
		@Override	
		public void onStop() {
			// TODO Auto-generated method stub
			super.onStop();
		}
		public void onClickOK()
		{
			this.onStop();
		}
	}

	
}

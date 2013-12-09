 package com.fragments;

import com.wifi_zombie.R;
import com.wifi_zombie.R.array;
import com.wifi_zombie.R.id;
import com.wifi_zombie.R.layout;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SlideMenuFragment extends ListFragment implements OnItemClickListener {
    
	private final int mainMenuFontSize = 20;
	private final int subMenuFontSize = 17;
	
    private SlideMenuAdapter adapter;
    private SlideMenuAdapter adapters[];
    private final int adaptersNum = 4;
    boolean isOpenSpot = false;
    boolean isOpenCoverage = false;
    Screen currentScreen;
    private String currentMenu = "Dashboard";
    
    private enum Screen {
        DASHBOARD(0), SPOT(1), COVERAGE(2), SETTINGS(3), HELP(4);

        final int numScreen;
        
        private Screen(int num) {
            this.numScreen = num;
        }

        public int getValue() {
            return this.numScreen;
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // beginning screen is dashboard
        currentScreen = Screen.DASHBOARD;
        
        // preparing list data        
        setAdapters();
        
        getListView().setOnItemClickListener(this);
    }
    
    private void setAdapters()
    {
    	adapters = new SlideMenuAdapter[adaptersNum];
    	for(int i=0 ; i<adaptersNum ; i++)
    		adapters[i] = new SlideMenuAdapter(getActivity());
    	
    	String[] slide_menu = getResources().getStringArray(R.array.slide_menu);
    	String[] children_1 = getResources().getStringArray(R.array.slide_menu_1);
    	String[] children_2 = getResources().getStringArray(R.array.slide_menu_2);
    	
    	// nothing
    	adapters[0].add(new SlideMenuItem(slide_menu[0], true, R.drawable.ic_menu_front));
        adapters[0].add(new SlideMenuItem(slide_menu[1], true, R.drawable.ic_menu_front));
        adapters[0].add(new SlideMenuItem(slide_menu[2], true, R.drawable.ic_menu_front));
        adapters[0].add(new SlideMenuItem(slide_menu[3], true, R.drawable.ic_menu_front));
        adapters[0].add(new SlideMenuItem(slide_menu[4], true, R.drawable.ic_menu_front));
        // on the spot open
        adapters[1].add(new SlideMenuItem(slide_menu[0], true, R.drawable.ic_menu_front));
        adapters[1].add(new SlideMenuItem(slide_menu[1], true, R.drawable.ic_menu_front));
        adapters[1].add(new SlideMenuItem(children_1[0], false, -1));
        adapters[1].add(new SlideMenuItem(children_1[1], false, -1));
        adapters[1].add(new SlideMenuItem(children_1[2], false, -1));
        adapters[1].add(new SlideMenuItem(slide_menu[2], true, R.drawable.ic_menu_front));
        adapters[1].add(new SlideMenuItem(slide_menu[3], true, R.drawable.ic_menu_front));
        adapters[1].add(new SlideMenuItem(slide_menu[4], true, R.drawable.ic_menu_front));
        //coverage open
        adapters[2].add(new SlideMenuItem(slide_menu[0], true, R.drawable.ic_menu_front));
        adapters[2].add(new SlideMenuItem(slide_menu[1], true, R.drawable.ic_menu_front));
        adapters[2].add(new SlideMenuItem(slide_menu[2], true, R.drawable.ic_menu_front));
        adapters[2].add(new SlideMenuItem(children_2[0], false, -1));
        adapters[2].add(new SlideMenuItem(children_2[1], false, -1));
        adapters[2].add(new SlideMenuItem(slide_menu[3], true, R.drawable.ic_menu_front));
        adapters[2].add(new SlideMenuItem(slide_menu[4], true, R.drawable.ic_menu_front));
        // on the spot , coverage open
        adapters[3].add(new SlideMenuItem(slide_menu[0], true, R.drawable.ic_menu_front));
        adapters[3].add(new SlideMenuItem(slide_menu[1], true, R.drawable.ic_menu_front));
        adapters[3].add(new SlideMenuItem(children_1[0], false, -1));
        adapters[3].add(new SlideMenuItem(children_1[1], false, -1));
        adapters[3].add(new SlideMenuItem(children_1[2], false, -1));
        adapters[3].add(new SlideMenuItem(slide_menu[2], true, R.drawable.ic_menu_front));
        adapters[3].add(new SlideMenuItem(children_2[0], false, -1));
        adapters[3].add(new SlideMenuItem(children_2[1], false, -1));
        adapters[3].add(new SlideMenuItem(slide_menu[3], true, R.drawable.ic_menu_front));
        adapters[3].add(new SlideMenuItem(slide_menu[4], true, R.drawable.ic_menu_front));
    	
        adapter = adapters[0];
        setListAdapter(adapter);
    }
    /*
     * Preparing the list data
     */
    private void prepareListData(boolean openSpot, boolean openCoverage) {
        adapter = new SlideMenuAdapter(getActivity());
        
        /** ADD Slide Menu Items **/
       if(openSpot && openCoverage)
    	   adapter = adapters[3];
       else if(openCoverage)
    	   adapter = adapters[2];
       else if(openSpot)
    	   adapter = adapters[1];
       else
    	   adapter = adapters[0];
       
        setListAdapter(adapter);
        
        isOpenSpot = openSpot;
        isOpenCoverage = openCoverage;
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final SlideMenuItem item = adapter.getItem(position);

        String[] menues = getResources().getStringArray(R.array.slide_menu);

        if(item.tag.equals(menues[0])) {
            currentScreen = Screen.DASHBOARD;
        } else if(item.tag.equals(menues[1])) {
            currentScreen = Screen.SPOT;
            if(isOpenSpot)
                prepareListData(false, isOpenCoverage);
            else
                prepareListData(true, isOpenCoverage);
            setListAdapter(adapter);
        } else if(item.tag.equals(menues[2])) {
            currentScreen = Screen.COVERAGE;
            if(isOpenCoverage)
                prepareListData(isOpenSpot, false);
            else
                prepareListData(isOpenSpot, true);
            setListAdapter(adapter);
        } else if (item.tag.equals(menues[3])) {
            currentScreen = Screen.SETTINGS;
            
        } else if (item.tag.equals(menues[4])) {
            currentScreen = Screen.HELP;
            
        }
        currentMenu = item.tag;
//        Toast.makeText(getActivity(), "You clciked \""+item.tag+"\"", Toast.LENGTH_SHORT).show();
        Uri noteUri = ContentUris.withAppendedId(Uri.parse("content://com.wifi_zombie/menu"), id);
        Uri.Builder uribuilder = noteUri.buildUpon();
        uribuilder.appendQueryParameter( "menu", item.tag );
        noteUri = uribuilder.build();
        mListener.onArticleSelected(noteUri);

    }
    public interface OnArticleSelectedListener {
        public void onArticleSelected(Uri articleUri);
    }
    
    OnArticleSelectedListener mListener;
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnArticleSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }
    private class SlideMenuItem {
        public String tag;
        public int iconRes;
        public int fontSize;
        public boolean isBold;
        public SlideMenuItem(String tag, boolean isMainMenu, int iconRes) {
        	if(isMainMenu)	//	main menu
        	{
        		this.tag = tag; 
                this.iconRes = iconRes;
                this.fontSize = mainMenuFontSize;
                this.isBold = true;
        	}
        	else	//	sub menu
        	{
        		this.tag = tag; 
                this.iconRes = iconRes;
                this.fontSize = subMenuFontSize;
                this.isBold = false;
        	}
            
        }
    }

    public class SlideMenuAdapter extends ArrayAdapter<SlideMenuItem> {

        public SlideMenuAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
            }

            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);                
            icon.setImageResource(getItem(position).iconRes);
            if(getItem(position).iconRes == -1) icon.setVisibility(0);
            
            TextView title = (TextView) convertView.findViewById(R.id.row_title);
            // set font size
            title.setTextSize(getItem(position).fontSize);
            // set bold
            if(getItem(position).isBold)
            	title.setText(Html.fromHtml("<b>"+getItem(position).tag+"</b>"));
            else
            	title.setText(getItem(position).tag);

            return convertView;
        }

    }
}

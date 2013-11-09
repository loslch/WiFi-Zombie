package com.wifi_zombie;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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
    
    SlideMenuAdapter adapter;
    boolean isOpenSpot;
    boolean isOpenCoverage;
    Screen currentScreen;
    
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
        prepareListData(false, false);
        
        getListView().setOnItemClickListener(this);
    }
    
    /*
     * Preparing the list data
     */
    private void prepareListData(boolean openSpot, boolean openCoverage) {
        adapter = new SlideMenuAdapter(getActivity());
        
        /** ADD Slide Menu Items **/
        String[] slide_menu = getResources().getStringArray(R.array.slide_menu);
        String[] children_1 = getResources().getStringArray(R.array.slide_menu_1);
        String[] children_2 = getResources().getStringArray(R.array.slide_menu_2);
        adapter.add(new SlideMenuItem(slide_menu[0], android.R.drawable.ic_menu_search));
        adapter.add(new SlideMenuItem(slide_menu[1], android.R.drawable.ic_menu_mylocation));
        if(openSpot) {
            adapter.add(new SlideMenuItem(children_1[0], -1));
            adapter.add(new SlideMenuItem(children_1[1], -1));
            adapter.add(new SlideMenuItem(children_1[2], -1));
            adapter.add(new SlideMenuItem(children_1[3], -1));
        }
        adapter.add(new SlideMenuItem(slide_menu[2], android.R.drawable.ic_menu_mapmode));
        if(openCoverage) {
            adapter.add(new SlideMenuItem(children_2[0], -1));
            adapter.add(new SlideMenuItem(children_2[1], -1));
        }
        adapter.add(new SlideMenuItem(slide_menu[3], android.R.drawable.ic_menu_view));
        adapter.add(new SlideMenuItem(slide_menu[4], android.R.drawable.ic_menu_help));

        setListAdapter(adapter);
        
        isOpenSpot = openSpot;
        isOpenCoverage = openCoverage;
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final SlideMenuItem item = adapter.getItem(position);

        if(item.tag.equals("Dashboard")) {
            currentScreen = Screen.DASHBOARD;
        } else if(item.tag.equals("On the Spot Analysis")) {
            currentScreen = Screen.SPOT;
            if(isOpenSpot)
                prepareListData(false, isOpenCoverage);
            else
                prepareListData(true, isOpenCoverage);
            setListAdapter(adapter);
        } else if(item.tag.equals("Coverage Analysis")) {
            currentScreen = Screen.COVERAGE;
            if(isOpenCoverage)
                prepareListData(isOpenSpot, false);
            else
                prepareListData(isOpenSpot, true);
            setListAdapter(adapter);
        } else if (item.tag.equals("Settings")) {
            currentScreen = Screen.SETTINGS;
            
        } else if (item.tag.equals("Help")) {
            currentScreen = Screen.HELP;
            
        }
        
        Toast.makeText(getActivity(), "You clciked \""+item.tag+"\"", Toast.LENGTH_SHORT).show();
    }
    
    private class SlideMenuItem {
        public String tag;
        public int iconRes;
        public SlideMenuItem(String tag, int iconRes) {
            this.tag = tag; 
            this.iconRes = iconRes;
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
            title.setText(getItem(position).tag);

            return convertView;
        }

    }
}

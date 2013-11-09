package com.wifi_zombie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

public class ExpandableSlideMenuFragment extends Fragment {
    private ExpandableListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.expandable_list, null);

        return v;
    }

    private static final String NAME = "NAME";
    private static final String IS_EVEN = "IS_EVEN";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();

        Map<String, String> curGroupMap2 = new HashMap<String, String>();
        groupData.add(curGroupMap2);
        curGroupMap2.put(NAME, "Group " + 1);
        Map<String, String> curGroupMap3 = new HashMap<String, String>();
        groupData.add(curGroupMap3);
        curGroupMap3.put(NAME, "Group " + 2);
        
        for (int i = 4; i < 10; i++) {
            Map<String, String> curGroupMap = new HashMap<String, String>();
            groupData.add(curGroupMap);
            curGroupMap.put(NAME, "Group " + i);
            curGroupMap.put(IS_EVEN, (i % 2 == 0)?"This group is even":"This group is odd");

            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
            for (int j = 0; j < 2; j++) {
                Map<String, String> curChildMap = new HashMap<String, String>();
                children.add(curChildMap);
                curChildMap.put(NAME, "Child " + j);
                curChildMap.put(IS_EVEN, (j % 2 == 0)?"This child is even":"This child is odd");
            }
            childData.add(children);
        }
        ExpandableListView lv = (ExpandableListView) getActivity().findViewById(R.id.expandable_list);
        // Set up our adapter
        mAdapter = new SimpleExpandableListAdapter(getActivity(), groupData,
                android.R.layout.simple_expandable_list_item_1, new String[] {NAME, IS_EVEN }, new int[] { android.R.id.text1, android.R.id.text2 }, childData,
                android.R.layout.simple_expandable_list_item_2, new String[] {NAME, IS_EVEN }, new int[] { android.R.id.text1, android.R.id.text2 });
        lv.setAdapter(mAdapter);
    }
}
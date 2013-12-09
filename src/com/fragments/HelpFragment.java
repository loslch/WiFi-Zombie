package com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.wifi_zombie.R;

import source.MyFragment;

public class HelpFragment extends MyFragment{

	private View thisView;
	private RadioGroup rg;
	private RadioButton rb_aboutus, rb_contactus, rb_menual;
	private LinearLayout aboutus, contactus, menual;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.help, null);
		rg = (RadioGroup) thisView.findViewById(R.id.help_radiogroup);
		aboutus = (LinearLayout)thisView.findViewById(R.id.help_layout_aboutus);
		contactus = (LinearLayout)thisView.findViewById(R.id.help_layout_contactus);
		menual = (LinearLayout)thisView.findViewById(R.id.help_layout_menual);
		
		rb_aboutus = ((RadioButton)thisView.findViewById(R.id.help_aboutus));
		rb_contactus = ((RadioButton)thisView.findViewById(R.id.help_contactus));
		rb_menual = ((RadioButton)thisView.findViewById(R.id.help_menual));
		
		rb_aboutus.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_checked));
		rb_contactus.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
		rb_menual.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int id = group.getCheckedRadioButtonId();
				switch(id)
				{
				case R.id.help_aboutus:
					aboutus.setVisibility(View.VISIBLE);
					contactus.setVisibility(View.INVISIBLE);
					menual.setVisibility(View.INVISIBLE);
					rb_aboutus.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_checked));
					rb_contactus.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
					rb_menual.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
					break;
				case R.id.help_contactus:
					contactus.setVisibility(View.VISIBLE);
					aboutus.setVisibility(View.INVISIBLE);
					menual.setVisibility(View.INVISIBLE);
					rb_contactus.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_checked));
					rb_aboutus.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
					rb_menual.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
					break;
				case R.id.help_menual:
					menual.setVisibility(View.VISIBLE);
					contactus.setVisibility(View.INVISIBLE);
					aboutus.setVisibility(View.INVISIBLE);
					rb_menual.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_checked));
					rb_contactus.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
					rb_aboutus.setButtonDrawable(getActivity().getResources().getDrawable(R.drawable.ic_radio_unchecked));
					break;
				}
			}
		});
		
        return thisView;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
}

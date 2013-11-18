package com.wifi_zombie;

import com.fragments.SlideMenuFragment.OnArticleSelectedListener;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnClickListener {
    // service manager
    private ServiceManager wifiInfoService;
    
	private FragmentManager fmanager = FragmentManager.getInstance();
	private FragmentTransaction mainT;
	private String currentScreen;
    private TextView txtView;
    //protected ListFragment mFrag;

	public MainActivity() {
		super(R.string.hello_world);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mainT = this.getSupportFragmentManager().beginTransaction();
		mainT.replace(R.id.activity_main, fmanager.getFragmentByMenu("Dashboard"));
    	mainT.commit();
		currentScreen = "Dashboard";
		// set the Above View
//		setContentView(R.layout.activity_main);
//		getSupportFragmentManager()
//		.beginTransaction()
//		.replace(R.id.content_frame, new SampleListFragment())
//		.commit();
		
		// service에서 보낸 message handler 이거를 통해서 각 fragment에 값 뿌려줌
		wifiInfoService = new ServiceManager(this, WifiInfoManager.class, new Handler() {
		    @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:                
                        break;
                } 
            }
        });
        
        
        setSlidingActionBarEnabled(true);
    }
    public void onArticleSelected(Uri articleUri){
    	String clickedMenu = articleUri.getQueryParameter("menu");
    	
    	if(!(clickedMenu.equals("On the Spot Analysis") || clickedMenu.equals("Coverage Analysis")))
    	{
    		getSlidingMenu().toggle(true);
    		if(!currentScreen.equals(clickedMenu))
        	{	
    			mainT = this.getSupportFragmentManager().beginTransaction();
        		mainT.replace(R.id.activity_main, fmanager.getFragmentByMenu(clickedMenu));
        		mainT.commit();
        	}
        	currentScreen = clickedMenu;
    	}
    }
    
    @Override
    public void onClick(View v) {
        this.setTitle(txtView.getText());
    }
    
}

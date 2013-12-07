package com.wifi_zombie;

import source.MyFragment;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.data.WifiInfoData;
import com.fragments.FragmentManager;

public class MainActivity extends BaseActivity implements OnClickListener {
	
	// service manager var
	private WifiService wifiService;
	// wifi info data var
	private WifiInfoData wifiInfoData;
	
	private FragmentManager fmanager = FragmentManager.getInstance();
	private FragmentTransaction mainT;
	private String currentScreen;	// 현재 fragment 
	private MyFragment currentFragment;
    private TextView txtView;
    
    private boolean isPuased = false;
    //protected ListFragment mFrags
    private Handler fromServiceHandler = new Handler() {	//	service에서 보낸 메세지 처리 handler포함하는 messenger
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
//			Log.i("wifi zombie", "service recieve?");
            switch (msg.what) {
	            case WifiService.MSG_UPDATE_INFO:    
	            	Toast.makeText(getApplicationContext(), "WIFI STATUS CAHNGED", Toast.LENGTH_SHORT).show();
	            	if(!isPuased)
	            		currentFragment.updateWifiData();
	            	// 업데이트 되면 WifiInfoData에서 정보를 들고오자
//	            	Log.i("wifi zombie", "service OOOOOOKKKKKKK");
	                break;
            } 	
		}
	};
    private Messenger ToServiceMessenger;
    private Messenger FromServiceMessenger = new Messenger(fromServiceHandler);
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
//			Log.i("wifi zombie", "onServiceConnected success");
			ToServiceMessenger = new Messenger(service);
			try {
				Message msg = Message.obtain(null, WifiService.MSG_REGISTER_CLIENT, 0, 0);
				msg.replyTo = FromServiceMessenger;
				ToServiceMessenger.send(msg);
//				Log.i("wifi zombie", "send message to service success");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
//				Log.i("wifi zombie", "send message to service failed");
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			FromServiceMessenger = null;
		}
	};

	public MainActivity() {
		super(R.string.hello_world);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBar(R.layout.actionbar);
		setContentView(R.layout.activity_main);
		
		wifiInfoData = WifiInfoData.getInstance();
		// service에서 wifi 정보 업데이트 여부를 받아오는 리스너 등록
		Intent lIntent = new Intent(MainActivity.this, WifiService.class);
		startService(lIntent);
		bindService(new Intent(MainActivity.this, WifiService.class), mConnection, Context.BIND_AUTO_CREATE);
		
		// service start
//		Intent lIntent = new Intent(MainActivity.this, WifiService.class);
//		lIntent.putExtra("Messenger", mActivityMessenger);
//		startService(lIntent);
//		try {          
//			 wifiService.send(Message.obtain(null, AbstractService.MSG_REGISTER_CLIENT, 0, 0));
//	        } 
//	        catch (RemoteException e) {
//	        	Log.i("wifi zombie", "아놔ㅡㅡ");
//	        }
		
		mainT = this.getSupportFragmentManager().beginTransaction();
		mainT.replace(R.id.activity_main, fmanager.getFragmentByMenu("Dashboard"));
    	mainT.commit();
		currentScreen = "Dashboard";
		currentFragment = (MyFragment)fmanager.getFragmentByMenu("Dashboard");
		// set the Above View
//		setContentView(R.layout.activity_main);
//		getSupportFragmentManager()
//		.beginTransaction()
//		.replace(R.id.content_frame, new SampleListFragment())
//		.commit();
        
        setSlidingActionBarEnabled(true);
//        Log.i("wifi zombie", "onCreate");        
    }
    
    private void setActionBar(int layout)
    {
    	ActionBar action = this.getActionBar();
    	action.setDisplayShowCustomEnabled(true);
    	action.setDisplayShowHomeEnabled(false);
    	action.setDisplayShowTitleEnabled(false);
    	action.setCustomView(layout);
    	// slide menu icon click
    	((ImageView)findViewById(R.id.actionbar_img_app)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getSlidingMenu().toggle(true);	
			}
		});
    	((ImageView)findViewById(R.id.actionbar_img_app)).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch(action)
				{
				case MotionEvent.ACTION_DOWN:
					((ImageView)v).setColorFilter(0xaa111111, Mode.SRC_OVER);
					break;
				case MotionEvent.ACTION_UP:
					((ImageView)v).setColorFilter(0x00000000, Mode.SRC_OVER);
					break;
				}
				return false;
			}
		});
    	// refresh icon click
    	((ImageView)findViewById(R.id.actionbar_img_refresh)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Message msg = Message.obtain(null, WifiService.MSG_REFRESH, 0, 0);
					msg.replyTo = FromServiceMessenger;
					ToServiceMessenger.send(msg);
//					Log.i("wifi zombie", "send message to service success");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
//					Log.i("wifi zombie", "send message to service failed");
				}			
			}
		});
    	((ImageView)findViewById(R.id.actionbar_img_refresh)).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch(action)
				{
				case MotionEvent.ACTION_DOWN:
					((ImageView)v).setColorFilter(0xaa111111, Mode.SRC_OVER);
					break;
				case MotionEvent.ACTION_UP:
					((ImageView)v).setColorFilter(0x00000000, Mode.SRC_OVER);
					break;
				}
				return false;
			}
		});
    }
    
    public void onArticleSelected(Uri articleUri){	//slide menu fragment에서 클릭 정보 받아옴 Uri로
    	String clickedMenu = articleUri.getQueryParameter("menu");
    	
    	if(!(clickedMenu.equals("On the Spot Analysis") || clickedMenu.equals("Coverage Analysis")))
    	{
    		getSlidingMenu().toggle(true);
    		if(!currentScreen.equals(clickedMenu))
        	{	
    			mainT = this.getSupportFragmentManager().beginTransaction();
        		mainT.replace(R.id.activity_main, fmanager.getFragmentByMenu(clickedMenu));
        		mainT.commit();
        		currentFragment = (MyFragment)fmanager.getFragmentByMenu(clickedMenu);
        	}
        	currentScreen = clickedMenu;
    	}
    	if(clickedMenu.equals("Outdoor Survey")) {
    		getSlidingMenu().setSlidingEnabled(false);
    	} else {
    		getSlidingMenu().setSlidingEnabled(true);
    	}
    }
    
    @Override
    public void onClick(View v) {
        this.setTitle(txtView.getText());
    }
    @Override
    protected void onResume() {
//    	Log.i("wifi zombie", "onResume");
    	isPuased = false;
        super.onResume();
    }
    @Override
    protected void onPause() {
    	isPuased = true;
        super.onPause();
        unbindService(mConnection);
    }
    @Override
    protected void onDestroy() {	// 어플 끝나면 unbind함
        super.onDestroy();
        try {
        	isPuased = true;
        	unbindService(mConnection);
        	wifiService.onDestroy();
        } catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }
    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.main, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_menu_refresh:
//        	Toast.makeText(this, "Refresh was clicked", Toast.LENGTH_SHORT).show();
        	try {
				Message msg = Message.obtain(null, WifiService.MSG_REFRESH, 0, 0);
				msg.replyTo = FromServiceMessenger;
				ToServiceMessenger.send(msg);
//				Log.i("wifi zombie", "send message to service success");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
//				Log.i("wifi zombie", "send message to service failed");
			}
        	break;
        case R.id.action_menu_slidemenu:
        	
        	break;
        }
        
    	return super.onOptionsItemSelected(item);
    }
  
}

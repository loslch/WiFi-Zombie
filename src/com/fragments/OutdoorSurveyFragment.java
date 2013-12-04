package com.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.data.WifiInfoData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.wifi_zombie.R;
import com.wifi_zombie.WifiZombieProto.WifiInfo;
import com.wifi_zombie.WifiZombieProto.WifiSurvey;
import com.wifi_zombie.WifiZombieProto.WifiSurvey.SurveyType;
import com.wifi_zombie.WifiZombieProto.WifiSurvey.WifiItem;
import com.wifi_zombie.WifiZombieProto.WifiSurvey.WifiItem.Position;

public class OutdoorSurveyFragment extends MyFragment implements LocationListener, LocationSource, OnClickListener, OnMarkerClickListener {
	private GoogleMap mMap = null;
	
    private OnLocationChangedListener mListener;
    private LocationManager locationManager;
    
    private View inflater;
    private Button btnSaveCurrentAPs; 
    private Context context;
    
    private List<WifiItem> lstWifiItem;
    private Point currentLocation;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater.inflate(R.layout.activity_map, null, false);
		return this.inflater;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Reload Option Menu
		setHasOptionsMenu(true);
		
		// Initialize & Bind Something
		initialize();
        
		setUpMapIfNeeded();
	}
	
	private void initialize() {
		context = getActivity().getApplicationContext();
		
		// Bind Layout & Event
		btnSaveCurrentAPs = (Button) inflater.findViewById(R.id.btnSaveCurrentAPs);
		btnSaveCurrentAPs.setOnClickListener(this);
        
		// Initialize List
        lstWifiItem = new ArrayList<WifiItem>();

        // Initialize Location Manager
        locationManager = (LocationManager) 
				getActivity().getSystemService(Context.LOCATION_SERVICE);
        
        if(locationManager != null)
        {
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
             
            if(gpsIsEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
            }
            else if(networkIsEnabled)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
            }
            else
            {
                //Show an error dialog that GPS is disabled...
            }
        }
        else
        {
            //Show some generic error dialog because something must have gone wrong with location manager.
        }
        
        // Initialize Current Location
        currentLocation = new Point();
        double[] latlng = getCurrentlocation();
        currentLocation.lat = latlng[0];
        currentLocation.lng = latlng[1];
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setOnMarkerClickListener(this);
		        mMap.setMyLocationEnabled(true);
				moveToCurrentLocation();
			}
 
            //This is how you register the LocationSource
            mMap.setLocationSource(this);
		}
	}
 
    @Override
    public void onPause()
    {
        if(locationManager != null)
        {
            locationManager.removeUpdates(this);
        }
         
        super.onPause();
    }
	
	@Override
	public void onResume() {
	    super.onResume();
        
       if(locationManager != null)
       {
           mMap.setMyLocationEnabled(true);
       }
	}

	@Override
	public void onDestroyView() {
	    super.onDestroyView();

	    Fragment f = getFragmentManager().findFragmentById(R.id.map);
	    if (f != null) {
	        getFragmentManager().beginTransaction().remove(f).commit();
	        mMap = null;
	    }
	}
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.outdoor, menu);
        return;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_outdoor_new:
            Toast.makeText(context, "New Menu selected", Toast.LENGTH_SHORT).show();
            break;
        case R.id.action_outdoor_save:
            Toast.makeText(context, "Save Menu selected", Toast.LENGTH_SHORT).show();
            break;
        case R.id.action_outdoor_load:
            Toast.makeText(context, "Load Menu selected", Toast.LENGTH_SHORT).show();
            break;
        case R.id.action_outdoor_heatmap:
            Toast.makeText(context, "Heatmap Menu selected", Toast.LENGTH_SHORT).show();
            break;
        case R.id.action_outdoor_ssid:
            Toast.makeText(context, "SSID Menu selected", Toast.LENGTH_SHORT).show();
            break;
        default:
        	break;
        }

        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnSaveCurrentAPs:
			storeCurrentWifiItem();
    		Toast.makeText(context, "저장", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}		
	}
	
	@Override
	public boolean onMarkerClick(Marker marker) {
		Toast.makeText(context, marker.getSnippet(), Toast.LENGTH_SHORT).show();
		
		return false;
	}

	private void storeCurrentWifiItem() {
		WifiItem currentWifiItem = getWifiItem();
		lstWifiItem.add(currentWifiItem);
		
		Point Share = new Point();
		Share.lat = currentWifiItem.getPosition().getX();
		Share.lng = currentWifiItem.getPosition().getY();

		// Bubble Icon 생성
		IconGenerator mIconGenerator = new IconGenerator(context);
		mIconGenerator.setStyle(mIconGenerator.STYLE_RED);
		Bitmap iconBitmap = mIconGenerator.makeIcon(String.valueOf(lstWifiItem.size()));
		
		mMap.addMarker(new MarkerOptions()
		.snippet("#"+String.valueOf(lstWifiItem.size()))
		.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
		.position(new LatLng(Share.lat, Share.lng)));
	}
	
	private WifiItem getWifiItem() {
		/* Wifi Info를 가져와서 위치 정보를 가진 Wifi Item으로 만든다 */
		WifiInfoData wifiInfoData = super.wifidata;
		WifiInfo wifiInfo = wifiInfoData.getWifiInfo();
		WifiItem wifiItem = WifiItem.newBuilder()
				.setPosition(Position.newBuilder()
						.setX(currentLocation.lat)
						.setY(currentLocation.lng)
						.build())
				.setWifiInfo(wifiInfo)
				.build();
		
		return wifiItem;
	}
	
	private WifiSurvey generateWifiSurvey() {
		/* 다수의 Wifi Item과 Survey정보를 가진 Wifi Survey 생성 */
		WifiSurvey.Builder wifiSurveyBuilder = WifiSurvey.newBuilder();
		wifiSurveyBuilder.setTitle("title")
			.setCreatedTime(0)
			.setCreator("creator")
			.setSurveyType(SurveyType.OUTDOOR);
		for (WifiItem item : lstWifiItem) {
			wifiSurveyBuilder.addWifiItemList(item);
		}
		
		return wifiSurveyBuilder.build();
	}

	private class Point {
		public double lat = 0;
		public double lng = 0;
	}

	private void moveToCurrentLocation() {
//		Share.lat = 37.5665;	// SEOUL
//		Share.lng = 126.9780;
		
//		mMap.addMarker(new MarkerOptions()
//				.position(new LatLng(Share.lat, Share.lng))
//				.title("Current Location"));

		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				currentLocation.lat, currentLocation.lng), 17));
	}

	public double[] getCurrentlocation() {
		List<String> providers = locationManager.getProviders(true);

		Location l = null;
		for (int i = 0; i < providers.size(); i++) {
			l = locationManager.getLastKnownLocation(providers.get(i));
			if (l != null)
				break;
		}
		double[] gps = new double[2];

		if (l != null) {
			gps[0] = l.getLatitude();
			gps[1] = l.getLongitude();
		}
		return gps;
	}

	
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
	}

	@Override
	public void deactivate() {
		mListener = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		/* GPS 위치가 변경되면 멤버 변수에 적용 */
		currentLocation.lat = location.getLatitude();
		currentLocation.lng = location.getLongitude();
		
		if (mListener != null) {
			mListener.onLocationChanged(location);

	        LatLngBounds bounds = this.mMap.getProjection().getVisibleRegion().latLngBounds;
	 
	        if(!bounds.contains(new LatLng(location.getLatitude(), location.getLongitude())))
	        {
	             //Move the camera to the user's location once it's available!
	             mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "provider disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "provider enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "status changed", Toast.LENGTH_SHORT).show();
	}
}

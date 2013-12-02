package com.fragments;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.wifi_zombie.R;

public class OutdoorSurveyFragment extends MyFragment implements LocationListener, LocationSource {
	private GoogleMap mMap = null;
	
    private OnLocationChangedListener mListener;
    private LocationManager locationManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_map, null, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

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
        
		setUpMapIfNeeded();
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

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
 
            //This is how you register the LocationSource
            mMap.setLocationSource(this);
		}
	}

	private void setUpMap() {
        mMap.setMyLocationEnabled(true);
		getCurrentLocation();
	}

	private class Point {
		public double lat = 0;
		public double lng = 0;
	}

	private void getCurrentLocation() {
		Point Share = new Point();
		double[] d = getlocation();
		Share.lat = d[0];
		Share.lng = d[1];

//		Share.lat = 37.5665;	// SEOUL
//		Share.lng = 126.9780;
		
//		mMap.addMarker(new MarkerOptions()
//				.position(new LatLng(Share.lat, Share.lng))
//				.title("Current Location"));

		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				Share.lat, Share.lng), 17));
	}

	public double[] getlocation() {
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
   public void activate(OnLocationChangedListener listener) 
   {
       mListener = listener;
   }
    
   @Override
   public void deactivate() 
   {
       mListener = null;
   }

   @Override
   public void onLocationChanged(Location location) 
   {
	    if( mListener != null )
	    {
	        mListener.onLocationChanged( location );
	 
	        LatLngBounds bounds = this.mMap.getProjection().getVisibleRegion().latLngBounds;
	 
	        if(!bounds.contains(new LatLng(location.getLatitude(), location.getLongitude())))
	        {
	             //Move the camera to the user's location once it's available!
	             mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
	        }
	    }
   }

   @Override
   public void onProviderDisabled(String provider) 
   {
       // TODO Auto-generated method stub
       Toast.makeText(getActivity().getApplicationContext(), "provider disabled", Toast.LENGTH_SHORT).show();
   }

   @Override
   public void onProviderEnabled(String provider) 
   {
       // TODO Auto-generated method stub
       Toast.makeText(getActivity().getApplicationContext(), "provider enabled", Toast.LENGTH_SHORT).show();
   }

   @Override
   public void onStatusChanged(String provider, int status, Bundle extras) 
   {
       // TODO Auto-generated method stub
       Toast.makeText(getActivity().getApplicationContext(), "status changed", Toast.LENGTH_SHORT).show();
   }
}

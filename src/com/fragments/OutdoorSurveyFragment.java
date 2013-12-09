package com.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import source.MyFragment;
import source.WifiDataListAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.data.WifiInfoData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.wifi_zombie.FileDialog;
import com.wifi_zombie.R;
import com.wifi_zombie.SelectionMode;
import com.wifi_zombie.WifiZombieProto.WifiInfo;
import com.wifi_zombie.WifiZombieProto.WifiInfo.WifiData;
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
    
    /* 서베이 정보 */
    private String surveyTitle;
    private String surveyCreator;
    private Calendar surveyCreatedTime;
    private SurveyType surveyType;
    private List<WifiItem> lstWifiItem;
    
    private boolean enableHeatmap;
    private boolean hasChanged;
    private String pathFromFileDialog;
    private final int REQUEST_NEW = 0;
    private final int REQUEST_SAVE = 1;
    private final int REQUEST_LOAD = 2;
    
    private Point currentLocation;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater.inflate(R.layout.fragment_outdoor, null, false);
		return this.inflater;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Reload Option Menu
		setHasOptionsMenu(true);
		
		// Initialize & Bind Something
		initialize();
		
		// Restore state
		onRestoreInstanceState();
        
		setUpMapIfNeeded();
	}
	
	public void onRestoreInstanceState() {
    	String filePath = "/sdcard/Application/wifi-zombie/wifi-zombie-outdoor-auto-save";
    	WifiSurvey wifiSurvey = null;
    	File file = new File(filePath);
    	FileInputStream fis;
    	
    	if(!file.exists()) {
    		return;
    	}
    	
    	try {
    		fis = new FileInputStream(file);
        	wifiSurvey = WifiSurvey.parseFrom(fis);
    		fis.close();
    		file.delete();
    	} 
    	catch (FileNotFoundException e) {} 
    	catch (IOException e) {}
		
		if(wifiSurvey != null) {
			if(wifiSurvey != null && wifiSurvey.getWifiItemListCount()>0) {
				surveyTitle = wifiSurvey.getTitle();
				surveyCreator = wifiSurvey.getCreator();
				surveyCreatedTime = Calendar.getInstance();
				surveyCreatedTime.setTimeInMillis(wifiSurvey.getCreatedTime());
				surveyType = wifiSurvey.getSurveyType();
				lstWifiItem = new ArrayList<WifiItem>(wifiSurvey.getWifiItemListList());
			}
		}
	}
    
	private void initialize() {
		enableHeatmap = true;
		hasChanged = false;
		context = getActivity().getApplicationContext();
		
		// Bind Layout & Event
		btnSaveCurrentAPs = (Button) inflater.findViewById(R.id.btnSaveCurrentAPs);
		btnSaveCurrentAPs.setOnClickListener(this);

        // Initialize Location Manager
        locationManager = (LocationManager) 
				getActivity().getSystemService(Context.LOCATION_SERVICE);
        
        // Initialize Current Location
        currentLocation = new Point();
        double[] latlng = getCurrentlocation();
        currentLocation.lat = latlng[0];
        currentLocation.lng = latlng[1];
        
        // Initialize Local variables
        surveyTitle = new String();
        surveyCreator = new String();
        surveyCreatedTime = Calendar.getInstance();
        surveyType = SurveyType.OUTDOOR;
        lstWifiItem = new ArrayList<WifiItem>();
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
				restoreMarker();
			}
 
            //This is how you register the LocationSource
            mMap.setLocationSource(this);
		}
	}
	
	private void restoreMarker() {
		if(lstWifiItem.size() > 0) {
			for (int i=0; i<lstWifiItem.size(); i++) {
				Point point = new Point();
				point.lat = lstWifiItem.get(i).getPosition().getX();
				point.lng = lstWifiItem.get(i).getPosition().getY();
				markOnMap(lstWifiItem.get(i), point, String.valueOf(i+1));
			}
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
           
           mMap.setMyLocationEnabled(true);
       }
	}

	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	    
    	String filePath = "/sdcard/Application/wifi-zombie/wifi-zombie-outdoor-auto-save";
    	byte[] surveyStream = generateWifiSurvey().toByteArray();
    	File file = new File(filePath);
    	FileOutputStream fos;
    	try {
    		file.createNewFile();
    	    fos = new FileOutputStream(file);
    	    fos.write(surveyStream);
    	    fos.flush();
    	    fos.close();
    	    hasChanged = false;
    	}
    	catch (FileNotFoundException e) {}
    	catch (IOException e) {}

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
        	discardSurveyData(REQUEST_NEW);
//        	newSurvey();
            break;
        case R.id.action_outdoor_save:
			if(surveyTitle.isEmpty()) {
				Toast.makeText(context, "You have to create new survey first.", Toast.LENGTH_SHORT).show();
				return false;
			}
        	openFileDialog(this.REQUEST_SAVE);
//        	saveSurvey();
            break;
        case R.id.action_outdoor_load:
        	discardSurveyData(REQUEST_LOAD);
//        	openFileDialog(this.REQUEST_LOAD);
//        	loadSurvey();
            break;
        case R.id.action_outdoor_heatmap:
        	if(enableHeatmap) {
        		enableHeatmap = false;
        		item.setTitle("Enable Heatmap");
        	} else {
        		enableHeatmap = true;
        		item.setTitle("Disable Heatmap");
        	}
        	mMap.clear();
        	restoreMarker();
            break;
//        case R.id.action_outdoor_ssid:
//        	selectCertainSSID();
//            break;
        default:
        	break;
        }

        return super.onOptionsItemSelected(item);
    }
    
    private void newSurvey() {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    	final View view = inflater.inflate(R.layout.dialog_new_survey, null);
    	
    	final EditText etSurveyTitle = (EditText) view.findViewById(R.id.etSurveyTitle);
    	final EditText etSurveyCreator = (EditText) view.findViewById(R.id.etSurveyCreator);
    	
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.icon);
        builderSingle.setTitle("Create New Survey");
        builderSingle.setView(view);
        builderSingle.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	if(etSurveyTitle.getText().toString().isEmpty() ||
            		etSurveyCreator.getText().toString().isEmpty()) {
            		Toast.makeText(context, "You must put the title and creator.", Toast.LENGTH_SHORT).show();
            		return;
            	}
            	surveyTitle = etSurveyTitle.getText().toString();
            	surveyCreator = etSurveyCreator.getText().toString();
            	surveyCreatedTime = Calendar.getInstance();
            	surveyType = SurveyType.OUTDOOR;
            	lstWifiItem = new ArrayList<WifiItem>();
        		mMap.clear();
        		hasChanged = true;
                dialog.dismiss();
            }
        });
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }
    
    private void openFileDialog(int requestCode) {
    	// create a File object for the parent directory
    	File zombieDirectory = new File("/sdcard/Application/wifi-zombie");
    	// have the object build the directory structure, if needed.
    	zombieDirectory.mkdirs();
    	
    	Intent intent = new Intent(context, FileDialog.class);
    	intent.putExtra(FileDialog.START_PATH, "/sdcard/Application/wifi-zombie");
        
        //can user select directories or not
        intent.putExtra(FileDialog.CAN_SELECT_DIR, true);
        
        //alternatively you can set file filter
        intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "zombie" });
        
        if(requestCode == this.REQUEST_SAVE) {
        	intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);
        } else {
        	intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
        }
        
        startActivityForResult(intent, requestCode);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            pathFromFileDialog = data.getStringExtra(FileDialog.RESULT_PATH);
            
            if (requestCode == REQUEST_SAVE) {
	            pathFromFileDialog.trim();
	            String[] filepath = pathFromFileDialog.split("/");
	            String filename = filepath[filepath.length-1];
	            if(filename.length() < 1 || !filename.matches("^[a-zA-Z0-9[.][_][-]]+")) {
	            	Toast.makeText(context, "File name is not allowed.", Toast.LENGTH_SHORT).show();
	            	return;
	            }
	            
	            if(!pathFromFileDialog.endsWith(".zombie")) {
	            	pathFromFileDialog = pathFromFileDialog.concat(".zombie");
	            }
            	saveSurvey();
            } else if (requestCode == REQUEST_LOAD) {
            	loadSurvey();
            }

        } else if (resultCode == getActivity().RESULT_CANCELED) {
        	pathFromFileDialog = "";
        }

    }
    
    private void saveSurvey() {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    	final View view = inflater.inflate(R.layout.dialog_save_survey, null);
    	
    	final TextView tvSavePath = (TextView) view.findViewById(R.id.tvSavePath);
    	tvSavePath.setText("FILE NAME : " + pathFromFileDialog);
    	
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.icon);
        builderSingle.setTitle("Export Survey Data");
        builderSingle.setView(view);
        builderSingle.setPositiveButton("Export", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	String filePath = pathFromFileDialog;
            	byte[] surveyStream = generateWifiSurvey().toByteArray();
            	File file = new File(filePath);
            	FileOutputStream fos;
            	try {
            	    fos = new FileOutputStream(file);
            	    fos.write(surveyStream);
            	    fos.flush();
            	    fos.close();
            	    hasChanged = false;
            	    
	            	Toast.makeText(context, "Exporting Survey Data is successed", Toast.LENGTH_SHORT).show();
            	} catch (FileNotFoundException e) {
            	    // handle exception
	            	Toast.makeText(context, "Exporting Survey Data is failed", Toast.LENGTH_SHORT).show();
            	} catch (IOException e) {
            	    // handle exception
	            	Toast.makeText(context, "Exporting Survey Data is failed", Toast.LENGTH_SHORT).show();
            	}

                dialog.dismiss();
            }
        });
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }
    
    private void loadSurvey() {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    	final View view = inflater.inflate(R.layout.dialog_load_survey, null);
    	
    	final TextView tvLoadPath = (TextView) view.findViewById(R.id.tvLoadPath);
    	tvLoadPath.setText("FILE NAME : " + pathFromFileDialog);
    	
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.icon);
        builderSingle.setTitle("Import Exist Survey");
        builderSingle.setView(view);
        builderSingle.setPositiveButton("Import", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	String filePath = pathFromFileDialog;
            	WifiSurvey wifiSurvey = null;
            	File file = new File(filePath);
            	FileInputStream fis;
            	try {
            		fis = new FileInputStream(file);
                	wifiSurvey = WifiSurvey.parseFrom(fis);
            		fis.close();
            	} catch (FileNotFoundException e) {
            	    // handle exception
	            	Toast.makeText(context, "Importing Survey Data is failed", Toast.LENGTH_SHORT).show();
            	} catch (IOException e) {
            	    // handle exception
	            	Toast.makeText(context, "Importing Survey Data is failed", Toast.LENGTH_SHORT).show();
            	}
            	
            	if(wifiSurvey != null) {
            		if(wifiSurvey.getSurveyType() != SurveyType.OUTDOOR) {
            			Toast.makeText(context, "Importing Survey Data is failed", Toast.LENGTH_SHORT).show();
            			return;
            		}
            		mMap.clear();
            		hasChanged = false;
	            	surveyTitle = wifiSurvey.getTitle();
	            	surveyCreator = wifiSurvey.getCreator();
	            	surveyCreatedTime = Calendar.getInstance();
	            	surveyCreatedTime.setTimeInMillis(wifiSurvey.getCreatedTime());
					surveyType = wifiSurvey.getSurveyType();
	            	lstWifiItem = new ArrayList<WifiItem>(wifiSurvey.getWifiItemListList());
	            	restoreMarker();

	            	Toast.makeText(context, "Importing Survey Data is successed", Toast.LENGTH_SHORT).show();
            	}
            	
                dialog.dismiss();
            }
        });
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }
    
    private void selectCertainSSID() {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.icon);
        builderSingle.setTitle("Select Certain SSID");
        builderSingle.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	surveyTitle = "";
            	surveyCreator = "";
            	surveyCreatedTime = Calendar.getInstance();
                dialog.dismiss();
            }
        });
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }
    
    private void discardSurveyData(final int request) {
    	if(hasChanged) {
			AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
	        builderSingle.setIcon(R.drawable.icon);
	        builderSingle.setTitle("Discard Survey Data");
	        builderSingle.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	switch (request) {
					case REQUEST_NEW:
						newSurvey();
						break;
					case REQUEST_LOAD:
						openFileDialog(request);
						break;
					}
	                dialog.dismiss();
	            }
	        });
	        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	            }
	        });
	        builderSingle.show();
    	} else {
        	switch (request) {
			case REQUEST_NEW:
				newSurvey();
				break;
			case REQUEST_LOAD:
				openFileDialog(request);
				break;
			}
    	}
    }
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnSaveCurrentAPs:
			if(surveyTitle.isEmpty()) {
				Toast.makeText(context, "You have to create new survey first.", Toast.LENGTH_SHORT).show();
				return;
			}
			storeCurrentWifiItem();
			break;

		default:
			break;
		}		
	}
	
	@Override
	public boolean onMarkerClick(final Marker marker) {
		final int wifiDataIndex = Integer.parseInt(marker.getSnippet())-1;
		WifiInfo wifiInfo = lstWifiItem.get(wifiDataIndex).getWifiInfo();		
        WifiDataListAdapter adapter = new WifiDataListAdapter(getActivity(), R.layout.aplist_item, wifiInfo.getWifiDataList());

		AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.icon);
        builderSingle.setTitle("Wireless Networks");
        builderSingle.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuffer wifiInfoStr = new StringBuffer();
                WifiData data = lstWifiItem.get(wifiDataIndex).getWifiInfo().getWifiData(which);
                wifiInfoStr.append("SSID: ").append(data.getSsid()).append("\n")
			    			.append("BSSID: ").append(data.getBssid()).append("\n")
			    			.append("Channel: ").append(data.getChannel()).append("\n")
			    			.append("Bandwidth: ").append(data.getBandwidth()).append("\n")
			    			.append("Strength: ").append(data.getStrength()).append("\n")
			    			.append("Security: ").append(data.getSecurity());
                AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                builderInner.setMessage(wifiInfoStr.toString());
                builderInner.setTitle(data.getSsid());
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	int index = Integer.parseInt(marker.getSnippet())-1;
            	lstWifiItem.remove(index);
            	mMap.clear();
				restoreMarker();
                dialog.dismiss();
            }
        });
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.show();
				
		return false;
	}

	private void storeCurrentWifiItem() {
		WifiItem currentWifiItem = getWifiItem();
		lstWifiItem.add(currentWifiItem);
		
		Point point = new Point();
		point.lat = currentWifiItem.getPosition().getX();
		point.lng = currentWifiItem.getPosition().getY();
		
		markOnMap(currentWifiItem, point, String.valueOf(lstWifiItem.size()));
		hasChanged = true;
	}
	
	private void markOnMap(WifiItem wifiItem, Point point, String name) {
		// Bubble Icon 생성
		IconGenerator mIconGenerator = new IconGenerator(context);
		mIconGenerator.setStyle(mIconGenerator.STYLE_PURPLE);
		Bitmap iconBitmap = mIconGenerator.makeIcon(name);
		
		mMap.addMarker(new MarkerOptions()
		.snippet(name)
		.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
		.position(new LatLng(point.lat, point.lng)));

		if(enableHeatmap) {
			addPoint(wifiItem, point);
		}
	}
	 
	public void addPoint(WifiItem wifiItem, Point point) {
		int maxStrength = (int) (Math.abs(getMaxStrength(wifiItem)) % 100);

		// Instantiates a new CircleOptions object and defines the center and radius
		CircleOptions circleOptions = new CircleOptions()
		    .center(new LatLng(point.lat, point.lng))
		    .radius(maxStrength)
		    .fillColor(Color.argb(maxStrength, 219, 46, 15))
		    .strokeColor(Color.TRANSPARENT); //dont show the border to the circle
		// Get back the mutable Circle
		mMap.addCircle(circleOptions);
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
	
	private int getMaxStrength(WifiItem wifiItem) {
		int max = -99;
		WifiInfo wifiInfo = wifiItem.getWifiInfo();
		
		for (WifiData data : wifiInfo.getWifiDataList()) {
			if(data.getStrength() > max) {
				max = data.getStrength();
			}
		}
		
		return max;
	}
	
	private WifiSurvey generateWifiSurvey() {
		/* 다수의 Wifi Item과 Survey정보를 가진 Wifi Survey 생성 */
		WifiSurvey.Builder wifiSurveyBuilder = WifiSurvey.newBuilder();
		wifiSurveyBuilder.setTitle(surveyTitle)
			.setCreatedTime(surveyCreatedTime.getTimeInMillis())
			.setCreator(surveyCreator)
			.setSurveyType(surveyType.OUTDOOR);
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
//		point.lat = 37.5665;	// SEOUL
//		point.lng = 126.9780;
		
//		mMap.addMarker(new MarkerOptions()
//				.position(new LatLng(point.lat, point.lng))
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
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
//		Toast.makeText(context, "provider disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
//		Toast.makeText(context, "provider enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
//		Toast.makeText(context, "status changed", Toast.LENGTH_SHORT).show();
	}
}

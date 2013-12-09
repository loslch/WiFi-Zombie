package com.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import source.GLSurf;
import source.IndoorMarker;
import source.IndoorMarkerOptions;
import source.MyFragment;
import source.OnIndoorMarkerClickListener;
import source.WifiDataListAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.data.WifiInfoData;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
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

public class IndoorSurveyFragment extends MyFragment implements OnClickListener, OnIndoorMarkerClickListener { 
	
    // Cocos-2d Surfaceview
	protected GLSurf _glSurfaceView;
    private FrameLayout surfaceFrame;
    
    private View inflater;
    private Button btnSaveCurrentAPs; 
    private Context context;
    
    /* 서베이 정보 */
    private String surveyTitle;
    private String surveyCreator;
    private Calendar surveyCreatedTime;
    private SurveyType surveyType;
    private String surveyImportedImage;
    private List<WifiItem> lstWifiItem;
    
    private boolean enableHeatmap;
    private boolean hasChanged;
    private String pathFromFileDialog;
    private final int REQUEST_NEW = 0;
    private final int REQUEST_SAVE = 1;
    private final int REQUEST_LOAD = 2;
    private final int ID_GLVIEW = 0x0001;
    
    private Point currentLocation;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater.inflate(R.layout.fragment_indoor, null, false);
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
    	String filePath = "/sdcard/Application/wifi-zombie/wifi-zombie-indoor-auto-save";
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
				surveyImportedImage = wifiSurvey.getImagePath();
				_glSurfaceView.setImportedImage(surveyImportedImage);
				surveyType = wifiSurvey.getSurveyType();
				lstWifiItem = new ArrayList<WifiItem>(wifiSurvey.getWifiItemListList());
				
			}
		}
	}
    
	private void initialize() {
		hasChanged = false;
		context = getActivity().getApplicationContext(); 
		
        // We create our Surfaceview for our OpenGL here.
		_glSurfaceView = new GLSurf(getActivity());
		_glSurfaceView.setId(ID_GLVIEW);
        surfaceFrame = (FrameLayout) inflater.findViewById(R.id.surfaceFrame);
        surfaceFrame.addView(_glSurfaceView);
		
		// Bind Layout & Event
		btnSaveCurrentAPs = (Button) inflater.findViewById(R.id.btnSaveCurrentAPs);
		btnSaveCurrentAPs.setOnClickListener(this);
        
        // Initialize Current Location
        currentLocation = new Point();
        
        // Initialize Local variables
        surveyTitle = new String();
        surveyCreator = new String();
        surveyCreatedTime = Calendar.getInstance();
		surveyImportedImage = new String();
        surveyType = SurveyType.INDOOR;
        lstWifiItem = new ArrayList<WifiItem>();
	}

	private void setUpMapIfNeeded() {
		if(surveyTitle != null && !surveyTitle.isEmpty())
			return;
		
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.icon);
        builderSingle.setTitle("New Survey");
        builderSingle.setPositiveButton("Load Survey", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	openFileDialog(REQUEST_LOAD);
                dialog.dismiss();
            }
        });
        builderSingle.setNegativeButton("New Survey", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	openFileDialog(REQUEST_NEW);
                dialog.dismiss();
            }
        });
        builderSingle.show();

		if (_glSurfaceView != null) {
			_glSurfaceView.setOnIndoorMarkerClickLinstener(this);
			restoreMarker();
		}
	}
	
	private void restoreMarker() {
		if(lstWifiItem.size() > 0) {
			for (int i=0; i<lstWifiItem.size(); i++) {
				Point point = new Point();
				point.x = lstWifiItem.get(i).getPosition().getX();
				point.y = lstWifiItem.get(i).getPosition().getY();
				markOnMap(lstWifiItem.get(i), point, String.valueOf(i+1));
			}
		}
	}
 
    @Override
    public void onPause()
    {
        super.onPause();
        _glSurfaceView.onPause();
    }
	
	@Override
	public void onResume() {
	    super.onResume();
	    _glSurfaceView.onResume();
	}

	@Override
	public void onDestroyView() {
	    super.onDestroyView();
	    
    	String filePath = "/sdcard/Application/wifi-zombie/wifi-zombie-indoor-auto-save";
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
	}
	
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.indoor, menu);
        return;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_indoor_new:
        	discardSurveyData(REQUEST_NEW);
//        	newSurvey();
            break;
        case R.id.action_indoor_save:
			if(surveyTitle.isEmpty()) {
				Toast.makeText(context, "You have to create new survey first.", Toast.LENGTH_SHORT).show();
				return false;
			}
        	openFileDialog(this.REQUEST_SAVE);
//        	saveSurvey();
            break;
        case R.id.action_indoor_load:
        	discardSurveyData(REQUEST_LOAD);
//        	openFileDialog(this.REQUEST_LOAD);
//        	loadSurvey();
            break;
        case R.id.action_indoor_heatmap:
        	if(enableHeatmap) {
        		enableHeatmap = false;
        		item.setTitle("Enable Heatmap");
        	} else {
        		enableHeatmap = true;
        		item.setTitle("Disable Heatmap");
        	}
//        	mMap.clear();
        	restoreMarker();
            break;
//        case R.id.action_indoor_ssid:
//        	selectCertainSSID();
//            break;
        default:
        	break;
        }

        return super.onOptionsItemSelected(item);
    }

	private void storeCurrentWifiItem() {
		WifiItem currentWifiItem = getWifiItem();
		lstWifiItem.add(currentWifiItem);
		
		Point point = new Point();
		point.x = currentWifiItem.getPosition().getX();
		point.y = currentWifiItem.getPosition().getY();
		
		markOnMap(currentWifiItem, point, String.valueOf(lstWifiItem.size()));
		hasChanged = true;
	}
	
	private void markOnMap(WifiItem wifiItem, Point point, String name) {
		// Bubble Icon 생성
		IconGenerator mIconGenerator = new IconGenerator(context);
		mIconGenerator.setStyle(mIconGenerator.STYLE_PURPLE);
		Bitmap iconBitmap = mIconGenerator.makeIcon(name);
		
		_glSurfaceView.addMarker(new IndoorMarkerOptions()
		.snippet(name)
		.icon(iconBitmap)
		.setX((float)point.x)
		.setY((float)point.y));

		if(enableHeatmap) {
			addPoint(wifiItem, point);
		}
	}
	 
	public void addPoint(WifiItem wifiItem, Point point) {
		int maxStrength = (int) (Math.abs(getMaxStrength(wifiItem)) % 100);

		// Instantiates a new CircleOptions object and defines the center and radius
		CircleOptions circleOptions = new CircleOptions()
		    .center(new LatLng(point.x, point.y))
		    .radius(maxStrength)
		    .fillColor(Color.argb(maxStrength, 219, 46, 15))
		    .strokeColor(Color.TRANSPARENT); //dont show the border to the circle
		// Get back the mutable Circle
//		mMap.addCircle(circleOptions);
	}
	
	private WifiItem getWifiItem() {
		/* Wifi Info를 가져와서 위치 정보를 가진 Wifi Item으로 만든다 */
		WifiInfoData wifiInfoData = super.wifidata;
		WifiInfo wifiInfo = wifiInfoData.getWifiInfo();
		WifiItem wifiItem = WifiItem.newBuilder()
				.setPosition(Position.newBuilder()
						.setX(currentLocation.x)
						.setY(currentLocation.y)
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
			.setImagePath(surveyImportedImage)
			.setSurveyType(surveyType.INDOOR);
		for (WifiItem item : lstWifiItem) {
			wifiSurveyBuilder.addWifiItemList(item);
		}
		
		return wifiSurveyBuilder.build();
	}

	private class Point {
		public double x = 0;
		public double y = 0;
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
	public boolean onIndoorMarkerClick(final IndoorMarker marker) {
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
            	_glSurfaceView.clear();
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
						openFileDialog(request);
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
				openFileDialog(request);
				break;
			case REQUEST_LOAD:
				openFileDialog(request);
				break;
			}
    	}
    }
    
    private void newSurvey() {
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    	final View view = inflater.inflate(R.layout.dialog_new_survey, null);
    	
    	final LinearLayout llNewSurvey = (LinearLayout) view.findViewById(R.id.dialog_new_survey);
    	final EditText etSurveyTitle = (EditText) view.findViewById(R.id.etSurveyTitle);
    	final EditText etSurveyCreator = (EditText) view.findViewById(R.id.etSurveyCreator);
    	final TextView tvImagePath = new TextView(context);
    	tvImagePath.setText("FILE NAME : " + pathFromFileDialog);
    	llNewSurvey.addView(tvImagePath);
    	final String strImportedImage = pathFromFileDialog;
    	
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.icon);
        builderSingle.setTitle("Create New Survey");
        builderSingle.setView(view);
        builderSingle.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	if(etSurveyTitle.getText().toString().isEmpty() ||
            		etSurveyCreator.getText().toString().isEmpty() ||
            		strImportedImage.isEmpty()) {
            		Toast.makeText(context, "You must put the title and creator.", Toast.LENGTH_SHORT).show();
            		return;
            	}
            	surveyTitle = etSurveyTitle.getText().toString();
            	surveyCreator = etSurveyCreator.getText().toString();
            	surveyCreatedTime = Calendar.getInstance();
				surveyImportedImage = strImportedImage;
				_glSurfaceView.setImportedImage(strImportedImage);
            	surveyType = SurveyType.INDOOR;
            	lstWifiItem = new ArrayList<WifiItem>();
//        		mMap.clear();
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
            		if(wifiSurvey.getSurveyType() != SurveyType.INDOOR) {
            			Toast.makeText(context, "Importing Survey Data is failed", Toast.LENGTH_SHORT).show();
            			return;
            		}
//            		mMap.clear();
            		hasChanged = false;
	            	surveyTitle = wifiSurvey.getTitle();
	            	surveyCreator = wifiSurvey.getCreator();
	            	surveyCreatedTime = Calendar.getInstance();
	            	surveyCreatedTime.setTimeInMillis(wifiSurvey.getCreatedTime());
					surveyImportedImage = wifiSurvey.getImagePath();
					_glSurfaceView.setImportedImage(surveyImportedImage);
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
            	surveyImportedImage = "";
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
        
        if(requestCode == this.REQUEST_SAVE) {
            intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "zombie" });
        	intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);
        } else if(requestCode == this.REQUEST_LOAD) {
            intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "zombie" });
        	intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
        } else if(requestCode == this.REQUEST_NEW) {
            intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png", "jpg", "bmp", "gif" });
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
            } else if (requestCode == REQUEST_NEW) {
            	newSurvey();
            }

        } else if (resultCode == getActivity().RESULT_CANCELED) {
        	pathFromFileDialog = "";
        }

    }
}

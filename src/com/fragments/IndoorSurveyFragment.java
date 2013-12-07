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
import source.MyFragment;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.data.WifiInfoData;
import com.google.maps.android.ui.IconGenerator;
import com.google.protobuf.InvalidProtocolBufferException;
import com.wifi_zombie.FileDialog;
import com.wifi_zombie.R;
import com.wifi_zombie.SelectionMode;
import com.wifi_zombie.WifiZombieProto.WifiInfo;
import com.wifi_zombie.WifiZombieProto.WifiSurvey;
import com.wifi_zombie.WifiZombieProto.WifiSurvey.SurveyType;
import com.wifi_zombie.WifiZombieProto.WifiSurvey.WifiItem;
import com.wifi_zombie.WifiZombieProto.WifiSurvey.WifiItem.Position;

public class IndoorSurveyFragment extends MyFragment implements OnClickListener { 
	
    // Our OpenGL Surfaceview
    private GLSurfaceView glSurfaceView;
    private FrameLayout surfaceFrame;
    
    private View inflater;
    private Button btnSaveCurrentAPs; 
    private Context context;
    
    /* 서베이 정보 */
    private String surveyTitle;
    private String surveyCreator;
    private Calendar surveyCreatedTime;
    private SurveyType surveyType;
    private List<WifiItem> lstWifiItem;
    
    private boolean hasChanged;
    private String pathFromFileDialog;
    private final int REQUEST_NEW = 0;
    private final int REQUEST_SAVE = 1;
    private final int REQUEST_LOAD = 2;
    
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
		if(savedInstanceState != null)
			onRestoreInstanceState(savedInstanceState);
        
		setUpMapIfNeeded();
	}
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if(savedInstanceState.containsKey("indoorSurvey")) {
			WifiSurvey wifiSurvey = null;
			try {
				wifiSurvey = WifiSurvey.parseFrom(savedInstanceState.getByteArray("indoorSurvey"));
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}
			
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

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putByteArray("indoorSurvey", generateWifiSurvey().toByteArray());
	}
    
	private void initialize() {
		hasChanged = false;
		context = getActivity().getApplicationContext(); 
		
        // We create our Surfaceview for our OpenGL here.
        glSurfaceView = new GLSurf(context);
        surfaceFrame = (FrameLayout) inflater.findViewById(R.id.surfaceFrame);
        surfaceFrame.addView(glSurfaceView);
		
		
		// Bind Layout & Event
		btnSaveCurrentAPs = (Button) inflater.findViewById(R.id.btnSaveCurrentAPs);
		btnSaveCurrentAPs.setOnClickListener(this);
        
        // Initialize Current Location
        currentLocation = new Point();
        
        // Initialize Local variables
        surveyTitle = new String();
        surveyCreator = new String();
        surveyCreatedTime = Calendar.getInstance();
        surveyType = SurveyType.INDOOR;
        lstWifiItem = new ArrayList<WifiItem>();
	}

	private void setUpMapIfNeeded() {
	}
	
	private void restoreMarker() {
		if(lstWifiItem.size() > 0) {
			for (int i=0; i<lstWifiItem.size(); i++) {
				Point share = new Point();
				share.x = lstWifiItem.get(i).getPosition().getX();
				share.y = lstWifiItem.get(i).getPosition().getY();
				markOnMap(share, String.valueOf(i+1));
			}
		}
	}
 
    @Override
    public void onPause()
    {
        super.onPause();
        glSurfaceView.onPause();
    }
	
	@Override
	public void onResume() {
	    super.onResume();
	    glSurfaceView.onResume();
	}

	@Override
	public void onDestroyView() {
	    super.onDestroyView();
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
//        case R.id.action_indoor_heatmap:
//            toggleHeatmap();
//            break;
        case R.id.action_indoor_ssid:
        	selectCertainSSID();
            break;
        default:
        	break;
        }

        return super.onOptionsItemSelected(item);
    }

	private void storeCurrentWifiItem() {
		WifiItem currentWifiItem = getWifiItem();
		lstWifiItem.add(currentWifiItem);
		
		Point Share = new Point();
		Share.x = currentWifiItem.getPosition().getX();
		Share.y = currentWifiItem.getPosition().getY();
		
//		markOnMap(Share, String.valueOf(lstWifiItem.size()));
		hasChanged = true;
	}
	
	private void markOnMap(Point share, String name) {
		// Bubble Icon 생성
		IconGenerator mIconGenerator = new IconGenerator(context);
		mIconGenerator.setStyle(mIconGenerator.STYLE_PURPLE);
		Bitmap iconBitmap = mIconGenerator.makeIcon(name);
		
//		mMap.addMarker(new MarkerOptions()
//		.snippet(name)
//		.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
//		.position(new LatLng(share.x, share.y)));
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
	
	private WifiSurvey generateWifiSurvey() {
		/* 다수의 Wifi Item과 Survey정보를 가진 Wifi Survey 생성 */
		WifiSurvey.Builder wifiSurveyBuilder = WifiSurvey.newBuilder();
		wifiSurveyBuilder.setTitle(surveyTitle)
			.setCreatedTime(surveyCreatedTime.getTimeInMillis())
			.setCreator(surveyCreator)
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
    
    private void toggleHeatmap() {
      Toast.makeText(context, "Heatmap Menu selected", Toast.LENGTH_SHORT).show();
    	
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

            if (requestCode == REQUEST_SAVE) {
            	saveSurvey();
            } else if (requestCode == REQUEST_LOAD) {
            	loadSurvey();
            }

        } else if (resultCode == getActivity().RESULT_CANCELED) {
        	pathFromFileDialog = "";
        }

    }
}

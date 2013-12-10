package source;

import java.util.ArrayList;
import java.util.List;

import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;

public class IndoorLayer extends CCColorLayer
{	
	private Activity currentActivity;
	
	private String importedImage;
	private CCSprite blueprintSprite;
	private List<IndoorMarker> markerList;
	private List<CCSprite> markerSpriteList;

	private float[] positionDiff;
	private float[] positionBegin;
	private CCSprite selectedSprite;
	private CCSprite currentPointMarker;
	private boolean isMultitouch;

	static OnIndoorMarkerClickListener onIndoorMarkerClickLinstener;
	
	public static CCScene scene(IndoorLayer layout)
	{
		CCScene scene = CCScene.node();
		
		CCColorLayer layer = layout;
		scene.addChild(layer);
		
		return scene;
	}
	
	protected IndoorLayer(ccColor4B color)
	{
		super(color);
		
		this.setIsTouchEnabled(true);
		
		importedImage = new String();
		markerList = new ArrayList<IndoorMarker>();
		markerSpriteList = new ArrayList<CCSprite>();
		positionDiff = new float[2];
		positionBegin = new float[2];
		isMultitouch = false;
		onIndoorMarkerClickLinstener = null;
		setCurrentActivity(null);

		currentPointMarker = CCSprite.sprite("ic_marker_squared_red.png");
        currentPointMarker.setScale(1.0f);
	}
	
	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		// Choose one of the touches to work with
		CGPoint touchPoint = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
		positionBegin[0] = touchPoint.x;
		positionBegin[1] = touchPoint.y;
		
		selectedSprite = getBluePrintSprite(touchPoint);
		if(selectedSprite!=null) {
			CGPoint spritePoint = selectedSprite.getPosition();
			positionDiff[0] = touchPoint.x - spritePoint.x;
			positionDiff[1] = touchPoint.y - spritePoint.y;
		}
		
//		Log.d("touch-began", touchPoint.x +", "+touchPoint.y+", count:"+event.getPointerCount());
		
		return true;
	}
	
	@Override
	public boolean ccTouchesMoved(MotionEvent event) {
		isMultitouch = (event.getPointerCount()>1)?true:false;
		
        if(!isMultitouch && selectedSprite != null) {    		
    		CGPoint location = CCDirector.sharedDirector().convertToGL(
				CGPoint.ccp(event.getX()-positionDiff[0], event.getY()+positionDiff[1]));
    		selectedSprite.setPosition(location);
//            Log.d("touch-moved", String.format("move %d=(%f,%f)", 0, location.x, location.y));
        } else {
        	selectedSprite = null;
	        for (int i = 0; i < event.getPointerCount(); i++) {
	            CGPoint location = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(i),event.getY(i)));
//	            Log.d("touch-moved", String.format("move %d=(%f,%f)", i, location.x, location.y));
	        }
        }
        
		return super.ccTouchesMoved(event);
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event)
	{
		// Choose one of the touches to work with
		CGPoint location = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
		long touchTime = (event.getEventTime()-event.getDownTime());
		
		if(!isMultitouch && touchTime > 1000 && Math.abs(positionBegin[0]-location.x)<50
				&& Math.abs(positionBegin[1]-location.y)<50 && selectedSprite != null) {			
    		CGPoint markerLocation = selectedSprite.convertTouchToNodeSpace(event);
    		currentPointMarker.setPosition(markerLocation);
    		selectedSprite.addChild(currentPointMarker);
		}

		if(onIndoorMarkerClickLinstener != null && markerList.size() > 0 &&
				!isMultitouch && Math.abs(positionBegin[0]-location.x)<50
				&& Math.abs(positionBegin[1]-location.y)<50) {
			CCSprite clickedMarkerSprite = getClickedMarker(event);
			if(clickedMarkerSprite != null) {
				int clickedMarkerIndex = markerSpriteList.indexOf(clickedMarkerSprite);
				final IndoorMarker marker = markerList.get(clickedMarkerIndex);
				new Thread() {
					public void run() {
						currentActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								onIndoorMarkerClickLinstener.onIndoorMarkerClick(marker);
							}
						});
					};
				}.start();
			}
		}
		
		selectedSprite = null;
		positionDiff = new float[2];
		
		return true;
	}
	
	public float[] getCurrentPosition() {
		float[] currentPosition = new float[2];
		currentPosition[0] = currentPointMarker.getPosition().x;
		currentPosition[1] = currentPointMarker.getPosition().y+10;
		return currentPosition;
	}

	public CCSprite getBluePrintSprite(CGPoint point) {
        if (blueprintSprite.getBoundingBox().contains(point.x, point.y))
        	return blueprintSprite;
        else
        	return null;
	}
	
	public void addMarker(IndoorMarker marker) {
		if(currentPointMarker != null) {
			CCSprite markerSprite = CCSprite.sprite(marker.getIcon(), marker.getSnippet());
			CGPoint currentLocation = CGPoint.ccp(
					marker.getX(), marker.getY());
			markerSprite.setScale(0.5f);
			markerSprite.setPosition(currentLocation);
			blueprintSprite.addChild(markerSprite);
			markerSpriteList.add(markerSprite);
			markerList.add(marker);
			currentPointMarker.cleanup();
		}
	}
	
	public void putImportedImage(String path) {
		clearImage();
		importedImage = path;
		
        // Retrieve our image from resources.
        if(importedImage != null && !importedImage.isEmpty()) {
    		CGSize winSize = CCDirector.sharedDirector().displaySize();
	        // Temporary create a bitmap
	        Bitmap bmp = BitmapFactory.decodeFile(importedImage);
    		blueprintSprite = CCSprite.sprite(bmp, importedImage);
    		blueprintSprite.setScale(2.0f);
    		blueprintSprite.setPosition(CGPoint.ccp(blueprintSprite.getContentSize().width/2.0f, winSize.height/2.0f));
    		addChild(blueprintSprite);
        }
	}
	
	public String getImportedImage() {
		return importedImage;
	}
	
	public CCSprite getClickedMarker(MotionEvent event) {
		CCSprite clickedMarker = null;
		
		for (CCSprite markerSprite : markerSpriteList) {
			CGRect markerRect =  markerSprite.getBoundingBox();
			CGPoint touchPoint = blueprintSprite.convertTouchToNodeSpace(event);
			if(CGRect.containsPoint(markerRect, touchPoint)) {
				clickedMarker = markerSprite;
			}
		}
		
		return clickedMarker;
	}
 
    public void setOnIndoorMarkerClickLinstener(
			OnIndoorMarkerClickListener listener) {
		onIndoorMarkerClickLinstener = listener;
	}
    
    public void clearImage() {
    	if(blueprintSprite != null) {
    		clearMarker();
    		removeChild(blueprintSprite, true);
    		blueprintSprite.cleanup();
    	}
    }
    
    public void clearMarker() {
    	if(markerSpriteList != null && markerList != null) {
    		blueprintSprite.removeAllChildren(true);
			markerList.clear();
			markerSpriteList.clear();
    	}
    }

	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
	}
}

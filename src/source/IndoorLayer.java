package source;

import java.util.ArrayList;
import java.util.List;

import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;

public class IndoorLayer extends CCColorLayer
{	
	private String importedImage;
	private CCSprite blueprintSprite;
	private List<CCSprite> markerList; 
	
	private float[] positionDiff;
	private CCSprite selectedSprite;
	private CCSprite currentPointMarker;
	private boolean isMultitouch;
	
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
		markerList = new ArrayList<CCSprite>();
		positionDiff = new float[2];
		isMultitouch = false;

		currentPointMarker = CCSprite.sprite("ic_marker_squared_red.png");
        currentPointMarker.setScale(1.0f);
	}
	
	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		// Choose one of the touches to work with
		CGPoint touchPoint = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
		
		selectedSprite = getBluePrintSprite(touchPoint);
		if(selectedSprite!=null) {
			CGPoint spritePoint = selectedSprite.getPosition();
			positionDiff[0] = touchPoint.x - spritePoint.x;
			positionDiff[1] = touchPoint.y - spritePoint.y;
		}
		
		Log.d("touch-began", touchPoint.x +", "+touchPoint.y+", count:"+event.getPointerCount());
		
		
		return true;
	}
	
	public float[] getCurrentPosition() {
		return positionDiff;
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event)
	{
		// Choose one of the touches to work with
		CGPoint location = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
		long touchTime = (event.getEventTime()-event.getDownTime());
		
		if(!isMultitouch && touchTime > 1000) {
			float[] currentPosition = getCurrentPosition();
			
		}
		
		selectedSprite = null;
		positionDiff = new float[2];

		Log.d("touch-ended", location.x +", "+location.y+", count:"+event.getPointerCount()+", duringTime:"+(event.getEventTime()-event.getDownTime()));
		
		return true;
	}
	
	
	@Override
	public boolean ccTouchesMoved(MotionEvent event) {
		isMultitouch = (event.getPointerCount()>1)?true:false;
		
        if(!isMultitouch && selectedSprite != null) {
    		CGPoint location = CCDirector.sharedDirector().convertToGL(
    				CGPoint.ccp(event.getX()-positionDiff[0], event.getY()+positionDiff[1]));
    		selectedSprite.setPosition(location);
            Log.d("touch-moved", String.format("move %d=(%f,%f)", 0, location.x, location.y));
        } else {
        	selectedSprite = null;
	        for (int i = 0; i < event.getPointerCount(); i++) {
	            CGPoint location = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(i),event.getY(i)));
	            Log.d("touch-moved", String.format("move %d=(%f,%f)", i, location.x, location.y));
	        }
        }
		
		return super.ccTouchesMoved(event);
	}

	public CCSprite getBluePrintSprite(CGPoint point) {
        if (blueprintSprite.getBoundingBox().contains(point.x, point.y))
        	return blueprintSprite;
        else
        	return null;
	}
	
	public void addMarker(IndoorMarker marker) {
		CCSprite markerSprite = CCSprite.sprite(marker.getIcon(), marker.getSnippet());
		//markerSprite.setPosition(x, y);
	}
	
	public void clearMarker() {
		markerList.clear();
	}
	
	public void putImportedImage(String path) {
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
}

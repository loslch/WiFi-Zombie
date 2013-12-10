package source;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.ccColor4B;

import android.app.Activity;
import android.content.Context;
 
public class GLSurf extends CCGLSurfaceView {
	IndoorLayer indoorLayer;

	public GLSurf(Context context) {
        super(context);
		
		CCDirector.sharedDirector().attachInView(this);
		CCDirector.sharedDirector().setDisplayFPS(true);
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60.0f);
		
		indoorLayer = new IndoorLayer(ccColor4B.ccc4(0, 0, 0, 0));
		CCScene scene = IndoorLayer.scene(indoorLayer);
		CCDirector.sharedDirector().runWithScene(scene);
    }
    
    public void setImportedImage(String path) {
    	indoorLayer.putImportedImage(path);
    }
    
    public String getImportedImage() {
    	return indoorLayer.getImportedImage();
    }
    
    public void addMarker(IndoorMarkerOptions markerOptions) {
    	indoorLayer.addMarker(markerOptions.getMarker());
    }
    
    public void clearImage() {
    	indoorLayer.clearImage();
    }
    
    public void clearMarker() {
    	indoorLayer.clearMarker();
    }
 
    public void setOnIndoorMarkerClickLinstener(
			OnIndoorMarkerClickListener onIndoorMarkerClickLinstener) {
		indoorLayer.setOnIndoorMarkerClickLinstener(onIndoorMarkerClickLinstener);
	}
    
	public float[] getCurrentPosition() {
		return indoorLayer.getCurrentPosition();
	}

	public void setCurrentActivity(Activity currentActivity) {
		indoorLayer.setCurrentActivity(currentActivity);
	}
	
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
		
		CCDirector.sharedDirector().pause();
    }
 
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
		
		CCDirector.sharedDirector().resume();
    }
 
}
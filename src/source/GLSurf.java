package source;

import java.util.ArrayList;
import java.util.List;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.ccColor4B;

import android.content.Context;
 
public class GLSurf extends CCGLSurfaceView {
	IndoorLayer indoorLayer;
	
	List<IndoorMarker> lstIndoorMarker;
	OnIndoorMarkerClickListener onIndoorMarkerClickLinstener;

	public GLSurf(Context context) {
        super(context);
		
		CCDirector.sharedDirector().attachInView(this);
		CCDirector.sharedDirector().setDisplayFPS(true);
		CCDirector.sharedDirector().setAnimationInterval(1.0f / 60.0f);
		
		lstIndoorMarker = new ArrayList<IndoorMarker>();
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
    
    public void clear() {
    	lstIndoorMarker.clear();
    }
 
    public void setOnIndoorMarkerClickLinstener(
			OnIndoorMarkerClickListener onIndoorMarkerClickLinstener) {
		this.onIndoorMarkerClickLinstener = onIndoorMarkerClickLinstener;
	}
    
	public float[] getCurrentPosition() {
		return indoorLayer.getCurrentPosition();
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
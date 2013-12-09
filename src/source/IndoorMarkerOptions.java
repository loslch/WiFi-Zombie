package source;

import android.graphics.Bitmap;

public class IndoorMarkerOptions {
	private IndoorMarker marker;
	
	public IndoorMarkerOptions() {
		marker = new IndoorMarker();
	}
	
	public IndoorMarker getMarker() {
		return marker;
	}

	public void setMarker(IndoorMarker marker) {
		this.marker = marker;
	}

	public IndoorMarkerOptions snippet(String snippet) {
		marker.setSnippet(snippet);
		return this;
	}
	
	public IndoorMarkerOptions icon(Bitmap bmp) {
		marker.setIcon(bmp);
		return this;
	}
	
	public IndoorMarkerOptions setX(float value) {
		marker.setX(value);
		return this;
	}
	
	public IndoorMarkerOptions setY(float value) {
		marker.setY(value);
		return this;
	}

	public String getSnippet() {
		return marker.getSnippet();
	}

	public Bitmap getIcon() {
		return marker.getIcon();
	}

	public float getX() {
		return marker.getX();
	}

	public float getY() {
		return marker.getY();
	}
}

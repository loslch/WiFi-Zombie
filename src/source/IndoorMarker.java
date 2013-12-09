package source;

import android.graphics.Bitmap;

public class IndoorMarker {
	private String snippet;
	private Bitmap icon;
	private float x;
	private float y;
	
	public String getSnippet() {
		return snippet;
	}
	public Bitmap getIcon() {
		return icon;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
	public void setX(float x) {
		this.x = x;
	}
	public void setY(float y) {
		this.y = y;
	}
}

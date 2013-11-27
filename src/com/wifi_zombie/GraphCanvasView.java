package com.wifi_zombie;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View; 
import android.view.WindowManager;

public class GraphCanvasView extends View{

	private Canvas thisCanvas;
	private boolean is5G = false;
	
	public GraphCanvasView(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public void onDraw(Canvas canvas) {
	
		thisCanvas  = canvas;
		canvas.drawColor(Color.WHITE);		
		if(is5G)
			draw5Ggraph();
		else 
			draw2_4Ggraph();
	}
	public void setIs5G(boolean is)
	{
		is5G = is;
	}
	public void draw2_4Ggraph()
	{
		Log.i("wifi zombie", "2.4G draw method");
		thisCanvas.drawColor(Color.WHITE);	
		
	}
	public void draw5Ggraph()
	{
		Log.i("wifi zombie", "5G draw method");
		thisCanvas.drawColor(Color.WHITE);	
		Paint paint = new Paint();
		
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(2);
		thisCanvas.drawLine(5, 0, 5, this.getHeight()-30, paint);		
	}
}

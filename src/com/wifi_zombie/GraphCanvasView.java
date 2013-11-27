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

	private boolean is5G = false;
	
	public GraphCanvasView(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public void onDraw(Canvas canvas) {
	
		canvas.drawColor(Color.WHITE);		
		if(is5G)
			draw5Ggraph(canvas);
		else 
			draw2_4Ggraph(canvas);
	}
	public void setIs5G(boolean is)
	{
		is5G = is;
	}
	public void draw2_4Ggraph(Canvas canvas)
	{
		Log.i("wifi zombie", "2.4G draw method");
		canvas.drawColor(Color.WHITE);	
Paint paint = new Paint();
		
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(2);
		canvas.drawLine(5, 0, 5, this.getHeight()-30, paint);	
		
	}
	public void draw5Ggraph(Canvas canvas)
	{
		Log.i("wifi zombie", "5G draw method");
		canvas.drawColor(Color.WHITE);	
			
	}
}

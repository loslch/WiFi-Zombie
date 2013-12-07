package source;


import com.data.WifiInfoData;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class GraphCanvasView5G extends MyView{
	private int xAxisLength, yAxisLength;
	private WifiInfoData data = null;
	public GraphCanvasView5G(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public void onDraw(Canvas canvas) {
		xAxisLength = this.getLayoutParams().width - leftAndBottomMargin;
		yAxisLength = this.getHeight() - leftAndBottomMargin
				- rightAndTopMargin;
		
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(30);

		// signal line
				paint.setStrokeWidth(1);
				for (int i = 1; i <= signalNum - 1; i++)
					canvas.drawLine(leftAndBottomMargin, rightAndTopMargin
							+ (yAxisLength / signalNum) * i, this.getWidth()
							- rightAndTopMargin, rightAndTopMargin
							+ (yAxisLength / signalNum) * i, paint);

				
				
		if ((data != null) && (data.getSize() != 0)) // graph ±◊∏≤
		{
			for (int i = 0; i < data.getSize(); i++) {
				int ch = data.getWifiInfoData(i).getChannel();
				int strength = data.getWifiInfoData(i).getStrength();

				if (ch > 14) {
					Paint graphPaint = new Paint();
					graphPaint.setTextSize(30);
					graphPaint.setColor(Color
							.parseColor(colorsBB[i % colorNum]));
					graphPaint.setAntiAlias(true);
					RectF rect = new RectF(leftAndBottomMargin + (xAxisLength / chNum5G) * (ch - chMin5G-2)+strokeWidth,
							((strength * (-1) - 20) / 10) * (yAxisLength / signalNum) + rightAndTopMargin,
							leftAndBottomMargin + (xAxisLength / chNum5G) * (ch - chMin5G+2)+strokeWidth,
							this.getHeight() - leftAndBottomMargin + (this.getHeight() - leftAndBottomMargin - ((strength * (-1) - 20) / 10) * (yAxisLength / signalNum) + rightAndTopMargin));
					
					canvas.drawOval(rect, graphPaint);
					graphPaint.setStyle(Style.STROKE);
					graphPaint.setStrokeWidth(strokeWidth);
					graphPaint.setColor(Color.parseColor(colors[i % colorNum]));
					canvas.drawOval(rect, graphPaint);
					graphPaint.setStrokeWidth(2);
					canvas.drawText(data.getWifiInfoData(i).getSSID(),
							leftAndBottomMargin + (xAxisLength / chNum5G) * (ch - chMin5G), ((strength
									* (-1) - 20) / 10)
									* (yAxisLength / signalNum)
									+ rightAndTopMargin, graphPaint);
				}
			}
		}
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, this.getHeight() - leftAndBottomMargin,
				this.getLayoutParams().width, this.getHeight(), paint);
		// x√‡
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(2);
		canvas.drawLine(leftAndBottomMargin, this.getHeight()
				- leftAndBottomMargin, this.getLayoutParams().width
				- rightAndTopMargin, this.getHeight() - leftAndBottomMargin,
				paint);
		paint.setTextSize(25);
		for (int i = chMin5G; i <= chMax5G; i++)
		{
			if ((i == 36) || (i == 40) || (i == 44) || (i == 48) || (i == 52)
					|| (i == 56) || (i == 60) || (i == 64) || (i == 100)
					|| (i == 104) || (i == 108) || (i == 112) || (i == 116)
					|| (i == 120) || (i == 124) || (i == 128) || (i == 132)
					|| (i == 136) || (i == 140) || (i == 149) || (i == 153)
					|| (i == 157) || (i == 161) || (i == 165)) {
				canvas.drawText(i + "", leftAndBottomMargin + (xAxisLength / chNum5G) * (i - chMin5G),
						this.getHeight() - leftAndBottomMargin / 2, paint);
			}
		}
		
	}

	public void setMargin(int i) {
		RelativeLayout.LayoutParams plControl = (RelativeLayout.LayoutParams) this.getLayoutParams();
		plControl.leftMargin = i;
		this.setLayoutParams(plControl);
		plControl = (RelativeLayout.LayoutParams) this.getLayoutParams();
		Log.i("wifi zombie", "%%%%%%%% "+plControl.leftMargin+"   "+plControl.rightMargin);
	}
	
	public void updateData(WifiInfoData data) {
		this.data = data;
		this.invalidate();
	}
}

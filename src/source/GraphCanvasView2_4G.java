package source;

import com.data.WifiDataItem;
import com.data.WifiInfoData;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class GraphCanvasView2_4G extends MyView {

	private WifiInfoData data = null;
	private int xAxisLength, yAxisLength;
	

	public GraphCanvasView2_4G(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public void onDraw(Canvas canvas) {

		canvas.drawColor(Color.WHITE);
		draw2_4Ggraph(canvas);
	}

	public void draw2_4Ggraph(Canvas canvas) {
		xAxisLength = this.getWidth() - leftAndBottomMargin - rightAndTopMargin;
		yAxisLength = this.getHeight() - leftAndBottomMargin
				- rightAndTopMargin;
		Log.i("wifi zombie", "2.4G draw method");
		canvas.drawColor(Color.WHITE);

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(1);
		// signal line
		for (int i = 1; i <= signalNum - 1; i++)
			canvas.drawLine(leftAndBottomMargin, rightAndTopMargin
					+ (yAxisLength / signalNum) * i, this.getWidth()
					- rightAndTopMargin, rightAndTopMargin
					+ (yAxisLength / signalNum) * i, paint);
		// channel line
		canvas.drawLine(leftAndBottomMargin + (xAxisLength / chNum2_4G) * 2,
				this.getHeight() - leftAndBottomMargin, leftAndBottomMargin
						+ (xAxisLength / chNum2_4G) * 2, rightAndTopMargin,
				paint);
		for (int i = 3; i <= chNum2_4G - 4; i++)
			canvas.drawLine(
					leftAndBottomMargin + (xAxisLength / chNum2_4G) * i,
					this.getHeight() - leftAndBottomMargin, leftAndBottomMargin
							+ (xAxisLength / chNum2_4G) * i,
							rightAndTopMargin, paint);
		canvas.drawLine(leftAndBottomMargin + (xAxisLength / chNum2_4G)
				* (chNum2_4G - 2), this.getHeight() - leftAndBottomMargin,
				leftAndBottomMargin + (xAxisLength / chNum2_4G)
						* (chNum2_4G - 2), rightAndTopMargin, paint);

		if ((data != null) && (data.getSize() != 0)) // graph 그림
		{
			for (int i = 0; i < data.getSize(); i++) {
				int ch = data.getWifiInfoData(i).getChannel();
				int strength = data.getWifiInfoData(i).getStrength();

				if (ch < 14) {
					Paint graphPaint = new Paint();
					graphPaint.setTextSize(30);
					graphPaint.setColor(Color
							.parseColor(colorsBB[i % colorNum]));
					graphPaint.setAntiAlias(true);
					// RectF rect = new
					// RectF((xAxisLength/chNum2_4G)*ch+strokeWidth,
					// ((strength*(-1)-20)/10)*(yAxisLength/signalNum)+rightAndTopMargin,
					// (xAxisLength/chNum2_4G)*(ch+4)-strokeWidth,
					// this.getHeight()-reftAndBottomMargin+
					// (this.getHeight()-reftAndBottomMargin-((strength*(-1)-20)/10)*(yAxisLength/signalNum)+rightAndTopMargin));
					RectF rect = new RectF(
							(xAxisLength / chNum2_4G) * ch + strokeWidth,
							((strength * (-1) - 20) / 10)
									* (yAxisLength / signalNum)
									+ rightAndTopMargin,
							(xAxisLength / chNum2_4G) * (ch + 4),
							this.getHeight()
									- leftAndBottomMargin
									+ (this.getHeight() - leftAndBottomMargin
											- ((strength * (-1) - 20) / 10)
											* (yAxisLength / signalNum) + rightAndTopMargin));
					canvas.drawOval(rect, graphPaint);
					graphPaint.setStyle(Style.STROKE);
					graphPaint.setStrokeWidth(strokeWidth);
					graphPaint.setColor(Color.parseColor(colors[i % colorNum]));
					canvas.drawOval(rect, graphPaint);
					graphPaint.setStrokeWidth(2);
					canvas.drawText(data.getWifiInfoData(i).getSSID(),
							(xAxisLength / chNum2_4G) * (ch + 1), ((strength
									* (-1) - 20) / 10)
									* (yAxisLength / signalNum)
									+ rightAndTopMargin, graphPaint);
				}
				else if(ch == 14)
				{
					RectF rect = new RectF(
							(xAxisLength / chNum2_4G) * (ch+1) + strokeWidth,
							((strength * (-1) - 20) / 10) * (yAxisLength / signalNum) + rightAndTopMargin,
							(xAxisLength / chNum2_4G) * (ch + 5), this.getHeight() - leftAndBottomMargin + (this.getHeight() - leftAndBottomMargin - ((strength * (-1) - 20) / 10) * (yAxisLength / signalNum) + rightAndTopMargin));
				}
			}
		}
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, this.getHeight() - leftAndBottomMargin,
				this.getWidth(), this.getHeight(), paint);
		// 그래프 축 그리기
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(2);
		// y축
		canvas.drawLine(leftAndBottomMargin, rightAndTopMargin,
				leftAndBottomMargin, this.getHeight() - leftAndBottomMargin,
				paint);
		// x축
		canvas.drawLine(leftAndBottomMargin, this.getHeight()
				- leftAndBottomMargin, this.getWidth() - rightAndTopMargin,
				this.getHeight() - leftAndBottomMargin, paint);

		paint.setTextSize(25);
		for (int i = 1; i <= signalNum - 1; i++)
			canvas.drawText("-" + (i * 10 + 20), 5, rightAndTopMargin
					+ (yAxisLength / signalNum) * i, paint);
		canvas.drawText("1", leftAndBottomMargin + (xAxisLength / chNum2_4G)
				* 2, this.getHeight() - leftAndBottomMargin / 2, paint);
		for (int i = 3; i <= chNum2_4G - 4; i++)
			canvas.drawText((i - 1) + "", leftAndBottomMargin
					+ (xAxisLength / chNum2_4G) * i, this.getHeight()
					- leftAndBottomMargin / 2, paint);
		canvas.drawText("14", leftAndBottomMargin + (xAxisLength / chNum2_4G)
				* (chNum2_4G - 2), this.getHeight() - leftAndBottomMargin / 2,
				paint);

	}

	public void updateData(WifiInfoData data) {
		this.data = data;
		this.invalidate();
	}

}

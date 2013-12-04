package source;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class GraphCanvasView5G extends MyView{
	private int xAxisLength, yAxisLength;
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

		// x√‡
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
				canvas.drawText(i + "", leftAndBottomMargin
						+ (xAxisLength / chNum5G) * (i - chMin5G),
						this.getHeight() - leftAndBottomMargin / 2, paint);
			}
		}
		// signal line
		paint.setStrokeWidth(1);
		for (int i = 1; i <= signalNum - 1; i++)
			canvas.drawLine(leftAndBottomMargin, rightAndTopMargin
					+ (yAxisLength / signalNum) * i, this.getWidth()
					- rightAndTopMargin, rightAndTopMargin
					+ (yAxisLength / signalNum) * i, paint);

	}

	public void setMargin(int i) {
		RelativeLayout.LayoutParams plControl = (RelativeLayout.LayoutParams) this.getLayoutParams();
		plControl.leftMargin = i;
		this.setLayoutParams(plControl);
		plControl = (RelativeLayout.LayoutParams) this.getLayoutParams();
		Log.i("wifi zombie", "%%%%%%%% "+plControl.leftMargin+"   "+plControl.rightMargin);
	}
}

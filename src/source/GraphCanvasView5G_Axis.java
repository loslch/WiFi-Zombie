package source;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GraphCanvasView5G_Axis extends MyView{
	private int xAxisLength, yAxisLength;
	
	public GraphCanvasView5G_Axis(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public void onDraw(Canvas canvas) {
		xAxisLength = this.getWidth() - leftAndBottomMargin - rightAndTopMargin;
		yAxisLength = this.getHeight() - leftAndBottomMargin - rightAndTopMargin;
		canvas.drawColor(Color.rgb(71, 71, 81));

		Paint paint = new Paint();
		paint.setColor(Color.rgb(132, 132, 140));


		// 그래프 축 그리기
		paint.setColor(Color.rgb(132, 132, 140));
		paint.setStrokeWidth(2);
		// y축
		canvas.drawLine(leftAndBottomMargin, rightAndTopMargin,
				leftAndBottomMargin, this.getHeight() - leftAndBottomMargin,
				paint);

		paint.setTextSize(25);
		for (int i = 1; i <= signalNum - 1; i++)
			canvas.drawText("-" + (i * 10 + 20), 5, rightAndTopMargin
					+ (yAxisLength / signalNum) * i, paint);
	}
}

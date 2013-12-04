package source;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class MyView extends View{

	public final static int leftAndBottomMargin = 45;
	public final static int rightAndTopMargin = 20;
	public final static int signalNum = 8;
	public final static int chNum2_4G = 18;
	public final static int chMax5G = 163;
	public final static int chMin5G = 34;
	public final static int chNum5G = chMax5G-chMin5G;
	public final static int chInScreen5G = 15;//큰 화면에 표시할 채널 갯수
	public final static int strokeWidth = 10;
	public final static String[] colorsBB = { "#bbfc3a3a", "#bbfb64c1", "#bbab64fb",
			"#bb6464fb", "#bb64f6fb", "#bb64fb88", "#bbf2fb64", "#bbfba464",
			"#bb79fb64", "#bbfb64a4", "#bbc7fb64", "#bb64abfb", "#bb285d99",
			"#bbce3842", "#bb482adc", "#bbdcd62a", "#bbdc502a", "#bb786965",
			"#bb368c25", "#bbf9910b" };
	public static final String[] colors = { "#fc3a3a", "#fb64c1", "#ab64fb",
			"#6464fb", "#64f6fb", "#64fb88", "#f2fb64", "#fba464", "#79fb64",
			"#fb64a4", "#c7fb64", "#64abfb", "#285d99", "#ce3842", "#482adc",
			"#dcd62a", "#dc502a", "#786965", "#368c25", "#f9910b" };
	public static final int colorNum = 20;
	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	
}

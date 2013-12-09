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
	public final static String[] colorsBB = { "#44a75d54", "#44e6af5f", "#44c0af69",
			"#446d918f", "#445d7b79", "#44ae6a57", "#444d6b69", "#44c1af87",
			"#44727876", "#44e89d80", "#44247878", "#446c9585", "#44e8cd62",
			"#44ae5767", "#446d7f91", "#4469c086", "#44e4ba7f", "#44887e8a",
			"#44a65551", "#445ca7a4" };
	public static final String[] colors = { "#a75d54", "#e6af5f", "#c0af69",
		"#6d918f", "#5d7b79", "#ae6a57", "#4d6b69", "#c1af87",
		"#727876", "#e89d80", "#247878", "#6c9585", "#e8cd62",
		"#ae5767", "#6d7f91", "#69c086", "#e4ba7f", "#887e8a",
		"#a65551", "#5ca7a4"};
	public static final int colorNum = 20;
	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	
}

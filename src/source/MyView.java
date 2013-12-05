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
	public final static String[] colorsBB = { "#bbfd181d", "#bbfd7318", "#bbfdf018",
			"#bb38fd18", "#bb18fdf5", "#bb2818fd", "#bbf518fd", "#bbd42626",
			"#bbd48c26", "#bbd2d426", "#bb26d426", "#bb26d4b9", "#bb264fd4",
			"#bb6c26d4", "#bbbd26d4", "#bbd42680", "#bbfc2298", "#bb01d250",
			"#bbd2013c", "#bb99ff66" };
	public static final String[] colors = { "#fd181d", "#fd7318", "#fdf018",
		"#38fd18", "#18fdf5", "#2818fd", "#f518fd", "#d42626",
		"#d48c26", "#d2d426", "#26d426", "#26d4b9", "#264fd4",
		"#6c26d4", "#bd26d4", "#d42680", "#fc2298", "#01d250",
		"#d2013c", "#99ff66"};
	public static final int colorNum = 20;
	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	
}

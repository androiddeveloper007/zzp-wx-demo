package control.pattern.tools;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtils {

	/**
	 * 获取根据屏幕获取实际大小 在自定义控件中，根据屏幕的大小来获取实际的大小
	 * 
	 * @param ctx
	 * @param orgSize
	 * @return
	 */
	public static int getActualSize(Context ctx, int orgSize) {
		WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);
		float density = (float) displayMetrics.density;

		return (int) (orgSize * density);
	}
}

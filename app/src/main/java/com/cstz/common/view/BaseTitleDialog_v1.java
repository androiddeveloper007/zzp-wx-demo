package com.cstz.common.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cstz.cstz_android.R;

public class BaseTitleDialog_v1 extends BaseDialog {

	private LinearLayout frContent;

	public BaseTitleDialog_v1(Context context, double p) {
		super(context);

		super.setContentView(R.layout.dialog_base_v1);

		Window window = getWindow();
		WindowManager.LayoutParams attributesParams = window.getAttributes();
		attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		attributesParams.dimAmount = 0.4f;

		@SuppressWarnings("deprecation")
		int sreemWidth = window.getWindowManager().getDefaultDisplay().getWidth();
		int windowWidth = (int) (sreemWidth * p);

		window.setLayout(windowWidth, LayoutParams.WRAP_CONTENT);

		frContent = (LinearLayout) findViewById(R.id.fr_content);
	}

	@Override
	public void setContentView(View view) {
		frContent.removeAllViews();
		frContent.addView(view, new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
	}
	
	@Override
	public void setContentView(View view, LayoutParams lp) {
		frContent.removeAllViews();
		frContent.addView(view, lp);
	}

	@Override
	public void setContentView(int layoutResID) {
		View view = View.inflate(getContext(), layoutResID, null);
		setContentView(view);
	}

}
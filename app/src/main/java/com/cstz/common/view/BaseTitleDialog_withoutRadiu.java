package com.cstz.common.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.cstz_android.R;

public abstract class BaseTitleDialog_withoutRadiu extends BaseDialog {

	private LinearLayout frContent;

	private TextView tvTitle;
	private ImageView iv_close;
	private View line;

	private RelativeLayout rl_title_basedialog;

	public BaseTitleDialog_withoutRadiu(Context context) {
		super(context);

		super.setContentView(R.layout.dialog_base);

		Window window = getWindow();
		WindowManager.LayoutParams attributesParams = window.getAttributes();
		attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		attributesParams.dimAmount = 0.4f;

		@SuppressWarnings("deprecation")
		int sreemWidth = window.getWindowManager().getDefaultDisplay().getWidth();
		int windowWidth = (int) (sreemWidth * 0.9);

		window.setLayout(windowWidth, LayoutParams.WRAP_CONTENT);

		frContent = (LinearLayout) findViewById(R.id.fr_content);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		
		iv_close = (ImageView) findViewById(R.id.iv_close);
		rl_title_basedialog = (RelativeLayout) findViewById(R.id.rl_title_basedialog);
		
		iv_close.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		line = (View) findViewById(R.id.baseDialog_line);
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

	/**
	 * 设置对话框的标题
	 * @param title
	 */
	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	/**
	 * 设置对话框的标题颜色
	 */
	public void setTitleColor(int i) {
		tvTitle.setTextColor(i);
	}
	
	abstract void onclickclose();
	
	public void setIvCloseVisible(boolean b){
		if(b)
			iv_close.setVisibility(View.VISIBLE);
		else
			iv_close.setVisibility(View.GONE);
	}
	
	public void setRlTitleVisible(boolean b){
		if(b)
			rl_title_basedialog.setVisibility(View.VISIBLE);
		else
			rl_title_basedialog.setVisibility(View.GONE);
	}

	public void setBaseLineVisible(boolean b){
		if(b)
			line.setVisibility(View.VISIBLE);
		else
			line.setVisibility(View.GONE);
	}
}
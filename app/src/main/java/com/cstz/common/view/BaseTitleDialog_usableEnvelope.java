package com.cstz.common.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.cstz_android.R;

public abstract class BaseTitleDialog_usableEnvelope extends BaseDialog {

	private LinearLayout frContent;

	private TextView tvTitle;
	private ImageView iv_close;

	public BaseTitleDialog_usableEnvelope(Context context) {
		super(context);

		super.setContentView(R.layout.dialog_base_usableenvelope);

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
		
		iv_close.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				dismiss();
				onclickclose(v);
			}
		});
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
	
	public abstract void onclickclose(View v);
	
	public void setIvCloseVisible(boolean b){
		if(b)
			iv_close.setVisibility(View.VISIBLE);
		else
			iv_close.setVisibility(View.GONE);
	}
}
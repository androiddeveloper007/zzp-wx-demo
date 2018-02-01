package com.cstz.common.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.cstz_android.R;

/**
 * 列表对话框
 * @author Administrator
 *
 */
public class PatternWarnDialog extends BaseTitleDialog implements View.OnClickListener {

	public TextView tvMsg;

	protected Button btn1;
	protected ImageView ivLine;
	
	public PatternWarnDialog(Context ctx) {
		this(ctx, true);
	}
	
	public PatternWarnDialog(Context ctx, boolean cancelable) {
		super(ctx);
		
		setContentView(View.inflate(getContext(), R.layout.dialog_alert_pattern_warn, null), 
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		tvMsg = (TextView) findViewById(R.id.tv_msg_pattern_warn);
		
		setCancelable(cancelable);
		
		setCanceledOnTouchOutside(false);
		
		btn1 = (Button) findViewById(R.id.btn_pattern_warn);
		
		btn1.setOnClickListener(this);
		
//		iv_close = (ImageView) findViewById(R.id.iv_close);
		
//		iv_close.setOnClickListener(this);

		setIvCloseVisible(true);
	}
	
	private View.OnClickListener onBtn1ClickListener;


	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_pattern_warn) {
			if (onBtn1ClickListener != null) {
				onBtn1ClickListener.onClick(v);
			}
			cancel();
		}
	}
	
	public void setMessage(String msg) {
		tvMsg.setText(msg);
	}
	
	public void setBtn1Text(String text) {
		btn1.setText(text);
	}
	
	
	public void setBtn1Visibility(boolean visible) {
		if (visible) {
			btn1.setVisibility(View.VISIBLE);
		} else {
			btn1.setVisibility(View.GONE);
		}
	}
	
	public void setOnBtn1ClickListener(View.OnClickListener onClickListener) {
		this.onBtn1ClickListener = onClickListener;
	}

	@Override
	public void onclickclose(View v) {
		if (onBtn1ClickListener != null) {
			onBtn1ClickListener.onClick(v);
		}
	}

	
}

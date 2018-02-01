package com.cstz.common.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.cstz_android.R;

/**
 * 列表对话框
 * @author ZZP
 *
 */
public class ConfirmPayPwdDialog extends BaseTitleDialog_withoutRadiu implements View.OnClickListener {

	public TextView tvMsg;

	protected TextView btn1,btn2;
	protected ImageView ivLine;
	
	public ConfirmPayPwdDialog(Context ctx) {
		this(ctx, true);
	}
	
	public ConfirmPayPwdDialog(Context ctx, boolean cancelable) {
		super(ctx);
		
		setContentView(View.inflate(getContext(), R.layout.confirm_paypwd_dialog, null), 
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
//		tvMsg = (TextView) findViewById(R.id.tv_msg_pattern_warn);
		
		setCancelable(cancelable);
		
		setCanceledOnTouchOutside(false);
		
		btn1 = (TextView) findViewById(R.id.btn_confirm_pay_0);
		btn2 = (TextView) findViewById(R.id.btn_confirm_pay_1);
		
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		
		setIvCloseVisible(true);
	}
	
	private View.OnClickListener onBtn1ClickListener;


	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_confirm_pay_0) {
			
			if (onBtn1ClickListener != null) {
				
				onBtn1ClickListener.onClick(v);
			}
			
			cancel();
		} 
		else if( id == R.id.btn_confirm_pay_1){
//			onclickConfirm();
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
	void onclickclose() {
		dismiss();
	}
	
//	abstract void onclickConfirm();
}

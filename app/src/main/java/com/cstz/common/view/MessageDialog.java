package com.cstz.common.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.cstz_android.R;

/**
 * 列表对话框
 * @author zzp
 *
 */
public abstract class MessageDialog extends BaseTitleDialog implements View.OnClickListener {

	public TextView tvMsg;
	public TextView tvMsg0;

	protected Button btn1;
	protected Button btn2;
	protected ImageView ivLine;
	protected LinearLayout ll_recharge;
	
	public MessageDialog(Context ctx) {
		this(ctx, true);
	}
	
	public MessageDialog(Context ctx, boolean cancelable) {
		super(ctx);
		
		setContentView(View.inflate(getContext(), R.layout.dialog_alert3, null), 
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		tvMsg = (TextView) findViewById(R.id.tv_msg);
		tvMsg0 = (TextView) findViewById(R.id.tv_msg0);
		ll_recharge = (LinearLayout) findViewById(R.id.ll_recharge);
		
		setCancelable(cancelable);
		
		setCanceledOnTouchOutside(false);
		
		setTitle("提示");
		
		ivLine = (ImageView) findViewById(R.id.iv_line);
		
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		ll_recharge.setOnClickListener(this);
		
		setIvCloseVisible(false);
	}
	
	private View.OnClickListener onBtn1ClickListener;
	private View.OnClickListener onBtn2ClickListener;

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn1) {
			
			if (onBtn1ClickListener != null) {
				onBtn1ClickListener.onClick(v);
			}
			
			cancel();
		} else if (id == R.id.btn2) {
			
			if (onBtn2ClickListener != null) {
				onBtn2ClickListener.onClick(v);
			}
			
			cancel();
		} else if(id == R.id.ll_recharge){
			recharge();
		}
	}
	
	public void setMessage(String msg) {
		tvMsg.setText(msg);
	}
	
	public void setMessage0(String msg) {
		tvMsg0.setText(msg);
	}

	
	public void setBtn1Text(String text) {
		btn1.setText(text);
	}
	
	public void setBtn2Text(String text) {
		btn2.setText(text);
	}
	
	
	public void setBtn1Visibility(boolean visible) {
		if (visible) {
			btn1.setVisibility(View.VISIBLE);
		} else {
			btn1.setVisibility(View.GONE);
		}
	}
	
	public void setBtn2Visibility(boolean visibile) {
		if (visibile) {
			btn2.setVisibility(View.VISIBLE);
		}  else {
			btn2.setVisibility(View.GONE);
		}
	}
	

	public void setOnBtn1ClickListener(View.OnClickListener onClickListener) {
		this.onBtn1ClickListener = onClickListener;
	}
	
	public void setOnBtn2ClickListener(View.OnClickListener onClickListener) {
		this.onBtn2ClickListener = onClickListener;
	}
	
	public abstract void recharge();
	
//	@Override
//	void onclickclose(View v) {
//
//	}
}

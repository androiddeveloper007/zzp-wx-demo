package com.cstz.common.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.cstz.cstz_android.R;

/**
 * 权限请求通知对话框
 * @author zzp
 *
 */
public class PermissionWarnDialog extends BaseTitleDialog_withoutRadiu implements View.OnClickListener {

	public TextView tvMsg;

	protected Button btn1;
//	protected ImageView ivLine;
	
	public OnClickNext onClickNext = null;
	
	public PermissionWarnDialog(Context ctx) {
		this(ctx, true);
	}
	
	public PermissionWarnDialog(Context ctx, boolean cancelable) {
		super(ctx);
		
		setContentView(View.inflate(getContext(), R.layout.permission_warn_dialog, null), 
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		setCancelable(cancelable);
		
		setCanceledOnTouchOutside(false);
		
		btn1 = (Button) findViewById(R.id.btn_permission_warn);
		
		btn1.setOnClickListener(this);
		
		setIvCloseVisible(false);
		
		setRlTitleVisible(false);

		setBaseLineVisible(false);
	}
	

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_permission_warn) {
			
			if(onClickNext != null){
				
				onClickNext.next();
				
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
	
	@Override
	void onclickclose() {
		dismiss();
	}
	
	/**
	 * 回调方法
	 * @author zzp
	 *
	 */
	public interface OnClickNext {
		public void next();
	}
	
	public void setOnClickNext(OnClickNext onClicknext)
	{
		this.onClickNext = onClicknext;
	}

	@Override
	public void onBackPressed() {
		return;
	}
}

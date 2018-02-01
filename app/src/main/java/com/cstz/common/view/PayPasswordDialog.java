package com.cstz.common.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.cstz_android.R;

/**
 * 抽奖确认对话框
 * @author zzp
 *
 */
public class PayPasswordDialog extends BaseDialog implements View.OnClickListener {

	public TextView tvMsg,tvMsg_daojishi;

	protected TextView btn1,btn2;
	protected ImageView ivLine;
	private EditText et_pay;
	private LinearLayout frContent;
	
	public PayPasswordDialog(Context ctx, double p) {
		this(ctx, true,p);
	}
	
	public PayPasswordDialog(final Context ctx, boolean cancelable, double p) {
		super(ctx);
		super.setContentView(R.layout.paypassword_dialog);
		Window window = getWindow();
		WindowManager.LayoutParams attributesParams = window.getAttributes();
		attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		attributesParams.dimAmount = 0.4f;
		setCanceledOnTouchOutside(false);
		@SuppressWarnings("deprecation")
		int sreemWidth = window.getWindowManager().getDefaultDisplay().getWidth();
		int windowWidth = (int) (sreemWidth * p);
		window.setLayout(windowWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
		frContent = (LinearLayout) findViewById(R.id.fr_content);
		btn1 = (TextView) findViewById(R.id.btn_confirm_pay_0);
		btn2 = (TextView) findViewById(R.id.btn_confirm_pay_1);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		et_pay = (EditText) findViewById(R.id.et_pay);
		ll_pay_content = (LinearLayout) findViewById(R.id.ll_pay_content);
		ll_pay_content.setFocusable(true);
		ll_pay_content.setFocusableInTouchMode(true);
		ll_pay_content.requestFocus();
		ll_pay_content.setOnTouchListener(new OnTouchListener() {//点击此布局就收回输入框
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 影藏软键盘
				InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				ll_pay_content.requestFocus();
				return false;
			}
		});
	}

	@Override
	public void setContentView(View view) {
		frContent.removeAllViews();
		frContent.addView(view, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams lp) {
		frContent.removeAllViews();
		frContent.addView(view, lp);
	}

	@Override
	public void setContentView(int layoutResID) {
		View view = View.inflate(getContext(), layoutResID, null);
		setContentView(view);
	}

	private View.OnClickListener onBtn1ClickListener;
	private View.OnClickListener onBtn2ClickListener;

	private LinearLayout ll_pay_content;


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
			if (onBtn2ClickListener != null) {
				onBtn2ClickListener.onClick(v);
			}
			
			cancel();
		}
	}
	
	public void setMessage(String msg) {
		tvMsg.setText(msg);
	}
	
	public void setOnBtn1ClickListener(View.OnClickListener onClickListener) {
		this.onBtn1ClickListener = onClickListener;
	}
	
	public void setOnBtn2ClickListener(View.OnClickListener onClickListener) {
		this.onBtn2ClickListener = onClickListener;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}

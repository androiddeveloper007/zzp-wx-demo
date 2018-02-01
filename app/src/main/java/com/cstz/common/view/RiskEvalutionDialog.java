package com.cstz.common.view;

import android.content.Context;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.cstz_android.R;

/**
 * 政策公告提示框
 * @author zzp
 *
 */
public class RiskEvalutionDialog extends BaseTitleDialog_v1 implements View.OnClickListener {

	public TextView tvMsg,tvMsg_daojishi,tv_title_luckydraw;

	protected TextView btn1,btn2;
	protected ImageView ivLine;
	public RelativeLayout rl_tv_luckydraw;
	private Context c;

	public RiskEvalutionDialog(Context ctx, double p) {
		this(ctx, true, p);
	}

	public RiskEvalutionDialog(Context ctx, boolean cancelable, double p) {
		super(ctx,p);
		c=ctx;
		setContentView(View.inflate(getContext(), R.layout.riskevalution_dialog, null),
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		tvMsg = (TextView) findViewById(R.id.tv_msg_luckdraw);
		tvMsg_daojishi = (TextView) findViewById(R.id.tv_daojishi_luckdraw);//倒计时
		tv_title_luckydraw = (TextView) findViewById(R.id.tv_title_luckydraw);//标题
		rl_tv_luckydraw = (RelativeLayout) findViewById(R.id.rl_tv_luckydraw);//投资成功提示自定义
		
		setCancelable(cancelable);
		
		setCanceledOnTouchOutside(false);
		
		btn1 = (TextView) findViewById(R.id.btn_confirm_pay_0);
		btn2 = (TextView) findViewById(R.id.btn_confirm_pay_1);
		
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
	}
	
	private View.OnClickListener onBtn1ClickListener;
	private View.OnClickListener onBtn2ClickListener;
	runnablefinished runnablefinished;

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
	
	public void setMessage(String msg,boolean luckyDraw) {
		if(luckyDraw)
			tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX, c.getResources().getDimensionPixelSize(R.dimen.dimen_13_5));
		tvMsg.setText(msg);
	}

	public void setMessage(SpannableStringBuilder msg,int textSizeId) {
		tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeId);
		tvMsg.setText(msg);
		tvMsg.setGravity(Gravity.LEFT);
	}

	public void setMessage(String msg,int textSizeId) {
		tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeId);
		tvMsg.setText(msg);
		tvMsg.setGravity(Gravity.LEFT);
	}

	public void setMessage(SpannableStringBuilder msg) {
		tvMsg.setText(msg);
	}
	
	public void refreshmessage(){
		tvMsg_daojishi.setText(" (" + count + "s)");
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

	public void setOnBtn1ClickListener(View.OnClickListener onClickListener) {
		this.onBtn1ClickListener = onClickListener;
	}
	
	public void setOnBtn2ClickListener(View.OnClickListener onClickListener) {
		this.onBtn2ClickListener = onClickListener;
	}

	public void setOnRunnablefinListener(runnablefinished runnablefinished) {
		this.runnablefinished = runnablefinished;
	}
	
	//倒计时
	private static Handler handler = new Handler();
	private final static int COUNT_DAOJISHI = 11;
	private static int count = COUNT_DAOJISHI;
	private Runnable myRunnable = new Runnable() {
		public void run() {
			handler.postDelayed(this, 1000);
			count--;
			if (count == 0) {
				if (runnablefinished != null) {
					stopSendButton();
				}
				cancel();
			}
		}
	};

	public interface runnablefinished{
		void runnablefin();
	}
	
	public void stopSendButton() {
		handler.removeCallbacks(myRunnable);
		count = COUNT_DAOJISHI;
	}
	
	@Override
	public void onBackPressed() {
		if (runnablefinished != null) {
			stopSendButton();
			runnablefinished.runnablefin();
		}
		super.onBackPressed();
	}
	
	public void setBtn2Gone(){
		View view = findViewById(R.id.line_luckdraw_btn);
		view.setVisibility(View.GONE);
		btn2.setVisibility(View.GONE);
	}
	public void setTitleVisible(boolean b,String str){
		if(b){
			tv_title_luckydraw.setVisibility(View.VISIBLE);
			tv_title_luckydraw.setText(str);
		}
		else
			tv_title_luckydraw.setVisibility(View.GONE);
	}
	public void setTextIvestVisible(boolean b){
		if(!b)
			rl_tv_luckydraw.setVisibility(View.GONE);
	}
	public void setTitleLeft(){
		tv_title_luckydraw.setGravity(Gravity.CENTER_VERTICAL);
	}
}

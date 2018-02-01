package com.cstz.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.cstz_android.R;

public class MySmsStyleSwitch extends RelativeLayout{

	public OnClickTextSendSms onClickTextSendSms = null;
	public OnClickSwitch onClickSwitch = null;

	public boolean flag_sms_style = false;//默认为短信验证码
	private LinearLayout ll_switch_sms_style;
	private TextView tv_send;
	
	public MySmsStyleSwitch(Context context) {
		super(context);
	}
	
	public MySmsStyleSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.my_smsswitch_view, this);

        tv_send = (TextView) findViewById(R.id.front_findpwd_sendbutton_text);

        ll_switch_sms_style=(LinearLayout)findViewById(R.id.ll_switch_sms_style);
        
        tv_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onClickTextSendSms != null){
					onClickTextSendSms.sendSms();
				}
			}
		});
        
        ll_switch_sms_style.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				flag_sms_style = !flag_sms_style;
				if(flag_sms_style)
					tv_send.setText("语音验证码");
				else
					tv_send.setText("短信验证码");
				if(onClickTextSendSms != null){
				}
			}
		});
	}


	public void setOnClickTextSendSms(OnClickTextSendSms onClickTextSendSms)
	{
		this.onClickTextSendSms = onClickTextSendSms;
	}
	
	public void setOnClickSwitch(OnClickSwitch onClickSwitch)
	{
		this.onClickSwitch = onClickSwitch;
	}
	
	//设置发送短信或语音验证码的区域不可点击
	public void setSendClickable(boolean clickable){
		if(clickable){
			tv_send.setClickable(true);
			ll_switch_sms_style.setClickable(true);
		} else{
			tv_send.setClickable(false);
			ll_switch_sms_style.setClickable(false);
		}
	}
	
	//设置文字
	public void setText(String str){
		tv_send.setText(str);
	}
	
	/**
	 * 回调方法
	 * @author zzp
	 *
	 */
	public interface OnClickTextSendSms {
		void sendSms();
	}
	
	/**
	 * 回调方法
	 * @author zzp
	 *
	 */
	public interface OnClickSwitch {
		
	}
}

package com.cstz.front.findpwd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.common.App.PostType;
import com.cstz.common.MyActivity;
import com.cstz.common.Validate;
import com.cstz.common.view.MySmsStyleSwitch;
import com.cstz.common.view.MySmsStyleSwitch.OnClickTextSendSms;
import com.cstz.cstz_android.R;
import com.cstz.tools.Convert;
import com.cstz.tools.ToastMakeText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 忘记密码
 */
public class FindPwd extends MyActivity implements OnClickListener {
	private TextView title_tv;
	private ImageView title_back;
	private LinearLayout mButton; // 提交
	// private RelativeLayout _sendMsg; //发送验证码
	// private TextView tv_sendMsg; //发送验证码文本
	private EditText _phone; // 手机号码
	private EditText _code; // 验证码

	private boolean _isSendSMS = false; // 是否有发送验证码
	MySmsStyleSwitch switcher;
	private String phone;
	private String phone_user;
	private String phone_real;

	private LinearLayout ll_findpwd_root; // 根布局
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.stopSendButton();
		setContentView(R.layout.front_findpwd);
		setTransparentStatusBar(this,false);
		initview();
	}

	/**
	 * 初始化控件
	 */
	public void initview() {
		title_tv = (TextView) findViewById(R.id.title_tv);
		title_tv.setText("找回密码");
		title_back = (ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		_phone = (EditText) findViewById(R.id.front_findpwd_phone);
		_code = (EditText) findViewById(R.id.front_findpwd_code);
		mButton = (LinearLayout) findViewById(R.id.front_findpwd_button);// 提交，点击下一步
		ll_findpwd_root = (LinearLayout) findViewById(R.id.ll_findpwd_root);
		switcher = (MySmsStyleSwitch) findViewById(R.id.myswitcher);
		switcher.setOnClickTextSendSms(new OnClickTextSendSms() {
			@Override
			public void sendSms() {
				// 发送验证码
				if (_isSendSMS) {
					switcher.setSendClickable(false);// 设置控件发送消息按钮是否可点击
					return;
				}
				phone = Convert.trim(_phone.getText().toString());
				if (TextUtils.equals(phone, phone_user) && !TextUtils.isEmpty(phone))
					phone = phone_real;
				switcher.setSendClickable(false);// 设置控件发送消息按钮是否可点击
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("phone", phone);
				map.put("type", "2");
				map.put("sys", "android");//Android
				String url = !switcher.flag_sms_style ? "/common/sms" : "/common/voice";
				postData(map, url, PostType.MESSAGE, false);
			}
		});
		
		ll_findpwd_root.setOnTouchListener(new OnTouchListener() {//点击此布局就收回输入框
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				ll_findpwd_root.setFocusable(true);
				ll_findpwd_root.setFocusableInTouchMode(true);
				ll_findpwd_root.requestFocus();
				return false;
			}
		});
		
		listener();

//		if ( getIntent() != null && !TextUtils.isEmpty(getIntent().getStringExtra("phone")) ) {
//			String phone = getIntent().getStringExtra("phone");
//			_phone.setText(phone);
//			_phone.setSelection(phone.length());
//			
//			ll_findpwd_root.setFocusable(true);
//			ll_findpwd_root.setFocusableInTouchMode(true);
//			ll_findpwd_root.requestFocus();
//		}


//		SharedPreferencesData sp = new SharedPreferencesData(this);
//		phone_user = sp.getValue("phone");
//		phone_real = sp.getOtherValue("user0");

		phone = Convert.trim(_phone.getText().toString());
//		if (TextUtils.equals(phone, phone_user) && !TextUtils.isEmpty(phone))
//			phone = phone_real;
	}

	/**
	 * 控件的监听事件
	 */
	public void listener() {
		title_back.setOnClickListener(this);
		mButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.title_back) {
			stopSendButton();
			//当键盘未关闭时关闭
			hideSoftKeyboard();
			this.finish();
		} else if (v.getId() == R.id.front_findpwd_button) {
			// 提交
			if (ToastMakeText.isFastClick()) {
				return;
			}
			if (TextUtils.isEmpty(phone)) {
				ToastMakeText.showToast(FindPwd.this, "请输入手机号码", 1200);
				return;
			}
			if (!Validate.validatePhone(phone)) {
				ToastMakeText.showToast(FindPwd.this, "输入的手机号码格式不正确", 1200);
				return;
			}
			String code = Convert.trim(_code.getText().toString());
			if (!_isSendSMS) {
				ToastMakeText.showToast(FindPwd.this, "请获取验证码", 1200);
				return;
			}

			setButtonStyle(false);

			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("phone", phone);
			map2.put("code", code);
			map2.put("step", "1");
			postData(map2, "/findpwd/find", PostType.SUBMIT, true);
		}
	}

	public void stopSendButton()// 控制发送验证码的状态为不可点击
	{
		super.stopSendButton();

		// tv_sendMsg.setText("发送验证码");
		// _sendMsg.setClickable(true);
		if (switcher.flag_sms_style)
			switcher.setText("语音验证码");
		else
			switcher.setText("短信验证码");
		switcher.setSendClickable(true);
		_isSendSMS = false;
		_phone.setEnabled(true);
	}

	public void reflushSendButton(int count) {
		super.reflushSendButton(count);

		if (count >= SMS_TIME - 1) {
			// tv_sendMsg.setText("发送验证码");
			if (switcher.flag_sms_style)
				switcher.setText("语音验证码");
			else
				switcher.setText("短信验证码");
		} else {
			// tv_sendMsg.setText("重新发送("+count+")");
			switcher.setText("重新发送(" + count + ")");
		}

	}

	public void requestConnectionFail(PostType postType) {
		super.requestConnectionFail(postType);

		if (postType == PostType.MESSAGE) {
			stopSendButton();

		} else if (postType == PostType.SUBMIT) {
			setButtonStyle(true);
		}
	}

	public void requestFail(String msg, PostType postType) {
		super.requestFail(msg, postType);
		if (postType == PostType.MESSAGE) {
			stopSendButton();

		} else if (postType == PostType.SUBMIT) {
			setButtonStyle(true);
		}
		ToastMakeText.showToast(this, msg, 1200);
	}

	public void requestSuccess(JSONObject object, PostType postType) {
		super.requestSuccess(object, postType);
		if (object != null) {
			if (postType == PostType.MESSAGE) {
				_phone.setEnabled(false);
				_isSendSMS = true;
			}
			if (object.has("data")) {
				if (postType == PostType.SUBMIT) {
					stopSendButton();
					setButtonStyle(false);
					try {
						JSONObject data = object.getJSONObject("data");
						int step = Convert.toInt(data.getString("step").toString());
						String token = data.getString("token").toString();
						Intent intent = new Intent();

						if (step == 2) {
							intent.setClass(FindPwd.this, FindPwd_idNo.class);
						} else {
							intent.setClass(FindPwd.this, FindPwd_Update.class);
						}
						intent.putExtra("token", token);
						startActivity(intent);
						FindPwd.this.finish();

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void setButtonStyle(boolean enable)// 下一步按钮状态
	{
		if (enable) {
			mButton.setBackgroundResource(R.drawable.button_normal);
			mButton.setClickable(true);

		} else {
			mButton.setBackgroundResource(R.drawable.button_disable);
			mButton.setClickable(false);
		}
	}
}

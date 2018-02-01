package com.cstz.usercenter.setting;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.common.App.PostType;
import com.cstz.common.MyActivity;
import com.cstz.cstz_android.R;
import com.cstz.tools.Convert;
import com.cstz.tools.ToastMakeText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
/**
 * 修改支付密码,输入身份证验证
 * */
public class UpdatePayPwd_idNo extends MyActivity implements OnClickListener{
	private TextView title_tv;
	private ImageView title_back;
	private EditText _idno;
	private LinearLayout mButton;   
	
	private String _token;       
//	private InputMethodManager imm;      
	
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home_updatepaypwd_idno);
		setTransparentStatusBar(this, false);
		initview();
		
		Intent intent = getIntent();
		if(intent != null ){
			_token = intent.getStringExtra("token").toString();
		}
    }
     /**
      * 控件的初始化
      * */
    public void initview(){
    	title_tv = (TextView) findViewById(R.id.title_tv);
    	title_tv.setText("修改支付密码");
    	title_back = (ImageView) findViewById(R.id.title_back);
    	title_back.setVisibility(View.VISIBLE);
    	_idno = (EditText)findViewById(R.id.home_updatepaypwd_idno);
    	mButton = (LinearLayout) findViewById(R.id.home_updatepaypwd_idno_button);

//		_idno.setRawInputType(InputType.TYPE_CLASS_NUMBER);//指定初始输入为数字可切换到字母
    	listener();
    }
    /**
     * 按钮的事件的监听
     * */
    public void listener(){
    	title_back.setOnClickListener(this);
    	mButton.setOnClickListener(this);
    	
    }

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			case R.id.title_back:
				stopSendButton();
				//收回键盘
				_idno.clearFocus();
				hideSoftKeyboard();
				this.finish();
				break;
			case R.id.home_updatepaypwd_idno_button:
				
				String idno = _idno.getText().toString();
				
				if (ToastMakeText.isFastClick()) {
					return ;
				}
				
				if(TextUtils.isEmpty(idno))
				{
					ToastMakeText.showToast(this, "请输入身份证后6位", 1200);
					return;
				}
				
				if(idno.length()<6)
				{
					ToastMakeText.showToast(this, "输入数字小于6位", 1200);
					return;
				}
				setButtonStyle(false);
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("step", 2);
				map.put("token",_token);
				map.put("idno",idno);
				postData(map,"/user/updatePayPwd",PostType.SUBMIT,true);
				break;
			default:
				break;
		}
	}
	
	public void requestConnectionFail(PostType postType)
	{
		super.requestConnectionFail(postType);
		
		setButtonStyle(true);
	}
	
	public void requestFail(String msg,PostType postType)
	{		
		super.requestFail(msg, postType);
		if(postType == PostType.SUBMIT)
		{
			setButtonStyle(true);
		}
		ToastMakeText.showToast(this, msg, 1200);
	}
	public void requestSuccess(JSONObject object,PostType postType)
	{
		super.requestSuccess(object, postType);
		if(object!=null)
		{
			
			if(postType == PostType.SUBMIT)
			{	
				setButtonStyle(false);
				
				try {
					JSONObject data = object.getJSONObject("data");
					if(data != null)
					{
						int step = Convert.toInt(data.getString("step").toString());
						String token =  data.getString("token").toString();
						if(step == 3)
						{
							Intent intent = new Intent();
							intent.setClass(this, UpdatePayPwd_Update.class);
							intent.putExtra("token", token);
							startActivity(intent);
							this.finish();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void setButtonStyle(boolean enable)
	{
		if(enable)
		{
			mButton.setBackgroundResource(R.drawable.button_normal);
			mButton.setClickable(true);
			
		}else
		{
			mButton.setBackgroundResource(R.drawable.button_disable);
			mButton.setClickable(false);
		}
	}
}

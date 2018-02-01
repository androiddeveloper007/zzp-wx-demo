package control.pattern;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cstz.common.App;
import com.cstz.common.App.PostType;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.view.MyDialog;
import com.cstz.common.view.MyDialog.Result;
import com.cstz.cstz_android.R;
import com.cstz.tools.ToastMakeText;
import com.ziyeyouhu.library.KeyboardTouchListener;
import com.ziyeyouhu.library.KeyboardUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import control.pattern.UnlockGesturePasswordActivity.Param;
import control.pattern.tools.SettingUtils;
import control.pattern.utils.LockPatternUtils;
/**
 * 忘记手势密码，验证登录密码
 * */
public class ForgetPatternComfirm extends MyActivity implements OnClickListener{
	private TextView title_tv;
	private ImageView title_back;
	private EditText _idno;
	private LinearLayout mButton;   
	private LinearLayout root_view;
	private ScrollView scrollView1;
	private SharedPreferencesData sp;
	public static final int RESULT_CODE_SUCCESS = 10003;
	public static final int RESULT_CODE_SUCCESS_NORMAL = 10004;
	private KeyboardUtil keyboardUtil;
	private String userType="0";
	private String regAsDeposit="0";
	private String depositCheck="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forget_pattern_comfirm);
		setTransparentStatusBar(this, false);
		initview();
		initMoveKeyBoard();
		sp = new SharedPreferencesData(this);
    }
    
    private void initMoveKeyBoard() {
        keyboardUtil = new KeyboardUtil(this, root_view, scrollView1);
	    keyboardUtil.setKeyBoardStateChangeListener(new KeyBoardStateListener());
	    keyboardUtil.setInputOverListener(new inputOverListener());
        _idno.setOnTouchListener(new KeyboardTouchListener(keyboardUtil, KeyboardUtil.INPUTTYPE_ABC, -1));
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            if(keyboardUtil.isShow){
                keyboardUtil.hideSystemKeyBoard();
                keyboardUtil.hideAllKeyBoard();
                keyboardUtil.hideKeyboardLayout();
            }else {
                return super.onKeyDown(keyCode, event);
            }
            return false;
        } else
            return super.onKeyDown(keyCode, event);
    }
    
    public void initview(){
    	title_tv = (TextView) findViewById(R.id.title_tv);
    	title_tv.setText("关闭手势密码");
    	title_back = (ImageView) findViewById(R.id.title_back);
    	title_back.setVisibility(View.VISIBLE);
    	_idno = (EditText)findViewById(R.id.home_updatepaypwd_idno);
    	mButton = (LinearLayout) findViewById(R.id.home_updatepaypwd_idno_button);
    	root_view = (LinearLayout) findViewById(R.id.root_view);
    	scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
    	listener();
    }

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
				if(TextUtils.isEmpty(idno))
				{
					ToastMakeText.showToast(this, "请输入登录密码", 1200);
					return;
				}
				if (ToastMakeText.isFastClick()) {
			        return ;
			    }
				setButtonStyle(false);
				login();
				break;
			default:
				break;
		}
	}
	
	private void login() {
    	String phone = sp.getOtherValue("user0");
    	String pwd = _idno.getText().toString();
    	Map<String,Object> map=new HashMap<>();
    	map.put("username", phone); 
    	map.put("pwd", pwd);
    	postData(map,"/loginAction",PostType.SUBMIT,true);
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
				JSONObject data;
				try {
					data = object.getJSONObject("data");
					if(data!=null)
					{
						sp.setValue("token", data.getString("token"));
						String phone = sp.getOtherValueSecurity("user0");
						String user0 = sp.getOtherValue("user0");
						if(data.has("userType"))
							userType = data.getString("userType");
						if(data.has("regAsDeposit"))
							regAsDeposit = data.getString("regAsDeposit");
						if(data.has("depositCheck"))
							depositCheck = data.getString("depositCheck");
						new LockPatternUtils(ForgetPatternComfirm.this,phone).clearLock();
						SettingUtils.getInstance(App.getInstance()).putIsPatternPwdOn(user0,false);
						setResult(RESULT_CODE_SUCCESS);
						MyDialog dialog = new MyDialog(this, "手势密码清除成功",Result.SUCCESS,750);
						dialog.setMyCallback(new MyCallback() {
							@Override
							public void callback() {
								//判断如果是从app启动页打开的清除手势密码，那么清楚成功后直接进入到设置手势密码
								if( ForgetPatternComfirm.this.getIntent().getStringExtra("fromNormal") != null){
									Intent intent = new Intent(ForgetPatternComfirm.this, CreatePatternPwdActivity.class);
									intent.putExtra(Param.REQ_TYPE, Param.ReqType.CREATE_FROM_LOGIN);
									intent.putExtra("fromNormal", "");
									intent.putExtra("userType",userType);
									intent.putExtra("regAsDeposit",regAsDeposit);
									intent.putExtra("depositCheck",depositCheck);
									startActivity(intent);
								}
								ForgetPatternComfirm.this.finish();
							}
							@Override
							public void doing() {
							}
						});
						dialog.showDialog();
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

	class KeyBoardStateListener implements KeyboardUtil.KeyBoardStateChangeListener {
        @Override
        public void KeyBoardStateChange(int state, EditText editText) {
        }
    }

    class inputOverListener implements KeyboardUtil.InputFinishListener {
        @Override
        public void inputHasOver(int onclickType, EditText editText) {
        }
    }
}

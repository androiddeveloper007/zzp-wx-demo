package com.cstz.front;


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
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.Validate;
import com.cstz.common.WebViewReleaseCg;
import com.cstz.common.view.MyDialog;
import com.cstz.common.view.MyDialog.Result;
import com.cstz.common.view.MySmsStyleSwitch;
import com.cstz.common.view.MySmsStyleSwitch.OnClickSwitch;
import com.cstz.common.view.MySmsStyleSwitch.OnClickTextSendSms;
import com.cstz.cstz_android.R;
import com.cstz.tab.MainActivity;
import com.cstz.tools.Convert;
import com.cstz.tools.ToastMakeText;
import com.ziyeyouhu.library.KeyboardTouchListener;
import com.ziyeyouhu.library.KeyboardUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import control.pattern.CreatePatternPwdActivity;
import control.pattern.UnlockGesturePasswordActivity;


/**
 * 注册
 */
public class Register extends MyActivity implements OnClickListener {
    private TextView title_tv;
    private TextView tv_register_agreement;
    private ImageView title_back;
    private LinearLayout mRegButton;
    //	private LinearLayout ly_sendMsg;
//	private TextView tv_sendMsg;     //发送验证码
    private EditText _phone;         //手机号码
    private EditText _pwd;           //密码
    private EditText _refferee;      //推荐人
    private EditText _smsCode;       //短信验证码

    private boolean _isSendSMS = false;
    private TextView _mRightNavigation;
    MySmsStyleSwitch switcher;
    private KeyboardUtil keyboardUtil;
    private LinearLayout root_view;
    private ScrollView scrollView1;
    private String phone,pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.stopSendButton();
        setContentView(R.layout.register);
        setTransparentStatusBar(this,false);
        initview();
    }

    /**
     * 初始化控件
     */
    public void initview() {

        title_tv = (TextView) findViewById(R.id.title_tv);
        tv_register_agreement = (TextView) findViewById(R.id.tv_register_agreement);
        title_tv.setText("注册");
        _mRightNavigation = (TextView) findViewById(R.id.navigation_right);
        _mRightNavigation.setText("登录");
        _mRightNavigation.setTextColor(getResources().getColor(R.color.text_color_right));
        title_back = (ImageView) findViewById(R.id.title_back);
//    	tv_sendMsg = (TextView) findViewById(R.id.register_sendbutton_text);
//    	ly_sendMsg = (LinearLayout)findViewById(R.id.register_sendbutton);
        title_back.setVisibility(View.VISIBLE);
        mRegButton = (LinearLayout) findViewById(R.id.register_button);
        _phone = (EditText) findViewById(R.id.register_phone);
        _pwd = (EditText) findViewById(R.id.register_pwd);
        _refferee = (EditText) findViewById(R.id.register_refferee);
        _smsCode = (EditText) findViewById(R.id.register_smsCode);


        root_view = (LinearLayout) findViewById(R.id.root_view);
        scrollView1 = (ScrollView) findViewById(R.id.scrollView2);

        switcher = (MySmsStyleSwitch) findViewById(R.id.myswitcher);
        switcher.setOnClickSwitch(new OnClickSwitch() {

        });
        switcher.setOnClickTextSendSms(new OnClickTextSendSms() {
            @Override
            public void sendSms() {
                String phone = Convert.trim(_phone.getText().toString());
                //发送验证码
                if (ToastMakeText.isFastClick()) {
                    return;
                }
                if (_isSendSMS) {
                    switcher.setSendClickable(false);
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    ToastMakeText.showToast(Register.this, "手机号码不能为空", 1200);
                    return;
                }

                if (!Validate.validatePhone(phone)) {
                    ToastMakeText.showToast(Register.this, "手机号码格式不正确", 1200);
                    return;
                }
                switcher.setSendClickable(false);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("phone", phone);
                map2.put("type", "1");
                map2.put("sys", "android");
                String url = !switcher.flag_sms_style ? "/common/sms" : "/common/voice";
                postData(map2, url, PostType.MESSAGE, false);
            }
        });

        clicklistener();
        initMoveKeyBoard();
    }

    private void initMoveKeyBoard() {
        keyboardUtil = new KeyboardUtil(this, root_view, scrollView1);
        keyboardUtil.setOtherEdittext(_phone);
        keyboardUtil.setOtherEdittext(_refferee);
        keyboardUtil.setOtherEdittext(_smsCode);
//        keyboardUtil.setKeyBoardStateChangeListener(new KeyBoardStateListener());
//        keyboardUtil.setInputOverListener(new inputOverListener());
        _pwd.setOnTouchListener(new KeyboardTouchListener(keyboardUtil, KeyboardUtil.INPUTTYPE_ABC, -1));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (keyboardUtil.isShow) {
                keyboardUtil.hideSystemKeyBoard();
                keyboardUtil.hideAllKeyBoard();
                keyboardUtil.hideKeyboardLayout();
            } else {
                return super.onKeyDown(keyCode, event);
            }

            return false;
        } else
            return super.onKeyDown(keyCode, event);
    }

    /**
     * 控件的点击事件监听
     */
    public void clicklistener() {
        title_back.setOnClickListener(this);
        mRegButton.setOnClickListener(this);
//    	ly_sendMsg.setOnClickListener(this);
        _mRightNavigation.setOnClickListener(this);
        tv_register_agreement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        phone = Convert.trim(_phone.getText().toString());
        pwd = Convert.trim(_pwd.getText().toString());
        String refferee = Convert.trim(_refferee.getText().toString());
        String smsCode = Convert.trim(_smsCode.getText().toString());
        switch (v.getId()) {
            case R.id.title_back:
                stopSendButton();
                this.finish();
                break;
            case R.id.register_button:
                if (ToastMakeText.isFastClick()) {
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    ToastMakeText.showToast(Register.this, "手机号码不能为空", 1200);
                    return;
                }
                if (!Validate.validatePhone(phone)) {
                    ToastMakeText.showToast(Register.this, "手机号码格式不正确", 1200);
                    return;
                }
                if (!_isSendSMS) {
                    ToastMakeText.showToast(Register.this, "请获取验证码", 1200);
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    ToastMakeText.showToast(Register.this, "密码不能为空", 1200);
                    return;
                }
                if (!Validate.validatePwd(pwd)) {
                    ToastMakeText.showToast(Register.this, "密码格式不正确", 1200);
                    return;
                }
                if (refferee.length() > 0) {
                    if (!Validate.validatePhone(refferee)) {
                        ToastMakeText.showToast(Register.this, "推荐人手机号码格式不正确", 1200);
                        return;
                    }
                }
                setButtonStyle(false);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("phone", phone);
                map.put("pwd", pwd);
                map.put("userType", 15);//android:15注册用户  5ios端注册用户
                map.put("refferee", refferee);
                map.put("phoneCode", smsCode);
                postData(map, "/registerAction", PostType.SUBMIT, true);
                break;
            case R.id.navigation_right:
                super.stopSendButton();
                Intent intent = new Intent();
                intent.setClass(Register.this, Login.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.tv_register_agreement:
                Intent i = new Intent(this, WebViewReleaseCg.class);
                i.putExtra("url", Config.getHttpConfig() + "/thirdParty/getAgreement?type=1");
                i.putExtra("title", "五星财富协议");
                i.putExtra("wap", "1");//设置页面非初始最大显示
                startActivity(i);
                break;
            default:break;
        }
    }

    public void stopSendButton() {
        super.stopSendButton();

//		tv_sendMsg.setText("发送验证码");
//		ly_sendMsg.setClickable(true);
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
//			tv_sendMsg.setText("发送验证码");
            if (switcher.flag_sms_style)
                switcher.setText("语音验证码");
            else
                switcher.setText("短信验证码");
        } else {
//			tv_sendMsg.setText("重新发送("+count+")");
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
                _isSendSMS = true;
                _phone.setEnabled(false);

            } else if (postType == PostType.SUBMIT) {
                stopSendButton();
                try {
                    JSONObject data = object.getJSONObject("data");
                    if (data != null) {
                        setButtonStyle(false);

                        new SharedPreferencesData(this).setValue("token", data.getString("token"));

                        MyDialog dialog = new MyDialog(Register.this, object.getString("msg"), Result.SUCCESS);
                        dialog.setMyCallback(new MyCallback() {
                            @Override
                            public void callback() {
                                //判断是否第一次设置手势密码
                                boolean isNotFirstIn_pattern = new SharedPreferencesData(Register.this).getBooleanByUser(phone, "isNotFirstIn_pattern");
                                String userType = "15";
                                String regAsDeposit = "1";
                                String depositCheck = "0";
                                if (!isNotFirstIn_pattern) {
                                    Intent intent = new Intent(Register.this, CreatePatternPwdActivity.class);
                                    intent.putExtra(UnlockGesturePasswordActivity.Param.REQ_TYPE, UnlockGesturePasswordActivity.Param.ReqType.CREATE_FROM_REGISTER);
                                    intent.putExtra("userType",userType);
                                    intent.putExtra("regAsDeposit",regAsDeposit);
                                    intent.putExtra("depositCheck",depositCheck);
                                    startActivity(intent);
                                    Register.this.finish();
                                    //登陆成功记录登陆手机
                                    SharedPreferencesData sp = new SharedPreferencesData(Register.this);
                                    sp.setOtherValue("user0", phone);
                                    sp.setOtherValue("pwd0", pwd);
                                    sp.setBoolean("hasLogin", true);
                                    App.userType=userType;
                                    App.regAsDeposit=regAsDeposit;
                                    App.depositCheck=depositCheck;
//                                    sp.setValue("userType",userType);//用户类型，-1代表借款人
//                                    sp.setValue("regAsDeposit",regAsDeposit);//是否新注册 1
//                                    sp.setValue("depositCheck",depositCheck);//是否已开通存管 1
                                } else {
                                    SharedPreferencesData sp = new SharedPreferencesData(Register.this);
                                    sp.setOtherValue("user0", phone);
                                    sp.setOtherValue("pwd0", pwd);
                                    sp.setBoolean("hasLogin", true);
                                    App.userType=userType;
                                    App.regAsDeposit=regAsDeposit;
                                    App.depositCheck=depositCheck;
//                                    sp.setValue("userType",userType);//用户类型，-1代表借款人
//                                    sp.setValue("regAsDeposit",regAsDeposit);//是否新注册 1
//                                    sp.setValue("depositCheck",depositCheck);//是否已开通存管 1
                                    Intent intent = new Intent();
                                    intent.putExtra("home", "");
                                    intent.putExtra("userType",userType);
                                    intent.putExtra("regAsDeposit",regAsDeposit);
                                    intent.putExtra("depositCheck",depositCheck);
                                    intent.setClass(Register.this, MainActivity.class);
                                    Register.this.startActivity(intent);
                                    Register.this.finish();
                                    new SharedPreferencesData(Register.this).setBoolean("hasLogin", true);
                                }
                            }

                            @Override
                            public void doing() {
                            }
                        });
                        dialog.showDialog();
                    }

                } catch (JSONException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }

            }
        }
    }

    private void setButtonStyle(boolean enable) {
        if (enable) {
            mRegButton.setBackgroundResource(R.drawable.button_normal);
            mRegButton.setClickable(true);

        } else {
            mRegButton.setBackgroundResource(R.drawable.button_disable);
            mRegButton.setClickable(false);
        }
    }
}



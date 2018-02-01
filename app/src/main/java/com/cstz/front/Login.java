package com.cstz.front;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cstz.common.App;
import com.cstz.common.App.PostType;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.SysApplication;
import com.cstz.cstz_android.R;
import com.cstz.front.findpwd.FindPwd;
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
 * 登录
 *
 * @author rxh
 */
public class Login extends MyActivity implements OnClickListener {
    private TextView title_tv;
    private ImageView title_back;
    private LinearLayout Layout_login; //登录
    private TextView tv_login_lose; //忘记密码
    private com.cstz.MyWidget.MClearEditText edit_phone;
    private com.cstz.MyWidget.MClearEditText edit_pwd;
    private TextView _mRightNavigation;
    private String phone;
    private String pwd;
    private KeyboardUtil keyboardUtil;
    private LinearLayout root_view;
    private ScrollView scrollView1;
    private String phone_real;
    private String phone_user;

    private static Handler handler = new Handler();
    private String userType="0";
    private String regAsDeposit="0";
    private String depositCheck="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        setTransparentStatusBar(this,false);
        initView();

        //申请存储权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }

    public void initView() {
        edit_phone = (com.cstz.MyWidget.MClearEditText) findViewById(R.id.editText1);//MClear
        edit_pwd = (com.cstz.MyWidget.MClearEditText) findViewById(R.id.editText2);//MClear
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("登录");
        _mRightNavigation = (TextView) findViewById(R.id.navigation_right);
        _mRightNavigation.setText("注册");
        _mRightNavigation.setTextColor(getResources().getColor(R.color.text_color_right));
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        Layout_login = (LinearLayout) findViewById(R.id.Layout_login);
        tv_login_lose = (TextView) findViewById(R.id.tv_login_lose);
        root_view = (LinearLayout) findViewById(R.id.root_view);
        scrollView1 = (ScrollView) findViewById(R.id.scrollView2);
        listener();
        initMoveKeyBoard();
        edit_phone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    edit_pwd.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                            SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, edit_pwd.getLeft() + 5, edit_pwd.getTop() + 5, 0));
                    edit_pwd.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                            SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, edit_pwd.getLeft() + 5, edit_pwd.getTop() + 5, 0));
                    return true;
                }
                return false;
            }
        });
        SharedPreferencesData sp = new SharedPreferencesData(this);
        phone_real = sp.getOtherValue("user0");
        phone_user = sp.getOtherValue("phone0");

        if (!TextUtils.isEmpty(phone_user) && !TextUtils.isEmpty(phone_real)) {
            edit_phone.setText(phone_user);
            edit_pwd.requestFocus();
            return;
        }
    }

    private void initMoveKeyBoard() {
        keyboardUtil = new KeyboardUtil(this, root_view, scrollView1);
        keyboardUtil.setOtherEdittext(edit_phone);
        keyboardUtil.setKeyBoardStateChangeListener(new KeyBoardStateListener());
        keyboardUtil.setInputOverListener(new inputOverListener());
        keyboardUtil.setOnClickFinishText(new onClickFinishTextListener());
        edit_pwd.setOnTouchListener(new KeyboardTouchListener(keyboardUtil, KeyboardUtil.INPUTTYPE_ABC, -1));
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
     * 给控件设置监听
     */
    public void listener() {
        title_back.setOnClickListener(this);
        tv_login_lose.setOnClickListener(this);
        Layout_login.setOnClickListener(this);
        _mRightNavigation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                if (getIntent().getStringExtra("pattern_warn") != null) {
                    Intent intent = new Intent();
                    intent.putExtra("tab0", "tab0");
                    intent.setClass(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
                this.finish();
                break;
            case R.id.tv_login_lose:
                Intent intent1 = new Intent(Login.this, FindPwd.class);
                startActivity(intent1);
                break;
            case R.id.navigation_right:
                this.stopSendButton();//归零定时器
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.Layout_login:
                login();
                break;
            default:
                break;
        }
    }

    public void requestConnectionFail(PostType postType) {
        super.requestConnectionFail(postType);

        setButtonStyle(true);
    }

    public void requestFail(String msg, PostType postType) {
        super.requestFail(msg, postType);
        ToastMakeText.showToast(this, msg, 1200);
        setButtonStyle(true);
    }

    public void requestSuccess(JSONObject object, PostType postType) {

        super.requestSuccess(object, postType);
        try {

            if (object != null) {
                JSONObject data = object.getJSONObject("data");
                if (data != null) {
                    new SharedPreferencesData(Login.this).setValue("token", data.getString("token"));
                    if(data.has("userType"))
                        userType = data.getString("userType");
                    if(data.has("regAsDeposit"))
                        regAsDeposit = data.getString("regAsDeposit");
                    if(data.has("depositCheck"))
                        depositCheck = data.getString("depositCheck");
                    //判断是否第一次设置手势密码
                    boolean isNotFirstIn_pattern = new SharedPreferencesData(this).getBooleanByUser(phone, "isNotFirstIn_pattern");
                    if (!isNotFirstIn_pattern) {
                        Intent intent = new Intent(this, CreatePatternPwdActivity.class);
                        intent.putExtra(UnlockGesturePasswordActivity.Param.REQ_TYPE, UnlockGesturePasswordActivity.Param.ReqType.CREATE_FROM_LOGIN);
                        intent.putExtra("userType",userType);
                        intent.putExtra("regAsDeposit",regAsDeposit);
                        intent.putExtra("depositCheck",depositCheck);
                        startActivity(intent);
                        Login.this.finish();
                        //登陆成功记录登陆手机
//                        showLog("userType:"+userType+"  regAsDeposit:"+regAsDeposit+"  depositCheck:"+depositCheck);
                        SharedPreferencesData sp = new SharedPreferencesData(this);
                        sp.setOtherValue("user0", phone);
                        sp.setOtherValue("pwd0", pwd);
                        sp.setBoolean("hasLogin", true);
                        App.userType=userType;
                        App.regAsDeposit=regAsDeposit;
                        App.depositCheck=depositCheck;
//                        sp.setValue("userType",userType);//用户类型，-1代表借款人
//                        sp.setValue("regAsDeposit",regAsDeposit);//是否新注册 1
//                        sp.setValue("depositCheck",depositCheck);//是否已开通存管 1
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("home", "");
                        intent.putExtra("userType",userType);
                        intent.putExtra("regAsDeposit",regAsDeposit);
                        intent.putExtra("depositCheck",depositCheck);
                        intent.setClass(Login.this, MainActivity.class);
                        startActivity(intent);
                        //登陆成功记录登陆手机
                        SharedPreferencesData sp = new SharedPreferencesData(this);
                        sp.setOtherValue("user0", phone);
                        sp.setOtherValue("pwd0", pwd);
                        sp.setBoolean("hasLogin", true);
                        App.userType=userType;
                        App.regAsDeposit=regAsDeposit;
                        App.depositCheck=depositCheck;
//                        showLog("userType:"+userType+" regAsDeposit:"+regAsDeposit+" depositCheck:"+depositCheck);
//                        sp.setValue("userType",userType);//用户类型，-1代表借款人
//                        sp.setValue("regAsDeposit",regAsDeposit);//是否新注册 1
//                        sp.setValue("depositCheck",depositCheck);//是否已开通存管 1
                        SysApplication.getInstance().finishAllActivity();//清空Activity栈
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setButtonStyle(boolean enable) {
        if (enable) {
            Layout_login.setBackgroundResource(R.drawable.button_normal);
            Layout_login.setClickable(true);

        } else {
            Layout_login.setBackgroundResource(R.drawable.button_disable);
            Layout_login.setClickable(false);
        }
    }

    class KeyBoardStateListener implements KeyboardUtil.KeyBoardStateChangeListener {
        @Override
        public void KeyBoardStateChange(int state, EditText editText) {
            if (state == 1) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView1.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                        edit_pwd.requestFocus();
                    }
                }, 50);
            } else if (state == 2) {

            }
        }
    }

    class inputOverListener implements KeyboardUtil.InputFinishListener {
        @Override
        public void inputHasOver(int onclickType, EditText editText) {
        }
    }

    class onClickFinishTextListener implements KeyboardUtil.OnClickFinishTextListener{
        @Override
        public void onClickFinishText() {
            login();
        }
    }
    private void login(){
        phone = Convert.trim(edit_phone.getText().toString());
        pwd = Convert.trim(edit_pwd.getText().toString());
        if (TextUtils.isEmpty(phone)) {
            ToastMakeText.showToast(Login.this, "手机号不能为空", 1200);
            return;
        }
        if (ToastMakeText.isFastClick()) {
            return;
        }
        if (!TextUtils.isEmpty(phone) && TextUtils.equals(phone, phone_user)) {
            phone = phone_real;
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastMakeText.showToast(Login.this, "登录密码不能为空", 1200);
            return;
        }
        setButtonStyle(false);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", phone);
        map.put("pwd", pwd);
        postData(map, "/loginAction", PostType.SUBMIT, true);
    }
}

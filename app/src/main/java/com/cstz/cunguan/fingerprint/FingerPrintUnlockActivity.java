package com.cstz.cunguan.fingerprint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.cstz.common.App;
import com.cstz.common.App.PostType;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.SysApplication;
import com.cstz.cstz_android.R;
import com.cstz.front.Login;
import com.cstz.tab.MainActivity;
import com.cstz.tools.ToastMakeText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import control.pattern.tools.SettingUtils;
import control.pattern.utils.LockPatternUtils;

/**
 * 指纹解锁界面
 *
 * @author zzp
 */
public class FingerPrintUnlockActivity extends MyActivity {

    @BindView(R.id.fpv_fingerprint_unlock)
    FingerPrinterView fingerPrinterView;
    @BindView(R.id.tv_fingerprint_username)
    TextView tvFingerprintUsername;
    private Context context;
    private int fingerErrorNum = 0; // 指纹错误次数
//    RxFingerPrinter rxfingerPrinter;
    private SharedPreferencesData sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fingerprint_unlock);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        context = this;
        sp = new SharedPreferencesData(this);
        SysApplication.getInstance().addActivity(this);
        initView();

    }

    private void initView(){
        fingerPrinterView.setOnStateChangedListener(new FingerPrinterView.OnStateChangedListener() {
            @Override public void onChange(int state) {
                if (state == FingerPrinterView.STATE_CORRECT_PWD) {
                    fingerErrorNum = 0;
                    Toast.makeText(context, "指纹识别成功", Toast.LENGTH_SHORT).show();
                    login();
                }
                if (state == FingerPrinterView.STATE_WRONG_PWD) {
                    if(5-fingerErrorNum==0){
                        //弹出提示框，输入登录密码重新验证指纹
                        ToastMakeText.showToast((Activity) context, "指纹认证失败，请重新登录！",3000);
                        fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
                        postData(null, "/logout", null, PostType.SUBMIT);
                        sp.removeAll();
                        Intent intent = new Intent(context, Login.class);
                        intent.putExtra("pattern_warn", "");
                        startActivity(intent);
                        finish();
                        return;
                    }
                    Toast.makeText(context, "指纹识别失败，还剩" + (5-fingerErrorNum) + "次机会",
                            Toast.LENGTH_SHORT).show();
                    fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
                }
            }
        });
        /*rxfingerPrinter = new RxFingerPrinter(context);
        rxfingerPrinter.unSubscribe(context);
        Subscription subscription = rxfingerPrinter.begin().subscribe(new Subscriber<Boolean>() {
            @Override
            public void onStart() {
                super.onStart();
                if (fingerPrinterView.getState() == FingerPrinterView.STATE_SCANING) {
                    return;
                } else if (fingerPrinterView.getState() == FingerPrinterView.STATE_CORRECT_PWD
                        || fingerPrinterView.getState() == FingerPrinterView.STATE_WRONG_PWD) {
                    fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
                } else {
                    fingerPrinterView.setState(FingerPrinterView.STATE_SCANING);
                }
            }
            @Override
            public void onCompleted() {}
            @Override
            public void onError(Throwable e) {
                if(e instanceof FPerException){
                    Toast.makeText(context,((FPerException) e).getDisplayMessage(),Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNext(Boolean aBoolean) {
                if(aBoolean){
                    fingerPrinterView.setState(FingerPrinterView.STATE_CORRECT_PWD);
                }else{
                    fingerErrorNum++;
                    fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
                }
            }
        });
        rxfingerPrinter.addSubscription(this,subscription);*/
    }

    private void login() {
        SharedPreferencesData sp = new SharedPreferencesData(this);
        String phone = sp.getOtherValue("user0");
        String pwd = sp.getOtherValue("pwd0");
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
            Map<String, Object> map = new HashMap<>();
            map.put("username", phone);
            map.put("pwd", pwd);
            postData(map, "/loginAction", PostType.SUBMIT, false);
        }
    }

    @Override
    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        try {
            if (object != null) {
                JSONObject data = object.getJSONObject("data");
                if (data != null) {
                    new SharedPreferencesData(FingerPrintUnlockActivity.this).setValue("token", data.getString("token"));
                    Intent intent = new Intent();
                    intent.putExtra("home", "");
                    intent.setClass(FingerPrintUnlockActivity.this, MainActivity.class);
                    FingerPrintUnlockActivity.this.startActivity(intent);
                    new SharedPreferencesData(this).setBoolean("hasLogin", true);
                    FingerPrintUnlockActivity.this.finish();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestFail(String msg, PostType postType) {
        ToastMakeText.showToast(this, msg + "，请重新登录", 2000);
        //此处当为登录密码错误时，应该跳到登录页重新登陆
        SharedPreferencesData sp = new SharedPreferencesData(this);
        String phone = sp.getOtherValueSecurity("user0");
        String user0 = sp.getOtherValue("user0");
        sp.setBooleanByUser(user0, "isNotFirstIn_pattern", false);
        new LockPatternUtils(this, phone).clearLock();
        SettingUtils.getInstance(App.getInstance()).putIsPatternPwdOn(user0, false);
        startActivity(new Intent(this, Login.class));
        super.requestFail(msg, postType);
        finish();
    }

    @OnClick({R.id.fpv_fingerprint_unlock, R.id.tv_fingerprint_unlock_other})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fpv_fingerprint_unlock:
                break;
            case R.id.tv_fingerprint_unlock_other:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        rxfingerPrinter.unSubscribe(context);
    }
}
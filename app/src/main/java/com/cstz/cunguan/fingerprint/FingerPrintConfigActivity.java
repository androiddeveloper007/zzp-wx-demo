package com.cstz.cunguan.fingerprint;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.common.App;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.SysApplication;
import com.cstz.common.view.MyDialog;
import com.cstz.cstz_android.R;
import com.cstz.tools.ToastMakeText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import control.pattern.tools.SettingUtils;

import static com.cstz.usercenter.setting.ChangeFingerprintState.RESULT_CODE_SUCCESS;

/**
 * 指纹设置界面
 *
 * @author zzp
 */
public class FingerPrintConfigActivity extends MyActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.fpv_fingerprint_config)
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
        setContentView(R.layout.activity_fingerprint_config);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        context = this;
        sp = new SharedPreferencesData(this);
        SysApplication.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        titleTv.setText("指纹密码");
        titleBack.setVisibility(View.VISIBLE);
        fingerPrinterView.setOnStateChangedListener(new FingerPrinterView.OnStateChangedListener() {
            @Override
            public void onChange(int state) {
                if (state == FingerPrinterView.STATE_CORRECT_PWD) {
                    fingerErrorNum = 0;
                    tvFingerprintUsername.setText("指纹认证成功");
                    MyDialog dialog = new MyDialog(context, "指纹认证成功", MyDialog.Result.SUCCESS, 600);
                    dialog.setMyCallback(new MyCallback() {
                        @Override
                        public void callback() {
                            SettingUtils.getInstance(App.getInstance()).putIsFingerPwdOn(sp.getOtherValue("user0"),true);
                            setResult(RESULT_CODE_SUCCESS);
                            finish();
                        }
                        @Override
                        public void doing() {}
                    });
                    dialog.showDialog();
                    fingerPrinterView.setState(FingerPrinterView.STATE_CORRECT_PWD);
                }
                if (state == FingerPrinterView.STATE_WRONG_PWD) {
                    if (5 - fingerErrorNum == 0) {//弹出提示框，输入登录密码重新验证指纹
                        ToastMakeText.showToast((Activity) context, "指纹认证失败，请稍后重试！", 3000);
                        fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
                        return;
                    }
                    tvFingerprintUsername.setText("指纹认证失败，还剩" + (5-fingerErrorNum) + "次机会");
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
                if (e instanceof FPerException) {
                    Toast.makeText(context, ((FPerException) e).getDisplayMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    fingerPrinterView.setState(FingerPrinterView.STATE_CORRECT_PWD);
                } else {
                    fingerErrorNum++;
                    fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
                }
            }
        });
        rxfingerPrinter.addSubscription(this, subscription);*/
    }

    @OnClick({R.id.fpv_fingerprint_config,R.id.title_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fpv_fingerprint_config:
                break;
            case R.id.title_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
//        rxfingerPrinter.unSubscribe(context);
        super.onDestroy();
    }
}
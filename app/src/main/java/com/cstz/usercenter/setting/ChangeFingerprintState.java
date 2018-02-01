package com.cstz.usercenter.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.common.App;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.fingerprint.FingerPrintConfigActivity;
import com.leaking.slideswitch.SlideSwitch;
import com.leaking.slideswitch.SlideSwitch.SlideListener;

import control.pattern.UnlockGesturePasswordActivity.Param;
import control.pattern.tools.SettingUtils;

/**
 * 指纹密码修改状态
 *
 * @author rxh
 */
public class ChangeFingerprintState extends MyActivity implements OnClickListener {
    private TextView title_tv;
    private ImageView title_back;
    SlideSwitch mSlide_pattern;

    public static final int REQ_CODE_CREATE = 10001;
    public static final int REQ_CODE_CLOSE = 10002;
    public static final int RESULT_CODE_SUCCESS = 10003;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_fingerprint);
        setTransparentStatusBar(this, true);
        SharedPreferencesData data = new SharedPreferencesData(this);
        phone = data.getOtherValue("user0");
        initView();
    }

    public void initView() {
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("指纹密码");
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);

        mSlide_pattern = (SlideSwitch) findViewById(R.id.slide_pattern);

        //根据缓存中布尔值是否显示修改手势密码布局
        if (SettingUtils.getInstance(App.getInstance()).isFingerPwdOn(phone)) {
            mSlide_pattern.initState(true);
        } else {
            mSlide_pattern.initState(false);
        }

        mSlide_pattern.setSlideListener(new SlideListener() {
            @Override
            public void open() {
                //开启手势密码
                if (!SettingUtils.getInstance(App.getInstance()).isFingerPwdOn(phone)) {
                    Intent intent9 = new Intent(ChangeFingerprintState.this, FingerPrintConfigActivity.class);
                    intent9.putExtra(Param.REQ_TYPE, Param.ReqType.CREATE_FROM_SET);
                    startActivityForResult(intent9, REQ_CODE_CREATE);
                }
            }
            @Override
            public void close() {
                //验证并清空手势密码
                if (SettingUtils.getInstance(App.getInstance()).isFingerPwdOn(phone)) {
                    Intent i = new Intent(ChangeFingerprintState.this, FingerPrintConfigActivity.class);
                    startActivityForResult(i, REQ_CODE_CLOSE);
                }
            }
        });

        listener();
    }

    public void listener() {
        title_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            default:break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_CREATE:
                if (resultCode == RESULT_CODE_SUCCESS) {
                    mSlide_pattern.initState(true);
                } else {
                    mSlide_pattern.initState(false);
                }
                break;
            case REQ_CODE_CLOSE:
                if (resultCode == RESULT_CODE_SUCCESS) {
                    mSlide_pattern.initState(false);
                } else {
                    mSlide_pattern.initState(true);
                }
                break;
            default:break;
        }
    }
}
package com.cstz.usercenter.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.common.App;
import com.cstz.common.App.PostType;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.cstz_android.R;
import com.cstz.tools.ToastMakeText;
import com.leaking.slideswitch.SlideSwitch;
import com.leaking.slideswitch.SlideSwitch.SlideListener;

import org.json.JSONObject;

import control.pattern.CreatePatternPwdActivity;
import control.pattern.ForgetPatternComfirm;
import control.pattern.UnlockGesturePasswordActivity;
import control.pattern.UnlockGesturePasswordActivity.Param;
import control.pattern.tools.SettingUtils;

/**
 * 手势密码修改选择
 *
 * @author rxh
 */
public class ChangePattern extends MyActivity implements OnClickListener {
    private TextView title_tv;
    private ImageView title_back;
    SlideSwitch mSlide_pattern;
    private RelativeLayout rl_change_patter;

    public static final int REQ_CODE_CREATE = 10001;
    public static final int REQ_CODE_CLOSE = 10002;
    public static final int RESULT_CODE_SUCCESS = 10003;
    public static final int REQ_CODE_UPDATE = 10004;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_pattern);
        setTransparentStatusBar(this, true);
        SharedPreferencesData data = new SharedPreferencesData(this);
        phone = data.getOtherValue("user0");
        initview();
    }

    public void initview() {
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("手势密码");
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);

        mSlide_pattern = (SlideSwitch) findViewById(R.id.slide_pattern);

        rl_change_patter = (RelativeLayout) findViewById(R.id.layout_pattern_change);

        //根据缓存中布尔值是否显示修改手势密码布局
        if (SettingUtils.getInstance(App.getInstance()).isPatternPwdOn(phone)) {
            rl_change_patter.setVisibility(View.VISIBLE);
            mSlide_pattern.initState(true);
        } else {
            rl_change_patter.setVisibility(View.GONE);
            mSlide_pattern.initState(false);
        }

        mSlide_pattern.setSlideListener(new SlideListener() {
            @Override
            public void open() {
                //开启手势密码
                if (!SettingUtils.getInstance(App.getInstance()).isPatternPwdOn(phone)) {
                    Intent intent9 = new Intent(ChangePattern.this, CreatePatternPwdActivity.class);
                    intent9.putExtra(Param.REQ_TYPE, Param.ReqType.CREATE_FROM_SET);
                    startActivityForResult(intent9, REQ_CODE_CREATE);
                }
            }

            @Override
            public void close() {
                //验证并清空手势密码
                if (SettingUtils.getInstance(App.getInstance()).isPatternPwdOn(phone)) {
                    Intent i = new Intent(ChangePattern.this, ForgetPatternComfirm.class);
                    startActivityForResult(i, REQ_CODE_CLOSE);
                }
            }
        });

        listener();
    }

    public void listener() {
        title_back.setOnClickListener(this);
        rl_change_patter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                ChangePattern.this.finish();
                break;
            case R.id.layout_pattern_change:
                Intent intent9 = new Intent(this, UnlockGesturePasswordActivity.class);
                intent9.putExtra(Param.REQ_TYPE, Param.ReqType.UNLOCK_FOR_UPDATE);
                String username = new SharedPreferencesData(this).getValue("nickName");
                if(TextUtils.isEmpty(username))
                    username = new SharedPreferencesData(this).getValue("phone");
                intent9.putExtra(Param.USERNAME, username);
                startActivityForResult(intent9, REQ_CODE_UPDATE);
                break;
            default:
                break;
        }
    }

    public void requestFail(String msg, PostType postType) {
        super.requestFail(msg, postType);
        ToastMakeText.showToast(this, msg, 1200);
    }

    public void requestSuccess(JSONObject object, PostType postType) {

        super.requestSuccess(object, postType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_CREATE:
                if (resultCode == RESULT_CODE_SUCCESS) {
                    mSlide_pattern.initState(true);
                    rl_change_patter.setVisibility(View.VISIBLE);
                } else {
                    mSlide_pattern.initState(false);
                }
                break;
            case REQ_CODE_CLOSE:
                if (resultCode == RESULT_CODE_SUCCESS) {
                    mSlide_pattern.initState(false);
                    rl_change_patter.setVisibility(View.GONE);
                } else {
                    mSlide_pattern.initState(true);
                }
                break;
            case REQ_CODE_UPDATE:
                if (resultCode == RESULT_OK) {
                    mSlide_pattern.initState(false);
                    rl_change_patter.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }
}
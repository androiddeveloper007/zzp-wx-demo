package com.cstz.front.findpwd;


import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
 * 忘记密码，确认身份证号后六位
 */
public class FindPwd_idNo extends MyActivity implements OnClickListener {
    private TextView title_tv;
    private ImageView title_back;
    private EditText _idno;
    private LinearLayout mButton;    //按扭

    private String _token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.front_findpwd_idno);
        setTransparentStatusBar(this,false);
        initview();

        Intent intent = getIntent();
        if (intent != null) {
            _token = intent.getStringExtra("token").toString();
        }
    }

    /**
     * 控件的初始化
     */
    public void initview() {
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("找回密码-身份验证");
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        _idno = (EditText) findViewById(R.id.front_findpwd_idno_text);
        mButton = (LinearLayout) findViewById(R.id.front_findpwd_idno_button);

        listener();
        _idno.setRawInputType(InputType.TYPE_CLASS_NUMBER);//指定默认输入为数字且可以切换到字母
    }

    /**
     * 按钮的事件的监听
     */
    public void listener() {
        title_back.setOnClickListener(this);
        mButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                //收回键盘
                _idno.clearFocus();
                hideSoftKeyboard();
                this.finish();
                break;
            case R.id.front_findpwd_idno_button:

                String idno = _idno.getText().toString();
                if (TextUtils.isEmpty(idno)) {
                    ToastMakeText.showToast(this, "请输入身份证后6位", 1200);
                    return;
                }
                if (idno.length() < 6) {
                    ToastMakeText.showToast(this, "输入的数字少于6位", 1200);
                    return;
                }
                if (ToastMakeText.isFastClick()) {
                    return;
                }
                setButtonStyle(false);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("step", 2);
                map.put("token", _token);
                map.put("idno", idno);
                postData(map, "/findpwd/find", PostType.SUBMIT, true);
                break;
            default:
                break;
        }
    }

    public void requestConnectionFail(PostType postType) {
        super.requestConnectionFail(postType);

        setButtonStyle(true);
    }

    public void requestError() {
        super.requestError();

        Intent intent = new Intent();
        intent.setClass(this, FindPwd.class);
        startActivity(intent);
        this.finish();
    }

    public void requestFail(String msg, PostType postType) {
        super.requestFail(msg, postType);
        if (postType == PostType.SUBMIT) {
            setButtonStyle(true);
        }
        ToastMakeText.showToast(this, msg, 1200);
    }

    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {

            if (postType == PostType.SUBMIT) {
                try {
                    JSONObject data = object.getJSONObject("data");
                    if (data != null) {
                        int step = Convert.toInt(data.getString("step").toString());
                        String token = data.getString("token").toString();
                        if (step == 3) {
                            Intent intent = new Intent();
                            intent.setClass(this, FindPwd_Update.class);
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

    private void setButtonStyle(boolean enable) {
        if (enable) {
            mButton.setBackgroundResource(R.drawable.button_normal);
            mButton.setClickable(true);

        } else {
            mButton.setBackgroundResource(R.drawable.button_disable);
            mButton.setClickable(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

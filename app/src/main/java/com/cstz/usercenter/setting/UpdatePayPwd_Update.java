package com.cstz.usercenter.setting;


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

import com.cstz.common.App.PostType;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.Validate;
import com.cstz.common.view.MyDialog;
import com.cstz.common.view.MyDialog.Result;
import com.cstz.cstz_android.R;
import com.cstz.tools.Convert;
import com.cstz.tools.ToastMakeText;
import com.ziyeyouhu.library.KeyboardTouchListener;
import com.ziyeyouhu.library.KeyboardUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改支付密码，输入修改后支付密码
 */
public class UpdatePayPwd_Update extends MyActivity implements OnClickListener {
    private TextView title_tv;
    private ImageView title_back;

    private EditText _newpwd = null;
    private EditText _quepwd = null;
    private LinearLayout mButton;

    private String _token;

    private KeyboardUtil keyboardUtil;
    private LinearLayout root_view;
    private ScrollView scrollView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_updatepaypwd_update);
        setTransparentStatusBar(this, false);
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
        title_tv.setText("修改支付密码");
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        mButton = (LinearLayout) findViewById(R.id.home_updatepaypwd_update_button);
        _newpwd = (EditText) findViewById(R.id.home_updatepaypwd_update_newpwd);
        _quepwd = (EditText) findViewById(R.id.home_updatepaypwd_update_quepwd);
        root_view = (LinearLayout) findViewById(R.id.root_view);
        scrollView1 = (ScrollView) findViewById(R.id.scrollView2);

        listener();
        initMoveKeyBoard();
    }


    private void initMoveKeyBoard() {
        keyboardUtil = new KeyboardUtil(this, root_view, scrollView1);
//        keyboardUtil.setOtherEdittext(edit_phone);
//        keyboardUtil.setKeyBoardStateChangeListener(new KeyBoardStateListener());
//        keyboardUtil.setInputOverListener(new inputOverListener());
        _newpwd.setOnTouchListener(new KeyboardTouchListener(keyboardUtil, KeyboardUtil.INPUTTYPE_ABC, -1));
        _quepwd.setOnTouchListener(new KeyboardTouchListener(keyboardUtil, KeyboardUtil.INPUTTYPE_ABC, -1));
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
                this.finish();
                break;
            case R.id.home_updatepaypwd_update_button:
                String newpwd = Convert.trim(_newpwd.getText().toString());
                String quepwd = Convert.trim(_quepwd.getText().toString());
                if (ToastMakeText.isFastClick()) {
                    return;
                }

                if (TextUtils.isEmpty(newpwd)) {
                    ToastMakeText.showToast(this, "新密码不能为空", 1200);
                    return;
                }

                if (!Validate.validatePwd(newpwd)) {
                    ToastMakeText.showToast(this, "新密码格式错误", 1200);
                    return;
                }

                if (TextUtils.isEmpty(quepwd)) {
                    ToastMakeText.showToast(this, "确认密码不能为空", 1200);
                    return;
                }

                if (!newpwd.equals(quepwd)) {
                    ToastMakeText.showToast(this, "两次密码不一致", 1200);
                    return;
                }

                setButtonStyle(false);

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("step", 3);
                map.put("newpwd", newpwd);
                map.put("token", _token);
                postData(map, "/user/updatePayPwd", PostType.SUBMIT, true);
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
        if (postType == PostType.SUBMIT) {
            setButtonStyle(true);
        }
        ToastMakeText.showToast(this, msg, 1200);
    }

    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            if (postType == PostType.SUBMIT) {
                stopSendButton();
                setButtonStyle(true);

                try {
                    MyDialog dialog = new MyDialog(this, object.getString("msg"), Result.SUCCESS);
                    dialog.setMyCallback(new MyCallback() {

                        @Override
                        public void callback() {
                            // TODO Auto-generated method stub
                            UpdatePayPwd_Update.this.finish();

                        }

                        @Override
                        public void doing() {
                            // TODO Auto-generated method stub

                        }
                    });
                    dialog.showDialog();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
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
}

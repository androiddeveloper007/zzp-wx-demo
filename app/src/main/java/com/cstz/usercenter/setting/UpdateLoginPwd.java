package com.cstz.usercenter.setting;

import android.os.Bundle;
import android.os.Handler;
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
import com.cstz.common.SharedPreferencesData;
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
 * 修改密码
 */
public class UpdateLoginPwd extends MyActivity implements OnClickListener {
    private ImageView title_back;
    private TextView title_tv;
    private LinearLayout mButton;  //修改密码确认

    private EditText _pwd = null;
    private EditText _newpwd = null;
    private EditText _quepwd = null;
    private KeyboardUtil keyboardUtil;
    private LinearLayout root_view;
    private ScrollView scrollView1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_updateloginpwd);
        setTransparentStatusBar(this, false);
        initview();
    }

    /**
     * 初始化控件
     */
    public void initview() {
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("修改密码");
        mButton = (LinearLayout) findViewById(R.id.home_updateloginpwd_button);

        _pwd = (EditText) findViewById(R.id.home_updateloginpwd_pwd);
        _newpwd = (EditText) findViewById(R.id.home_updateloginpwd_newpwd);
        _quepwd = (EditText) findViewById(R.id.home_updateloginpwd_quepwd);

        root_view = (LinearLayout) findViewById(R.id.root_view);
        scrollView1 = (ScrollView) findViewById(R.id.scrollView2);

        listener();
        initMoveKeyBoard();
    }


    private void initMoveKeyBoard() {
        keyboardUtil = new KeyboardUtil(this, root_view, scrollView1);
        keyboardUtil.setKeyBoardStateChangeListener(new KeyBoardStateListener());
        keyboardUtil.setInputOverListener(new inputOverListener());
        _pwd.setOnTouchListener(new KeyboardTouchListener(keyboardUtil, KeyboardUtil.INPUTTYPE_ABC, -1));
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
     * 按钮的监听事件
     */
    public void listener() {
        title_back.setOnClickListener(this);
        mButton.setOnClickListener(this);
    }

    String newpwd;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.home_updateloginpwd_button:
                String pwd = Convert.trim(_pwd.getText().toString());
                newpwd = Convert.trim(_newpwd.getText().toString());
                String quepwd = Convert.trim(_quepwd.getText().toString());
                if (ToastMakeText.isFastClick()) {
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    ToastMakeText.showToast(this, "原始密码不能为空", 1200);
                    return;
                }
                if (!Validate.validatePwd(newpwd)) {
                    ToastMakeText.showToast(this, "新密码格式错误", 1200);
                    return;
                }
                if (!newpwd.equals(quepwd)) {
                    ToastMakeText.showToast(this, "两次密码不一致", 1200);
                    return;
                }
                setButtonStyle(false);
                SharedPreferencesData data = new SharedPreferencesData(UpdateLoginPwd.this);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("token", data.getValue("token"));
                map.put("pwd", pwd);
                map.put("newpwd", newpwd);
                postData(map, "/user/updateLoginPwdAction", PostType.SUBMIT, true);
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
        if (msg != null) {
            setButtonStyle(true);
            ToastMakeText.showToast(this, msg, 1200);
        }
    }

    public void requestSuccess(JSONObject object, PostType postType) {
        if (object != null) {
            if (postType == PostType.SUBMIT) {
                try {
                    MyDialog dialog = new MyDialog(UpdateLoginPwd.this, object.getString("msg"), Result.SUCCESS);
                    dialog.setMyCallback(new MyCallback() {

                        @Override
                        public void callback() {
                            //将新密码存sp中，手势密码使用
                            new SharedPreferencesData(UpdateLoginPwd.this).setOtherValue("pwd0", newpwd);
                            UpdateLoginPwd.this.finish();
                        }

                        @Override
                        public void doing() {

                        }
                    });
                    dialog.showDialog();
                } catch (JSONException e) {
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


    private static Handler handler = new Handler();
    boolean keyboardHasUp = false;

    class KeyBoardStateListener implements KeyboardUtil.KeyBoardStateChangeListener {
        @Override
        public void KeyBoardStateChange(int state, final EditText editText) {
            if (state == 1) {
                if (!keyboardHasUp) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollView1.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                            switch (editText.getId()) {
                                case R.id.home_updateloginpwd_pwd:
                                    _pwd.requestFocus();
                                    break;
                                case R.id.home_updateloginpwd_newpwd:
                                    _newpwd.requestFocus();
                                    break;
                                case R.id.home_updateloginpwd_quepwd:
                                    _quepwd.requestFocus();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }, 50);
                }
                keyboardHasUp = true;
            } else if (state == 2) {
                keyboardHasUp = false;
            }
        }
    }

    class inputOverListener implements KeyboardUtil.InputFinishListener {
        @Override
        public void inputHasOver(int onclickType, EditText editText) {
        }
    }


}

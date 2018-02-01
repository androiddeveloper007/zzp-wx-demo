package com.cstz.front.findpwd;

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
import com.cstz.front.Login;
import com.cstz.tools.Convert;
import com.cstz.tools.ToastMakeText;
import com.ziyeyouhu.library.KeyboardTouchListener;
import com.ziyeyouhu.library.KeyboardUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 忘记密码界面
 */
public class FindPwd_Update extends MyActivity implements OnClickListener {
    private TextView title_tv;
    private ImageView title_back;
    private LinearLayout mButton;     // 按扭
    private EditText _newpwd;
    private EditText _quepwd;
    private String _token = "";
    private KeyboardUtil keyboardUtil;
    private LinearLayout root_view;
    private ScrollView scrollView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.front_findpwd_update);
        setTransparentStatusBar(this, false);

        Intent intent = getIntent();

        if (intent != null && intent.getExtras().getString("token") != null) {
            _token = intent.getStringExtra("token");
        }

        initview();
    }

    /**
     * 初始化控件
     */
    public void initview() {
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("找回密码-修改密码");
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        _newpwd = (EditText) findViewById(R.id.text_newpwd);
        _quepwd = (EditText) findViewById(R.id.text_quepwd);
        mButton = (LinearLayout) findViewById(R.id.layout_findpwd_button);
        root_view = (LinearLayout) findViewById(R.id.root_view);
        scrollView1 = (ScrollView) findViewById(R.id.scrollView2);
        listener();

        initMoveKeyBoard();
    }

    private void initMoveKeyBoard() {
        keyboardUtil = new KeyboardUtil(this, root_view, scrollView1);
//          keyboardUtil.setOtherEdittext(_quepwd);
        keyboardUtil.setKeyBoardStateChangeListener(new KeyBoardStateListener());
        keyboardUtil.setInputOverListener(new inputOverListener());
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
     * 控件的监听事件
     */
    public void listener() {
        title_back.setOnClickListener(this);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String newpwd = Convert.trim(_newpwd.getText().toString());
        String quepwd = Convert.trim(_quepwd.getText().toString());
        // TODO Auto-generated method stub
        if (v.getId() == R.id.title_back) {
            this.finish();
        } else if (v.getId() == R.id.layout_findpwd_button) {
            if (ToastMakeText.isFastClick()) {
                return;
            }
            if (TextUtils.isEmpty(newpwd)) {
                ToastMakeText.showToast(this, "请输入新密码", 1200);
                return;
            } else if (!Validate.validatePwd(newpwd)) {
                ToastMakeText.showToast(this, "新密码格式错误", 1200);
                return;
            }
            if (!newpwd.equals(quepwd)) {
                ToastMakeText.showToast(this, "两次输入的密码不一致", 1200);
                return;
            }

            setButtonStyle(false);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("token", _token);
            map.put("newpwd", newpwd);
            postData(map, "/findpwd/update", PostType.SUBMIT, true);

        }

    }

    public void requestConnectionFail(PostType postType) {
        super.requestConnectionFail(postType);

        setButtonStyle(true);
    }

    public void requestFail(String msg, PostType postType) {
        super.requestFail(msg, postType);

        setButtonStyle(true);

        ToastMakeText.showToast(this, msg, 1200);
    }

    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            if (postType == PostType.SUBMIT) {
                try {

                    MyDialog dialog = new MyDialog(this, object.getString("msg"), Result.SUCCESS);
                    dialog.setMyCallback(new MyCallback() {

                        @Override
                        public void callback() {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent();
                            intent.setClass(FindPwd_Update.this, Login.class);
                            FindPwd_Update.this.startActivity(intent);
                            FindPwd_Update.this.finish();
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

    class KeyBoardStateListener implements KeyboardUtil.KeyBoardStateChangeListener {

        @Override
        public void KeyBoardStateChange(int state, EditText editText) {
//            System.out.println("state" + state);
//            System.out.println("editText" + editText.getText().toString());
//        	Toast.makeText(Login.this, "state" + state, 2000).show();
        }
    }

    class inputOverListener implements KeyboardUtil.InputFinishListener {

        @Override
        public void inputHasOver(int onclickType, EditText editText) {
//            System.out.println("onclickType" + onclickType);
//            System.out.println("editText" + editText.getText().toString());
//        	Toast.makeText(Login.this, "onclickType" + onclickType, 2000).show();
        }
    }
}

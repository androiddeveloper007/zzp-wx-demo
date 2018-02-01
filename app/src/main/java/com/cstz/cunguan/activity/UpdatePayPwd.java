package com.cstz.cunguan.activity;


import android.content.Intent;
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

import com.cstz.MyWidget.DepositDialog;
import com.cstz.common.App;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.Validate;
import com.cstz.common.view.MyDialog;
import com.cstz.cstz_android.R;
import com.cstz.front.Login;
import com.cstz.tools.Convert;
import com.cstz.tools.ToastMakeText;
import com.ziyeyouhu.library.KeyboardTouchListener;
import com.ziyeyouhu.library.KeyboardUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 修改还款密码
 */
public class UpdatePayPwd extends MyActivity implements OnClickListener {
    private ImageView title_back;
    private TextView title_tv,tv_invest_verify_tip;
    private LinearLayout mButton;  //修改密码确认
    private com.cstz.MyWidget.MClearEditText _pwd = null;
    private com.cstz.MyWidget.MClearEditText _newpwd = null;
    private com.cstz.MyWidget.MClearEditText _quepwd = null;
    private KeyboardUtil keyboardUtil;
    private LinearLayout root_view;
    private ScrollView scrollView1;
    String newpwd;
    private final Handler handler = new Handler();
    boolean keyboardHasUp = false;
    private SharedPreferencesData sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_update_pay_pwd);
        setTransparentStatusBar(this,true);
        sp = new SharedPreferencesData(this);
        initView();
        boolean hasRepaymentPwdEdit = sp.getOtherBoolean("hasRepaymentPwdEdit");
        setTipVisible(hasRepaymentPwdEdit);
    }

    public void initView() {
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("修改还款密码");
        mButton = (LinearLayout) findViewById(R.id.home_updateloginpwd_button);

        _pwd = (com.cstz.MyWidget.MClearEditText) findViewById(R.id.home_updateloginpwd_pwd);
        _newpwd = (com.cstz.MyWidget.MClearEditText) findViewById(R.id.home_updateloginpwd_newpwd);
        _quepwd = (com.cstz.MyWidget.MClearEditText) findViewById(R.id.home_updateloginpwd_quepwd);

        root_view = (LinearLayout) findViewById(R.id.root_view);
        scrollView1 = (ScrollView) findViewById(R.id.scrollView2);
        tv_invest_verify_tip = (TextView) findViewById(R.id.tv_invest_verify_tip);

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
            case R.id.home_updateloginpwd_button:
                String pwd = Convert.trim(_pwd.getText().toString());
                newpwd = Convert.trim(_newpwd.getText().toString());
                String quepwd = Convert.trim(_quepwd.getText().toString());
                if (ToastMakeText.isFastClick()) {
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    ToastMakeText.showToast(this, "原始还款密码不能为空", 1200);
                    return;
                }
                if (!Validate.validatePwd(newpwd)) {
                    ToastMakeText.showToast(this, "新还款密码格式错误", 1200);
                    return;
                }
                if (!newpwd.equals(quepwd)) {
                    ToastMakeText.showToast(this, "两次还款密码不一致", 1200);
                    return;
                }
                setButtonStyle(false);
                requestUpdateRepaymentPwd(pwd,newpwd);
                break;
            default:break;
        }
    }

    private void requestUpdateRepaymentPwd(String pwd,String newpwd) {
        String path = Config.getHttpConfig() + "/user/updateRepaymentPwd";
        final RequestParams params = new RequestParams(path);
        params.addParameter("oldpwd", pwd);
        params.addParameter("newpwd", newpwd);
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        final DepositDialog depositDialog = new DepositDialog(this, "加载中...");
        depositDialog.setDuration(1000);
        depositDialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() {}
            @Override
            public void doing() {
                x.http().post(params, new Callback.CacheCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try {
//                            Log.e("WXCF","还款返回json："+arg0);
                            JSONObject data = new JSONObject(arg0);
                            if(data.has("result")){
                                String result = data.getString("result");
                                if (TextUtils.equals("1", result)) {
                                    showToast(data.getString("msg"),3000);
                                    sp.setOtherBoolean("hasRepaymentPwdEdit",true);
                                    finish();
                                } else if(TextUtils.equals("-1", result)){
                                    showToast(data.getString("msg"),3000);
                                } else if(TextUtils.equals("-4", result)){
                                    ToastMakeText.showToast(UpdatePayPwd.this,"会话过期，请重新登录",3000);
                                    sp.removeAll();
                                    Intent intent = new Intent(UpdatePayPwd.this, Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastMakeText.showToast(UpdatePayPwd.this, "服务器连接失败", 1000);
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {}
                    @Override
                    public void onFinished() {
                        if(depositDialog!=null)
                            depositDialog.hideDialog();
                        setButtonStyle(true);
                    }
                    @Override
                    public boolean onCache(String result) {
                        return false;
                    }
                });
            }
        });
        depositDialog.showDialog();
    }

    public void requestConnectionFail(App.PostType postType) {
        super.requestConnectionFail(postType);
        setButtonStyle(true);
    }

    public void requestFail(String msg, App.PostType postType) {
        if (msg != null) {
            setButtonStyle(true);
            ToastMakeText.showToast(this, msg, 1200);
        }
    }

    public void requestSuccess(JSONObject object, App.PostType postType) {
        if (object != null) {
            if (postType == App.PostType.SUBMIT) {
                MyDialog dialog = new MyDialog(this, "还款密码修改成功", MyDialog.Result.SUCCESS);
                dialog.setMyCallback(new MyCallback() {
                    @Override
                    public void callback() {
                        //将新密码存sp中，手势密码使用
                        sp.setBoolean("hasRepaymentPwdEdit", true);
                        finish();
                    }
                    @Override
                    public void doing() {
                    }
                });
                dialog.showDialog();
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
        public void KeyBoardStateChange(int state, final EditText editText) {
            if (state == 1) {
                if (!keyboardHasUp) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switch (editText.getId()) {
                                case R.id.home_updateloginpwd_pwd:
                                    _pwd.requestFocus();
                                    keyboardHasUp = false;
                                    break;
                                case R.id.home_updateloginpwd_newpwd:
                                    scrollView1.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                                    _newpwd.requestFocus();
                                    keyboardHasUp = true;
                                    break;
                                case R.id.home_updateloginpwd_quepwd:
                                    scrollView1.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                                    _quepwd.requestFocus();
                                    keyboardHasUp = true;
                                    break;
                                default:
                                    keyboardHasUp = true;break;
                            }
                        }
                    }, 50);
                }
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

    public void setTipVisible(boolean b){
        tv_invest_verify_tip.setVisibility(!b?View.VISIBLE:View.GONE);
    }
}
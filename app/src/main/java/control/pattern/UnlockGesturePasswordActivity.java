package control.pattern;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.common.App;
import com.cstz.common.App.PostType;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.SysApplication;
import com.cstz.common.view.PatternWarnDialog;
import com.cstz.cstz_android.R;
import com.cstz.front.Login;
import com.cstz.tab.MainActivity;
import com.cstz.tools.ToastMakeText;
import com.cstz.usercenter.TiXian;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import control.pattern.tools.SettingUtils;
import control.pattern.utils.LockPatternUtils;
import control.pattern.utils.LockPatternView;

/**
 * 解锁界面
 *
 * @author zzp
 */
public class UnlockGesturePasswordActivity extends MyActivity implements OnClickListener {

//    static final String TAG = UnlockGesturePasswordActivity.class.getSimpleName();

//    public static final int REQ_CODE_UNLOCK = 0x401;

    private Handler handler = new Handler();

    public static class Param {
        public static final String REQ_TYPE = "reqType";

        public static class ReqType {
            public static final int UNLOCK_FOR_NORMAL = 0;
            public static final int UNLOCK_FOR_UPDATE = 1;
            public static final int UNLOCK_FOR_FROMHOME = 2;
            public static final int UNLOCK_FOR_OFF = 3;
            public static final int UNLOCK_FOR_USERCENTER = 4;

            public static final int CREATE_FROM_LOGIN = 5;
            public static final int CREATE_FROM_SET = 6;
            public static final int CREATE_FROM_REGISTER = 7;

        }

        public static final String USERNAME = "username";
    }

    private LockPatternView mLockPatternView;

    private int mFailedPatternAttemptsSinceLastTimeout = 0;//错误次数
    private CountDownTimer mCountdownTimer = null;

    private TextView tvUsername;

    private TextView mHeadTextView;
    private Animation mShakeAnim;

    private TextView tvForgetPwd;
    private TextView tvOtherAccount;

    public LinearLayout ll_forget_pwd, ll_gesture_other, gesturepwd_unlock_face;
    private int reqType;


    private TextView title_tv;
    private ImageView title_back;
    private View title_bar;

    private TextView tv_unlockpattern_year_month;
    private TextView tv_unlockpattern_week;
    private TextView tv_unlockpattern_day;

    private String user0;
    private String userType="0";
    private String regAsDeposit="0";
    private String depositCheck="0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gesturepassword_unlock);
        setTransparentStatusBar(this, false);

        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("解锁");
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        title_back.setOnClickListener(this);

        title_bar = (View) findViewById(R.id.title_bar);

        tvUsername = (TextView) findViewById(R.id.tv_username);
        if (getIntent() != null) {
            String userName = getIntent().getStringExtra(Param.USERNAME);//SettingUtils.getInstance(this).getUsername();
//            tvUsername.setText(Utils.formatPhoneNumber(userName));
            if (userName != null && userName.length() < 4) {
                findViewById(R.id.line_gesture_unlock_0).setVisibility(View.GONE);
                findViewById(R.id.line_gesture_unlock_1).setVisibility(View.VISIBLE);
            }
            tvUsername.setText(userName);
        }

        mLockPatternView = (LockPatternView) this.findViewById(R.id.gesturepwd_unlock_lockview);
        mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
        mLockPatternView.setTactileFeedbackEnabled(false);

        mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);

        tvForgetPwd = (TextView) findViewById(R.id.gesturepwd_unlock_forget);
        ll_forget_pwd = (LinearLayout) findViewById(R.id.ll_forget_pwd);
        ll_gesture_other = (LinearLayout) findViewById(R.id.ll_gesture_other);
        gesturepwd_unlock_face = (LinearLayout) findViewById(R.id.gesturepwd_unlock_face);

        tvForgetPwd.setOnClickListener(this);

        tvOtherAccount = (TextView) findViewById(R.id.tv_other_account);
        tvOtherAccount.setOnClickListener(this);

        mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);

        if (getIntent() != null)
            reqType = getIntent().getIntExtra(Param.REQ_TYPE, Param.ReqType.UNLOCK_FOR_NORMAL);

        if (reqType == Param.ReqType.UNLOCK_FOR_UPDATE) {
            tvOtherAccount.setVisibility(View.GONE);
            ll_gesture_other.setVisibility(View.GONE);
            tvForgetPwd.setVisibility(View.VISIBLE);
            ll_forget_pwd.setGravity(Gravity.CENTER);
            tvForgetPwd.setText("忘记手势密码");

            title_tv.setText("验证原手势密码");

            gesturepwd_unlock_face.setVisibility(View.GONE);
            TextView tv_username_1 = (TextView) findViewById(R.id.tv_username_1);
            tv_username_1.setVisibility(View.VISIBLE);
            if (getIntent() != null) {
                String userName = getIntent().getStringExtra(Param.USERNAME);
                tv_username_1.setText("当前账号：" + userName);
                mHeadTextView.setText("请绘制手势密码");
            }
        } else if (reqType == Param.ReqType.UNLOCK_FOR_NORMAL) {
            tvForgetPwd.setVisibility(View.VISIBLE);
            tvOtherAccount.setVisibility(View.VISIBLE);
//            mHeadTextView.setText("请绘制手势密码");
            title_bar.setVisibility(View.GONE);

        } else if (reqType == Param.ReqType.UNLOCK_FOR_FROMHOME) {//by zhuzhipeng 增加home键回屏幕时锁屏的界
            tvForgetPwd.setVisibility(View.VISIBLE);
            tvOtherAccount.setVisibility(View.VISIBLE);
            title_tv.setText("手势密码");
        } else if (reqType == Param.ReqType.UNLOCK_FOR_OFF) {
            tvOtherAccount.setVisibility(View.GONE);
            ll_gesture_other.setVisibility(View.GONE);
            ll_forget_pwd.setGravity(Gravity.CENTER);
            title_tv.setText("验证手势密码");
            tvForgetPwd.setText("忘记手势密码");
        } else if (reqType == Param.ReqType.UNLOCK_FOR_USERCENTER) {
            tvOtherAccount.setVisibility(View.GONE);
            ll_gesture_other.setVisibility(View.GONE);
            ll_forget_pwd.setGravity(Gravity.CENTER);
            title_tv.setText("安全验证");
            tvForgetPwd.setText("忘记手势密码");
        }

        /**
         * 初始化日期数据
         * */
        tv_unlockpattern_year_month = (TextView) findViewById(R.id.tv_unlockpattern_year_month);
        tv_unlockpattern_week = (TextView) findViewById(R.id.tv_unlockpattern_week);
        tv_unlockpattern_day = (TextView) findViewById(R.id.tv_unlockpattern_day);

        Date date = new Date();
        int number = date.getDay();
        if (number == 0) {
            tv_unlockpattern_week.setText("星期日");
        } else if (number == 1) {
            tv_unlockpattern_week.setText("星期一");
        } else if (number == 2) {
            tv_unlockpattern_week.setText("星期二");
        } else if (number == 3) {
            tv_unlockpattern_week.setText("星期三");
        } else if (number == 4) {
            tv_unlockpattern_week.setText("星期四");
        } else if (number == 5) {
            tv_unlockpattern_week.setText("星期五");
        } else if (number == 6) {
            tv_unlockpattern_week.setText("星期六");
        }
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);//年

        int month = calendar.get(Calendar.MONTH) + 1;//月

        int day = calendar.get(Calendar.DAY_OF_MONTH);//日

        tv_unlockpattern_year_month.setText(year + "." + month);

        tv_unlockpattern_day.setText(day + "");

        SharedPreferencesData data = new SharedPreferencesData(this);
        user0 = data.getOtherValue("user0");
        //将mFailedPatternAttemptsSinceLastTimeout的值设置为sp中保存的值
        mFailedPatternAttemptsSinceLastTimeout = new SharedPreferencesData(this).getOtherIntValueByUser(user0, "mFailedPatternAttemptsSinceLastTimeout");

        SysApplication.getInstance().addActivity(this);
    }


    private void login() {
        SharedPreferencesData sp = new SharedPreferencesData(this);
        String phone = sp.getOtherValue("user0");
        String pwd = sp.getOtherValue("pwd0");
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("username", phone);
            map.put("pwd", pwd);
            postData(map, "/loginAction", PostType.SUBMIT, false);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountdownTimer != null) {
            mCountdownTimer.cancel();
        }
    }

    // 清空绘制
    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    // 解锁控件监听
    protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

        public void onPatternStart() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
            patternInProgress();
        }

        public void onPatternCleared() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
        }

        public void onPatternDetected(List<LockPatternView.Cell> pattern) {
            if (pattern == null) {
                return;
            }

            // 解锁成功
            String phone = new SharedPreferencesData(UnlockGesturePasswordActivity.this).getOtherValueSecurity("user0");
            if (new LockPatternUtils(UnlockGesturePasswordActivity.this, phone).checkPattern(pattern)) {//App.getInstance().getLockPatternUtils()

                // 显示成功
                mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                onUnlockSuccess();
            } else {

                mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);

                if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
                    mFailedPatternAttemptsSinceLastTimeout++;

                    //error数记录至sp中
                    SharedPreferencesData sp = new SharedPreferencesData(UnlockGesturePasswordActivity.this);
                    sp.setOtherIntValueByUser(user0, "mFailedPatternAttemptsSinceLastTimeout", mFailedPatternAttemptsSinceLastTimeout);

                    int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT - mFailedPatternAttemptsSinceLastTimeout;
                    if (retry >= 0) {
                        mHeadTextView.setText("密码错误，您还有" + retry + "次机会");
                        mHeadTextView.setTextColor(getResources().getColor(R.color.red));
                        mHeadTextView.startAnimation(mShakeAnim);
                    }
                } else {
                    mHeadTextView.setText("请至少连接4个点");
                    mHeadTextView.setTextColor(getResources().getColor(R.color.red));
                    mHeadTextView.startAnimation(mShakeAnim);
                }

                if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
//                    handler.removeCallbacks(attemptLockout);
//                    handler.post(attemptLockout);

                    //清空登录密码并重置手势密码状态
                    String user0 = new SharedPreferencesData(UnlockGesturePasswordActivity.this).getOtherValue("user0");
                    SettingUtils.getInstance(App.getInstance()).putIsPatternPwdOn(user0, false);
                    String phone1 = new SharedPreferencesData(UnlockGesturePasswordActivity.this).getOtherValueSecurity("user0");
                    new LockPatternUtils(UnlockGesturePasswordActivity.this, phone1).clearLock();
                    new SharedPreferencesData(UnlockGesturePasswordActivity.this).
                            setOtherIntValueByUser(user0, "mFailedPatternAttemptsSinceLastTimeout", 0);//输入手势密码错误次数重置为0
                    new SharedPreferencesData(UnlockGesturePasswordActivity.this).setBooleanByUser(user0, "isNotFirstIn_pattern", false);

                    final PatternWarnDialog messageDialog = new PatternWarnDialog(UnlockGesturePasswordActivity.this, false);
                    messageDialog.setTitle("温馨提示");
                    messageDialog.tvMsg.setText("您已连续输错5次，请重新登录！");
                    messageDialog.show();
                    messageDialog.setOnBtn1ClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(messageDialog!=null && messageDialog.isShowing())
                                messageDialog.dismiss();
                            new SharedPreferencesData(UnlockGesturePasswordActivity.this).setBoolean("hasLogin", false);
                            postData(null, "/logout", null, PostType.SUBMIT);
                            new SharedPreferencesData(UnlockGesturePasswordActivity.this).removeAll();
                            Intent intent = new Intent(UnlockGesturePasswordActivity.this, Login.class);
                            intent.putExtra("pattern_warn", "");
                            startActivity(intent);
                            UnlockGesturePasswordActivity.this.finish();
                        }
                    });
                } else {
                    mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
                }
            }
        }

        public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

        }

        private void patternInProgress() {
        }
    };

    /**
     * 解锁成功
     */
    private void onUnlockSuccess() {
        if (reqType == Param.ReqType.UNLOCK_FOR_NORMAL) {
            new SharedPreferencesData(UnlockGesturePasswordActivity.this).
                    setOtherIntValueByUser(user0, "mFailedPatternAttemptsSinceLastTimeout", 0);//重置为0
            login();//模拟登陆的操作
        } else if (reqType == Param.ReqType.UNLOCK_FOR_UPDATE) {
            //更改手势密码时验证原密码成功
            new SharedPreferencesData(UnlockGesturePasswordActivity.this).
                    setOtherIntValueByUser(user0, "mFailedPatternAttemptsSinceLastTimeout", 0);//重置为0
            Intent intent = new Intent(this, CreatePatternPwdActivity.class);
            intent.putExtra(Param.REQ_TYPE, Param.ReqType.CREATE_FROM_SET);
            startActivity(intent);
            finish();
        } else if (reqType == Param.ReqType.UNLOCK_FOR_FROMHOME) {
            finish();
        } else if (reqType == Param.ReqType.UNLOCK_FOR_USERCENTER) {
            Intent intent = new Intent(this, TiXian.class);
            if (getIntent() != null) {
                intent.putExtra("usableSum", getIntent().getStringExtra("usableSum"));
                intent.putExtra("bankName", getIntent().getStringExtra("bankName"));
                intent.putExtra("cardNo", getIntent().getStringExtra("cardNo"));
                App.userType=getIntent().getStringExtra("userType");
                App.regAsDeposit=getIntent().getStringExtra("regAsDeposit");
                App.depositCheck=getIntent().getStringExtra("depositCheck");
            }
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && reqType == Param.ReqType.UNLOCK_FOR_NORMAL) {
            if (!isExit) {
                isExit = true;
                ToastMakeText.showToast(UnlockGesturePasswordActivity.this, "再按一次退出程序", 1200);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 1500);
                return false;
            } else {
                SysApplication.getInstance().exit();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private static Boolean isExit = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gesturepwd_unlock_forget:
                doForgetPatternPwd();
                break;
            case R.id.tv_other_account:
                accountLogin();
                break;
            case R.id.title_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        try {
            if (object != null) {
                JSONObject data = object.getJSONObject("data");
                if (data != null) {
                    new SharedPreferencesData(UnlockGesturePasswordActivity.this).setValue("token", data.getString("token"));
                    if(data.has("userType"))
                        userType = data.getString("userType");
                    if(data.has("regAsDeposit"))
                        regAsDeposit = data.getString("regAsDeposit");
                    if(data.has("depositCheck"))
                        depositCheck = data.getString("depositCheck");
                    Intent intent = new Intent();
                    intent.putExtra("home", "");
                    intent.putExtra("userType",userType);
                    intent.putExtra("regAsDeposit",regAsDeposit);
                    intent.putExtra("depositCheck",depositCheck);
                    App.userType=userType;
                    App.regAsDeposit=regAsDeposit;
                    App.depositCheck=depositCheck;
                    intent.setClass(UnlockGesturePasswordActivity.this, MainActivity.class);

                    UnlockGesturePasswordActivity.this.startActivity(intent);

                    new SharedPreferencesData(this).setBoolean("hasLogin", true);

                    UnlockGesturePasswordActivity.this.finish();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestConnectionFail(PostType postType) {
        super.requestConnectionFail(postType);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ForgetPatternComfirm.RESULT_CODE_SUCCESS:
                if (resultCode == ForgetPatternComfirm.RESULT_CODE_SUCCESS) {
                    UnlockGesturePasswordActivity.this.setResult(RESULT_OK);
                    UnlockGesturePasswordActivity.this.finish();
                }
                break;

            case ForgetPatternComfirm.RESULT_CODE_SUCCESS_NORMAL:

                if (reqType == Param.ReqType.UNLOCK_FOR_NORMAL && resultCode == ForgetPatternComfirm.RESULT_CODE_SUCCESS) {
                    UnlockGesturePasswordActivity.this.finish();
                } else {

                }

                break;
            default:
                break;
        }
    }

    // 用户点击了忘记密码或者使用其他账户登
    private void doForgetPatternPwd() {
        Intent i = new Intent(this, ForgetPatternComfirm.class);
        if (reqType == Param.ReqType.UNLOCK_FOR_NORMAL) {

            i.putExtra("fromNormal", "");
            startActivityForResult(i, ForgetPatternComfirm.RESULT_CODE_SUCCESS_NORMAL);

        } else if (reqType == Param.ReqType.UNLOCK_FOR_UPDATE) {

            startActivityForResult(i, ForgetPatternComfirm.RESULT_CODE_SUCCESS);

        } else if (reqType == Param.ReqType.UNLOCK_FOR_FROMHOME) {

            startActivityForResult(i, ForgetPatternComfirm.RESULT_CODE_SUCCESS);

        } else if (reqType == Param.ReqType.UNLOCK_FOR_USERCENTER) {

            startActivityForResult(i, ForgetPatternComfirm.RESULT_CODE_SUCCESS);

        }

    }

    private void accountLogin() {
        Intent i = new Intent(this, Login.class);
        i.putExtra("phone", new SharedPreferencesData(this).getOtherValue("user0"));
        startActivity(i);
    }

}

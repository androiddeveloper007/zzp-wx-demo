package control.pattern;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.common.App;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.SysApplication;
import com.cstz.common.view.MyDialog;
import com.cstz.common.view.MyDialog.Result;
import com.cstz.cstz_android.R;
import com.cstz.tab.MainActivity;
import com.cstz.usercenter.setting.ChangePattern;

import java.util.ArrayList;
import java.util.List;

import control.pattern.UnlockGesturePasswordActivity.Param;
import control.pattern.tools.SettingUtils;
import control.pattern.utils.LockPatternUtils;
import control.pattern.utils.LockPatternView;

import static control.pattern.CreatePatternPwdActivity.Stage.NeedToConfirm;

public class CreatePatternPwdActivity extends MyActivity implements OnClickListener {

    private TextView title_tv, title_right;
    private ImageView title_back;

//    public static final int REQ_CREATE_GESTURE_PASSWORD = 0x201;

    private static final int ID_EMPTY_MESSAGE = -1;
    private static final String KEY_UI_STAGE = "uiStage";
    private static final String KEY_PATTERN_CHOICE = "chosenPattern";

    private LockPatternView mLockPatternView;

    private Button mFooterRightButton;
    private Button mFooterLeftButton;

    protected TextView mHeaderText, tv_pattern_guid;
    protected List<LockPatternView.Cell> mChosenPattern = null;

//    private Toast mToast;

    // 界面跳转状
    private Stage mUiStage = Stage.Introduction;

    // 图形密码预览
    private View mPreviewViews[][] = new View[3][3];

    public static Handler handler = new Handler();

    private int reqType;

    private boolean isFromNormal = false;

    private boolean isFirstInputOk = false;

    private String user0;
    private SharedPreferencesData sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gesturepassword_create);
        setTransparentStatusBar(this, false);
        sp = new SharedPreferencesData(this);
        initview(savedInstanceState);
        Intent i = getIntent();
        if (i != null && i.getStringExtra("fromNormal") != null) {
            isFromNormal = true;
//			mFooterLeftButton.setEnabled(false);
        }
        if (i != null)
            reqType = i.getIntExtra(Param.REQ_TYPE, Param.ReqType.CREATE_FROM_LOGIN);
        if (reqType == Param.ReqType.CREATE_FROM_LOGIN) {
            title_back.setVisibility(View.GONE);
        } else if (reqType == Param.ReqType.CREATE_FROM_SET) {
            title_right.setVisibility(View.GONE);
            title_back.setVisibility(View.VISIBLE);
        }
        SysApplication.getInstance().addActivity(this);
        user0 = sp.getOtherValue("user0");
    }

    public void initview(Bundle savedInstanceState) {
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("设置手势密码");
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        title_right = (TextView) findViewById(R.id.navigation_right);
        title_right.setText("跳过");
        title_right.setOnClickListener(this);
        title_right.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dimen_18));
        title_right.setVisibility(View.VISIBLE);
        tv_pattern_guid = (TextView) findViewById(R.id.tv_pattern_guid);
        tv_pattern_guid.setOnClickListener(this);
        listener();

        mHeaderText = (TextView) findViewById(R.id.gesturepwd_create_text);

        mLockPatternView = (LockPatternView) this.findViewById(R.id.gesturepwd_create_lockview);
        mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
        mLockPatternView.setTactileFeedbackEnabled(false);

        mFooterRightButton = (Button) this.findViewById(R.id.right_btn);
        mFooterLeftButton = (Button) this.findViewById(R.id.reset_btn);

        mFooterRightButton.setOnClickListener(this);
        mFooterLeftButton.setOnClickListener(this);

        initPreviewViews();

        if (savedInstanceState == null) {

            updateStage(Stage.Introduction);//创建一个图案

        } else {
            final String patternString = savedInstanceState.getString(KEY_PATTERN_CHOICE);
            if (patternString != null) {
                mChosenPattern = LockPatternUtils.stringToPattern(patternString);
            }
            updateStage(Stage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
        }
    }


    private void initPreviewViews() {
        mPreviewViews = new View[3][3];

        mPreviewViews[0][0] = findViewById(R.id.gesturepwd_setting_preview_0);
        mPreviewViews[0][1] = findViewById(R.id.gesturepwd_setting_preview_1);
        mPreviewViews[0][2] = findViewById(R.id.gesturepwd_setting_preview_2);
        mPreviewViews[1][0] = findViewById(R.id.gesturepwd_setting_preview_3);
        mPreviewViews[1][1] = findViewById(R.id.gesturepwd_setting_preview_4);
        mPreviewViews[1][2] = findViewById(R.id.gesturepwd_setting_preview_5);
        mPreviewViews[2][0] = findViewById(R.id.gesturepwd_setting_preview_6);
        mPreviewViews[2][1] = findViewById(R.id.gesturepwd_setting_preview_7);
        mPreviewViews[2][2] = findViewById(R.id.gesturepwd_setting_preview_8);
    }

    private void updatePreviewViews() {
        if (mChosenPattern == null) {
            return;
        }

        for (LockPatternView.Cell cell : mChosenPattern) {
            mPreviewViews[cell.getRow()][cell.getColumn()].setBackgroundResource(R.drawable.pattern_preview_selected);
        }
    }

    private void resetPreviewViews() {
        if (mChosenPattern == null) {
            return;
        }

        for (LockPatternView.Cell cell : mChosenPattern) {
            mPreviewViews[cell.getRow()][cell.getColumn()].setBackgroundResource(R.drawable.pattern_preview_normal);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_UI_STAGE, mUiStage.ordinal());

        if (mChosenPattern != null) {
            outState.putString(KEY_PATTERN_CHOICE, LockPatternUtils.patternToString(mChosenPattern));
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = getIntent();
            if (i != null && i.getStringExtra("fromNormal") != null)
                return true;
            else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

        public void onPatternStart() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
            patternInProgress();
        }

        public void onPatternCleared() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
        }

        // 绘制完成回调
        public void onPatternDetected(List<LockPatternView.Cell> pattern) {
            if (pattern == null) {
                return;
            }
            if (mUiStage == NeedToConfirm || mUiStage == Stage.ConfirmWrong) {
                if (mChosenPattern == null)
                    throw new IllegalStateException("null chosen pattern in stage 'need to confirm");
                if (mChosenPattern.equals(pattern)) {
                    updateStage(Stage.ChoiceConfirmed);
                } else {
                    updateStage(Stage.ConfirmWrong);
                }
            } else if (mUiStage == Stage.Introduction || mUiStage == Stage.ChoiceTooShort) {
                if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
                    updateStage(Stage.ChoiceTooShort);
                } else {
                    mChosenPattern = new ArrayList<LockPatternView.Cell>(pattern);
                    updateStage(Stage.FirstChoiceValid);
                    //将图案显示在预览
                }
            } else {
                throw new IllegalStateException("Unexpected stage " + mUiStage + " when " + "entering the pattern.");
            }
        }

        public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

        }

        private void patternInProgress() {
            mHeaderText.setText(R.string.lockpattern_recording_inprogress);
            mFooterLeftButton.setEnabled(false);
            mFooterRightButton.setEnabled(false);
        }
    };

    private void updateStage(Stage stage) {
        mUiStage = stage;

        if (stage == Stage.ChoiceTooShort) {
            mHeaderText.setText(getResources().getString(stage.headerMessage, LockPatternUtils.MIN_LOCK_PATTERN_SIZE));
        } else {
            mHeaderText.setText(stage.headerMessage);
        }

        if (stage.leftMode == LeftButtonMode.Gone) {
            mFooterLeftButton.setVisibility(View.GONE);
        } else {
            mFooterLeftButton.setVisibility(View.VISIBLE);
            mFooterLeftButton.setText(stage.leftMode.text);
//			int i = stage.leftMode.text;
//			if(isFromNormal && stage.leftMode.enabled == true)
//				mFooterLeftButton.setEnabled(false);
//			else
            mFooterLeftButton.setEnabled(stage.leftMode.enabled);

        }

        mFooterRightButton.setText(stage.rightMode.text);
        mFooterRightButton.setEnabled(stage.rightMode.enabled);

        if (stage.patternEnabled) {
            mLockPatternView.enableInput();
        } else {
            mLockPatternView.disableInput();
        }

        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);

        switch (mUiStage) {
            case Introduction:
                mLockPatternView.clearPattern();
                break;
            case ChoiceTooShort://连接的点小于4
                mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                postClearPatternRunnable();
                break;
            case FirstChoiceValid://第一次输入合法了
                updatePreviewViews();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mUiStage != Stage.FirstChoiceValid) {
                            throw new IllegalStateException("expected ui stage " + Stage.FirstChoiceValid + " when button is "
                                    + RightButtonMode.Continue);
                        }
                        updateStage(NeedToConfirm);
                    }
                }, 350);

                isFirstInputOk = true;

                title_right.setText("重设");

                if (reqType == Param.ReqType.CREATE_FROM_SET) {
                    title_right.setVisibility(View.VISIBLE);
                }
                break;
            case NeedToConfirm://等待第二次确认
                mLockPatternView.clearPattern();
                updatePreviewViews();
                break;
            case ConfirmWrong://确认出现错误
                mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                postClearPatternRunnable();
                break;
            case ChoiceConfirmed://第二次输入通过
                if (mUiStage != Stage.ChoiceConfirmed) {
                    throw new IllegalStateException("expected ui stage " + Stage.ChoiceConfirmed + " when button is " + RightButtonMode.Confirm);
                }
                if (reqType == Param.ReqType.CREATE_FROM_LOGIN) {//第一次登录进入手势密码设置
                    saveChosenPatternAndFinish();
                } else if(reqType == Param.ReqType.CREATE_FROM_REGISTER){//第一次注册进入手势密码设置
                    saveChosenPatternAndFinish();
                }else if (reqType == Param.ReqType.CREATE_FROM_SET) {

                    String phone = sp.getOtherValueSecurity("user0");

                    new LockPatternUtils(this, phone).saveLockPattern(mChosenPattern);

                    SettingUtils.getInstance(App.getInstance()).putIsPatternPwdOn(user0, true);

                    setResult(ChangePattern.RESULT_CODE_SUCCESS);

                    MyDialog dialog = new MyDialog(this, "设置成功", Result.SUCCESS, 600);
                    dialog.setMyCallback(new MyCallback() {
                        @Override
                        public void callback() {
                            CreatePatternPwdActivity.this.finish();
                        }
                        @Override
                        public void doing() {
                        }
                    });
                    dialog.showDialog();
                }

                break;
            default:
                break;
        }
    }

    // 清除图案
    private void postClearPatternRunnable() {
        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, 1000);
    }

    private void saveChosenPatternAndFinish() {
        String phone = sp.getOtherValueSecurity("user0");
        new LockPatternUtils(this, phone).saveLockPattern(mChosenPattern);
//		new SharedPreferencesData(this).setBooleanByUser(phone,"isValidate", true);//设置已验证
        String user0 = sp.getOtherValue("user0");
        SettingUtils.getInstance(App.getInstance()).putIsPatternPwdOn(user0, true);
        sp.setBooleanByUser(user0, "isNotFirstIn_pattern", true);
        sp.setBoolean("hasLogin", true);
        MyDialog dialog = new MyDialog(this, "设置成功", Result.SUCCESS, 600);
        dialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() {
                Intent intent = new Intent();
                intent.putExtra("home", "");
                if(getIntent()!=null && getIntent().hasExtra("userType")){
                    intent.putExtra("userType", getIntent().getStringExtra("userType"));
                    intent.putExtra("regAsDeposit", getIntent().getStringExtra("regAsDeposit"));
                    intent.putExtra("depositCheck", getIntent().getStringExtra("depositCheck"));
                    App.userType=getIntent().getStringExtra("userType");
                    App.regAsDeposit=getIntent().getStringExtra("regAsDeposit");
                    App.depositCheck=getIntent().getStringExtra("depositCheck");
                }
//                if(reqType == Param.ReqType.CREATE_FROM_REGISTER)
//                    intent.putExtra("registerToPattern","");
                intent.setClass(CreatePatternPwdActivity.this, MainActivity.class);
                startActivity(intent);
                CreatePatternPwdActivity.this.finish();
            }

            @Override
            public void doing() {

            }
        });
        dialog.showDialog();
    }

    /**
     * 给控件设置监听
     */
    public void listener() {
        title_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.reset_btn:
                if (mUiStage.leftMode == LeftButtonMode.Retry) {
                    resetPreviewViews();
                    mChosenPattern = null;
                    mLockPatternView.clearPattern();
                    updateStage(Stage.Introduction);
                } else if (mUiStage.leftMode == LeftButtonMode.Cancel) {
                    if (isFromNormal)
                        return;
                    finish();
                } else {
                    throw new IllegalStateException(
                            "left footer button pressed, but stage of " + mUiStage + " doesn't make sense");
                }
                break;
            case R.id.right_btn:
                if (mUiStage.rightMode == RightButtonMode.Continue) {
                    if (mUiStage != Stage.FirstChoiceValid) {
                        throw new IllegalStateException("expected ui stage " + Stage.FirstChoiceValid + " when button is "
                                + RightButtonMode.Continue);
                    }
                    updateStage(NeedToConfirm);
                } else if (mUiStage.rightMode == RightButtonMode.Confirm) {
                    if (mUiStage != Stage.ChoiceConfirmed) {
                        throw new IllegalStateException("expected ui stage " + Stage.ChoiceConfirmed + " when button is "
                                + RightButtonMode.Confirm);
                    }
                    if (reqType == Param.ReqType.CREATE_FROM_LOGIN) {
                        saveChosenPatternAndFinish();
                    } else if (reqType == Param.ReqType.CREATE_FROM_SET) {

                        String phone = sp.getOtherValueSecurity("user0");

                        new LockPatternUtils(this, phone).saveLockPattern(mChosenPattern);

                        SettingUtils.getInstance(App.getInstance()).putIsPatternPwdOn(user0, true);

                        setResult(ChangePattern.RESULT_CODE_SUCCESS);

                        MyDialog dialog = new MyDialog(this, "设置成功", Result.SUCCESS, 500);
                        dialog.setMyCallback(new MyCallback() {
                            @Override
                            public void callback() {
                                finish();
                            }

                            @Override
                            public void doing() {
                            }
                        });

                        dialog.showDialog();
                    }
                } else if (mUiStage.rightMode == RightButtonMode.Ok) {
                    mLockPatternView.clearPattern();
                    mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                    updateStage(Stage.Introduction);
                }
                break;
            case R.id.navigation_right:
                if (!isFirstInputOk) {

                    this.finish();
                    setHasSetPattern();
                } else {
                    resetPreviewViews();
                    mChosenPattern = null;
                    mLockPatternView.clearPattern();
                    updateStage(Stage.Introduction);

                    title_right.setText("跳过");
                    isFirstInputOk = false;
                    if (reqType == Param.ReqType.CREATE_FROM_SET) {
                        title_right.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.tv_pattern_guid:

                break;
            default:
                break;
        }
    }

    /**
     * 左边按钮
     */
    enum LeftButtonMode {
        Cancel(R.string.cancel, true),
        CancelDisabled(R.string.cancel, false),
        Retry(R.string.lockpattern_retry_button_text, true),
        RetryDisabled(R.string.lockpattern_retry_button_text, false),
        Gone(ID_EMPTY_MESSAGE, false);

        /**
         * @param text    按钮文字
         * @param enabled 按钮是否可用
         */
        LeftButtonMode(int text, boolean enabled) {
            this.text = text;
            this.enabled = enabled;
        }

        final int text;
        final boolean enabled;
    }

    /**
     * 右边按钮
     */
    enum RightButtonMode {
        Continue(R.string.lockpattern_continue_button_text, true),
        ContinueDisabled(R.string.lockpattern_continue_button_text, false),
        Confirm(R.string.lockpattern_confirm_button_text, true),
        ConfirmDisabled(R.string.lockpattern_confirm_button_text, false),
        Ok(R.string.ok, true);

        /**
         * @param text    按钮的文
         * @param enabled 按钮是否可用
         */
        RightButtonMode(int text, boolean enabled) {
            this.text = text;
            this.enabled = enabled;
        }

        final int text;
        final boolean enabled;
    }

    /**
     * 设置图形密码状
     */
    protected enum Stage {

        Introduction(R.string.lockpattern_recording_intro_header, LeftButtonMode.Cancel, RightButtonMode.ContinueDisabled, ID_EMPTY_MESSAGE, true),
        ChoiceTooShort(R.string.lockpattern_recording_incorrect_too_short, LeftButtonMode.Retry, RightButtonMode.ContinueDisabled, ID_EMPTY_MESSAGE, true),
        FirstChoiceValid(R.string.lockpattern_pattern_entered_header, LeftButtonMode.Retry, RightButtonMode.Continue, ID_EMPTY_MESSAGE, false),
        NeedToConfirm(R.string.lockpattern_need_to_confirm, LeftButtonMode.Cancel, RightButtonMode.ConfirmDisabled, ID_EMPTY_MESSAGE, true),
        ConfirmWrong(R.string.lockpattern_need_to_unlock_wrong, LeftButtonMode.Cancel, RightButtonMode.ConfirmDisabled, ID_EMPTY_MESSAGE, true),
        ChoiceConfirmed(R.string.lockpattern_pattern_confirmed_header, LeftButtonMode.Cancel, RightButtonMode.Confirm, ID_EMPTY_MESSAGE, false);

        /**
         * @param headerMessage  头上的文字说
         * @param leftMode       左边按钮的状
         * @param rightMode      右边按钮的状
         * @param footerMessage  底部文字
         * @param patternEnabled 绘制控件是否可用
         */
        Stage(int headerMessage, LeftButtonMode leftMode, RightButtonMode rightMode, int footerMessage, boolean patternEnabled) {

            this.headerMessage = headerMessage;
            this.leftMode = leftMode;
            this.rightMode = rightMode;
            this.footerMessage = footerMessage;
            this.patternEnabled = patternEnabled;

        }

        final int headerMessage;
        final LeftButtonMode leftMode;
        final RightButtonMode rightMode;
        final int footerMessage;
        final boolean patternEnabled;
    }

    public void setHasSetPattern() {    //跳过设置图形密码
        String username = sp.getOtherValue("user0");
        sp.setBooleanByUser(username, "isNotFirstIn_pattern", true);
        sp.setBoolean("hasLogin", true);
        Intent intent = new Intent();
        intent.putExtra("home", "");
        if(getIntent()!=null && getIntent().hasExtra("userType")){
            intent.putExtra("userType", getIntent().getStringExtra("userType"));
            intent.putExtra("regAsDeposit", getIntent().getStringExtra("regAsDeposit"));
            intent.putExtra("depositCheck", getIntent().getStringExtra("depositCheck"));
            App.userType=getIntent().getStringExtra("userType");
            App.regAsDeposit=getIntent().getStringExtra("regAsDeposit");
            App.depositCheck=getIntent().getStringExtra("depositCheck");
        }
//        if(reqType == Param.ReqType.CREATE_FROM_REGISTER)
//            intent.putExtra("registerToPattern","");
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
    }

}

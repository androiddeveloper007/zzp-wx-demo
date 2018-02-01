package com.cstz.cunguan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.MyWidget.DepositDialog;
import com.cstz.common.App;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.WebViewRelease;
import com.cstz.cstz_android.R;
import com.cstz.front.Login;
import com.cstz.tools.ToastMakeText;
import com.cstz.usercenter.setting.ChangeFingerprintState;
import com.cstz.usercenter.setting.ChangePattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import control.pattern.tools.SettingUtils;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.view.View.GONE;

/**
 * 账户安全
 */
public class AccountSafety extends MyActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.iv_safety)
    GifImageView iv_safety;
    @BindView(R.id.tv_safety_pattern_state)
    TextView tvSafetyPatternState;
    @BindView(R.id.tv_account_safety_pattern)
    TextView tv_account_safety_pattern;
    @BindView(R.id.layout_change_pay_pwd)
    RelativeLayout layoutChangePayPwd;
    @BindView(R.id.layout_find_pay_pwd)
    RelativeLayout layoutFindPayPwd;
    @BindView(R.id.layout_forget_pay_pwd)
    RelativeLayout layout_forget_pay_pwd;
    @BindView(R.id.line_create_pattern)
    LinearLayout line_create_pattern;
    private SharedPreferencesData sp;
    private Context context;
    private String level="1";
    private String uCenter = "1";
    private DepositDialog loadDialog;
    private GifDrawable gifDrawable;
    private boolean hasLoadSafeLevel=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_account_safety);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        sp = new SharedPreferencesData(this);
        context = this;
        if(getIntent()!=null && getIntent().getStringExtra("uCenter")!=null)
            uCenter = getIntent().getStringExtra("uCenter");
        if(TextUtils.equals("1",uCenter) || !TextUtils.equals("1",App.depositCheck)){
            layoutChangePayPwd.setVisibility(GONE);
            layoutFindPayPwd.setVisibility(GONE);
        }
        //对公账户，1、渤海银行 2、非渤海银行
        if(TextUtils.equals("2",App.borrowerType)){
            //隐藏“修改、找回存管密码”
            layoutChangePayPwd.setVisibility(GONE);
            layoutFindPayPwd.setVisibility(GONE);
        }
        initView();
        iv_safety.setImageResource(R.drawable.safety_level);
        gifDrawable = (GifDrawable) iv_safety.getDrawable();
        gifDrawable.stop();
        requestSafetyLevel();
    }

    private void initView() {
        titleTv.setText("账户安全");
        titleBack.setVisibility(View.VISIBLE);
//        iv_safety.setImageResource(R.drawable.safety_level3);
        //设置借款人的时候，忘记支付密码
        if(TextUtils.equals("-1",App.userType)){
            layout_forget_pay_pwd.setVisibility(View.VISIBLE);
            line_create_pattern.setVisibility(View.VISIBLE);
        }
    }

    private void requestSafetyLevel() {
        String path = Config.getHttpConfig() + "/user/safetyLevel";
        final RequestParams params = new RequestParams(path);
        String deposit = App.depositCheck;
        if (!TextUtils.isEmpty(deposit))
            params.addParameter("deposit", deposit);
        else
            params.addParameter("deposit", "0");
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.addParameter("token", sp.getValue("token"));
        params.setConnectTimeout(20 * 1000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject obj = new JSONObject(arg0);
                    JSONObject data=null;
                    if(obj.has("data"))
                        data = new JSONObject(obj.getString("data"));
                    if(obj.has("result")&&TextUtils.equals("-4",obj.getString("result"))){
                        ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                        sp.removeAll();
                        Intent intent = new Intent(context, Login.class);
                        startActivity(intent);
                        finish();
                    }else if (data !=null&&data.has("level")) {
                        hasLoadSafeLevel=true;
                        level = data.getString("level");
                        startLevelAnimation();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showToast("服务器连接失败");
            }
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() {}
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    gifDrawable.stop();break;
                case 2:
                    gifDrawable.stop();break;
                case 3:
                    gifDrawable.stop();break;
                case 4:
                    gifDrawable.stop();break;
                default:break;
            }
            int position = 2600;
            switch (level){
                case "1":position=2600;break;
                case "2":position=3100;break;
                case "3":position=3500;break;
                case "4":position=5250;break;
                default:break;
            }
            gifDrawable.seekTo(position);
        }
    };
    private void startLevelAnimation() {
//        String hardware = Build.HARDWARE;//判断是否是海思平台cpu
//        boolean isHaisiCpu =  hardware.indexOf("hi")!=-1;
        gifDrawable.start();
        switch (level) {
            case "1":
                handler.sendEmptyMessageDelayed(1,2600);//!isHaisiCpu ? 2750:
                break;
            case "2":
                handler.sendEmptyMessageDelayed(2,3150);//!isHaisiCpu ? 3300:
                break;
            case "3":
                handler.sendEmptyMessageDelayed(3,3750);//!isHaisiCpu ? 4000:
                break;
            case "4":
                handler.sendEmptyMessageDelayed(4,5300);//!isHaisiCpu ? 5450:
                break;
        }
    }

    @OnClick({R.id.title_back, R.id.layout_change_pwd, R.id.layout_change_pay_pwd
            , R.id.layout_change_pattern, R.id.layout_find_pay_pwd,R.id.layout_forget_pay_pwd
    ,R.id.layout_fingerprint})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.layout_change_pwd:
                intent.setClass(this, UpdateLoginPwd.class);
                startActivity(intent);
                break;
            case R.id.layout_change_pay_pwd:
                requestChangeCgPwd(true);
                break;
            case R.id.layout_find_pay_pwd:
                requestChangeCgPwd(false);
                break;
            case R.id.layout_change_pattern:
                intent.setClass(this, ChangePattern.class);
                startActivity(intent);
                break;
            case R.id.layout_forget_pay_pwd:
                intent.setClass(this, com.cstz.cunguan.activity.UpdatePayPwd.class);
                startActivity(intent);
                break;
            case R.id.layout_fingerprint:
                intent.setClass(this, ChangeFingerprintState.class);
                startActivity(intent);
                break;
            default:break;
        }
    }

    private void requestChangeCgPwd(final boolean change) {//修改存管密码
        String path = Config.getHttpConfig() + "/bank/member/bindPass";
        final RequestParams params = new RequestParams(path);
        params.addParameter("token", sp.getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.addParameter("depositAccount", sp.getValue("depositAccount"));
        if (!change) params.addParameter("mobile", sp.getOtherValue("user0"));
        params.setConnectTimeout(20 * 1000);
        if(loadDialog==null)
            loadDialog = new DepositDialog(context, "加载中...");
        loadDialog.setDuration(300);
        loadDialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() {}
            @Override
            public void doing() {
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try{
                            JSONObject data = new JSONObject(arg0);
                            if(data.has("key") && TextUtils.equals("success",data.getString("key"))){
                                if(data.has("relationKey"))
                                    startActivity(new Intent(context, WebViewRelease.class).
                                            putExtra("html", data.getString("relationKey")).putExtra("title", change?"修改存管密码":"找回存管密码"));
                            }else if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
                                ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                                sp.removeAll();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastMakeText.showToast((Activity) context,data.getString("message"),1200);
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastMakeText.showToast((Activity) context, "服务器连接失败", 1000);
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {
                    }
                    @Override
                    public void onFinished() {
                        if(loadDialog!=null)
                            loadDialog.hideDialog();
                    }
                });
            }
        });
        loadDialog.showDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String phone = sp.getOtherValue("user0");
        if (SettingUtils.getInstance(App.getInstance()).isPatternPwdOn(phone)) {
            tvSafetyPatternState.setText("修改");
            tv_account_safety_pattern.setText("修改手势密码");
        }else {
            tvSafetyPatternState.setText("新建");
            tv_account_safety_pattern.setText("设置手势密码");
        }
        if(hasLoadSafeLevel){
            int position = 2600;
            switch (level){
                case "1":position=2600;break;
                case "2":position=3100;break;//3090
                case "3":position=3500;break;//3567
                case "4":position=5250;break;
                default:break;
            }
            gifDrawable.seekTo(position);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(gifDrawable.isPlaying()){
            gifDrawable.stop();
        }
    }

    // 获取CPU名字
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
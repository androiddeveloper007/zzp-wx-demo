package com.cstz.cunguan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.MyWidget.DepositDialog;
import com.cstz.common.App;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.dialog.PayPasswordDialog;
import com.cstz.front.Login;
import com.cstz.tools.ToastMakeText;
import com.leaking.slideswitch.SlideSwitch;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 还款设置
 *
 * @author zzp
 */
public class RepaymentSet extends MyActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.slide_pattern)
    SlideSwitch slidePattern;
    private Context context;
    private SharedPreferencesData sp;
    private DepositDialog loadDialog;
    private PayPasswordDialog d;
    private boolean changeStateSuccess=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_repayment_set);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        context = this;
        sp = new SharedPreferencesData(this);
        initView();
    }

    private void initView() {
        titleTv.setText("还款设置");
        titleBack.setVisibility(View.VISIBLE);
        slidePattern.setSlideable(false);
        String auto_repayment = App.auto_repayment;
        if(TextUtils.equals("1",auto_repayment))
            slidePattern.initState(true);
        else
            slidePattern.initState(false);
        slidePattern.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                changeStateSuccess=false;
                d = new PayPasswordDialog(context,0.85);
                d.setTitle("请输入还款密码确认开启");
                d.setOnRightClickListener(new PayPasswordDialog.onClickListener() {
                    @Override
                    public void click() {
                        if(TextUtils.isEmpty(d.etInvestVerify.getText().toString())) {showToast("请输入支付密码");return;}
                        requestChangeState(true, d.etInvestVerify.getText().toString());
                    }
                });
                d.show();
                d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        slidePattern.initState(changeStateSuccess);
                    }
                });
            }
            @Override
            public void close() {
                changeStateSuccess=false;
                d = new PayPasswordDialog(context,0.85);
                d.setTitle("请输入还款密码确认关闭");
                d.setOnRightClickListener(new PayPasswordDialog.onClickListener() {
                    @Override
                    public void click() {
                        if(TextUtils.isEmpty(d.etInvestVerify.getText().toString())) {showToast("请输入支付密码");return;}
                        requestChangeState(false,d.etInvestVerify.getText().toString());
                    }
                });
                d.show();
                d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        slidePattern.initState(!changeStateSuccess);
                    }
                });
            }
        });
    }

    private void requestChangeState(final boolean b,String pwd) {
        changeStateSuccess=false;
        int autoRepayment = b? 1:0;
        String path = Config.getHttpConfig() + "/bank/member/autoRepaymentAPP";
        final RequestParams params = new RequestParams(path);
        params.addParameter("dealPWD", pwd);
        params.addParameter("autoRepayment", autoRepayment);
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        loadDialog = new DepositDialog(context,"加载中...");
        loadDialog.setDuration(1000);
        loadDialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() {}
            @Override
            public void doing() {
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try {
                            JSONObject object = new JSONObject(arg0);
                            if(object.has("result")&&TextUtils.equals("-4",object.getString("result"))){
                                ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                                sp.removeAll();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                            String key = object.getString("key");
                            switch (key) {
                                case "success":
                                    changeStateSuccess = true;
                                    showToast(object.getString("message"),3000);
                                    slidePattern.initState(b);
                                    App.auto_repayment=b?"1":"0";
                                    break;
                                case "error":
                                    showToast(object.getString("message"),3000);
                                    slidePattern.initState(!b);
                                    break;
                                default:break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastMakeText.showToast((Activity) context, "服务器连接失败", 2000);
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {
                    }
                    @Override
                    public void onFinished() {
                        if(loadDialog!=null)
                            loadDialog.hideDialog();
                        d.dismiss();
                        if(!changeStateSuccess)
                            slidePattern.initState(!b);
                    }
                });}
        });
        loadDialog.showDialog();
    }

    @OnClick({R.id.title_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            default:break;
        }
    }
}
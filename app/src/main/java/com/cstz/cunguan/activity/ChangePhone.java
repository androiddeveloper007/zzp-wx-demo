package com.cstz.cunguan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.MyWidget.DepositDialog;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.Validate;
import com.cstz.common.WebViewRelease;
import com.cstz.cstz_android.R;
import com.cstz.front.Login;
import com.cstz.tools.ToastMakeText;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 更改手机号
 *
 * @author zzp
 */
public class ChangePhone extends MyActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.tv_change_phone)
    TextView tvChangePhone;
    @BindView(R.id.et_change_phone)
    com.cstz.MyWidget.MClearEditText etChangePhone;
    private Context context;
    private SharedPreferencesData sp;
    private DepositDialog loadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.stopSendButton();
        setContentView(R.layout.aa_activity_change_phone);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        context = this;
        sp = new SharedPreferencesData(this);
        titleBack.setVisibility(View.VISIBLE);
        titleTv.setText("手机号码");
        tvChangePhone.setText(Html.fromHtml("<u>" + sp.getValue("phone") + "</u>"));
    }

    @OnClick({R.id.title_back, R.id.ll_change_phone_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.ll_change_phone_submit:
                if (ToastMakeText.isFastClick1()) return;
                if (TextUtils.isEmpty(etChangePhone.getText().toString())){
                    showToast("请输入新手机号");
                    return;
                }
                if (!Validate.validatePhone(etChangePhone.getText().toString())) {
                    ToastMakeText.showToast((Activity) context, "手机号码格式不正确", 1200);
                    return;
                }
                requestChangePhone();
                break;
            default:break;
        }
    }

    private void requestChangePhone() {
        String path = Config.getHttpConfig() + "/bank/member/bindMobileNo";
        final RequestParams params = new RequestParams(path);
        String depositAccount = sp.getValue("depositAccount");
        if (TextUtils.isEmpty(depositAccount)) {
            showToast("存管账号不能为空");
            return;
        }
        params.addParameter("depositAccount", depositAccount);
        params.addParameter("mobile", etChangePhone.getText().toString().trim());
        params.addParameter("token", sp.getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
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
                        try {
                            JSONObject data = new JSONObject(arg0);
                            if (data.has("key") && TextUtils.equals("success", data.getString("key"))) {
                                if (data.has("relationKey"))
                                    startActivity(new Intent(context, WebViewRelease.class).
                                            putExtra("html", data.getString("relationKey")).putExtra("title", "修改手机号"));
                            } else if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
                                ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                                sp.removeAll();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastMakeText.showToast((Activity) context, data.getString("message"), 2000);
                            }
                        } catch (JSONException e) {
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
}
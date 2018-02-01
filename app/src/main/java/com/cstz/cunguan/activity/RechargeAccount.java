package com.cstz.cunguan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.MyWidget.DepositDialog;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
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
 * 充值账户
 */
public class RechargeAccount extends MyActivity implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.tv_recharge_account_balance)
    TextView tv_recharge_account_balance;
    @BindView(R.id.tv_recharge_account_consumer_name)
    TextView tv_recharge_account_consumer_name;
    @BindView(R.id.tv_recharge_account_consumer_account)
    TextView tv_recharge_account_consumer_account;
    @BindView(R.id.tv_recharge_account_consumer_bank)
    TextView tv_recharge_account_consumer_bank;

    private Context context;
    private SharedPreferencesData sp;
    private DepositDialog loadDialog;
    private String accountName,chargeAccount,avlBal="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_recharge_account);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        titleTv.setText("充值账户");
        titleBack.setVisibility(View.VISIBLE);
        context = this;
        sp = new SharedPreferencesData(this);
        initView();
        requestBigRechargeData();
    }

    private void initView() {

    }

    @OnClick({R.id.title_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            default:break;
        }
    }

    private void requestBigRechargeData() {
        String depositAccount = sp.getValue("depositAccount");
        if (TextUtils.isEmpty(depositAccount)) {showToast("存管账号不能为空");return;}
        String path = Config.getHttpConfig() + "/bank/member/queryChargeAccount";
        final RequestParams params = new RequestParams(path);
        params.addParameter("depositAccount", depositAccount);
        params.addParameter("accountTyp", 2);//1-对私 2-对公
        params.addParameter("token", sp.getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        loadDialog = new DepositDialog(this, "加载中...");
        loadDialog.setDuration(100);
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
                                JSONObject relationKey = data.getJSONObject("relationKey");
                                if(relationKey.has("accountName"))
                                    accountName = relationKey.getString("accountName");
                                if(relationKey.has("chargeAccount"))
                                    chargeAccount = relationKey.getString("chargeAccount");
                                if(relationKey.has("avlBal"))
                                    avlBal = relationKey.getString("avlBal");
                                tv_recharge_account_balance.setText(avlBal);
                                tv_recharge_account_consumer_name.setText(accountName);
                                tv_recharge_account_consumer_account.setText(chargeAccount);
                                tv_recharge_account_consumer_bank.setText("渤海银行深圳分行");
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
                    public void onCancelled(CancelledException cex) {}
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
    protected void onDestroy() {
        super.onDestroy();
    }
}
package com.cstz.cunguan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.MyWidget.DepositDialog;
import com.cstz.MyWidget.MClearEditText;
import com.cstz.common.App;
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

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 提现
 *
 * @author zzp
 */
public class WithDraw extends MyActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.recharge_money)
    MClearEditText rechargeMoney;
    @BindView(R.id.navigation_right)
    TextView navigationRight;
    @BindView(R.id.iv_navigation_right)
    ImageView ivNavigationRight;
    @BindView(R.id.tv_withdraw_realname)
    TextView tvWithdrawRealname;
    @BindView(R.id.tv_withdraw_cg_account)
    TextView tvWithdrawCgAccount;
    @BindView(R.id.tv_withdraw_money_usable)
    TextView tvWithdrawMoneyUsable;
    @BindView(R.id.tv_withdraw_money_free)
    TextView tvWithdrawMoneyFree;
    private Context context;
    private SharedPreferencesData sp;
    private DepositDialog loadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_withdraw);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        context = this;
        sp = new SharedPreferencesData(this);
        initView();
        requestUsableAmount();
    }

    private void requestUsableAmount() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", sp.getValue("token"));
        map.put("ucenter", "2");//这里一直是存管账户
        postData(map, "/user/index", null, App.PostType.SUBMIT);
    }

    private void initView() {
        titleTv.setText("提现");
        titleBack.setVisibility(View.VISIBLE);
        navigationRight.setText("提现记录");
        navigationRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dimen_16));
        navigationRight.setVisibility(View.VISIBLE);
        tvWithdrawRealname.setText("真实姓名："+sp.getValue("realName"));
        tvWithdrawCgAccount.setText("渤海存管账号："+Validate.replaceWithStar(sp.getValue("depositAccount")));
    }

    @OnClick({R.id.title_back, R.id.navigation_right,R.id.home_recharge_button})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.navigation_right:
                startActivity(new Intent(this, WithDrawRecord.class));
                break;
            case R.id.home_recharge_button:
                requestWithdraw();
                break;
            default:break;
        }
    }

    private void requestWithdraw() {
        String depositAccount = sp.getValue("depositAccount");
        if (TextUtils.isEmpty(depositAccount)) {
            showToast("当前存管账号为空，请重新登录或联系客服。",3000);
            return;
        }
        if (TextUtils.isEmpty(rechargeMoney.getText().toString())) {
            showToast("请输入提现金额");
            return;
        }
        String path = Config.getHttpConfig() + "/bank/member/withdraw";
        final RequestParams params = new RequestParams(path);
        params.addParameter("depositAccount", depositAccount);
        params.addParameter("money", rechargeMoney.getText().toString().trim());
        params.addParameter("token", sp.getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        loadDialog = new DepositDialog(this, "加载中...");
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
                                            putExtra("html", data.getString("relationKey")).putExtra("title", "提现"));
                            } else if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
                                ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                                sp.removeAll();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                                finish();
                            }else {
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

    /*请求免提额度*/
    private void requestWithdrawFee() {
        String path = Config.getHttpConfig() + "/bank/member/userFreeChargeAmount";
        final RequestParams params = new RequestParams(path);
        params.addParameter("token", sp.getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject data = new JSONObject(arg0);
                    if (data.has("key") && TextUtils.equals("success", data.getString("key"))) {
                        if (data.has("relationKey") && !TextUtils.isEmpty(data.getString("relationKey")))
                            tvWithdrawMoneyFree.setText(data.getString("relationKey")+"元");
                    } else if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
                        ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                        sp.removeAll();
                        Intent intent = new Intent(context, Login.class);
                        startActivity(intent);
                        finish();
                    }else {
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

    @Override
    public void requestSuccess(JSONObject object, App.PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            try {
                JSONObject data = object.getJSONObject("data");
                String availableCount = data.getString("cg_usableAmount").replace(",", "");
                String withdrawMoneyFree = data.getString("cg_usableAmount").replace(",", "");
                tvWithdrawMoneyUsable.setText(availableCount+"元");
                tvWithdrawMoneyFree.setText(withdrawMoneyFree+"元");//Convert.strToFloat
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void requestFinished() {
        super.requestFinished();
        requestWithdrawFee();
    }
}
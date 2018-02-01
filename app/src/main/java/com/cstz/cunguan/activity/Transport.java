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
import android.widget.TextView;

import com.cstz.MyWidget.DepositDialog;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.dialog.ExitDialog;
import com.cstz.front.Login;
import com.cstz.tools.ToastMakeText;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.widget.NumberAnimTextView;

/**
 * 迁移
 *
 * @author zzp
 */
public class Transport extends MyActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.navigation_right)
    TextView navigationRight;
    @BindView(R.id.btn_transport)
    TextView btn_transport;
    @BindView(R.id.iv_transport_bg)
    ImageView ivTransportBg;
    @BindView(R.id.tv_old_account)
    NumberAnimTextView tvOldAccount;
    @BindView(R.id.tv_new_account)
    NumberAnimTextView tvNewAccount;
    private Context context;
    private SharedPreferencesData sp;
    private DepositDialog loadDialog;
    private String usableAmount;
    private String cg_usableAmount;
    public static final int REQUEST_TRANSPORT_SUC = 0x00000000;
    public static final int RESULT_TRANSPORT_SUC = 0x00000001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.stopSendButton();
        setContentView(R.layout.aa_activity_transport);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        context = this;
        sp = new SharedPreferencesData(this);
        titleTv.setText("原账户资金迁移");
        titleBack.setVisibility(View.VISIBLE);
//        navigationRight.setText("跳过");
//        navigationRight.setVisibility(View.VISIBLE);
        ivTransportBg.setScaleType(ImageView.ScaleType.FIT_XY);
        if(getIntent()!=null && getIntent().hasExtra("usableAmount")){
            usableAmount = getIntent().getStringExtra("usableAmount");
            cg_usableAmount = getIntent().getStringExtra("cg_usableAmount");
        }
        tvOldAccount.setText(usableAmount);
        tvNewAccount.setText(cg_usableAmount);
    }

    @OnClick({R.id.title_back, R.id.navigation_right, R.id.btn_transport})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.navigation_right:
                break;
            case R.id.btn_transport:
                if(!(Double.parseDouble(usableAmount)>0)){showToast("原账户余额为0，不支持资金迁移。");return;}
                requestTransport();
                break;
            default:break;
        }
    }

    private void requestTransport() {
        String path = Config.getHttpConfig() + "/bank/member/userBalTransfer";
        final RequestParams params = new RequestParams(path);
        String token = sp.getValue("token");
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        if(TextUtils.isEmpty(token)) showToast("token is null");
        String depositAccount = sp.getValue("depositAccount");
        if(TextUtils.isEmpty(depositAccount)) showToast("depositAccount is null");
        params.addParameter("token", token);
        params.addParameter("depositAccount", depositAccount);
        params.setConnectTimeout(20 * 1000);
        loadDialog = new DepositDialog(context, "请稍等...");
        loadDialog.setDuration(300);
        loadDialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() {
            }
            @Override
            public void doing() {
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try {
//                            showLog("迁移时返回的结果："+arg0);
                            btn_transport.setText("迁移中...");
                            JSONObject data = new JSONObject(arg0);
                            if(data.has("key") && TextUtils.equals("success",data.getString("key"))){
                                tvOldAccount.setNumberString(usableAmount, "0.00");
                                BigDecimal b1 = new BigDecimal(usableAmount);
                                BigDecimal b2 = new BigDecimal(cg_usableAmount);
                                tvNewAccount.setNumberString(b1.add(b2).toString());
                                mHandler.sendEmptyMessageDelayed(1, 1000);
                            }else if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
                                ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                                sp.removeAll();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                                finish();
                            }else{
//                                showToast("迁移失败",3000);
                                showToast(data.getString("message"),3000);
                                btn_transport.setText(data.getString("迁移失败"));
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
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    btn_transport.setText("迁移成功");
                    final ExitDialog d = new ExitDialog(context, 0.85);
                    d.setText("恭喜您资金迁移成功，请在存管账户查看。");
                    d.setTextSize(R.dimen.dimen_14);
                    d.showCenterBtn();
                    d.setFillViewVisibility(false);
                    d.setOnCenterClickListener(new ExitDialog.onClickListener() {
                        @Override
                        public void click() {
                            d.dismiss();
                            finish();
                        }
                    });
                    d.show();
                    setResult(RESULT_TRANSPORT_SUC);
                    break;
                default:break;
            }
            super.handleMessage(msg);
        }
    };
}
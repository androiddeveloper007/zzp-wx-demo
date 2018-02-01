package com.cstz.cunguan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.MyWidget.DepositDialog;
import com.cstz.MyWidget.MClearEditText;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.Validate;
import com.cstz.common.WebViewRelease;
import com.cstz.common.WebViewReleaseCg;
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
 * 充值
 *
 * @author zzp
 */
public class Recharge extends MyActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
//    @BindView(R.id.slider_recharge)
//    MySliderCg sliderRecharge;
//    @BindView(R.id.pager_recharge)
//    ViewPager pagerRecharge;
//    private List<View> viewList;
//    private View page1, page2;
    private TextView tv_recharge_userName;
    private TextView tv_recharge_cg_account;
//    private TextView tv_recharge_userName_big;
//    private TextView tv_recharge_cg_account_big,tv_recharge_balance_big;
    private MClearEditText et_recharge_money;
    private LinearLayout ll_recharge_button_cg;
//    private LinearLayout ll_refresh_recharge_big;
    private SharedPreferencesData sp;
    private DepositDialog loadDialog;
    private Context context;
    private String accountName,chargeAccount,avlBal="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.stopSendButton();
        setContentView(R.layout.aa_pager_recharge0);//aa_activity_recharge
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        sp = new SharedPreferencesData(this);
        context = this;
        initViews();
    }

    private void initViews() {
        titleTv.setText("充值");
        titleBack.setVisibility(View.VISIBLE);
        initPage1();
        /*sliderRecharge.setOnClickSlider(new MySliderCg.OnClickSlider() {
            @Override
            public void getIndex(int index) {
                pagerRecharge.setCurrentItem(index);
            }
        });
        viewList = new ArrayList<>();
        page1 = View.inflate(this, R.layout.aa_pager_recharge0, null);
        page2 = View.inflate(this, R.layout.aa_pager_recharge1, null);
        viewList.add(page1);
        viewList.add(page2);
        initPage1();
        initPage2();
        pagerRecharge.setAdapter(new MyPagerAdapter(viewList));
        pagerRecharge.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                sliderRecharge.setSelectIndex(position);
                if(position==1){
                    hideSoftKeyboard();
                    requestBigRechargeData();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });*/
    }

    private void initPage1() {
        tv_recharge_userName = (TextView)findViewById(R.id.tv_recharge_userName);//page1.
        tv_recharge_cg_account = (TextView)findViewById(R.id.tv_recharge_cg_account);//
        et_recharge_money = (MClearEditText)findViewById(R.id.et_recharge_money);//
        ll_recharge_button_cg = (LinearLayout)findViewById(R.id.ll_recharge_button_cg);//
        ll_recharge_button_cg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ToastMakeText.isFastClick()) return;
                requestRecharge();
            }
        });
        tv_recharge_userName.setText("真实姓名："+sp.getValue("realName"));
        tv_recharge_cg_account.setText("渤海存管账号："+Validate.replaceWithStar(sp.getValue("depositAccount")));
    }

    private void initPage2() {
//        tv_recharge_userName_big = (TextView)page2.findViewById(R.id.tv_recharge_userName_big);
//        tv_recharge_cg_account_big = (TextView)page2.findViewById(R.id.tv_recharge_cg_account_big);
//        tv_recharge_balance_big = (TextView)page2.findViewById(R.id.tv_recharge_balance_big);
//        ll_refresh_recharge_big = (LinearLayout)page2.findViewById(R.id.ll_refresh_recharge_big);
//        ll_refresh_recharge_big.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                requestBigRechargeRefresh();
//            }
//        });
//        SpannableStringBuilder style = new SpannableStringBuilder("（当前可用余额为"+avlBal+"元）");
//        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_title)), 8, 9 + avlBal.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tv_recharge_balance_big.setText(style);
    }

    private void requestRecharge() {
        String depositAccount = sp.getValue("depositAccount");
        if (TextUtils.isEmpty(depositAccount)) {
            showToast("当前存管账号为空，请重新登录或联系客服。",3000);
            return;
        }
        if (TextUtils.isEmpty(et_recharge_money.getText().toString())) {
            showToast("请输入充值金额");
            return;
        }
        String path = Config.getHttpConfig() + "/bank/member/recharge";
        final RequestParams params = new RequestParams(path);
        params.addParameter("depositAccount", depositAccount);
        params.addParameter("money", et_recharge_money.getText().toString().trim());
        params.addParameter("token", sp.getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        loadDialog = new DepositDialog(this, "加载中...");
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
                            JSONObject data = new JSONObject(arg0);
                            if (data.has("key") && TextUtils.equals("success", data.getString("key"))) {
                                if (data.has("relationKey"))
                                    startActivity(new Intent(context, WebViewRelease.class).
                                            putExtra("html", data.getString("relationKey")).putExtra("title", "充值"));
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

    private void requestBigRechargeData() {
        String depositAccount = sp.getValue("depositAccount");
        if (TextUtils.isEmpty(depositAccount)) {showToast("存管账号不能为空");return;}
        String path = Config.getHttpConfig() + "/bank/member/queryChargeAccount";
        final RequestParams params = new RequestParams(path);
        params.addParameter("depositAccount", depositAccount);
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
                                JSONObject relationKey = data.getJSONObject("relationKey");
                                if(relationKey.has("accountName"))
                                    accountName = relationKey.getString("accountName");
                                if(relationKey.has("chargeAccount"))
                                    chargeAccount = relationKey.getString("chargeAccount");
                                if(relationKey.has("avlBal"))
                                    avlBal = relationKey.getString("avlBal");
//                                tv_recharge_userName_big.setText(accountName);
//                                tv_recharge_cg_account_big.setText(chargeAccount);
                                SpannableStringBuilder style = new SpannableStringBuilder("（当前可用余额为"+avlBal+"元）");
                                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.product_title)), 8, 9 + avlBal.length(),
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                tv_recharge_balance_big.setText(style);
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

    private void requestBigRechargeRefresh() {
        String token = sp.getValue("token");
        String path = Config.getHttpConfig() + "/bank/member/syncDepositFund";
        final RequestParams params = new RequestParams(path);
        params.addParameter("token", token);
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
                                ToastMakeText.showToast((Activity) context, data.getString("message"), 2000);
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

    @OnClick({R.id.title_back,R.id.tv_recharge_detail})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.tv_recharge_detail:
//                RechargeTipDialog d = new RechargeTipDialog(context,0.9);
//                d.show();
                Intent i = new Intent(this, WebViewReleaseCg.class);
                i.putExtra("url", Config.getHttpConfig() + "/view/usercenter/common/recharge_limit.html");
                i.putExtra("title", "充值限额");
                startActivity(i);
                break;
            default:break;
        }
    }
}
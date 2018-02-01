package com.cstz.cunguan.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.MyWidget.DepositDialog;
import com.cstz.common.App;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.SysApplication;
import com.cstz.common.UpdateManager;
import com.cstz.common.WebViewRelease;
import com.cstz.common.WebViewReleaseCg;
import com.cstz.common.view.UpdateDialog;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.dialog.ExitDialog;
import com.cstz.cunguan.fragment.UserCenter;
import com.cstz.front.Login;
import com.cstz.tools.ToastMakeText;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置
 */
public class CgSetting extends MyActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.iv_update)
    ImageView iv_update;
    @BindView(R.id.textView_phone)
    TextView textViewPhone;
    @BindView(R.id.textView_nickname)
    TextView textView_nickname;
    @BindView(R.id.tv_bind_cardnum)
    TextView tv_bind_cardnum;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.tv_versionname)
    TextView tv_versionname;
    @BindView(R.id.layout_bankcard)
    RelativeLayout layout_bankcard;
    @BindView(R.id.layout_realName)
    RelativeLayout layout_realName;
    @BindView(R.id.layout_research)
    RelativeLayout layout_research;
    SharedPreferencesData sp;
    private String bankName = "";
    private String bankMobilePhone = "";
    private String cardNo = "";
    private String depositAccount;
    private String userType;
    private String regAsDeposit;
    private String depositCheck;
    private String uCenter = "1";//1:原账户 2：存管账户 3：借款人
    private DepositDialog depositDialog;
    private Context context;
    private boolean isFromAddCgPage;
    private ExitDialog d;
    private String appUrl = "";
    private UpdateDialog updateDialog;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_setting);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        context = this;
        sp = new SharedPreferencesData(this);
        //1:原账户 2：存管账户 3：借款人
        if (getIntent() != null && getIntent().getStringExtra("uCenter") != null)
            uCenter = getIntent().getStringExtra("uCenter");
        initView();
        if (TextUtils.equals("1", uCenter)) {

        } else {
            if (TextUtils.equals("1", depositCheck)) {
                depositAccount = sp.getValue("depositAccount");
                if (depositAccount.length() > 4) {
                    tv_bind_cardnum.setText("尾号 ("+depositAccount.substring(depositAccount.length() - 4)+")");
                } else
                    tv_bind_cardnum.setText(depositAccount);
            } else
                tv_bind_cardnum.setText("未开通");
        }
    }

    private void initView() {
        titleTv.setText("设置");
        titleBack.setVisibility(View.VISIBLE);

        PackageManager manager = this.getPackageManager();
        String version = "";
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tv_versionname.setText("当前版本号：V" + version);
        textViewPhone.setText(sp.getValue("phone"));

        String realName = sp.getValue("realName");
        textView_nickname.setText(TextUtils.isEmpty(realName) ? "未认证" : realName);

        depositAccount = sp.getValue("depositAccount");
        //根据用户类型分别显示不同文字
        userType = App.userType;
        regAsDeposit = App.regAsDeposit;
        depositCheck = App.depositCheck;
        if (TextUtils.equals("-1", userType)) {
            //借款人
            textView3.setText("存管账户");
            imageView2.setVisibility(View.VISIBLE);
            layout_research.setVisibility(View.GONE);//借款人隐藏问卷
            //对公账户，1、渤海银行 2、非渤海银行
            if(TextUtils.equals("2",App.borrowerType)){
                //隐藏“实名认证”、“存管账户”
                layout_realName.setVisibility(View.GONE);
                layout_bankcard.setVisibility(View.GONE);
            }
        } else if (TextUtils.equals("1", regAsDeposit)) {
            //新注册用户，全部显示存管账户
            textView3.setText("存管账户");
            if(TextUtils.equals("1",depositCheck))
                imageView2.setVisibility(View.VISIBLE);
            else
                imageView2.setVisibility(View.VISIBLE);
        } else if (TextUtils.equals("1", uCenter)) {
            //老用户开通了存管，但个人中心切换到了原账户
            //老用户且未开通存管
            textView3.setText("银行卡");
            imageView2.setVisibility(View.GONE);
            if(TextUtils.equals("1",sp.getValue("cardStatus"))){
                tv_bind_cardnum.setText(sp.getValue("cardNo"));
            }else{
//                tv_bind_cardnum.setText("未绑定");
                layout_bankcard.setVisibility(View.GONE);
            }
        } else if (!TextUtils.equals("1", regAsDeposit) && TextUtils.equals("2", uCenter)) {
            //老用户开通了存管且个人中心为存管账户
            textView3.setText("存管账户");
            imageView2.setVisibility(View.VISIBLE);
        }

        if (App.HAS_UPDATE) {
            iv_update.setVisibility(View.VISIBLE);
            tv_versionname.setVisibility(View.GONE);
        } else {
            iv_update.setVisibility(View.GONE);
            tv_versionname.setVisibility(View.VISIBLE);
            try {
                PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
                version = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            tv_versionname.setText("V" + version);
        }
    }

    @OnClick({R.id.layout_phone, R.id.layout_realName, R.id.layout_bankcard,R.id.layout_safety,
            R.id.title_back, R.id.layout_research, R.id.layout_button_exit,R.id.layout_update})
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.layout_phone:
                //对公账户，渤海和非渤海
                if(TextUtils.equals("2",App.borrowerType)){
                    final ExitDialog d = new ExitDialog(this, 0.85);
                    d.setText("请联系客服修改");
                    d.setTextSize(R.dimen.dimen_18);
                    d.showCenterBtn();
                    d.setCenterBtnText("确定");
                    d.setOnCenterClickListener(new ExitDialog.onClickListener() {
                        @Override
                        public void click() {
                            d.dismiss();
                        }
                    });
                    d.show();
                    return;
                }
                //未开通存管或者是原账户进入设置
                if (TextUtils.equals("1", uCenter) || !TextUtils.equals("1",App.depositCheck)) {
                    if (TextUtils.equals("1", regAsDeposit)){
                        final ExitDialog d = new ExitDialog(this, 0.85);
                        d.setText("开通存管账户后即可修改手机号");
                        d.setLeftBtnText("稍后再说");
                        d.setRightBtnText("马上开通");
                        d.setOnRightClickListener(new ExitDialog.onClickListener() {
                            @Override
                            public void click() {
                                requestAddCgAccount();
                                d.dismiss();
                            }
                        });
                        d.show();
                    }
                    return;
                }
                startActivity(new Intent(this, ChangePhone.class));
                break;
            case R.id.layout_realName:
                if (TextUtils.isEmpty(sp.getValue("realName"))) {
                    final ExitDialog d = new ExitDialog(this, 0.85);
                    d.setText("开通存管账户后即可完成实名认证");
                    d.setLeftBtnText("稍后再说");
                    d.setRightBtnText("马上开通");
                    d.setOnRightClickListener(new ExitDialog.onClickListener() {
                        @Override
                        public void click() {
                            requestAddCgAccount();
                            d.dismiss();
                        }
                    });
                    d.show();
                } else {
                    startActivity(new Intent(this, CgBindRealName.class).putExtra("",sp.getValue("")));
                }
                break;
            case R.id.layout_bankcard:
                if (TextUtils.equals("1", uCenter) || !TextUtils.equals("1",App.depositCheck)) {
                    //老用户开通了存管，但个人中心切换到了原账户 //老用户且未开通存管
                    if(TextUtils.equals("1",sp.getValue("cardStatus"))){// 1 审核成功 2 待审核 3 审核失败
                        i = new Intent(this, BankCard.class);
                        i.putExtra("bankName", sp.getValue("bankName"));
                        i.putExtra("bankMobilePhone", sp.getValue("phone"));
                        i.putExtra("cardNo", sp.getValue("cardNo"));
                        startActivity(i);
                    }else{
                        if(TextUtils.equals("1",depositCheck)){
                            i = new Intent(this, BankCardCg.class);
                            i.putExtra("bankName", bankName);
                            i.putExtra("bankMobilePhone", bankMobilePhone);
                            i.putExtra("cardNo", cardNo);
                            if (!TextUtils.isEmpty(depositAccount))
                                i.putExtra("depositAccount", depositAccount);
                            startActivity(i);
                        }else{
                            final ExitDialog d = new ExitDialog(this, 0.85);
                            d.setText("开通存管账户后即可完成绑定");
                            d.setLeftBtnText("稍后再说");
                            d.setRightBtnText("马上开通");
                            d.setOnRightClickListener(new ExitDialog.onClickListener() {
                                @Override
                                public void click() {
                                    requestAddCgAccount();
                                    d.dismiss();
                                }
                            });
                            d.show();
                        }
                    }
                } else {
                    //老用户开通了存管且个人中心为存管账户,或者是新新用户
                    i = new Intent(this, BankCardCg.class);
//                    i.putExtra("bankName", bankName);
//                    i.putExtra("bankMobilePhone", bankMobilePhone);
//                    i.putExtra("cardNo", cardNo);
                    if (!TextUtils.isEmpty(depositAccount))
                        i.putExtra("depositAccount", depositAccount);
                    startActivity(i);
                }
                break;
            case R.id.layout_safety:
                startActivity(new Intent(this, AccountSafety.class).putExtra("uCenter", uCenter));
                break;
            case R.id.layout_research:
                i = new Intent(this, WebViewReleaseCg.class);
                i.putExtra("url", Config.getHttpConfig() + "/activity/toRiskQuery");
                i.putExtra("title", "合格投资者问卷");
                startActivity(i);
                break;
            case R.id.title_back:
                if(isFromAddCgPage)
                    setResult(UserCenter.RESULT_ADD_CG);
                finish();
                break;
            case R.id.layout_button_exit:
                d = new ExitDialog(this, 0.85);
                d.setFillViewVisibility(false);
                d.setOnRightClickListener(new ExitDialog.onClickListener() {
                    @Override
                    public void click() {
                        postData(null, "/logout", null, App.PostType.SUBMIT);
                        SharedPreferencesData sp = new SharedPreferencesData(CgSetting.this);
                        sp.removeAll();
//                        String user0 = sp.getOtherValue("user0");
//                        SettingUtils.getInstance(App.getInstance()).putIsPatternPwdOn(user0, false);
//                        sp.setBooleanByUser(user0, "isNotFirstIn_pattern", false);
                        SysApplication.getInstance().finishAllActivity();
                        startActivity(new Intent(CgSetting.this, Login.class));
                    }
                });
                d.show();
                break;
            case R.id.layout_update:
                checkUpdate();
                break;
            default:break;
        }
    }

    public void requestFail(String msg, App.PostType postType) {
        super.requestFail(msg, postType);
        ToastMakeText.showToast(this, msg, 1200);
    }

    public void requestSuccess(JSONObject object, App.PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            try {
                JSONObject data = object.getJSONObject("data");
                if (data != null) {
                    //M 运行时权限申请
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    }, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    }
                    if (data.has("appUrl")) {
                        appUrl = data.getString("appUrl");
                        App.setHasUpdate(true);
                    }
                    if (data.has("forceUpdate") && TextUtils.equals("0", data.getString("forceUpdate"))) {

                        if (data.has("updateLog") && !TextUtils.isEmpty(data.getString("updateLog"))) {
                            List<String> list;
                            String updateLog = data.getString("updateLog");
                            String[] array = updateLog.split("；");
                            list = java.util.Arrays.asList(array);
                            updateDialog = new UpdateDialog(this, list);
                            updateDialog.setOnBtn1ClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (updateDialog.getBooleanIgnore())
                                        new SharedPreferencesData(context).setOtherBoolean("IGNORE_UPDATE", true);
                                }
                            });
                            updateDialog.setOnBtn2ClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new UpdateManager(context, appUrl).start();
                                }
                            });
                            updateDialog.setCheckboxVisibility(false);
                            updateDialog.show();
                            updateDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    App.FIRSTCHECKREQUEST = false;
                                }
                            });
                            updateDialog.setversion(data.getString("versionNum"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestAddCgAccount() {
        String path = Config.getHttpConfig() + "/bank/member/realNameWeb";
        final RequestParams params = new RequestParams(path);
        params.addParameter("mobile", sp.getOtherValue("user0"));
        params.addParameter("token", sp.getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        depositDialog = new DepositDialog(context, "加载中...");
        depositDialog.setDuration(300);
        depositDialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() {}
            @Override
            public void doing() {
                x.http().post(params, new Callback.CacheCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try {
                            JSONObject data = new JSONObject(arg0);
                            if (data.has("key") && TextUtils.equals("success", data.getString("key"))) {
                                if (data.has("relationKey"))
                                    startActivityForResult(new Intent(context, WebViewRelease.class).
                                            putExtra("html", data.getString("relationKey"))
                                            .putExtra("title", "渤海存管账户开通"), UserCenter.REQUEST_ADD_CG);
                            } else if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
                                ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                                sp.removeAll();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastMakeText.showToast((Activity)context, data.getString("message"), 1200);
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
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {
                        if (depositDialog != null)
                            depositDialog.hideDialog();
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

    private void checkUpdate() {
        Map<String, Object> map = new HashMap<>();
        PackageManager manager = getPackageManager();
        String version = "";
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        map.put("version", version);
        map.put("appType", "1");
        postData(map, "/more/getVersion", App.PostType.SUBMIT, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==UserCenter.REQUEST_ADD_CG && resultCode==UserCenter.RESULT_ADD_CG){
            //开通存管页返回
            isFromAddCgPage = true;
        }
    }

    @Override
    public void onBackPressed() {
        if(isFromAddCgPage) {
            setResult(UserCenter.RESULT_ADD_CG);
        }
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(d!=null && d.isShowing()){
            d.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ToastMakeText.showToastLongDuration(this, "您拒绝了访问存储空间，可能导致程序出错，请在下一次提示允许该权限", 2000);
                    checkUpdate();
                }
                break;
            default:
                break;
        }
    }
}

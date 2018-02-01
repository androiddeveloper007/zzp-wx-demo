package com.cstz.front;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cstz.MyWidget.DepositDialog;
import com.cstz.common.App;
import com.cstz.common.App.PostType;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.SysApplication;
import com.cstz.common.Web;
import com.cstz.common.WebViewReleaseCg;
import com.cstz.common.view.LuckyDrawDialog;
import com.cstz.common.view.MyDialog;
import com.cstz.common.view.MyDialog.Result;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.activity.InvestRecord;
import com.cstz.cunguan.activity.Recharge;
import com.cstz.cunguan.dialog.InvestVerifyDialog;
import com.cstz.tab.MainActivity;
import com.cstz.tools.Convert;
import com.cstz.tools.MyDIYParcelable;
import com.cstz.tools.ToastMakeText;
import com.ziyeyouhu.library.KeyboardTouchListener;
import com.ziyeyouhu.library.KeyboardUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cstz.front.ProductActivity.RES_INVEST_SUC;

/**
 * 立即投资
 * @author rxh
 */
public class Invest extends MyActivity implements OnClickListener {
    private TextView title_tv;
    private TextView tv_invest_forget_pwd;//忘记支付密码
    private TextView tv_invest_agreement;//投资认购协议
    private ImageView title_back;
    private ScrollView mScrollView;
    private LinearLayout mButton;   //确定投资
    private TextView _interest;      //预计收益
    private TextView _money;        //投资金额
    private EditText _dealpwd;      //支付密码
    private TextView _deadline;        //周期
    private TextView _productTitle;
    private TextView _annualRate;
    private String productId = "";
    private String amount = "";
    private RelativeLayout layout_hobao;
    private TextView textview_hongbao;
    private LinearLayout linearlayout_hongbao;
    private TextView tv_invest_keyboard_introduce;
    private EditText et_invest_keyboard;
    private RelativeLayout rl_keyboard;
    private LinearLayout ll_keyboard_result;
    private ImageView iv_keyboard_result;
    private TextView tv_invest_keyboard_result;


    public List<Map<String, Object>> _redPacketList = new ArrayList<Map<String, Object>>();
    private String _redpacketCode = "";
    private String _redpacketWrap = "";

    private CheckBox type;
    private int hongbao = 0;
    private int submit_type = 0;//提交类型，0是投资，1是充值,2是是否获得抽奖
    private static Handler handler = new Handler();
    int selected_position = -1;
    private LinearLayout root_view;
    private KeyboardUtil keyboardUtil;
    private DepositDialog loadDialog;
    private Context context;
    private SharedPreferencesData sp;
    private InvestVerifyDialog d;
    private int type_1 = 0;//请求类型： 0投资成功的数据操作  5抽奖的数据操作
    private boolean keyCodeStatus=false;//K码合法性，默认false
    private String keyCodeSuccess;//合法时的k码，临时变量
    private Animation mShakeAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.front_invest);
        setTransparentStatusBar(this,false);
        context = this;
        sp = new SharedPreferencesData(this);
        initView();
        initData();
    }

    public void initView() {
        title_tv = (TextView) findViewById(R.id.title_tv);
        tv_invest_forget_pwd = (TextView) findViewById(R.id.tv_invest_forget_pwd);
        tv_invest_agreement = (TextView) findViewById(R.id.tv_invest_agreement);
        title_tv.setText("立即投资");
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);

        mScrollView = (ScrollView) findViewById(R.id.front_invest_scrollview);
        mButton = (LinearLayout) findViewById(R.id.invest_button);
        _money = (TextView) findViewById(R.id.front_invest_money);
        _productTitle = (TextView) findViewById(R.id.textView_title);
        _dealpwd = (EditText) findViewById(R.id.front_invest_dealpwd);
        _interest = (TextView) findViewById(R.id.textView_yjsy);
        _annualRate = (TextView) findViewById(R.id.textView_annualRate);
        layout_hobao = (RelativeLayout) findViewById(R.id.layout_hobao);
        textview_hongbao = (TextView) findViewById(R.id.textview_hongbao);
        _deadline = (TextView) findViewById(R.id.textView_deadline);
        linearlayout_hongbao = (LinearLayout) findViewById(R.id.linearlayout_hongbao);
        type = (CheckBox) findViewById(R.id.checkBox_type);
        type.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setButtonStyle(isChecked);
            }
        });

        //K码各控件
        tv_invest_keyboard_introduce = (TextView) findViewById(R.id.tv_invest_keyboard_introduce);
        et_invest_keyboard = (EditText) findViewById(R.id.et_invest_keyboard);
        rl_keyboard = (RelativeLayout) findViewById(R.id.rl_keyboard);
        ll_keyboard_result = (LinearLayout) findViewById(R.id.ll_keyboard_result);
        iv_keyboard_result = (ImageView) findViewById(R.id.iv_keyboard_result);
        tv_invest_keyboard_result = (TextView) findViewById(R.id.tv_invest_keyboard_result);
        et_invest_keyboard.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (temp.length() == 10) {
                    if(!TextUtils.isEmpty(et_invest_keyboard.getText()))
                        checkKCode(et_invest_keyboard.getText().toString());
                    hideSoftKeyboard();
                }else if(temp.length()<10){
                    ll_keyboard_result.setVisibility(View.GONE);
                    keyCodeStatus=false;
                }
            }
        });

        listener();
        setButtonStyle(false);

        root_view = (LinearLayout) findViewById(R.id.root_view);
        initMoveKeyBoard();
    }

    private DepositDialog depositDialog;
    /*检查K码合法性*/
    public void checkKCode(final String code) {
        String path = Config.getHttpConfig() + "/product/checkKeyCode";
        final RequestParams params = new RequestParams(path);
        params.addParameter("keyCode", code);
        params.addParameter("investAmount", amount);
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        if(depositDialog==null)
            depositDialog = new DepositDialog(Invest.this, "检验K码中...");
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
                            if (data.has("key") && TextUtils.equals("success", data.getString("key"))) {//K码验证通过
                                //设置提示图片和文字，且设置它们可见
                                iv_keyboard_result.setImageResource(R.drawable.icon_keyboard_result);
                                tv_invest_keyboard_result.setText(data.getString("message"));
                                ll_keyboard_result.setVisibility(View.VISIBLE);
                                keyCodeStatus = true;
                                keyCodeSuccess = code;
                            } else {
                                iv_keyboard_result.setImageResource(R.drawable.icon_keyboard_result1);
                                tv_invest_keyboard_result.setText(data.getString("message"));
                                tv_invest_keyboard_result.setTextColor(getResources().getColor(R.color.navigation_cg));
                                ll_keyboard_result.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastMakeText.showToast(Invest.this, "服务器连接失败", 1000);
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {}
                    @Override
                    public void onFinished() {
                        if(depositDialog!=null&&depositDialog.isShowing())
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

    private void initMoveKeyBoard() {
        keyboardUtil = new KeyboardUtil(this, root_view, mScrollView);
        keyboardUtil.setKeyBoardStateChangeListener(new KeyBoardStateListener());
        _dealpwd.setOnTouchListener(new KeyboardTouchListener(keyboardUtil, KeyboardUtil.INPUTTYPE_ABC, -1));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (keyboardUtil.isShow) {
                keyboardUtil.hideSystemKeyBoard();
                keyboardUtil.hideAllKeyBoard();
                keyboardUtil.hideKeyboardLayout();
            } else {
                return super.onKeyDown(keyCode, event);
            }
            return false;
        } else
            return super.onKeyDown(keyCode, event);
    }

    public void listener() {
        title_back.setOnClickListener(this);
        mButton.setOnClickListener(this);
        layout_hobao.setOnClickListener(this);
        tv_invest_forget_pwd.setOnClickListener(this);
        tv_invest_keyboard_introduce.setOnClickListener(this);
        tv_invest_agreement.setOnClickListener(this);
    }

    private void initData() {
        SharedPreferencesData data = new SharedPreferencesData(Invest.this);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras().getString("productId") != null) {
            productId = intent.getStringExtra("productId");
        }

        if (intent != null && intent.getExtras().getString("amount") != null) {
            amount = intent.getStringExtra("amount");
        }
        //投资金额长度超过7位时，将投资金额、预计收益字符缩小
        int length = amount.length();
        if (length > 6) {
            _money.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dimen_13));
            _interest.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dimen_13));
        } else if (length > 9) {
            _money.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dimen_10));
            _interest.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dimen_10));
        }

        _money.setText(amount);

        Map<String, Object> map = new HashMap<>();
        try {
            map.put("productId", productId);
            map.put("token", data.getValue("token"));
            map.put("amount", amount);
            postData(map, "/product/order", PostType.LOAD, true);//根据产品id和金额生成支付信息 //true investInit
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestConnectionFail(PostType postType) {
        super.requestConnectionFail(postType);

        this.finish();
    }

    public void requestFail(String msg, PostType postType) {
        super.requestFail(msg, postType);
        if (postType == PostType.SUBMIT) {
            setButtonStyle(true);
            ToastMakeText.showToast(this, msg.replaceAll("<([^>]*)>", ""), 1200);
        } else if (postType == PostType.LOAD) {
            ToastMakeText.showToast(this, msg, 1000);
            this.finish();
        }
    }

    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            try {
                if (postType == PostType.LOAD) {
                    mScrollView.setVisibility(View.VISIBLE);
                    setButtonStyle(true);

                    JSONObject data = object.getJSONObject("data");
                    if (data != null)//加载投资页面的数据并显示 && TextUtils.isEmpty(bankMobilePhone)
                    {
                        if(type_1 == 0) {
                            DecimalFormat df = new DecimalFormat("0.00");
                            final float award = Convert.strToFloat(data.getString("award"), 0);
                            final float annualRate = Convert.strToFloat(data.getString("annualRate"), 0);
                            if (award > 0) {
                                _annualRate.setText(df.format(award + annualRate) + "%");
                            } else {
                                _annualRate.setText(df.format(annualRate) + "%");
                            }
                            _productTitle.setText(data.getString("productTitle"));
                            _interest.setText(data.getString("interest"));
                            _deadline.setText(data.getString("deadline") + "天");

                            if (data.getString("redpacketcount") != null && !TextUtils.isEmpty(data.getString("redpacketcount")))
                                hongbao = Convert.strToInt(data.getString("redpacketcount"), 0);

                            if (hongbao > 0) {
                                linearlayout_hongbao.setVisibility(View.VISIBLE);
                                textview_hongbao.setText("有" + hongbao + "个红包可用");

                                JSONArray list = object.getJSONArray("data2");
                                for (int i = 0; i < list.length(); i++) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    JSONObject image = list.getJSONObject(i);
                                    map.put("code", image.getString("redpacketCode"));
                                    map.put("redwrap", image.getString("redwrap"));
                                    map.put("redproportion", image.getString("redproportion"));
                                    _redPacketList.add(map);
                                }
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("code", "");
                                map.put("redwrap", "");
                                map.put("redproportion", "");
                                _redPacketList.add(map);
                                //判断后台参数，默认是否选择红包
                                if(_redPacketList.size() > 0){
                                    //nodefault=1 就不默认选中第一个红包。
                                    if(data.has("nodefault") && TextUtils.equals("1",data.getString("nodefault"))){

                                    } else{
                                        _redpacketWrap = _redPacketList.get(0).get("redwrap").toString();
                                        String str = "已选择" + _redpacketWrap + "元红包";
                                        SpannableStringBuilder style = new SpannableStringBuilder(str);
                                        style.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.product_title)), 3, 4 + _redpacketWrap.length(),
                                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        textview_hongbao.setText(style);
                                        selected_position = 0;
                                        _redpacketCode = _redPacketList.get(0).get("code").toString();
                                    }
                                }
                            } else {
                                linearlayout_hongbao.setVisibility(View.VISIBLE);
                                textview_hongbao.setText("暂无红包可用");
                            }
                            //判断 keyBoxStatus 字段，如果为true，则显示k码区域，反之隐藏
                            String keyBoxStatus = data.getString("keyBoxStatus");
                            if (TextUtils.equals("true", keyBoxStatus)) {
                                rl_keyboard.setVisibility(View.VISIBLE);
                            }
                        } else if( type_1 == 5) {//交易完成点通知对话框中大转盘，进入抽奖url
                            String result = data.getString("r_url");
                            Intent intent = new Intent();
                            intent.setClass(Invest.this, Web.class);
                            intent.putExtra("title", "幸运抽奖");
                            intent.putExtra("url", result);
                            startActivity(intent);
                            Invest.this.finish();//关闭投资页
                        }
                    }
                } else if (postType == PostType.SUBMIT) {
                    //点击立即投资的操作
                    //后台判断余额不足，点击充值的成功操作
                    if (submit_type == 1) {
                        JSONObject data = object.getJSONObject("data");
                        if (data != null) {
                            String result = data.getString("result");
                            Intent intent = new Intent();
                            if (result.equals("-1")) {
//                                intent.putExtra("step", data.getString("step"));
//                                intent.setClass(Invest.this, BankCardAdd.class);
                            } else {
                                int cardStatus = Convert.strToInt(data.getString("cardStatus"), 1);
                                if (cardStatus == 1) {
                                    intent.putExtra("bankName", data.getString("bankName"));
                                    intent.putExtra("cardNo", data.getString("cardNo"));
                                    intent.putExtra("isInvest", true);
                                    intent.setClass(Invest.this, Recharge.class);//充值
                                } else {
//                                    intent.putExtra("bankMobilePhone", data.getString("bankMobilePhone"));
//                                    intent.putExtra("cardNo", data.getString("cardNo"));
//                                    intent.putExtra("realName", data.getString("realName"));
//                                    intent.putExtra("idNo", data.getString("idNo"));
//                                    intent.putExtra("step", data.getString("step"));
//                                    intent.setClass(Invest.this, EditorBankCard.class);
                                }
                            }
                            startActivity(intent);
                        }
                    } else if (submit_type == 0) {

                        postData(null, "/activity/prizedraw/getUserAddPrizeNumber", PostType.SUBMIT, false);
                        submit_type = 2;

                    } else if(submit_type==2) {
                        String msg="";
                        if(object.has("data")){
                            msg = object.getString("data");
                        }
                        int count = 0;
                        if(isNumeric(msg))
                            count = Integer.parseInt(msg);
                        if (count>0) {
                            //提示进入抽奖页
                            final LuckyDrawDialog d = new LuckyDrawDialog(Invest.this, true,0.8);
                            String str = "投资成功！恭喜您已获得【" + msg + "】次抽奖机";
                            d.tvMsg_daojishi.setText("个人中心");
                            d.setBtn1Text("知道了");
                            d.setBtn2Gone();
                            d.setTitleVisible(true,"提示信息");
                            d.tvMsg_daojishi.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //跳到个人中心
                                    Intent i = new Intent(Invest.this, MainActivity.class);
                                    i.putExtra("home", "");
                                    Invest.this.startActivity(i);
                                    // 清空Activity栈
                                    SysApplication.getInstance().finishAllActivity();
                                }
                            });
                            d.setMessage(str);//style
                            d.show();
                            d.setOnBtn1ClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Invest.this, InvestRecord.class);
                                    Invest.this.startActivity(intent);
                                    Invest.this.finish();
                                }
                            });
                            d.setOnRunnablefinListener(new LuckyDrawDialog.runnablefinished() {//点击系统返回键
                                @Override
                                public void runnablefin() {
                                    Intent intent = new Intent(Invest.this, InvestRecord.class);
                                    Invest.this.startActivity(intent);
                                    Invest.this.finish();
                                }
                            });
                        } else { // 提交成功，请求后台未获得抽奖，显示投资成功
                            MyDialog dialog = new MyDialog(Invest.this, "投资成功", Result.SUCCESS);
                            dialog.setMyCallback(new MyCallback() {
                                @Override
                                public void callback() {
                                    Intent intent = new Intent(Invest.this, InvestRecord.class);
                                    Invest.this.startActivity(intent);
                                    Invest.this.finish();
                                }
                                @Override
                                public void doing() {
                                }
                            });
                            dialog.showDialog();
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void requestError() {
//		setButtonStyle(true);
        super.requestError();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_back) {
            this.finish();
        } else if (v.getId() == R.id.invest_button) {
            if (ToastMakeText.isFastClick()) return;
            if(!keyCodeStatus && !TextUtils.isEmpty(et_invest_keyboard.getText())){
                if(mShakeAnim==null)
                    mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
                if(et_invest_keyboard.getText().length()<10) {
                    iv_keyboard_result.setImageResource(R.drawable.icon_keyboard_result1);
                    tv_invest_keyboard_result.setText("无效K码!");
                    tv_invest_keyboard_result.setTextColor(getResources().getColor(R.color.navigation_cg));
                    ll_keyboard_result.setVisibility(View.VISIBLE);
                }else{

                }
                ll_keyboard_result.startAnimation(mShakeAnim);
                return;
            }
            d = new InvestVerifyDialog(this,0.85);
            d.setOnRightClickListener(new InvestVerifyDialog.onClickListener() {
                @Override
                public void click() {
                    if(!d.tvInvestVerifyGetCode.hasSend) {showToast("未获取验证码");return;}
                    requestInvest();
                }
            });
            d.setOnCodeClickListener(new InvestVerifyDialog.onClickListener1() {
                @Override
                public void click() {
                    d.tvInvestVerifyGetCode.hasSend=true;
                    requestInvestCode();//申请动态口令
                }
            });
            d.show();
            /*String dealpwd = Convert.trim(_dealpwd.getText().toString());
            if (TextUtils.isEmpty(dealpwd)) {
                ToastMakeText.showToast(this, "请输入支付密码", 1200);
                return;
            }*/

            /*setButtonStyle(false);

            SharedPreferencesData data = new SharedPreferencesData(Invest.this);
            Map<String, Object> map = new HashMap<>();
            map.put("token", data.getValue("token"));
            map.put("productId", productId);
            map.put("redpacketCode", _redpacketCode);
            map.put("investAmount", amount);
//            map.put("dealpwd", dealpwd);
            map.put("investMode", 3);//移动端，（3 android 4 iOS）
            submit_type = 0;
            postData(map, "/product/invest", PostType.SUBMIT, true);*/

        } else if (v.getId() == R.id.layout_hobao) {
            if (ToastMakeText.isFastClick()) {
                return;
            }

            if (hongbao > 0) {

                Intent intent = new Intent();

                intent.setClass(Invest.this, product_hongbao.class);

                ArrayList<List<Map<String, Object>>> bundlelist = new ArrayList<List<Map<String, Object>>>();

                bundlelist.add(_redPacketList);

                MyDIYParcelable parcelable = new MyDIYParcelable();

                parcelable.bundlelist = bundlelist;

                intent.putExtra("list", parcelable);

                intent.putExtra("selectIndex", selected_position);
                startActivityForResult(intent, 150);
            }
        }else if(v.getId()==R.id.tv_invest_agreement){
            Intent i = new Intent(this, WebViewReleaseCg.class);
            i.putExtra("url", Config.getHttpConfig() + "/view/invest_agreement.html");
            i.putExtra("title", "项目认购协议");
            startActivity(i);
        }else if (v.getId() == R.id.tv_invest_keyboard_introduce) {//k码活动介绍页
            Intent intent = new Intent();
            intent.setClass(Invest.this, WebViewReleaseCg.class);
            intent.putExtra("title", "了解什么是K码");
            intent.putExtra("url", Config.getHttpConfig() + "/view/keyboard/index.html");
            startActivity(intent);
        }
    }

    private void requestInvest() {
        if(TextUtils.isEmpty(d.etInvestVerify.getText().toString())){ToastMakeText.showToast((Activity) context,"请输入验证码",2000);return;}
        String path = Config.getHttpConfig() + "/bank/member/invest";
        final RequestParams params = new RequestParams(path);
        params.addParameter("token", sp.getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.addParameter("smsCode", d.etInvestVerify.getText().toString());
        params.addParameter("productId", productId);
        params.addParameter("investAmount", amount);
        params.addParameter("redpacketCode", _redpacketCode);
        params.addParameter("depositAccount", sp.getValue("depositAccount"));
        params.addParameter("investMode", "3");//1:电脑 2:微信 3:安卓手机 4:苹果手机
        params.setConnectTimeout(30 * 1000);
        if(keyCodeStatus && TextUtils.equals(keyCodeSuccess,et_invest_keyboard.getText()))
            params.addParameter("keyCode",keyCodeSuccess);//键盘K码
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
                            if(data.has("key") && TextUtils.equals("success",data.getString("key"))){
                                ToastMakeText.showToast((Activity) context,data.getString("message"),3000);
                                d.dismiss();
                                d.tvInvestVerifyGetCode.resetCount();
                                Intent intent = new Intent(context,InvestRecord.class);
                                intent.putExtra("uCenter","2");
                                if(TextUtils.equals("-1", App.oldAccountCloseDay))
                                    intent.putExtra("oldAccountCloseDay",App.oldAccountCloseDay);
                                startActivity(intent);
                                setResult(RES_INVEST_SUC);
                                finish();
                            }else if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
                                ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                                sp.removeAll();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                                finish();
                            } else if(data.has("message")) {
                                ToastMakeText.showToast((Activity) context,data.getString("message"),3000);
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

    private void requestInvestCode() {
        String path = Config.getHttpConfig() + "/bank/member/getdepotBankSmsCode";
        final RequestParams params = new RequestParams(path);
        params.addParameter("mobile", sp.getOtherValue("user0"));
        params.addParameter("token", sp.getValue("token"));
        params.addParameter("depositAccount", sp.getValue("depositAccount"));
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
                            if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
                                ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                                sp.removeAll();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                            if(data.has("key") && TextUtils.equals("success",data.getString("key")))
                                d.tvInvestVerifyGetCode.startSend();//请求到短信验证码后，开始倒计时
                            if (data.has("message") && TextUtils.equals("error",data.getString("key"))) {
                                ToastMakeText.showToast((Activity) context,data.getString("message"),2000);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode > 0) {
            switch (resultCode) {
                case 150:
                    _redpacketWrap = data.getExtras().getString("redwrap");
                    _redpacketCode = data.getExtras().getString("code");
                    if (TextUtils.isEmpty(_redpacketWrap)) {
                        _redpacketCode = "";
                        textview_hongbao.setText("有" + hongbao + "个红包可用");
                    } else {
                        String str = "已选择" + _redpacketWrap + "元红包";
                        SpannableStringBuilder style = new SpannableStringBuilder(str);
                        style.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.product_title)), 3, 4 + _redpacketWrap.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        textview_hongbao.setText(style);
                    }
                    selected_position = data.getExtras().getInt("selectedIndex");
                    break;
                default:
                    break;
            }
        }
    }

    private void setButtonStyle(boolean enable) {
        if (enable) {
            mButton.setBackgroundResource(R.drawable.button_normal);
            mButton.setClickable(true);

        } else {
            mButton.setBackgroundResource(R.drawable.button_disable);
            mButton.setClickable(false);
        }
    }

    class KeyBoardStateListener implements KeyboardUtil.KeyBoardStateChangeListener {
        @Override
        public void KeyBoardStateChange(int state, EditText editText) {
            if (state == 1) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                        _dealpwd.requestFocus();
                    }
                }, 50);
            } else if (state == 2) {

            }
        }
    }

    public final static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim()))
            return s.matches("^[0-9]*$");
        else
            return false;
    }
}
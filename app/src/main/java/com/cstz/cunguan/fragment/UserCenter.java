package com.cstz.cunguan.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.MyWidget.DepositDialog;
import com.cstz.common.App;
import com.cstz.common.App.PostType;
import com.cstz.common.Config;
import com.cstz.common.MyCallback;
import com.cstz.common.MyFragment;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.WebViewRelease;
import com.cstz.common.WebViewReleaseCg;
import com.cstz.common.view.RiskEvalutionDialog;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.activity.BorrowerFundDetail;
import com.cstz.cunguan.activity.BorrowerFundDetailOfPublic;
import com.cstz.cunguan.activity.CgSetting;
import com.cstz.cunguan.activity.FundDetail;
import com.cstz.cunguan.activity.InvestRecord;
import com.cstz.cunguan.activity.MessageCenter;
import com.cstz.cunguan.activity.RechargeAccount;
import com.cstz.cunguan.activity.RedPacket;
import com.cstz.cunguan.activity.Repayment;
import com.cstz.cunguan.activity.Transport;
import com.cstz.cunguan.activity.WithDraw;
import com.cstz.cunguan.dialog.CunGuanMailDialog;
import com.cstz.cunguan.dialog.CunGuanWarningDialog;
import com.cstz.cunguan.dialog.ExitDialog;
import com.cstz.cunguan.dialog.PayPasswordDialog;
import com.cstz.cunguan.dialog.TipOpenCgDialog;
import com.cstz.front.InviteReward;
import com.cstz.front.Login;
import com.cstz.model.User;
import com.cstz.tools.Convert;
import com.cstz.tools.StatusBarUtil;
import com.cstz.tools.ToastMakeText;
import com.cstz.usercenter.TiXian;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGAViewPager;
import library.widget.NumberAnimTextView;

/**
 * 存管个人中心
 */
public class UserCenter extends MyFragment implements BGAViewPager.AutoPlayDelegate {

    @BindView(R.id.tv_user_name)
    TextView tvUserPhone;
    @BindView(R.id.tv_usercenter_money)
    NumberAnimTextView tvUsercenterMoney;
    @BindView(R.id.tv_usercenter_usable_money)
    NumberAnimTextView tvUsercenterUsableMoney;
    @BindView(R.id.tv_money_has_interest)
    NumberAnimTextView tv_money_has_interest;
    @BindView(R.id.tv_money_interest_future)
    NumberAnimTextView tv_money_interest_future;
    @BindView(R.id.tv_cunguan_status)
    TextView tvCunguanStatus;
    @BindView(R.id.tv_activeTitle)
    TextView tv_activeTitle;
    @BindView(R.id.iv_notice_icon)
    ImageView iv_notice_icon;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.vp_borrower)
    BGAViewPager vpBorrower;
    @BindView(R.id.fl_vp)
    FrameLayout flVp;
    @BindView(R.id.btn_recharge)
    Button btn_recharge;
    @BindView(R.id.btn_tixian)
    Button btn_tixian;
    @BindView(R.id.btn_transport)
    Button btn_transport;
    @BindView(R.id.rl_ucenter_tab_cg)
    LinearLayout rl_ucenter_tab_cg;
    @BindView(R.id.line_ucenter_tab_cg)
    View line_ucenter_tab_cg;
    @BindView(R.id.ll_cg_tip)
    LinearLayout ll_cg_tip;
    @BindView(R.id.rl_ucenter_tab_hongbao)
    LinearLayout rlUcenterTabHongbao;
    @BindView(R.id.line_ucenter_tab_hongbao)
    View lineUcenterTabHongbao;
    @BindView(R.id.rl_ucenter_tab_hk)
    LinearLayout rlUcenterTabHk;
    @BindView(R.id.line_ucenter_tab_hk)
    View lineUcenterTabHk;
    @BindView(R.id.rl_ucenter_invest_record)
    LinearLayout rl_ucenter_invest_record;
    @BindView(R.id.line_ucenter_tab_zjmx)
    View line_ucenter_tab_zjmx;
    @BindView(R.id.line_ucenter_tab_invest_record)
    View line_ucenter_tab_invest_record;
    @BindView(R.id.rl_ucenter_tab_zuixinhuodong)
    LinearLayout rlUcenterTabZuixinhuodong;
    @BindView(R.id.line_ucenter_tab_zuixinhuodong)
    View lineUcenterTabZuixinhuodong;
    @BindView(R.id.rl_ucenter_tab_invite)
    LinearLayout rlUcenterTabInvite;
    @BindView(R.id.line_ucenter_tab_invite)
    View lineUcenterTabInvite;
    @BindView(R.id.rl_ucenter_tab_recharge_account)
    LinearLayout rlUcenterTabRechargeAccount;
    @BindView(R.id.line_ucenter_tab_recharge_account)
    View lineUcenterTabRechargeAccount;
    @BindView(R.id.iv_isRedpacketUnRead)
    ImageView ivIsRedpacketUnRead;
    @BindView(R.id.user_center_switch_deposit)
    ImageView user_center_switch_deposit;
    @BindView(R.id.tv_usercenter0)
    TextView tv_usercenter0;
    @BindView(R.id.tv_usercenter_1)
    TextView tv_usercenter_1;
    @BindView(R.id.tv_usercenter_2)
    TextView tv_usercenter_2;
    @BindView(R.id.tv_usercenter1)
    TextView tv_usercenter1;
    @BindView(R.id.tv_cg_tip)
    TextView tv_cg_tip;
    @BindView(R.id.view_cg_tip_borrower)
    View view_cg_tip_borrower;
    @BindView(R.id.tv_usercenter_borrower)
    TextView tv_usercenter_borrower;
    @BindView(R.id.line_borrower)
    View line_borrower;
    @BindView(R.id.tv_usercenter_borrower_amount)
    NumberAnimTextView tv_usercenter_borrower_amount;

    private SharedPreferencesData sp;
    private Activity context;
    private int type = 0;
    private List<View> viewlist;
    //    private List<View> mHackyViews;
    private String userType = "0";//用户类型，-1代表借款人
    private String depositCheck = "0";//是否已开通存管
    private String active_url = "";
    private String active_title = "最新活动";
    private String isRedpacketUnRead = "";//0：新红包，1：没有新红包
    private DepositDialog depositDialog;
    private int mPageScrollPosition;
    private float mPageScrollPositionOffset;
    private static final int VEL_THRESHOLD = 400;
    private String uCenter = "1";//1:原账户 2：存管账户 3：借款人
    private String regAsDeposit;//1：存管上线后注册的用户，0：之前注册的用户
    private String usableAmount = "0";
    private String accountSum;
    private String hasRePayInterest;
    private String forPayInterest;
    boolean isNotInit = false;
    private String oldAccountCloseDay="";//-1代表老用户已经关闭
    private String cardStatus;
    private String cg_usableAmount;
    private String isReadMail;
    public static final int REQUEST_ADD_CG = 0x00000010;
    public static final int REQUEST_SETTING = 0x00000100;
    public static final int RESULT_ADD_CG = 0x00000011;
    //    private boolean createCgAccount;
    private String oldAccountAccountSum = "1";//原账户总金额
    private String borrowerType;//借款人类型 1:对私，2：对公
    private String borrowerAccountType;//借款人账号类型  1:非渤海，2：渤海, 其他：不用理会
    private String sumRepayedAmout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        sp = new SharedPreferencesData(context);
        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            userType = bundle.getString("userType");
            regAsDeposit = bundle.getString("regAsDeposit");
            depositCheck = bundle.getString("depositCheck");
            App.depositCheck = depositCheck;
            if (TextUtils.equals("-1", userType)) {
                uCenter = "3";
            } else if (TextUtils.equals("1", depositCheck) || TextUtils.equals("1", regAsDeposit)) {//如果已经开通存管，默认状态为存管账户
                uCenter = "2";
            }
        } else {
            userType = App.userType;
            regAsDeposit = App.regAsDeposit;
            depositCheck = App.depositCheck;
            if (TextUtils.equals("-1", userType)) {
                uCenter = "3";
            } else if (TextUtils.equals("1", depositCheck) || TextUtils.equals("1", regAsDeposit)) {
                uCenter = "2";
            }
        }
        if (sp.getBoolean("OpenCgCacheData0")
                && !TextUtils.equals("1", regAsDeposit) && !TextUtils.equals("-1", userType)) {
            sp.setBoolean("OpenCgCacheData0", false);
            addGuideLayoutOne(R.layout.guideview_1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View meLayout = inflater.inflate(R.layout.aa_fg_usercenter, container, false);
        if(Build.VERSION.SDK_INT > 22) {
            meLayout.setPadding(0, getStatusBarHeight(getActivity()), 0, 0);
            StatusBarUtil.setColor(getActivity(), getResources().getColor(R.color.ucenter_background), 0);
            resetFragmentView(this);
        }
        ButterKnife.bind(this, meLayout);
        initView();
        if (TextUtils.isEmpty(sp.getValue("token"))) {
            startActivity(new Intent(getActivity(), Login.class));
        }else{
            initData(false);
        }
        return meLayout;
    }

    public void resetFragmentView(Fragment fragment) {
        View contentView = getActivity().findViewById(android.R.id.content);
        if (contentView != null) {
            ViewGroup rootView;
            rootView = (ViewGroup) ((ViewGroup) contentView).getChildAt(0);
            if (rootView.getPaddingTop() != 0) {
                rootView.setPadding(0, 0, 0, 0);
            }
        }
        if (fragment.getView() != null)
            fragment.getView().setPadding(0, getStatusBarHeight(getActivity()), 0, 0);
    }

    private void initView() {//根据三种登录类型，初始化个人中心布局
//        initAccountState();
//        refreshLayout.setReboundDuration(1000);//设置回弹动画时长
        refreshLayout.setDisableContentWhenRefresh(true);//是否开启在刷新时候禁止操作内容视图
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
            }
        });
    }

    @OnClick({R.id.ll_message, R.id.ll_setting, R.id.btn_recharge, R.id.btn_tixian,
            R.id.rl_ucenter_tab_cg, R.id.rl_ucenter_tab_hongbao, R.id.rl_ucenter_tab_hk, R.id.rl_ucenter_invest_record,
            R.id.rl_ucenter_tab_zjmx, R.id.rl_ucenter_tab_invite, R.id.rl_ucenter_tab_zuixinhuodong,
            R.id.iv_vp_left, R.id.iv_vp_right, R.id.user_center_switch_deposit, R.id.btn_transport,R.id.rl_ucenter_tab_recharge_account})
    public void onClick(View view) {
        if (TextUtils.isEmpty(sp.getValue("token"))) {
            startActivity(new Intent(getActivity(), Login.class));
            return;
        }
        switch (view.getId()) {
            case R.id.ll_message:
                Intent i0 = new Intent(context, MessageCenter.class).putExtra("uCenter", uCenter);
                if (!TextUtils.equals("0", isReadMail)) i0.putExtra("isReadMail", isReadMail);
                startActivityForResult(i0, RedPacket.REQUEST_RED_PACKET);
                break;
            case R.id.ll_setting:
                startActivityForResult(new Intent(context, CgSetting.class).putExtra("uCenter", uCenter), REQUEST_SETTING);
                break;
            case R.id.btn_recharge:
                if (!TextUtils.equals("-1", userType) && !TextUtils.equals("1", regAsDeposit) &&
                        TextUtils.equals("1", depositCheck) && Double.parseDouble(usableAmount) > 0 && TextUtils.equals("1", uCenter))
                    startActivityForResult(new Intent(context, com.cstz.cunguan.activity.Transport.class)
                            .putExtra("usableAmount", usableAmount)
                            .putExtra("cg_usableAmount", cg_usableAmount), Transport.REQUEST_TRANSPORT_SUC);
                else {
                    if (TextUtils.equals("1", depositCheck))
                        startActivity(new Intent(context, com.cstz.cunguan.activity.Recharge.class));
                    else {
                        if (!TextUtils.equals("1", regAsDeposit) && !TextUtils.equals("1", depositCheck)) {//老用户未开通村官
                            final TipOpenCgDialog d = new TipOpenCgDialog(context, 0.9);
                            d.setText("尊敬的用户，为了保障您资金的安全，请您开通存管账户，原账户充值、" +
                                    "提现、投资业务已暂停，开通完成后在存管账户中操作，谢谢配合！");
                            d.setTextSize(R.dimen.dimen_14);
                            d.setLeftBtnText("稍后再说");
                            d.setRightBtnText("马上开通");
                            d.setOnRightClickListener(new TipOpenCgDialog.onClickListener() {
                                @Override
                                public void click() {
                                    requestAddCgAccount();
                                    d.dismiss();
                                }
                            });
                            d.show();
                        } else {
                            final CunGuanWarningDialog d = new CunGuanWarningDialog(context, 0.90);
                            d.hideBtn1();
                            d.setStyleInvest(false);
                            d.setOnApplyClickListener(new CunGuanWarningDialog.onApplayButtonClick() {
                                @Override
                                public void applyClick() {
                                    requestAddCgAccount();
                                    d.dismiss();
                                }
                            });
                            d.show();
                        }
                    }
                }
                break;
            case R.id.btn_tixian:
                if (TextUtils.equals("-1", userType)) {
                    if (TextUtils.equals("1", depositCheck)) {
                        startActivity(new Intent(context, WithDraw.class));
                    } else {
                        final CunGuanWarningDialog d = new CunGuanWarningDialog(context, 0.90);
                        d.hideBtn1();
                        d.setOnApplyClickListener(new CunGuanWarningDialog.onApplayButtonClick() {
                            @Override
                            public void applyClick() {
                                requestAddCgAccount();
                                d.dismiss();
                            }
                        });
                        d.show();
                    }
                    return;
                }
                //原账户
                if (TextUtils.equals("1", uCenter)) {
                    //老用户
                    if (!TextUtils.equals("1", regAsDeposit)) {
                        if (TextUtils.equals("1", depositCheck)) {
                            startActivity(new Intent(context, WithDraw.class));
                        } else {
                            final TipOpenCgDialog d = new TipOpenCgDialog(context, 0.9);
                            d.setText("尊敬的用户，为了保障您资金的安全，请您开通存管账户，原账户充值、" +
                                    "提现、投资业务已暂停，开通完成后在存管账户中操作，谢谢配合！");
                            d.setTextSize(R.dimen.dimen_14);
                            d.setLeftBtnText("稍后再说");
                            d.setRightBtnText("马上开通");
                            d.setOnRightClickListener(new TipOpenCgDialog.onClickListener() {
                                @Override
                                public void click() {
                                    requestAddCgAccount();
                                    d.dismiss();
                                }
                            });
                            d.show();
                        }
                    }
                } else if (TextUtils.equals("2", uCenter)) {//存管账户
                    if (TextUtils.equals("1", depositCheck)) {
                        startActivity(new Intent(context, WithDraw.class));
                    } else {
                        //新用户
                        final CunGuanWarningDialog d = new CunGuanWarningDialog(context, 0.90);
                        d.hideBtn1();
                        d.setStyleInvest(false);
                        d.setOnApplyClickListener(new CunGuanWarningDialog.onApplayButtonClick() {
                            @Override
                            public void applyClick() {
                                requestAddCgAccount();
                                d.dismiss();
                            }
                        });
                        d.show();
                    }
                }
                break;
            case R.id.btn_transport:
                startActivityForResult(new Intent(context, com.cstz.cunguan.activity.Transport.class)
                        .putExtra("usableAmount", usableAmount)
                        .putExtra("cg_usableAmount", cg_usableAmount), Transport.REQUEST_TRANSPORT_SUC);
                break;
            case R.id.rl_ucenter_tab_cg:
                requestAddCgAccount();
                break;
            case R.id.rl_ucenter_tab_hongbao:
                Intent i = new Intent(context, RedPacket.class);
                if (TextUtils.equals("0", isRedpacketUnRead))
                    i.putExtra("isRedpacketUnRead", isRedpacketUnRead);
                    i.putExtra("uCenter", uCenter);
                startActivityForResult(i, RedPacket.REQUEST_RED_PACKET);
                break;
            case R.id.rl_ucenter_tab_hk:
                startActivityForResult(new Intent(context, Repayment.class), REQUEST_SETTING);
                break;
            case R.id.rl_ucenter_invest_record:
                Intent intent = new Intent(context, InvestRecord.class);
//                intent.putExtra("deposit", depositCheck);
                intent.putExtra("uCenter", uCenter);//1.原账户，2.存管账户
//                intent.putExtra("regAsDeposit", regAsDeposit);//1.新注册
                if (TextUtils.equals("-1", oldAccountCloseDay))
                    intent.putExtra("oldAccountCloseDay", oldAccountCloseDay);//-1.代表原账户已关闭
                startActivity(intent);
                break;
            case R.id.rl_ucenter_tab_zjmx:
                if (TextUtils.equals("-1", userType))
                    if(TextUtils.equals("2",borrowerType))
                        startActivity(new Intent(context, BorrowerFundDetailOfPublic.class));
                    else
                        startActivity(new Intent(context, BorrowerFundDetail.class));
                else {
                    Intent intent1 = new Intent(context, FundDetail.class);
                    intent1.putExtra("deposit", depositCheck)
                            .putExtra("uCenter", uCenter)
                            .putExtra("regAsDeposit", regAsDeposit);
                    if (TextUtils.equals("-1", oldAccountCloseDay))
                        intent1.putExtra("oldAccountCloseDay", oldAccountCloseDay);
                    startActivity(intent1);
                }
                break;
            case R.id.rl_ucenter_tab_invite:
                startActivity(new Intent(getActivity(), InviteReward.class));
                break;
            case R.id.rl_ucenter_tab_zuixinhuodong:
                startActivity(new Intent(getActivity(), WebViewReleaseCg.class)
                        .putExtra("url", Config.getHttpConfig() + active_url.replace("\\", "")).putExtra("title", active_title));
                break;
            case R.id.iv_vp_left:
                if(vpBorrower.getCurrentItem()==0){
                    ToastMakeText.showToast(context,"没有更多了",1000);
                    return;
                }
                vpBorrower.setCurrentItem(vpBorrower.getCurrentItem() - 1);
                break;
            case R.id.iv_vp_right:
                if(vpBorrower.getCurrentItem()==viewlist.size()-1){
                    ToastMakeText.showToast(context,"没有更多了",1000);
                    return;
                }
                vpBorrower.setCurrentItem(vpBorrower.getCurrentItem() + 1);
                break;
            case R.id.user_center_switch_deposit:
                depositDialog = new DepositDialog(getActivity(), "切换中...", DepositDialog.Result.SUCCESS);
                depositDialog.setDuration(1000);
                depositDialog.setMyCallback(new MyCallback() {
                    @Override
                    public void callback() {
                        if (TextUtils.equals("2", uCenter)) {
                            uCenter = "1";
//                            initAccountState();//控制各种类型账户的布局
//                            changeRechargeBtnState();//充值按钮状态切换
                            //这里判断是否显示过第二引导页，否则显示它
                            if (sp.getBoolean("OpenCgCacheData1") && !TextUtils.equals("1", regAsDeposit) && !TextUtils.equals("-1", userType)) {
                                sp.setBoolean("OpenCgCacheData1", false);
                                addGuideLayoutOne(R.layout.guideview);
                            }
                        } else {
                            uCenter = "2";
                            initAccountState();
//                            changeRechargeBtnState();
                        }
                        initData(false);
                    }

                    @Override
                    public void doing() {
                    }
                });
                depositDialog.showDialog();
                break;
            case R.id.rl_ucenter_tab_recharge_account:
                startActivity(new Intent(getActivity(), RechargeAccount.class));
                break;
            default:
                break;
        }
    }

    private void initData(boolean showDialog) {
        isNotInit = false;
        if (showDialog) {
            depositDialog = new DepositDialog(context, "加载中...");
            depositDialog.setDuration(1000);
            depositDialog.setMyCallback(new MyCallback() {
                @Override
                public void callback() {}
                @Override
                public void doing() {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("token", sp.getValue("token"));
                    userMap.put("ucenter", uCenter);
                    postData(userMap, "/user/index", null, PostType.LOAD);
                }
            });
            depositDialog.showDialog();
        } else {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("token", sp.getValue("token"));
            userMap.put("ucenter", uCenter);
            postData(userMap, "/user/index", null, PostType.LOAD);
        }
    }

    private void initData() {
        isNotInit = false;
        depositDialog = new DepositDialog(context, "加载中...",true);
        depositDialog.setDuration(1000);
        depositDialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() {}
            @Override
            public void doing() {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("token", sp.getValue("token"));
                userMap.put("ucenter", uCenter);
                postData(userMap, "/user/index", null, PostType.LOAD);
            }
        });
        depositDialog.showDialog();
    }

    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            JSONObject data;
            try {
                data = object.getJSONObject("data");
                String result = "";
                if (data != null && data.has("result"))
                    result = data.getString("result");
                Intent intent = new Intent();
                if (result.equals("-1")) {//未绑卡
//                    intent.putExtra("step", data.getString("step"));
//                    intent.setClass(getActivity(), BankCardAdd.class);
//                    startActivity(intent);
                } else {
                    if (data.has("cardStatus") && isNotInit) {//非初始化数据加载
                        int cardStatus = Convert.strToInt(data.getString("cardStatus"), 1);
                        if (cardStatus == 1) {
                            switch (type) {
                                case 1://充值
//                                    intent.putExtra("bankName", data.getString("bankName"));
//                                    intent.putExtra("cardNo", data.getString("cardNo"));
//                                    intent.setClass(getActivity(), Recharge.class);
                                    break;
                                case 2://提现
                                    String usableSum = data.getString("usableSum");
                                    String bankName = data.getString("bankName");
                                    String cardNo = data.getString("cardNo");
                                    intent.putExtra("usableSum", usableSum);
                                    intent.putExtra("bankName", bankName);
                                    intent.putExtra("cardNo", cardNo);
                                    intent.setClass(getActivity(), TiXian.class);//WithDraw
                                    break;
                                case 3://绑卡
//                                    intent.putExtra("bankMobilePhone", data.getString("bankMobilePhone"));
//                                    intent.putExtra("cardNo", data.getString("cardNo"));
//                                    intent.putExtra("bankName", data.getString("bankName"));
//                                    intent.setClass(getActivity(), BankCard.class);
                                    break;
                                default:
                                    break;
                            }
                        } else {//卡状态不为1，表示已经实名认证，但未填写银行卡信息
//                            intent.putExtra("bankMobilePhone", data.getString("bankMobilePhone"));
//                            intent.putExtra("cardNo", data.getString("cardNo"));
//                            intent.putExtra("realName", data.getString("realName"));
//                            intent.putExtra("idNo", data.getString("idNo"));
//                            intent.putExtra("step", data.getString("step"));
//                            intent.setClass(getActivity(), EditorBankCard.class);
                        }
                        startActivity(intent);
                    } else {//初始化数据加载
                        isNotInit = true;
                        if (type == 0) {
                            if (data != null) {
                                String phone = data.getString("phone");
                                String userName = data.getString("userName");
                                String nickName = data.getString("nickName");
                                String realName = data.getString("realName");
                                User user = new User();
                                try {
                                    user.setHasSignIn(data.getString("hasSignIn"));
                                    user.setPhone(phone);
                                    user.setUserName(userName);
                                    user.setNickName(nickName);
                                    user.setrRealName(data.getString("realName"));
                                    user.setIdNo(data.getString("idNo"));
                                    user.setRealNameStatus(data.getString("realNameAuditStatus"));
                                    user.setSex(data.getString("sex"));
                                    user.setUsableCount(data.getString("usableAmount"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                sp.setUserInfo(user);
                                sp.setOtherValue("phone0", phone);
//                                if (!TextUtils.isEmpty(nickName)) {
//                                    tvUserPhone.setText(nickName);
//                                } else
                                //刷新depositCheck
                                if (data.has("depositCheck") && !TextUtils.equals("1", depositCheck) && TextUtils.equals("1", data.getString("depositCheck"))) {
                                    depositCheck = data.getString("depositCheck");
                                    App.depositCheck = depositCheck;
                                    sp.setValue("depositCheck", depositCheck);
                                    if (TextUtils.equals("1", depositCheck))//开通存管，更新个人中心状态为存管账户 && !createCgAccount
                                        uCenter = "2";
//                                    initAccountState();
                                }
                                String addName = "";
                                if (TextUtils.equals("1", uCenter)) {
                                    if (!TextUtils.equals("1", regAsDeposit) && TextUtils.equals("1", depositCheck))
                                        addName = "（原账户）";
                                } else {
                                    if (TextUtils.equals("1", depositCheck) && !TextUtils.equals("1", regAsDeposit)) {
                                        oldAccountCloseDay = data.getString("oldAccountCloseDay");
                                        App.oldAccountCloseDay = oldAccountCloseDay;//解决禅道bug
                                        if (data.has("oldAccountCloseDay") && !TextUtils.equals("-1", oldAccountCloseDay)) {
                                            addName = "（存管账户）";
                                        }
                                    }
                                }
                                if (!TextUtils.isEmpty(realName)) {
                                    tvUserPhone.setText(realName.trim() + addName);
                                }else if (!TextUtils.isEmpty(nickName)) {
                                    tvUserPhone.setText(nickName.trim() + addName);
                                } else if (!TextUtils.isEmpty(userName)) {
                                    tvUserPhone.setText(userName.trim() + addName);
                                } else {
                                    tvUserPhone.setText(phone.trim() + addName);
                                }
                                //借款人对公对私类型
                                borrowerType = data.getString("borrowerType");
                                App.borrowerType = borrowerType;
                                App.borrowerAccountType = borrowerAccountType;
                                borrowerAccountType = data.getString("borrowerAccountType");
                                //如果为借款人，头部三个标题和值要取特定值
                                if (TextUtils.equals("-1", userType)) {
                                    tv_usercenter0.setText("待还款（元）");
                                    tv_usercenter_1.setText("冻结金额（元）");
                                    tv_usercenter_2.setText("已还款（元）");
                                    tv_usercenter1.setText("可用余额（元）");
                                    tvUsercenterMoney.setNumberString(data.getString("sumNeedRepayAmount").replaceAll(",", ""));//待还款
                                    tv_money_interest_future.setNumberString(data.getString("sumRepayingAmount").replaceAll(",", ""));//冻结金额
                                    sumRepayedAmout = data.getString("sumRepayedAmout").replaceAll(",", "");
                                    if(!(TextUtils.equals("2",borrowerType)  && TextUtils.equals("1",borrowerAccountType)))
                                        tv_money_has_interest.setNumberString(sumRepayedAmout);//已还款
                                    cg_usableAmount = data.getString("cg_usableAmount").replaceAll(",", "");
                                    tvUsercenterUsableMoney.setNumberString(cg_usableAmount);//可用余额

                                    //借款人还款列表
                                    requestRepaymentList();
                                } else {
                                    accountSum = data.getString("accountSum").replaceAll(",", ""); //总金额
                                    if (TextUtils.equals("1", uCenter))
                                        oldAccountAccountSum = accountSum;
                                    /*else{
                                        //如果原账户总资产大于0
                                        if(!(Double.parseDouble(oldAccountAccountSum)>0)){
                                            user_center_switch_deposit.setVisibility(View.GONE);
                                        }
                                    }*/
                                    hasRePayInterest = data.getString("hasRePayInterest").replaceAll(",", "");//累计收益
                                    forPayInterest = data.getString("forPayInterest").replaceAll(",", "");//待收益
                                    usableAmount = data.getString("usableAmount").replaceAll(",", "");//可用余额
                                    String cg_accountSum = data.getString("cg_accountSum").replaceAll(",", "");//存管账户总余额
                                    //存管账户可用余额
                                    cg_usableAmount = data.getString("cg_usableAmount").replaceAll(",", "");
                                    if (TextUtils.equals("1", uCenter)) {
                                        tvUsercenterMoney.setNumberString(accountSum);//总金额
                                        tv_money_has_interest.setNumberString(hasRePayInterest);//累计收益
                                        tv_money_interest_future.setNumberString(forPayInterest);//待收益
                                        tvUsercenterUsableMoney.setNumberString(usableAmount);//可用余额
                                        changeRechargeBtnState();
                                        //原账户绑卡状态
                                        if (data.has("cardStatus")) {
                                            cardStatus = data.getString("cardStatus");
                                            sp.setValue("cardStatus", cardStatus);// 1 审核成功 2 待审核 3 审核失败
                                            sp.setValue("bankName", data.getString("bankName"));
                                            sp.setValue("cardNo", data.getString("cardNo"));
                                        }
                                    } else if (TextUtils.equals("2", uCenter)) {
                                        tvUsercenterMoney.setNumberString(cg_accountSum);
                                        tv_money_has_interest.setNumberString(hasRePayInterest);
                                        tv_money_interest_future.setNumberString(forPayInterest);
                                        tvUsercenterUsableMoney.setNumberString(cg_usableAmount);
                                    }
                                }
                                if (data.has("isReadMail")) {
                                    isReadMail = data.getString("isReadMail");
                                    iv_notice_icon.setImageResource(TextUtils.equals("0", isReadMail) ? R.mipmap.ucenter_notice0 : R.mipmap.ucenter_notice1);
                                }
                                if (data.has("activeTitle")) {
                                    if (!TextUtils.isEmpty(data.getString("activeTitle"))) {
                                        active_title = data.getString("activeTitle");
                                    }
                                }
                                tv_activeTitle.setText(active_title);
                                if (data.has("activeUrl")) {
                                    active_url = data.getString("activeUrl");
                                    if (TextUtils.isEmpty(active_url)) {
                                        rlUcenterTabZuixinhuodong.setVisibility(View.GONE);
                                        line_ucenter_tab_zjmx.setVisibility(View.GONE);
                                    } else {
                                        rlUcenterTabZuixinhuodong.setVisibility(View.VISIBLE);
                                        line_ucenter_tab_zjmx.setVisibility(View.VISIBLE);
                                    }
                                }
//                                initAccountState();
                                //红包状态
                                if (data.has("isRedpacketUnRead"))
                                    isRedpacketUnRead = data.getString("isRedpacketUnRead");
                                if (TextUtils.equals("0", isRedpacketUnRead))
                                    ivIsRedpacketUnRead.setVisibility(View.VISIBLE);
                                else ivIsRedpacketUnRead.setVisibility(View.GONE);
                                if (data.has("depositAccount"))
                                    sp.setValue("depositAccount", data.getString("depositAccount"));
                                //老用户第一次登录进入个人中心，弹出开通存管的通知
                                boolean hasOldAccountFirstSign = sp.getBooleanByUser(sp.getOtherValue("user0"),"hasOldAccountFirstSign");
                                if (!TextUtils.equals("1", depositCheck) && TextUtils.equals("1",uCenter) &&
                                        !hasOldAccountFirstSign) {
                                    CunGuanMailDialog d = new CunGuanMailDialog(context, 0.90);
                                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            requestEverRiskEvalution();
                                        }
                                    });
                                    d.show();
                                    sp.setBooleanByUser(sp.getOtherValue("user0"),"hasOldAccountFirstSign", true);
                                }else if (!TextUtils.equals("-1", userType)) {//&&App.FIRSTCHECKRISK !hasOldAccountFirstSign &&
                                    requestEverRiskEvalution();
                                }
                                //通知用户原账户在三天后关闭
                                if (data.has("oldAccountCloseDay") && !TextUtils.isEmpty(data.getString("oldAccountCloseDay"))) {
                                    oldAccountCloseDay = data.getString("oldAccountCloseDay");
                                    App.oldAccountCloseDay = oldAccountCloseDay;
                                    if (TextUtils.equals("-1", oldAccountCloseDay) && TextUtils.equals("2", uCenter)) {//存管账户
                                        user_center_switch_deposit.setVisibility(View.GONE);
                                    } else if (!TextUtils.equals("-1", userType) && !TextUtils.equals("-1", oldAccountCloseDay)) {//原账户
                                        tv_cg_tip.setText("温馨提示： 您的原账户资产已全部提现/迁移，我们将于" + oldAccountCloseDay +
                                                "日关闭原账户，感谢配合！");
                                        tv_cg_tip.setVisibility(View.VISIBLE);
                                        ll_cg_tip.setVisibility(View.GONE);
                                    } else {
                                        tv_cg_tip.setVisibility(View.GONE);
                                    }
                                    if (TextUtils.equals("-1", oldAccountCloseDay) && TextUtils.equals("2", uCenter)) {//当老账户被关闭
                                        user_center_switch_deposit.setVisibility(View.GONE);
                                    }
                                }
                                initAccountState();
                                //借款人自动还款状态
                                if (data.has("auto_repayment")) {
                                    String auto_repayment = data.getString("auto_repayment");
                                    App.auto_repayment = auto_repayment;
                                }
                            } else {
                                tvUsercenterMoney.setText("0.00");
                                tvUsercenterUsableMoney.setText("0.00");
                                tv_money_has_interest.setText("0.00");
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void requestOnFinish() {
        super.requestOnFinish();
//        if(isNotInit)
        refreshLayout.finishRefresh();
        if (depositDialog != null && depositDialog.isShowing())
            depositDialog.hideDialog();
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
            public void callback() {
            }

            @Override
            public void doing() {
                x.http().post(params, new Callback.CacheCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try {
                            JSONObject data = new JSONObject(arg0);
                            if (data.has("key") && TextUtils.equals("success", data.getString("key"))) {
                                if (data.has("relationKey"))
                                    startActivityForResult(new Intent(getActivity(), WebViewRelease.class).
                                            putExtra("html", data.getString("relationKey")).
                                            putExtra("title", "渤海存管账户开通"), REQUEST_ADD_CG);
                            } else if (data.has("result") && TextUtils.equals("-4", data.getString("result"))) {
                                ToastMakeText.showToast(context, "会话过期，请重新登录", 3000);
                                sp.removeAll();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                            } else {
                                ToastMakeText.showToast(context, data.getString("message"), 1200);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastMakeText.showToast(context, "服务器连接失败", 1000);
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

    private boolean isLastPage = false;
    private boolean isDragPage = false;
    private void requestRepaymentList() {
        String path = Config.getHttpConfig() + "/user/queryRepayList";
        final RequestParams params = new RequestParams(path);
        params.addParameter("pageNum", "1");
        params.addParameter("pageSize", 20);
        params.addParameter("time", "0");//时间检索类型0:不限制，其他：相应的天数,1个月以内 传30，半年以内 传180
        params.addParameter("type", "5");//0：全部，1:待还款，2：还款中，3：已还款，4：还款失败，5：个人中心待还款列表
        params.addParameter("token", sp.getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject data = new JSONObject(arg0);
                    if (data.has("result") && TextUtils.equals("1", data.getString("result"))) {
                        JSONObject data1 = data.getJSONObject("data");
                        if (data1.has("data")) {
                            JSONArray data2 = data1.getJSONArray("data");
                            int length = data2.length();
                            if (length > 0) {
                                flVp.setVisibility(View.VISIBLE);
                                viewlist = new ArrayList<>();
                                for (int i = 0; i < length; i++) {
                                    final JSONObject record = data2.getJSONObject(i);
                                    View page = View.inflate(context, R.layout.pager_item_borrower_repayment, null);
                                    TextView tv_repayment_title = (TextView) page.findViewById(R.id.tv_repayment_title);
                                    tv_repayment_title.setText(record.getString("productTitle"));
                                    TextView tv_repayment_money = (TextView) page.findViewById(R.id.tv_repayment_money);
                                    tv_repayment_money.setText(record.getString("productAmount") + "元");
                                    TextView tv_repayment_status = (TextView) page.findViewById(R.id.tv_repayment_status);
                                    String brstatus = record.getString("brstatus");
                                    switch (brstatus) {//1:待还款  2:还款中 3:已还款4:还款失败,-1:数据有问题导致状态不能显示
                                        case "1":
                                            tv_repayment_status.setText("待还款");
                                            tv_repayment_status.setBackgroundResource(R.drawable.btn_radius_cg_notice1);
                                            break;
                                        case "2":
                                            tv_repayment_status.setVisibility(View.GONE);
                                            break;
                                        case "3":
                                            tv_repayment_status.setVisibility(View.GONE);
                                        case "4":
                                            tv_repayment_status.setText("还款失败");
                                            tv_repayment_status.setBackgroundResource(R.drawable.btn_radius_cg_repayment_red);
                                        default:
                                            tv_repayment_status.setVisibility(View.GONE);
                                            break;
                                    }
                                    TextView tv_repayment_end_day = (TextView) page.findViewById(R.id.tv_repayment_end_day);
                                    tv_repayment_end_day.setText("最后还款日：" + record.getString("repayDate"));//investEndDay
                                    TextView tv_repayment_type = (TextView) page.findViewById(R.id.tv_repayment_type);
                                    final String pid = record.getString("pid");
                                    tv_repayment_type.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final PayPasswordDialog d = new PayPasswordDialog(context, 0.85);
                                            d.setOnRightClickListener(new PayPasswordDialog.onClickListener() {
                                                @Override
                                                public void click() {
                                                    d.dismiss();
                                                    requestRepayAction(pid, d.etInvestVerify.getText().toString());
                                                }
                                            });
                                            d.show();
                                        }
                                    });
                                    viewlist.add(page);
                                }
                                vpBorrower.setAdapter(new PageAdapter());
                                vpBorrower.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                                        mPageScrollPosition = position;
//                                        mPageScrollPositionOffset = positionOffset;
                                        if (isLastPage && isDragPage && positionOffsetPixels == 0){
                                            ToastMakeText.showToast(context,"没有更多了",800);
                                        }
                                        if(position==0&&isDragPage && positionOffsetPixels == 0){
                                            ToastMakeText.showToast(context,"没有更多了",800);
                                        }
                                    }

                                    @Override
                                    public void onPageSelected(int position) {
                                        isLastPage = position == viewlist.size()-1;
                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {
                                        isDragPage = state == 1;
                                    }
                                });
//                                int zeroItem = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % viewlist.size();
                                vpBorrower.setCurrentItem(0);
                            } else {
                                flVp.setVisibility(View.GONE);
                            }
                        }
                    } else if (data.has("result") && TextUtils.equals("-4", data.getString("result"))) {
                        ToastMakeText.showToast(context, "会话过期，请重新登录", 3000);
                        sp.removeAll();
                        Intent intent = new Intent(context, Login.class);
                        startActivity(intent);
                    } else {
                        ToastMakeText.showToast(context, data.getString("message"), 1200);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastMakeText.showToast(context, "服务器连接失败", 1000);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    private void changeRechargeBtnState() {
        if (!TextUtils.equals("-1", userType) && !TextUtils.equals("1", regAsDeposit) && TextUtils.equals("1", depositCheck)) {
            if (TextUtils.equals("1", uCenter)) {//原账户
                btn_recharge.setVisibility(View.GONE);
                btn_tixian.setVisibility(View.GONE);
                btn_transport.setVisibility(View.VISIBLE);
                //这里判断sp中OpenCgCacheData的值weitrue时，显示引导页，在投资页中开通存管
                //未执行显示。
                if (sp.getBoolean("OpenCgCacheData1") && !TextUtils.equals("1", regAsDeposit) && !TextUtils.equals("-1", userType)) {
                    sp.setBoolean("OpenCgCacheData1", false);
                    addGuideLayoutOne(R.layout.guideview);
                }
            } else if (TextUtils.equals("2", uCenter)) {//存管账户
                btn_recharge.setVisibility(View.VISIBLE);
                btn_tixian.setVisibility(View.VISIBLE);
                btn_transport.setVisibility(View.GONE);
            }
        }
    }

    private void requestRepayAction(String productId, String pwd) {
        depositDialog.backEnable = false;
        String path = Config.getHttpConfig() + "/bank/member/repayment";
        final RequestParams params = new RequestParams(path);
        params.addParameter("productId", productId);
        params.addParameter("dealPWD", pwd);
        params.addParameter("token", sp.getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(30 * 1000);
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
//                            Log.e("WXCF","还款返回json："+arg0);
                            JSONObject data = new JSONObject(arg0);
                            if (data.has("key")) {
                                String key = data.getString("key");
                                if (TextUtils.equals("success", key)) {
                                    final ExitDialog d = new ExitDialog(context, 0.85);
                                    d.setFillViewVisibility(false);
                                    d.setText(data.getString("message"));
                                    d.setTextSize(R.dimen.dimen_16);
                                    d.showCenterBtn();
                                    d.setFillViewVisibility(false);
                                    d.setOnCenterClickListener(new ExitDialog.onClickListener() {
                                        @Override
                                        public void click() {
                                            d.dismiss();
                                        }
                                    });
                                    d.show();
//                                    ToastMakeText.showToast(context,data.getString("message"),3000);
                                    //还款成功，刷新数据
                                    initData(false);
                                } else {
//                                    ToastMakeText.showToast(context,data.getString("message"),3000);
                                    final ExitDialog d = new ExitDialog(context, 0.85);
                                    d.setFillViewVisibility(false);
                                    d.setText(data.getString("message"));
                                    d.setTextSize(R.dimen.dimen_16);
                                    d.showCenterBtn();
                                    d.setFillViewVisibility(false);
                                    d.setOnCenterClickListener(new ExitDialog.onClickListener() {
                                        @Override
                                        public void click() {
                                            d.dismiss();
                                        }
                                    });
                                    d.show();
                                }
                            } else if (data.has("result") && TextUtils.equals("-4", data.getString("result"))) {
                                ToastMakeText.showToast(context, "会话过期，请重新登录", 3000);
                                sp.removeAll();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastMakeText.showToast(context, "服务器连接失败", 1000);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {
                        if (depositDialog != null)
                            depositDialog.hideDialog();
                        depositDialog.backEnable = true;
                    }

                    @Override
                    public boolean onCache(String result) {
                        return false;
                    }
                });
            }
        });
        depositDialog.showDialog();
        depositDialog.setBackForbidText("正在还款中，请稍等！");
    }

    private void initAccountState() {
        if (TextUtils.equals("-1", userType)) {//借款人账户
            btn_recharge.setVisibility(View.VISIBLE);
            rlUcenterTabHongbao.setVisibility(View.GONE);
            lineUcenterTabHongbao.setVisibility(View.GONE);
            rlUcenterTabZuixinhuodong.setVisibility(View.GONE);
            lineUcenterTabZuixinhuodong.setVisibility(View.GONE);
            rlUcenterTabInvite.setVisibility(View.GONE);
            lineUcenterTabInvite.setVisibility(View.GONE);
            rl_ucenter_invest_record.setVisibility(View.GONE);
            line_ucenter_tab_invest_record.setVisibility(View.GONE);
            rlUcenterTabHk.setVisibility(View.VISIBLE);
            lineUcenterTabHk.setVisibility(View.VISIBLE);
//            ll_cg_tip.setVisibility(View.GONE);
            view_cg_tip_borrower.setVisibility(View.VISIBLE);
            if (TextUtils.equals("1", depositCheck)) {
                rl_ucenter_tab_cg.setVisibility(View.GONE);
                line_ucenter_tab_cg.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(oldAccountCloseDay) && !TextUtils.equals("-1", oldAccountCloseDay)) {
                    tv_cg_tip.setVisibility(View.VISIBLE);
                    ll_cg_tip.setVisibility(View.GONE);
                } else {
                    tv_cg_tip.setVisibility(View.GONE);
                    ll_cg_tip.setVisibility(View.VISIBLE);
                }
            } else {
                ll_cg_tip.setVisibility(View.GONE);
                rl_ucenter_tab_cg.setVisibility(View.VISIBLE);
                line_ucenter_tab_cg.setVisibility(View.VISIBLE);
            }
            user_center_switch_deposit.setVisibility(View.GONE);//存管切换按钮
            //对公账户，1、渤海银行 2、非渤海银行
            if(TextUtils.equals("2",borrowerType)  && TextUtils.equals("2",borrowerAccountType)){//1、渤海银行
                tv_usercenter_1.setVisibility(View.GONE);
                tv_usercenter_2.setVisibility(View.GONE);
                tv_money_interest_future.setVisibility(View.GONE);
                tv_money_has_interest.setVisibility(View.GONE);
                line_ucenter_tab_zjmx.setVisibility(View.GONE);
                rlUcenterTabRechargeAccount.setVisibility(View.GONE);
                tv_usercenter0.setTextSize(TypedValue.COMPLEX_UNIT_PX,getActivity().getResources().getDimensionPixelOffset(R.dimen.dimen_16));
                tvUsercenterMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX,getActivity().getResources().getDimensionPixelOffset(R.dimen.dimen_36));
                if(tv_usercenter0.getLayoutParams() instanceof ViewGroup.MarginLayoutParams){
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tv_usercenter0.getLayoutParams();
                    int lMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.dimen_25);
                    int tMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.dimen_20);
                    p.setMargins(lMargin, tMargin, 0, 0);
                    tv_usercenter0.requestLayout();
                }
                //可用余额变成已还款
//                tv_usercenter1.setText("已还款");
//                tvUsercenterUsableMoney.setText(sumRepayedAmout);
                tv_usercenter1.setVisibility(View.GONE);
                tvUsercenterUsableMoney.setVisibility(View.GONE);
                btn_recharge.setVisibility(View.GONE);
                btn_tixian.setVisibility(View.GONE);
                tv_usercenter_borrower.setVisibility(View.VISIBLE);
                line_borrower.setVisibility(View.VISIBLE);
                tv_usercenter_borrower_amount.setVisibility(View.VISIBLE);
                tv_usercenter_borrower_amount.setText(sumRepayedAmout);
            }else if(TextUtils.equals("2",borrowerType)  && TextUtils.equals("1",borrowerAccountType)){//2、非渤海银行
                tv_usercenter_1.setVisibility(View.GONE);
                tv_money_interest_future.setVisibility(View.GONE);
                line_ucenter_tab_zjmx.setVisibility(View.VISIBLE);
                rlUcenterTabRechargeAccount.setVisibility(View.VISIBLE);
                //已还款改成可用余额
                tv_usercenter_2.setText("可用余额(元)");
                requestBigRechargeData();//请求接口获取非渤海可用余额

                tv_usercenter1.setVisibility(View.GONE);
                tvUsercenterUsableMoney.setVisibility(View.GONE);
                btn_recharge.setVisibility(View.GONE);
                btn_tixian.setVisibility(View.GONE);
                tv_usercenter_borrower.setVisibility(View.VISIBLE);
                line_borrower.setVisibility(View.VISIBLE);
                tv_usercenter_borrower_amount.setVisibility(View.VISIBLE);
                tv_usercenter_borrower_amount.setNumberString(sumRepayedAmout);
            }
        } else if (TextUtils.equals("1", regAsDeposit)) {//新注册的用户直接显示存管用户界面
            btn_recharge.setVisibility(View.VISIBLE);
            if (!TextUtils.equals("1", depositCheck)) {//未开通
                rl_ucenter_tab_cg.setVisibility(View.VISIBLE);
                line_ucenter_tab_cg.setVisibility(View.VISIBLE);
            } else {
                rl_ucenter_tab_cg.setVisibility(View.GONE);
                line_ucenter_tab_cg.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(oldAccountCloseDay) && !TextUtils.equals("-1", oldAccountCloseDay)) {
                tv_cg_tip.setVisibility(View.VISIBLE);
                ll_cg_tip.setVisibility(View.GONE);
            } else {
                tv_cg_tip.setVisibility(View.GONE);
                if (TextUtils.equals("1", depositCheck))
                    ll_cg_tip.setVisibility(View.VISIBLE);
            }
            rlUcenterTabHk.setVisibility(View.GONE);
            lineUcenterTabHk.setVisibility(View.GONE);
            rlUcenterTabHongbao.setVisibility(View.VISIBLE);
            lineUcenterTabHongbao.setVisibility(View.VISIBLE);
            flVp.setVisibility(View.GONE);
            user_center_switch_deposit.setVisibility(View.GONE);
        } else if (TextUtils.equals("1", depositCheck)) {//老用户已开通存管&& TextUtils.equals("0",regAsDeposit)
            btn_recharge.setVisibility(View.VISIBLE);
            rl_ucenter_tab_cg.setVisibility(View.GONE);
            line_ucenter_tab_cg.setVisibility(View.GONE);
            rlUcenterTabHk.setVisibility(View.GONE);
            lineUcenterTabHk.setVisibility(View.GONE);
            rlUcenterTabHongbao.setVisibility(View.VISIBLE);
            lineUcenterTabHongbao.setVisibility(View.VISIBLE);
            flVp.setVisibility(View.GONE);
            //如果原账户的总资产小于0，直接隐藏切换按钮
            if (oldAccountCloseDay != null && !TextUtils.equals("-1", oldAccountCloseDay)) {
                user_center_switch_deposit.setVisibility(View.VISIBLE);
            }
            if (TextUtils.equals("2", uCenter)) {
                user_center_switch_deposit.setImageResource(R.mipmap.cg_switch1);
                if (!TextUtils.isEmpty(oldAccountCloseDay) && !TextUtils.equals("-1", oldAccountCloseDay)) {
                    tv_cg_tip.setVisibility(View.GONE);//VISIBLE
                    ll_cg_tip.setVisibility(View.VISIBLE);//GONE
                } else {
                    tv_cg_tip.setVisibility(View.GONE);
                    ll_cg_tip.setVisibility(View.VISIBLE);
                }
            } else {//原账户
                user_center_switch_deposit.setImageResource(R.mipmap.cg_switch0);
                if (!TextUtils.isEmpty(oldAccountCloseDay) && !TextUtils.equals("-1", oldAccountCloseDay)) {
                    tv_cg_tip.setVisibility(View.VISIBLE);//VISIBLE
                    ll_cg_tip.setVisibility(View.GONE);//
                } else {
                    tv_cg_tip.setVisibility(View.GONE);
                    ll_cg_tip.setVisibility(View.GONE);//VISIBLE
                }
            }
            changeRechargeBtnState();
        } else {//老用户未开通存管
//            btn_recharge.setVisibility(View.GONE);
            rl_ucenter_tab_cg.setVisibility(View.VISIBLE);
            line_ucenter_tab_cg.setVisibility(View.VISIBLE);
            ll_cg_tip.setVisibility(View.GONE);
            rlUcenterTabHk.setVisibility(View.GONE);
            lineUcenterTabHk.setVisibility(View.GONE);
            rlUcenterTabHongbao.setVisibility(View.VISIBLE);
            lineUcenterTabHongbao.setVisibility(View.VISIBLE);
            flVp.setVisibility(View.GONE);
            user_center_switch_deposit.setVisibility(View.GONE);
        }
    }

    public void setBooleanFirstclicktab2(boolean b) {
        isfirstclicktab2 = b;
    }

    @Override
    public void handleAutoPlayActionUpOrCancel(float xVelocity) {
        if (vpBorrower != null && mPageScrollPosition < vpBorrower.getCurrentItem()) {
            // 往右滑
            if (xVelocity > VEL_THRESHOLD || (mPageScrollPositionOffset < 0.7f && xVelocity > -VEL_THRESHOLD)) {
                vpBorrower.setBannerCurrentItemInternal(mPageScrollPosition);
            } else {
                vpBorrower.setBannerCurrentItemInternal(mPageScrollPosition + 1);
            }
        } else {
            // 往左滑
            if (xVelocity < -VEL_THRESHOLD || (mPageScrollPositionOffset > 0.3f && xVelocity < VEL_THRESHOLD)) {
                vpBorrower.setBannerCurrentItemInternal(mPageScrollPosition + 1);
            } else {
                vpBorrower.setBannerCurrentItemInternal(mPageScrollPosition);
            }
        }
    }

    private class PageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
//            return viewlist == null ? 0 : Integer.MAX_VALUE;
            return viewlist.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            final int finalPosition = position % viewlist.size();
//            View view;
//            if (mHackyViews == null)
//                view = viewlist.get(finalPosition);
//            else
//                view = mHackyViews.get(position % mHackyViews.size());
//            if (container.equals(view.getParent())) {
//                container.removeView(view);
//            }
//            container.addView(view);
//            return view;
            ((ViewPager) container).addView(viewlist.get(position), 0);
            return viewlist.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (viewlist.size() > position)
                ((ViewPager) container).removeView(viewlist.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

//        @Override
//        public int getItemPosition(Object object) {
//            return POSITION_NONE;
//        }

    }

    public void requestFail(String msg, PostType postType) {
        super.requestFail(msg, postType);
        ToastMakeText.showToast(getActivity(), msg, 1200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Transport.REQUEST_TRANSPORT_SUC && resultCode == Transport.RESULT_TRANSPORT_SUC) {
            uCenter="2";//资金迁移完成后，点确定返回个人中心，应跳到存管账号界面
            initData(true);//迁移成功返回
        } else if (requestCode == RedPacket.REQUEST_RED_PACKET && resultCode == RedPacket.RESULT_RED_PACKET) {
            initData(false);//红包状态更新后返回
        } else if (requestCode == REQUEST_ADD_CG && resultCode == RESULT_ADD_CG) {
            if(TextUtils.equals("1",App.depositCheck))
                uCenter = "2";
            initData(true);//开通存管页返回
            if (sp.getBoolean("OpenCgCacheData0") && !TextUtils.equals("1", regAsDeposit) && !TextUtils.equals("-1", userType)) {
                sp.setBoolean("OpenCgCacheData0", false);
                addGuideLayoutOne(R.layout.guideview_1);
            }
        } else if (requestCode == REQUEST_SETTING && resultCode == RESULT_ADD_CG) {
            initData(true);//设置页开通存管页返回
            if (sp.getBoolean("OpenCgCacheData0") && !TextUtils.equals("1", regAsDeposit) && !TextUtils.equals("-1", userType)) {
                sp.setBoolean("OpenCgCacheData0", false);
                addGuideLayoutOne(R.layout.guideview_1);
            }
        } else if (requestCode == REQUEST_SETTING && resultCode == RESULT_ADD_CG) {
            initData(true);//项目还款页成功还款后返回
        }
    }

    private void requestEverRiskEvalution() {
        String path = Config.getHttpConfig() + "/activity/questionnairAnswered";
        final RequestParams params = new RequestParams(path);
        params.addParameter("questionId", "hg01");
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("result");
                    String msg = object.getString("msg");
                    msg = msg.replace("&nbsp;", " ");
                    switch (result) {
                        case "0":
                            RiskEvalutionDialog d = new RiskEvalutionDialog(getActivity(), true, 0.8);
                            String text = "五星一直坚持合法合规运营，稳定长期发展。为广大投资者创造安全可靠的投资平台。为响应监管机构64号文件，" +
                                    "明确要求权益持有人不得超过200人的要求。五星财富做出如下调整：\n1、 起投金额调整为5万元\n" +
                                    "为了满足投资人数不得超过200人的合规要求，决定调整项目起投金额为5万元。后续随着项目摘牌金额的变化，起投金额也会随之产生浮动。" +
                                    "\n2、 必须完成合格投资者问卷\n" + "即日起，每一位五星财富老用户需先填写完风险评估问卷后方可再进行投资。";
                            if (TextUtils.isEmpty(msg))
                                d.setMessage(text, getResources().getDimensionPixelOffset(R.dimen.dimen_12));
                            else
                                d.setMessage(msg, getResources().getDimensionPixelOffset(R.dimen.dimen_12));
                            d.setBtn1Text("稍后再说");
                            d.setBtn2Text("填写问卷");
                            d.setTitleVisible(true, "尊敬的用户：");
                            d.setTitleLeft();
                            d.setTextIvestVisible(false);
                            d.show();
                            d.setOnBtn2ClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(getActivity(), WebViewRelease.class);
                                    i.putExtra("url", Config.getHttpConfig() + "/activity/toRiskQuery");
                                    i.putExtra("title", "合格投资者问卷");
                                    startActivity(i);
                                }
                            });
                        case "1":
                            break;
                        case "-4":
                            ToastMakeText.showToast(context, "会话过期，请重新登录", 3000);
                            sp.removeAll();
                            Intent intent = new Intent(context, Login.class);
                            startActivity(intent);
                            break;
                        default:
                            break;
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
            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    private void addGuideLayoutOne(final int resId) {
        View view = context.getWindow().getDecorView().findViewById(R.id.my_content_view);
        if (view == null) return;
        ViewParent viewParent = view.getParent();
        if (viewParent instanceof FrameLayout) {
            final FrameLayout frameLayout = (FrameLayout) viewParent;
            final FrameLayout Guidview = (FrameLayout) View.inflate(context, resId, null);//R.layout.guideview
            Button btn = (Button) Guidview.findViewById(R.id.guide_btn_1);
            ImageView iv = (ImageView) Guidview.findViewById(R.id.iv_guide_1);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (resId == R.layout.guideview) {
                        frameLayout.removeView(Guidview);
                    } else if (resId == R.layout.guideview_1) {
                        frameLayout.removeView(Guidview);
                    }
                }
            });
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    return;
                }
            });
            frameLayout.addView(Guidview);//添加引导布局
        }
    }

    /*非渤海借款人可用余额*/
    private void requestBigRechargeData() {
        String depositAccount = sp.getValue("depositAccount");
        String path = Config.getHttpConfig() + "/bank/member/queryChargeAccount";
        final RequestParams params = new RequestParams(path);
        params.addParameter("depositAccount", depositAccount);
        params.addParameter("accountTyp", 2);//1-对私 2-对公
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
                        JSONObject relationKey = data.getJSONObject("relationKey");
                        if(relationKey.has("avlBal"))
                            tv_money_has_interest.setNumberString(relationKey.getString("avlBal"));
                    } else if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
                        ToastMakeText.showToast(context,"会话过期，请重新登录",3000);
                        sp.removeAll();
                        Intent intent = new Intent(context, Login.class);
                        startActivity(intent);
                    } else {
                        ToastMakeText.showToast(context, data.getString("message"), 2000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastMakeText.showToast(context, "服务器连接失败", 1000);
            }
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() {}
        });
    }
}
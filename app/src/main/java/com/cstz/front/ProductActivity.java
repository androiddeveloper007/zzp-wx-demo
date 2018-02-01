package com.cstz.front;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cstz.MyWidget.DepositDialog;
import com.cstz.adapter.MyAdapter;
import com.cstz.adapter.MyBaseAdapter;
import com.cstz.adapter.MyPagerAdapter;
import com.cstz.common.App;
import com.cstz.common.App.PostType;
import com.cstz.common.Config;
import com.cstz.common.MyCallback;
import com.cstz.common.MyFragmentActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.SysApplication;
import com.cstz.common.Validate;
import com.cstz.common.WebViewRelease;
import com.cstz.common.view.LuckyDrawDialog_1;
import com.cstz.common.view.MyDialog;
import com.cstz.common.view.MySlider;
import com.cstz.common.view.MySlider.OnClickSlider;
import com.cstz.common.view.RiskEvalutionDialog;
import com.cstz.common.view.UseableRedEnvelopeDialog;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.dialog.CunGuanWarningDialog;
import com.cstz.cunguan.fragment.UserCenter;
import com.cstz.front.viewpager.ViewPager;
import com.cstz.tools.Convert;
import com.cstz.tools.StatusBarUtil;
import com.cstz.tools.ToastMakeText;
import com.cstz.ui.xlistview.XListView;
import com.cstz.ui.xlistview.XListView.IXListViewListener;

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

/**
 * 项目详情页
 */
public class ProductActivity extends MyFragmentActivity implements OnClickListener, IXListViewListener {

    private MySlider mSlider;
    private android.support.v4.view.ViewPager viewPager;// 页卡内容
    private ImageView title_back;
    private TextView title_tv;
    private String _productId = "";   //产品编号
    private EditText _money;          //投资金额
    private RelativeLayout _button;   //确定投资
    private String money;
    private String yue;//融资余额
    private boolean isAvailableCountReq = false;//是否请求可用余额
    private String deadline;
    private Context context;
    private SharedPreferencesData sp;
    private DepositDialog loadDialog;
    private boolean refreshCgState;
    private final static int REQ_INVEST_SUC = 123;
    public final static int RES_INVEST_SUC = 234;
    List<View> viewlist;
    View page1, page2, page3;
    private ImageView imageview_song1;
    private TextView textView_productAmount, textView_annualRate,textView_deadline,textview_producttitle,textview_publisher,
            textview_exchangeName,textview_minTenderedSum,textview_yue,textvew_song1,textview_reapydate,textview_limit,tv_tag_cashback;
    private RelativeLayout rl_tag_cashback;
    private LinearLayout mLayout;
    private String result = "";
    private WebView xiangmugaishu, huankuanlaiyuan, anquanbaozhang;
    private GridView _gridview;
    private List<Map<String, Object>> _list = new ArrayList<Map<String, Object>>();
    private MyAdapter _adapter = null;
    public final static String CSS_STYLE = "<style>*{color:#666666;}</style>";//设置WebView字体颜色
    private ScrollView mScrollView;
    String laiyuan = "(1)标的资产为金融资产；<br/>(2)转让方深圳中成资产管理有限公司产品到期回购兑付本金及收益。";
    String baozhang = "转让方在投资协议中签署回购协议，保证产品到期还款。";
    private XListView _listview;
    private MyBaseAdapter adapter = null;
    private List<Map<String, Object>> listget = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> listget_envelope = new ArrayList<Map<String, Object>>();
    private static Handler mhandler = new Handler();
    private int _pageNum = 1;
    boolean isDialog = true;
    UseableRedEnvelopeDialog d = null;
    private int type = 0;
    private boolean tab0_firstIn = true, tab1_firstIn = true, tab2_firstIn = true;
    private String publisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.front_product_tabwidget);
        setTransparentStatusBar(this, false);
        context = this;
        sp = new SharedPreferencesData(this);
        initView();
        SysApplication.getInstance().addActivity(this);
    }
    /*设置6.0以上系统，沉浸式状态栏*/
    public void setTransparentStatusBar(Activity activity,boolean cg){
        if(Build.VERSION.SDK_INT > 22){
            RelativeLayout toolbar = (RelativeLayout) activity.findViewById(R.id.layout_title);
            if(toolbar!=null){
                ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
                lp.height = getResources().getDimensionPixelOffset(R.dimen.dimen_42);
                toolbar.setLayoutParams(lp);
            }
            StatusBarUtil.setColor(this, getResources().getColor(!cg?R.color.window_background:R.color.navigation_cg), 0);
        }
    }

    private void initView() {

        mSlider = (MySlider) findViewById(R.id.product_detail_slider);
        mSlider.setOnClickSlider(new OnClickSlider() {

            @Override
            public void getIndex(int index) {
                viewPager.setCurrentItem(index);
            }

        });

        _money = (EditText) findViewById(R.id.front_invest_money);
        _button = (RelativeLayout) findViewById(R.id.front_invest_button);

        title_tv = (TextView) findViewById(R.id.title_tv);
        Intent intent = getIntent();
        if (intent != null) {
            _productId = intent.getStringExtra("productId");
        }
        title_tv.setText("项目详情");
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);

        InitViewPager();
        clicklistener();
    }

    private void InitViewPager() {
        viewPager = (android.support.v4.view.ViewPager) findViewById(R.id.vPager);
        viewlist = new ArrayList<>();
        page1 = View.inflate(this, R.layout.front_product_xiangqing, null);
        page2 = View.inflate(this, R.layout.front_product_jieshao, null);
        page3 = View.inflate(this, R.layout.front_product_jilv, null);
        viewlist.add(page1);
        viewlist.add(page2);
        viewlist.add(page3);
        viewPager.setAdapter(new MyPagerAdapter(viewlist));
        getPage1Data(page1);
        getPage2Data(page2);
        getPage3Data(page3);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

    }

    public void getPage1Data(View view) {
        if (mLayout == null) {
            mLayout = (LinearLayout) view.findViewById(R.id.front_product_xx_layout);
            mLayout.setVisibility(View.GONE);

            textView_productAmount = (TextView) view.findViewById(R.id.textView_productAmount);
            textView_annualRate = (TextView) view.findViewById(R.id.textView_annualRate);
            textView_deadline = (TextView) view.findViewById(R.id.textView_deadline);
            textview_producttitle = (TextView) view.findViewById(R.id.textview_producttitle);
            textview_publisher = (TextView) view.findViewById(R.id.textview_publisher);
            textview_exchangeName = (TextView) view.findViewById(R.id.textview_exchangeName);
            textview_minTenderedSum = (TextView) view.findViewById(R.id.textview_minTenderedSum);
            textview_yue = (TextView) view.findViewById(R.id.textview_yue);
            imageview_song1 = (ImageView) view.findViewById(R.id.imageview_song1);
            textvew_song1 = (TextView) view.findViewById(R.id.textvew_song1);
            textview_reapydate = (TextView) view.findViewById(R.id.textview_repaydate);
            textview_limit = (TextView) view.findViewById(R.id.textview_limit);
            tv_tag_cashback = (TextView) view.findViewById(R.id.tv_tag_cashback);
            rl_tag_cashback = (RelativeLayout) view.findViewById(R.id.rl_tag_cashback);

        }

        Intent intent = getIntent();
        if (intent != null && intent.getExtras().getString("productId") != null) {
            _productId = intent.getStringExtra("productId");
        }

        if (!TextUtils.isEmpty(_productId)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("productId", _productId);
            map.put("token",sp.getValue("token"));
            postData(map, "/product/detail", PostType.LOAD, true);//true
        }

    }

    public void getPage2Data(View view) {
        if (mScrollView == null) {
            mScrollView = (ScrollView) view.findViewById(R.id.front_product_js_scrollview);
            mScrollView.setVisibility(View.GONE);

            xiangmugaishu = (WebView) view.findViewById(R.id.webView_xiangmugaishu);
            huankuanlaiyuan = (WebView) view.findViewById(R.id.webView_huankuanlaiyuan);
            anquanbaozhang = (WebView) view.findViewById(R.id.webView_anquanbaozhang);

            anquanbaozhang.loadDataWithBaseURL(null, CSS_STYLE + baozhang, "text/html", "utf-8", null);

            _gridview = (GridView) view.findViewById(R.id.gridView_beichawenjian);
        }

    }

    public void initData2() {
        _list.clear();

        Intent intent = getIntent();
        if (intent != null && intent.getExtras().getString("productId") != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("productId", intent.getStringExtra("productId"));
            postData(map, "/product/pMoreDetail", PostType.LOAD, true);//true
        }
    }

    public void getPage3Data(View view) {
        _listview = (XListView) view.findViewById(R.id.product_listView_jilu);
        _listview.setPullRefreshEnable(false);
        _listview.setPullLoadEnable(false); // 下拉加载
        _listview.setXListViewListener(ProductActivity.this);
//		_listview.setEmptyViewEnable(true);
    }

    public void initData3() {
        if (!TextUtils.isEmpty(_productId)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("productId", getProductId());
            map.put("pageNum", _pageNum);
            postData(map, "/product/pInvestListData", PostType.LOAD, isDialog);
        }
    }

    public void requestFail(String msg, PostType postType) {
        super.requestFail(msg, postType);
        ToastMakeText.showToast(this, msg, 1200);
        if (type == 2)
            _listview.stopLoadMore();
    }

    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            try {
                if (postType == PostType.SUBMIT) {
                    JSONObject data = object.getJSONObject("data");
                    if (data != null) {
                        if (isAvailableCountReq) {
                            isAvailableCountReq = false;//点击投资按钮返回结果
                            //去掉字符串中逗号
//                            float availableCount = Convert.strToFloat(data.getString("availableBalance").replace(",", ""), 0);
                            float availableCount = Convert.strToFloat(data.getString("cg_usableAmount").replace(",", ""), 0);
                            if (Convert.strToFloat(money, 0) > availableCount) {
                                //金额大于余额时，提示充值
                                LuckyDrawDialog_1 d = new LuckyDrawDialog_1(ProductActivity.this, true, 0.8);
//                                d.setMessage("投资金额" + money + "元，余额" + availableCount + "元，不足投资，马上充值？", true);
                                d.setMessage("存管账户余额不足，马上充值？", true);
                                d.setBtn1Text("取消");
                                d.setBtn2Text("充值");
                                d.setTitleVisible(true, "充值提示");
                                d.setTextIvestVisible(false);
                                d.show();
                                d.setOnBtn2ClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        Map<String, Object> map2 = new HashMap<String, Object>();
//                                        try {
//                                            type = 1;
//                                            map2.put("token", sp.getValue("token"));
//                                            postData(map2, "/user/recharge", PostType.SUBMIT, true);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
                                        startActivity(new Intent(context, com.cstz.cunguan.activity.Recharge.class));
                                    }
                                });
                            } else {
                                //投资成功时，退到这个页面时，刷新数据
                                Intent intent = new Intent(ProductActivity.this, Invest.class);
                                intent.putExtra("productId", _productId);
                                intent.putExtra("amount", money);
                                startActivityForResult(intent, REQ_INVEST_SUC);
                            }
                        } else {
                            //根据后台返回的depositCheck更新，存管状态
                            if(!TextUtils.equals("1",App.depositCheck) && data.has("depositCheck")){
                                String depositCheck = data.getString("depositCheck");
                                if(TextUtils.equals("1",depositCheck)){
                                    App.depositCheck=depositCheck;
                                    sp.setValue("depositCheck",depositCheck);
                                    //真实姓名和存管账号
                                    if(data.has("realName"))
                                        sp.setValue("realName",data.getString("realName"));
                                    if(data.has("depositAccount"))
                                        sp.setValue("depositAccount",data.getString("depositAccount"));
                                }
                            }
                            result = data.getString("result");
                            Intent intent = new Intent();
                            if (result.equals("-1")) {
//                                intent.putExtra("step", data.getString("step"));
//                                intent.setClass(ProductActivity.this, BankCardAdd.class);
                            } else {
                                int cardStatus = Convert.strToInt(data.getString("cardStatus"), 1);
                                if (cardStatus == 1) {
                                    if (type == 1) {
//                                        intent.putExtra("bankName", data.getString("bankName"));
//                                        intent.putExtra("cardNo", data.getString("cardNo"));
//                                        intent.putExtra("isInvest", true);
//                                        intent.setClass(ProductActivity.this, Recharge.class);
                                    }
                                } else {
//                                    intent.putExtra("bankMobilePhone", data.getString("bankMobilePhone"));
//                                    intent.putExtra("cardNo", data.getString("cardNo"));
//                                    intent.putExtra("realName", data.getString("realName"));
//                                    intent.putExtra("idNo", data.getString("idNo"));
//                                    intent.putExtra("step", data.getString("step"));
//                                    intent.setClass(ProductActivity.this, EditorBankCard.class);
                                }
                            }
                            startActivity(intent);
                        }
                    }
                } else if (postType == PostType.LOAD) {
                    switch (type) {
                        case 0:
                            tab0_firstIn = false;
                            JSONObject data = object.getJSONObject("data");
                            if (data != null) {
                                mLayout.setVisibility(View.VISIBLE);
                                DecimalFormat df = new DecimalFormat("0.0");
                                final float award = Convert.strToFloat(data.getString("award"), 0);
                                final float annualRate = Convert.strToFloat(data.getString("annualRate"), 0);
                                if (award > 0) {
                                    imageview_song1.setBackgroundResource(R.drawable.song1);
                                    textvew_song1.setText("+" + award + "%");
                                } else {
                                    imageview_song1.setBackgroundResource(0);
                                    textvew_song1.setText("");
                                }
                                if(data.has("cashback")){
                                    final float cashback = Convert.strToFloat(data.getString("cashback"), 0);
                                    if (cashback > 0) {
                                        tv_tag_cashback.setText("返现" + cashback + "%");
                                        rl_tag_cashback.setVisibility(View.VISIBLE);
                                    } else {
                                        rl_tag_cashback.setVisibility(View.GONE);
                                    }
                                }
                                textView_annualRate.setText(df.format(annualRate));
                                textView_productAmount.setText(data.getString("productAmount"));
                                deadline = data.getString("deadline");
                                textView_deadline.setText(deadline);
                                textview_producttitle.setText(data.getString("productTitle"));
                                publisher = data.getString("publisher");
                                textview_publisher.setText(publisher);
                                textview_exchangeName.setText(data.getString("exchangeName"));
                                textview_minTenderedSum.setText(data.getString("minTenderedSum") + "元起投");
                                textview_yue.setText(data.getString("yue") + "元");
                                textview_reapydate.setText(data.getString("investEndDay"));
                                if(data.has("maxInvestAmount")){
                                    if(TextUtils.isEmpty(data.getString("maxInvestAmount")) || TextUtils.equals("0",data.getString("maxInvestAmount")) || TextUtils.equals("0.00",data.getString("maxInvestAmount")))
                                        textview_limit.setText("不限制");
                                    else
                                        textview_limit.setText("单用户每天最多投资"+data.getString("maxInvestAmount")+"元");
                                }else{
                                    textview_limit.setText("不限制");
                                }

                                //返回Activity设置ProductId
                                setProductId(data.getString("pId"));
                                yue = data.getString("yue");

                                viewPaint(textView_productAmount);
                                viewPaint(textView_annualRate);
                                viewPaint(textView_deadline);

                                searchRedEnvelope(1);
                            }
                            break;
                        case 1:
                            tab1_firstIn = false;
                            JSONObject data1 = object.getJSONObject("data");
                            if (data1 != null) {
                                mScrollView.setVisibility(View.VISIBLE);
                                xiangmugaishu.loadDataWithBaseURL(null, CSS_STYLE + data1.getString("productInfo"), "text/html", "utf-8", null);
                                String laiyuan1 = "(1)标的资产为金融资产；<br/>(2)转让方"+publisher+"产品到期回购兑付本金及收益。";//深圳中成资产管理有限公司
                                if(!TextUtils.isEmpty(publisher))
                                    huankuanlaiyuan.loadDataWithBaseURL(null, CSS_STYLE + laiyuan1, "text/html", "utf-8", null);
                                else
                                    huankuanlaiyuan.loadDataWithBaseURL(null, CSS_STYLE + laiyuan, "text/html", "utf-8", null);
                                JSONArray list = new JSONArray(data1.getString("pImage"));
                                final List<String> pathList = new ArrayList<String>();
                                final List<String> titleList = new ArrayList<String>();
                                for (int i = 0; i < list.length(); i++) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    JSONObject image = list.getJSONObject(i);
                                    map.put("name", image.getString("name"));
                                    map.put("path", image.getString("img"));
                                    _list.add(map);
                                    pathList.add(image.getString("img"));
                                    titleList.add(image.getString("name"));
                                }
                                if (_adapter == null) {
                                    _adapter = new MyAdapter(_list, ProductActivity.this);
                                    _gridview.setAdapter(_adapter);
                                    _gridview.setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(ProductActivity.this, ViewPager.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("position", position);
                                            bundle.putStringArrayList("pathList", (ArrayList<String>) pathList);
                                            bundle.putStringArrayList("titleList", (ArrayList<String>) titleList);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    _adapter.notifyDataSetChanged();
                                }
                            }
                            break;
                        case 2:
                            _listview.setEmptyViewText(R.string.app_no_data_tips);
                            tab2_firstIn = false;
                            if (isDialog)
                                isDialog = false;
                            JSONArray data3 = object.getJSONArray("data");
                            if (data3 != null) {
                                int length = data3.length();
                                for (int i = 0; i < length; i++) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    JSONObject investRecord = data3.getJSONObject(i);
                                    map.put("username", investRecord.getString("username"));
                                    map.put("investAmount", investRecord.getString("investAmount"));
                                    map.put("investMode", investRecord.getString("investMode"));
                                    map.put("investTime", investRecord.getString("investTime"));
                                    listget.add(map);
                                }

                                if (length < 20) {
                                    _listview.setPullLoadEnable(false);

                                } else {
                                    _listview.setPullLoadEnable(true);
                                }

                                if (adapter == null) {
                                    adapter = new MyBaseAdapter(listget, ProductActivity.this);
                                    _listview.setAdapter(adapter);
                                } else {

                                    _listview.stopLoadMore();

                                    if (_listview.getAdapter() == null) {
                                        _listview.setAdapter(adapter);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            break;
                        case 3:
                            JSONArray array = null;
                            if( object.has("data") && object.getJSONArray("data") != null){
                                array = object.getJSONArray("data");
                            }
                            int length=0;
                            if(array!=null) {
                                length = array.length();
                                for (int i = 0; i < length; i++) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    JSONObject record = array.getJSONObject(i);
                                    map.put("redproportion", record.get("REDPROPORTION"));
                                    map.put("redwrap", record.get("REDWRAP"));
                                    String term = record.get("PRODUCTDAY").toString();
                                    map.put("term", term);//期限
//                                    if( Integer.parseInt(term) <= Integer.parseInt(deadline))
                                    listget_envelope.add(map);
                                }
                            }
                            if(d == null && length>0){
                                d = new UseableRedEnvelopeDialog(this,true, listget_envelope);
                                d.setBtn1Visibility(true);
                                d.setBtn1Text("知道了");
                                d.setTitle("尚未使用的红包");
//                                d.setLoadMoreListener(new UseableRedEnvelopeDialog.loadmoreListener(){
//                                    @Override
//                                    public void loadmore() {
//                                        searchRedEnvelope(++pageNum);
//                                    }
//                                });
                                d.show();
                            } else if(d != null && length>0){
//                                d.loadmore(listget_envelope);
                            }
//                            if(length < 15)
//                                d.setHasMoreData(false);
//                            else
//                                d.setHasMoreData(true);
                            break;
                        default:
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void searchRedEnvelope(int pageNum) {
        type=3;
        listget_envelope.clear();
        if(!TextUtils.isEmpty(sp.getValue("token"))){
            Map<String,Object> map = new HashMap<>();
            map.put("token", sp.getValue("token"));
            map.put("type", "1");
//            map.put("pageNum", pageNum);
            map.put("dealine", deadline);
            postData(map,"/user/queryAllUnUsedRedPacketList",null,PostType.LOAD);// queryRedPacketList
        }
    }

    /**
     * 字体加粗
     */
    private void viewPaint(TextView view) {
        TextPaint tp = view.getPaint();
        tp.setFakeBoldText(true);
    }

    /**
     * 按钮的监听事件
     */
    public void clicklistener() {
        title_back.setOnClickListener(this);
        _button.setOnClickListener(this);
    }

    /**
     * 为选项卡绑定监听器
     */
    public class MyOnPageChangeListener implements OnPageChangeListener {

        public void onPageScrollStateChanged(int index) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int index) {
            mSlider.setSelectIndex(index);
            //这里做处理，让请求和type值
            switch (index) {
                case 0:
                    type = 0;
                    if (tab0_firstIn)
                        getPage1Data(page1);
                    break;
                case 1:
                    type = 1;
                    if (tab1_firstIn)
                        initData2();
                    break;
                case 2:
                    type = 2;
                    if (tab2_firstIn)
                        initData3();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 定义适配器
     */
    class MyFragmentPageAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        public MyFragmentPageAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        /**
         * 每个页面的title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        /**
         * 页面的总个数
         */
        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentList.get(arg0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.front_invest_button:
                if (ToastMakeText.isFastClick()) {
                    return;
                }
                if(TextUtils.equals("-1", App.userType)){
                    ToastMakeText.showToast(ProductActivity.this, "抱歉，暂不开放借款人投资项目功能", 3000);
                    return;
                }
                money = Convert.trim(_money.getText().toString());
                if (TextUtils.isEmpty(money)) {
                    ToastMakeText.showToast(ProductActivity.this, "请输入投资金额", 1200);
                    return;
                } else if (!Validate.validateMoney(money)) {
                    ToastMakeText.showToast(ProductActivity.this, "投资金额格式错误", 1200);
                    return;
                } else if (!(Double.parseDouble(money) % 100 == 0)) {//投资金额需要是100倍数
                    ToastMakeText.showToast(ProductActivity.this, "投资金额需满足100的倍数", 1200);
                    return;
                } else if (Double.parseDouble(money) <= 0) {
                    ToastMakeText.showToast(ProductActivity.this, "请输入正确的投资金额", 1200);
                    return;
                } else if (Convert.strToFloat(yue.replace(",", ""), 0) < Double.parseDouble(money)) {
                    ToastMakeText.showToast(ProductActivity.this, "您输入的金额超出了项目融资余额", 2000);
                    return;
                }
                if (TextUtils.isEmpty(sp.getValue("token"))) {
                    startActivity(new Intent(context, Login.class));return;
                }
                //未开通存管，点投资提示开通存管的框
                if(TextUtils.equals("0", App.depositCheck)){
                    final CunGuanWarningDialog d = new CunGuanWarningDialog(ProductActivity.this,0.90);
                    d.hideBtn0();
                    d.show();
                    d.setOnApplyClickListener(new CunGuanWarningDialog.onApplayButtonClick() {
                        @Override
                        public void applyClick() {
                            requestAddCgAccount();
                            d.dismiss();
                        }
                    });
                    return;
                }
                //判断时候参与问卷调查
                requestEverRiskEvalution();
                break;
            default:break;
        }
    }

    private void requestEverRiskEvalution() {
        String path = Config.getHttpConfig() + "/activity/questionnairAnswered";
        final RequestParams params = new RequestParams(path);
        params.addParameter("questionId", "hg01");
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        final MyDialog mDialog = new MyDialog(this, "加载中...");
        mDialog.setDuration(300);
        mDialog.setMyCallback(new MyCallback() {
            @Override
            public void doing() {
                x.http().post(params, new Callback.CacheCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try {
                            JSONObject object = new JSONObject(arg0);
                            String result = object.getString("result");
                            String msg = object.getString("msg");
                            msg = msg.replace("&nbsp;"," ");
                            switch (result) {
                                case "1":
                                    //判断余额不足时，提示充值
                                    isAvailableCountReq = true;
                                    Map<String, Object> map = new HashMap<>();
                                    try {
//                                        map.put("productId", _productId);
//                                        map.put("token", sp.getValue("token"));
//                                        map.put("amount", money);
//                                        postData(map, "/product/investInit", PostType.SUBMIT, true);//根据产品id和金额生成支付信息
                                        map.put("token", sp.getValue("token"));
                                        map.put("ucenter", "2");//这里一直是存管账户
                                        postData(map, "/user/index", null, PostType.SUBMIT);//根据isAvailableCountReq直接定位到指定代码块中
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "0":
                                    RiskEvalutionDialog d = new RiskEvalutionDialog(ProductActivity.this, true, 0.8);
                                    String text = "五星一直坚持合法合规运营，稳定长期发展。为广大投资者创造安全可靠的投资平台。为响应监管机构64号文件，" +
                                            "明确要求权益持有人不得超过200人的要求。五星财富做出如下调整：\n1、 起投金额调整为5万元\n" +
                                            "为了满足投资人数不得超过200人的合规要求，决定调整项目起投金额为5万元。后续随着项目摘牌金额的变化，起投金额也会随之产生浮动。" +
                                            "\n2、 必须完成合格投资者问卷\n" +"即日起，每一位五星财富老用户需先填写完风险评估问卷后方可再进行投资。";
//                                    SpannableStringBuilder span;
//                                    if(TextUtils.isEmpty(msg))
//                                        span = new SpannableStringBuilder("" + text);
//                                    else
//                                        span = new SpannableStringBuilder("缩进"+msg);
//                                    span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 0,
//                                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                    if(TextUtils.isEmpty(msg))
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
                                            Intent i = new Intent(ProductActivity.this, WebViewRelease.class);
                                            i.putExtra("url", Config.getHttpConfig()+"/activity/toRiskQuery");
                                            i.putExtra("title", "合格投资者问卷");
                                            startActivity(i);
                                        }
                                    });
                                    break;
                                case "-4"://重新登录
                                    ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                                    sp.removeAll();
                                    Intent intent = new Intent(ProductActivity.this, Login.class);
                                    startActivity(intent);
                                    break;
                                default:break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ToastMakeText.showToast(ProductActivity.this,"服务器连接失败", 1000);
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {
                    }
                    @Override
                    public void onFinished() {
                        if (mDialog != null) {
                        mDialog.hideDialog();
                        d = null;
                    }
                    }
                    @Override
                    public boolean onCache(String result) {
                        return false;
                    }
                });
            }
            @Override
            public void callback() {}
        });
        mDialog.showDialog();
    }

    private void requestAddCgAccount() {
        String path = Config.getHttpConfig()+"/bank/member/realNameWeb";
        final RequestParams params = new RequestParams(path);
        params.addParameter("mobile", sp.getOtherValue("user0"));
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
                x.http().post(params, new Callback.CacheCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try{
                            JSONObject data = new JSONObject(arg0);
                            if(data.has("key") && TextUtils.equals("success",data.getString("key"))){
                                if(data.has("relationKey"))
                                    startActivityForResult(new Intent(context, WebViewRelease.class).
                                            putExtra("html", data.getString("relationKey"))
                                            .putExtra("title", "渤海存管账户开通"), UserCenter.REQUEST_ADD_CG);
                            }else if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
                                ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                                sp.removeAll();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                                finish();
                            }else{
                                ToastMakeText.showToast((Activity) context,data.getString("message"),2000);
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
                    @Override
                    public boolean onCache(String result) {
                        return false;
                    }
                });}
        });
        loadDialog.showDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10001) {
            if (resultCode == RESULT_OK) {//当手势密码验证成功
                Intent intent = new Intent(ProductActivity.this, Invest.class);
                intent.putExtra("productId", _productId);
                intent.putExtra("amount", money);
                startActivity(intent);
            }
        }else if(requestCode== UserCenter.REQUEST_ADD_CG && resultCode==UserCenter.RESULT_ADD_CG){
            //打开开通存管页面后的操作，刷新个人中心开通存管的状态
            refreshCgState = true;
            Map<String, Object> map = new HashMap<>();
            map.put("token", sp.getValue("token"));
            map.put("ucenter", "1");//这里一直是存管账户
            postData(map, "/user/index", null, PostType.SUBMIT);
        }else if(requestCode== REQ_INVEST_SUC && resultCode==RES_INVEST_SUC){
            //投资成功返回，刷新页面数据
            if (!TextUtils.isEmpty(_productId)) {
                type=0;
                Map<String, Object> map = new HashMap<>();
                map.put("productId", _productId);
                map.put("token",sp.getValue("token"));
                postData(map, "/product/detail", PostType.LOAD, true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置产品ID
     *
     * @param productId
     */
    public void setProductId(String productId) {
        _productId = productId;
    }

    /**
     * 获取产品ID
     *
     * @return
     */
    public String getProductId() {
        return _productId;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        ++_pageNum;
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData3();
            }
        }, 600);
    }
}
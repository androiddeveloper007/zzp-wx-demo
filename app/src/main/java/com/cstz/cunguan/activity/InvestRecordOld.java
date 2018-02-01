package com.cstz.cunguan.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.adapter.MyPagerAdapterCg;
import com.cstz.common.App;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.view.MySliderCg;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.fragment.ViewPagerItemView;
import com.lzy.widget.HeaderViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.widget.xlistview.XListView;

/**
 * 原账户投资记录
 */
public class InvestRecordOld extends MyActivity implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;

    MySliderCg slider_invest_record;
    ViewPager pager_invest_record;
    List<ViewPagerItemView> viewlist;
    ViewPagerItemView page1, page2, page3;
    private XListView lv0, lv1, lv2;
    private TextView tvInvesting;
    private TextView tvForPay;
    private TextView tvHasRepay;
    private Context context;
    private List<Map<String, String>> listGet0, listGetTotal0;
    private List<Map<String, String>> listGet1, listGetTotal1;
    private List<Map<String, String>> listGet2, listGetTotal2;
    private MyAdapter adapter,adapter1,adapter2;
    private int pageNum = 1;
    private int pageNum1 = 1;
    private int pageNum2 = 1;
    private final static int pageSize = 15;
    private int mScreenHeight; // 屏幕高度
    private HeaderViewPager scrollableLayout;
    private int currentYTemp = 0;
    private String deposit = "0";
    private int status = 0;
    boolean isPage1First = true;
    boolean isPage2First = true;
    private SharedPreferencesData sp;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_invest_record);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        titleTv.setText("原账户投资记录");
        titleBack.setVisibility(View.VISIBLE);
        context = this;
        sp = new SharedPreferencesData(context);
        mHandler = new Handler();
        mScreenHeight = DensityUtil.getScreenHeight();
        loadData(0, pageNum);//初始化数据
        initViewStub(false);
    }

    private void initViewStub(boolean noData) {
        if (noData) {
            ViewStub stub = (ViewStub) findViewById(R.id.view_invest_record0);
            stub.inflate();
        } else {
            ViewStub stub = (ViewStub) findViewById(R.id.view_invest_record1);
            stub.inflate();
            slider_invest_record = (MySliderCg) findViewById(R.id.slider_invest_record);
            pager_invest_record = (ViewPager) findViewById(R.id.pager_invest_record);
            scrollableLayout = (HeaderViewPager) findViewById(R.id.header_viewPager);
            tvInvesting = (TextView) findViewById(R.id.tv_invest_record1);
            tvForPay = (TextView) findViewById(R.id.tv_invest_record4);
            tvHasRepay = (TextView) findViewById(R.id.tv_invest_record5);
            initView();
        }
    }

    private void loadData(int status, int index) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("pageNum", index);
        map.put("pageSize", pageSize);
        map.put("deposit", deposit);
        map.put("token", sp.getValue("token"));
        postData(map, "/user/investRecord", null, App.PostType.LOAD);
    }

    private void initView() {
        slider_invest_record.setOnClickSlider(new MySliderCg.OnClickSlider() {
            @Override
            public void getIndex(int index) {
                pager_invest_record.setCurrentItem(index);
            }
        });
        viewlist = new ArrayList<>();
        page1 = (ViewPagerItemView) View.inflate(this, R.layout.funddetail_tab1, null);
        page1.setScrollableView((XListView) page1.findViewById(R.id.lv_funddetail_tab0));
        page2 = (ViewPagerItemView) View.inflate(this, R.layout.funddetail_tab1, null);
        page2.setScrollableView((XListView) page2.findViewById(R.id.lv_funddetail_tab0));
        page3 = (ViewPagerItemView) View.inflate(this, R.layout.funddetail_tab1, null);
        page3.setScrollableView((XListView) page3.findViewById(R.id.lv_funddetail_tab0));
        viewlist.add(page1);
        viewlist.add(page2);
        viewlist.add(page3);
        initPage0();
        initPage1();
        initPage2();
        pager_invest_record.setAdapter(new MyPagerAdapterCg(viewlist));
        pager_invest_record.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                slider_invest_record.setSelectIndex(position);
                scrollableLayout.setCurrentScrollableContainer(viewlist.get(position));
                if(position==0){
                    status = 0;
                }else if(position==1){
                    status = 1;
                    if(isPage1First){
                        isPage1First=false;
                        loadData(status,pageNum1);
                    }
                }else if(position==2){
                    status = -1;
                    if(isPage2First){
                        isPage2First=false;
                        loadData(status,pageNum2);
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        scrollableLayout.setOnScrollListener(new HeaderViewPager.OnScrollListener() {
            @Override
            public void onScroll(int currentY, int maxY) {
                if (currentY == 0) {
                    lv0.setPullRefreshEnable(true);
                    lv1.setPullRefreshEnable(true);
                    lv2.setPullRefreshEnable(true);
                } else {
                    if (!lv0.getIsPullRefreshing() && currentY < currentYTemp)
                        lv0.setPullRefreshEnable(false);
                    if (!lv1.getIsPullRefreshing() && currentY < currentYTemp)
                        lv1.setPullRefreshEnable(false);
                    if (!lv2.getIsPullRefreshing() && currentY < currentYTemp)
                        lv2.setPullRefreshEnable(false);
                }
                currentYTemp = currentY;
            }
        });
        pager_invest_record.setCurrentItem(0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(scrollableLayout!=null){
            scrollableLayout.setTopOffset(0);
            scrollableLayout.setCurrentScrollableContainer(viewlist.get(pager_invest_record.getCurrentItem()));
        }
    }

    private void initPage0() {
        if (lv0 == null) {
            lv0 = (XListView) page1.findViewById(R.id.lv_funddetail_tab0);
            lv0.setPullRefreshEnable(true);
            lv0.setPullLoadEnable(false);
            lv0.setAutoLoadEnable(true);
            lv0.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    lv0.setPullLoadEnable(false);
                    lv0.setAutoLoadEnable(true);
                    pageNum = 1;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listGet0.clear();
                            listGetTotal0.clear();
                            loadData(0, pageNum);
                        }
                    }, 300);
                }
                @Override
                public void onLoadMore() {
                    pageNum++;
                    loadData(0, pageNum);
                }
            });
            listGetTotal0 = new ArrayList<>();
            listGet0 = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            listGet0.add(map);
            adapter = new MyAdapter(listGet0,0);
            lv0.setAdapter(adapter);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_340);
            adapter.getNoDataEntity(height);
        }
    }

    private void initPage1() {
        if (lv1 == null) {
            lv1 = (XListView) page2.findViewById(R.id.lv_funddetail_tab0);
            lv1.setPullRefreshEnable(true);
            lv1.setPullLoadEnable(false);
            lv1.setAutoLoadEnable(true);
            lv1.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    lv1.setPullLoadEnable(false);
                    lv1.setAutoLoadEnable(true);
                    pageNum1 = 1;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listGet1.clear();
                            listGetTotal1.clear();
                            loadData(1, pageNum1);
                        }
                    }, 300);
                }
                @Override
                public void onLoadMore() {
                    pageNum1++;
                    loadData(1, pageNum1);
                }
            });
            listGetTotal1 = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            listGet1 = new ArrayList<>();
            listGet1.add(map);
            adapter1 = new MyAdapter(listGet1,1);
            lv1.setAdapter(adapter1);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_340);
            adapter1.getNoDataEntity(height);
        }
    }

    private void initPage2() {
        if (lv2 == null) {
            lv2 = (XListView) page3.findViewById(R.id.lv_funddetail_tab0);
            lv2.setPullRefreshEnable(true);
            lv2.setPullLoadEnable(false);
            lv2.setAutoLoadEnable(true);
            lv2.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    lv2.setPullLoadEnable(false);
                    lv2.setAutoLoadEnable(true);
                    pageNum2 = 1;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listGet2.clear();
                            listGetTotal2.clear();
                            loadData(-1, pageNum2);
                        }
                    }, 300);
                }
                @Override
                public void onLoadMore() {
                    pageNum2++;
                    loadData(-1, pageNum2);
                }
            });
            listGetTotal2 = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            listGet2 = new ArrayList<>();
            listGet2.add(map);
            adapter2 = new MyAdapter(listGet2,2);
            lv2.setAdapter(adapter2);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_340);
            adapter2.getNoDataEntity(height);
        }
    }

    @Override
    public void requestSuccess(JSONObject object, App.PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            try {
                JSONObject data = object.getJSONObject("data");
                JSONObject data2 = object.getJSONObject("data2");
                JSONArray array;
                if(data2!=null && status==0){
                    if(data2.has("forPayInterest"))
                        tvForPay.setText(data2.getString("forPayInterest"));
                    if(data2.has("forPayPrincipal"))
                        tvInvesting.setText(data2.getString("forPayPrincipal"));
                    if(data2.has("hasRePayInterest"))
                        tvHasRepay.setText(data2.getString("hasRePayInterest"));
                }
                if (data.has("page") && data.getJSONArray("page") != null) {
                    array = data.getJSONArray("page");
                    if (array != null) {
                        int length = array.length();
                        switch (status) {
                            case 0:
                                if (length == 0) {
                                    lv0.setPullLoadEnable(false);
                                    if(pageNum>1)
                                    lv0.setNoMoreData();
                                    return;
                                }
                                listGet0 = new ArrayList<>(array.length());
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject product = array.getJSONObject(i);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("productTitle", product.getString("productTitle"));
                                    map.put("repayTime", product.getString("repayTime"));
                                    map.put("interest", product.getString("interest"));
                                    map.put("pDeadline", product.getString("pDeadline"));
                                    map.put("investAmount", product.getString("investAmount"));
                                    map.put("investTime", product.getString("investTime"));
                                    map.put("annualRate", product.getString("annualRate"));
                                    map.put("deadline", product.getString("deadline"));
                                    map.put("status", product.getString("status"));//状态 2：投资中，3 已募满 4：兑付中 5：已完成
                                    if(product.has("keybd_code"))
                                        map.put("keybd_code", product.getString("keybd_code"));
                                    if(product.has("price_amount"))
                                        map.put("price_amount", product.getString("price_amount"));
                                    if(product.has("keybd_state"))
                                        map.put("keybd_state", product.getString("keybd_state"));
                                    listGet0.add(map);
                                }
                                if (length < pageSize)
                                    lv0.setNoMoreData();
                                else
                                    lv0.setPullLoadEnable(true);
                                if (adapter.isNoData) {
                                    adapter = new MyAdapter(listGet0,0);
                                    lv0.setAdapter(adapter);
                                    listGetTotal0.addAll(listGet0);
                                } else{
                                    listGetTotal0.addAll(listGet0);
                                    adapter.update(listGetTotal0);
                                }
                                break;
                            case 1:
                                if (length == 0) {
                                    lv1.setPullLoadEnable(false);
                                    if(pageNum1>1)
                                        lv1.setNoMoreData();
                                    return;
                                }
                                listGet1 = new ArrayList<>(length);
                                for (int i = 0; i < length; i++) {
                                    JSONObject product = array.getJSONObject(i);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("productTitle", product.getString("productTitle"));
                                    map.put("repayTime", product.getString("repayTime"));
                                    map.put("interest", product.getString("interest"));
                                    map.put("pDeadline", product.getString("pDeadline"));
                                    map.put("investAmount", product.getString("investAmount"));
                                    map.put("investTime", product.getString("investTime"));
                                    map.put("annualRate", product.getString("annualRate"));
                                    map.put("deadline", product.getString("deadline"));
                                    map.put("status", product.getString("status"));
                                    if(product.has("keybd_code"))
                                        map.put("keybd_code", product.getString("keybd_code"));
                                    if(product.has("price_amount"))
                                        map.put("price_amount", product.getString("price_amount"));
                                    if(product.has("keybd_state"))
                                        map.put("keybd_state", product.getString("keybd_state"));
                                    listGet1.add(map);
                                }
                                if (length < pageSize) {
                                    lv1.setNoMoreData();
                                } else {
                                    lv1.setPullLoadEnable(true);
                                }
                                if (adapter1.isNoData) {
                                    adapter1 = new MyAdapter(listGet1,1);
                                    lv1.setAdapter(adapter1);
                                    listGetTotal1.addAll(listGet1);
                                } else{
                                    listGetTotal1.addAll(listGet1);
                                    adapter1.update(listGetTotal1);
                                }
                                break;
                            case -1:
                                if (length == 0) {
                                    lv2.setPullLoadEnable(false);
                                    if(pageNum2>1)
                                        lv2.setNoMoreData();
                                    return;
                                }
                                listGet2 = new ArrayList<>(length);
                                for (int i = 0; i < length; i++) {
                                    JSONObject product = array.getJSONObject(i);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("productTitle", product.getString("productTitle"));
                                    map.put("repayTime", product.getString("repayTime"));
                                    map.put("interest", product.getString("interest"));
                                    map.put("pDeadline", product.getString("pDeadline"));
                                    map.put("investAmount", product.getString("investAmount"));
                                    map.put("investTime", product.getString("investTime"));
                                    map.put("annualRate", product.getString("annualRate"));
                                    map.put("deadline", product.getString("deadline"));
                                    map.put("status", product.getString("status"));
                                    if(product.has("keybd_code"))
                                        map.put("keybd_code", product.getString("keybd_code"));
                                    if(product.has("price_amount"))
                                        map.put("price_amount", product.getString("price_amount"));
                                    if(product.has("keybd_state"))
                                        map.put("keybd_state", product.getString("keybd_state"));
                                    listGet2.add(map);
                                }
                                if (length < pageSize) {
                                    lv2.setNoMoreData();
                                } else {
                                    lv2.setPullLoadEnable(true);
                                }
                                if (adapter2.isNoData) {
                                    adapter2 = new MyAdapter(listGet2,2);
                                    lv2.setAdapter(adapter2);
                                    listGetTotal2.addAll(listGet2);
                                } else{
                                    listGetTotal2.addAll(listGet2);
                                    adapter2.update(listGetTotal2);
                                }
                                break;
                        }
                    } else {
                        switch (status) {
                            case 0:
                                lv0.setPullLoadEnable(false);
                                break;
                            case 1:
                                lv1.setPullLoadEnable(false);
                                break;
                            case -1:
                                lv2.setPullLoadEnable(false);
                                break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void requestFinished() {
        super.requestFinished();
        switch (status) {
            case 0:
                lv0.stopRefresh();
                lv0.stopLoadMore();
                break;
            case 1:
                lv1.stopRefresh();
                lv1.stopLoadMore();
                break;
            case -1:
                lv2.stopRefresh();
                lv2.stopLoadMore();
                break;
        }
    }

    @Override
    public void requestFail(String msg, JSONObject obj, App.PostType postType) {
        super.requestFail(msg, obj, postType);
    }

    @OnClick({R.id.title_back, R.id.iv_navigation_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            default:break;
        }
    }

    class MyAdapter extends BaseAdapter {
        private List<Map<String, String>> list = new ArrayList<>();
        private boolean isNoData;
        private int height;
        private int type;
        protected LayoutInflater mInflater;
        public MyAdapter(List<Map<String, String>> listget,int type) {
            this.list = listget;
            this.type = type;
            mInflater = LayoutInflater.from(context);
        }
        // 暂无数据
        public void getNoDataEntity(int height) {
            List<Map<String, String>> list = new ArrayList<>();
            Map<String, String> entity = new HashMap<>();
            this.isNoData = true;
            this.height = height;
            list.add(entity);
            update(list);
        }

        public void update(List<Map<String, String>> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (isNoData) {
                convertView = mInflater.inflate(R.layout.item_no_data_layout, null);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_root_view);
//                ImageView iv = ButterKnife.findById(convertView, R.id.iv_placeholder_image);
//                iv.setImageResource(R.mipmap.invest_record_nodata);
                TextView tv = ButterKnife.findById(convertView, R.id.tv_nodata);
                switch (type){
                    case 0:tv.setText("暂无数据");break;
                    case 1:tv.setText("暂无收益中数据");break;
                    case 2:tv.setText("暂无已兑付数据");break;
                }
                rootView.setLayoutParams(params);
                return convertView;
            }
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_invest_record, parent, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tvInvestRecordTitleSub.setText(list.get(position).get("productTitle"));
            holder.tvInvestRecordEarning.setText("投资时间："+list.get(position).get("investTime"));
            holder.tvInvestRecordEndTime.setText("兑付日期："+list.get(position).get("repayTime"));
            holder.tvInvestRecordEarning.setText("预计收益："+list.get(position).get("interest"));
            holder.tvInvestRecordDays.setText("投资"+list.get(position).get("deadline")+"天");
            holder.tvInvestRecordMoney.setText("投资金额："+list.get(position).get("investAmount"));
            holder.tvInvestRecordStyle.setText("年化"+list.get(position).get("annualRate")+"%");
            switch(list.get(position).get("status")){
                case "2":
                    holder.tvInvestRecordStatus.setText("投资中");break;
                case "3":
                    holder.tvInvestRecordStatus.setText("已募满");break;
                case "4":
                    holder.tvInvestRecordStatus.setText("兑付中");break;
                case "5":
                    holder.tvInvestRecordStatus.setText("已完成");break;
                default:break;
            }
            switch(list.get(position).get("pDeadline")){
                case "33":
                    holder.tvInvestRecordTitle.setText("五星月享（33天）");break;
                case "93":
                    holder.tvInvestRecordTitle.setText("五星季享（93天）");break;
                case "183":
                    holder.tvInvestRecordTitle.setText("五星双季享（183天）");break;
                default:break;
            }
            if(list.get(position).get("keybd_code")==null || TextUtils.isEmpty(list.get(position).get("keybd_code").toString())){//没有Kcode字段，就隐藏那块布局
                holder.rl_keyboard_invest_record.setVisibility(View.GONE);
            }else{
                holder.rl_keyboard_invest_record.setVisibility(View.VISIBLE);
                holder.tv_code_used.setText(list.get(position).get("keybd_code").toString());
                holder.tv_code_fanxian.setText(list.get(position).get("price_amount").toString());
                String keybd_state = list.get(position).get("keybd_state").toString();
                switch (keybd_state){
                    case "2":holder.tv_code_result_tip.setText("项目兑付时返现");break;
                    case "3":holder.tv_code_result_tip.setText("已返现");break;
                    case "4":holder.tv_code_result_tip.setText("键盘已退货，K码已失效");break;
                    default:break;
                }
            }
            return convertView;
        }
        @Override
        public boolean isEnabled(int position) {
            return false;
        }
        class Holder {
            @BindView(R.id.tv_invest_record_title)
            TextView tvInvestRecordTitle;
            @BindView(R.id.tv_invest_record_title_sub)
            TextView tvInvestRecordTitleSub;
            @BindView(R.id.tv_invest_record_style)
            TextView tvInvestRecordStyle;
            @BindView(R.id.tv_invest_record_days)
            TextView tvInvestRecordDays;
            @BindView(R.id.tv_invest_record_money)
            TextView tvInvestRecordMoney;
            @BindView(R.id.tv_invest_record_time)
            TextView tvInvestRecordTime;
            @BindView(R.id.tv_invest_record_earning)
            TextView tvInvestRecordEarning;
            @BindView(R.id.tv_invest_record_endTime)
            TextView tvInvestRecordEndTime;
            @BindView(R.id.tv_invest_record_status)
            TextView tvInvestRecordStatus;
            TextView tv_code_used;  //已使用K码
            @BindView(R.id.tv_code_fanxian)
            TextView tv_code_fanxian;  //返现
            @BindView(R.id.tv_code_result_tip)
            TextView tv_code_result_tip;  //结果提示
            @BindView(R.id.rl_keyboard_invest_record)
            RelativeLayout rl_keyboard_invest_record;  //布局块
            Holder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
    @Override
    protected void onDestroy() {
        sp=null;
        mHandler=null;
        super.onDestroy();
    }
}
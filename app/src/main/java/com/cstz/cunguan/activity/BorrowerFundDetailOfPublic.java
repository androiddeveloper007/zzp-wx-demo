package com.cstz.cunguan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

import com.cstz.adapter.MyPagerAdapterBorrow;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.fragment.ViewPagerItemView;
import com.cstz.front.Login;
import com.cstz.tools.ToastMakeText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.widget.PagerSlidingTabStrip;
import library.widget.xlistview.XListView;

/**
 * 借款人资金明细对公账户
 */
public class BorrowerFundDetailOfPublic extends MyActivity implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
//    MySliderCg slider_repayment;
    PagerSlidingTabStrip slider_repayment;
    ViewPager pager_repayment;
    List<ViewPagerItemView> viewlist;
    ViewPagerItemView page1, page2, page3, page4,page5;
    private XListView lv0, lv1, lv2, lv3,lv4;
    private Context context;
    private List<Map<String, String>> listget0, listgetTotal0, listget1,
            listgetTotal1, listget2, listgetTotal2, listget3, listgetTotal3, listget4, listgetTotal4;
    private MyAdapter adapter, adapter1, adapter2, adapter3, adapter4;
    private int pageNum = 1, pageNum1 = 1, pageNum2 = 1, pageNum3 = 1, pageNum4 = 1;
    private final static int pageSize = 15;
    private int mScreenHeight; // 屏幕高度
    private SharedPreferencesData sp;
    private Handler mHandler;
    private int type = 0;//0:全部,2:充值,3:提现,5:还款 ,6:放款 全部,充值,放款,还款,提现02653
    boolean isPage1First = true, isPage2First = true, isPage3First = true, isPage4First = true;
    private TextView tv_fund_detail1,tv_fund_detail4,tv_fund_detail5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_borrower_fund_detail_public);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        titleTv.setText("资金明细");
        titleBack.setVisibility(View.VISIBLE);
        context = this;
        mHandler = new Handler();
        sp = new SharedPreferencesData(this);
        mScreenHeight = DensityUtil.getScreenHeight();
        requestBorrowerFundList(pageNum);
        initViewStub(false);
    }

    private void initViewStub(boolean b) {
        if (b) {
            ViewStub stub = (ViewStub) findViewById(R.id.view_borrow_fund0);
            stub.inflate();
        } else {
            ViewStub stub = (ViewStub) findViewById(R.id.view_borrow_fund1);
            stub.inflate();
//            slider_repayment = (MySliderCg) findViewById(R.id.slider_borrower_fund_detail);
            slider_repayment = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            pager_repayment = (ViewPager) findViewById(R.id.pager_borrower_fund_detail);
            tv_fund_detail1 = (TextView) findViewById(R.id.tv_fund_detail1);
            tv_fund_detail4 = (TextView) findViewById(R.id.tv_fund_detail4);
            tv_fund_detail5 = (TextView) findViewById(R.id.tv_fund_detail5);
            initView();
        }
    }

    private void initView() {
        /*slider_repayment.setOnClickSlider(new MySliderCg.OnClickSlider() {
            @Override
            public void getIndex(int index) {
                pager_repayment.setCurrentItem(index);
            }
        });*/
        viewlist = new ArrayList<>();
        page1 = (ViewPagerItemView)View.inflate(this, R.layout.funddetail_tab1, null);
        page2 = (ViewPagerItemView)View.inflate(this, R.layout.funddetail_tab1, null);
        page3 = (ViewPagerItemView)View.inflate(this, R.layout.funddetail_tab1, null);
        page4 = (ViewPagerItemView)View.inflate(this, R.layout.funddetail_tab1, null);
        page5 = (ViewPagerItemView)View.inflate(this, R.layout.funddetail_tab1, null);
        viewlist.add(page1);
        viewlist.add(page2);
        viewlist.add(page3);
        viewlist.add(page4);
        viewlist.add(page5);
        initPage0();
        initPage1();
        initPage2();
        initPage3();
        initPage4();
        pager_repayment.setAdapter(new MyPagerAdapterBorrow(viewlist));
        pager_repayment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
//                slider_repayment.setSelectIndex(position);
                if (position == 0) {//全部,充值,放款,还款,提现
                    type = 0;
                } else if (position == 1) {
                    type = 2;
                    if (isPage1First) {
                        isPage1First = false;
                        requestBorrowerFundList(pageNum1);
                    }
                } else if (position == 2) {
                    type = 6;
                    if (isPage2First) {
                        isPage2First = false;
                        requestBorrowerFundList(pageNum2);
                    }
                } else if (position == 3) {
                    type = 5;
                    if (isPage3First) {
                        isPage3First = false;
                        requestBorrowerFundList(pageNum3);
                    }
                } else if(position == 4){
                    type = 3;
                    if (isPage4First) {
                        isPage4First = false;
                        requestBorrowerFundList(pageNum4);
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        slider_repayment.setViewPager(pager_repayment);
        pager_repayment.setCurrentItem(0);
    }

    private void requestBorrowerFundList(int index) {
        String path = Config.getHttpConfig() + "/user/fundRecord";
        final RequestParams params = new RequestParams(path);
        params.addParameter("pageNum", index);
        params.addParameter("pageSize", pageSize);
        params.addParameter("fundType", type);//0:全部  2:充值 3:提现  5:还款
        params.addParameter("deposit", "1");//1：存管账户
        params.addParameter("token", sp.getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    JSONObject data = object.getJSONObject("data");
                    JSONObject data2 = object.getJSONObject("data2");
                    JSONArray array;
                    if (data.has("page") && data.getJSONArray("page") != null) {
                        array = data.getJSONArray("page");
                        if (array != null) {
                            int length = array.length();
                            switch (type) {
                                case 0:
                                    if (data2 != null) {
//                                        if (data2.has("accountSum"))
//                                            tv_fund_detail1.setText(data2.getString("accountSum"));
//                                        if (data2.has("usableSum"))
//                                            tv_fund_detail4.setText(data2.getString("usableSum"));
//                                        if (data2.has("freezeSum"))
//                                            tv_fund_detail5.setText(data2.getString("freezeSum"));
                                    }
                                    if (length == 0) {
                                        lv0.setPullLoadEnable(false);
                                        if(pageNum>1)
                                            lv0.setNoMoreData();
                                        return;
                                    }
                                    listget0 = new ArrayList<>(array.length());
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject product = array.getJSONObject(i);
                                        Map<String, String> map = new HashMap<>();
                                        map.put("title", product.getString("fundMode"));
                                        map.put("text", product.getString("remarks"));
                                        map.put("time", product.getString("recordTime"));
                                        listget0.add(map);
                                    }
                                    if (length < pageSize) {
                                        lv0.setNoMoreData();
                                    }
                                    else
                                        lv0.setPullLoadEnable(true);
                                    if (adapter.isNoData) {
                                        adapter = new MyAdapter(listget0,0);
                                        lv0.setAdapter(adapter);
                                        listgetTotal0.addAll(listget0);
                                    } else {
                                        listgetTotal0.addAll(listget0);
                                        adapter.update(listgetTotal0);
                                    }
                                    break;
                                case 2:
                                    if (length == 0) {
                                        lv1.setPullLoadEnable(false);
                                        if(pageNum1>1)
                                            lv1.setNoMoreData();
                                        return;
                                    }
                                    listget1 = new ArrayList<>(length);
                                    for (int i = 0; i < length; i++) {
                                        JSONObject product = array.getJSONObject(i);
                                        Map<String, String> map = new HashMap<>();
                                        map.put("title", product.getString("fundMode"));
                                        map.put("text", product.getString("remarks"));
                                        map.put("time", product.getString("recordTime"));
                                        listget1.add(map);
                                    }
                                    if (length < pageSize) {
                                        lv1.setNoMoreData();
                                    } else {
                                        lv1.setPullLoadEnable(true);
                                    }
                                    if (adapter1.isNoData) {
                                        adapter1 = new MyAdapter(listget1,1);
                                        lv1.setAdapter(adapter1);
                                        listgetTotal1.addAll(listget1);
                                    } else {
                                        listgetTotal1.addAll(listget1);
                                        adapter1.update(listgetTotal1);
                                    }
                                    break;
                                case 6:
                                    if (length == 0) {
                                        if(pageNum2>1)
                                            lv2.setNoMoreData();
                                        return;
                                    }
                                    listget2 = new ArrayList<>(length);
                                    for (int i = 0; i < length; i++) {
                                        JSONObject product = array.getJSONObject(i);
                                        Map<String, String> map = new HashMap<>();
                                        map.put("title", product.getString("fundMode"));
                                        map.put("text", product.getString("remarks"));
                                        map.put("time", product.getString("recordTime"));
                                        listget2.add(map);
                                    }
                                    if (length < pageSize) {
                                        lv2.setNoMoreData();
                                    } else {
                                        lv2.setPullLoadEnable(true);
                                    }
                                    if (adapter2.isNoData) {
                                        adapter2 = new MyAdapter(listget2,2);
                                        lv2.setAdapter(adapter2);
                                        listgetTotal2.addAll(listget2);
                                    } else {
                                        listgetTotal2.addAll(listget2);
                                        adapter2.update(listgetTotal2);
                                    }
                                    break;
                                case 5:
                                    if (length == 0) {
                                        if(pageNum3>1)
                                            lv3.setNoMoreData();
                                        return;
                                    }
                                    listget3 = new ArrayList<>(length);
                                    for (int i = 0; i < length; i++) {
                                        JSONObject product = array.getJSONObject(i);
                                        Map<String, String> map = new HashMap<>();
                                        map.put("title", product.getString("fundMode"));
                                        map.put("text", product.getString("remarks"));
                                        map.put("time", product.getString("recordTime"));
                                        listget3.add(map);
                                    }
                                    if (length < pageSize) {
                                        lv3.setNoMoreData();
                                    } else {
                                        lv3.setPullLoadEnable(true);
                                    }
                                    if (adapter3.isNoData) {
                                        adapter3 = new MyAdapter(listget3,3);
                                        lv3.setAdapter(adapter3);
                                        listgetTotal3.addAll(listget3);
                                    } else {
                                        listgetTotal3.addAll(listget3);
                                        adapter3.update(listgetTotal3);
                                    }
                                    break;
                                case 3:
                                    if (length == 0) {
                                        if(pageNum4>1)
                                            lv4.setNoMoreData();
                                        return;
                                    }
                                    listget4 = new ArrayList<>(length);
                                    for (int i = 0; i < length; i++) {
                                        JSONObject product = array.getJSONObject(i);
                                        Map<String, String> map = new HashMap<>();
                                        map.put("title", product.getString("fundMode"));
                                        map.put("text", product.getString("remarks"));
                                        map.put("time", product.getString("recordTime"));
                                        listget4.add(map);
                                    }
                                    if (length < pageSize) {
                                        lv4.setNoMoreData();
                                    } else {
                                        lv4.setPullLoadEnable(true);
                                    }
                                    if (adapter4.isNoData) {
                                        adapter4 = new MyAdapter(listget4,4);
                                        lv4.setAdapter(adapter4);
                                        listgetTotal4.addAll(listget4);
                                    } else {
                                        listgetTotal4.addAll(listget4);
                                        adapter4.update(listgetTotal4);
                                    }
                                    break;
                            }
                        }
                    }else if(data.has("result")&& TextUtils.equals("-4",data.getString("result"))){
                        ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                        sp.removeAll();
                        Intent intent = new Intent(context, Login.class);
                        startActivity(intent);
                        finish();
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
                switch (type) {
                    case 0:
                        lv0.stopRefresh();
                        lv0.stopLoadMore();
                        break;
                    case 2:
                        lv1.stopRefresh();
                        lv1.stopLoadMore();
                        break;
                    case 6:
                        lv2.stopRefresh();
                        lv2.stopLoadMore();
                        break;
                    case 5:
                        lv3.stopRefresh();
                        lv3.stopLoadMore();
                        break;
                    case 3:
                        lv4.stopRefresh();
                        lv4.stopLoadMore();
                        break;
                }
            }
            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
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
                            listget0.clear();
                            listgetTotal0.clear();
                            requestBorrowerFundList(pageNum);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum++;
                    requestBorrowerFundList(pageNum);
                }
            });
            Map<String, String> map = new HashMap<>();
            listgetTotal0 = new ArrayList<>();
            listget0 = new ArrayList<>();
            listget0.add(map);
            adapter = new MyAdapter(listget0,0);
            lv0.setAdapter(adapter);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_180);//340
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
                            listget1.clear();
                            listgetTotal1.clear();
                            requestBorrowerFundList(pageNum1);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum1++;
                    requestBorrowerFundList(pageNum1);
                }
            });
            Map<String, String> map = new HashMap<>();
            listgetTotal1 = new ArrayList<>();
            listget1 = new ArrayList<>();
            listget1.add(map);
            adapter1 = new MyAdapter(listget1,1);
            lv1.setAdapter(adapter1);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_180);
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
                            listget2.clear();
                            listgetTotal2.clear();
                            requestBorrowerFundList(pageNum2);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum2++;
                    requestBorrowerFundList(pageNum2);
                }
            });
            Map<String, String> map = new HashMap<>();
            listget2 = new ArrayList<>();
            listgetTotal2 = new ArrayList<>();
            listget2.add(map);
            adapter2 = new MyAdapter(listget2,2);
            lv2.setAdapter(adapter2);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_180);
            adapter2.getNoDataEntity(height);
        }
    }

    private void initPage3() {
        if (lv3 == null) {
            lv3 = (XListView) page4.findViewById(R.id.lv_funddetail_tab0);
            lv3.setPullRefreshEnable(true);
            lv3.setPullLoadEnable(false);
            lv3.setAutoLoadEnable(true);
            lv3.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    lv3.setPullLoadEnable(false);
                    lv3.setAutoLoadEnable(true);
                    pageNum3 = 1;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listget3.clear();
                            listgetTotal3.clear();
                            requestBorrowerFundList(pageNum3);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum3++;
                    requestBorrowerFundList(pageNum3);
                }
            });
            Map<String, String> map = new HashMap<>();
            listgetTotal3 = new ArrayList<>();
            listget3 = new ArrayList<>();
            listget3.add(map);
            adapter3 = new MyAdapter(listget3,3);
            lv3.setAdapter(adapter3);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_180);
            adapter3.getNoDataEntity(height);
        }
    }

    private void initPage4() {
        if (lv4 == null) {
            lv4 = (XListView) page5.findViewById(R.id.lv_funddetail_tab0);
            lv4.setPullRefreshEnable(true);
            lv4.setPullLoadEnable(false);
            lv4.setAutoLoadEnable(true);
            lv4.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    lv4.setPullLoadEnable(false);
                    lv4.setAutoLoadEnable(true);
                    pageNum4 = 1;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listget4.clear();
                            listgetTotal4.clear();
                            requestBorrowerFundList(pageNum4);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum4++;
                    requestBorrowerFundList(pageNum4);
                }
            });
            Map<String, String> map = new HashMap<>();
            listgetTotal4 = new ArrayList<>();
            listget4 = new ArrayList<>();
            listget4.add(map);
            adapter4 = new MyAdapter(listget4,4);
            lv4.setAdapter(adapter4);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_180);
            adapter4.getNoDataEntity(height);
        }
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
                TextView tv = ButterKnife.findById(convertView, R.id.tv_nodata);
                switch (type){
                    case 0:tv.setText("暂无数据");break;
                    case 1:tv.setText("暂无充值数据");break;
                    case 2:tv.setText("暂无放款数据");break;
                    case 3:tv.setText("暂无还款数据");break;
                    case 4:tv.setText("暂无提现数据");break;
                }
                rootView.setLayoutParams(params);
                return convertView;
            }
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_fund_detail, parent, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tv_fund_title.setText(list.get(position).get("title"));
            holder.tv_fund_text.setText(list.get(position).get("text"));
            holder.tv_fund_time.setText("时间："+list.get(position).get("time"));
            return convertView;
        }
        @Override
        public boolean isEnabled(int position) {
            if(Build.VERSION.SDK_INT > 16)
                return false;
            else
                return true;
        }
        class Holder {
            @BindView(R.id.tv_fund_title)
            TextView tv_fund_title;
            @BindView(R.id.tv_fund_text)
            TextView tv_fund_text;
            @BindView(R.id.tv_fund_time)
            TextView tv_fund_time;
            Holder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
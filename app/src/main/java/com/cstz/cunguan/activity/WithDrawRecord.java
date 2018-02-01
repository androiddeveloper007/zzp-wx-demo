package com.cstz.cunguan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.adapter.MyPagerAdapter;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.view.MySliderCg;
import com.cstz.cstz_android.R;
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

/**
 * 提现记录
 */
public class WithDrawRecord extends MyActivity implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.slider_withdraw_record)
    MySliderCg slider_withdraw_record;
    @BindView(R.id.pager_withdraw_record)
    ViewPager pager_withdraw_record;

    List<View> viewlist;
    View page0,page1,page2,page3;
    private library.widget.xlistview.XListView lv0,lv1,lv2,lv3;
    private Context context;
    private Handler mHandler;
    private List<Map<String, String>> listget0, listgetTotal0,listget1, listgetTotal1,listget2, listgetTotal2,listget3, listgetTotal3;
    private MyAdapter adapter,adapter1,adapter2,adapter3;
    private int pageNum = 1,pageNum1 = 1,pageNum2 = 1,pageNum3 = 1;
    private final static int pageSize=15;
    private int mScreenHeight; // 屏幕高度
    private int type = 0;
    boolean isPage1First = true, isPage2First = true, isPage3First = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_withdraw_record);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        titleTv.setText("提现记录");
        titleBack.setVisibility(View.VISIBLE);
        context = this;
        mHandler = new Handler();
        mScreenHeight = DensityUtil.getScreenHeight();
        initView();
        initData(pageNum,type);
    }

    private void initView() {
        listgetTotal0 = new ArrayList<>();
        listgetTotal1 = new ArrayList<>();
        listgetTotal2 = new ArrayList<>();
        listgetTotal3 = new ArrayList<>();
        slider_withdraw_record.setOnClickSlider(new MySliderCg.OnClickSlider() {
            @Override
            public void getIndex(int index) {
                pager_withdraw_record.setCurrentItem(index);
            }
        });
        viewlist = new ArrayList<>();
        page0 = View.inflate(this, R.layout.funddetail_tab1, null);
        page1 = View.inflate(this, R.layout.funddetail_tab1, null);
        page2 = View.inflate(this, R.layout.funddetail_tab1, null);
        page3 = View.inflate(this, R.layout.funddetail_tab1, null);
        viewlist.add(page0);
        viewlist.add(page1);
        viewlist.add(page2);
        viewlist.add(page3);
        initPage0();
        initPage1();
        initPage2();
        initPage3();
        pager_withdraw_record.setAdapter(new MyPagerAdapter(viewlist));
        pager_withdraw_record.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                slider_withdraw_record.setSelectIndex(position);
                if (position == 0) {
                    type = 0;
                } else if (position == 1) {
                    type = 3;
                    if (isPage1First) {
                        isPage1First = false;
                        initData(pageNum1, type);
                    }
                } else if (position == 2) {
                    type = 1;
                    if (isPage2First) {
                        isPage2First = false;
                        initData(pageNum2, type);
                    }
                } else if (position == 3) {
                    type = 4;
                    if (isPage3First) {
                        isPage3First = false;
                        initData(pageNum3, type);
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void initPage0() {
        if (lv0 == null) {
            lv0 = (library.widget.xlistview.XListView) page0.findViewById(R.id.lv_funddetail_tab0);
            lv0.setPullRefreshEnable(true);
            lv0.setPullLoadEnable(false);
            lv0.setAutoLoadEnable(true);
            lv0.setXListViewListener(new library.widget.xlistview.XListView.IXListViewListener() {
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
                            initData(pageNum,type);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum++;
                    initData(pageNum,type);
                }
            });
            Map<String, String> map = new HashMap<>();
            listget0 = new ArrayList<>();
            listgetTotal0 = new ArrayList<>();
            listget0.add(map);
            adapter = new MyAdapter(listget0,0);
            lv0.setAdapter(adapter);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_220);
            adapter.getNoDataEntity(height);
        }
    }

    private void initPage1() {
        if (lv1 == null) {
            lv1 = (library.widget.xlistview.XListView) page1.findViewById(R.id.lv_funddetail_tab0);
            lv1.setPullRefreshEnable(true);
            lv1.setPullLoadEnable(false);
            lv1.setAutoLoadEnable(true);
            lv1.setXListViewListener(new library.widget.xlistview.XListView.IXListViewListener() {
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
                            initData(pageNum1,type);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum1++;
                    initData(pageNum1,type);
                }
            });
            Map<String, String> map = new HashMap<>();
            listget1 = new ArrayList<>();
            listgetTotal1 = new ArrayList<>();
            listget1.add(map);
            adapter1 = new MyAdapter(listget1,1);
            lv1.setAdapter(adapter1);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_220);
            adapter1.getNoDataEntity(height);
        }
    }

    private void initPage2() {
        if (lv2 == null) {
            lv2 = (library.widget.xlistview.XListView) page2.findViewById(R.id.lv_funddetail_tab0);
            lv2.setPullRefreshEnable(true);
            lv2.setPullLoadEnable(false);
            lv2.setAutoLoadEnable(true);
            lv2.setXListViewListener(new library.widget.xlistview.XListView.IXListViewListener() {
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
                            initData(pageNum2,type);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum2++;
                    initData(pageNum2,type);
                }
            });
            Map<String, String> map = new HashMap<>();
            listget2 = new ArrayList<>();
            listgetTotal2 = new ArrayList<>();
            listget2.add(map);
            adapter2 = new MyAdapter(listget2,2);
            lv2.setAdapter(adapter2);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_220);
            adapter2.getNoDataEntity(height);
        }
    }

    private void initPage3() {
        if (lv3 == null) {
            lv3 = (library.widget.xlistview.XListView) page3.findViewById(R.id.lv_funddetail_tab0);
            lv3.setPullRefreshEnable(true);
            lv3.setPullLoadEnable(false);
            lv3.setAutoLoadEnable(true);
            lv3.setXListViewListener(new library.widget.xlistview.XListView.IXListViewListener() {
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
                            initData(pageNum3,type);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum3++;
                    initData(pageNum3,type);
                }
            });
            Map<String, String> map = new HashMap<>();
            listget3 = new ArrayList<>();
            listgetTotal3 = new ArrayList<>();
            listget3.add(map);
            adapter3 = new MyAdapter(listget3,3);
            lv3.setAdapter(adapter3);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_220);
            adapter3.getNoDataEntity(height);
        }
    }

    private void initData(int index, int type0) {
        String path = Config.getHttpConfig() + "/user/queryWithdrawList";
        final RequestParams params = new RequestParams(path);
        params.addParameter("pageSize", pageSize);
        params.addParameter("pageNum", index);
        params.addParameter("isDeposit", "1");
        params.addParameter("fundtype", type0);//0全部，3转账中+审核中，1已提现 ，4失败
        params.addParameter("token", new SharedPreferencesData(context).getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject data = new JSONObject(arg0);
                    String result = data.getString("result");
                    if (TextUtils.equals("1", result)) {
                        if (data.has("data")) {
                            JSONArray array = data.getJSONArray("data");
                            int length = array.length();
                            switch (type){
                                case 0:
                                    if (length == 0) {
                                        lv0.setPullLoadEnable(false);
                                        if(pageNum>1)
                                            lv0.setNoMoreData();
                                        return;
                                    }
                                    listget0 = new ArrayList<>(array.length());
                                    for (int i = 0; i < length; i++) {
                                        JSONObject record = array.getJSONObject(i);
                                        Map<String, String> map = new HashMap<>();
                                        map.put("bankName", record.getString("bankName"));
                                        map.put("acount", record.getString("acount"));
                                        map.put("sum", record.getString("sum"));
                                        if(record.has("poundage"))
                                            map.put("poundage", record.getString("poundage"));
                                        map.put("applyTime", record.getString("applyTime"));
                                        map.put("status", record.getString("status"));//0全部，3转账中+审核中 ，1成功，4失败
                                        listget0.add(map);
                                    }
                                    if (length < pageSize)
                                        lv0.setNoMoreData();
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
                                case 3:
                                    if (length == 0) {
                                        lv1.setPullLoadEnable(false);
                                        if(pageNum1>1)
                                            lv1.setNoMoreData();
                                        return;
                                    }
                                    listget1 = new ArrayList<>(array.length());
                                    for (int i = 0; i < length; i++) {
                                        JSONObject record = array.getJSONObject(i);
                                        Map<String, String> map = new HashMap<>();
                                        map.put("bankName", record.getString("bankName"));
                                        map.put("acount", record.getString("acount"));
                                        map.put("sum", record.getString("sum"));
                                        if(record.has("poundage"))
                                            map.put("poundage", record.getString("poundage"));
                                        map.put("applyTime", record.getString("applyTime"));
                                        map.put("status", record.getString("status"));//status:1 审核中  2 已提现 ，4转账中 5 失败
                                        listget1.add(map);
                                    }
                                    if (length < pageSize)
                                        lv1.setNoMoreData();
                                    else
                                        lv1.setPullLoadEnable(true);
                                    if (adapter1.isNoData) {
                                        adapter1 = new MyAdapter(listget1,1);
                                        lv1.setAdapter(adapter1);
                                        listgetTotal1.addAll(listget1);
                                    } else {
                                        listgetTotal1.addAll(listget1);
                                        adapter1.update(listgetTotal1);
                                    }
                                    break;
                                case 1:
                                    if (length == 0) {
                                        lv2.setPullLoadEnable(false);
                                        if(pageNum2>1)
                                            lv2.setNoMoreData();
                                        return;
                                    }
                                    listget2 = new ArrayList<>(array.length());
                                    for (int i = 0; i < length; i++) {
                                        JSONObject record = array.getJSONObject(i);
                                        Map<String, String> map = new HashMap<>();
                                        map.put("bankName", record.getString("bankName"));
                                        map.put("acount", record.getString("acount"));
                                        map.put("sum", record.getString("sum"));
                                        if(record.has("poundage"))
                                            map.put("poundage", record.getString("poundage"));
                                        map.put("applyTime", record.getString("applyTime"));
                                        map.put("status", record.getString("status"));
                                        listget2.add(map);
                                    }
                                    if (length < pageSize)
                                        lv2.setNoMoreData();
                                    else
                                        lv2.setPullLoadEnable(true);
                                    if (adapter2.isNoData) {
                                        adapter2 = new MyAdapter(listget2,2);
                                        lv2.setAdapter(adapter2);
                                        listgetTotal2.addAll(listget2);
                                    } else {
                                        listgetTotal2.addAll(listget2);
                                        adapter2.update(listgetTotal2);
                                    }
                                    break;
                                case 4:
                                    if (length == 0) {
                                        lv3.setPullLoadEnable(false);
                                        if(pageNum3>1)
                                            lv3.setNoMoreData();
                                        return;
                                    }
                                    listget3 = new ArrayList<>(array.length());
                                    for (int i = 0; i < length; i++) {
                                        JSONObject record = array.getJSONObject(i);
                                        Map<String, String> map = new HashMap<>();
                                        map.put("bankName", record.getString("bankName"));
                                        map.put("acount", record.getString("acount"));
                                        map.put("sum", record.getString("sum"));
                                        if(record.has("poundage"))
                                            map.put("poundage", record.getString("poundage"));
                                        map.put("applyTime", record.getString("applyTime"));
                                        map.put("status", record.getString("status"));//status:1 审核中  2 已提现 ，4转账中 5 失败
                                        listget3.add(map);
                                    }
                                    if (length < pageSize)
                                        lv3.setNoMoreData();
                                    else
                                        lv3.setPullLoadEnable(true);
                                    if (adapter3.isNoData) {
                                        adapter3 = new MyAdapter(listget3,3);
                                        lv3.setAdapter(adapter3);
                                        listgetTotal3.addAll(listget3);
                                    } else {
                                        listgetTotal3.addAll(listget3);
                                        adapter3.update(listgetTotal3);
                                    }
                                    break;
                                default:break;
                            }
                        }
                    }else if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
                        ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                        new SharedPreferencesData(context).removeAll();
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
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                switch (type) {
                    case 0:
                        lv0.stopRefresh();
                        lv0.stopLoadMore();
                        break;
                    case 3:
                        lv1.stopRefresh();
                        lv1.stopLoadMore();
                        break;
                    case 1:
                        lv2.stopRefresh();
                        lv2.stopLoadMore();
                        break;
                    case 4:
                        lv3.stopRefresh();
                        lv3.stopLoadMore();
                        break;
                }
            }
            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
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
        protected LayoutInflater mInflater;
        private int type;

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
                    case 1:tv.setText("暂无转账中数据");break;
                    case 2:tv.setText("暂无成功数据");break;
                    case 3:tv.setText("暂无失败数据");break;
                }
                rootView.setLayoutParams(params);
                return rootView;
            }
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_withdraw_record, parent, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tv_withdraw_bankName.setText(list.get(position).get("bankName"));
            holder.tv_withdraw_bankNum_end.setText(list.get(position).get("acount"));
            holder.tv_withdraw_money.setText(list.get(position).get("sum"));
            holder.tv_withdraw_fee.setText(TextUtils.isEmpty(list.get(position).get("poundage"))?"0":list.get(position).get("poundage"));
            holder.tv_withdraw_time.setText(list.get(position).get("applyTime"));
            switch(list.get(position).get("status")){//status:1 审核中  2 已提现 3 取消 4转账中 5 失败
                case "1":
                    holder.btn_status_withdraw.setText("转账中");
                    holder.btn_status_withdraw.setBackgroundResource(R.drawable.btn_radius_no);break;
                case "2":
                    holder.btn_status_withdraw.setText("成功");
                    holder.btn_status_withdraw.setBackgroundResource(R.drawable.btn_radius_yes);break;
                case "3":
                    holder.btn_status_withdraw.setText("失败");
                    holder.btn_status_withdraw.setBackgroundResource(R.drawable.tv_status_bg_cg1);break;
                case "4":
                    holder.btn_status_withdraw.setText("转账中");
                    holder.btn_status_withdraw.setBackgroundResource(R.drawable.btn_radius_no);break;
                case "5":
                    holder.btn_status_withdraw.setText("失败");
                    holder.btn_status_withdraw.setBackgroundResource(R.drawable.tv_status_bg_cg1);break;
                default:break;
            }
            return convertView;
        }
        @Override
        public boolean isEnabled(int position) {
            return false;
        }
        class Holder {
            @BindView(R.id.tv_withdraw_bankName)
            TextView tv_withdraw_bankName;
            @BindView(R.id.tv_withdraw_bankNum_end)
            TextView tv_withdraw_bankNum_end;
            @BindView(R.id.tv_withdraw_money)
            TextView tv_withdraw_money;
            @BindView(R.id.tv_withdraw_fee)
            TextView tv_withdraw_fee;
            @BindView(R.id.tv_withdraw_time)
            TextView tv_withdraw_time;
            @BindView(R.id.btn_status_withdraw)
            Button btn_status_withdraw;
            Holder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    @Override
    protected void onDestroy() {
        mHandler=null;
        super.onDestroy();
    }
}
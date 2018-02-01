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
import android.view.ViewStub;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.MyWidget.DepositDialog;
import com.cstz.adapter.MyPagerAdapterRepayment;
import com.cstz.common.App;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.dialog.PayPasswordDialog;
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

import static com.cstz.cunguan.fragment.UserCenter.RESULT_ADD_CG;

/**
 * 还款项目
 */
public class Repayment extends MyActivity implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.navigation_right)
    TextView navigation_right;
    @BindView(R.id.title_back)
    ImageView titleBack;
//    MySliderCg slider_repayment;
    PagerSlidingTabStrip slider_repayment;
    ViewPager pager_repayment;
    List<ViewPagerItemView> viewlist;
    ViewPagerItemView page1, page2, page3, page4, page5;
    private library.widget.xlistview.XListView lv0, lv1, lv2, lv3, lv4;
    private Context context;
    private List<Map<String, String>> listget0, listgetTotal0,listget1, listgetTotal1,listget2,
            listgetTotal2,listget3, listgetTotal3,listget4, listgetTotal4;
    private MyAdapter adapter,adapter1,adapter2,adapter3,adapter4;
    private int pageNum = 1, pageNum1 = 1, pageNum2 = 1, pageNum3 = 1, pageNum4 = 1;
    private final static int pageSize = 15;
    private int mScreenHeight; // 屏幕高度
    private SharedPreferencesData sp;
    private Handler mHandler;
    private int type = 0;
    boolean isPage1First = true, isPage2First = true, isPage3First = true, isPage4First = true;
    private DepositDialog depositDialog;
    private boolean hasRequestSuccess=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_repayment);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        titleTv.setText("还款项目");
        if(TextUtils.equals("2",App.borrowerType))
            titleBack.setVisibility(View.GONE);
        else{
            navigation_right.setText("设置");
            navigation_right.setVisibility(View.VISIBLE);
        }
        titleBack.setVisibility(View.VISIBLE);
        context = this;
        sp = new SharedPreferencesData(this);
        mHandler = new Handler();
        mScreenHeight = DensityUtil.getScreenHeight();
        requestRepayList(pageNum, type);
        initViewStub(false);
    }

    private void initViewStub(boolean b) {
        if (b) {
            ViewStub stub = (ViewStub) findViewById(R.id.view_repayment0);
            stub.inflate();
        } else {
            ViewStub stub = (ViewStub) findViewById(R.id.view_repayment1);
            stub.inflate();
//            slider_repayment = (MySliderCg) findViewById(R.id.slider_repayment);
            slider_repayment = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            pager_repayment = (ViewPager) findViewById(R.id.pager_repayment);
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
        page1 = (ViewPagerItemView) View.inflate(this, R.layout.funddetail_tab1, null);
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
        pager_repayment.setAdapter(new MyPagerAdapterRepayment(viewlist));
        pager_repayment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
//                slider_repayment.setSelectIndex(position);
                if (position == 0) {
                    type = 0;
                } else if (position == 1) {
                    type = 1;
                    if (isPage1First) {
                        isPage1First = false;
                        requestRepayList(pageNum1, type);
                    }
                } else if (position == 2) {
                    type = 2;
                    if (isPage2First) {
                        isPage2First = false;
                        requestRepayList(pageNum2, type);
                    }
                } else if (position == 3) {
                    type = 3;
                    if (isPage3First) {
                        isPage3First = false;
                        requestRepayList(pageNum3, type);
                    }
                } else if (position == 4) {
                    type = 4;
                    if (isPage4First) {
                        isPage4First = false;
                        requestRepayList(pageNum4, type);
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        slider_repayment.setViewPager(pager_repayment);
        pager_repayment.setCurrentItem(0);
    }

    private void initPage0() {
        if (lv0 == null) {
            lv0 = (library.widget.xlistview.XListView) page1.findViewById(R.id.lv_funddetail_tab0);
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
                            requestRepayList(pageNum,type);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum++;
                    requestRepayList(pageNum,type);
                }
            });
            Map<String, String> map = new HashMap<>();
            listgetTotal0 = new ArrayList<>();
            listget0 = new ArrayList<>();
            listget0.add(map);
            adapter = new MyAdapter(listget0,0);
            lv0.setAdapter(adapter);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_180);
            adapter.getNoDataEntity(height);
        }
    }

    private void initPage1() {
        if (lv1 == null) {
            lv1 = (library.widget.xlistview.XListView) page2.findViewById(R.id.lv_funddetail_tab0);
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
                            requestRepayList(pageNum1,type);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum1++;
                    requestRepayList(pageNum1,type);
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
            lv2 = (library.widget.xlistview.XListView) page3.findViewById(R.id.lv_funddetail_tab0);
            lv2.setPullRefreshEnable(true);
            lv2.setPullLoadEnable(false);
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
                            requestRepayList(pageNum2,type);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum2++;
                    requestRepayList(pageNum2,type);
                }
            });
            Map<String, String> map = new HashMap<>();
            listgetTotal2 = new ArrayList<>();
            listget2 = new ArrayList<>();
            listget2.add(map);
            adapter2 = new MyAdapter(listget2,2);
            lv2.setAdapter(adapter2);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_180);
            adapter2.getNoDataEntity(height);
        }
    }

    private void initPage3() {
        if (lv3 == null) {
            lv3 = (library.widget.xlistview.XListView) page4.findViewById(R.id.lv_funddetail_tab0);
            lv3.setPullRefreshEnable(true);
            lv3.setPullLoadEnable(false);
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
                            requestRepayList(pageNum3,type);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum3++;
                    requestRepayList(pageNum3,type);
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
            lv4 = (library.widget.xlistview.XListView) page5.findViewById(R.id.lv_funddetail_tab0);
            lv4.setPullRefreshEnable(true);
            lv4.setPullLoadEnable(false);
            lv4.setXListViewListener(new library.widget.xlistview.XListView.IXListViewListener() {
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
                            requestRepayList(pageNum4,type);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum4++;
                    requestRepayList(pageNum4,type);
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

    private void requestRepayList(int index, int type0) {
        String path = Config.getHttpConfig() + "/user/queryRepayList";
        final RequestParams params = new RequestParams(path);
        params.addParameter("pageNum", index);
        params.addParameter("pageSize", pageSize);
        params.addParameter("time", "0");//时间检索类型0:不限制，其他：相应的天数,1个月以内 传30，半年以内 传180
        params.addParameter("type", type0);//0：全部,1:待还款，2：还款中，3：已还款，4：还款失败，5：个人中心待还款列表
        params.addParameter("token", sp.getValue("token"));
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
                        JSONObject data1 = data.getJSONObject("data");
                        if (data1.has("data")) {
                            JSONArray array = data1.getJSONArray("data");
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
                                        map.put("productTitle", record.getString("productTitle"));
                                        map.put("productAmount", record.getString("productAmount"));
                                        map.put("investEndDay", "最后还款日：" + record.getString("repayDate"));//investEndDay
                                        map.put("repayDate", "还款日期：" + record.getString("realRepayDate"));//repayDate
                                        map.put("brstatus", record.getString("brstatus"));
                                        map.put("pid", record.getString("pid"));
                                        listget0.add(map);
                                    }
                                    if (length < pageSize)
                                        lv0.setNoMoreData();
                                    else
                                        lv0.setPullLoadEnable(true);
                                    if (adapter.isNoData) {
                                        adapter = new MyAdapter(listget0);
                                        lv0.setAdapter(adapter);
                                        listgetTotal0.addAll(listget0);
                                    } else {
                                        listgetTotal0.addAll(listget0);
                                        adapter.update(listgetTotal0);
                                    }
                                    break;
                                case 1:
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
                                        map.put("productTitle", record.getString("productTitle"));
                                        map.put("productAmount", record.getString("productAmount"));
                                        map.put("investEndDay", "最后还款日：" + record.getString("investEndDay"));
                                        map.put("repayDate", "还款日期：" + record.getString("repayDate"));
                                        map.put("brstatus", record.getString("brstatus"));
                                        map.put("pid", record.getString("pid"));
                                        listget1.add(map);
                                    }
                                    if (length < pageSize)
                                        lv1.setNoMoreData();
                                    else
                                        lv1.setPullLoadEnable(true);
                                    if (adapter1.isNoData) {
                                        adapter1 = new MyAdapter(listget1);
                                        lv1.setAdapter(adapter1);
                                        listgetTotal1.addAll(listget1);
                                    } else {
                                        listgetTotal1.addAll(listget1);
                                        adapter1.update(listgetTotal1);
                                    }
                                    break;
                                case 2:
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
                                        map.put("productTitle", record.getString("productTitle"));
                                        map.put("productAmount", record.getString("productAmount"));
                                        map.put("investEndDay", "最后还款日：" + record.getString("investEndDay"));
                                        map.put("repayDate", "还款日期：" + record.getString("repayDate"));
                                        map.put("brstatus", record.getString("brstatus"));
                                        map.put("pid", record.getString("pid"));
                                        listget2.add(map);
                                    }
                                    if (length < pageSize)
                                        lv2.setNoMoreData();
                                    else
                                        lv2.setPullLoadEnable(true);
                                    if (adapter2.isNoData) {
                                        adapter2 = new MyAdapter(listget2);
                                        lv2.setAdapter(adapter2);
                                        listgetTotal2.addAll(listget2);
                                    } else {
                                        listgetTotal2.addAll(listget2);
                                        adapter2.update(listgetTotal2);
                                    }
                                    break;
                                case 3:
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
                                        map.put("productTitle", record.getString("productTitle"));
                                        map.put("productAmount", record.getString("productAmount"));
                                        map.put("investEndDay", "最后还款日：" + record.getString("investEndDay"));
                                        map.put("repayDate", "还款日期：" + record.getString("repayDate"));
                                        map.put("brstatus", record.getString("brstatus"));
                                        map.put("pid", record.getString("pid"));
                                        listget3.add(map);
                                    }
                                    if (length < pageSize)
                                        lv3.setNoMoreData();
                                    else
                                        lv3.setPullLoadEnable(true);
                                    if (adapter3.isNoData) {
                                        adapter3 = new MyAdapter(listget3);
                                        lv3.setAdapter(adapter3);
                                        listgetTotal3.addAll(listget3);
                                    } else {
                                        listgetTotal3.addAll(listget3);
                                        adapter3.update(listgetTotal3);
                                    }
                                    break;
                                case 4:
                                    if (length == 0) {
                                        lv4.setPullLoadEnable(false);
                                        if(pageNum4>1)
                                            lv4.setNoMoreData();
                                        return;
                                    }
                                    listget4 = new ArrayList<>(array.length());
                                    for (int i = 0; i < length; i++) {
                                        JSONObject record = array.getJSONObject(i);
                                        Map<String, String> map = new HashMap<>();
                                        map.put("productTitle", record.getString("productTitle"));
                                        map.put("productAmount", record.getString("productAmount"));
                                        map.put("investEndDay", "最后还款日：" + record.getString("investEndDay"));
                                        map.put("repayDate", "还款日期：" + record.getString("repayDate"));
                                        map.put("brstatus", record.getString("brstatus"));
                                        map.put("pid", record.getString("pid"));
                                        listget4.add(map);
                                    }
                                    if (length < pageSize)
                                        lv4.setNoMoreData();
                                    else
                                        lv4.setPullLoadEnable(true);
                                    if (adapter4.isNoData) {
                                        adapter4 = new MyAdapter(listget4);
                                        lv4.setAdapter(adapter4);
                                        listgetTotal4.addAll(listget4);
                                    } else {
                                        listgetTotal4.addAll(listget4);
                                        adapter4.update(listgetTotal4);
                                    }
                                    break;
                                default:break;
                            }
                        }
                    }else if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
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
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                switch (type) {
                    case 0:
                        lv0.stopRefresh();
                        lv0.stopLoadMore();
                        break;
                    case 1:
                        lv1.stopRefresh();
                        lv1.stopLoadMore();
                        break;
                    case 2:
                        lv2.stopRefresh();
                        lv2.stopLoadMore();
                        break;
                    case 3:
                        lv3.stopRefresh();
                        lv3.stopLoadMore();
                        break;
                    case 4:
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

    private void requestRepayAction(String productId, String pwd) {
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
                            JSONObject data = new JSONObject(arg0);
                            if(data.has("key")){
                                String key = data.getString("key");
                                if (TextUtils.equals("success", key)) {
                                    showToast(data.getString("message"),3000);
                                    hasRequestSuccess = true;
                                    //还款成功，刷新数据
                                    if(type==0){
                                        lv0.autoRefresh();
                                    }else if(type==1){
                                        lv1.autoRefresh();
                                    }
                                }else if(data.has("result")&&TextUtils.equals("-4",data.getString("result"))){
                                    ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                                    sp.removeAll();
                                    Intent intent = new Intent(context, Login.class);
                                    startActivity(intent);
                                    finish();
                                } else{
                                    showToast(data.getString("message"),3000);
                                }
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
                        if(depositDialog!=null)
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

    @OnClick({R.id.title_back, R.id.navigation_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                if(hasRequestSuccess)
                    setResult(RESULT_ADD_CG);
                finish();
                break;
            case R.id.navigation_right:
                startActivity(new Intent(this, RepaymentSet.class));
                break;
            default:break;
        }
    }

    @Override
    public void onBackPressed() {
        if(hasRequestSuccess)
            setResult(RESULT_ADD_CG);
        super.onBackPressed();
    }

    class MyAdapter extends BaseAdapter {
        private List<Map<String, String>> list = new ArrayList<>();
        private boolean isNoData;
        private int height;
        protected LayoutInflater mInflater;
        private int type;
        public MyAdapter(List<Map<String, String>> listget) {
            this.list = listget;
            mInflater = LayoutInflater.from(context);
        }
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (isNoData) {
                convertView = mInflater.inflate(R.layout.item_no_data_layout, null);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_root_view);
                TextView tv = ButterKnife.findById(convertView, R.id.tv_nodata);
                switch (type){
                    case 0:tv.setText("暂无数据");break;
                    case 1:tv.setText("暂无待还款数据");break;
                    case 2:tv.setText("暂无还款中数据");break;
                    case 3:tv.setText("暂无已还款数据");break;
                    case 4:tv.setText("暂无还款失败数据");break;
                    default:tv.setText("暂无数据");break;
                }
                rootView.setLayoutParams(params);
                return convertView;
            }
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_borrower_repayment, parent, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tvRepaymentTitle.setText(list.get(position).get("productTitle"));
            holder.tvRepaymentMoney.setText(list.get(position).get("productAmount"));
            holder.tvRepaymentEndDay.setText(list.get(position).get("investEndDay"));
            String repayDate = list.get(position).get("repayDate");
            if(repayDate.length()==5){
                holder.tv_repayment_time.setVisibility(View.GONE);
            }else{
                holder.tv_repayment_time.setVisibility(View.VISIBLE);
                holder.tv_repayment_time.setText(repayDate);
            }
            String brstatus = list.get(position).get("brstatus");
            switch (brstatus){//1:待还款  2:还款中 3:已还款4:还款失败,-1:数据有问题导致状态不能显示
                case "1":
                    holder.tv_repayment_status.setVisibility(View.VISIBLE);
                    holder.tv_repayment_status.setText("还款");
                    holder.tv_repayment_status.setBackgroundResource(R.drawable.btn_radius_cg_notice1);
                    holder.tvRepaymentType.setText("待还款");
                    break;
                case "2":
                    holder.tv_repayment_status.setVisibility(View.GONE);
                    holder.tvRepaymentType.setText("还款中");
                    break;
                case "3":
                    holder.tv_repayment_status.setVisibility(View.GONE);
                    holder.tvRepaymentType.setText("已还款");break;
                case "4":
                    holder.tv_repayment_status.setVisibility(View.VISIBLE);
                    holder.tv_repayment_status.setText("重新还款");
                    holder.tv_repayment_status.setBackgroundResource(R.drawable.btn_radius_cg_repayment_red);
                    holder.tvRepaymentType.setText("还款失败");break;
                case "-1":
                    holder.tv_repayment_status.setVisibility(View.GONE);
                    holder.tvRepaymentType.setText("状态未知");break;
                default:break;
            }
            holder.tv_repayment_status.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PayPasswordDialog d = new PayPasswordDialog(context,0.85);
                    d.setOnRightClickListener(new PayPasswordDialog.onClickListener() {
                        @Override
                        public void click() {
                            d.dismiss();
                            requestRepayAction(list.get(position).get("pid"), d.etInvestVerify.getText().toString());
                        }
                    });
                    d.show();
                }
            });
            return convertView;
        }
        @Override
        public boolean isEnabled(int position) {
            return false;
        }
        class Holder {
            @BindView(R.id.tv_repayment_title)
            TextView tvRepaymentTitle;
            @BindView(R.id.tv_repayment_money)
            TextView tvRepaymentMoney;
            @BindView(R.id.tv_repayment_end_day)
            TextView tvRepaymentEndDay;
            @BindView(R.id.tv_repayment_time)
            TextView tv_repayment_time;
            @BindView(R.id.tv_repayment_status)
            TextView tv_repayment_status;
            @BindView(R.id.tv_repayment_type)
            TextView tvRepaymentType;
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
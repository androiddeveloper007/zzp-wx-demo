package com.cstz.cunguan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
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
import library.widget.PagerSlidingTabStrip;
import library.widget.xlistview.XListView;

/**
 * 原账户资金明细
 */
public class FundDetailOld extends MyActivity implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;

//    MySliderCg slider_zjmx;
    PagerSlidingTabStrip slider_zjmx;
    ViewPager pager_zjmx;
    List<ViewPagerItemView> viewlist;
    ViewPagerItemView page1, page2, page3, page4, page5, page6;
    private XListView lv0, lv1, lv2, lv3, lv4, lv5;
    private Context context;
    private List<Map<String, String>> listget0, listgetTotal0,listget1, listgetTotal1,listget2, listgetTotal2,
            listget3, listgetTotal3,listget4, listgetTotal4,listget5, listgetTotal5;
    private MyAdapter adapter, adapter1, adapter2, adapter3, adapter4, adapter5;
    private int pageNum = 1,pageNum1 = 1,pageNum2 = 1,pageNum3 = 1,pageNum4 = 1,pageNum5 = 1;
    private final static int pageSize = 15;
    private int mScreenHeight; // 屏幕高度
    private HeaderViewPager scrollableLayout;
    private int currentYTemp = 0;
    private String deposit = "0";
    private int status = 0;
    private TextView tv_fund_detail1;
    private TextView tv_fund_detail4;
    private TextView tv_fund_detail5;
    boolean isPage1First = true, isPage2First = true, isPage3First = true, isPage4First = true, isPage5First = true;
    private SharedPreferencesData sp;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_zjmx);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        titleTv.setText("原账户资金明细");
        titleBack.setVisibility(View.VISIBLE);
        context = this;
        sp = new SharedPreferencesData(context);
        mHandler = new Handler();
        mScreenHeight = DensityUtil.getScreenHeight();
        loadData(0, pageNum);
        initViewStub(false);
    }

    private void initViewStub(boolean noData) {
        if (noData) {
            ViewStub stub = (ViewStub) findViewById(R.id.stub_fund_detail0);
            stub.inflate();
        } else {
            ViewStub stub = (ViewStub) findViewById(R.id.stub_fund_detail1);
            stub.inflate();
//            slider_zjmx = (MySliderCg) findViewById(R.id.tabs);//slider_zjmx
            slider_zjmx = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            pager_zjmx = (ViewPager) findViewById(R.id.pager_zjmx);
            scrollableLayout = (HeaderViewPager) findViewById(R.id.header_viewPager);
            tv_fund_detail1 = (TextView) findViewById(R.id.tv_fund_detail1);
            tv_fund_detail4 = (TextView) findViewById(R.id.tv_fund_detail4);
            tv_fund_detail5 = (TextView) findViewById(R.id.tv_fund_detail5);
            initView();
        }
    }

    private void loadData(int status, int index) {
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", index);
        map.put("pageSize", pageSize);
        map.put("fundType", status);//0全部，1项目，2充值，3提现，4其他 ，5还款（借款人）
        map.put("deposit", deposit);
        map.put("token",sp.getValue("token"));
        postData(map, "/user/fundRecord", null, App.PostType.LOAD);
    }

    private void initView() {
//        slider_zjmx.setOnClickSlider(new MySliderCg.OnClickSlider() {
//            @Override
//            public void getIndex(int index) {
//                pager_zjmx.setCurrentItem(index);
//            }
//        });
        viewlist = new ArrayList<>();
        page1 = (ViewPagerItemView) View.inflate(this, R.layout.funddetail_tab1, null);
        page1.setScrollableView((XListView) page1.findViewById(R.id.lv_funddetail_tab0));
        page2 = (ViewPagerItemView) View.inflate(this, R.layout.funddetail_tab1, null);
        page2.setScrollableView((XListView) page2.findViewById(R.id.lv_funddetail_tab0));
        page3 = (ViewPagerItemView) View.inflate(this, R.layout.funddetail_tab1, null);
        page3.setScrollableView((XListView) page3.findViewById(R.id.lv_funddetail_tab0));
        page4 = (ViewPagerItemView) View.inflate(this, R.layout.funddetail_tab1, null);
        page4.setScrollableView((XListView) page4.findViewById(R.id.lv_funddetail_tab0));
        page5 = (ViewPagerItemView) View.inflate(this, R.layout.funddetail_tab1, null);
        page5.setScrollableView((XListView) page5.findViewById(R.id.lv_funddetail_tab0));
        page6 = (ViewPagerItemView) View.inflate(this, R.layout.funddetail_tab1, null);
        page6.setScrollableView((XListView) page5.findViewById(R.id.lv_funddetail_tab0));
        viewlist.add(page1);
        viewlist.add(page2);
        viewlist.add(page3);
        viewlist.add(page4);
        viewlist.add(page5);
        viewlist.add(page6);
        initPage0();
        initPage1();
        initPage2();
        initPage3();
        initPage4();
        initPage5();
        pager_zjmx.setAdapter(new MyPagerAdapterCg(viewlist));
        pager_zjmx.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
//                slider_zjmx.setSelectIndex(position);
                scrollableLayout.setCurrentScrollableContainer(viewlist.get(position));
                if (position == 0) {
                    status = 0;
                } else if (position == 1) {
                    status = 1;
                    if (isPage1First) {
                        isPage1First = false;
                        loadData(status, pageNum1);
                    }
                } else if (position == 2) {
                    status = 2;
                    if (isPage2First) {
                        isPage2First = false;
                        loadData(status, pageNum2);
                    }
                } else if (position == 3) {
                    status = 3;
                    if (isPage3First) {
                        isPage3First = false;
                        loadData(status, pageNum3);
                    }
                } else if (position == 4) {
                    status = 7;
                    if (isPage4First) {
                        isPage4First = false;
                        loadData(status, pageNum4);
                    }
                }else if (position == 5) {
                    status = 4;
                    if (isPage5First) {
                        isPage5First = false;
                        loadData(status, pageNum5);
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
                    lv3.setPullRefreshEnable(true);
                    lv4.setPullRefreshEnable(true);
                    lv5.setPullRefreshEnable(true);
                } else {
                    if (!lv0.getIsPullRefreshing() && currentY < currentYTemp)
                        lv0.setPullRefreshEnable(false);
                    if (!lv1.getIsPullRefreshing() && currentY < currentYTemp)
                        lv1.setPullRefreshEnable(false);
                    if (!lv2.getIsPullRefreshing() && currentY < currentYTemp)
                        lv2.setPullRefreshEnable(false);
                    if (!lv3.getIsPullRefreshing() && currentY < currentYTemp)
                        lv3.setPullRefreshEnable(false);
                    if (!lv4.getIsPullRefreshing() && currentY < currentYTemp)
                        lv4.setPullRefreshEnable(false);
                    if (!lv5.getIsPullRefreshing() && currentY < currentYTemp)
                        lv5.setPullRefreshEnable(false);
                }
                currentYTemp = currentY;
            }
        });
        pager_zjmx.setCurrentItem(0);
        slider_zjmx.setViewPager(pager_zjmx);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (scrollableLayout != null) {
            scrollableLayout.setTopOffset(0);
            scrollableLayout.setCurrentScrollableContainer(viewlist.get(pager_zjmx.getCurrentItem()));
        }
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
                            loadData(status, pageNum);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum++;
                    loadData(status, pageNum);
                }
            });
            listgetTotal0 = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            listget0 = new ArrayList<>();
            listget0.add(map);
            adapter = new MyAdapter(listget0,0);
            lv0.setAdapter(adapter);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_340);
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
                            loadData(status, pageNum1);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum1++;
                    loadData(status, pageNum1);
                }
            });
            listgetTotal1 = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            listget1 = new ArrayList<>();
            listget1.add(map);
            adapter1 = new MyAdapter(listget1,1);
            lv1.setAdapter(adapter1);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_340);
            adapter1.getNoDataEntity(height);
        }
    }

    private void initPage2() {
        if (lv2 == null) {
            lv2 = (library.widget.xlistview.XListView) page3.findViewById(R.id.lv_funddetail_tab0);
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
                            loadData(status, pageNum2);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum2++;
                    loadData(status, pageNum2);
                }
            });
            listgetTotal2 = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            listget2 = new ArrayList<>();
            listget2.add(map);
            adapter2 = new MyAdapter(listget2,2);
            lv2.setAdapter(adapter2);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_340);
            adapter2.getNoDataEntity(height);
        }
    }

    private void initPage3() {
        if (lv3 == null) {
            lv3 = (library.widget.xlistview.XListView) page4.findViewById(R.id.lv_funddetail_tab0);
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
                            loadData(status, pageNum3);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum3++;
                    loadData(status, pageNum3);
                }
            });
            listgetTotal3 = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            listget3 = new ArrayList<>();
            listget3.add(map);
            adapter3 = new MyAdapter(listget3,3);
            lv3.setAdapter(adapter3);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_340);
            adapter3.getNoDataEntity(height);
        }
    }

    private void initPage4() {
        if (lv4 == null) {
            lv4 = (library.widget.xlistview.XListView) page5.findViewById(R.id.lv_funddetail_tab0);
            lv4.setPullRefreshEnable(true);
            lv4.setPullLoadEnable(false);
            lv4.setAutoLoadEnable(true);
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
                            loadData(status, pageNum4);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum4++;
                    loadData(status, pageNum4);
                }
            });
            listgetTotal4 = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            listget4 = new ArrayList<>();
            listget4.add(map);
            adapter4 = new MyAdapter(listget4,4);
            lv4.setAdapter(adapter4);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_340);
            adapter4.getNoDataEntity(height);
        }
    }

    private void initPage5() {
        if (lv5 == null) {
            lv5 = (library.widget.xlistview.XListView) page6.findViewById(R.id.lv_funddetail_tab0);
            lv5.setPullRefreshEnable(true);
            lv5.setPullLoadEnable(false);
            lv5.setAutoLoadEnable(true);
            lv5.setXListViewListener(new library.widget.xlistview.XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    lv5.setPullLoadEnable(false);
                    lv5.setAutoLoadEnable(true);
                    pageNum5 = 1;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listget5.clear();
                            listgetTotal5.clear();
                            loadData(status, pageNum5);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum5++;
                    loadData(status, pageNum5);
                }
            });
            listgetTotal5 = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            listget5 = new ArrayList<>();
            listget5.add(map);
            adapter5 = new MyAdapter(listget5,5);
            lv5.setAdapter(adapter5);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_340);
            adapter5.getNoDataEntity(height);
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
                if (data2 != null && status == 0) {
                    if (data2.has("accountSum"))
                        tv_fund_detail1.setText(data2.getString("accountSum"));
                    if (data2.has("usableSum"))
                        tv_fund_detail4.setText(data2.getString("usableSum"));
                    if (data2.has("freezeSum"))
                        tv_fund_detail5.setText(data2.getString("freezeSum"));
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
                                listget0 = new ArrayList<>(array.length());
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject product = array.getJSONObject(i);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("title", product.getString("fundMode"));
                                    map.put("text", product.getString("remarks"));
                                    map.put("time", product.getString("recordTime"));
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
                            case 1:
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
                            case 2:
                                if (length == 0) {
                                    lv2.setPullLoadEnable(false);
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
                            case 3:
                                if (length == 0) {
                                    lv3.setPullLoadEnable(false);
                                    if(pageNum3>3)
                                    lv3.setNoMoreData();
                                    return;}
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
                            case 7:
                                if (length == 0) {
                                    lv4.setPullLoadEnable(false);
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
                            case 4:
                                if (length == 0) {
                                    lv5.setPullLoadEnable(false);
                                    if(pageNum5>1)
                                    lv5.setNoMoreData();
                                    return;
                                }
                                listget5 = new ArrayList<>(length);
                                for (int i = 0; i < length; i++) {
                                    JSONObject product = array.getJSONObject(i);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("title", product.getString("fundMode"));
                                    map.put("text", product.getString("remarks"));
                                    map.put("time", product.getString("recordTime"));
                                    listget5.add(map);
                                }
                                if (length < pageSize) {
                                    lv5.setNoMoreData();
                                } else {
                                    lv5.setPullLoadEnable(true);
                                }
                                if (adapter5.isNoData) {
                                    adapter5 = new MyAdapter(listget5,5);
                                    lv5.setAdapter(adapter5);
                                    listgetTotal5.addAll(listget5);
                                } else {
                                    listgetTotal5.addAll(listget5);
                                    adapter5.update(listgetTotal5);
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
                            case 2:
                                lv2.setPullLoadEnable(false);
                                break;
                            case 3:
                                lv3.setPullLoadEnable(false);
                                break;
                            case 7:
                                lv4.setPullLoadEnable(false);
                                break;
                            case 4:
                                lv5.setPullLoadEnable(false);
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
            case 2:
                lv2.stopRefresh();
                lv2.stopLoadMore();
                break;
            case 3:
                lv3.stopRefresh();
                lv3.stopLoadMore();
                break;
            case 7:
                lv4.stopRefresh();
                lv4.stopLoadMore();
                break;
            case 4:
                lv5.stopRefresh();
                lv5.stopLoadMore();
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
            case R.id.iv_navigation_right:
                startActivity(new Intent(this, FundDetailOld.class));
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
//                ImageView iv = ButterKnife.findById(convertView, R.id.iv_placeholder_image);
//                iv.setImageResource(R.mipmap.fund_detail_nodata);
                TextView tv = ButterKnife.findById(convertView, R.id.tv_nodata);
                switch (type){
                    case 0:tv.setText("暂无数据");break;
                    case 1:tv.setText("暂无投资数据");break;
                    case 2:tv.setText("暂无充值数据");break;
                    case 3:tv.setText("暂无提现数据");break;
                    case 4:tv.setText("暂无回款数据");break;
                    case 5:tv.setText("暂无其它数据");break;
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
            return false;
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

    @Override
    protected void onDestroy() {
        sp=null;
        mHandler=null;
        super.onDestroy();
    }
}
package com.cstz.cunguan.activity;

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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.adapter.MyPagerAdapter;
import com.cstz.common.App;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.view.MySliderCg;
import com.cstz.cstz_android.R;
import com.cstz.tab.MainActivity;
import com.cstz.tools.ToastMakeText;

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

/**
 * 我的红包
 */
public class RedPacket extends MyActivity implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.slider_red_packet)
    MySliderCg slider_red_packet;
    @BindView(R.id.pager_red_packet)
    ViewPager pager_red_packet;

    List<View> viewlist;
    View page1, page2, page3;
    private library.widget.xlistview.XListView lv0, lv1, lv2;
    private Context context;
    private List<Map<String, String>> listget0, listgetTotal0;
    private List<Map<String, String>> listget1, listgetTotal1;
    private List<Map<String, String>> listget2, listgetTotal2;
    private MyAdapter adapter;
    private MyAdapter adapter1;
    private MyAdapter adapter2;
    private int pageNum = 1;
    private int pageNum1 = 1;
    private int pageNum2 = 1;
    private int mScreenHeight; // 屏幕高度
    private SharedPreferencesData sp;
    private Handler mHandler;
    private int type = 1;
    private boolean page1FirstLoad=true,page2FirstLoad=true;
    public static final int REQUEST_RED_PACKET = 0x00000002;
    public static final int RESULT_RED_PACKET = 0x00000003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_redpacket);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        titleTv.setText("我的红包");
        titleBack.setVisibility(View.VISIBLE);
        context = this;
        sp = new SharedPreferencesData(this);
        mHandler = new Handler();
        mScreenHeight = DensityUtil.getScreenHeight();
        initView();
        listgetTotal0 = new ArrayList<>();
        listgetTotal1 = new ArrayList<>();
        listgetTotal2 = new ArrayList<>();
        if(getIntent()!=null && !TextUtils.isEmpty(getIntent().getStringExtra("isRedpacketUnRead")))
            postData(null, "/user/redpacketRead", null, App.PostType.LOAD);//更新红包未读状态
        //判断如果是已经开通存管，且当前是老账户的话
        if(TextUtils.equals("1",App.depositCheck) && TextUtils.equals("1",getIntent().getStringExtra("uCenter"))){

        }else{
            initData(type,pageNum);
        }
    }

    public void initData(int type,int page) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", sp.getValue("token"));
        map.put("type", type);
        map.put("pageNum", page);
        map.put("isDeposit", TextUtils.equals("1",getIntent().getStringExtra("uCenter")) ? "0":"1");//是否存管红包界面：0：非存管,1:存管。(可用红包不区分此数据)
        postData(map, "/user/queryRedPacketList", null, App.PostType.LOAD);
    }

    private void initView() {
        slider_red_packet.setOnClickSlider(new MySliderCg.OnClickSlider() {
            @Override
            public void getIndex(int index) {
                pager_red_packet.setCurrentItem(index);
            }
        });
        viewlist = new ArrayList<>();
        page1 = View.inflate(this, R.layout.funddetail_tab1, null);
        page2 = View.inflate(this, R.layout.funddetail_tab1, null);
        page3 = View.inflate(this, R.layout.funddetail_tab1, null);
        viewlist.add(page1);
        viewlist.add(page2);
        viewlist.add(page3);
        initPage0();
        initPage1();
        initPage2();
        pager_red_packet.setAdapter(new MyPagerAdapter(viewlist));
        pager_red_packet.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                slider_red_packet.setSelectIndex(position);
                if(position==0){
                    type=1;
                }else if(position==1){
                    type=2;
                    if(page1FirstLoad)
                        initData(type,pageNum1);
                    page1FirstLoad = false;
                }else{
                    type=3;
                    if(page2FirstLoad)
                        initData(type,pageNum2);
                    page2FirstLoad = false;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
                    if(TextUtils.equals("1",App.depositCheck) && TextUtils.equals("1",getIntent().getStringExtra("uCenter"))){
                        lv0.stopRefresh();
                    }else{
                        lv0.setPullLoadEnable(false);
                        lv0.setAutoLoadEnable(true);
                        pageNum = 1;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listget0.clear();
                                listgetTotal0.clear();
                                initData(1,pageNum);
                            }
                        },300);
                    }
                }
                @Override
                public void onLoadMore() {
                    pageNum++;
                    initData(1,pageNum);
                }
            });
            Map<String, String> map = new HashMap<>();
            listget0 = new ArrayList<>();
            listget0.add(map);
            adapter = new MyAdapter(listget0,1);
            lv0.setAdapter(adapter);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_280);
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
//                    if(TextUtils.equals("1",App.depositCheck) && TextUtils.equals("1",getIntent().getStringExtra("uCenter"))){
//
//                    }else{
//                    }
                    lv1.setPullLoadEnable(false);
                    lv1.setAutoLoadEnable(true);
                    pageNum1 = 1;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listget1.clear();
                            listgetTotal1.clear();
                            initData(2,pageNum1);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum1++;
                    initData(2,pageNum1);
                }
            });
            Map<String, String> map = new HashMap<>();
            listget1 = new ArrayList<>();
            listget1.add(map);
            adapter1 = new MyAdapter(listget1,2);
            lv1.setAdapter(adapter1);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_280);
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
//                    if(TextUtils.equals("1",App.depositCheck) && TextUtils.equals("1",getIntent().getStringExtra("uCenter"))){
//
//                    }else{
//                    }
                    lv2.setPullLoadEnable(false);
                    lv2.setAutoLoadEnable(true);
                    pageNum2 = 1;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listget2.clear();
                            listgetTotal2.clear();
                            initData(3,pageNum2);
                        }
                    },300);
                }
                @Override
                public void onLoadMore() {
                    pageNum2++;
                    initData(3,pageNum2);
                }
            });
            Map<String, String> map = new HashMap<>();
            listget2 = new ArrayList<>();
            listget2.add(map);
            adapter2 = new MyAdapter(listget2,3);
            lv2.setAdapter(adapter2);
            int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_280);
            adapter2.getNoDataEntity(height);
        }
    }

    @OnClick({R.id.title_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                if(getIntent()!=null && getIntent().hasExtra("isRedpacketUnRead"))
                    setResult(RESULT_RED_PACKET);
                finish();
                break;
            default:break;
        }
    }

    @Override
    public void onBackPressed() {
        if(getIntent()!=null && getIntent().hasExtra("isRedpacketUnRead"))
            setResult(RESULT_RED_PACKET);
        super.onBackPressed();
    }

    @Override
    public void requestNoConnection(App.PostType postType) {
//        slider_red_packet.setItemClickable(true);
    }

    public void requestFail(String msg, App.PostType postType) {
        super.requestFail(msg, postType);
        ToastMakeText.showToast(this, msg, 1200);
//        slider_red_packet.setItemClickable(true);
    }

    public void requestSuccess(JSONObject object, App.PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            try {
                JSONArray data = null;
                if (object.has("data") && object.getJSONArray("data") != null) {
                    data = object.getJSONArray("data");
                }
                if (data != null) {
                    int length = data.length();
                    switch (type){
                        case 1:
                            if(length==0){
                                lv0.setPullLoadEnable(false);
                                if(pageNum>1)
                                    lv0.setNoMoreData();
                                return;
                            }
                            listget0= new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                Map<String, String> map = new HashMap<>();
                                JSONObject record = data.getJSONObject(i);
                                map.put("strat_date", record.getString("STRAT_DATE"));
                                map.put("end_date", record.getString("END_DATE"));
//                                map.put("use_strat", record.getString("USE_STATE"));
                                map.put("redproportion", record.getString("REDPROPORTION"));
                                map.put("redwrap", record.getString("REDWRAP"));
                                map.put("term", record.getString("PRODUCTDAY"));//期限
                                listget0.add(map);
                            }
                            if (length < 15) {
                                lv0.setNoMoreData();
                            } else {
                                lv0.setPullLoadEnable(true);
                            }
                            if (adapter == null) {
                                adapter = new MyAdapter(listget0,1);
                                lv0.setAdapter(adapter);
                            } else {
                                if (adapter.isNoData) {
                                    adapter = new MyAdapter(listget0,1);
                                    lv0.setAdapter(adapter);
                                    listgetTotal0.addAll(listget0);
                                    lv0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(context, MainActivity.class);
                                            intent.putExtra("product", "");
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                } else{
                                    listgetTotal0.addAll(listget0);
                                    adapter.update(listgetTotal0);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            break;
                        case 2:
                            if(length==0){
                                lv1.setPullLoadEnable(false);
                                if(pageNum1>1)
                                    lv1.setNoMoreData();
                                return;
                            }
                            listget1= new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                Map<String, String> map = new HashMap<>();
                                JSONObject record = data.getJSONObject(i);
                                map.put("strat_date", record.getString("STRAT_DATE"));
                                map.put("end_date", record.getString("END_DATE"));
//                                map.put("use_strat", record.getString("USE_STATE"));
                                map.put("redproportion", record.getString("REDPROPORTION"));
                                map.put("redwrap", record.getString("REDWRAP"));
                                map.put("term", record.getString("PRODUCTDAY"));//期限
                                listget1.add(map);
                            }
                            if (length < 15) {
                                lv1.setNoMoreData();
                            } else {
                                lv1.setPullLoadEnable(true);
                            }
                            if (adapter1 == null) {
                                adapter1 = new MyAdapter(listget1,2);
                                lv1.setAdapter(adapter1);
                            } else {
                                if (adapter1.isNoData) {
                                    adapter1 = new MyAdapter(listget1,2);
                                    lv1.setAdapter(adapter1);
                                    listgetTotal1.addAll(listget1);
                                } else{
                                    listgetTotal1.addAll(listget1);
                                    adapter1.update(listgetTotal1);
                                    adapter1.notifyDataSetChanged();
                                }
                            }
                            break;
                        case 3:
                            if(length==0){
                                lv2.setPullLoadEnable(false);
                                if(pageNum2>1)
                                    lv2.setNoMoreData();
                                return;
                            }
                            listget2= new ArrayList<>();
                            for (int i = 0; i < length; i++) {
                                Map<String, String> map = new HashMap<>();
                                JSONObject record = data.getJSONObject(i);
                                map.put("strat_date", record.getString("STRAT_DATE"));
                                map.put("end_date", record.getString("END_DATE"));
//                                map.put("use_strat", record.getString("USE_STATE"));
                                map.put("redproportion", record.getString("REDPROPORTION"));
                                map.put("redwrap", record.getString("REDWRAP"));
                                map.put("term", record.getString("PRODUCTDAY"));//期限
                                listget2.add(map);
                            }
                            if (length < 15) {
                                lv2.setNoMoreData();
                            } else {
                                lv2.setPullLoadEnable(true);
                            }
                            if (adapter2 == null) {
                                adapter2 = new MyAdapter(listget2,3);
                                lv2.setAdapter(adapter2);
                            } else {
                                if (adapter2.isNoData) {
                                    adapter2 = new MyAdapter(listget2,3);
                                    lv2.setAdapter(adapter2);
                                    listgetTotal2.addAll(listget2);
                                } else{
                                    listgetTotal2.addAll(listget2);
                                    adapter2.update(listgetTotal2);
                                    adapter2.notifyDataSetChanged();
                                }
                            }
                            break;
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
        switch (type){
            case 1:
                lv0.stopRefresh();
                lv0.stopLoadMore();
                break;
            case 2:
                lv1.stopRefresh();
                lv1.stopLoadMore();
                break;
            case 3:
                lv2.stopRefresh();
                lv2.stopLoadMore();
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
        public boolean isEnabled(int position) {
            if(type==1)
                return true;
            else
                return false;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (isNoData) {
                convertView = mInflater.inflate(R.layout.item_no_data_layout, null);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_root_view);
                ImageView iv = ButterKnife.findById(convertView, R.id.iv_placeholder_image);
                rootView.setLayoutParams(params);
                switch (type){
                    case 1:
                        if(TextUtils.equals("1",App.depositCheck) && TextUtils.equals("1",getIntent().getStringExtra("uCenter"))){
                            iv.setImageResource(R.mipmap.red_packet_nodata3);
                        }else{
                            iv.setImageResource(R.mipmap.red_packet_nodata0);
                        }
                        break;
                    case 2:iv.setImageResource(R.mipmap.red_packet_nodata2);break;
                    case 3:iv.setImageResource(R.mipmap.red_packet_nodata1);break;
                    default:iv.setImageResource(R.mipmap.red_packet_nodata0);break;
                }
                return convertView;
            }
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.aa_item_red_packet, parent, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.redEnvelopeRedproportion.setText("【投资满"+list.get(position).get("redproportion")+"可用】");
            holder.redEnvelopeRedwrap.setText("¥"+list.get(position).get("redwrap"));
            holder.redEnvelopeTime.setText(list.get(position).get("strat_date")+"/"+list.get(position).get("end_date"));
            if(TextUtils.equals("0",list.get(position).get("term")))
                holder.textviewQixian1.setText("不限");
            else
                holder.textviewQixian1.setText(list.get(position).get("term")+"天及以上");
            switch (type+""){
                case "1":
                    holder.redEnvelopeImage.setBackgroundResource(R.drawable.hongbaobg2);
                    holder.redEnvelopeNext.setVisibility(View.VISIBLE);
                    holder.redEnvelopeNext.setBackgroundResource(R.drawable.next);
                    holder.redEnvelopeState.setBackgroundResource(0);
                    holder.redEnvelopeState.setVisibility(View.GONE);
                    break;
                case "2":
                    holder.redEnvelopeImage.setBackgroundResource(R.drawable.hongbaobg3);
                    holder.redEnvelopeNext.setVisibility(View.GONE);
                    holder.redEnvelopeNext.setBackgroundResource(0);
                    holder.redEnvelopeState.setBackgroundResource(R.drawable.ysy);
                    holder.redEnvelopeState.setVisibility(View.VISIBLE);
                    break;
                case "3":
                    holder.redEnvelopeImage.setBackgroundResource(R.drawable.hongbaobg4);
                    holder.redEnvelopeNext.setVisibility(View.GONE);
                    holder.redEnvelopeNext.setBackgroundResource(0);
                    holder.redEnvelopeState.setBackgroundResource(R.drawable.ygq);
                    holder.redEnvelopeState.setVisibility(View.VISIBLE);
                    break;
                default:break;
            }
            return convertView;
        }
        class Holder {
            @BindView(R.id.red_envelope_redwrap)
            TextView redEnvelopeRedwrap;
            @BindView(R.id.red_envelope_redproportion)
            TextView redEnvelopeRedproportion;
            @BindView(R.id.textview_qixian1)
            TextView textviewQixian1;
            @BindView(R.id.red_envelope_time)
            TextView redEnvelopeTime;
            @BindView(R.id.red_envelope_state)
            ImageView redEnvelopeState;
            @BindView(R.id.red_envelope_next)
            ImageView redEnvelopeNext;
            @BindView(R.id.red_envelope_image)
            ImageView redEnvelopeImage;

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
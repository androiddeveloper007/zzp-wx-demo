package com.cstz.cunguan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.cstz.common.App;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.dialog.ExitDialog;
import com.cstz.tools.Convert;
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
import library.widget.xlistview.XListView;

/**
 * 消息中心
 */
public class MessageCenter extends MyActivity implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.lv_message_center)
    XListView lvMessageCenter;

    private Context context;
    private SharedPreferencesData sp;
    private Handler mHandler;

    private int pageNum = 1;
    private final static int pageSize=15;
    private List<Map<String, String>> list,listTotal;
    private MyAdapter adapter;
    public final static int REQUEST_CODE = 0123;
    public final static int RESPONSE_CODE = 0124;
    public final static int RESPONSE_CODE_1 = 0125;
    private String uCenter="1";
    private String deposit="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_message_center);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        titleTv.setText("消息中心");
        titleBack.setVisibility(View.VISIBLE);
        context = this;
        sp = new SharedPreferencesData(this);
        mHandler = new Handler();
        if(getIntent()!=null && getIntent().getStringExtra("uCenter")!=null)
            uCenter = getIntent().getStringExtra("uCenter");
        deposit = "0";
        if(TextUtils.equals("2",uCenter))
            deposit ="1";
        else if(TextUtils.equals("3",uCenter))
            deposit="1";
        initView();
        initData(pageNum);
        listTotal = new ArrayList<>();
    }

    private void initView() {
        lvMessageCenter.setPullRefreshEnable(true);
        lvMessageCenter.setPullLoadEnable(false);
        lvMessageCenter.setAutoLoadEnable(true);
        lvMessageCenter.setXListViewListener(new library.widget.xlistview.XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                lvMessageCenter.setPullLoadEnable(false);
                lvMessageCenter.setAutoLoadEnable(true);
                pageNum = 1;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        list.clear();
                        listTotal.clear();
                        initData(pageNum);
                    }
                }, 300);
            }
            @Override
            public void onLoadMore() {
                pageNum++;
                initData(pageNum);
            }
        });
        Map<String, String> map = new HashMap<>();
        list = new ArrayList<>(1);
        list.add(map);
        adapter = new MyAdapter(list);
        lvMessageCenter.setAdapter(adapter);
        final int height = DensityUtil.getScreenHeight() - getResources().getDimensionPixelOffset(R.dimen.dimen_130);
        adapter.getNoDataEntity(height);
        lvMessageCenter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter.isNoData) return;
                Intent intent = new Intent();
                intent.setClass(context, MessageDetail.class);
                intent.putExtra("mailId", listTotal.get((int)id).get("mailId").toString());
                startActivityForResult(intent, REQUEST_CODE);
                listTotal.get((int)id).put("mailStatus", "3");
//                adapter.notifyDataSetChanged();
            }
        });
        lvMessageCenter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                final ExitDialog d = new ExitDialog(MessageCenter.this,0.85);
                d.setFillViewVisibility(false);
                d.setText("确定要删除此条消息吗？");
                d.setOnRightClickListener(new ExitDialog.onClickListener() {
                    @Override
                    public void click() {
                        Map<String, Object> map = new HashMap<>();
                        map.put("token", sp.getValue("token"));
                        map.put("mailIds", listTotal.get((int)id).get("mailId"));
                        postData(map, "/user/deleteMailAsMore", App.PostType.SUBMIT, false);
                        listTotal.remove((int)id);
                        if(listTotal.size()==0){
                            adapter = new MyAdapter(listTotal);
                            lvMessageCenter.setAdapter(adapter);
                            adapter.getNoDataEntity(height);
                        }else{
                            adapter.update(listTotal);
                        }
                        d.dismiss();
                    }
                });
                d.show();
                return true;
            }
        });
    }

    public void initData(int pageNum) {
        Map<String, Object> map0 = new HashMap<>();
        map0.put("token", sp.getValue("token"));
        map0.put("pageNum", pageNum);
        map0.put("deposit", deposit);//0：原账户数据 1：存管账户的数据
        postData(map0, "/user/mailList", null, App.PostType.LOAD);
    }

    public void requestFail(String msg, App.PostType postType) {
        super.requestFail(msg, postType);
        ToastMakeText.showToast(this, msg, 1200);
    }

    public void requestSuccess(JSONObject object, App.PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            try {
                JSONArray data = object.getJSONArray("data");
                if (data != null) {
                    list = new ArrayList<>();
                    int length = data.length();
                    if(length<pageSize){
//                        lvMessageCenter.setPullLoadEnable(false);
//                        if(pageNum>1)
                            lvMessageCenter.setNoMoreData();
                    }
                    else
                        lvMessageCenter.setPullLoadEnable(true);

                    for (int i = 0; i < length; i++) {
                        Map<String, String> map = new HashMap<>();
                        JSONObject record = data.getJSONObject(i);
                        map.put("mailTitle", record.getString("mailTitle"));
                        map.put("mailContent", record.getString("mailContent"));
                        map.put("mailId", record.getString("mailId"));
                        map.put("sendTime", record.getString("sendTime"));
                        map.put("mailStatus", record.getString("mailStatus"));
                        list.add(map);
                    }
                    if (adapter == null) {
                        adapter = new MyAdapter(list);
                        lvMessageCenter.setAdapter(adapter);
                        listTotal.addAll(list);
                    } else {
                        if (adapter.isNoData) {
                            adapter = new MyAdapter(list);
                            lvMessageCenter.setAdapter(adapter);
                            listTotal.addAll(list);
                        } else{
                            listTotal.addAll(list);
                            adapter.update(listTotal);
                            adapter.notifyDataSetChanged();
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
        lvMessageCenter.stopRefresh();
        lvMessageCenter.stopLoadMore();
    }

    @OnClick({R.id.title_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                if(getIntent()!=null && getIntent().hasExtra("isReadMail")) setResult(RedPacket.RESULT_RED_PACKET);
                finish();
                break;
            default:break;
        }
    }

    @Override
    public void onBackPressed() {
        if(getIntent()!=null && getIntent().hasExtra("isReadMail")) setResult(RedPacket.RESULT_RED_PACKET);
        super.onBackPressed();
    }

    class MyAdapter extends BaseAdapter {
        private List<Map<String, String>> list = new ArrayList<>();
        private boolean isNoData;
        private int height;
        protected LayoutInflater mInflater;

        public MyAdapter(List<Map<String, String>> listget) {
            this.list = listget;
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
                ImageView iv = ButterKnife.findById(convertView, R.id.iv_placeholder_image);
                iv.setImageResource(R.mipmap.message_center_nodata);
                rootView.setLayoutParams(params);
                return convertView;
            }
            MyAdapter.Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_message_center, parent, false);
                holder = new MyAdapter.Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (MyAdapter.Holder) convertView.getTag();
            }
            holder.title.setText(TextUtils.isEmpty(list.get(position).get("mailTitle")) ? "":list.get(position).get("mailTitle"));
            holder.text.setText(list.get(position).get("mailContent"));
            holder.time.setText(list.get(position).get("sendTime"));
            int mailStatus = Convert.strToInt(list.get(position).get("mailStatus").toString(), 1);
            if (mailStatus == 1) {
//                holder.rl_message_item.setBackgroundResource(R.drawable.message_item_selector);
                holder.title.setTextColor(getResources().getColor(R.color.text_color_7, null));
                holder.text.setTextColor(getResources().getColor(R.color.text_color_7, null));
                holder.time.setTextColor(getResources().getColor(R.color.text_color_2, null));
            } else if (mailStatus == 3) {
//                holder.rl_message_item.setBackgroundResource(R.drawable.message_unread_item_selector);
                holder.title.setTextColor(getResources().getColor(R.color.text_color_4, null));
                holder.text.setTextColor(getResources().getColor(R.color.text_color_4, null));
                holder.time.setTextColor(getResources().getColor(R.color.text_color_4, null));
            }
            return convertView;
        }

        class Holder {
            @BindView(R.id.tv_invite_user)
            TextView title;
            @BindView(R.id.tv_register_time)
            TextView text;
            @BindView(R.id.tv_invite0)
            TextView time;
//            @BindView(R.id.imageview_mailStatus)
//            ImageView imageview_mailStatus;
            @BindView(R.id.rl_message_item)
            RelativeLayout rl_message_item;

            Holder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode == RESPONSE_CODE){
            lvMessageCenter.autoRefresh();
        }else if(requestCode==REQUEST_CODE && resultCode == RESPONSE_CODE_1){
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onDestroy() {
        sp=null;
        mHandler=null;
        super.onDestroy();
    }
}
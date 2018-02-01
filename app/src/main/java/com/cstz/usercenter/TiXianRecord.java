package com.cstz.usercenter;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.common.App.PostType;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.cstz_android.R;
import com.cstz.tools.Convert;
import com.cstz.tools.ToastMakeText;
import com.cstz.ui.xlistview.XListView;
import com.cstz.ui.xlistview.XListView.IXListViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 提现记录
 *
 * @author ZP
 */
public class TiXianRecord extends MyActivity implements OnClickListener, IXListViewListener {

    private TextView title_tv;
    private ImageView title_back;
    private XListView _listview;
    private MyAdapter adapter = null;
    private List<Map<String, Object>> listget = new ArrayList<Map<String, Object>>();
    private RelativeLayout mLayoutListView = null;
    private static Handler mhandler = new Handler();
    private int _pageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_tixian_record);
        initview();
        initData(_pageNum, true);
    }

    /**
     * 初始化控件
     */
    public void initview() {
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("提现记录");
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        _listview = (XListView) findViewById(R.id.home_tixian_listview);
        mLayoutListView = (RelativeLayout) findViewById(R.id.home_tixian_listview_layout);
        _listview.setPullRefreshEnable(false);
        _listview.setPullLoadEnable(true);   //下拉加载
        _listview.setXListViewListener(this);
        listener();
    }

    public void initData(int pageNum, boolean isShow) {
        SharedPreferencesData data = new SharedPreferencesData(TiXianRecord.this);
        Map<String, Object> map = new HashMap<>();
        map.put("token", data.getValue("token"));
        map.put("pageNum", pageNum);
        map.put("isDeposit", "0");//0：原账户,1:存管-借款人
        map.put("fundtype", "0");////0全部，1已提现，2审核中，3转账中+审核中 ，4失败
        postData(map, "/user/queryWithdrawList", isShow == true ? mLayoutListView : null, PostType.LOAD);
    }

    public void reloadAcitivtyView(RelativeLayout view) {
        super.reloadAcitivtyView(view);
        setContentView(R.layout.home_tixian_record);
        initview();
    }

    public void requestFail(String msg, PostType postType) {
        super.requestFail(msg, postType);
        ToastMakeText.showToast(this, msg, 1200);
    }

    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            try {
                JSONArray data = object.getJSONArray("data");
                if (data != null) {
                    int length = data.length();
                    for (int i = 0; i < length; i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        JSONObject record = data.getJSONObject(i);
                        map.put("bankName", record.getString("bankName"));
                        map.put("cardNumber", record.getString("acount"));
                        map.put("money", record.getString("sum"));
                        map.put("status", record.getInt("status"));
                        map.put("time", record.getString("applyTime"));
                        listget.add(map);
                    }
                    if (length < 15) {
                        _listview.setPullLoadEnable(false);
                    } else {
                        _listview.setPullLoadEnable(true);
                    }
                    if (adapter == null) {
                        adapter = new MyAdapter(listget);
                        _listview.setAdapter(adapter);
                    } else {
                        if (_listview.getAdapter() == null) {
                            _listview.setAdapter(adapter);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void listener() {
        title_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            default:break;
        }
    }

    class MyAdapter extends BaseAdapter {
        private List<Map<String, Object>> listget = new ArrayList<>();
        public MyAdapter(List<Map<String, Object>> listget) {
            this.listget = listget;
        }
        @Override
        public int getCount() {
            return listget.size();
        }

        @Override
        public Object getItem(int position) {
            return listget.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            myhouder houdler = null;
            if (convertView == null) {
                houdler = new myhouder();
                convertView = LayoutInflater.from(TiXianRecord.this).inflate(R.layout.home_tixian_record_list, null);
                houdler.textView_bankName = (TextView) convertView.findViewById(R.id.textView_bankName);
                houdler.textView_acount = (TextView) convertView.findViewById(R.id.textView_acount);
                houdler.textView_sum = (TextView) convertView.findViewById(R.id.textView_sum);
                houdler.imageViews_tatus = (ImageView) convertView.findViewById(R.id.imageViews_tatus);
                houdler.textView_time = (TextView) convertView.findViewById(R.id.textView_time);
                houdler.bankcard_img = (ImageView) convertView.findViewById(R.id.bankcard_img);
                convertView.setTag(houdler);
            } else {
                houdler = (myhouder) convertView.getTag();
            }
            //状态(1 默认审核中  2 已提现 3 取消 4转账中 5 失败)
            int status = Convert.toInt(listget.get(position).get("status").toString());
            houdler.textView_bankName.setText(listget.get(position).get("bankName").toString());
            houdler.textView_acount.setText("(" + listget.get(position).get("cardNumber").toString() + ")");
            houdler.textView_sum.setText("-" + listget.get(position).get("money").toString());
            houdler.bankcard_img.setImageResource(Config.getBankNameLogo(listget.get(position).get("bankName").toString()));
            switch (status) {
                case 1:
                    houdler.imageViews_tatus.setBackgroundResource(R.drawable.tx_chuli);
                    break;
                case 2:
                    houdler.imageViews_tatus.setBackgroundResource(R.drawable.tx_chenggong);
                    break;
                case 4:
                    houdler.imageViews_tatus.setBackgroundResource(R.drawable.tx_chuli);
                    break;
                case 5:
                    houdler.imageViews_tatus.setBackgroundResource(R.drawable.tx_shibai);
                    break;
                default:break;
            }
            houdler.textView_time.setText(listget.get(position).get("time").toString());
            viewPaint(houdler.textView_sum);
            return convertView;
        }

        class myhouder {
            private TextView textView_bankName;
            private TextView textView_acount;
            private TextView textView_sum;
            private ImageView imageViews_tatus;
            private TextView textView_time;
            private ImageView bankcard_img;
        }
    }

    private void onLoad() {
        _listview.stopRefresh();
        _listview.stopLoadMore();
    }

    public void onRefresh() {
    }

    @Override
    public void requestFinished() {
        super.requestFinished();
        onLoad();
    }

    public void onLoadMore() {
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData(++_pageNum, false);
            }
        }, 600);
    }

    public void viewPaint(TextView view) {
        TextPaint tp = view.getPaint();
        tp.setFakeBoldText(true);
    }
}
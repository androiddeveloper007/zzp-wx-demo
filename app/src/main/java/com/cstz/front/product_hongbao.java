package com.cstz.front;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cstz.common.MyActivity;
import com.cstz.cstz_android.R;
import com.cstz.tools.MyDIYParcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 选择红包
 *
 * @author rxh
 */
public class product_hongbao extends MyActivity implements OnClickListener, OnItemClickListener {
    private TextView title_tv;
    private ImageView title_back;
    private ListView listview;
    private int selectPosition = -1;//用于记录选中的下标
    private Myadapter adapter;
    private List<Map<String, Object>> listget = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.front_product_hongbao);
        setTransparentStatusBar(this,false);
        initview();
        initData();
    }

    /**
     * 控件的初始化
     */
    public void initview() {
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("选择红包");
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        listview = (ListView) findViewById(R.id.front_product_hongbao_listview);
        listener();
    }

    public void listener() {
        title_back.setOnClickListener(this);
        listview.setOnItemClickListener(this);
    }

    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                MyDIYParcelable parcelable = intent.getParcelableExtra("list");
                ArrayList<List<Map<String, Object>>> list = parcelable.bundlelist;
                listget = (List<Map<String, Object>>) list.get(0);
                selectPosition = intent.getIntExtra("selectIndex", -1);
                adapter = new Myadapter(listget);
                listview.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                product_hongbao.this.finish();
                break;
            default:
                break;
        }
    }

    class Myadapter extends BaseAdapter {
        private List<Map<String, Object>> listget = new ArrayList<Map<String, Object>>();

        public Myadapter(List<Map<String, Object>> listget) {
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
                convertView = LayoutInflater.from(product_hongbao.this).inflate(R.layout.front_product_hongbao_list, null);
                houdler.textview_redwrap = (TextView) convertView.findViewById(R.id.textview_redwrap);
                houdler.textview_redproportion = (TextView) convertView.findViewById(R.id.textview_redproportion);
                houdler.select_hongbao = (RadioButton) convertView.findViewById(R.id.id_select_hongbao);
                convertView.setTag(houdler);
            } else {
                houdler = (myhouder) convertView.getTag();
            }
            if(position==listget.size()-1){
                houdler.textview_redproportion.setVisibility(View.GONE);
                houdler.textview_redwrap.setText("不使用红包");
            }else{
                houdler.textview_redproportion.setVisibility(View.VISIBLE);
                houdler.textview_redwrap.setText("¥" + listget.get(position).get("redwrap").toString());
                houdler.textview_redproportion.setText("投资满" + listget.get(position)
                        .get("redproportion").toString() + "元可用");
            }
            if (selectPosition == position) {
                houdler.select_hongbao.setChecked(true);
            } else {
                houdler.select_hongbao.setChecked(false);
            }
            return convertView;
        }

        class myhouder {
            private TextView textview_redwrap;
            private TextView textview_redproportion;
            private RadioButton select_hongbao;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        if (selectPosition == position) {
//            selectPosition = -1;
//            adapter.notifyDataSetChanged();
//
//            intent.putExtra("selectedIndex", -1);
        } else {

            selectPosition = position;
            adapter.notifyDataSetChanged();

            intent.putExtra("redwrap", listget.get(position).get("redwrap").toString());
            intent.putExtra("code", listget.get(position).get("code").toString());

            intent.putExtra("selectedIndex", position);
            setResult(150, intent);
            finish();
        }
    }

    /**
     * 字体加粗
     */
    public void viewPaint(TextView view) {
        TextPaint tp = view.getPaint();
        tp.setFakeBoldText(true);
    }
}

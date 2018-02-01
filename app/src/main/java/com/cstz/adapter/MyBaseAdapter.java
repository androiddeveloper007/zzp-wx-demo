package com.cstz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cstz.cstz_android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyBaseAdapter extends BaseAdapter {
	private List<Map<String, Object>> listget = new ArrayList<Map<String, Object>>();
	private Context context;
	public MyBaseAdapter(List<Map<String, Object>> listget,Context context) {
		this.listget = listget;
		this.context = context;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.front_product_invest_record_list,null);
			houdler.username = (TextView) convertView.findViewById(R.id.front_product_invest_record_list_userrealname);
			houdler.investAmount = (TextView) convertView.findViewById(R.id.front_product_invest_record_list_investamount);
			houdler.investMode = (TextView) convertView.findViewById(R.id.front_product_invest_record_list_investmode);
			houdler.investTime = (TextView) convertView.findViewById(R.id.front_product_invest_record_list_investtime);
			convertView.setTag(houdler);
		} else {
			houdler = (myhouder) convertView.getTag();
		}
		houdler.username.setText(listget.get(position).get("username").toString());
		houdler.investAmount.setText(listget.get(position).get("investAmount").toString());
		houdler.investMode.setText(listget.get(position).get("investMode").toString());
		houdler.investTime.setText(listget.get(position).get("investTime").toString());
		//viewPaint(houdler.investAmount);
		return convertView;
	}

	class myhouder {
		private TextView username;
		private TextView investAmount;
		private TextView investMode;
		private TextView investTime;
	}
}

package com.cstz.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.cstz_android.R;
import com.cstz.tools.Convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author rxh
 *
 */
public class ItemAdapter  extends BaseAdapter{
	private List<Map<String, Object>> listget = new ArrayList<Map<String,Object>>();
	private Activity _activity;
	
	public ItemAdapter(Activity activity,List<Map<String, Object>> listget){
		this.listget = listget;
		_activity = activity;
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
		Item houdler = null;
		if(convertView==null)
		{
			houdler = new Item();
			convertView = LayoutInflater.from(_activity).inflate(R.layout.list, null);
			houdler.image = (ImageView) convertView.findViewById(R.id.table_item_image);
			houdler.title = (TextView) convertView.findViewById(R.id.table_item_title);
			houdler.content = (TextView) convertView.findViewById(R.id.table_item_content);
			houdler.msg = (TextView) convertView.findViewById(R.id.table_item_msg);
			houdler.arrow = (ImageView) convertView.findViewById(R.id.table_item_arrow);
			convertView.setTag(houdler);
		}else{
			houdler = (Item) convertView.getTag();
		}
		if(!TextUtils.isEmpty(listget.get(position).get("image").toString()))
		{
			houdler.image.setImageResource(Convert.toInt(listget.get(position).get("image").toString()));
		}
		
		houdler.title.setText(listget.get(position).get("title").toString());
		houdler.content.setText(listget.get(position).get("content").toString());
		houdler.msg.setText(listget.get(position).get("msg").toString());
		if(!TextUtils.isEmpty(listget.get(position).get("arrow").toString()))
		{
			houdler.arrow.setImageResource(0);
		}else
		{
			houdler.arrow.setVisibility(View.GONE);
		}

		return convertView;
	}
	
	class Item{
		private ImageView image;
		private TextView title;
		private TextView content;
		private TextView msg;
		private ImageView arrow;
	}
}





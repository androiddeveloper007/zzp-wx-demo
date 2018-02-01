package com.cstz.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.common.AsyncImageLoader;
import com.cstz.cstz_android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
	private List<Map<String, Object>> listget = new ArrayList<Map<String, Object>>();
	private Context context;

	public MyAdapter(List<Map<String, Object>> listget, Context c) {
		this.listget = listget;
		this.context=c;
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
		final myhouder houdler;
		if (convertView == null) {
			houdler = new myhouder();
			convertView = LayoutInflater.from(context).inflate(R.layout.reference_documents_list, parent,false);//null
			houdler.imageView_zjitem_pic = (ImageView) convertView.findViewById(R.id.imageView_wenjiain_image);
			houdler.textView_zjitem_name = (TextView) convertView.findViewById(R.id.textView_wenjian_name);
			convertView.setTag(houdler);
		} else {
			houdler = (myhouder) convertView.getTag();
		}
		houdler.textView_zjitem_name.setText(listget.get(position).get("name").toString());
		String picurl = listget.get(position).get("path").toString();

		if(picurl.contains("http://www.wuxingjinrong.com/")){
			picurl = picurl.replaceAll("http", "https");
		}

		AsyncImageLoader loader = new AsyncImageLoader(context);
		loader.setCache2File(true);

		loader.downloadImage(picurl, true, new AsyncImageLoader.ImageCallback() {
			@Override
			public void onImageLoaded(Bitmap bitmap, String imageUrl) {
				if(bitmap!=null){
					houdler.imageView_zjitem_pic.setImageBitmap(bitmap);
				}else{
					houdler.imageView_zjitem_pic.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_stub));
				}
			}
		});

		return convertView;
	}

	class myhouder {
		private ImageView imageView_zjitem_pic;
		private TextView textView_zjitem_name;
	}
}

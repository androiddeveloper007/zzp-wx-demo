package com.cstz.common.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.cstz.cstz_android.R;
import com.cstz.ui.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 可用红包列表对话框
 * @author zzp
 *
 */
public class UseableRedEnvelopeDialog extends BaseTitleDialog_usableEnvelope implements View.OnClickListener{//, XListView.IXListViewListener

	public TextView tvMsg;

	protected Button btn1;
//	protected ImageView ivLine;
	private XListView lv_dialog_usable_envelope;
	private Context context;
//	private List<Map<String, Object>> redPacketList = new ArrayList<Map<String, Object>>();;
	public Myadapter adapter;

	public UseableRedEnvelopeDialog(Context ctx,List<Map<String, Object>> redPacketList) {
		this(ctx, true, redPacketList);
	}

	public UseableRedEnvelopeDialog(Context ctx, boolean cancelable, List<Map<String, Object>> redPacketList) {
		super(ctx);
		this.context = ctx;
		setContentView(View.inflate(getContext(), R.layout.dialog_usable_envelope, null),
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		tvMsg = (TextView) findViewById(R.id.tv_msg_pattern_warn);
		
		setCancelable(cancelable);
		
		setCanceledOnTouchOutside(false);
		
		btn1 = (Button) findViewById(R.id.btn_pattern_warn);
		
		btn1.setOnClickListener(this);
		
		setIvCloseVisible(false);

//		this.redPacketList = redPacketList;

		lv_dialog_usable_envelope = (XListView) findViewById(R.id.lv_dialog_usable_envelope);
		lv_dialog_usable_envelope.setPullRefreshEnable(false);
		lv_dialog_usable_envelope.setPullLoadEnable(false);   //下拉加载
		adapter = new Myadapter(redPacketList);
		lv_dialog_usable_envelope.setAdapter(adapter);
	}
	
	private View.OnClickListener onBtn1ClickListener;

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_pattern_warn) {
			if (onBtn1ClickListener != null) {
				onBtn1ClickListener.onClick(v);
			}
			cancel();
		}
	}
	
	public void setMessage(String msg) {
		tvMsg.setText(msg);
	}
	
	public void setBtn1Text(String text) {
		btn1.setText(text);
	}
	
	
	public void setBtn1Visibility(boolean visible) {
		if (visible) {
			btn1.setVisibility(View.VISIBLE);
		} else {
			btn1.setVisibility(View.GONE);
		}
	}
	
	public void setOnBtn1ClickListener(View.OnClickListener onClickListener) {
		this.onBtn1ClickListener = onClickListener;
	}

	@Override
	public void onclickclose(View v) {
		if (onBtn1ClickListener != null) {
			onBtn1ClickListener.onClick(v);
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
			Holder holder;
			if (convertView == null) {
				holder = new Holder();
				convertView = LayoutInflater.from(context).inflate(R.layout.dialog_useable_envelope_list, null);
				holder.select_hongbao = (TextView) convertView.findViewById(R.id.tv_envelope_dialog);
				holder.textview_redwrap = (TextView) convertView.findViewById(R.id.tv_redwrap_dialog);
				holder.textview_redproportion = (TextView) convertView.findViewById(R.id.tv_redproportion_dialog);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.select_hongbao.setText(listget.get(position).get("redwrap").toString()+"元");
			String term = listget.get(position).get("term").toString();
			if(TextUtils.equals("0", term))
				holder.textview_redwrap.setText("不限");
			else
				holder.textview_redwrap.setText("≥" + term +"天");
			holder.textview_redproportion.setText("单笔满" + listget.get(position).get("redproportion").toString() + "元");
			return convertView;
		}

		class Holder {
			private TextView textview_redwrap;
			private TextView textview_redproportion;
			private TextView select_hongbao;
		}
	}
}

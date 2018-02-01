package com.cstz.common.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cstz.cstz_android.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateDialog extends BaseDialog {

    public boolean ignore_update;
    @BindView(R.id.tv_update_version)
    TextView tvUpdateVersion;
    @BindView(R.id.cb_update)
    CheckBox cbUpdate;
    @BindView(R.id.tv_update_left)
    TextView tvUpdateLeft;
    @BindView(R.id.tv_update_right)
    TextView tvUpdateRight;
    @BindView(R.id.tv_update_tip)
    TextView tv_update_tip;
    @BindView(R.id.ll_update_checkbox)
    LinearLayout llUpdateCheckbox;
    @BindView(R.id.lv_updatelog)
    ListView lvUpdatelog;
    private Context context;

    public UpdateDialog(Context context,List<String> list) {
        super(context);
        this.context = context;

        View v = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
        super.setContentView(v);

        ButterKnife.bind(this, v);

        Window window = getWindow();
        WindowManager.LayoutParams attributesParams = window.getAttributes();
        attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributesParams.dimAmount = 0.4f;

        @SuppressWarnings("deprecation")
        int sreemWidth = window.getWindowManager().getDefaultDisplay().getWidth();
        int windowWidth = (int) (sreemWidth * 0.85);

        window.setLayout(windowWidth, LayoutParams.WRAP_CONTENT);
        setCanceledOnTouchOutside(false);

        cbUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ignore_update = isChecked;
                if (isChecked)
                    tvUpdateLeft.setText("不再提醒");
                else
                    tvUpdateLeft.setText("下次再说");
            }
        });

        lvUpdatelog.setAdapter(new MyAdapter(list));
    }

    private View.OnClickListener onBtn1ClickListener;
    private View.OnClickListener onBtn2ClickListener;

    public void setOnBtn1ClickListener(View.OnClickListener onClickListener) {
        this.onBtn1ClickListener = onClickListener;
    }

    public void setOnBtn2ClickListener(View.OnClickListener onClickListener) {
        this.onBtn2ClickListener = onClickListener;
    }

    public void setversion(String s) {
        tvUpdateVersion.setText("发现新版本:"+s);
    }

    public boolean getBooleanIgnore() {
        return this.ignore_update;
    }

    @OnClick({R.id.tv_update_left, R.id.tv_update_right})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_update_left:
                if (onBtn1ClickListener != null)
                    onBtn1ClickListener.onClick(v);
                cancel();
                break;
            case R.id.tv_update_right:
                if (onBtn2ClickListener != null)
                    onBtn2ClickListener.onClick(v);
                cancel();
                break;
            default:
                break;
        }
    }

    public void setCheckboxVisibility(boolean b) {
        if (!b)
            llUpdateCheckbox.setVisibility(View.GONE);
        else
            llUpdateCheckbox.setVisibility(View.VISIBLE);
    }

    class MyAdapter extends BaseAdapter {
        private List<String> list = new ArrayList<String>();

        public MyAdapter(List<String> list) {
            this.list = list;
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
            UpdateDialog.MyAdapter.Holder holder = null;
            if (convertView == null) {
                holder = new UpdateDialog.MyAdapter.Holder();
                convertView = LayoutInflater.from(context).inflate(R.layout.dialog_updatelog_item, null);
                holder.tv_update_item = (TextView) convertView.findViewById(R.id.tv_update_item);
                convertView.setTag(holder);
            } else {
                holder = (UpdateDialog.MyAdapter.Holder) convertView.getTag();
            }
            holder.tv_update_item.setText(list.get(position).toString());
            return convertView;
        }

        class Holder {
            private TextView tv_update_item;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
    }

    public void setTipVisible(boolean b){
        tv_update_tip.setVisibility(b?View.VISIBLE:View.GONE);
    }
}
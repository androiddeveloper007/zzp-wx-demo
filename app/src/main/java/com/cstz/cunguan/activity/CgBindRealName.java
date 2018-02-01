package com.cstz.cunguan.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.cstz_android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 实名认证
 */
public class CgBindRealName extends MyActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.tv_bind_id_name)
    TextView tvBindIdName;
    @BindView(R.id.tv_bind_id_number)
    TextView tvBindIdNumber;
    @BindView(R.id.title_back)
    ImageView title_back;
    private SharedPreferencesData sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_bindrealname);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        sp = new SharedPreferencesData(this);
        initView();
    }

    public void initView() {
        title_tv.setText("实名认证");
        title_back.setVisibility(View.VISIBLE);
        tvBindIdName.setText("姓名："+sp.getValue("realName"));
        tvBindIdNumber.setText("身份证："+sp.getValue("idNo"));
    }

    @OnClick(R.id.title_back)
    public void onClick() {
        finish();
    }
}
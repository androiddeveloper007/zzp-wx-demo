package com.cstz.cunguan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.cstz_android.R;
import com.cstz.tools.Convert;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 银行卡老帐户
 */
public class BankCard extends MyActivity implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.iv_bank_logo)
    ImageView ivBankLogo;
    @BindView(R.id.tv_bankcard_num)
    TextView tv_bankcard_num;
    @BindView(R.id.tv_bank_name)
    TextView tv_bank_name;
    @BindView(R.id.tv_bank_user)
    TextView tvBankUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_bankcard);
        setTransparentStatusBar(this,false);
        ButterKnife.bind(this);
        titleTv.setText("银行卡");
        titleBack.setVisibility(View.VISIBLE);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            String cardIdNo = Convert.strToStr(intent.getStringExtra("cardNo").toString(), "");
            String bankName = Convert.strToStr(intent.getStringExtra("bankName").toString(), "");
//            String bankMobilePhone = intent.getStringExtra("bankMobilePhone").toString();
            ivBankLogo.setImageResource(Config.getBankNameLogo(bankName));
            tv_bank_name.setText(bankName);
//            tvBankName.setText(bankName);
            tv_bankcard_num.setText(cardIdNo);
            tvBankUser.setText("持卡人："+new SharedPreferencesData(this).getValue("realName"));//bankMobilePhone
        }
    }

    @OnClick({R.id.title_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            default:break;
        }
    }
}
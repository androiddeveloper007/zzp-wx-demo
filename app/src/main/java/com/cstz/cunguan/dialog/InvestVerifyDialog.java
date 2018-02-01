package com.cstz.cunguan.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.MyWidget.MClearEditText;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.view.BaseDialog;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.widget.SmsSecondView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 投标验证码
 *
 * @author zzp
 */
public class InvestVerifyDialog extends BaseDialog {
    @BindView(R.id.et_invest_verify)
    public MClearEditText etInvestVerify;
    @BindView(R.id.tv_invest_verify_get_code)
    public SmsSecondView tvInvestVerifyGetCode;
    @BindView(R.id.tv_invest_verify)
    TextView tv_invest_verify;
    @BindView(R.id.layout_button_no)
    LinearLayout layoutButtonNo;
    @BindView(R.id.layout_button_yes)
    LinearLayout layoutButtonYes;
    private LinearLayout frContent;

    public InvestVerifyDialog(Context context, double p) {
        super(context);
        super.setContentView(R.layout.dialog_cunguan_invest_verify);
        ButterKnife.bind(this);
        Window window = getWindow();
        WindowManager.LayoutParams attributesParams = window.getAttributes();
        attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributesParams.dimAmount = 0.4f;
        setCanceledOnTouchOutside(false);
        @SuppressWarnings("deprecation")
        int sreemWidth = window.getWindowManager().getDefaultDisplay().getWidth();
        int windowWidth = (int) (sreemWidth * p);
        window.setLayout(windowWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        frContent = (LinearLayout) findViewById(R.id.fr_content);
        tv_invest_verify.setText("请输入"+new SharedPreferencesData(context).getValue("phone")+"接收的短信验证码");
    }

    @Override
    public void setContentView(View view) {
        frContent.removeAllViews();
        frContent.addView(view, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams lp) {
        frContent.removeAllViews();
        frContent.addView(view, lp);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = View.inflate(getContext(), layoutResID, null);
        setContentView(view);
    }

    @OnClick({R.id.layout_button_no, R.id.layout_button_yes, R.id.tv_invest_verify_get_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_button_no:
                dismiss();
                tvInvestVerifyGetCode.resetCount();
                break;
            case R.id.layout_button_yes:
                if (listener != null)
                    listener.click();
                break;
            case R.id.tv_invest_verify_get_code:
                if (listener1 != null)
                    listener1.click();
//                tvInvestVerifyGetCode.startSend();
                break;
            default:break;
        }
    }

    public interface onClickListener {
        void click();
    }

    public interface onClickListener1 {
        void click();
    }

    public onClickListener listener;

    public void setOnRightClickListener(onClickListener listener) {
        this.listener = listener;
    }

    public onClickListener1 listener1;

    public void setOnCodeClickListener(onClickListener1 listener) {
        this.listener1 = listener;
    }

    public void refreshText(String str) {
        tvInvestVerifyGetCode.setText(str);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        tvInvestVerifyGetCode.resetCount();
    }
}
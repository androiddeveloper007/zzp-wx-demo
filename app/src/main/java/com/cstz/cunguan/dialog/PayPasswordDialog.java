package com.cstz.cunguan.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
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
import com.cstz.tools.ToastMakeText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 还款密码输入框
 *
 * @author zzp
 */
public class PayPasswordDialog extends BaseDialog {
    @BindView(R.id.et_invest_verify)
    public MClearEditText etInvestVerify;
    @BindView(R.id.tv_invest_verify_tip)
    public TextView tv_invest_verify_tip;
    @BindView(R.id.tv_title_invest_verify)
    public TextView tv_title_invest_verify;
    private LinearLayout frContent;
    private Activity activity;

    public PayPasswordDialog(Context context, double p) {
        super(context);
        super.setContentView(R.layout.dialog_pay_password);
        activity = (Activity) context;
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
        boolean hasRepaymentPwdEdit = new SharedPreferencesData(context).getOtherBoolean("hasRepaymentPwdEdit");
        setTipVisible(hasRepaymentPwdEdit);
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

    @OnClick({R.id.layout_button_no, R.id.layout_button_yes})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_button_no:
                dismiss();
                break;
            case R.id.layout_button_yes:
                if(TextUtils.isEmpty(etInvestVerify.getText())){
                    ToastMakeText.showToast(activity,"请输入还款密码！",3000);return;}
                if(listener!=null)
                    listener.click();
                break;
        }
    }

    public interface onClickListener {
        void click();
    }

    public onClickListener listener;

    public void setOnRightClickListener(onClickListener listener) {

        this.listener = listener;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setTipVisible(boolean b){
        tv_invest_verify_tip.setVisibility(!b?View.VISIBLE:View.GONE);
    }

    public void setTitle(String s){
        tv_title_invest_verify.setText(s);
    }
}
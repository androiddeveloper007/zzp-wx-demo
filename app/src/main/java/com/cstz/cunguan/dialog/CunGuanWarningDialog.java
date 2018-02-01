package com.cstz.cunguan.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.common.view.BaseDialog;
import com.cstz.cstz_android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 开通存管对话框
 *
 * @author zzp
 */
public class CunGuanWarningDialog extends BaseDialog {
    @BindView(R.id.iv_cg_notice_close)
    ImageView ivCgNoticeClose;
    @BindView(R.id.tv_cg_warning_product)
    TextView tvCgWarningProduct;
    @BindView(R.id.ll_cg_apply)
    LinearLayout llCgApply;
    @BindView(R.id.ll_cg_apply_btns)
    LinearLayout llCgApplyBtns;
    @BindView(R.id.view_cg_warning)
    View viewCgWarning;
    private LinearLayout frContent;
    private onApplayButtonClick mListener;
    private Activity activity;

    public CunGuanWarningDialog(Context context, double p) {
        super(context);
        super.setContentView(R.layout.dialog_cungaun_warning);
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

    @OnClick({R.id.iv_cg_notice_close, R.id.ll_cg_apply, R.id.ll_cg_apply_no, R.id.ll_cg_apply_yes})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_cg_notice_close:
                dismiss();
                break;
            case R.id.ll_cg_apply:
                if(mListener!=null)
                    mListener.applyClick();
                break;
            case R.id.ll_cg_apply_no:
                dismiss();
                break;
            case R.id.ll_cg_apply_yes:
                if(mListener!=null)
                    mListener.applyClick();
                break;
            default:break;
        }
    }

    public void showImageBottom() {
        tvCgWarningProduct.setVisibility(View.VISIBLE);
    }

    public void hideBtn0() {//和hideBtn1对应两种样式，分别调用即可
        llCgApply.setVisibility(View.GONE);
        llCgApplyBtns.setVisibility(View.VISIBLE);
        tvCgWarningProduct.setVisibility(View.VISIBLE);
        ivCgNoticeClose.setVisibility(View.GONE);
        viewCgWarning.setVisibility(View.VISIBLE);
    }

    public void hideBtn1() {
        llCgApply.setVisibility(View.VISIBLE);
        llCgApplyBtns.setVisibility(View.GONE);
        tvCgWarningProduct.setVisibility(View.GONE);
        ivCgNoticeClose.setVisibility(View.VISIBLE);
        viewCgWarning.setVisibility(View.GONE);
    }

    public void setStyleInvest(boolean b){
        if(!b){
            tvCgWarningProduct.setVisibility(View.VISIBLE);
//            tvCgWarningProduct.setTextColor(activity.getResources().getColor(R.color.cg_warning_dialog_tv));
//            tvCgWarningProduct.setBackgroundColor(0xffffffff);
            tvCgWarningProduct.setText("请开通存管后再充值（提现）");
        }
    }

    public interface onApplayButtonClick{
        void applyClick();
    }

    public void setOnApplyClickListener(onApplayButtonClick listener){
        this.mListener = listener;
    }
}
package com.cstz.cunguan.dialog;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.common.view.BaseDialog;
import com.cstz.cstz_android.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 老用户未开通存管，充值，提现时弹出的提示框
 *
 * @author zzp
 */
public class TipOpenCgDialog extends BaseDialog {
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.tv_left_btn_exit)
    TextView tvLeftBtnExit;
    @BindView(R.id.tv_right_btn_exit)
    TextView tvRightBtnExit;
    @BindView(R.id.tv_center_btn_exit)
    TextView tv_center_btn_exit;
    @BindView(R.id.layout_button_yes)
    public LinearLayout layoutButtonYes;
    @BindView(R.id.layout_button_no)
    public LinearLayout layout_button_no;
    @BindView(R.id.layout_button_center)
    public LinearLayout layout_button_center;
    private LinearLayout frContent;

    public TipOpenCgDialog(Context context, double p) {
        super(context);
        super.setContentView(R.layout.dialog_cungaun_tip);
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

    @OnClick({R.id.layout_button_no, R.id.layout_button_yes,R.id.layout_button_center})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_button_no:
                dismiss();
                break;
            case R.id.layout_button_yes:
                listener.click();
                break;
            case R.id.layout_button_center:
                listener.click();
                break;
            default:break;
        }
    }

    public interface onClickListener {
        void click();
    }

    public onClickListener listener;

    public void setOnRightClickListener(onClickListener listener) {
        this.listener = listener;
    }

    public void setOnCenterClickListener(onClickListener listener) {
        this.listener = listener;
    }

    public void setText(String str) {
        tvExit.setText(str);
    }

    public void setTextSize(int dimen){
        tvExit.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(dimen));
    }

    public void setLeftBtnText(String str) {
        tvLeftBtnExit.setText(str);
    }

    public void setRightBtnText(String str) {
        tvRightBtnExit.setText(str);
    }

    public void setCenterBtnText(String str) {
        tv_center_btn_exit.setText(str);
    }

    public void showCenterBtn(){
        layout_button_no.setVisibility(View.GONE);
        layoutButtonYes.setVisibility(View.GONE);
        layout_button_center.setVisibility(View.VISIBLE);
    }
}
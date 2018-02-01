package com.cstz.cunguan.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cstz.common.Config;
import com.cstz.common.WebViewReleaseCg;
import com.cstz.common.view.BaseDialog;
import com.cstz.cstz_android.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 存管专题介绍页提示框
 *
 * @author zzp
 */
public class CunGuanIntroduceDialog extends BaseDialog {

    private LinearLayout frContent;

    public CunGuanIntroduceDialog(Context context, double p) {
        super(context);
        super.setContentView(R.layout.dialog_cungaun_introduce);
        ButterKnife.bind(this);
        Window window = getWindow();
        WindowManager.LayoutParams attributesParams = window.getAttributes();
        attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributesParams.dimAmount = 0.4f;
        setCanceledOnTouchOutside(false);
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

    @OnClick({R.id.cg_mail_close,R.id.tv_dialog_introduce})//
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cg_mail_close:
                dismiss();
                break;
            case R.id.tv_dialog_introduce:
                Intent i = new Intent(getContext(), WebViewReleaseCg.class);
                i.putExtra("url", Config.getHttpConfig() + "/view/bankguide/index.html");
                i.putExtra("title", "银行存管介绍");
                getContext().startActivity(i);
                dismiss();
                break;
        }
    }
}

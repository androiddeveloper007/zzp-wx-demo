package com.cstz.common.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cstz.cstz_android.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 活动通知对话框
 *
 * @author zzp
 */
public class EventNoticeDialog extends BaseDialog {
    private LinearLayout frContent;

    public EventNoticeDialog(Context context, double p) {
        super(context);
        super.setContentView(R.layout.dialog_event_notice);
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

    @OnClick(R.id.iv_close_event_notice)
    public void onClick() {
        dismiss();
    }
}

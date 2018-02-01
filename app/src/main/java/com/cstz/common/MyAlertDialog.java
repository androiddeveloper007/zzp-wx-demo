package com.cstz.common;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.cstz_android.R;

public class MyAlertDialog extends AlertDialog implements android.view.View.OnClickListener {


    private String _content;
    private MyCallback myCallback;

    public MyAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    public MyAlertDialog(Context context) {
        super(context);
    }

    public MyAlertDialog(Context context, String content) {
        super(context);
        this._content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.dialog_alert);
        TextView tv_content = (TextView) findViewById(R.id.dialog_alert_content);
        tv_content.setText(_content);

        LinearLayout lay_sure = (LinearLayout) findViewById(R.id.dialog_alert_sure);
        lay_sure.setOnClickListener(this);

        LinearLayout lay_cancel = (LinearLayout) findViewById(R.id.dialog_alert_cancel);
        lay_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_alert_sure:
                if (myCallback != null) {
                    myCallback.callback();
                }
                this.dismiss();
                break;
            case R.id.dialog_alert_cancel:
                this.dismiss();
                break;
            default:
                break;
        }
    }

    public void setMyCallback(MyCallback myCallback) {
        this.myCallback = myCallback;
    }
}

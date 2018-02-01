package com.cstz.cunguan.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.cstz.cstz_android.R;

/**
 * Created by ZP on 2017/6/9.
 */

public class SmsSecondView extends TextView {
    private Handler handler = new Handler();
    private int count = 60;
    public final static int SMS_TIME = 60;
    public static boolean hasSend = false;
    private Runnable myRunnable = new Runnable() {
        public void run() {
            handler.postDelayed(this, 1000);
            refreshSendButton(count);
            count--;
            if (count == 0) {
                count = SMS_TIME;
                stopSendButton();
            }
        }
    };

    public void refreshSendButton(int count) {
//        if(count == SMS_TIME)
//            setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.dimen_14));
        setText(count + "s");
    }

    public void stopSendButton() {
        handler.removeCallbacks(myRunnable);
        count = SMS_TIME;

        setBackgroundResource(R.drawable.btn_radius_sms_resend);
        setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.dimen_14));
        setTextColor(getResources().getColor(R.color.white));
        setText("重新发送");
        setClickable(true);
    }

    public void startSend() {
        setClickable(false);
        setBackgroundColor(getResources().getColor(R.color.white));
        setTextColor(getResources().getColor(R.color.navigation_cg));
        setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.dimen_24));
        handler.post(myRunnable); // 发送倒计时
    }

    public SmsSecondView(Context context) {
        super(context);
    }

    public SmsSecondView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmsSecondView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SmsSecondView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public void resetCount(){
        handler.removeCallbacks(myRunnable);
        count=60;
    }
}

package com.cstz.tools;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

public class ToastMakeText {
    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 200) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public synchronized static boolean isFastClick1() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static void showToast(final Activity activity, final String word, final long time) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(activity, word, Toast.LENGTH_SHORT);
                toast.show();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        toast.cancel();
                    }
                }, time);
            }
        });
    }

    public static void showToastLongDuration(final Activity activity, final String msg, final int duration) {
        final Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_LONG);
        toast.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    //线程开始休眠
                    Thread.sleep((long) (duration));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //这100毫秒只是用来让toast从隐藏到显示所给的时间，不然直接休眠线程的话，toast是无法显示出来的
        }, 100);
    }
}

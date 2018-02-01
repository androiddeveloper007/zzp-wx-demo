package com.cstz.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.common.App.AlertViewType;
import com.cstz.common.App.PostType;
import com.cstz.common.view.MyDialog;
import com.cstz.cstz_android.R;
import com.cstz.front.Login;
import com.cstz.tools.Device;
import com.cstz.tools.ToastMakeText;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class MyActivity_v4 extends FragmentActivity{

    /**
     * DeviceID
     *
     * @return
     */
    public String getDeviceId() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

        return deviceUuid.toString();
    }

    public static int SMS_TIME = 91;
    public static final int MAIL_STATUS_ALREADY = 3;
    public MyDialog mDialog = null;

    private RelativeLayout mAlertView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SysApplication.getInstance().addActivity(this);// 添加到SysApplication中，方便一次关闭//SysApplication.getInstance()
    }

    /**
     * 提交/获取网络数据
     *
     * @param map
     * @param action
     * @param postType
     */
    public void postData(final Map<String, Object> map, final String action, final PostType postType,
                         boolean isDialog) {
        if (isDialog) {

            String text = (postType == PostType.LOAD ? "加载中..." : "请稍等...");
            mDialog = new MyDialog(this, text);
            mDialog.setDuration(800);
            mDialog.setMyCallback(new MyCallback() {

                @Override
                public void callback() {

                }

                @Override
                public void doing() {
                    postData(map, action, null, postType);
                }

            });
            mDialog.showDialog();
        } else {
            postData(map, action, null, postType);
        }
    }

    /**
     * 提交/获取网络数据
     *
     * @param
     * @param
     */
    public void postData(Map<String, Object> map, final String action, RelativeLayout view, final PostType postType) {
        if (Device.getConnectedType(MyActivity_v4.this) == -1) {
            if (view != null) {
                mAlertView = view;
                alertView(AlertViewType.NO_CONNECTION);
            } else {
                ToastMakeText.showToast(MyActivity_v4.this, "当前网络已断开", 1000);
                requestNoConnection(postType);
            }
            return;
        }
        if (postType == PostType.MESSAGE) {
            handler.post(myRunnable); // 发送倒计时
        } else if (postType == PostType.SUBMIT) {

        } else if (postType == PostType.LOAD) {
            if (view != null) {
                mAlertView = view;
                alertView(AlertViewType.LOADING);
            }
        }

        String path = Config.getHttpConfig() + action;
        RequestParams params = new RequestParams(path);
        params.addParameter("rt", "app");
//		params.addParameter("sys", "android");
        if (action.contains("/loginAction") || action.contains("/registerAction")) {
            params.addParameter("deviceId", getDeviceId());
        }
        if (map != null) {
            for (Entry<String, Object> entry : map.entrySet()) {
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }
        params.setConnectTimeout(20 * 1000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException arg0) {
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {

                if (MyActivity_v4.this.mDialog != null) {
                    mDialog.hideDialog();
                }
                requestConnectionFail(postType);

                alertView(AlertViewType.NO_CONNECTION);
            }

            @Override
            public void onFinished() {
                requestFinished();
            }

            @Override
            public void onSuccess(String arg0) {

                if (MyActivity_v4.this.mDialog != null) {
                    mDialog.hideDialog();
                }

                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("result");
                    if (result.equals("1")) {
                        if (postType == PostType.LOAD) {
                            if (object.has("data")) {
                                if (mAlertView != null) {
                                    reloadAcitivtyView(mAlertView);
                                }
                                requestSuccess(object, postType);
                            } else {
                                // 无数据
                                alertView(AlertViewType.NO_DATA);
                                requestSuccess(object, postType);
                            }
                        } else {
                            requestSuccess(object, postType);
                        }
                    } else if (result.equals("-1")) {
                        requestError();

                    } else if (result.equals("-2")) {
                        // 维护中
                        Intent intent = new Intent(MyActivity_v4.this, Web.class);
                        intent.putExtra("title", "系统维护中");
                        intent.putExtra("url", Config.getHttpConfig() + "/more/weihu");
                        MyActivity_v4.this.startActivity(intent);
                        MyActivity_v4.this.finish();

                    } else if (result.equals("-4")) {
                        // 重新登录
                        Intent intent = new Intent(MyActivity_v4.this, Login.class);
                        MyActivity_v4.this.startActivity(intent);
//						MyActivity.this.finish();
                        new SharedPreferencesData(MyActivity_v4.this).removeAll();
                        requestFail(object.getString("msg"), postType);
                    } else {

                        requestFail(object.getString("msg"), postType);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 服务器连接失败
     */
    public void requestConnectionFail(PostType postType) {
        ToastMakeText.showToast(MyActivity_v4.this, "服务器连接失败", 2000);
    }

    /**
     * 无网络连接
     */
    public void requestNoConnection(PostType postType) {
    }

    /***
     * 请求出错
     */
    public void requestError() {

    }

    /**
     * 请求数据返回失败
     *
     * @param msg
     * @param postType
     */
    public void requestFail(String msg, PostType postType) {

    }

    public void requestFail(String msg, JSONObject obj, PostType postType) {

    }

    public void requestFinished() {
    }

    /**
     * 请求数据返回成功
     *
     * @param object
     * @param postType
     */
    public void requestSuccess(JSONObject object, PostType postType) {

    }

    private void alertView(AlertViewType alertViewType) {
        if (mAlertView != null) {

            View view = LayoutInflater.from(this).inflate(R.layout.load_data, null);
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.load_data_layout);
            layout.setOrientation(LinearLayout.VERTICAL);
            TextView text = (TextView) view.findViewById(R.id.text);
            ImageView image = (ImageView) view.findViewById(R.id.imageView1);
            mAlertView.removeAllViews();
            mAlertView.addView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            if (alertViewType == AlertViewType.NO_CONNECTION) {
                // 加载失败
                text.setText("加载失败，点击刷新");
                layout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reloadAcitivtyView(mAlertView);
                    }
                });
            } else if (alertViewType == AlertViewType.NO_DATA) {
                // 无数据提示
                image.setBackgroundResource(R.drawable.data_no);

            } else if (alertViewType == AlertViewType.LOADING) {
                text.setText("加载中...");
            }
        }
    }

    public void reloadAcitivtyView(RelativeLayout view) {
        view.removeAllViews();
    }

    /******************
     * 发送验证码
     **************************/

    private static Handler handler = new Handler();
    private int count = 90;

    private Runnable myRunnable = new Runnable() {
        public void run() {

            handler.postDelayed(this, 1000);
            count--;
            if (count == 0) {
                count = SMS_TIME;
                stopSendButton();
            }
            reflushSendButton(count);

        }
    };

    public void stopSendButton() {
        handler.removeCallbacks(myRunnable);
        count = SMS_TIME;
    }

    public void reflushSendButton(int count) {

    }

    @Override
    protected void onDestroy() {
        stopSendButton();
        super.onDestroy();
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    @SuppressLint("ClickableViewAccessibility")
    public void hideSoftInput(View view) {
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }

            });
        }

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {

                View innerView = viewGroup.getChildAt(i);

                hideSoftInput(innerView);
            }
        }
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    /**
     * 弹出软键盘
     */
    public void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 弹出软键盘
     */
    public void showSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, 0);
    }

    public void showLogs(String tag, String str) {
        Log.e(tag, str);
    }

    public void showLog(String str) {
        Log.e("WXCF", str);
    }
}

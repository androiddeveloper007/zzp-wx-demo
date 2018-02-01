package com.cstz.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

public class MyFragment extends Fragment {
    private RelativeLayout mAlertView = null;
    private MyDialog mDialog = null;
    public static boolean isfirstclicktab2 = true;
    public static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
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
            mDialog = new MyDialog(this.getActivity(), text);
            mDialog.setDuration(300);
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
    public void postData(Map<String, Object> map, String action, RelativeLayout view, final PostType postType) {
        if (Device.getConnectedType(this.getActivity()) == -1) {
            if (view != null) {
                mAlertView = view;
                alertView(AlertViewType.NO_CONNECTION);
            } else {
                ToastMakeText.showToast(MyFragment.this.getActivity(), "当前网络已断开", 1000);
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
        if (map != null) {
            for (Entry<String, Object> entry : map.entrySet()) {
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }
        params.setConnectTimeout(20 * 1000);
//        params.setCacheMaxAge(1000*60*24);
        x.http().post(params, new Callback.CommonCallback<String>() {// Callback.CacheCallback
            @Override
            public void onCancelled(CancelledException arg0) {

            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {

                if (MyFragment.this.mDialog != null) {
                    mDialog.hideDialog();
                }

                requestConnectionFail();

                alertView(AlertViewType.NO_CONNECTION);
            }

            @Override
            public void onFinished() {
                requestOnFinish();
            }

            @Override
            public void onSuccess(String arg0) {
                if (MyFragment.this.mDialog != null) {
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
                            }
                        } else {
                            requestSuccess(object, postType);
                        }
                    } else if (result.equals("-1")) {
                        requestError();

                    } else if (result.equals("-2")) {
                        // 维护中
                        Intent intent = new Intent(MyFragment.this.getActivity(), Web.class);
                        intent.putExtra("title", "系统维护中");
                        intent.putExtra("url", Config.getHttpConfig() + "/more/weihu");
                        MyFragment.this.getActivity().startActivity(intent);
                        MyFragment.this.getActivity().finish();

                    } else if (object.getString("result").equals("-4")) {
                        // 重新登录
                        requestFail(object.getString("msg"), postType);
                        new SharedPreferencesData(MyFragment.this.getActivity()).removeAll();
                        Intent intent = new Intent(MyFragment.this.getActivity(), Login.class);
                        MyFragment.this.getActivity().startActivity(intent);
//						MyFragment.this.getActivity().finish();
                    } else {
                        requestFail(object.getString("msg"), postType);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void requestConnectionFail() {
        ToastMakeText.showToast(getActivity(), "服务器连接失败", 2000);
    }

    public void requestOnFinish() {
    }

    /***
     * 请求出错
     */
    public void requestError() {

    }

    public void requestFail(String msg, PostType postType) {

    }

    public void requestSuccess(JSONObject object, PostType postType) {

    }

    public void alertView(AlertViewType alertViewType) {
        if (mAlertView != null) {
            View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.load_data, null);
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
    private int count = 91;

    private Runnable myRunnable = new Runnable() {
        public void run() {

            handler.postDelayed(this, 1000);
            count--;
            if (count == 0) {
                count = 91;
                stopSendButton();
            }
            reflushSendButton(count);

        }
    };

    public void stopSendButton() {
        handler.removeCallbacks(myRunnable);
        count = 91;
    }

    public void reflushSendButton(int count) {

    }

    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}

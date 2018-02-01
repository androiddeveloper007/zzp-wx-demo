package com.cstz.cunguan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.common.App.PostType;
import com.cstz.common.MyActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.dialog.ExitDialog;
import com.cstz.tools.ToastMakeText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cstz.cunguan.activity.MessageCenter.RESPONSE_CODE_1;

/**
 * 消息详情
 */
public class MessageDetail extends MyActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.textview_mailTitle)
    TextView textviewMailTitle;
    @BindView(R.id.textView_sendTime)
    TextView textViewSendTime;
    @BindView(R.id.tv_mailContent)
    TextView tv_mailContent;
//    @BindView(R.id.webView_mailContent)
//    WebView webViewMailContent;
    @BindView(R.id.iv_navigation_right)
    ImageView ivNavigationRight;
    private SharedPreferencesData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aa_activity_message_detail);
        setTransparentStatusBar(this,true);
        ButterKnife.bind(this);
        data = new SharedPreferencesData(MessageDetail.this);
        initView();
        initData();
    }

    private void initView() {
        titleBack.setVisibility(View.VISIBLE);
        titleTv.setText("消息详情");
        ivNavigationRight.setVisibility(View.VISIBLE);
        ivNavigationRight.setImageResource(R.mipmap.cg_delete);
    }

    public void initData() {
        Intent intent = getIntent();
        Map<String, Object> map = new HashMap<>();
        if (intent != null) {
            map.put("token", data.getValue("token"));
            map.put("id", intent.getStringExtra("mailId"));
            postData(map, "/user/mailDetail", null, PostType.LOAD);
        }
    }

    public void requestFail(String msg, PostType postType) {
        super.requestFail(msg, postType);
        ToastMakeText.showToast(this, msg, 1200);
    }

    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            try {
                if(postType.equals(PostType.SUBMIT)){
                    setResult(MessageCenter.RESPONSE_CODE);
                    finish();
                }else{
                    JSONObject data = object.getJSONObject("data");
                    if (data != null) {
                        textviewMailTitle.setText(data.getString("mailTitle"));
                        textViewSendTime.setText(data.getString("sendTime"));
//                        webViewMailContent.loadDataWithBaseURL(null, data.getString("mailContent"), "text/html", "utf-8", null);
                        tv_mailContent.setText(data.getString("mailContent"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick({R.id.title_back,R.id.iv_navigation_right})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                setResult(RESPONSE_CODE_1);
                finish();
                break;
            case R.id.iv_navigation_right:
                ExitDialog d = new ExitDialog(this,0.85);
                d.setFillViewVisibility(false);
                d.setText("确定要删除此条消息吗？");
                d.setOnRightClickListener(new ExitDialog.onClickListener() {
                    @Override
                    public void click() {
                        Map<String, Object> map = new HashMap<>();
                        map.put("token", data.getValue("token"));
                        map.put("mailIds", getIntent().getStringExtra("mailId"));
                        postData(map, "/user/deleteMailAsMore", PostType.SUBMIT, true);
                    }
                });
                d.show();
                break;
            default:break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESPONSE_CODE_1);
        super.onBackPressed();
    }
}
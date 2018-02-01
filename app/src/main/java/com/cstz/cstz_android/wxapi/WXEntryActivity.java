/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.cstz.cstz_android.wxapi;

import android.content.Intent;
import android.widget.Toast;

import cn.sharesdk.wechat.utils.WXAppExtendObject;
import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;

/**
 * 微信客户端回调activity示例
 */
public class WXEntryActivity extends WechatHandlerActivity {// implements IWXAPIEventHandler
//    @BindView(R.id.tv_wx_demo)
//    TextView tvWxDemo;//WechatHandler
//    private IWXAPI api;
//    private int mTargetScene = SendMessageToWX.Req.WXSceneSession;
    /**
     * 处理微信发出的向第三方应用请求app message
     * <p>
     * 在微信客户端中的聊天页面有“添加工具”，可以将本应用的图标添加到其中
     * 此后点击图标，下面的代码会被执行。Demo仅仅只是打开自己而已，但你可
     * 做点其他的事情，包括根本不打开任何页面
     */
	public void onGetMessageFromWXReq(WXMediaMessage msg) {
		if (msg != null) {
			Intent iLaunchMyself = getPackageManager().getLaunchIntentForPackage(getPackageName());
			startActivity(iLaunchMyself);
		}
	}

    /**
     * 处理微信向第三方应用发起的消息
     * <p>
     * 此处用来接收从微信发送过来的消息，比方说本demo在wechatpage里面分享
     * 应用时可以不分享应用文件，而分享一段应用的自定义信息。接受方的微信
     * 客户端会通过这个方法，将这个信息发送回接收方手机上的本demo中，当作
     * 回调。
     * <p>
     * 本Demo只是将信息展示出来，但你可做点其他的事情，而不仅仅只是Toast
     */
	public void onShowMessageFromWXReq(WXMediaMessage msg) {
		if (msg != null && msg.mediaObject != null
				&& (msg.mediaObject instanceof WXAppExtendObject)) {
			WXAppExtendObject obj = (WXAppExtendObject) msg.mediaObject;
			Toast.makeText(this, obj.extInfo, Toast.LENGTH_SHORT).show();
		}
	}
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.wx_demo);
//        ButterKnife.bind(this);
//        api = WXAPIFactory.createWXAPI(this, Config.APP_ID, false);
//        try {
//            api.handleIntent(getIntent(), this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        api.handleIntent(intent, this);
//        Toast.makeText(this, "onNewIntent（）被触发", Toast.LENGTH_LONG).show();
//    }

//    @Override
//    public void onReq(BaseReq req) {
//        /*Log.e("wxcf", "发送请求时的参数：" + req.toString());
//        Toast.makeText(this, "发送请求时的参数：" + req.toString(), Toast.LENGTH_LONG).show();
//        switch (req.getType()) {
//            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//                Toast.makeText(this, "留在微信", Toast.LENGTH_LONG).show();
//                break;
//            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//                Toast.makeText(this, "返回原应用", Toast.LENGTH_LONG).show();
//                break;
//            default:
//                break;
//        }*/
//    }

//    @Override
//    public void onResp(BaseResp baseResp) {
//        int result = 0;
//        Toast.makeText(this, "baseresp.getType = " + baseResp.getType(), Toast.LENGTH_LONG).show();
//        tvWxDemo.setText(baseResp.getType()+"");
//        switch (baseResp.errCode) {
//            case BaseResp.ErrCode.ERR_OK:
//                result = R.string.errcode_success;
//                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                result = R.string.errcode_cancel;
//                break;
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                result = R.string.errcode_deny;
//                break;
//            case BaseResp.ErrCode.ERR_UNSUPPORT:
//                result = R.string.errcode_unsupported;
//                break;
//            default:
//                result = R.string.errcode_unknown;
//                break;
//        }
//        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//    }

//    @OnClick(R.id.layout_button_wx)
//    public void onClick() {
//        String text = "这段文字发送自微信SDK示例程序";
//        WXTextObject textObj = new WXTextObject();
//        textObj.text = text;
//        WXMediaMessage msg = new WXMediaMessage();
//        msg.mediaObject = textObj;
//        msg.description = text;
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction("text");
//        req.message = msg;
//        req.scene = mTargetScene;
//        api.sendReq(req);
//    }

//    private String buildTransaction(final String type) {
//        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Intent i = getIntent();
//        ToastMakeText.showToast(this,i.getStringExtra("")+"",2000);
//    }
}

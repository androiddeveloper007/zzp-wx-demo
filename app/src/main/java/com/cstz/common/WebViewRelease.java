package com.cstz.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.cstz_android.R;
import com.cstz.cunguan.dialog.ExitDialog;
import com.cstz.cunguan.fragment.UserCenter;
import com.cstz.front.InviteReward;
import com.cstz.front.Login;
import com.cstz.front.Register;
import com.cstz.tab.MainActivity;
import com.cstz.tools.StatusBarUtil;
import com.cstz.tools.ToastMakeText;
import com.cstz.tools.WebViewEx;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;


/**
 * 公用打开Web地址
 *
 * @author zhuzhipeng
 */
public class WebViewRelease extends Activity implements OnClickListener {//, IWXAPIEventHandler
    private TextView title_tv;
    private ImageView title_back;
    private WebViewEx mWebView;
    private String url = "";
    String mUrl2 = "http://192.168.1.96:8083/view/usercenter/index.html";
    private Context context;
    private SharedPreferencesData sp;
    private String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.commom_web_release);
        setTransparentStatusBar(this, false);
        context = this;
        initView();
    }
    /*设置6.0以上系统，沉浸式状态栏*/
    public void setTransparentStatusBar(Activity activity,boolean cg){
        if(Build.VERSION.SDK_INT > 22){
            RelativeLayout toolbar = (RelativeLayout) activity.findViewById(R.id.layout_title);
            if(toolbar!=null){
                ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
                lp.height = getResources().getDimensionPixelOffset(R.dimen.dimen_42);
                toolbar.setLayoutParams(lp);
            }
            StatusBarUtil.setColor(this, getResources().getColor(!cg?R.color.window_background:R.color.navigation_cg), 0);
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    public void initView() {
        title_tv = (TextView) findViewById(R.id.title_tv);
        title_back = (ImageView) findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        mWebView = (WebViewEx) findViewById(R.id.webview);

        Intent intent = getIntent();
        if (intent != null) {
            title_tv.setText(intent.getStringExtra("title"));
            url = intent.getStringExtra("url");
            html = intent.getStringExtra("html");
        }

        mWebView.getSettings().setJavaScriptEnabled(true);

//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 开启 DOM storage API 功能
//        mWebView.getSettings().setDomStorageEnabled(false);
        //开启 database storage API 功能
//        mWebView.getSettings().setDatabaseEnabled(false);

        // 设置可以支持缩放
        mWebView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        mWebView.getSettings().setBuiltInZoomControls(true);
        //设置可在大视野范围内上下左右拖动，并且可以任意比例缩放
        mWebView.getSettings().setUseWideViewPort(true);
        //设置默认加载的可视范围是大视野范围
        mWebView.getSettings().setLoadWithOverviewMode(true);
        //自适应屏幕(导致活动页面显示出错了)
//        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //JS映射
//        mWebView.addJavascriptInterface(this, "android");

        mWebView.addJavascriptInterface(new JSInterface(), "jsInterface");

        //判断是否登录，已登录拼接token
        sp = new SharedPreferencesData(this);
        if (sp.getBoolean("hasLogin") && !TextUtils.isEmpty(sp.getValue("token")) && !TextUtils.isEmpty(url)) {
            if (!url.contains("?")) {
                url += "?token=" + sp.getValue("token");
//                mUrl2 += "?token=" + sp.getValue("token");
            } else {
                url += "&token=" + sp.getValue("token");
//                mUrl2 += "&token=" + sp.getValue("token");
            }
        }

        if (TextUtils.isEmpty(html))
            mWebView.loadUrl(url);//mUrl2
        else
            mWebView.loadData(html, "text/html; charset=UTF-8", null);// "text/html","GBK"

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url0) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//	         view.loadUrl(url);
                url = url0;
//                Log.e("ZP","webview跳转的url:"+url);
                return false;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                Log.e("wxcf",""+error.getPrimaryError());
                if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
                        || error.getPrimaryError() == SslError.SSL_EXPIRED
                        || error.getPrimaryError() == SslError.SSL_INVALID
                        || error.getPrimaryError() == SslError.SSL_UNTRUSTED
                        || error.getPrimaryError() == SslError.SSL_IDMISMATCH) {
                    handler.proceed();//接受证书
                } else {
                    handler.cancel();//默认的处理方式，WebView变成空白页
                }
            }
        });

        listener();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && !mWebView.canGoBack()) {
            setResult(RESULT_OK);
            if (getIntent() != null && getIntent().hasExtra("html"))//开通存管时，无论成功与否，回退时刷新个人中心数据
                setResult(UserCenter.RESULT_ADD_CG);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void listener() {
        title_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                setResult(RESULT_OK);
                if (getIntent() != null && getIntent().hasExtra("html"))
                    setResult(UserCenter.RESULT_ADD_CG);
                finish();
                break;
            default:
                break;
        }
    }

//    @Override
//    public void onReq(BaseReq req) {
//        Log.e("wxcf","发送请求时的参数："+req.openId);
//        Toast.makeText(this, "发送请求时的参数："+req.openId, Toast.LENGTH_SHORT).show();
//        switch (req.getType()) {
//            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//                break;
//            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//                break;
//            default:
//                break;
//        }
//    }

//    @Override
//    public void onResp(BaseResp baseResp) {
//        int result = 0;
//        Toast.makeText(this, "baseresp.getType = " + baseResp.getType(), Toast.LENGTH_SHORT).show();
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

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        api.handleIntent(intent, this);
//    }

    class JSInterface {
        @JavascriptInterface
        public void login() {
            Intent intent = new Intent(WebViewRelease.this, Login.class);
            startActivity(intent);
            finish();
        }

        @JavascriptInterface
        public void register() {
            Intent intent = new Intent(WebViewRelease.this, Register.class);
            WebViewRelease.this.startActivity(intent);
            WebViewRelease.this.finish();
        }

        @JavascriptInterface
        public void finishWebViewActivity() {
            WebViewRelease.this.finish();
        }

        @JavascriptInterface
        public void gotoProduct() {
            Intent intent = new Intent(WebViewRelease.this, MainActivity.class);
            intent.putExtra("product", "");
            WebViewRelease.this.startActivity(intent);
            WebViewRelease.this.finish();
        }

        @JavascriptInterface
        public void showShare(String url, String txt, final String key) {
            OnekeyShare oks = new OnekeyShare();
            oks.setCallback(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    //成功后，抽奖次数加一
                    requestShareSuccess(key);
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    ToastMakeText.showToast((Activity) context, "分享出错了" + throwable, 2000);
                }

                @Override
                public void onCancel(Platform platform, int i) {
                    ToastMakeText.showToast((Activity) context, "微信分享已取消", 2000);
                }
            });
            //关闭sso授权
            oks.disableSSOWhenAuthorize();
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
            oks.setTitle(getString(R.string.share_title));
            // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
            oks.setTitleUrl(url);
            // text是分享文本，所有平台都需要这个字段
            oks.setText(txt);
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            String imgUri = new SharedPreferencesData(WebViewRelease.this).getValue("ShareImgUri");
            if (TextUtils.isEmpty(imgUri)) {
                oks.setImageUrl("https://m.wuxingjinrong.com/view/eggswaptwo/images/fenxiangicon.jpg");
            } else {
                oks.setImagePath(imgUri);
            }
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(url);
            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
            oks.setComment(getResources().getString(R.string.share_title));
            // site是分享此内容的网站名称，仅在QQ空间使用
            oks.setSite(getString(R.string.share_title));
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            oks.setSiteUrl(url);
            //控制只分享到朋友圈
            oks.addHiddenPlatform("Wechat");
            oks.show(WebViewRelease.this);

            /*String text = "这段文字发送自微信SDK示例程序";
            WXTextObject textObj = new WXTextObject();
            textObj.text = text;
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = textObj;
            msg.description = text;
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("text");
            req.message = msg;
            req.scene = mTargetScene;
            api.sendReq(req);*/
        }

        @JavascriptInterface
        public void showShare(String url, String txt, final String key, String title, String imgUrl, final String isInviteToRegister) {
//            Log.e("ZP","url:"+url+"txt:"+txt+"title:"+title+"imgUrl:"+imgUrl);//+"key:"+key+"isInviteToRegister:"+isInviteToRegister
            OnekeyShare oks = new OnekeyShare();
            oks.setCallback(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    //成功后，抽奖次数加一
                    if (TextUtils.equals("1", isInviteToRegister)) {
                        ToastMakeText.showToast((Activity) context, "分享朋友圈成功！", 2000);
                        requestShareSuccess(key);
                    } else {
                        ToastMakeText.showToast((Activity) context, "邀请好友的分享成功！", 2000);
                    }
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    ToastMakeText.showToast((Activity) context, "分享出错了" + throwable, 2000);
                }

                @Override
                public void onCancel(Platform platform, int i) {
                    ToastMakeText.showToast((Activity) context, "微信分享已取消", 2000);
                }
            });
            //关闭sso授权
            oks.disableSSOWhenAuthorize();
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
            oks.setTitle(title);
            // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
            oks.setTitleUrl(url);
            // text是分享文本，所有平台都需要这个字段
            oks.setText(txt);
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            oks.setImageUrl(imgUrl);
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(url);
            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
            oks.setComment(title);
            // site是分享此内容的网站名称，仅在QQ空间使用
            oks.setSite(title);
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            oks.setSiteUrl(url);
            //控制只分享到朋友圈
            if (TextUtils.equals("1", isInviteToRegister))
                oks.addHiddenPlatform("Wechat");
            oks.show(WebViewRelease.this);
        }

        @JavascriptInterface
        public void goMyShare() {
            startActivity(new Intent(context, InviteReward.class));
        }

        @JavascriptInterface
        public void gotoUserCenter() {
            Intent intent = new Intent(WebViewRelease.this, MainActivity.class);
            intent.putExtra("home", "");
            startActivity(intent);
            finish();
        }

        /*开通存管的操作，点击H5上的按钮进入到个人中心*/
        @JavascriptInterface
        public void gotoUserCenter(boolean createCgAccount) {
            App.depositCheck="1";
            Intent intent = new Intent(WebViewRelease.this, MainActivity.class);
            intent.putExtra("home", "");
            if (createCgAccount)
                intent.putExtra("createCgAccount", true);
            startActivity(intent);
            finish();
        }

        /*开通存管时，往sp中写缓存，代表开通存管第一次打开（返回）个人中心*/
        @JavascriptInterface
        public void OpenCgCacheData(boolean b) {
            if (b) {
                sp.setBoolean("OpenCgCacheData0", true);
                sp.setBoolean("OpenCgCacheData1", true);
            }
        }

        /*充值提现等失败时，弹出拨打电话的对话框*/
        @JavascriptInterface
        public void callService(final String tel) {
            final ExitDialog d = new ExitDialog(context, 0.85);
            d.setFillViewVisibility(false);
            d.setText("呼叫客服： "+tel+" ？");
            d.setTextSize(R.dimen.dimen_18);
            d.setLeftBtnText("取消");
            d.setRightBtnText("拨打");
            d.setOnRightClickListener(new ExitDialog.onClickListener() {
                @Override
                public void click() {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
                    context.startActivity(intent);
                    d.dismiss();
                }
            });
            d.show();
        }

        /*跳转到注册页*/
        @JavascriptInterface
        public void gotoRegister() {
            Intent intent = new Intent(WebViewRelease.this, Register.class);
            startActivity(intent);
            finish();
        }
    }

//    private String buildTransaction(final String type) {
//        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
//    }

    private void requestShareSuccess(String key) {
        String path = Config.getHttpConfig() + "/activity/addWeixinShare";
        RequestParams params = new RequestParams(path);
        params.addParameter("userId", new SharedPreferencesData(context).getValue("token"));
        params.addParameter("activityKey", key);
        params.addParameter("channel", "app");
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.addParameter("token", sp.getValue("token"));
        params.setConnectTimeout(20 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("result");
                    switch (result) {
                        case "-1":
                            ToastMakeText.showToast((Activity) context, "分享失败", 2000);
                            break;
                        case "0":
                            ToastMakeText.showToast((Activity) context, "参数不完整", 2000);
                            break;
                        case "1":
                            ToastMakeText.showToast((Activity) context, "分享成功", 2000);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mWebView.loadUrl(url);//重新加载页面
                                }
                            }, 1000);
                            break;
                        case "-4":
                            ToastMakeText.showToast((Activity) context, "会话过期，请重新登录", 3000);
                            sp.removeAll();
                            Intent intent = new Intent(context, Login.class);
                            startActivity(intent);
                            finish();
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastMakeText.showToast((Activity) context, "服务器连接失败", 1000);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }
}
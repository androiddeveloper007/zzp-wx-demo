package com.cstz.tab;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.common.Config;
import com.cstz.common.MyFragment;
import com.cstz.cstz_android.R;
import com.cstz.front.Register;
import com.cstz.tools.ProgressWebView;
import com.cstz.tools.StatusBarUtil;

/**
 * 
 * 更多Fragment
 */
public class More extends MyFragment{
	private static ProgressWebView _webview;
	private TextView title_tv;
	private RelativeLayout titleLayout;
	private ImageView title_back;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View shezhiLayout = inflater.inflate(R.layout.more_web, container, false);
		if(Build.VERSION.SDK_INT > 22) {
			StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 0, null);
			//设置状态栏文字白色
			getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		initview(shezhiLayout);
		initData();
		return shezhiLayout;
	}

	/**
	 * 控件的初始化
	 * */
	public void initview(View view) {
		title_tv = (TextView) view.findViewById(R.id.title_tv);
//		title_tv.setText("更多");
		titleLayout = (RelativeLayout) view.findViewById(R.id.layout_title);
		titleLayout.setVisibility(View.GONE);
		title_back = (ImageView) view.findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
	   	_webview = (ProgressWebView) view.findViewById(R.id.webview);
		title_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(_webview.canGoBack())
					_webview.goBack();
			}
		});
	}

    @SuppressLint("SetJavaScriptEnabled")
	public void initData(){
    	_webview.getSettings().setJavaScriptEnabled(true);  
         //JS映射
		_webview.addJavascriptInterface(new More.JSInterface(), "jsInterface");
           
    	_webview.setWebViewClient(new WebViewClient(){   
              public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                  view.loadUrl(url);
                  return false;//true
              }

			@Override
			public void onPageFinished(WebView view, String url) {
//				super.onPageFinished(view, url);
				if(url.contains(Config.getHttpConfig()+"/more/index")){//TextUtils.equals(url,Config.getHttpConfig()+"/more/index?isBottom=1")
					titleLayout.setVisibility(View.GONE);
				} else{
					String title = view.getTitle();
					title_tv.setText(title);
					titleLayout.setVisibility(View.VISIBLE);
				}
			}
		});
    	_webview.loadUrl(Config.getHttpConfig()+"/more/index?isBottom=1&isAndroid=1");//
    }

	class JSInterface {
		/*跳转到注册页*/
		@JavascriptInterface
		public void gotoRegister() {
			Intent intent = new Intent(getActivity(), Register.class);
			startActivity(intent);
		}
	}

    public static boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK  && _webview.canGoBack()) {
        		_webview.goBack();
        		return true;
        }
        
        return true;
    }
    
    public static boolean home(){
    	if(_webview.canGoBack())
    		return false;
    	else
    		return true;
    }
}

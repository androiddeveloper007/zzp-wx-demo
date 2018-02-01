package com.cstz.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.JavascriptInterface;

import com.cstz.tab.MainActivity;
public class WebHost extends Activity{  
    public Context context;  
  
    public WebHost(Context context){  
        this.context = context;  
    }  
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
    @JavascriptInterface
    public void callRechargeOK(){  
    	Intent intent = new Intent();
    	intent.putExtra("home", "");
		intent.setClass(context, MainActivity.class);
		context.startActivity(intent);
    }
} 
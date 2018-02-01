package com.cstz.common;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
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

public class MyFragmentActivity extends FragmentActivity{

	private RelativeLayout mAlertView = null;
	private MyDialog mDialog = null;
	/**
	 * 提交/获取网络数据
	 * @param map
	 * @param action
	 * @param postType
	 */
	public void postData(final Map<String,Object> map,final String action,final PostType postType,boolean isDialog)
	{
		if(isDialog)
		{
			String text = (postType == PostType.LOAD?"加载中...":"请稍等...");
			mDialog = new MyDialog(this,text);
			mDialog.setDuration(300);
			mDialog.setMyCallback(new MyCallback(){
				@Override
				public void callback() {
				}

				@Override
				public void doing() {
					postData(map,action,null,postType);
				}
			});
			mDialog.showDialog();
		}else
		{
			postData(map,action,null,postType);
		}
	}
	
	/**
	 * 提交/获取网络数据
	 * @param
	 * @param
	 */
	public void postData(Map<String,Object> map,String action,RelativeLayout view,final PostType postType)
	{
		if(Device.getConnectedType(this)==-1)
		{
			if(view!=null)
			{
				mAlertView = view;
				alertView(AlertViewType.NO_CONNECTION);
			}else
			{
				ToastMakeText.showToast(this, "当前网络已断开", 1000);
			}
			return ;
		}
		if(postType == PostType.MESSAGE)
		{
			handler.post(myRunnable); //发送倒计时
			
		}
		else if(postType == PostType.SUBMIT)
		{
			
			
		}else if(postType == PostType.LOAD)
		{
			if(view!=null)
			{
				mAlertView = view;				
				alertView(AlertViewType.LOADING);
			}
			
		}
		
		String path = Config.getHttpConfig()+action;
		RequestParams params = new RequestParams(path);
		params.addParameter("rt", "app");
//		params.addParameter("sys", "android");
		if(map!=null)
		{
			for(Map.Entry<String, Object> entry:map.entrySet()){
				params.addParameter(entry.getKey(), entry.getValue());
			}
		}
		params.setConnectTimeout(20*1000);
	    x.http().post(params, new Callback.CommonCallback<String>() {
	        @Override
	        public void onCancelled(Callback.CancelledException arg0) {

	        }

	        @Override
	        public void onError(Throwable arg0, boolean arg1) {

	        	if(MyFragmentActivity.this.mDialog!=null)
	        	{
	        		mDialog.hideDialog();
	        	}

				if(!MyFragmentActivity.this.isFinishing())//add by zzp 2017/4/20解决activity被finish后有dialog弹出的异常
					requestConnectionFail();

	        	alertView(AlertViewType.NO_CONNECTION);
	        }

	        @Override
	        public void onFinished() {

	        }

	        @Override
	        public void onSuccess(String arg0) {
	        	if(MyFragmentActivity.this.mDialog!=null)
	        	{
	        		mDialog.hideDialog();
	        	}
	        	try {
					JSONObject object = new JSONObject(arg0);
					String result=object.getString("result");
					if (result.equals("1"))
					{
						if(postType == PostType.LOAD)
						{
							if(object.has("data"))
							{
								if(mAlertView!=null)
								{
									reloadAcitivtyView(mAlertView);
								}
								requestSuccess(object,postType);
							}else
							{
								alertView(AlertViewType.NO_DATA);
							}
						}else
						{
							requestSuccess(object,postType);
						}
					}else if(result.equals("-1"))
					{
						requestError();

					}
					else if(result.equals("-2"))
					{
						//维护中
						Intent intent = new Intent(MyFragmentActivity.this, Web.class);
						intent.putExtra("title", "系统维护中");
						intent.putExtra("url",Config.getHttpConfig()+"/more/weihu");
						MyFragmentActivity.this.startActivity(intent);
						MyFragmentActivity.this.finish();

					}else if(object.getString("result").equals("-4")){
						//重新登录
						new SharedPreferencesData(MyFragmentActivity.this).removeAll();
						requestFail(object.getString("msg"),postType);
						Intent intent = new Intent(MyFragmentActivity.this, Login.class);
						MyFragmentActivity.this.startActivity(intent);
//						MyFragmentActivity.this.finish();
					}else
					{
						requestFail(object.getString("msg"),postType);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        }
	    });
	}

	public void requestConnectionFail()
	{
			ToastMakeText.showToast(this, "服务器连接失败", 2000);
	}
	/***
	 * 请求出错
	 */
	public void requestError()
	{
	
	}
	public void requestFail(String msg,PostType postType){
		
	}
	public void requestSuccess(JSONObject object,PostType postType){
		
	}
    
	private void alertView(AlertViewType alertViewType)
	{
		if(mAlertView!=null)
		{
			View view = LayoutInflater.from(this).inflate(R.layout.load_data, null);
			LinearLayout layout = (LinearLayout) view.findViewById(R.id.load_data_layout);
			layout.setOrientation(LinearLayout.VERTICAL);
			TextView text = (TextView)view.findViewById(R.id.text);
			ImageView image = (ImageView)view.findViewById(R.id.imageView1);
			mAlertView.removeAllViews();
			mAlertView.addView(layout,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));			
			if(alertViewType == AlertViewType.NO_CONNECTION)
			{
				//加载失败
				text.setText("加载失败，点击刷新");
				layout.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						reloadAcitivtyView(mAlertView);
					}
				});
			}else if(alertViewType == AlertViewType.NO_DATA)
			{
				//无数据提示
				image.setBackgroundResource(R.drawable.data_no);
				
			}else if(alertViewType == AlertViewType.LOADING)
			{
				text.setText("加载中...");
			}
		}
	}
	
	
	public void reloadAcitivtyView(RelativeLayout view)
	{
		view.removeAllViews();
	}
	
	
	
	/******************发送验证码**************************/
	
	private static Handler handler = new Handler();
	private int count = 91;
	  
    private Runnable myRunnable= new Runnable() {
        public void run() {  
            
        	handler.postDelayed(this, 1000);
        	count--;
        	if(count == 0)
        	{
        		count = 91;
        		stopSendButton();
        	}
        	reflushSendButton(count);
        	
        }  
    }; 
    
    public void stopSendButton()
    {
    	handler.removeCallbacks(myRunnable);
    	count = 91;
    }
    public void reflushSendButton(int count)
    {
    	
    }


}

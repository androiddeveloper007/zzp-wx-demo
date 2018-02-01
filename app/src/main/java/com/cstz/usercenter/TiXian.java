package com.cstz.usercenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cstz.common.App.PostType;
import com.cstz.common.Config;
import com.cstz.common.MyActivity;
import com.cstz.common.MyCallback;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.Validate;
import com.cstz.common.view.MyDialog;
import com.cstz.common.view.MyDialog.Result;
import com.cstz.cstz_android.R;
import com.cstz.common.Web;
import com.cstz.tools.Convert;
import com.cstz.tools.ToastMakeText;
import com.ziyeyouhu.library.KeyboardTouchListener;
import com.ziyeyouhu.library.KeyboardUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 提现
 * @author rxh
 *
 */
public class TiXian extends MyActivity implements OnClickListener {
	private TextView title_tv;
	private ImageView title_back;
	private TextView _mRightNavigation;
	private LinearLayout mButton;     
	private EditText _money;            //提现金额
	private EditText _dealpwd;          //交易密码
	private TextView _textview_help_tx; //提现帮助
	private TextView mUsableSumTextView; //可提现金额
	private ImageView bankcard_img;
	private TextView bankcard_name;
	private TextView bankcard_cardid;
	
	private String cardIdNo = "";
	private String bankName = "";
	private String usableSum = "";
	private KeyboardUtil keyboardUtil;
	private LinearLayout root_view;
	private ScrollView scrollView1;
	private static Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home_tixian);
		initview();
		initData();
	}

	/**
	 * 控件的初始化
	 * 
	 * */
	public void initview() {
		title_tv = (TextView) findViewById(R.id.title_tv);
		title_tv.setText("提现");
		title_back = (ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		_mRightNavigation = (TextView)findViewById(R.id.navigation_right);
		_mRightNavigation.setText("提现记录");
//		_mRightNavigation.setTextColor(getResources().getColor(R.color.text_color_right));
		mButton = (LinearLayout) findViewById(R.id.home_tixian_button);
		bankcard_img = (ImageView) findViewById(R.id.bankcard_img);
		bankcard_name = (TextView) findViewById(R.id.bankcard_name);
		bankcard_cardid = (TextView) findViewById(R.id.bankcard_cardid);

		_money = (EditText)findViewById(R.id.home_tixian_money);
		mUsableSumTextView = (TextView)findViewById(R.id.home_tixian_usableSum);
		_dealpwd = (EditText)findViewById(R.id.home_tixian_dealpwd);
		_textview_help_tx = (TextView)findViewById(R.id.textview_help_tx);
    	root_view = (LinearLayout) findViewById(R.id.root_view);
    	scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
		listener();

    	initMoveKeyBoard();
		_money.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT){
					_dealpwd.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
							SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, _dealpwd.getLeft()+5, _dealpwd.getTop()+5, 0));
					_dealpwd.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
							SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, _dealpwd.getLeft()+5, _dealpwd.getTop()+5, 0));
					return true;
				}
				return false;
			}
		});
	}
	
    private void initMoveKeyBoard() {
        keyboardUtil = new KeyboardUtil(this, root_view, scrollView1);
        keyboardUtil.setOtherEdittext(_money);
        keyboardUtil.setKeyBoardStateChangeListener(new KeyBoardStateListener());
        _dealpwd.setOnTouchListener(new KeyboardTouchListener(keyboardUtil, KeyboardUtil.INPUTTYPE_ABC, -1));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            if(keyboardUtil.isShow){
                keyboardUtil.hideSystemKeyBoard();
                keyboardUtil.hideAllKeyBoard();
                keyboardUtil.hideKeyboardLayout();
            }else {
                return super.onKeyDown(keyCode, event);
            }

            return false;
        } else
            return super.onKeyDown(keyCode, event);
    }
    
	class KeyBoardStateListener implements KeyboardUtil.KeyBoardStateChangeListener {

        @Override
        public void KeyBoardStateChange(int state, EditText editText) {
//        	if(state == 1) {
//        		handler.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						scrollView1.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
//						_dealpwd.requestFocus();
//					}
//				}, 50);
//        	} else if(state ==2) {
//
//        	}
        }
    }

	/**
	 * 控件的点击事件监听
	 * */
	public void listener() {
		title_back.setOnClickListener(this);
		mButton.setOnClickListener(this);
		_mRightNavigation.setOnClickListener(this);
		_textview_help_tx.setOnClickListener(this);
	}
	
	private void initData()
	{
		Intent intent = getIntent();
		if(intent != null ){
			cardIdNo = Convert.strToStr(intent.getStringExtra("cardNo").toString(), "");	
			usableSum = Convert.strToStr(intent.getStringExtra("usableSum").toString(), "");
			bankName = Convert.strToStr(intent.getStringExtra("bankName").toString(), "");
			mUsableSumTextView.setText(usableSum+" 元");
		}
		bankcard_img.setImageResource(Config.getBankNameLogo(bankName));
		bankcard_name.setText(bankName);
		bankcard_cardid.setText(cardIdNo);
	}
	
	
	

	/**
	 * 字体加粗
	 * */
	public void viewPaint(TextView view){
		TextPaint tp = view.getPaint(); 
		tp.setFakeBoldText(true);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() ==R.id.title_back ) {
			hideSoftKeyboard();
			this.finish();
		}else if(v.getId() == R.id.navigation_right)
		{
			Intent intent = new Intent(TiXian.this, TiXianRecord.class);
			startActivity(intent);
		}
		else if(v.getId() == R.id.home_tixian_button)
		{
			
			String money = Convert.trim(_money.getText().toString());
			String dealpwd = Convert.trim(_dealpwd.getText().toString());
			if (ToastMakeText.isFastClick()) {
		        return ;
		    }
			if(TextUtils.isEmpty(money))
			{
				ToastMakeText.showToast(this, "提现金额不能为空", 1200);
				return;
			}
			if(!Validate.validateMoney(money))
			{
				ToastMakeText.showToast(this, "输入的提现金额错误", 1200);
				return;
			}
			if(TextUtils.isEmpty(dealpwd))
			{
				ToastMakeText.showToast(this, "支付密码不能为空", 1200);
				return;
			}
			
			setButtonStyle(false);
			
			Map<String,Object> map = new HashMap<String,Object>();			
			map.put("token", new SharedPreferencesData(TiXian.this).getValue("token"));
			map.put("money",money);
			map.put("dealpwd", dealpwd);
			postData(map,"/user/addWithdraw",PostType.SUBMIT,true);
			
						
		}else if(v.getId() == R.id.textview_help_tx){
			Intent intent = new Intent();
			intent.setClass(TiXian.this, Web.class);
			intent.putExtra("title", "如何提现");
			if(!Config.ISDEMO)
			{
				intent.putExtra("url", Config.getHttpConfig()+"/more/detail?type=9&id=30&pagetype=1&isTop=0");
			}else{
				intent.putExtra("url", Config.getHttpConfig()+"/more/detail?type=9&pagetype=1&isTop=0");
			}
			
			startActivity(intent);
		}
	}
	
	public void requestConnectionFail(PostType postType)
	{
		super.requestConnectionFail(postType);
		
		setButtonStyle(true);
	}
	public void requestFail(String msg,PostType postType)
	{
		super.requestFail(msg, postType);
		if(postType == PostType.SUBMIT)
		{
			setButtonStyle(true);
			ToastMakeText.showToast(this, msg, 1200);
		}				
	}
	
	public void requestSuccess(JSONObject object,PostType postType)
	{
		if(object!=null)
		{
			if(postType == PostType.SUBMIT)
			{
				try {
					MyDialog dialog = new MyDialog(TiXian.this, object.getString("msg"), Result.SUCCESS);
					dialog.setMyCallback(new MyCallback() {
						
						@Override
						public void callback() {
							setButtonStyle(true);
							
							Intent intent = new Intent(TiXian.this,TiXianRecord.class);
							TiXian.this.startActivity(intent);
//							TiXian.this.finish();
						}

						@Override
						public void doing() {
							
						}
					});
					dialog.showDialog();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	private void setButtonStyle(boolean enable)
	{
		if(enable)
		{
			mButton.setBackgroundResource(R.drawable.button_normal);
			mButton.setClickable(true);
			
		}else
		{
			mButton.setBackgroundResource(R.drawable.button_disable);
			mButton.setClickable(false);
		}
	}
	
}

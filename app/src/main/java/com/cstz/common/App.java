package com.cstz.common;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import com.cstz.cstz_android.R;

import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;

import control.pattern.UnlockGesturePasswordActivity;
import control.pattern.tools.SettingUtils;

public class App extends Application {

	public static App app;
	public static boolean FIRSTCHECKREQUEST=true;//app首页Index第一次获取版本号的标记，之后不会再请求
//	public static boolean FIRSTCHECKRISK=true;//app进入第一次查询问卷状态，之后不会再请求
	public static boolean IGNORE_UPDATE;//是否忽略版本更新
	public static boolean HAS_UPDATE;//是否有更新未下载
	private static SharedPreferencesData sp;
	private long time = -2;
	private int count = 0;
	private static final String FILE_NAME = "/share.png";
	public static String userType="0";//用户类型，-1代表借款人
	public static String regAsDeposit="0";//是否新注册 1
	public static String depositCheck="0";//是否已开通存管 1
	public static String oldAccountCloseDay="";//-1代表老用户已经关闭
	public static String auto_repayment="0";//自动还款：0,代表未开启 1，代表已开启
	public static String borrowerType="0";//借款人类型 1:对私，2：对公
	public static String borrowerAccountType="0";//借款人账号类型  1:非渤海，2：渤海, 其他：不用理会

	public static App getInstance() {
		return app;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		app = this;

		x.Ext.init(app);
		x.Ext.setDebug(false);// 设置是否输出debug
//		CrashLogHandlerUtils.getInstance(this).setSavePath(Environment.getExternalStorageDirectory()).start();

		//判断图片是否存在，否则在sd卡中新建
		initShareImagePath();

		FIRSTCHECKREQUEST=true;
		sp = new SharedPreferencesData(getApplicationContext());
		IGNORE_UPDATE= sp.getOtherBoolean("IGNORE_UPDATE");
		HAS_UPDATE= sp.getOtherBoolean("HAS_UPDATE");

		//判断app在后台驻留时间超过1min时进入手势密码验证页
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			}
			@Override
			public void onActivityStarted(Activity activity) {
				if (count == 0) {
					// 首先计算时间 网络请求判断是否进入手势密码验证界面
					if (-2 == time) {
						//判断进入解锁手势密码
					} else {
						Date date2 = new Date();
						long returnTime = date2.getTime();
						double sub = new BigDecimal(returnTime).subtract(new BigDecimal(time)).doubleValue();
						if (sub >= 60000d) {
							//此处是判断应用到后台多久时间以后需要开启手势密码
							time = -1;
							ifGotoPatternUnLock();
						}
					}
				} else {
					if (-2 == time) {
					}
					time = -1;
				}
				count++;
			}
			@Override
			public void onActivityResumed(Activity activity) {
			}
			@Override
			public void onActivityPaused(Activity activity) {
			}
			@Override
			public void onActivityStopped(Activity activity) {
				count--;
				if (count == 0) {
					Date date = new Date();
					time = date.getTime();
				} else {
					time = -1;
				}
			}
			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
			}
			@Override
			public void onActivityDestroyed(Activity activity) {
			}
		});
	}

	private void ifGotoPatternUnLock(){
		String phone;
		phone = new SharedPreferencesData(this).getOtherValue("user0");
		if (SettingUtils.getInstance(App.getInstance()).isPatternPwdOn(phone) && new SharedPreferencesData(this).getBoolean("hasLogin")) {

			Intent intent = new Intent(this, UnlockGesturePasswordActivity.class);
			intent.putExtra(UnlockGesturePasswordActivity.Param.REQ_TYPE, UnlockGesturePasswordActivity.Param.ReqType.UNLOCK_FOR_NORMAL);
			String username = "";
			if (!TextUtils.isEmpty(new SharedPreferencesData(this).getValue("nickName"))) {
				username = new SharedPreferencesData(this).getValue("nickName");
			} else if (!TextUtils.isEmpty(new SharedPreferencesData(this).getValue("userName"))) {
				username = new SharedPreferencesData(this).getValue("userName");
			} else {
				username = new SharedPreferencesData(this).getValue("phone");
			}
			intent.putExtra(UnlockGesturePasswordActivity.Param.USERNAME, username);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			new SharedPreferencesData(this).setBoolean("hasLogin", true);
		}
	}

	public enum PostType
	{
		MESSAGE(0),LOAD(1),SUBMIT(2);

		private int value;

		PostType(int value)
		{
			this.value = value;
		}
	}
	
	public enum AlertViewType
	{
		NO_CONNECTION(1),NO_DATA(2),LOADING(3);
		private int value;
		AlertViewType(int value)
		{
			this.value = value;
		}
	}

	public static void setHasUpdate(boolean b){
		if(b){
			HAS_UPDATE = true;
			sp.setOtherBoolean("HAS_UPDATE",true);
		} else {
			HAS_UPDATE = false;
			sp.setOtherBoolean("HAS_UPDATE",false);
		}
	}

	public static void setIgnoreUpdate(boolean b){
		if(b){
			HAS_UPDATE = true;
			sp.setOtherBoolean("IGNORE_UPDATE",true);
		} else {
			HAS_UPDATE = false;
			sp.setOtherBoolean("IGNORE_UPDATE",false);
		}
	}

	public void initShareImagePath() {
		try {
			File file;
			if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
				file = getExternalCacheDir();
			} else {
				String path = getFilesDir().getPath();
				file = new File(path);
				if (!file.exists()) {
					file.mkdirs();
				}
			}
			File imgFile = new File(file,FILE_NAME);
			if (!imgFile.exists()) {
				imgFile.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(getResources(), R.mipmap.share);
				FileOutputStream fos = new FileOutputStream(imgFile);
				pic.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
				new SharedPreferencesData(this).setValue("ShareImgUri",imgFile.toString());
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
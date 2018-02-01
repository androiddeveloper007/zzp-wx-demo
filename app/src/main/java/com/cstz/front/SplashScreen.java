package com.cstz.front;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.cstz.common.App;
import com.cstz.common.Config;
import com.cstz.common.MyActivity_v4;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.view.PermissionWarnDialog;
import com.cstz.common.view.PermissionWarnDialog.OnClickNext;
import com.cstz.cstz_android.R;
import com.cstz.tab.MainActivity;
import com.cstz.tools.ToastMakeText;

import java.util.HashMap;
import java.util.Map;

import control.pattern.UnlockGesturePasswordActivity;
import control.pattern.tools.SettingUtils;

/**
 * 启动页
 */
public class SplashScreen extends MyActivity_v4 {

//    private String appUrl = "";

    boolean isFirstIn = false;

    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private static final int DENY_KEY_BACK = 1002;

    private static final String SHAREDPREFERENCES_NAME = "first_pref";

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3;

    private SharedPreferencesData sp;

    private static boolean isbackkeyvaluable = false;

    static String phone;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GO_HOME:
                        goHome();
                        break;
                    case GO_GUIDE:
                        goGuide();
                        break;
                    case DENY_KEY_BACK:
                        isbackkeyvaluable = true;
                        break;
                    case 1004:
                        if (SettingUtils.getInstance(SplashScreen.this).isPatternPwdOn(phone) && new SharedPreferencesData(SplashScreen.this).getBoolean("hasLogin")) {

                            Intent intent = new Intent(SplashScreen.this, UnlockGesturePasswordActivity.class);
                            intent.putExtra(UnlockGesturePasswordActivity.Param.REQ_TYPE, UnlockGesturePasswordActivity.Param.ReqType.UNLOCK_FOR_NORMAL);
                            String username = "";
                            if (!TextUtils.isEmpty(new SharedPreferencesData(SplashScreen.this).getValue("nickName"))) {
                                username = new SharedPreferencesData(SplashScreen.this).getValue("nickName");
                            } else if (!TextUtils.isEmpty(new SharedPreferencesData(SplashScreen.this).getValue("userName"))) {
                                username = new SharedPreferencesData(SplashScreen.this).getValue("userName");
                            } else {
                                username = new SharedPreferencesData(SplashScreen.this).getValue("phone");
                            }
                            intent.putExtra(UnlockGesturePasswordActivity.Param.USERNAME, username);
                            startActivity(intent);
                            new SharedPreferencesData(SplashScreen.this).setBoolean("hasLogin", true);
                            finish();
                        } else {

                            Intent intent = new Intent();
                            intent.putExtra("tab0", "tab0");
                            intent.setClass(SplashScreen.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

		setContentView(R.layout.splash);
        //让状态栏透明


        init();

        if (Build.VERSION.SDK_INT >= 23) {//M 运行时权限申请
            //申请存储权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    ) {
                PermissionWarnDialog d = new PermissionWarnDialog(this);
                d.setTitle("");
                d.show();
                d.setOnClickNext(new OnClickNext() {
                    @Override
                    public void next() {
                        if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SplashScreen.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        } else if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SplashScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                            , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        } else if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SplashScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                                            , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    }
                });
            } else {
                load();
            }
        } else {
            load();
        }

        mHandler.sendEmptyMessageDelayed(DENY_KEY_BACK, 3000);


        try {
            phone = sp.getOtherValue("user0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        sp = new SharedPreferencesData(this);

        // 使用SharedPreferences来记录程序的使用次数
        SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);

        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = preferences.getBoolean("isFirstIn", true);
    }

    private void load() {
        Map<String, Object> map = new HashMap<>();
        map.put("appType", "1");
        postData(map, "/more/getVersion", App.PostType.LOAD, false);/*此处是防止，登录后报回话超时的bug，不能去掉*/
        if (!isFirstIn) {
            //判断SP中存的版本号是否小于app中取得,每次app升级安装后都会出现引导页
            PackageManager manager = this.getPackageManager();
            int versionCode = 1;
            try {
                PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
                versionCode = info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            int versionCodeSP = sp.getOtherIntValue("versionCode");

            boolean hasPatternOn = false;
            if (SettingUtils.getInstance(App.getInstance()).isPatternPwdOn(phone)) {// && sp.getBoolean("hasLogin")
                hasPatternOn = true;
            }
            if (versionCode > versionCodeSP) {
                mHandler.sendEmptyMessageDelayed(GO_HOME, hasPatternOn ? Config.SPLASH_DELAY_MILLIS_PATTERN : Config.SPLASH_DELAY_MILLIS);
                App.setHasUpdate(false);
                App.setIgnoreUpdate(false);
            } else {
                mHandler.sendEmptyMessageDelayed(GO_HOME, hasPatternOn ? Config.SPLASH_DELAY_MILLIS_PATTERN : Config.SPLASH_DELAY_MILLIS);
            }
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, Config.SPLASH_DELAY_MILLIS);
        }
    }

    private void goHome() {
//        if(SettingUtils.getInstance(this).isFingerPwdOn(phone)){
//            startActivity(new Intent(this, FingerPrintUnlockActivity.class));
//        }else
		if(SettingUtils.getInstance(this).isPatternPwdOn(phone)){// && sp.getBoolean("hasLogin")

			Intent intent = new Intent(this, UnlockGesturePasswordActivity.class);
			intent.putExtra(UnlockGesturePasswordActivity.Param.REQ_TYPE, UnlockGesturePasswordActivity.Param.ReqType.UNLOCK_FOR_NORMAL);
			String username="";
			if (!TextUtils.isEmpty(sp.getValue("nickName"))) {
				username = sp.getValue("nickName");
			} else if(!TextUtils.isEmpty(sp.getValue("userName"))){
				username = sp.getValue("userName");
			} else if(!TextUtils.isEmpty(sp.getValue("phone"))){
				username = sp.getValue("phone");
			} else{
                username = sp.getOtherValue("user0");
            }
			intent.putExtra(UnlockGesturePasswordActivity.Param.USERNAME, username);
            startActivity(intent);
			sp.setBoolean("hasLogin", true);
            finish();
		} else {
			Intent intent = new Intent();
			intent.putExtra("tab0", "tab0");
			intent.setClass(this, MainActivity.class);
            startActivity(intent);
            finish();
		}
    }

    private void goGuide() {
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
        finish();
    }

//    public void requestFail(String msg, App.PostType postType) {
//        super.requestFail(msg, postType);
//
//        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
//        if (!isFirstIn) {
//            //判断SP中存的版本号是否小于app中取得,每次app升级安装后都会出现引导页
//            PackageManager manager = this.getPackageManager();
//            int versionCode = 1;
//            try {
//                PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
//                versionCode = info.versionCode;
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//            SharedPreferencesData sp = new SharedPreferencesData(this);
//            int versionCodeSP = sp.getOtherIntValue("versionCode");
//
//            boolean hasPatternOn = false;
//            if (SettingUtils.getInstance(getApplicationContext()).isPatternPwdOn(phone) && new SharedPreferencesData(this).getBoolean("hasLogin")) {
//                hasPatternOn = true;
//            }
//            if (versionCode > versionCodeSP) {
////                mHandler.sendEmptyMessageDelayed(GO_GUIDE, hasPatternOn ? Config.SPLASH_DELAY_MILLIS_PATTERN : Config.SPLASH_DELAY_MILLIS);
//
//                //因为版本无引导页，所以不进入Guide页
//                mHandler.sendEmptyMessageDelayed(GO_HOME, hasPatternOn ? Config.SPLASH_DELAY_MILLIS_PATTERN : Config.SPLASH_DELAY_MILLIS);
//            } else {
//                mHandler.sendEmptyMessageDelayed(GO_HOME, hasPatternOn ? Config.SPLASH_DELAY_MILLIS_PATTERN : Config.SPLASH_DELAY_MILLIS);
//            }
//        } else {
//            mHandler.sendEmptyMessageDelayed(GO_GUIDE, Config.SPLASH_DELAY_MILLIS);
//        }
//
//    }
//
//    public void requestSuccess(JSONObject object, App.PostType postType) {
//        super.requestSuccess(object, postType);
//        if (object != null) {
//            if (postType == App.PostType.LOAD) {
//                try {
//                    JSONObject data = object.getJSONObject("data");
//                    if (data != null) {
//                        if (Build.VERSION.SDK_INT >= 23) {//M 运行时权限申请
//                            if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                                ActivityCompat.requestPermissions(SplashScreen.this,
//                                        new String[]{
//                                                Manifest.permission.READ_EXTERNAL_STORAGE
//                                                , Manifest.permission.WRITE_EXTERNAL_STORAGE
//                                        }, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//                            } else {
//                                appUrl = data.getString("appUrl");
//                                new UpdateManager(SplashScreen.this, appUrl).start();
//                            }
//                        } else {
//                            appUrl = data.getString("appUrl");
//                            new UpdateManager(SplashScreen.this, appUrl).start();
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else if (postType == App.PostType.SUBMIT) {
//                JSONObject data;
//                try {
//                    data = object.getJSONObject("data");
//                    if (data != null) {
//                        new SharedPreferencesData(SplashScreen.this).setValue("token", data.getString("token"));
//                        Intent intent = new Intent();
//                        intent.putExtra("home", "");
//                        intent.setClass(SplashScreen.this, MainActivity.class);
//                        sp.setBoolean("hasLogin", true);
//                        startActivity(intent);
////						sp.setBooleanByUser(phone,"isValidate", false);
//                        finish();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (!isbackkeyvaluable) {
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("Override")
    @Override
    public void onRequestPermissionsResult(int arg0, String[] arg1, int[] arg2) {
        super.onRequestPermissionsResult(arg0, arg1, arg2);
        switch (arg0) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (arg1.length > 0 && arg2[0] == PackageManager.PERMISSION_GRANTED) {
                    load();
                } else {
                    ToastMakeText.showToastLongDuration(SplashScreen.this, "您拒绝了访问存储空间，可能导致程序出错，请在下一次提示允许该权限", 2000);
                    load();
                }
                break;
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
                if (arg1.length > 0 && arg2[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SplashScreen.this,
                                new String[]{
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                } else {
                    Toast.makeText(SplashScreen.this, "您拒绝了拨打电话，可能导致程序出错，请在下一次提示允许该权限", Toast.LENGTH_LONG).show();
                    if (ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SplashScreen.this,
                                new String[]{
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }
                break;
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:

                break;
            default:
                break;
        }
    }
}
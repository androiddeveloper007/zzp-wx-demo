package com.cstz.tab;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstz.common.App;
import com.cstz.common.App.PostType;
import com.cstz.common.MyFragmentActivity;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.SysApplication;
import com.cstz.common.UpdateManager;
import com.cstz.common.WebViewRelease;
import com.cstz.common.view.LuckyDrawDialog_1;
import com.cstz.common.view.UpdateDialog;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.fragment.UserCenter;
import com.cstz.tools.StatusBarUtil;
import com.cstz.tools.ToastMakeText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends MyFragmentActivity implements OnClickListener {
    private More Shezhifragment;//UserCenter
    //    private Home Gerenfragment;
    private Product Xiangmufragment;
    private Index indexFragment;
    //    private Partner partnerFragment;
    private UserCenter meActivity;//HomeLogin
    private View shouye_layout;
    private View xiangmu_layout;
    private View geren_layout;
    private View shezhi_layout;
    private FragmentManager fragmentManager;
    private ImageView shouye_image;
    private ImageView xiangmu_image;
    private ImageView geren_image;
    private ImageView shezhi_image;
    private TextView shouye_text;
    private TextView geren_text;
    private TextView shezhi_text;
    private TextView xiangmu_text;

    private boolean isfirstclicktab2_1 = true;
    private static Handler handler = new Handler();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static Boolean isExit = false;
    private FragmentTransaction transaction;
    private String appUrl = "";
    private UpdateDialog d;
    private Fragment fg;
    boolean rightBtnClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        SysApplication.getInstance().addActivity(MainActivity.this);
        initview();

        if (getIntent() != null && getIntent().getStringExtra("partner") != null) {
            setTabSelection(3);
        } else if (getIntent() != null && getIntent().getStringExtra("home") != null) {
            StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
            setTabSelection(2);
//            if(getIntent().hasExtra("registerToPattern"))
//                showRiskEvalutionDia();
        } else if (getIntent() != null && getIntent().getStringExtra("product") != null) {
            StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
            setTabSelection(1);
        } else if (getIntent() != null && getIntent().getStringExtra("tab0") != null) {
            setTabSelection(0);
        } else {
            setTabSelection(0);
        }

        //检测版本更新
        load();
    }

    private void showRiskEvalutionDia() {
        LuckyDrawDialog_1 d = new LuckyDrawDialog_1(this, true, 0.8);
        String text = "根据监管要求，您需要进行风险承受能力评估。我们将根据评估结果为您更好的配置资产。请您认真作答，" +
                "感谢您的配合!";
        SpannableStringBuilder span = new SpannableStringBuilder("缩进"+text);
        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        d.setMessage(span, getResources().getDimensionPixelOffset(R.dimen.dimen_12));
        d.setBtn1Text("取消");
        d.setBtn2Text("确定");
        d.setTitleVisible(true, "尊敬的用户：");
        d.setTitleLeft();
        d.setTextIvestVisible(false);
        d.show();
        d.setOnBtn2ClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, WebViewRelease.class);
                i.putExtra("url","http://m.dm.wuxingjinrong.com/activity/inDraw?vname=eggswaptwo");
                i.putExtra("title","风险能力评估");
                startActivity(i);
            }
        });
    }

    private void load() {
        boolean ignore_update = App.IGNORE_UPDATE;
        if (App.FIRSTCHECKREQUEST && !ignore_update) {
            Map<String, Object> map = new HashMap<String, Object>();
            PackageManager manager = getPackageManager();
            String version = "";
            try {
                PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
                version = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            map.put("version", version);
            map.put("appType", "1");
            postData(map, "/more/getVersion", PostType.LOAD, false);
        }
    }
    /**
     * 初始化各个控件
     */
    public void initview() {
        fragmentManager = getSupportFragmentManager();
        shouye_layout = findViewById(R.id.shouye_layout);
        xiangmu_layout = findViewById(R.id.xiangmu_layout);
        geren_layout = findViewById(R.id.geren_layout);
        shezhi_layout = findViewById(R.id.shezhi_layout);

        shouye_image = (ImageView) findViewById(R.id.shouye_image);
        xiangmu_image = (ImageView) findViewById(R.id.xiangmu_image);
        geren_image = (ImageView) findViewById(R.id.geren_image);
        shezhi_image = (ImageView) findViewById(R.id.shezhi_image);

        shouye_text = (TextView) findViewById(R.id.shouye_text);
        geren_text = (TextView) findViewById(R.id.geren_text);
        shezhi_text = (TextView) findViewById(R.id.shezhi_text);
        xiangmu_text = (TextView) findViewById(R.id.xiangmu_text);

        shouye_layout.setOnClickListener(this);
        xiangmu_layout.setOnClickListener(this);
        geren_layout.setOnClickListener(this);
        shezhi_layout.setOnClickListener(this);

    }

    /**
     * tab切换对应的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shouye_layout:
                setTabSelection(0);
                break;
            case R.id.xiangmu_layout:
                setTabSelection(1);
                break;
            case R.id.geren_layout:
                if (ToastMakeText.isFastClick()) {
                    return;
                }
                setTabSelection(2);
                break;
            case R.id.shezhi_layout:
                setTabSelection(4);
                break;
            default:break;
        }

    }

    /**
     * 点击对应的tab切换到对应的fragment,并且颜色 图标根据点击而变化;
     */
    private void setTabSelection(int index) {
        clearSelection();
//		FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:
                shouye_image.setImageResource(R.drawable.shouyebule);
                shouye_text.setTextColor(Color.parseColor("#e04745"));
                indexFragment = new Index();
                transaction.add(R.id.content, indexFragment);
                fg = indexFragment;
                break;
            case 1:
                xiangmu_image.setImageResource(R.drawable.xiangmubule);
                xiangmu_text.setTextColor(Color.parseColor("#e04745"));
                Xiangmufragment = new Product();
                transaction.add(R.id.content, Xiangmufragment);
                fg = Xiangmufragment;
                break;
            case 2:
                geren_image.setImageResource(R.drawable.gerenbule);
                geren_text.setTextColor(Color.parseColor("#e04745"));
                /*if(meActivity==null){

                }else{
                    transaction.show(meActivity);
                    fg = meActivity;
                }*/
                meActivity = new UserCenter();// HomeLogin
                if(getIntent()!=null && getIntent().hasExtra("userType")){
                    Bundle bundle = new Bundle();
                    bundle.putString("userType", getIntent().getStringExtra("userType"));
                    bundle.putString("regAsDeposit", getIntent().getStringExtra("regAsDeposit"));
                    bundle.putString("depositCheck", getIntent().getStringExtra("depositCheck"));
                    meActivity.setArguments(bundle);
                }
                transaction.add(R.id.content, meActivity);
                transaction.show(meActivity);
                if (isfirstclicktab2_1) {
                    isfirstclicktab2_1 = false;
                    meActivity.setBooleanFirstclicktab2(true);
                } else {
                    meActivity.setBooleanFirstclicktab2(false);
                }
                fg = meActivity;
                break;
            case 4:
                shezhi_image.setImageResource(R.drawable.shezhibule);
                shezhi_text.setTextColor(Color.parseColor("#e04745"));
                if (Shezhifragment != null) {
                    transaction.show(Shezhifragment);
                    fg = Shezhifragment;
//                    if(Build.VERSION.SDK_INT > 22) {
//                        StatusBarUtil.setTranslucentForImageViewInFragment(MainActivity.this, 0, null);
//                    }
                } else {
                    Shezhifragment = new More();//UserCenter
                    transaction.add(R.id.content, Shezhifragment);
                    fg = Shezhifragment;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Shezhifragment = null;
                        }
                    }, 30000);
                }
                break;
            default:break;
        }
        transaction.commit();
    }

    /**
     * 对每个fragment进行隐藏显示等控制。
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (indexFragment != null) {
            transaction.hide(indexFragment);
        }
        if (Xiangmufragment != null) {
            transaction.hide(Xiangmufragment);
        }
        if (meActivity != null) {
            transaction.hide(meActivity);
        }
        if (Shezhifragment != null) {
            transaction.hide(Shezhifragment);
        }
    }

    /**
     * 原先每个图片，text的初始值;
     */
    private void clearSelection() {
        shouye_image.setImageResource(R.drawable.shouye);
        shouye_text.setTextColor(Color.parseColor("#afafaf"));
        xiangmu_image.setImageResource(R.drawable.xiangmu);
        xiangmu_text.setTextColor(Color.parseColor("#afafaf"));
        geren_image.setImageResource(R.drawable.geren);
        geren_text.setTextColor(Color.parseColor("#afafaf"));
        shezhi_image.setImageResource(R.drawable.shezhi);
        shezhi_text.setTextColor(Color.parseColor("#afafaf"));
    }

    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            if (postType == PostType.LOAD) {
                try {
                    JSONObject data = object.getJSONObject("data");
                    //M 运行时权限申请
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    }, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    }
                    if (data != null) {
                        if (data.has("appUrl") && !TextUtils.isEmpty(data.getString("appUrl")))
                            appUrl = data.getString("appUrl");
                        if (data.has("forceUpdate") && TextUtils.equals("0", data.getString("forceUpdate")))
                            App.setHasUpdate(true);//设置有版本更新
                        else
                            App.setHasUpdate(false);
                        //强制更新
                        if (data.has("forceUpdate") && TextUtils.equals("1", data.getString("forceUpdate"))) {
//                            new UpdateManager(this, appUrl).start();
                            if (data.has("updateLog") && !TextUtils.isEmpty(data.getString("updateLog"))) {
                                List<String> list;
                                String updateLog = data.getString("updateLog");
                                String[] array = updateLog.split("；");
                                list = java.util.Arrays.asList(array);
                                d = new UpdateDialog(this, list);
                                if (data.has("versionNum") && !TextUtils.isEmpty(data.getString("versionNum")))
                                    d.setversion(data.getString("versionNum"));
                                d.setOnBtn2ClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        rightBtnClicked=true;
                                        new UpdateManager(MainActivity.this, appUrl,true).start();
                                    }
                                });
                                d.setCheckboxVisibility(false);
                                d.setTipVisible(true);
                                d.show();
                                d.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        if(!rightBtnClicked) SysApplication.getInstance().exit();
                                    }
                                });
                            }
                            return;
                        }
                        //非强制更新
                        if (data.has("forceUpdate") && TextUtils.equals("0", data.getString("forceUpdate"))) {
                            boolean ignore_update = App.IGNORE_UPDATE;
                            if (App.FIRSTCHECKREQUEST && !ignore_update) {
                                if (data.has("updateLog") && !TextUtils.isEmpty(data.getString("updateLog"))) {
                                    List<String> list;
                                    String updateLog = data.getString("updateLog");
                                    String[] array = updateLog.split("；");
                                    list = java.util.Arrays.asList(array);
                                    d = new UpdateDialog(this, list);
                                    if (data.has("versionNum") && !TextUtils.isEmpty(data.getString("versionNum")))
                                        d.setversion(data.getString("versionNum"));
                                    d.setOnBtn1ClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (d.getBooleanIgnore())
                                                new SharedPreferencesData(MainActivity.this).setOtherBoolean("IGNORE_UPDATE", true);
                                        }
                                    });
                                    d.setOnBtn2ClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new UpdateManager(MainActivity.this, appUrl).start();
                                        }
                                    });
                                    d.show();
                                    d.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            App.FIRSTCHECKREQUEST = false;
                                        }
                                    });
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fg instanceof More) {
                if (!More.home())
                    More.onKeyDown(keyCode, event);
                else if (keyCode == KeyEvent.KEYCODE_BACK && More.home()) {
                    if (!isExit) {
                        isExit = true;
                        ToastMakeText.showToast(MainActivity.this, "再按一次退出程序", 1200);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isExit = false;
                            }
                        }, 1500);
                        return false;
                    } else {
                        new SharedPreferencesData(this).setValue("token", "");
                        SysApplication.getInstance().exit();
                        return true;
                    }
                }
            } else {
                if (!isExit) {
                    isExit = true;
                    ToastMakeText.showToast(MainActivity.this, "再按一次退出程序", 1200);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isExit = false;
                        }
                    }, 1500);
                    return false;
                } else {
                    new SharedPreferencesData(this).setValue("token", "");
                    SysApplication.getInstance().exit();
                    return true;
                }
            }
            return true;
        } else {

            return super.onKeyDown(keyCode, event);//true
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ToastMakeText.showToastLongDuration(this, "您拒绝了访问存储空间，可能导致程序出错，请在下一次提示允许该权限", 2000);
                    load();
                }
                break;
            default:
                break;
        }
    }
}
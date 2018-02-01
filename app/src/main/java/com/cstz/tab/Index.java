package com.cstz.tab;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cstz.common.App;
import com.cstz.common.App.PostType;
import com.cstz.common.Config;
import com.cstz.common.MyFragment;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.UpdateManager;
import com.cstz.common.Web;
import com.cstz.common.WebViewRelease;
import com.cstz.common.view.UpdateDialog;
import com.cstz.cstz_android.R;
import com.cstz.cunguan.dialog.CunGuanIntroduceDialog;
import com.cstz.front.Login;
import com.cstz.front.ProductActivity;
import com.cstz.tools.Convert;
import com.cstz.tools.StatusBarUtil;
import com.cstz.tools.ToastMakeText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.bgabanner.BGABanner;
import library.widget.progressbarcg.FloatTextProgressBar;

/**
 * 首页
 */
public class Index extends MyFragment implements OnClickListener, OnItemClickListener {
    private BGABanner _bannerView;

    private ArrayList<Map<String, Object>> _bannerList = new ArrayList<>();
    private List<String> banner_list = new ArrayList<>();
    private RelativeLayout mProductLayout = null;
    private RelativeLayout relativelayout_xinshou, relativelayout_yhhd;
    private ListView _listview;//XListView
    private MyAdapter_p adapter_p = null;
    private List<Map<String, Object>> listget = new ArrayList<>();
    private UpdateDialog d;
    private int type = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private String appUrl = "";
    private LinearLayout headerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.index0, container, false);
        if(Build.VERSION.SDK_INT > 22) {
            StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 0, null);
            //设置状态栏文字白色
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initView(view);
        initData();
        return view;
    }

    public void initView(View view) {
        _listview = (ListView) view.findViewById(R.id.listView_index);//XListView
//        _listview.setPullRefreshEnable(false);
//        _listview.setPullLoadEnable(false);
        _listview.setOnItemClickListener(this);
    }

    public void initView1(View view){
        relativelayout_xinshou = (RelativeLayout) view.findViewById(R.id.relativelayout_xinshou);
        relativelayout_yhhd = (RelativeLayout) view.findViewById(R.id.relativelayout_yhhd);
        mProductLayout = (RelativeLayout) view.findViewById(R.id.index_relativelayout);
        clicklistener();
    }

    private void initData() {
        Map<String, Object> map = new HashMap<>();
        map.put("token",new SharedPreferencesData(getActivity()).getValue("token"));
        postData(map, "/index", mProductLayout, PostType.LOAD);
    }

    public void reloadAcitivtyView(RelativeLayout view) {
        super.reloadAcitivtyView(view);
        view.addView(_listview);
    }

    public void requestFail(String msg, PostType postType) {
        super.requestFail(msg, postType);
        if (type == 0)
            ToastMakeText.showToast(getActivity(), msg, 1200);
    }

    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        if (object != null) {
            if (postType == PostType.LOAD && type == 0) {
                if (object.has("data")) {
                    try {
                        JSONArray data = object.getJSONArray("data");
                        int length = data.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject img = data.getJSONObject(i);
                            Map<String, Object> map = new HashMap<>();
                            map.put("imgUrl", img.getString("imgUrl"));
                            map.put("imgLinkUrl", img.getString("imgLinkUrl"));
                            map.put("imgName", img.getString("imgName"));
                            _bannerList.add(map);
                            String img_url = img.getString("imgUrl");
                            if (!img.getString("imgUrl").contains("http")) {
                                img_url = Config.getHttpConfig() + img.get("imgUrl").toString();
                            }
                            banner_list.add(img_url);
                        }
                        headerView = (LinearLayout) View.inflate(getActivity(), R.layout.index_head, null);
                        initView1(headerView);
                        _bannerView = (BGABanner) headerView.findViewById(R.id.index_banner);
                        _bannerView.setAdapter(new BGABanner.Adapter() {
                            @Override
                            public void fillBannerItem(BGABanner banner, final View view, Object model, int position) {
                                Glide.with(getActivity())
                                        .load(model).placeholder(R.drawable.icon_stub)
                                        .error(R.drawable.icon_stub).into((ImageView) view);
                            }
                        });
                        _bannerView.setData(R.layout.view_image, banner_list, null);
                        _bannerView.setOnItemClickListener(new BGABanner.OnItemClickListener() {
                            @Override
                            public void onBannerItemClick(BGABanner banner, View view, Object model, int position) {
                                int current = _bannerView.getCurrentItem();
                                String imgName = _bannerList.get(current).get("imgName").toString();
                                String imgLinkUrl = _bannerList.get(current).get("imgLinkUrl").toString();
                                if (!TextUtils.isEmpty(imgLinkUrl)) {
                                    Intent intent = new Intent(getActivity(), WebViewRelease.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("url", imgLinkUrl);
                                    bundle.putString("title", imgName);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
                        });
                    } catch (JSONException e) {
                    }
                }
                if (object.has("data2")) {
                    try {
                        JSONArray data = object.getJSONArray("data2");
                        if (data != null) {
                            int length = data.length();
                            if(!(length>0)){
                                alertView(App.AlertViewType.NO_DATA);
                            }
                            for (int i = 0; i < length; i++) {
                                JSONObject record = data.getJSONObject(i);
                                Map<String, Object> map = new HashMap<>();
                                map.put("schedules", record.getString("schedules"));
                                map.put("productId", record.getString("productId"));
                                map.put("productTitle", record.getString("productTitle"));
                                map.put("annualRate", record.getString("annualRate"));
                                map.put("deadline", record.getString("deadline"));
                                map.put("publisher", record.getString("publisher"));
                                map.put("productAmount", record.getString("productAmount"));
                                map.put("status", record.getString("status"));
                                map.put("position", record.getString("position"));
                                map.put("award", record.getString("award"));
                                if(record.has("cashback"))
                                    map.put("cashback", record.getString("cashback"));
                                listget.add(map);
                            }
                            if (adapter_p == null) {
                                adapter_p = new MyAdapter_p(listget);

                                _listview.addHeaderView(headerView);
                                _listview.requestFocus();

                                _listview.setAdapter(adapter_p);
                            } else {
                                if (_listview.getAdapter() == null) {
                                    _listview.setAdapter(adapter_p);
                                }
                                adapter_p.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                    }
                }
                //检测版本更新
//                load();
                //弹出银行存管专题页弹框
                SharedPreferencesData sp = new SharedPreferencesData(context);
                /*判断上线后一个月内弹出，调用后台接口*/
                /*if(!sp.getOtherBoolean("CG_INTRODUCE") &&
                        Config.nowIsInRange(Config.stringToDate("2017-11-15"),new Date(),30)){
                    sp.setOtherBoolean("CG_INTRODUCE",true);
                }*/
                if(sp.getBoolean("hasLogin") && !TextUtils.isEmpty(sp.getValue("token")))
                    requestCgIntroduce();
            } else if (postType == PostType.LOAD && type == 1) {
                type = 0;
                try {
                    JSONObject data = object.getJSONObject("data");
                    //M 运行时权限申请
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(),
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
                            new UpdateManager(getActivity(), appUrl).start();
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
                                    d = new UpdateDialog(getActivity(), list);
                                    if (data.has("versionNum") && !TextUtils.isEmpty(data.getString("versionNum")))
                                        d.setversion(data.getString("versionNum"));
                                    d.setOnBtn1ClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (d.getBooleanIgnore())
                                                new SharedPreferencesData(getActivity()).setOtherBoolean("IGNORE_UPDATE", true);
                                        }
                                    });
                                    d.setOnBtn2ClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new UpdateManager(getActivity(), appUrl).start();
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
            } else if (postType == PostType.SUBMIT) {
                JSONObject data;
                try {
                    data = object.getJSONObject("data");
                    if (data != null) {
                        new SharedPreferencesData(getActivity()).setValue("token", data.getString("token"));
                        initData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void clicklistener() {
        relativelayout_xinshou.setOnClickListener(this);
        relativelayout_yhhd.setOnClickListener(this);
    }

    public void viewPaint(TextView view) {
        TextPaint tp = view.getPaint();
        tp.setFakeBoldText(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relativelayout_xinshou:
//                String url = Config.getHttpConfig() + "/xinren?isBottom=1&pagetype=1";//old
                String url = Config.getHttpConfig() + "/more/news?isTop=0";
//                String url = "http://m.wuxingjinrong.com/thirdParty/getAgreement?type=1";
                Intent intent = new Intent(getActivity(), Web.class);
                intent.putExtra("url", url);
                intent.putExtra("title", "最新公告");//新手指引
                getActivity().startActivity(intent);
                break;
            case R.id.relativelayout_yhhd:
                /*Intent intent2 = new Intent(getActivity(), MallIndex.class);
                getActivity().startActivity(intent2);*/
                String url1 = Config.getHttpConfig() + "/more/wxInfo?isTop=0";
                Intent intent1 = new Intent(getActivity(), Web.class);
                intent1.putExtra("url", url1);
                intent1.putExtra("title", "五星优势");//优惠活动
                getActivity().startActivity(intent1);
                break;
            default:break;
        }
    }

    private View view;

    class MyAdapter extends PagerAdapter {
        List<String> list_url = null;
        private int mSize;

        public MyAdapter(List<String> list1) {
            this.list_url = list1;
            mSize = list_url.size();
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(getActivity());
            if (imageView != null) {
                imageView.setScaleType(ScaleType.CENTER_CROP);
                String imgurl = banner_list.get(position);
                if (!imgurl.contains("http")) {
                    imgurl = Config.getHttpConfig() + _bannerList.get(position).get("imgUrl").toString();
                }
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ImageOptions options = new ImageOptions.Builder().setIgnoreGif(false)
                        .setImageScaleType(ScaleType.CENTER_CROP)
                        .setLoadingDrawableId(R.drawable.icon_stub)
                        .setFailureDrawableId(R.drawable.icon_error)
                        .build();
                x.image().bind(imageView, imgurl, options);
            }
            imageView.setOnClickListener(new OnClickListener() {//list.get(position).
                @Override
                public void onClick(View v) {
                    String imgName = _bannerList.get(position).get("imgName").toString();
                    String imgLinkUrl = _bannerList.get(position).get("imgLinkUrl").toString();
                    if (!TextUtils.isEmpty(imgLinkUrl)) {
                        Intent intent = new Intent(getActivity(), WebViewRelease.class);//Web
//                        Log.e("wxcf",imgLinkUrl);
                        Bundle bundle = new Bundle();
                        bundle.putString("url", imgLinkUrl);
                        bundle.putString("title", imgName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    class MyAdapter_p extends BaseAdapter {
        private List<Map<String, Object>> listget = new ArrayList<Map<String, Object>>();
        private List<Boolean> isLoaded = new ArrayList<>();
        public MyAdapter_p(List<Map<String, Object>> listget) {
            this.listget = listget;
            for(int i = 0; i<listget.size();i++){ isLoaded.add(false);}
        }

        @Override
        public int getCount() {
            return listget.size();
        }

        @Override
        public Object getItem(int position) {
            return listget.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            myhouder houdler = null;
            if (convertView == null) {
                houdler = new myhouder();
                convertView = LayoutInflater.from(Index.this.getActivity()).inflate(R.layout.index_list, parent, false);//null
                houdler.productTitle = (TextView) convertView.findViewById(R.id.index_product_title);
                houdler.annualRate = (TextView) convertView.findViewById(R.id.textView_annualRate);
                houdler.deadline = (TextView) convertView.findViewById(R.id.textView_deadline);
                houdler.publisher = (TextView) convertView.findViewById(R.id.textview_publisher);
                houdler.textview_jiner = (TextView) convertView.findViewById(R.id.textview_jiner);
                houdler.progressBar_index = (FloatTextProgressBar) convertView.findViewById(R.id.progressBar_index);
//                houdler.textview_baifenbi = (TextView) convertView.findViewById(R.id.textview_baifenbi);
                houdler.imageview_status = (ImageView) convertView.findViewById(R.id.imageview_status);
//                houdler.image_name_song = (ImageView) convertView.findViewById(R.id.image_name_song);
                houdler.list_name_song = (TextView) convertView.findViewById(R.id.list_name_song);
                houdler.imageview_xins = (ImageView) convertView.findViewById(R.id.imageview_xins);
//                houdler.iv_index_list_next = (ImageView) convertView.findViewById(R.id.iv_index_list_next);
                houdler.tv_tag_cashback = (TextView) convertView.findViewById(R.id.tv_tag_cashback);
                houdler.view_balance = convertView.findViewById(R.id.view_balance);
                houdler.view_balance1 = convertView.findViewById(R.id.view_balance1);
                houdler.ll_cashback_award = (LinearLayout) convertView.findViewById(R.id.ll_cashback_award);
                convertView.setTag(houdler);
            } else {
                houdler = (myhouder) convertView.getTag();
            }

            DecimalFormat df = new DecimalFormat("0.0");
            final float award = Convert.strToFloat(listget.get(position).get("award").toString(), 0);
            float cashback = 0.0f;
            if(listget.get(position).get("cashback")!=null){
                cashback = Convert.strToFloat(listget.get(position).get("cashback").toString(), 0);
                if (cashback > 0) {
                    houdler.tv_tag_cashback.setText("返现" + cashback + "%");
                    houdler.tv_tag_cashback.setVisibility(View.VISIBLE);
                } else {
                    houdler.tv_tag_cashback.setVisibility(View.GONE);
                }
            }
            final float annualRate = Convert.strToFloat(listget.get(position).get("annualRate").toString(), 0);
            if (award > 0) {
//                houdler.image_name_song.setBackgroundResource(R.drawable.song);
                houdler.list_name_song.setText("加息" + award + "%");
                houdler.list_name_song.setVisibility(View.VISIBLE);
            } else {
//                houdler.image_name_song.setBackgroundResource(0);
                houdler.list_name_song.setText("");
                houdler.list_name_song.setVisibility(View.GONE);
            }
            //如果返现和加息都为0时
            if(cashback==0 && award==0){
                houdler.ll_cashback_award.setVisibility(View.GONE);
                houdler.view_balance.setVisibility(View.VISIBLE);
                houdler.view_balance1.setVisibility(View.VISIBLE);
            }else{
                houdler.ll_cashback_award.setVisibility(View.VISIBLE);
                houdler.view_balance.setVisibility(View.GONE);
                houdler.view_balance1.setVisibility(View.GONE);
            }
//            int schedules = Convert.toInt(listget.get(position).get("schedules").toString());
            houdler.productTitle.setText(listget.get(position).get("productTitle").toString());
            houdler.annualRate.setText(df.format(annualRate)+"%");
            houdler.deadline.setText(listget.get(position).get("deadline").toString() + "天");
            houdler.publisher.setText(listget.get(position).get("publisher").toString());
            houdler.textview_jiner.setText(listget.get(position).get("productAmount").toString() + "万元");
//            houdler.progressBar_index.setProgress(schedules);
            //避免重复加载
            if(isLoaded.get(position)){

            }else{
                isLoaded.set(position, true);
                float progress = Convert.strToFloat(listget.get(position).get("schedules").toString(),0.00f);
                houdler.progressBar_index.setProgress(progress);
            }
//            float progress = Convert.strToFloat(listget.get(position).get("schedules").toString(),0.00f);
//            houdler.progressBar_index.setProgress(progress);
//            houdler.textview_baifenbi.setText(listget.get(position).get("schedules").toString() + "%");

            int status = Convert.strToInt(listget.get(position).get("status").toString(), 1);
            switch (status) {
                case 2:
                    houdler.imageview_status.setBackgroundResource(R.drawable.product_left);
                    break;
                case 3:
                    houdler.imageview_status.setBackgroundResource(R.drawable.product_left3);
                    break;
                case 4:
                    houdler.imageview_status.setBackgroundResource(R.drawable.product_left1);
                    break;
//                case 5:
//                    houdler.imageview_status.setBackgroundResource(R.drawable.product_left2);
//                    break;

                default:
                    break;
            }
            int xins = Convert.toInt(listget.get(position).get("position").toString());
            if (xins == 1) {
                houdler.imageview_xins.setBackgroundResource(R.drawable.xinshou);//xins
                houdler.imageview_xins.setVisibility(View.VISIBLE);
//                houdler.iv_index_list_next.setVisibility(View.GONE);
            } else {
                houdler.imageview_xins.setVisibility(View.GONE);
//                houdler.iv_index_list_next.setVisibility(View.VISIBLE);
            }
            viewPaint(houdler.annualRate);
            return convertView;
        }

        class myhouder {
            private TextView productTitle;// 标题
            private TextView deadline;// 投资期限
            private TextView annualRate;// 收益
            private TextView publisher;//来源
            private TextView textview_jiner;//金额
            private FloatTextProgressBar progressBar_index;//进度
//            private TextView textview_baifenbi;//进度百分比
            private ImageView imageview_status;//投资状态

//            private ImageView image_name_song;
            private TextView list_name_song;
            private ImageView imageview_xins;
//            private ImageView iv_index_list_next;
            private TextView tv_tag_cashback;
            private View view_balance;
            private View view_balance1;
            private LinearLayout ll_cashback_award;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ProductActivity.class);
        intent.putExtra("productId", listget.get(position - 1).get("productId").toString());
        intent.putExtra("productTitle", listget.get(position - 1).get("productTitle").toString());
        intent.putExtra("status", listget.get(position - 1).get("status").toString());
        startActivity(intent);
    }

    private void load() {
        type = 1;
        boolean ignore_update = App.IGNORE_UPDATE;
        if (App.FIRSTCHECKREQUEST && !ignore_update) {
            Map<String, Object> map = new HashMap<String, Object>();
            PackageManager manager = getActivity().getPackageManager();
            String version = "";
            try {
                PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
                version = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            map.put("version", version);
            map.put("appType", "1");
            postData(map, "/more/getVersion", PostType.LOAD, false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ToastMakeText.showToastLongDuration(getActivity(), "您拒绝了访问存储空间，可能导致程序出错，请在下一次提示允许该权限", 2000);
                    load();
                }
                break;
            default:
                break;
        }
    }

    private void requestCgIntroduce() {
        String path = Config.getHttpConfig() + "/queryPromptAccess";
        final RequestParams params = new RequestParams(path);
        params.addParameter("inputFlag", "3");
        params.addParameter("token", new SharedPreferencesData(context).getValue("token"));
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject data = new JSONObject(arg0);
                    if (data.has("result") && TextUtils.equals("1", data.getString("result"))) {
                        if (data.has("data") && TextUtils.equals("0",data.getString("data"))){
                            //未提示过
                            CunGuanIntroduceDialog d = new CunGuanIntroduceDialog(context,0.9);
                            d.show();
                        }
                    } else if (data.has("result") && TextUtils.equals("-4", data.getString("result"))) {
                        ToastMakeText.showToast(getActivity(), "会话过期，请重新登录", 3000);
                        new SharedPreferencesData(context).removeAll();
                        Intent intent = new Intent(context, Login.class);
                        startActivity(intent);
                    } else {
//                        ToastMakeText.showToast(getActivity(), data.getString("msg"), 1200);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastMakeText.showToast(getActivity(), "服务器连接失败", 1000);
            }
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() {}
            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }
}
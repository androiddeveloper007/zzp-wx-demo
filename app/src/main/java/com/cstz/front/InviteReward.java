package com.cstz.front;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.adapter.MyPagerAdapter;
import com.cstz.common.Config;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.WebViewReleaseCg;
import com.cstz.common.view.EventNoticeDialog;
import com.cstz.common.view.MySliderCg;
import com.cstz.cstz_android.R;
import com.cstz.tools.ToastMakeText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 邀请有奖
 */
public class InviteReward extends Activity implements OnClickListener {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.navigation_right)
    TextView navigationRight;
    @BindView(R.id.title_back)
    ImageView titleBack;
    @BindView(R.id.tv_reward_1)
    TextView tvReward1;
    @BindView(R.id.slider_invite)
    MySliderCg sliderInvite;
    @BindView(R.id.pager_invite)
    ViewPager pagerInvite;
    @BindView(R.id.layout_button_invite)
    LinearLayout layoutButtonInvite;

    List<View> viewlist;
    View page1, page2;
    private library.widget.xlistview.XListView lv0, lv1;
    private Context context;
    private List<Map<String, String>> listget0, listget1, listgetTotal0, listgetTotal1;
    private MyAdapter adapter;
    private RecordAdapter earningAdapter;
    private int pageNum = 1;
    private int pageNum1 = 1;
    private final static int pageSize=15;
    private JSONArray array;
    private int mScreenHeight; // 屏幕高度
    private SharedPreferencesData sp;
    private String shareTitle;
    private String shareImgUrl;
    private String shareUrl;
    private String shareText;
    private String hasAccessInvitePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.invite_reward);
        ButterKnife.bind(this);
        titleTv.setText("邀请有奖");
        navigationRight.setText("规则");
        titleBack.setVisibility(View.VISIBLE);
        context = this;
        sp = new SharedPreferencesData(this);
        initView();
        mScreenHeight = DensityUtil.getScreenHeight();
        requestSum();//总收益
    }

    private void initView() {
        sliderInvite.setOnClickSlider(new MySliderCg.OnClickSlider() {
            @Override
            public void getIndex(int index) {
                pagerInvite.setCurrentItem(index);
            }
        });
        viewlist = new ArrayList<>();
        page1 = View.inflate(this, R.layout.fragment_invite0, null);
        page2 = View.inflate(this, R.layout.fragment_invite1, null);
        viewlist.add(page1);
        viewlist.add(page2);
        initPage0();
        initPage1();
        pagerInvite.setAdapter(new MyPagerAdapter(viewlist));
        pagerInvite.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                sliderInvite.setSelectIndex(position);
                switch (position) {
                    case 0:
//                        lv0.autoRefresh();
                        break;
                    case 1:
//                        lv1.autoRefresh();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void requestSum() {
        String path = Config.getHttpConfig() + "/user/getFriendInvestTotalReward";
        final RequestParams params = new RequestParams(path);
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("result");
                    if (TextUtils.equals("1", result)) {
                        JSONObject data = new JSONObject(object.getString("data"));
                        String totalReward = "0";
                        if (data.has("totalReward"))
                            totalReward = data.getString("totalReward");
                        if (data.has("shareTitle"))
                            shareTitle = data.getString("shareTitle");
                        if (data.has("shareImgUrl"))
                            shareImgUrl = data.getString("shareImgUrl");
                        if (data.has("shareText"))
                            shareText = data.getString("shareText");
                        if (data.has("url"))
                            shareUrl = data.getString("url");
                        if(data.has("hasAccessed"))
                            hasAccessInvitePage = data.getString("hasAccessed");
                        tvReward1.setText(totalReward);
                        //判断是否第一次进入此页面
                        if (!sp.getBoolean("isFirstInInvitePage") && TextUtils.equals("0",hasAccessInvitePage)) {
                            sp.setBoolean("isFirstInInvitePage", true);
                            showDialog();
                        }
                    }else if(TextUtils.equals("-4",result)){
                        ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                        sp.removeAll();
                        Intent intent = new Intent(context, Login.class);
                        startActivity(intent);
                        finish();
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

    private void requestInvitate() {
        String path = Config.getHttpConfig() + "/user/getUserFriend";
        final RequestParams params = new RequestParams(path);
        params.addParameter("pageNum", pageNum);
        params.addParameter("pageSize", pageSize);
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("result");
                    switch (result) {
                        case "1":
                            array = object.getJSONArray("data");
                            if (array != null) {
                                int length = array.length();
                                if (length == 0) {
                                    lv0.setPullLoadEnable(false);
                                    return;
                                }
                                listget0 = new ArrayList<>(length);
                                for (int i = 0; i < length; i++) {
                                    JSONObject product = array.getJSONObject(i);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("mobilePhone", product.getString("mobilePhone"));
                                    map.put("createTime", product.getString("createTime"));
                                    map.put("investFriendCount", product.getString("investFriendCount"));
                                    listget0.add(map);
                                }
                                if (length < pageSize) {
                                    lv0.setPullLoadEnable(false);
                                } else {
                                    lv0.setPullLoadEnable(true);
                                }
                                if (adapter == null && listget0.size() > 0) {
                                    adapter = new MyAdapter(listget0);
                                    lv0.setAdapter(adapter);
                                    listgetTotal0 = new ArrayList<>(length);
                                    listgetTotal0.addAll(listget0);
                                } else if (adapter != null && listget0.size() > 0) {
                                    listgetTotal0.addAll(listget0);
                                    adapter.update(listgetTotal0);
                                }
                            } else {
                                lv0.setPullLoadEnable(false);
                            }
                            break;
                        case "-4":
                            ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
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
                lv0.stopRefresh();
                lv0.stopLoadMore();
                //判断请求返回结果，若为null或空数据，在lv上显示空布局
                if (listget0 == null || listget0.size() == 0) {
                    lv0.setPullLoadEnable(false);
                    listget0 = new ArrayList<>(1);
                    Map<String, String> map = new HashMap<>();
                    map.put("mobilePhone", "");
                    map.put("createTime", "");
                    map.put("investFriendCount", "");
                    listget0.add(map);
                    adapter = new MyAdapter(listget0);
                    lv0.setAdapter(adapter);
                    int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_340);
                    adapter.getNoDataEntity(height);
                    if (pageNum == 1) {
                        listget0.clear();
                    }
                }
            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    private void requestEarning() {
        String path = Config.getHttpConfig() + "/user/getFriendInvestRewardRecord";
        final RequestParams params = new RequestParams(path);
        params.addParameter("pageNum", pageNum1);
        params.addParameter("rt", "app");
        params.addParameter("sys", "android");
        params.setConnectTimeout(20 * 1000);
        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String arg0) {
                try {
                    JSONObject object = new JSONObject(arg0);
                    String result = object.getString("result");
                    switch (result) {
                        case "1":
                            JSONArray array = object.getJSONArray("data");
                            if (array != null) {
                                int length = array.length();
                                if (length == 0) {
                                    lv1.setPullLoadEnable(false);
                                    return;
                                }
                                listget1 = new ArrayList<>(length);
                                for (int i = 0; i < length; i++) {
                                    JSONObject product = array.getJSONObject(i);
                                    Map<String, String> map = new HashMap<>();
                                    map.put("reward", product.getString("reward"));
                                    map.put("mobilePhone", product.getString("mobilePhone"));
                                    map.put("investTime", product.getString("investTime"));
                                    listget1.add(map);
                                }
                                if (length == pageSize) {
                                    lv1.setPullLoadEnable(true);
                                } else {
                                    lv1.setPullLoadEnable(false);
                                }
                                if (earningAdapter == null && listget1.size() > 0) {
                                    earningAdapter = new RecordAdapter(listget1);
                                    lv1.setAdapter(earningAdapter);
                                    listgetTotal1 = new ArrayList<>();
                                    listgetTotal1.addAll(listget1);
                                } else if(earningAdapter != null && listget1.size() > 0){
                                    listgetTotal1.addAll(listget1);
                                    earningAdapter.update(listgetTotal1);
                                }
                            } else {
                                lv1.setPullLoadEnable(false);
                            }
                            break;
                        case "-4":
                            ToastMakeText.showToast((Activity) context,"会话过期，请重新登录",3000);
                            sp.removeAll();
                            Intent intent = new Intent(context, Login.class);
                            startActivity(intent);
                            finish();
                        break;
                        default:break;
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
                lv1.stopRefresh();
                lv1.stopLoadMore();
                //判断请求返回结果，若为null或空数据，在lv上显示空布局
                if (listget1 == null || listget1.size() == 0) {
                    lv1.setPullLoadEnable(false);
                    listget1 = new ArrayList<>(1);
                    Map<String, String> map = new HashMap<>();
                    listget1.add(map);
                    earningAdapter = new RecordAdapter(listget1);
                    lv1.setAdapter(earningAdapter);
                    int height = mScreenHeight - getResources().getDimensionPixelOffset(R.dimen.dimen_340);
                    earningAdapter.getNoDataEntity(height);
                    if (pageNum1 == 1) {
                        listget1.clear();//当非上拉加载时，将之前内存中数据清空
                    }
                }
            }

            @Override
            public boolean onCache(String result) {
                return false;
            }
        });
    }

    private void initPage0() {
        if (lv0 == null) {
            lv0 = (library.widget.xlistview.XListView) page1.findViewById(R.id.listView_invite0);
            lv0.setPullRefreshEnable(true);
            lv0.setPullLoadEnable(false);
            lv0.setAutoLoadEnable(true);
            lv0.setXListViewListener(new library.widget.xlistview.XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    pageNum = 1;
                    listget0 = null;
                    listgetTotal0 = null;
                    adapter = null;
                    requestInvitate();
                }
                @Override
                public void onLoadMore() {
                    pageNum++;
                    requestInvitate();
                }
            });
            requestInvitate();//邀请列表
        }
    }

    private void initPage1() {
        if (lv1 == null) {
            lv1 = (library.widget.xlistview.XListView) page2.findViewById(R.id.listView_invite1);
            lv1.setPullRefreshEnable(true);
            lv1.setPullLoadEnable(false);
            lv1.setAutoLoadEnable(true);
            lv1.setXListViewListener(new library.widget.xlistview.XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    pageNum1 = 1;
                    listget1 = null;
                    listgetTotal1 = null;
                    earningAdapter = null;
                    requestEarning();
                }
                @Override
                public void onLoadMore() {
                    pageNum1++;
                    requestEarning();
                }
            });
            requestEarning();//收益列表
        }
    }

    @OnClick({R.id.title_back, R.id.navigation_right, R.id.layout_button_invite})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.navigation_right:
                Intent i = new Intent(this, WebViewReleaseCg.class);
                i.putExtra("url", Config.getHttpConfig() + "/invite/inviteRule");
                i.putExtra("title", "规则");
                startActivity(i);
                break;
            case R.id.layout_button_invite:
                String title = "缤纷八月，五星以礼相待";
                String text = "和我一起来五星财富理财，专享项目年化收益率高达12%，更有3.5%返现奖励！本息有保障！";
                String imgUrl = Config.getHttpConfig()+"/view/170801/images/shareImg.jpg";
                String url = Config.getHttpConfig()+"/register";
                if(!TextUtils.isEmpty(shareTitle))
                    showShare(shareTitle,shareUrl, shareText, shareImgUrl);
                else
                    showShare(title, url, text, imgUrl);
//                showDialog();
                break;
            default:break;
        }
    }

    private void showDialog() {
        EventNoticeDialog d = new EventNoticeDialog(this, 0.85);
        d.show();
    }

    private void showShare(String title, String url, String txt,String imgUrl) {
//        Log.e("ZP","title:"+title+" url:"+url+"\ntxt"+txt+" imgUrl:"+imgUrl);
        OnekeyShare oks = new OnekeyShare();
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                ToastMakeText.showToast((Activity) context,"邀请好友的分享成功！",2000);
            }
            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                ToastMakeText.showToast((Activity)context,"分享出错了"+throwable,2000);
            }
            @Override
            public void onCancel(Platform platform, int i) {
                ToastMakeText.showToast((Activity)context,"微信分享已取消",2000);
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
        oks.show(this);
    }

    class MyAdapter extends BaseAdapter {
        private List<Map<String, String>> list = new ArrayList<>();
        private boolean isNoData;
        private int height;
        protected LayoutInflater mInflater;

        public MyAdapter(List<Map<String, String>> listget) {
            this.list = listget;
            mInflater = LayoutInflater.from(context);
        }

        // 暂无数据
        public void getNoDataEntity(int height) {
            List<Map<String, String>> list = new ArrayList<>();
            Map<String, String> entity = new HashMap<>();
            this.isNoData = true;
            this.height = height;
            list.add(entity);
            update(list);
        }

        public void update(List<Map<String, String>> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (isNoData) {
                convertView = mInflater.inflate(R.layout.item_no_data_layout, null);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_root_view);
                ImageView iv = ButterKnife.findById(convertView, R.id.iv_placeholder_image);
                iv.setImageResource(R.mipmap.no_data_0);
                rootView.setLayoutParams(params);
                return convertView;
            }
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_invite0, parent, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tvInviteUser.setText(list.get(position).get("mobilePhone").toString());
            holder.tvRegisterTime.setText("注册时间："+list.get(position).get("createTime").toString());
            if (TextUtils.equals("0", list.get(position).get("investFriendCount").toString())) {
                holder.ivInvite0.setBackgroundResource(R.drawable.btn_radius_no);
                holder.ivInvite0.setText("未投资");
            } else {
                holder.ivInvite0.setBackgroundResource(R.drawable.btn_radius_yes);
                holder.ivInvite0.setText("已投资");
            }
            return convertView;
        }

        class Holder {
            @BindView(R.id.tv_invite_user)
            TextView tvInviteUser;
            @BindView(R.id.tv_register_time)
            TextView tvRegisterTime;
            @BindView(R.id.btn_invite0)
            Button ivInvite0;

            Holder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    class RecordAdapter extends BaseAdapter {
        private List<Map<String, String>> list = new ArrayList<>();
        private boolean isNoData;
        private int height;
        protected LayoutInflater mInflater;

        public RecordAdapter(List<Map<String, String>> listget) {
            this.list = listget;
            mInflater = LayoutInflater.from(context);
        }

        // 暂无数据
        public void getNoDataEntity(int height) {
            List<Map<String, String>> list = new ArrayList<>();
            Map<String, String> entity = new HashMap<>();
            this.isNoData = true;
            this.height = height;
            list.add(entity);
            update(list);
        }

        public void update(List<Map<String, String>> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (isNoData) {
                convertView = mInflater.inflate(R.layout.item_no_data_layout, null);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_root_view);
                ImageView iv = ButterKnife.findById(convertView, R.id.iv_placeholder_image);
                iv.setImageResource(R.mipmap.no_data_1);
                rootView.setLayoutParams(params);
                return convertView;
            }
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_invite1, parent, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tvInviteUser.setText("(" + list.get(position).get("mobilePhone").toString() + ")");
            holder.tvRegisterTime.setText(list.get(position).get("investTime").toString());
            String reward = list.get(position).get("reward").toString();
            if (reward.length() < 5)
                holder.tv_invite_earning.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dimen_12));
            else
                holder.tv_invite_earning.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dimen_10));
            holder.tv_invite_earning.setText("+" + reward);// + "元"
            return convertView;
        }

        class Holder {
            @BindView(R.id.tv_invite_user1)
            TextView tvInviteUser;
            @BindView(R.id.tv_register_time)
            TextView tvRegisterTime;
            @BindView(R.id.tv_invite_earning)
            Button tv_invite_earning;

            Holder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
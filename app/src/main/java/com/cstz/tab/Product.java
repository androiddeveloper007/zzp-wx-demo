package com.cstz.tab;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.common.App.PostType;
import com.cstz.common.MyFragment;
import com.cstz.common.SharedPreferencesData;
import com.cstz.common.view.Caculator;
import com.cstz.common.view.MyScreen;
import com.cstz.common.view.MyScreen.OnClickScreen;
import com.cstz.common.view.MySliderWithArrow;
import com.cstz.cstz_android.R;
import com.cstz.front.ProductActivity;
import com.cstz.tools.Convert;
import com.cstz.tools.StatusBarUtil;
import com.cstz.tools.ToastMakeText;
import com.cstz.ui.xlistview.XListView;
import com.cstz.ui.xlistview.XListView.IXListViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import library.widget.FloatingView;
import library.widget.progressbarcg.FloatTextProgressBar;

/**
 * 产品列表Fragment
 */
public class Product extends MyFragment implements IXListViewListener, OnClickListener, FloatingView.OnFloatClickListener {
    private TextView title_tv;
    private XListView _listview;
    private List<Map<String, Object>> listget = null;
    private List<Map<String, Object>> listGetAdded = new ArrayList<>();
    private MyAdapter adapter = null;
    private static Handler mhandler = new Handler();
    private int _pageNum = 1;
    private MySliderWithArrow mSlider;
    private MyScreen mScreen;
    private String type = "1";
    private String publisherId = "";
    private String annualRateId = "";
    private String deadlineId = "";
    private RelativeLayout mLayoutListView;
    int flag = 0;
    boolean flag0 = false;
    boolean flag1 = true;
    boolean flag2 = true;
    private FloatingView mFloatingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.front_product, container, false);
        if(Build.VERSION.SDK_INT > 22) {
            RelativeLayout toolbar = (RelativeLayout) view.findViewById(R.id.layout_title);
            ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
            lp.height = getResources().getDimensionPixelOffset(R.dimen.dimen_42);
            toolbar.setLayoutParams(lp);
            view.setPadding(0, getStatusBarHeight(getActivity()), 0, 0);
            StatusBarUtil.setColor(getActivity(), getResources().getColor(R.color.window_background), 0);
            resetFragmentView(this);
        }
        initview(view);
        initData(_pageNum, true);

        mFloatingView = (FloatingView) view.findViewById(R.id.float_view);
        mFloatingView.setOnFloatClickListener(this);

        return view;
    }

    public void resetFragmentView(Fragment fragment) {
        View contentView = getActivity().findViewById(android.R.id.content);
        if (contentView != null) {
            ViewGroup rootView;
            rootView = (ViewGroup) ((ViewGroup) contentView).getChildAt(0);
            if (rootView.getPaddingTop() != 0) {
                rootView.setPadding(0, 0, 0, 0);
            }
        }
        if (fragment.getView() != null)
            fragment.getView().setPadding(0, getStatusBarHeight(getActivity()), 0, 0);
    }

    /**
     * 初始化控件
     */
    public void initview(View view) {
        final Map<String, Integer> fromMap = new HashMap<String, Integer>();
        fromMap.put("全部", 0);
        fromMap.put("投资中", 2);
        fromMap.put("已募满", 3);
        fromMap.put("兑付中", 4);
        final Map<String, Integer> deadlineMap = new HashMap<String, Integer>();
        deadlineMap.put("全部", 0);
        deadlineMap.put("1个月内", 1);
        deadlineMap.put("1-3个月", 2);
        deadlineMap.put("3-6个月", 3);
        deadlineMap.put("6-12个月", 4);
        final Map<String, Integer> annualRateMap = new HashMap<String, Integer>();
        annualRateMap.put("全部", 0);
        annualRateMap.put("8%以下", 1);
//        annualRateMap.put("8-10%", 2);
        annualRateMap.put("8-12%", 2);
//        annualRateMap.put("10-12%", 3);
        annualRateMap.put("12-16%", 3);
//        annualRateMap.put("12%以上", 4);

        title_tv = (TextView) view.findViewById(R.id.title_tv);
        title_tv.setText("理财");
        mSlider = (MySliderWithArrow) view.findViewById(R.id.myslider);
        mScreen = (MyScreen) view.findViewById(R.id.myscreen);
        mSlider.setOnClickSlider(new MySliderWithArrow.OnClickSlider() {
            @Override
            public void getIndex(int index) {
                switch (index) {
                    case 0:
//                        mScreen.setVisibility(View.GONE);
//                        flag = index;
                        mScreen.setText(sortMap(fromMap), index);
                        mScreen.setVisibility(View.VISIBLE);
                        if (mScreen.getVisibility() == View.VISIBLE && flag != index) {
                            flag = index;
                            flag0 = true;
                            mSlider.resetArrowState(index,true);
                        } else if (flag == index) {
                            if (!flag0) {
                                mScreen.setVisibility(View.VISIBLE);
                                flag0 = true;
                                mSlider.resetArrowState(index,true);
                            } else {
                                mScreen.setVisibility(View.GONE);
                                flag0 = false;
                                mSlider.resetArrowState(index,false);
                            }
                        }
                        break;
                    case 1:
                        mScreen.setText(sortMap(deadlineMap), index);
                        mScreen.setVisibility(View.VISIBLE);
                        if (mScreen.getVisibility() == View.VISIBLE && flag != index) {
                            flag = index;
                            flag1 = true;
                            mSlider.resetArrowState(index,true);
                        } else if (flag == index) {
                            if (!flag1) {
                                mScreen.setVisibility(View.VISIBLE);
                                flag1 = true;
                                mSlider.resetArrowState(index,true);
                            } else {
                                mScreen.setVisibility(View.GONE);
                                flag1 = false;
                                mSlider.resetArrowState(index,false);
                            }
                        }
                        break;
                    case 2:
                        mScreen.setText(sortMap(annualRateMap), index);
                        mScreen.setVisibility(View.VISIBLE);
                        if (mScreen.getVisibility() == View.VISIBLE && flag != index) {
                            flag = index;
                            flag2 = true;
                            mSlider.resetArrowState(index,true);
                        } else if (flag == index) {
                            if (!flag2) {
                                mScreen.setVisibility(View.VISIBLE);
                                flag2 = true;
                                mSlider.resetArrowState(index,true);
                            } else {
                                mScreen.setVisibility(View.GONE);
                                flag2 = false;
                                mSlider.resetArrowState(index,false);
                            }

                        }
                        break;
                    default: break;
                }
            }
        });
        mScreen.setOnClickScreen(new OnClickScreen() {

            @Override
            public void getSelectIndex(int sliderIndex, String selectText,
                                       String selectValue) {

                mSlider.resetArrowState(0,false);
                String _selectText = "";
                if (listget != null && listget.size()>0) listget.clear();
                if(adapter!=null) adapter=null;
                _listview.setPullLoadEnable(false);//点击时出现，加载更多的layout，添加此行代码可隐藏
                switch (sliderIndex) {
                    case 0:
                        publisherId = selectValue;
                        if (selectValue.equals("0")) {
                            _selectText = "状态";
                        } else {
                            _selectText = selectText;
                        }
                        break;
                    case 1:
                        deadlineId = selectValue;
                        if (selectValue.equals("0")) {
                            _selectText = "期限";
                        } else {
                            _selectText = selectText;
                        }
                        break;
                    case 2:
                        annualRateId = selectValue;
                        if (selectValue.equals("0")) {
                            _selectText = "收益";
                        } else {
                            _selectText = selectText;
                        }
                        break;
                    default:
                        break;
                }
                mSlider.setSelectText(_selectText, sliderIndex);
                _pageNum = 1;
                initData(1, false);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                flag0 = false;
                flag1 = false;
                flag2 = false;
                mScreen.setVisibility(View.GONE);
            }

            @Override
            public void setFlags() {
                flag0 = false;
                flag1 = false;
                flag2 = false;
                mSlider.resetArrowState(0,false);
            }

        });
        mScreen.setVisibility(View.GONE);

        mLayoutListView = (RelativeLayout) view.findViewById(R.id.product_listview_layout);
        _listview = (XListView) view.findViewById(R.id.product_listview);
        _listview.setPullRefreshEnable(true);//下拉刷新
        _listview.setPullLoadEnable(true);   //下拉加载
        _listview.setXListViewListener(this);
    }


    private void initData(int pageNum, boolean isShow) {
        new SharedPreferencesData(getActivity()).setOtherValue("refreshTime", DateUtils.formatDateTime(getActivity()
                        .getApplicationContext(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL));

        _listview.setRefreshTime(new SharedPreferencesData(getActivity()).getOtherValue("refreshTime"));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pageNum", pageNum);
        map.put("type", type);//1
//        map.put("publisherId", publisherId);//来源
        map.put("status", publisherId);//状态
        map.put("annualRateId", annualRateId);//收益
        map.put("deadlineId", deadlineId);//期限
        map.put("token",new SharedPreferencesData(getActivity()).getValue("token"));
        postData(map, "/product/productListData", isShow == true ? mLayoutListView : null, PostType.LOAD);
    }

//    private void onLoad() {
//    	_listview.stopRefresh();
//    	_listview.stopLoadMore();
//    }


    public void onRefresh() {
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                _pageNum = 1;
                initData(1, true);//true
                _listview.stopRefresh();
                listget.clear();
                adapter = null;
            }
        }, 900);
    }


    public void onLoadMore() {
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData(++_pageNum, false);//
//				_listview.stopLoadMore();
            }
        }, 600);
    }

    /**
     * 字体加粗
     */
    public void viewPaint(TextView view) {
        TextPaint tp = view.getPaint();
        tp.setFakeBoldText(true);
    }


    public void reloadAcitivtyView(RelativeLayout view) {
        super.reloadAcitivtyView(view);

        view.addView(_listview);

    }

    public void requestConnectionFail() {
        super.requestConnectionFail();
        _listview.stopLoadMore();
    }

    public void requestFail(String msg, PostType postType) {
        super.requestFail(msg, postType);
        ToastMakeText.showToast(getActivity(), msg, 1200);
        _listview.stopLoadMore();
    }

    public void requestSuccess(JSONObject object, PostType postType) {
        super.requestSuccess(object, postType);
        if (listget == null) {
            listget = new ArrayList<Map<String, Object>>();
        }
        listGetAdded.clear();
        if (object != null) {
            if (postType == PostType.LOAD) {
                try {
                    JSONArray array = object.getJSONArray("data");
                    if (array != null) {
                        int length = array.length();

                        for (int i = 0; i < length; i++) {
                            JSONObject product = array.getJSONObject(i);
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("productId", product.getString("productId"));
                            map.put("productTitle", product.getString("productTitle"));
                            map.put("annualRate", product.getString("annualRate"));
                            map.put("award", product.getString("award"));
                            map.put("deadline", product.getString("deadline"));
                            map.put("status", product.getString("status"));
                            map.put("publisher", product.getString("publisher"));
                            map.put("schedules", product.getString("schedules"));
                            map.put("productAmount", product.getString("productAmount"));
                            map.put("position", product.getString("position"));
                            if(product.has("cashback"))
                                map.put("cashback", product.getString("cashback"));
                            listget.add(map);
                            if(adapter==null){

                            } else listGetAdded.add(map);
                        }
                        if (length < 10) {//分页大小为10
                            _listview.setPullLoadEnable(false);
                        } else {
                            _listview.setPullLoadEnable(true);
                        }
                        if (adapter == null) {
                            adapter = new MyAdapter(listget);
                            _listview.setAdapter(adapter);
                            _listview.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent,
                                                        View view, int position, long id) {
                                    Intent intent = new Intent(getActivity(), ProductActivity.class);
//									Intent intent = new Intent(getActivity(), PullupseedetailDemo.class);
                                    intent.putExtra("productId", listget.get(position - 1).get("productId").toString());
                                    intent.putExtra("productTitle", listget.get(position - 1).get("productTitle").toString());
                                    intent.putExtra("status", listget.get(position - 1).get("status").toString());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            _listview.stopLoadMore();
                            adapter.refreshList();
                            adapter.notifyDataSetChanged();
                        }

                    } else {
                        _listview.setPullLoadEnable(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MyAdapter extends BaseAdapter {
        private List<Map<String, Object>> listget = new ArrayList<>();
        private List<Boolean> isLoaded = new ArrayList<>();

        public MyAdapter(List<Map<String, Object>> listget) {
            this.listget = listget;
            for(int i = 0; i<listget.size();i++){ isLoaded.add(false);}
        }

        public void refreshList(){
            for(int i = 0; i<listGetAdded.size();i++){
                isLoaded.add(false);
            }
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.front_product_list, parent, false);//null
                houdler.productTitle = (TextView) convertView.findViewById(R.id.index_product_title);
                houdler.annualRate = (TextView) convertView.findViewById(R.id.textView_annualRate);
                houdler.deadline = (TextView) convertView.findViewById(R.id.textView_deadline);
                houdler.publisher = (TextView) convertView.findViewById(R.id.textview_publisher);
                houdler.textview_jiner = (TextView) convertView.findViewById(R.id.textview_jiner);
                houdler.progressBar_index = (FloatTextProgressBar) convertView.findViewById(R.id.progressBar_index);
                houdler.imageview_status = (ImageView) convertView.findViewById(R.id.imageview_status);
                houdler.list_name_song = (TextView) convertView.findViewById(R.id.list_name_song);
                houdler.imageview_xins = (ImageView) convertView.findViewById(R.id.imageview_xins);
                houdler.tv_tag_cashback = (TextView) convertView.findViewById(R.id.tv_tag_cashback);
                houdler.view_balance = convertView.findViewById(R.id.view_balance);
                houdler.view_balance1 = convertView.findViewById(R.id.view_balance1);
                houdler.ll_cashback_award = (LinearLayout) convertView.findViewById(R.id.ll_cashback_award);
                convertView.setTag(houdler);
            } else {
                houdler = (myhouder) convertView.getTag();
            }
            DecimalFormat df = new DecimalFormat("0.0");
            final float annualRate = Convert.strToFloat(listget.get(position).get("annualRate").toString(), 0);
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
            if (award > 0) {
//                houdler.list_name_song.setBackgroundResource(R.drawable.song);
//                houdler.list_name_song.setText("送" + award + "%");
                houdler.list_name_song.setText("加息" + award + "%");
                houdler.list_name_song.setVisibility(View.VISIBLE);
            } else {
//                houdler.list_name_song.setBackgroundResource(0);
//                houdler.list_name_song.setText("");
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
            houdler.productTitle.setText(listget.get(position).get("productTitle").toString());
            houdler.annualRate.setText(df.format(annualRate)+"%");
            houdler.deadline.setText(listget.get(position).get("deadline").toString() + "天");
            houdler.publisher.setText(listget.get(position).get("publisher").toString());
            houdler.textview_jiner.setText(listget.get(position).get("productAmount").toString() + "万元");
            //避免重复加载
            if(isLoaded.get(position)){
                float progress = Convert.strToFloat(listget.get(position).get("schedules").toString(),0.00f);
                houdler.progressBar_index.setProgressWithoutAnim(progress);
            }else{
                isLoaded.set(position, true);
                float progress = Convert.strToFloat(listget.get(position).get("schedules").toString(),0.00f);
                houdler.progressBar_index.setProgress(progress);
            }

            int status = Convert.toInt(listget.get(position).get("status").toString());
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
                default:
                    break;
            }
            int xins = Convert.toInt(listget.get(position).get("position").toString());
            if (xins == 1) {
                houdler.imageview_xins.setBackgroundResource(R.drawable.xinshou);
                houdler.imageview_xins.setVisibility(View.VISIBLE);
            } else {
                houdler.imageview_xins.setVisibility(View.GONE);
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
            private ImageView imageview_status;//投资状态
            private TextView list_name_song;
            private ImageView imageview_xins;
            private TextView tv_tag_cashback;
            private View view_balance;
            private View view_balance1;
            private LinearLayout ll_cashback_award;
        }
    }

    @Override
    public void onClick(View v) {
    }

    public Map<String, Integer> sortMap(Map<String, Integer> oldMap) {
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(oldMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

            @Override
            public int compare(Entry<java.lang.String, Integer> arg0,
                               Entry<java.lang.String, Integer> arg1) {
                return arg0.getValue() - arg1.getValue();
            }
        });
        Map<String, Integer> newMap = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < list.size(); i++) {
            newMap.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return newMap;
    }

    @Override
    public void floatClick(View view) {
//		Toast.makeText(getActivity(),"点击悬浮广告",Toast.LENGTH_SHORT).show();
//		startActivity(new Intent(getActivity(), Calculater.class));
        Caculator messageDialog = new Caculator(getActivity(), true);

        messageDialog.setTitle("收益计算器");
        messageDialog.show();
    }

    @Override
    public void floatCloseClick() {

    }

}

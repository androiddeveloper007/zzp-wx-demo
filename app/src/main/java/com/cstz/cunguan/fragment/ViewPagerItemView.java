package com.cstz.cunguan.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.lzy.widget.HeaderScrollHelper;

import library.widget.xlistview.XListView;

/**
 * Created by ZP on 2017/8/16.
 */

public class ViewPagerItemView extends ScrollView implements HeaderScrollHelper.ScrollableContainer{

    XListView listView;

    public ViewPagerItemView(Context context) {
        super(context);
    }

    public ViewPagerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPagerItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ViewPagerItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public View getScrollableView() {
        if(listView==null)
            return this;
        else
            return listView;
    }

    public void setScrollableView(XListView listView){
        this.listView = listView;
    }
}

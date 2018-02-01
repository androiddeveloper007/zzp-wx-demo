package com.cstz.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cstz.cunguan.fragment.ViewPagerItemView;

import java.util.List;

public class MyPagerAdapterRepayment extends PagerAdapter{
	List<ViewPagerItemView> viewlist;
	private final String[] TITLES = { "全部", "待还款", "还款中", "已还款", "还款失败"};
	public MyPagerAdapterRepayment(List<ViewPagerItemView> viewlist){
		this.viewlist = viewlist;
	}

	@Override
	public int getCount() {
		return viewlist.size();
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(viewlist.get(position),0);

		return viewlist.get(position);
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		if(viewlist.size()>position)
			((ViewPager) container).removeView(viewlist.get(position));
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position];
	}
}

package com.cstz.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.cstz_android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 选择产品类型的面板
 * */
public class MyScreen extends LinearLayout {

	public class ScreenData
	{
		private int selectIndex;         //当前项选择项
		private Map<String,Integer> map;  //map数据
		private int sliderIndex;               //当前项
		
		public ScreenData(Map<String,Integer> map,int sliderIndex)
		{
			this.selectIndex = -1;
			this.map = map;
			this.sliderIndex = sliderIndex;
		}
		
		public void setSelectIndex(int selectIndex)
		{
			this.selectIndex = selectIndex;
		}
		public int getSelectIndex()
		{
			return this.selectIndex;
		}
		
		public Map<String,Integer> getMap()
		{
			return this.map;
		}
		
		public int getSliderIndex()
		{
			return this.sliderIndex;
		}
		
	}

	private OnClickScreen onClickScreen = null;
	
	private ArrayList<ScreenData> mScreenDataList = new ArrayList<ScreenData>();
	
	private int mSelectIndex = -1;  //当前选择的tab下标

	
	private Context mContext;
	

	List<LinearLayout> layoutlist = new ArrayList<LinearLayout>();
	
	public MyScreen(Context context) {
		super(context);
		mContext = context;
	}
	
	public MyScreen(Context context,AttributeSet attrs) {
		super(context,attrs);
		mContext = context;
		
		this.setBackgroundColor(Color.WHITE);
	}
	
	/**
	 * 是否添加了数据
	 * @param index
	 * @return
	 */
	private int hasMap(int index)
	{
		int has = -2;
//		if(getSelectIndex()==-1)
//			return -2;
		for(int i=0;i<mScreenDataList.size();i++)
		{
			if(mScreenDataList.get(i).getSliderIndex() == index)
			{
				has = mScreenDataList.get(i).getSelectIndex();
				break;
			}
		}
		return has;
	}
	/**
	 * 获取选项对应的显示的文本
	 * @param map
	 * @param index
	 * @return
	 */
	private String getMapKey(Map<String,Integer> map,int index)
	{
		String key = "";
		int i = 0;
		for (Map.Entry<String, Integer> entry : map.entrySet()) {  
			  
			if(i ==index)
			{
				key = entry.getKey();
				break;
			}
		    i++;
		}  
		return key;
	}
	
	/**
	 * 获取选项对应的值
	 * @param map
	 * @param index
	 * @return
	 */
	private String getMapValue(Map<String,Integer> map,int index)
	{
		String value = "";
		int i = 0;
		for (Map.Entry<String, Integer> entry : map.entrySet()) {  
			  
			if(i ==index)
			{
				value = entry.getValue().toString();
				break;
			}
		    i++;
		}  
		return value;
	}
	
	public void setText(Map<String,Integer> map,int sliderIndex)
	{
		
		final int _sliderIndex = sliderIndex;
		if(map!=null)
		{
			this.removeAllViews();
			final int has = hasMap(sliderIndex);
			if(has == -2)
			{
				mScreenDataList.add(new ScreenData(map,sliderIndex));
			}
			int textLength = map.size();
			
			this.setWeightSum(textLength);
			
			for(int i=0;i<textLength;i++)
			{
				LinearLayout  textLayout = new LinearLayout(mContext);
				LinearLayout.LayoutParams  textlayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT) ;
				textlayoutParams.weight = 1f;
				textlayoutParams.setMargins(15, 10, 15, 10);//20, 10, 20, 10
				textLayout.setLayoutParams(textlayoutParams);
				textLayout.setId(1000+i);
				
				final TextView textView = new TextView(mContext);
				textView.setText(getMapKey(map,i));
				textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,mContext.getResources().getDimensionPixelSize(R.dimen.dimen_13));
				textView.setGravity(Gravity.CENTER);
				if(has == i)
				{
					textView.setBackgroundResource(R.drawable.screen_style_selected);
					textView.setTextColor(Color.RED);
				}else
				{
					textView.setBackgroundResource(R.drawable.screen_style_select);
				}
				textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT) );
				textLayout.addView(textView);
				textLayout.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View view) {
						
						@SuppressLint("ResourceType") int selectIndex = view.getId()-1000;
						
						if(selectIndex != has)	//getSelectIndex()
						{
							//修改选中状态
							updateBackGroundColor(selectIndex);

							String selectText = "";
							String selectValue = "";
							
//							int length = mScreenDataList.size();//长度没有写死导致的bug
							
							if(_sliderIndex<3)//length
							{
								for(int i=0;i<3;i++)//length
								{
									ScreenData data = mScreenDataList.get(i);
									if(data.getSliderIndex() == _sliderIndex)
									{
										data.setSelectIndex(selectIndex);
										selectText = getMapKey(data.getMap(),selectIndex);
										selectValue = getMapValue(data.getMap(),selectIndex);
										break;
									}
								}
							}
							//回调
							if(onClickScreen!=null)
							{
								onClickScreen.getSelectIndex(_sliderIndex, selectText, selectValue);
							}
							setSelectIndex(selectIndex);
						}else//当选项已经被选择了
						{
							setVisibility(View.GONE);
							
							if(onClickScreen!=null)
							{
								onClickScreen.setFlags();
							}
						}
						
						
					}
					
					
				});
				
				addView(textLayout);
			}
		}
	}
	
	/**
	 * 修改文本颜色
	 * @param index
	 */
	private void updateBackGroundColor(int index)
	{
		int length = this.getChildCount();
		if(index<=length)
		{
			for(int i=0;i<length;i++)
			{
				LinearLayout layout = (LinearLayout)this.getChildAt(i);
				if(layout!=null)
				{
					TextView textView = (TextView)layout.getChildAt(0);
					if(textView!=null)
					{
						if(i == index)
						{
							textView.setBackgroundResource(R.drawable.screen_style_selected);
							textView.setTextColor(Color.RED);
						}else
						{
							textView.setBackgroundResource(R.drawable.screen_style_select);
							textView.setTextColor(Color.BLACK);
						}
					}
				}
			}
			
		}
	}
	
	
	private void setSelectIndex(int selectIndex)
	{
		this.mSelectIndex = selectIndex;
	}
	
	private int getSelectIndex()
	{
		return this.mSelectIndex;
	}
	
	

	public void setOnClickScreen(OnClickScreen onClickScreen)
	{
		this.onClickScreen = onClickScreen;
	}
	
	public interface OnClickScreen {
		void getSelectIndex(int sliderIndex,String selectText,String selectValue);
		void setFlags();
	}
}

package com.cstz.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.cstz_android.R;

import java.util.ArrayList;

/***
 * tab选择控件
 * @author ZZP
 *
 */
public class MySlider extends LinearLayout  {


	int mIndex;
	public OnClickSlider onClickSlider = null;
	
	private ArrayList<LinearLayout> mItemList = new ArrayList<LinearLayout>();
	private ArrayList<ImageView> mItemImageViewList = new ArrayList<ImageView>();
	
	private int selectColor = 0;
	private int defaultColor = 0;
	
	public boolean itemClickable = true;
	
	public MySlider(Context context) {
		super(context);
		
	}
	
	public MySlider(Context context, AttributeSet attrs) {
		super(context,attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MySlider);

		int weightSum = Integer.parseInt(a.getString(R.styleable.MySlider_weightSum));
		this.setWeightSum(weightSum);
		
		setDefaultColor(a.getColor(R.styleable.MySlider_defaultColor, Color.WHITE));
		setSelectColor(a.getColor(R.styleable.MySlider_selectColor, Color.WHITE));
		
		String text = a.getString(R.styleable.MySlider_text);
		String [] texts = text.split(",");
		
		for(int i=0;i<texts.length;i++)
		{
			
			LinearLayout layout = new LinearLayout(context);
			LayoutParams params = new LayoutParams(
	                LayoutParams.WRAP_CONTENT,
	                LayoutParams.MATCH_PARENT);
			params.weight=1.0f;
			params.width = 0;
			layout.setWeightSum(1);
			layout.setOrientation(VERTICAL);
			layout.setLayoutParams(params);
			addView(layout);



			LinearLayout  itemLayoutText = new LinearLayout(context);
			LayoutParams  itemLayoutTextParams = new
					LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT) ;
			itemLayoutText.setBackgroundColor(Color.WHITE);
			itemLayoutTextParams.weight = 0.9f;
			itemLayoutText.setId(1000+i);
			itemLayoutText.setLayoutParams(itemLayoutTextParams);
			itemLayoutText.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View view) {
					if(!itemClickable){
						return;
					}

					int id = view.getId();
					int index =id - 1000;

					updateBackGroundColor(true,index);
					mIndex = index;

					if(onClickSlider!=null)
					{
						onClickSlider.getIndex(index);
					}

				}

			});

			TextView textView = new TextView(context);
			textView.setText(texts[i]);
//			textView.setTextSize(16);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.dimen_15));
			textView.setGravity(Gravity.CENTER);
			if(i==1)
			{
				//textView.setBackgroundColor(Color.TRANSPARENT);
			}else
			{
				//textView.setBackgroundColor(Color.BLUE);
			}
			textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT) );
			itemLayoutText.addView(textView, 0);
			layout.addView(itemLayoutText);

			mItemList.add(itemLayoutText);

			//
			LinearLayout  itemLayoutText2 = new LinearLayout(context);
			LayoutParams  itemLayoutTextParams2 = new
					LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT) ;
			itemLayoutTextParams2.weight = 0.1f;
			itemLayoutText2.setLayoutParams(itemLayoutTextParams2);
			
			
			
			ImageView imageView = new ImageView(context);
			//imageView.setBackground(getResources().getDrawable(R.drawable.tab_selected_bg));
			imageView.setBackgroundColor(getDefaultColor());
			imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			itemLayoutText2.addView(imageView);
			
			mItemImageViewList.add(imageView);
			
			layout.addView(itemLayoutText2);
		}
		
		//默认项
		updateBackGroundColor(true,0);
		
		a.recycle();
	}
	
	
	private void updateBackGroundColor(boolean isSelected,int index)
	{
		if(isSelected)
		{
			if(index<=mItemList.size())
			{
				TextView tv = null;
				for(int i=0;i<mItemList.size();i++)
				{
					if(index == i)
					{
						tv = (TextView)mItemList.get(i).getChildAt(0);
						if(tv!=null)
						{
							tv.setTextColor(getSelectColor());
							//tv.setTextSize(18);
						}
						mItemImageViewList.get(i).setBackgroundColor(getSelectColor());
					}else
					{
						tv = (TextView)mItemList.get(i).getChildAt(0);
						if(tv!=null)
						{
							tv.setTextColor(Color.BLACK);
							//tv.setTextSize(16);
						}
						mItemImageViewList.get(i).setBackgroundColor(getDefaultColor());
					}
				}
			
			}
			
		}else
		{
			for(int i=0;i<mItemList.size();i++)
			{
				mItemList.get(i).setBackgroundColor(getDefaultColor());
				mItemImageViewList.get(i).setBackgroundColor(getDefaultColor());
				
			}
		}
	}
	
	public void setSelectIndex(int index)
	{
		updateBackGroundColor(true,index);
	}
	
	public void setSelectColor(int color)
	{
		selectColor = color;
	}
	private int getSelectColor()
	{
		return selectColor;
	}
	
	public void setDefaultColor(int color)
	{
		defaultColor = color;
	}
	private int getDefaultColor()
	{
		return defaultColor;
	}
	
	
	public void setOnClickSlider(OnClickSlider onClickSlider)
	{
		this.onClickSlider = onClickSlider;
	}
	
	public void setSelectText(String text,int index)
	{
		if(index<=mItemList.size())
		{
			TextView tv = null;
			for(int i=0;i<mItemList.size();i++)
			{
				if(index == i)
				{
					tv = (TextView)mItemList.get(i).getChildAt(0);
					if(tv!=null)
					{
						tv.setText(text);
						break;
					}
				}
			}
		}
	}
	
	
	
	
	/**
	 * 回调方法
	 * @author songxingcheng
	 *
	 */
	public interface OnClickSlider {

		void getIndex(int index);
		
	}
	
	
	public void setItemClickable(boolean b){
		itemClickable = b;
	}
}

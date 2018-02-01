package com.cstz.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable{
	private boolean isChecked = false;  
	  
    public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    public CheckableLinearLayout(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public CheckableLinearLayout(Context context) {  
        super(context);  
    }  
  
    @Override  
    public void setChecked(boolean checked) {  
        isChecked = checked;  
        changeColor(checked);  
    }  
  
    @Override  
    public boolean isChecked() {  
  
        return isChecked;  
    }  
  
    @Override  
    public void toggle() {  
        this.isChecked = !this.isChecked;  
        changeColor(this.isChecked);  
  
    }  
  
    private void changeColor(boolean isChecked) {  
        //根据check的状态切换颜色  
        if (isChecked) {  
            setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));  
        } else {  
            setBackgroundColor(getResources().getColor(android.R.color.transparent));  
        }  
    }  
  

	
}

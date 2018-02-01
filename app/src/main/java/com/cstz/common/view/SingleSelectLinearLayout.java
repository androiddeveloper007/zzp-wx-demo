package com.cstz.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class SingleSelectLinearLayout extends LinearLayout implements Checkable{
	private boolean checked;
	
	private static final int[] CheckedStateSet = {
		android.R.attr.state_checked
	};
	
	public SingleSelectLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setChecked(boolean checked) {
		this.checked = checked;
		if(checked){
			
		}
		refreshDrawableState();
	}

	@Override
	public boolean isChecked() {
		return checked;
	}

	@Override
	public void toggle() {
		setChecked(!checked);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
            mergeDrawableStates(drawableState, CheckedStateSet);
        }
		return drawableState;
	}
	
	@Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }
	
	
	
}

package com.cstz.MyWidget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.cstz.cstz_android.R;

public class CleanableEditText extends EditText {
	
	private TextWatcherCallBack mCallback;
	private Drawable mDrawable;
	private Context mContext;

	public void setCallBack(TextWatcherCallBack mCallback) {
		this.mCallback = mCallback;
	}

	public CleanableEditText(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public CleanableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public CleanableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public void init() {
		mDrawable = mContext.getResources().getDrawable(R.drawable.icon_clear_edit);//ic_action_clear
		mCallback = null;
		TextWatcher textWatcher = new TextWatcherAdapter() {
			@Override
			public void afterTextChanged(Editable s) {
				updateCleanable(length(), true);
				if(mCallback != null)
					mCallback.handleMoreTextChanged();
			}
		};
		this.addTextChangedListener(textWatcher);
		this.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				updateCleanable(length(), hasFocus);
			}
		});
	}

	public void updateCleanable(int length, boolean hasFocus){
		if(length() > 0 && hasFocus)
			setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawable, null);
		else
			setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int DRAWABLE_RIGHT = 2;
		Drawable rightIcon = getCompoundDrawables()[DRAWABLE_RIGHT];
		if (rightIcon != null && event.getAction() == MotionEvent.ACTION_UP) {
			int leftEdgeOfRightDrawable = getRight() - getPaddingRight()
					- rightIcon.getBounds().width();
			if (event.getRawX() >= leftEdgeOfRightDrawable) {
				setText("");
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void finalize() throws Throwable {
		mDrawable = null;
		super.finalize();
	}
}

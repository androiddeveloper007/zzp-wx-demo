package com.cstz.common.view;

import com.cstz.cstz_android.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;

public class BaseDialog extends Dialog {
	
	@SuppressLint("InlinedApi")
	public BaseDialog(Context context) {
		super(context);
		getContext().setTheme(R.style.CustomDialogStyle);//Theme_Holo_InputMethod
	}

}
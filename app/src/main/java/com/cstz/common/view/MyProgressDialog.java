package com.cstz.common.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.cstz.cstz_android.R;

public class MyProgressDialog extends AlertDialog{

	private ProgressCallback myCallback;
	private ProgressBar mProgress;

	public MyProgressDialog(Context context, int theme) {
	    super(context, theme);
	}

	public MyProgressDialog(Context context) {
	    super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	   setContentView(R.layout.dialog_progress);
	   mProgress = (ProgressBar)findViewById(R.id.dialog_progress1);
	   if(myCallback!=null)
		{
			myCallback.callback();
		}
	}

	@Override
	public void dismiss()
	{
		super.dismiss();
	}
	
	 public void setProgress(int progress)
	 {
		 mProgress.setProgress(progress);
	 }

	 public void setCallback(ProgressCallback myCallback) {  
	        this.myCallback = myCallback;  
	 }
	 
	 public interface ProgressCallback {
			void callback();
			void cancel();
	}

	@Override
	public void onBackPressed() {
		if(myCallback!=null)
			myCallback.cancel();
		super.onBackPressed();
	}
}
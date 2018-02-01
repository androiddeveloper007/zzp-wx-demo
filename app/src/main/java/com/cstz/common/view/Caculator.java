package com.cstz.common.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cstz.cstz_android.R;
import com.cstz.tools.ToastMakeText;

import org.angmarch.views.NiceSpinner;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 计算器对话框
 * 
 * @author ZZP
 *
 */
public class Caculator extends BaseTitleDialog_withoutRadiu implements View.OnClickListener {

	protected TextView btn1, btn2;
	protected ImageView ivLine;
	private Button btn_caculate_dia, btn_reset_dia;
	private EditText et_money_dia;
	private Activity context;
	private NiceSpinner spinner_lilv;
	private NiceSpinner spinner_day;//_uneditable
	private List<String> dataset0;
	private List<String> dataset1;
	private TextView tv_result_dia;
	LinearLayout ll_caculator_dia;

	public Caculator(Context ctx) {
		this(ctx, true);
	}

	public Caculator(Context ctx, boolean cancelable) {
		super(ctx);

		setContentView(View.inflate(getContext(), R.layout.caculator_dialog, null),
				new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		et_money_dia = (EditText) findViewById(R.id.et_money_dia);
		
		tv_result_dia = (TextView) findViewById(R.id.tv_result_dia);
		
		context = (Activity) ctx;

		setCancelable(cancelable);

		setCanceledOnTouchOutside(true);

		setIvCloseVisible(true);

		setBaseLineVisible(false);

		spinner_day = (NiceSpinner) findViewById(R.id.custom_sinnper_day);//_uneditable
		dataset0 = new LinkedList<>(Arrays.asList("33", "93", "183", "365"));
		spinner_day.attachDataSource(dataset0);

		spinner_lilv = (NiceSpinner) findViewById(R.id.custom_sinnper_lilv);
		spinner_lilv.boolean_edit=true;
		dataset1 = new LinkedList<>(Arrays.asList("6", "7", "8", "9", "10", "11", "12", "13", "14", "15"));
		spinner_lilv.attachDataSource(dataset1);

		btn_caculate_dia = (Button) findViewById(R.id.btn_caculate_dia);
		btn_reset_dia = (Button) findViewById(R.id.btn_reset_dia);
		btn_caculate_dia.setOnClickListener(this);
		btn_reset_dia.setOnClickListener(this);
		ll_caculator_dia = (LinearLayout) findViewById(R.id.ll_caculator_dia);

		ll_caculator_dia.setFocusable(true);
		ll_caculator_dia.setFocusableInTouchMode(true);
		ll_caculator_dia.requestFocus();

		setontouchlistener();

		spinner_lilv.setOnPopupwindowCloseListener(new NiceSpinner.popupwindowClosed() {
			@Override
			public void closed() {

				ll_caculator_dia.setFocusable(true);
				ll_caculator_dia.setFocusableInTouchMode(true);
				ll_caculator_dia.requestFocus();
			}
		});

//		ll_caculator_dia.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				ll_caculator_dia.setFocusable(true);
//				ll_caculator_dia.setFocusableInTouchMode(true);
//				ll_caculator_dia.requestFocus();
//				spinner_day.setEnabled(false);
//				spinner_lilv.setEnabled(false);
//			}
//		});
//		spinner_lilv.setEnabled(false);
	}
	private void setOnLongClickListener() {
		spinner_day.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				ToastMakeText.showToast(context,"",1000);
				return false;
			}
		});
	}
	private void setontouchlistener() {
		spinner_day.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 影藏软键盘
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				View currentFocus = getCurrentFocus();
				if (currentFocus != null) {
					imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
				}
				et_money_dia.clearFocus();
				spinner_day.setFocusable(true);
				spinner_day.setFocusableInTouchMode(true);
				spinner_day.requestFocus();
				return false;
			}
		});
		spinner_lilv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 影藏软键盘
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				View currentFocus = getCurrentFocus();
				if (currentFocus != null) {
					imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
				}
				spinner_lilv.setFocusable(true);
				spinner_lilv.setFocusableInTouchMode(true);
				spinner_lilv.requestFocus();
				return false;
			}
		});
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_caculate_dia) {
			// 影藏软键盘
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			View currentFocus = getCurrentFocus();
			if (currentFocus != null) {
				imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
			}
			final String money = et_money_dia.getText().toString();
			final String cal_interest_rate = spinner_lilv.getText().toString();
			final String cal_term = spinner_day.getText().toString();
			if (TextUtils.isEmpty(money)) {
				ToastMakeText.showToast(context, "请输入金额", 1000);
				return;
			}
			if (TextUtils.isEmpty(cal_term)) {
				ToastMakeText.showToast(context, "请输入投资期限", 1000);
				return;
			}
			if (TextUtils.isEmpty(cal_interest_rate)) {
				ToastMakeText.showToast(context, "请输入利率", 1000);
				return;
			}
			final String result = new DecimalFormat("#0.00").format(new BigDecimal(money)
					.multiply(new BigDecimal(cal_interest_rate).divide(new BigDecimal(100)))
					.multiply(new BigDecimal(cal_term)).divide(new BigDecimal(365), 2, BigDecimal.ROUND_HALF_EVEN));
			tv_result_dia.setText(result + "元");
		} else if (id == R.id.btn_reset_dia) {
			tv_result_dia.setText("0.00元");
			spinner_day.setSelectedIndex(0);
			spinner_lilv.setSelectedIndex(0);
			et_money_dia.setText("");
			et_money_dia.requestFocus();
		}
	}
	@Override
	void onclickclose() {
		dismiss();
	}
}

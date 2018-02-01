package control.publishSelectPicPopupWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.cstz.cstz_android.R;

/**
 * 安全退出
 * */
public class PublishSelectPicPopupWindow extends PopupWindow{
	  private RelativeLayout kefu;
	  private RelativeLayout exit;
	  private RelativeLayout quxiao;
	  private View mMenuView;
	  private LinearLayout layout_content;
	  
	  public PublishSelectPicPopupWindow(Activity context,OnClickListener itemsOnClick) {
	    super(context);
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    
	    
	    mMenuView = inflater.inflate(R.layout.popupwindow_out, null);
	    
	    kefu = (RelativeLayout) mMenuView.findViewById(R.id.layout_lianxikefu);
	    exit = (RelativeLayout) mMenuView.findViewById(R.id.layout_out);
	    quxiao=(RelativeLayout) mMenuView.findViewById(R.id.layout_quxiao);
	    layout_content = (LinearLayout) mMenuView.findViewById(R.id.layout_content);
	    quxiao.setOnClickListener(new OnClickListener() {
		      public void onClick(View v) {
		        dismiss();
		      }
	    });
	    layout_content.setOnClickListener(new OnClickListener() {
		      public void onClick(View v) {
		        dismiss();
		      }
	    });
	    //设置按钮监听
//	    kefu.setOnClickListener(itemsOnClick);
	    exit.setOnClickListener(itemsOnClick);
	    //设置SelectPicPopupWindow的View
	    this.setContentView(mMenuView);
	    //设置SelectPicPopupWindow弹出窗体的宽
	    this.setWidth(LayoutParams.MATCH_PARENT);
	    //设置SelectPicPopupWindow弹出窗体的高
	    this.setHeight(LayoutParams.MATCH_PARENT);
	    //设置SelectPicPopupWindow弹出窗体可点击
	    this.setFocusable(true);
	    //设置SelectPicPopupWindow弹出窗体动画效果
	    this.setAnimationStyle(R.style.Dialog);
	    //实例化一个ColorDrawable颜色为半透明
	    ColorDrawable dw = new ColorDrawable(0xb0000000);
	    //设置SelectPicPopupWindow弹出窗体的背景
	    this.setBackgroundDrawable(dw);
	    //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
	    mMenuView.setOnTouchListener(new OnTouchListener() {
	      public boolean onTouch(View v, MotionEvent event) {
	        
	        int height = mMenuView.findViewById(R.id.pop_layout).getTop();
	        int y=(int) event.getY();
	        if(event.getAction()==MotionEvent.ACTION_UP){
	          if(y<height){
	            dismiss();
	          }
	        }				
	        return true;
	      }
	    });

	  }
}

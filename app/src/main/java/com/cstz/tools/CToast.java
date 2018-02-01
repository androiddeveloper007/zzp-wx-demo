package com.cstz.tools;


import com.cstz.common.MyCallback;
import com.cstz.cstz_android.R;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自定义时长的Toast
 * @author DexYang
 *
 */
public class CToast {
	
	
	  
    public CToast(Context context) {
        init(context);
    }
        
    private static CToast single=null;  
       
    public static CToast getInstance(Context context) {  
         if (single == null) {    
             single = new CToast(context);  
         }    
        
        return single;  
    }  
	
	public enum AlterType
	{
		SUCCESS(0),FAIL(1),TEXT(2);
//		private int value;
		AlterType(int value)
		{
//			this.value = value;
		}
		
	}
	
	public static final int LENGTH_SHORT = 2000;
    public static final int LENGTH_LONG = 3500;
//	private static Context _context;
	private MyCallback myCallback;
	
	public static CToast alert(Context context, CharSequence text,AlterType type)
	{
		return  alert( context,  text, LENGTH_SHORT,type);
	}
	
    public static CToast alert(Context context, CharSequence text, int duration,AlterType type) 
    {
    	
    	
//    	_context = context;

    	CToast result = CToast.getInstance(context);
    	if(result.mView!=null)
    	{
    		((TextView)result.mView.findViewById(R.id.dialog_alert_msg)).setText(text);
    		
    	}else
    	{
    		
    	LinearLayout mLayout=new LinearLayout(context);
    	
    	View view = null;
		TextView tv_msg = null;
		if(type == AlterType.TEXT)
		{
			
			view = LayoutInflater.from(context).inflate(R.layout.dialog_toast_text,
				     null);
			tv_msg= (TextView)view.findViewById(R.id.dialog_alert_msg);
			
		}else
		{
			 view = LayoutInflater.from(context).inflate(R.layout.dialog_toast_image,
				     null);
			 tv_msg= (TextView)view.findViewById(R.id.dialog_alert_image_msg);
			 ImageView image = (ImageView)view.findViewById(R.id.dialog_alert_image_image);
			 if(type == AlterType.SUCCESS)
			 {
				 image.setImageResource(R.drawable.alert_success);
			 }else if(type == AlterType.FAIL)
			 {
				 image.setImageResource(R.drawable.alert_fail);
			 }
		}
		tv_msg.setText(text);
		view.getBackground().setAlpha(220); //0~255
		mLayout.addView(view);
		
		//ToastManager.ToastLong(context,mLayout.getChildCount()+"");
		//View v = mLayout.getChildAt(0);
		
		
		
    	
    	/*mLayout.setId(10001);
        TextView tv = new TextView(context);
        tv.setId(10002);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        mLayout.setBackgroundColor(Color.RED);
       // mLayout.setBackgroundResource(R.drawable.widget_toast_bg);
        
        int w=context.getResources().getDisplayMetrics().widthPixels / 2;
        int h=context.getResources().getDisplayMetrics().widthPixels / 10;
        mLayout.addView(tv, w, h);
        */
        
        result.mNextView = mLayout;
        result.mDuration = duration;
    	}
        return result;
    }
    
    
    
    private final Handler mHandler = new Handler();    
    private int mDuration=LENGTH_SHORT;
    private int mGravity = Gravity.CENTER;
    private int mX, mY;
    private float mHorizontalMargin;
    private float mVerticalMargin;
    private View mView;
    private View mNextView;
    
    private WindowManager mWM;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	
	
	 
	 
	 
	/**
     * Set the view to show.
     * @see #getView
     */
    public void setView(View view) {
        mNextView = view;
    }

    /**
     * Return the view.
     * @see #setView
     */
    public View getView() {
        return mNextView;
    }

    /**
     * Set how long to show the view for.
     * @see #LENGTH_SHORT
     * @see #LENGTH_LONG
     */
    public void setDuration(int duration) {
        mDuration = duration;
    }

    /**
     * Return the duration.
     * @see #setDuration
     */
    public int getDuration() {
        return mDuration;
    }
    
    /**
     * Set the margins of the view.
     *
     * @param horizontalMargin The horizontal margin, in percentage of the
     *        container width, between the container's edges and the
     *        notification
     * @param verticalMargin The vertical margin, in percentage of the
     *        container height, between the container's edges and the
     *        notification
     */
    public void setMargin(float horizontalMargin, float verticalMargin) {
        mHorizontalMargin = horizontalMargin;
        mVerticalMargin = verticalMargin;
    }

    /**
     * Return the horizontal margin.
     */
    public float getHorizontalMargin() {
        return mHorizontalMargin;
    }

    /**
     * Return the vertical margin.
     */
    public float getVerticalMargin() {
        return mVerticalMargin;
    }

    /**
     * Set the location at which the notification should appear on the screen.
     * @see android.view.Gravity
     * @see #getGravity
     */
    public void setGravity(int gravity, int xOffset, int yOffset) {
        mGravity = gravity;
        mX = xOffset;
        mY = yOffset;
    }

     /**
     * Get the location at which the notification should appear on the screen.
     * @see android.view.Gravity
     * @see #getGravity
     */
    public int getGravity() {
        return mGravity;
    }

    /**
     * Return the X offset in pixels to apply to the gravity's location.
     */
    public int getXOffset() {
        return mX;
    }
    
    /**
     * Return the Y offset in pixels to apply to the gravity's location.
     */
    public int getYOffset() {
        return mY;
    }
	
    /**
     * schedule handleShow into the right thread
     */
    public void show() {
    	
        mHandler.post(mShow);
        if(mDuration>0)
        {
        	 mHandler.postDelayed(mHide, mDuration);
        }
        
    }

    /**
     * schedule handleHide into the right thread
     */
    public void hide() {
        mHandler.post(mHide);  
        
    }
    
    private final Runnable mShow = new Runnable() {
        public void run() {
            handleShow();
        }
    };

    private final Runnable mHide = new Runnable() {
        public void run() {
            handleHide();
        }
    };

    private void init(Context context)
    {
    	
    	 final WindowManager.LayoutParams params = mParams;
         params.height = WindowManager.LayoutParams.WRAP_CONTENT;
         params.width = WindowManager.LayoutParams.WRAP_CONTENT;
         params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                 | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                 | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
         params.format = PixelFormat.TRANSLUCENT;
         params.windowAnimations = android.R.style.Animation_Toast;
         params.type = WindowManager.LayoutParams.TYPE_TOAST;
        
         
         mWM = (WindowManager) context.getApplicationContext()
                 .getSystemService(Context.WINDOW_SERVICE);
    }
    
    
    private void handleShow() {

    	
        if (mView != mNextView) {
            // remove the old view if necessary
            handleHide();
            mView = mNextView;
//            mWM = WindowManagerImpl.getDefault();
            final int gravity = mGravity;
            mParams.gravity = gravity;
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) 
            {
                mParams.horizontalWeight = 1.0f;
            }
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) 
            {
                mParams.verticalWeight = 1.0f;
            }
            mParams.x = mX;
            mParams.y = mY;
            mParams.verticalMargin = mVerticalMargin;
            mParams.horizontalMargin = mHorizontalMargin;
            if (mView.getParent() != null) 
            {
                mWM.removeView(mView);
            }
            
            
            
            mWM.addView(mView, mParams);
           
        }
    }

    private void handleHide() 
    {
        if (mView != null) 
        {
            if (mView.getParent() != null) 
            {
                mWM.removeView(mView);
            }
            mView = null;
            
            //回调
            if(myCallback!=null)
            {
            	myCallback.callback();
            }
        }
    }
    
    public void setMyCallback(MyCallback myCallback) {  
        this.myCallback = myCallback;  
    }
}

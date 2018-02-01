package com.cstz.front.viewpager;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cstz.common.AsyncImageLoader;
import com.cstz.cstz_android.R;
import com.cstz.tools.StatusBarUtil;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;
/**
 * 证件材料图片轮播放大缩小的实现窗体
 */

public class ViewPager extends Activity  {
	private HackyViewPager mViewPager;
	private List<String> pathList,titleList;
	private int position;
	private ImageView imageView_back;
	private TextView textView_start;
	private TextView textView_sum;
	private TextView title_tv;
	//	private ImageDownloader imageDownloadHelper;
//	public ImageLoader imageLoader = ImageLoader.getInstance();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.activity_view_pager_two);
		setTransparentStatusBar(this,false);
//        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        
        Intent intent = getIntent();
        
        if(intent!=null) {
        	pathList= intent.getExtras().getStringArrayList("pathList");
        	titleList= intent.getExtras().getStringArrayList("titleList");
        	position = intent.getExtras().getInt("position");
        }
        
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
		title_tv = (TextView) findViewById(R.id.title_tv);
  	  	title_tv.setText(titleList.get(position));
        imageView_back = (ImageView) findViewById(R.id.title_back);
        imageView_back.setVisibility(View.VISIBLE);
        imageView_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ViewPager.this.finish();
			}

		});
        textView_start = (TextView) findViewById(R.id.textView_start);
        textView_sum = (TextView) findViewById(R.id.textView_sum);
		mViewPager.setAdapter(new SamplePagerAdapter());
		mViewPager.setCurrentItem(position);
		mViewPager.setOffscreenPageLimit(pathList.size());
		textView_start.setText(position+1+"");
		textView_sum.setText(pathList.size()+"");
		
		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				textView_start.setText(arg0+1+"");
				title_tv.setText(titleList.get(arg0));
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});

	}
	/*设置6.0以上系统，沉浸式状态栏*/
	public void setTransparentStatusBar(Activity activity,boolean cg){
		if(Build.VERSION.SDK_INT > 22){
			RelativeLayout toolbar = (RelativeLayout) activity.findViewById(R.id.layout_title);
			if(toolbar!=null){
				ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
				lp.height = getResources().getDimensionPixelOffset(R.dimen.dimen_42);
				toolbar.setLayoutParams(lp);
			}
			StatusBarUtil.setColor(this, getResources().getColor(!cg?R.color.window_background:R.color.navigation_cg), 0);
		}
	}
    
    public void clickbutton(View view){
    	switch (view.getId()) {

			default:
				break;
		}
    }

	 class SamplePagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return pathList.size();
		}

		ImageView photoView;
		@Override
		public View instantiateItem(ViewGroup container, int position) {
			photoView = new ImageView(container.getContext());
			AsyncImageLoader loader = new AsyncImageLoader(ViewPager.this);//getApplicationContext()
			loader.setCache2File(false);
			String url = pathList.get(position);
			if(pathList.get(position).contains("http://www.wuxingjinrong.com/")){
				url = url.replaceAll("http", "https");
			}
			loader.downloadImage(url, false, new AsyncImageLoader.ImageCallback() {

				@Override
				public void onImageLoaded(Bitmap bitmap, String imageUrl) {
					if(bitmap!=null){
						photoView.setImageBitmap(bitmap);
					}else{
				    	photoView.setImageDrawable(getResources().getDrawable(R.drawable.icon_stub));
					}
				}
			});
			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			if(photoView != null)
				new PhotoViewAttacher(photoView);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}
}
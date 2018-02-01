package com.cstz.common;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;
  
/**
 * 
* 类名称：SysApplication   
* 类描述：   退出整个应用程序的类
* 创建时间：2014-4-25 下午1:58:00   
* 修改备注：   
* @version    
*
 */
 
public class SysApplication extends Application {
     
    private List<Activity> mList = new LinkedList<Activity>();

    private static SysApplication instance;

    private SysApplication() {}

    public synchronized static SysApplication getInstance() {

        if (null == instance) { 
            instance = new SysApplication();
        } 
        return instance; 
    }

    // add Activity  
    public void addActivity(Activity activity) { 
    	
    	if(!mList.contains(activity))
    	{
    		mList.add(activity); 
    	}
    }

    public List<Activity> getmList() {
		return mList;
	}
    public void exit() { 
        try { 
            for (Activity activity : mList) { 
                if (activity != null) 
                 activity.finish(); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        } 
    } 
    
    public void finishAllActivity(){
    	try { 
            for (Activity activity : mList) { 
                if (activity != null) 
                 activity.finish(); 
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
    
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
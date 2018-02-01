package com.cstz.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Device {

	public static int getConnectedType(Context context) { 
	
		//ConnectivityManager.TYPE_MOBILE
		if (context != null)
		{ 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
					.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) { 
				return mNetworkInfo.getType(); 
				} 
			} 
			return -1; 
		} 
}

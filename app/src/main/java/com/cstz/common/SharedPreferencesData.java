package com.cstz.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.cstz.model.User;
import com.cstz.tools.DesSecurityUtil;

public class SharedPreferencesData {

	private Context context;
	
	public SharedPreferencesData(Context context)
	{
		this.context = context;
	}
	
	public void setUserInfo(User user)
	{
		SharedPreferences shared = this.context.getSharedPreferences("user",0);
		SharedPreferences.Editor editor = shared.edit();
		try
		{
			DesSecurityUtil des = new DesSecurityUtil();
			if(user.getPhone()!=null&& !TextUtils.isEmpty(user.getPhone()))
				editor.putString("phone", des.encrypt(user.getPhone()));
			if(user.getUserName()!=null&& !TextUtils.isEmpty(user.getUserName()))
				editor.putString("userName", des.encrypt(user.getUserName()));
			if(user.getNickName()!=null&& !TextUtils.isEmpty(user.getNickName()))
				editor.putString("nickName", des.encrypt(user.getNickName()));
			if(user.getSex()!=null&& !TextUtils.isEmpty(user.getSex()))
				editor.putString("sex", des.encrypt(user.getSex()));
			if(user.getRealName()!=null && !TextUtils.isEmpty(user.getRealName()))
				editor.putString("realName", des.encrypt(user.getRealName()));
			if(user.getIdNo()!=null && !TextUtils.isEmpty(user.getIdNo()))
				editor.putString("idNo", des.encrypt(user.getIdNo()));
			if(user.getRealNameStatus()!=null && !TextUtils.isEmpty(user.getRealNameStatus()))
				editor.putString("realNameAuditStatus", des.encrypt(user.getRealNameStatus()));
			if(user.getHasSignIn()!=null && !TextUtils.isEmpty(user.getHasSignIn()))
				editor.putString("hasSignIn", des.encrypt(user.getHasSignIn()));
			if(user.getUsableCount()!=null && !TextUtils.isEmpty(user.getUsableCount()))
				editor.putString("availableBalance",des.encrypt(user.getUsableCount()));
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		editor.commit();
	}
	
	public void setValue(String key,String value)
	{
		SharedPreferences shared = this.context.getSharedPreferences("user",0);
		SharedPreferences.Editor editor = shared.edit();
		try {
			editor.putString(key, new DesSecurityUtil().encrypt(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
		editor.commit();
	}
	
	public String getValue(String key)
	{
		SharedPreferences shared = this.context.getSharedPreferences("user",0);
		try {
			return new DesSecurityUtil().decrypt(shared.getString(key, ""));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public boolean getBooleanValue(String key){
		SharedPreferences shared = this.context.getSharedPreferences("app",0);
		try {
			return shared.getBoolean(key, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setBooleanValue(String key,boolean value){
		SharedPreferences shared = this.context.getSharedPreferences("app",0);
		SharedPreferences.Editor editor = shared.edit();
		try {
			editor.putBoolean(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		editor.commit();
	}
	
	public boolean getBoolean(String key){
		SharedPreferences shared = this.context.getSharedPreferences("user",0);
		try {
			return shared.getBoolean(key, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setBoolean(String key,boolean value){
		SharedPreferences shared = this.context.getSharedPreferences("user",0);
		SharedPreferences.Editor editor = shared.edit();
		try {
			editor.putBoolean(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		editor.commit();
	}
	
	public void setOtherValue(String key,String value)
	{
		SharedPreferences shared = this.context.getSharedPreferences("other",0);
		SharedPreferences.Editor editor = shared.edit();
		try {
			editor.putString(key, new DesSecurityUtil().encrypt(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
//		editor.putString(key, value);
		editor.commit();
	}
	
	public String getOtherValue(String key)
	{
		SharedPreferences shared = this.context.getSharedPreferences("other",0);
//		return shared.getString(key, "");
		try {
			return new DesSecurityUtil().decrypt(shared.getString(key, ""));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public void setOtherIntValue(String key,int value)
	{
		SharedPreferences shared = this.context.getSharedPreferences("other1",0);
		SharedPreferences.Editor editor = shared.edit();
		try {
			editor.putInt(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		editor.commit();
	}
	
	public int getOtherIntValue(String key)
	{
		SharedPreferences shared = this.context.getSharedPreferences("other1",0);
		try {
			return shared.getInt(key, 1);
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	public void setOtherIntValueByUser(String username, String key,int value)
	{
		SharedPreferences shared = this.context.getSharedPreferences(username,0);
		SharedPreferences.Editor editor = shared.edit();
		try {
			editor.putInt(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		editor.commit();
	}

	public int getOtherIntValueByUser(String username, String key)
	{
		SharedPreferences shared = this.context.getSharedPreferences(username,0);
		try {
			return shared.getInt(key, 0);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public String getOtherValueSecurity(String key){
		SharedPreferences shared = this.context.getSharedPreferences("other",0);
		return shared.getString(key, "");
	}
	
	public void setValueByUser(String username, String key, String value){
		SharedPreferences shared = this.context.getSharedPreferences(username,0);
		SharedPreferences.Editor editor = shared.edit();
		try {
			editor.putString(key, new DesSecurityUtil().encrypt(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
		editor.commit();
	}
	
	public String getValueByUser(String username, String key)
	{
		SharedPreferences shared = this.context.getSharedPreferences(username,0);
		try {
			return new DesSecurityUtil().decrypt(shared.getString(key, ""));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public boolean getBooleanByUser(String username, String key){
		SharedPreferences shared = this.context.getSharedPreferences(username,0);
		try {
			return shared.getBoolean(key, false);//默认值改为false
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setBooleanByUser(String username, String key,boolean value){
		SharedPreferences shared = this.context.getSharedPreferences(username,0);
		SharedPreferences.Editor editor = shared.edit();
		try {
			editor.putBoolean(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		editor.commit();
	}

	public boolean getOtherBoolean(String key){
		SharedPreferences shared = this.context.getSharedPreferences("other",0);
		try {
			return shared.getBoolean(key, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setOtherBoolean(String key,boolean value){
		SharedPreferences shared = this.context.getSharedPreferences("other",0);
		SharedPreferences.Editor editor = shared.edit();
		try {
			editor.putBoolean(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		editor.commit();
	}
	
	public void removeAll()
	{
		SharedPreferences shared = this.context.getSharedPreferences("user",0);
		SharedPreferences.Editor editor = shared.edit();
		editor.clear();
		editor.commit();
	}
	
	public void removeOtherAll()
	{
		SharedPreferences shared = this.context.getSharedPreferences("other",0);
		SharedPreferences.Editor editor = shared.edit();
		editor.clear();
		editor.commit();
	}
}

package com.cstz.tools;


public class Filter {


	static public String phoneFilter(String phone)
	{
		String str = "";
		if(phone.length()>4)
		{
			str = phone.substring(0,3)+"****"+phone.substring(phone.length()-4);
		}
		
		return str;
	}
	static public String filterRealName(String realName)
	{
		String str = "";
		int length = realName.length();
		if(length>0 && length<3)
		{
			str = realName.substring(0, 1)+"*";
		}else if(length>0 && length>=3)
		{
			str = realName.substring(0, 1)+getFilterCenter(length-2)+realName.substring(length-1);
		}
		return str;
	}
	
	static public String filterIdCardNo(String idCardNo)
	{
		String str = "";
		if(idCardNo.length()>4)
		{
			str = idCardNo.substring(idCardNo.length() - 4,idCardNo.length());
		}
		
		return str;
	}
	
	static String getFilterCenter(int length)
	{
		String str = "";
		for(int i=0;i<length;i++)
		{
			str+="*";
		}
		return str;
	}
}

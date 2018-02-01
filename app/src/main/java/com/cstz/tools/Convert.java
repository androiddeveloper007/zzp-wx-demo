package com.cstz.tools;

public class Convert {

	static public String trim(String str) {
		if (str != null) {
			return str.trim();
		}
		return "";
	}

	static public float toFloat(String str) {
		if (str != null) {
			try {
				if (str.indexOf(".") > -1) {
					str = str.substring(0, str.indexOf("."));
				}
				return Float.valueOf(str);

			} catch (Exception ex) {
				return 0.0f;
			}
		}
		return 0.0f;
	}

	static public int toInt(String str) {
		if (str != null) {
			try {
				if (str.indexOf(".") > -1) {
					str = str.substring(0, str.indexOf("."));
				}
				Integer integer;
				integer = Integer.valueOf(str);
				return integer.intValue();
			} catch (Exception ex) {
				return 0;
			}
		}
		return 0;
	}
	public static int strToInt(String str, int defaultValue)
	  {
//	    defaultValue = defaultValue;
	    try
	    {
	      defaultValue = Integer.parseInt(str);
	    }
	    catch (Exception localException)
	    {
	    }
	    return defaultValue;
	  }
	public static float strToFloat(String str, float defaultValue) {
//		defaultValue = defaultValue;
		try {
			defaultValue = Float.parseFloat(str);
		} catch (Exception localException) {
		}
		return defaultValue;
	}
	public static long strToLong(String str, long defaultValue)
	  {
	    long l = defaultValue;
	    try
	    {
	      l = Long.parseLong(str);
	    }
	    catch (Exception localException)
	    {
	    }
	    return l;
	  }
	public static String strToStr(String str, String defaultValue)
	  {
//	    defaultValue = defaultValue;
	    if ((str != null) && (!str.isEmpty()))
	      defaultValue = str;
	    return defaultValue;
	  }
}

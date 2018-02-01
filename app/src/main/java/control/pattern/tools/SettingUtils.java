package control.pattern.tools;

import android.content.Context;

import com.cstz.common.SharedPreferencesData;

/**
 * 本地所有需要缓存的数据都通过此类的方法来统一控制
 * @author Administrator
 *
 */
public class SettingUtils {
	
	public static class SettingItems {
		public static final String IS_PATTERN_PWD_NO = "is_pattern_pwd_no";
        public static final String IS_FINGER_PWD = "is_finger_pwd";
	}

    private static final String SETTING_NAME = "setting_name";

    private PreferenceUtils preferenceUtils;

    private SharedPreferencesData sp;//根据用户手机号存手势密码开启状态

   private static SettingUtils instance;

    public static SettingUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SettingUtils();
//            instance.preferenceUtils = PreferenceUtils.getInstance(context, SETTING_NAME);
            instance.sp = new SharedPreferencesData(context);
        }
        return  instance;
    }

    private SettingUtils() {
    }

	public boolean isPatternPwdOn(String username) {
//		return preferenceUtils.getBoolean(SettingItems.IS_PATTERN_PWD_NO, false);
		return sp.getBooleanByUser(username, SettingItems.IS_PATTERN_PWD_NO);
	}

    // 是否开启了图形密码
	public  void putIsPatternPwdOn(String username,boolean isNo) {
//		preferenceUtils.putBoolean(SettingItems.IS_PATTERN_PWD_NO, isNo);
		sp.setBooleanByUser(username, SettingItems.IS_PATTERN_PWD_NO, isNo);
	}

    public boolean isFingerPwdOn(String username) {
        return sp.getBooleanByUser(username, SettingItems.IS_FINGER_PWD);
    }

    // 是否开启了指纹密码
    public  void putIsFingerPwdOn(String username,boolean isNo) {
        sp.setBooleanByUser(username, SettingItems.IS_FINGER_PWD, isNo);
    }
}

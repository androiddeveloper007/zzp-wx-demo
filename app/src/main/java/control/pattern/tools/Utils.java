package control.pattern.tools;

import android.text.TextUtils;

/**
 * Created by xiaofanqing on 15/6/8.
 */
public class Utils {

    /**
     * 加密显示手机号码
     * @param number
     * @return
     */
    public static String formatPhoneNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return null;
        }

        int nameLength = number.length();
        if(nameLength < 11){    //当number为用户名
            return number;
        }else{
            StringBuffer buffer = new StringBuffer();
            buffer.append(number.substring(0, 3));
            buffer.append("****");

            buffer.append(number.substring(nameLength - 4, nameLength));
            number = buffer.toString();
            return number;
        }
    }
}

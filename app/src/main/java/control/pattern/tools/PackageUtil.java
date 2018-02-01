package control.pattern.tools;

import android.content.Context;

public class PackageUtil {
	public PackageUtil() {
    }

    public static String getVersion(Context var0) {
        try {
            return var0.getPackageManager().getPackageInfo(var0.getPackageName(), 0).versionName;
        } catch (Exception var2) {
            var2.printStackTrace();
            return "unknown version";
        }
    }
}

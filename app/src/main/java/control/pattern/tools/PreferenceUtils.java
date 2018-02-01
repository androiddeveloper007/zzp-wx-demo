package control.pattern.tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class PreferenceUtils {
	private String a;
    private Map b = new ConcurrentHashMap();
    private Context c;
    private static Map d;
    private SharedPreferences e;

    public static PreferenceUtils getInstance(Context var0, String var1) {
        PreferenceUtils var2;
        if((var2 = (PreferenceUtils)d.get(var1)) == null) {
            Class var3 = PreferenceUtils.class;
            synchronized(PreferenceUtils.class) {
                if(var2 == null) {
                    var2 = new PreferenceUtils(var0.getApplicationContext(), var1);
                    d.put(var1, var2);
                }
            }
        }

        return var2;
    }

    private PreferenceUtils(Context var1, String var2) {
        this.c = var1;
        this.a = var2;
    }

    public boolean putString(String var1, String var2) {
        if(this.a().putString(var1, var2).commit()) {
            this.b.put(var1, var2);
            return true;
        } else {
            return false;
        }
    }

    public String getString(String var1) {
        String var2;
        if(!TextUtils.isEmpty(var2 = (String)this.b.get(var1))) {
            return var2;
        } else {
            if(!TextUtils.isEmpty(var2 = this.b().getString(var1, (String)null))) {
                this.b.put(var1, var2);
            }

            return var2;
        }
    }

    public boolean putInt(String var1, int var2) {
        if(this.a().putInt(var1, var2).commit()) {
            this.b.put(var1, Integer.valueOf(var2));
            return true;
        } else {
            return false;
        }
    }

    public int getInt(String var1) {
        return this.getInt(var1, 0);
    }

    public int getInt(String var1, int var2) {
        int var5;
        try {
            Object var3;
            if((var3 = this.b.get(var1)) != null && (var5 = ((Integer)var3).intValue()) != 0) {
                return var5;
            }
        } catch (Exception var4) {
            ;
        }

        if((var5 = this.b().getInt(var1, var2)) != 0) {
            this.b.put(var1, Integer.valueOf(var5));
        }

        return var5;
    }

    public void putLong(String var1, long var2) {
        this.b.put(var1, Long.valueOf(var2));
        this.a().putLong(var1, var2).commit();
    }

    public long getLong(String var1) {
        return this.getLong(var1, 0L);
    }

    public long getLong(String var1, long var2) {
        long var7;
        try {
            Object var4;
            if((var4 = this.b.get(var1)) != null && (var7 = ((Long)var4).longValue()) != 0L) {
                return var7;
            }
        } catch (Exception var6) {
            ;
        }

        if((var7 = this.b().getLong(var1, var2)) != 0L) {
            this.b.put(var1, Long.valueOf(var7));
        }

        return var7;
    }

    public void putFloat(String var1, float var2) {
        this.b.put(var1, Float.valueOf(var2));
        this.a().putFloat(var1, var2).commit();
    }

    public float getFloat(String var1) {
        return this.getFloat(var1, 0.0F);
    }

    public float getFloat(String var1, float var2) {
        float var5;
        try {
            Object var3;
            if((var3 = this.b.get(var1)) != null && (var5 = ((Float)var3).floatValue()) != 0.0F) {
                return var5;
            }
        } catch (Exception var4) {
            ;
        }

        if((var5 = this.b().getFloat(var1, var2)) != 0.0F) {
            this.b.put(var1, Float.valueOf(var5));
        }

        return var5;
    }

    public void putBoolean(String var1, boolean var2) {
        this.b.put(var1, Boolean.valueOf(var2));
        this.a().putBoolean(var1, var2).commit();
    }

    public boolean getBoolean(String var1) {
        return this.getBoolean(var1, false);
    }

    public boolean getBoolean(String var1, boolean var2) {
        try {
            Object var3;
            if((var3 = this.b.get(var1)) != null) {
                ((Boolean)var3).booleanValue();
            }
        } catch (Exception var4) {
            ;
        }

        var2 = this.b().getBoolean(var1, var2);
        this.b.put(var1, Boolean.valueOf(var2));
        return var2;
    }

    public boolean remove(String var1) {
        this.b.clear();
        return this.a().remove(var1).commit();
    }

    public boolean clear() {
        this.b.clear();
        return this.a().clear().commit();
    }

    private Editor a() {
        return this.b().edit();
    }

    private SharedPreferences b() {
        Context var1 = this.c;
        if(TextUtils.isEmpty(this.a)) {
            this.a = var1.getPackageName();
        }

        if(this.e == null) {
            this.e = var1.getSharedPreferences(this.a, 0);
        }

        return this.e;
    }

    static {
        PreferenceUtils.class.getSimpleName();
        d = new ConcurrentHashMap();
    }
}

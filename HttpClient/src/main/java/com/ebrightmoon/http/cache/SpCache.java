package com.ebrightmoon.http.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.ebrightmoon.http.common.AppConfig;
import com.ebrightmoon.http.util.BASE64;
import com.ebrightmoon.http.util.BytesUtils;
import com.ebrightmoon.http.util.HexUtil;

/**
 * @Description: SharedPreferences存储，支持对象加密存储
 */
public class SpCache implements ICache {
    private SharedPreferences sp;

    public SpCache(Context context) {
        this(context, AppConfig.CACHE_SP_NAME);
    }

    public SpCache(Context context, String fileName) {
        sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSp() {
        return sp;
    }

    @Override
    public void put(String key, Object ser) {
        try {
            if (ser == null) {
                sp.edit().remove(key).apply();
            } else {
                byte[] bytes = BytesUtils.objectToByte(ser);
                bytes = BASE64.encode(bytes);
                put(key, HexUtil.encodeHexStr(bytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object get(String key) {
        try {
            String hex = get(key, null);
            if (hex == null) return null;
            byte[] bytes = HexUtil.decodeHex(hex.toCharArray());
            bytes = BASE64.decode(bytes);
            Object obj = BytesUtils.byteToObject(bytes);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean contains(String key) {
        return sp.contains(key);
    }

    @Override
    public void remove(String key) {
        sp.edit().remove(key).apply();
    }

    @Override
    public void clear() {
        sp.edit().clear().apply();
    }

    public void put(String key, String value) {
        if (value == null) {
            sp.edit().remove(key).apply();
        } else {
            sp.edit().putString(key, value).apply();
        }
    }

    public void put(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public void put(String key, float value) {
        sp.edit().putFloat(key, value).apply();
    }

    public void put(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public String get(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public boolean get(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public float get(String key, float defValue) {
        return sp.getFloat(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public long get(String key, long defValue) {
        return sp.getLong(key, defValue);
    }
}

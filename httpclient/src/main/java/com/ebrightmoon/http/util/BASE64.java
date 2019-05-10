package com.ebrightmoon.http.util;

import android.util.Base64;

/**
 * 作者：create by  Administrator on 2019/1/17
 * 邮箱：2315813288@qq.com
 */
public class BASE64 {

    public static byte[] encode(byte[] plain) {
        return Base64.encode(plain, Base64.DEFAULT);
    }

    public static String encodeToString(byte[] plain) {
        return Base64.encodeToString(plain, Base64.DEFAULT);
    }

    public static byte[] decode(String text) {
        return Base64.decode(text, Base64.DEFAULT);
    }

    public static byte[] decode(byte[] text) {
        return Base64.decode(text, Base64.DEFAULT);
    }
}

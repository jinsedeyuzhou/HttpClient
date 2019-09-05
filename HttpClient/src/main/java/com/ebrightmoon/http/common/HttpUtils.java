package com.ebrightmoon.http.common;




import com.ebrightmoon.http.util.MD5;

import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wyy on 2018/1/29.
 */

public class HttpUtils {


    private HttpUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");

    }


    /**
     * 判断是否是汉字
     */
    /**
     * 是否是中文
     *
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[u4e00-u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 拼接参数
     *
     * @param hashmap
     * @return
     * @throws UnsupportedEncodingException
     */
    public static StringBuffer spellParams(Map<String, String> hashmap) {
        List<BasicNameValuePair> array = new ArrayList();
        Object[] key = hashmap.keySet().toArray();
        Arrays.sort(key);
        for (int i = 0; i < key.length; i++) {
            if (isContainChinese(hashmap.get(key[i]))) {
                String encode = null;
                try {
                    encode = URLEncoder.encode(hashmap.get(key[i]), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                array.add(new BasicNameValuePair(key[i] + "", encode + ""));
            } else {
                array.add(new BasicNameValuePair(key[i] + "", hashmap.get(key[i]) + ""));
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.size(); i++) {
            if ((array.size() - 1) == i) {
                sb.append(array.get(i).toString());
            } else {
                sb.append(array.get(i).toString() + "&");
            }
        }
        return sb;
    }


    /**
     * 将输入流写入文件
     *
     * @param inputString
     * @param filePath
     */
    public static String writeFile(InputStream inputString, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            byte[] b = new byte[1024];

            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            inputString.close();
            fos.close();
            return "下载成功";
        } catch (FileNotFoundException e) {
            return e.toString();
        } catch (IOException e) {
            return e.toString();
        }
    }



    /**
     * 获取第一级type
     *
     * @param t
     * @param <T>
     * @return
     */
    public static  <T> Type getType(T t) {
        Type genType = t.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            finalNeedType = type;
        }
        return finalNeedType;
    }

    /**
     * 获取次一级type(如果有)
     *
     * @param t
     * @param <T>
     * @return
     */
    public static  <T> Type getSubType(T t) {
        Type genType = t.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        Type finalNeedType;
        if (params.length > 1) {
            if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
            finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            if (type instanceof ParameterizedType) {
                finalNeedType = ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                finalNeedType = type;
            }
        }
        return finalNeedType;
    }


}



package com.ebrightmoon.demo.http;

/**
 * Time: 2019-09-18
 * Author:wyy
 * Description:
 */
public class HttpManager {

    private static IHttp innerHttp;
    private static IHttp externalHttp;

    public static void setHttp(IHttp Http) {
        if (externalHttp == null && Http != null) {
            externalHttp = Http;
        }
    }

    public static IHttp getHttp() {
        if (innerHttp == null) {
            synchronized (HttpManager.class) {
                if (innerHttp == null) {
                    if (externalHttp != null) {
                        innerHttp = externalHttp;
                    } else {
                        innerHttp = new RestofitHttp();
                    }
                }
            }
        }
        return innerHttp;
    }
}

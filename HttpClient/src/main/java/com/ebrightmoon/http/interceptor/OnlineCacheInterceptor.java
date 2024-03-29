package com.ebrightmoon.http.interceptor;

import androidx.annotation.NonNull;
import android.text.TextUtils;


import com.ebrightmoon.http.common.AppConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 *  在线缓存拦截 无论有无网络我们都先获取缓存的数据。仅仅针对get请求，post请求不支持
 */
public class OnlineCacheInterceptor implements Interceptor {
    private String cacheControlValue;

    public OnlineCacheInterceptor() {
        this(AppConfig.MAX_AGE_ONLINE);
    }

    public OnlineCacheInterceptor(int cacheControlValue) {
        this.cacheControlValue = String.format("max-age=%d", cacheControlValue);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        String cacheControl = originalResponse.header("Cache-Control");
        if (TextUtils.isEmpty(cacheControl) || cacheControl.contains("no-store") || cacheControl.contains("no-cache") || cacheControl
                .contains("must-revalidate") || cacheControl.contains("max-age") || cacheControl.contains("max-stale")) {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, " + cacheControlValue)
                    .removeHeader("Pragma")
                    .build();
        } else {
            return originalResponse;
        }
    }
}

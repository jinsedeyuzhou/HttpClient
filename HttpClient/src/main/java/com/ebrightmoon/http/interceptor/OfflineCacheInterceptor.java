package com.ebrightmoon.http.interceptor;

import android.content.Context;
import androidx.annotation.NonNull;


import com.ebrightmoon.http.common.AppConfig;
import com.ebrightmoon.http.util.Network;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *  离线缓存拦截 有网络获取网络或混存数据，无网络获取缓存数据 仅仅针对get请求，post请求不支持，post仅支持
 *  本地缓存
 */
public class OfflineCacheInterceptor implements Interceptor {
    private Context context;
    private String cacheControlValue;

    public OfflineCacheInterceptor(Context context) {
        this(context, AppConfig.MAX_AGE_OFFLINE);
    }

    public OfflineCacheInterceptor(Context context, int cacheControlValue) {
        this.context = context;
        this.cacheControlValue = String.format("max-stale=%d", cacheControlValue);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (!Network.isConnected(context)) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, " + cacheControlValue)
                    .removeHeader("Pragma")
                    .build();
        }
        return chain.proceed(request);
    }
}

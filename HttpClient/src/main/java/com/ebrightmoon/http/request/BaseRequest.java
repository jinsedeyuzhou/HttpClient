package com.ebrightmoon.http.request;


import android.util.Log;

import com.ebrightmoon.http.callback.UCallback;
import com.ebrightmoon.http.common.AppConfig;
import com.ebrightmoon.http.common.GlobalParams;
import com.ebrightmoon.http.core.ApiCookie;
import com.ebrightmoon.http.interceptor.HeadersInterceptor;
import com.ebrightmoon.http.interceptor.UploadProgressInterceptor;
import com.ebrightmoon.http.mode.ApiHost;
import com.ebrightmoon.http.mode.HttpHeaders;
import com.ebrightmoon.http.restrofit.HttpClient;
import com.ebrightmoon.http.util.SSL;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  请求基类
 * @author:
 * @date: 2017-04-28 16:05
 */
public abstract class BaseRequest<R extends BaseRequest> {
    protected GlobalParams httpGlobalConfig;//全局配置
    protected Retrofit retrofit;//Retrofit对象
    protected List<Interceptor> interceptors = new ArrayList<>();//局部请求的拦截器
    protected List<Interceptor> networkInterceptors = new ArrayList<>();//局部请求的网络拦截器
    protected HttpHeaders headers = new HttpHeaders();//请求头
    protected String baseUrl;//基础域名
    protected Object tag;//请求标签
    protected long readTimeOut;//读取超时时间
    protected long writeTimeOut;//写入超时时间
    protected long connectTimeOut;//连接超时时间
    protected boolean isHttpCache;//是否使用Http缓存
    protected UCallback uploadCallback;//上传进度回调

    /**
     * 设置基础域名，当前请求会替换全局域名
     *
     * @param baseUrl
     * @return
     */
    public R baseUrl(String baseUrl) {
        if (baseUrl != null) {
            this.baseUrl = baseUrl;
        }
        return (R) this;
    }

    /**
     * 添加请求头
     *
     * @param headerKey
     * @param headerValue
     * @return
     */
    public R addHeader(String headerKey, String headerValue) {
        this.headers.put(headerKey, headerValue);
        return (R) this;
    }

    /**
     * 添加请求头
     *
     * @param headers
     * @return
     */
    public R addHeaders(Map<String, String> headers) {
        this.headers.put(headers);
        return (R) this;
    }

    /**
     * 移除请求头
     *
     * @param headerKey
     * @return
     */
    public R removeHeader(String headerKey) {
        this.headers.remove(headerKey);
        return (R) this;
    }

    /**
     * 设置请求头
     *
     * @param headers
     * @return
     */
    public R headers(HttpHeaders headers) {
        if (headers != null) {
            this.headers = headers;
        }
        return (R) this;
    }

    /**
     * 设置请求标签
     *
     * @param tag
     * @return
     */
    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    /**
     * 设置连接超时时间（秒）
     *
     * @param connectTimeOut
     * @return
     */
    public R connectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
        return (R) this;
    }

    /**
     * 设置读取超时时间（秒）
     *
     * @param readTimeOut
     * @return
     */
    public R readTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
        return (R) this;
    }

    /**
     * 设置写入超时时间（秒）
     *
     * @param writeTimeOut
     * @return
     */
    public R writeTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return (R) this;
    }

    /**
     * 设置是否进行HTTP缓存
     *
     * @param isHttpCache
     * @return
     */
    public R setHttpCache(boolean isHttpCache) {
        this.isHttpCache = isHttpCache;
        return (R) this;
    }

    /**
     * 局部设置拦截器
     *
     * @param interceptor
     * @return
     */
    public R interceptor(Interceptor interceptor) {
        if (interceptor != null) {
            interceptors.add(interceptor);
        }
        return (R) this;
    }

    /**
     * 局部设置网络拦截器
     *
     * @param interceptor
     * @return
     */
    public R networkInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            networkInterceptors.add(interceptor);
        }
        return (R) this;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public long getReadTimeOut() {
        return readTimeOut;
    }

    public long getWriteTimeOut() {
        return writeTimeOut;
    }

    public long getConnectTimeOut() {
        return connectTimeOut;
    }

    public boolean isHttpCache() {
        return isHttpCache;
    }

    /**
     * 生成局部配置
     */
    protected void generateLocalConfig() {
        OkHttpClient.Builder newBuilder = HttpClient.getOkHttpClient().newBuilder();

        if (httpGlobalConfig.getGlobalHeaders() != null) {
            headers.put(httpGlobalConfig.getGlobalHeaders());
        }

        if (!interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                newBuilder.addInterceptor(interceptor);
            }
        }

        if (!networkInterceptors.isEmpty()) {
            for (Interceptor interceptor : networkInterceptors) {
                newBuilder.addNetworkInterceptor(interceptor);
            }
        }

        if (headers.headersMap.size() > 0) {
            newBuilder.addInterceptor(new HeadersInterceptor(headers.headersMap));
        }

        if (uploadCallback != null) {
            newBuilder.addNetworkInterceptor(new UploadProgressInterceptor(uploadCallback));
        }

        if (readTimeOut > 0) {
            newBuilder.readTimeout(readTimeOut, TimeUnit.SECONDS);
        }

        if (writeTimeOut > 0) {
            newBuilder.readTimeout(writeTimeOut, TimeUnit.SECONDS);
        }

        if (connectTimeOut > 0) {
            newBuilder.readTimeout(connectTimeOut, TimeUnit.SECONDS);
        }

        if (isHttpCache) {
            try {
                if (httpGlobalConfig.getHttpCache() == null) {
                    httpGlobalConfig.httpCache(new Cache(httpGlobalConfig.getHttpCacheDirectory(), AppConfig.CACHE_MAX_SIZE));
                }
                httpGlobalConfig.cacheOnline(httpGlobalConfig.getHttpCache());
                httpGlobalConfig.cacheOffline(httpGlobalConfig.getHttpCache());
            } catch (Exception e) {
                Log.e("","Could not create http cache" + e);
            }
            newBuilder.cache(httpGlobalConfig.getHttpCache());
        }

        if (baseUrl != null) {
            Retrofit.Builder newRetrofitBuilder = new Retrofit.Builder();
            newRetrofitBuilder.baseUrl(baseUrl);
            if (httpGlobalConfig.getConverterFactory() != null) {
                newRetrofitBuilder.addConverterFactory(httpGlobalConfig.getConverterFactory());
            }
            if (httpGlobalConfig.getCallAdapterFactory() != null) {
                newRetrofitBuilder.addCallAdapterFactory(httpGlobalConfig.getCallAdapterFactory());
            }
            if (httpGlobalConfig.getCallFactory() != null) {
                newRetrofitBuilder.callFactory(httpGlobalConfig.getCallFactory());
            }
            newBuilder.hostnameVerifier(new SSL.UnSafeHostnameVerifier(baseUrl));
            newRetrofitBuilder.client(newBuilder.build());
            retrofit = newRetrofitBuilder.build();
        } else {
            HttpClient.getRetrofitBuilder().client(newBuilder.build());
            retrofit = HttpClient.getRetrofitBuilder().build();
        }
    }

    /**
     * 生成全局配置
     */
    protected void generateGlobalConfig() {
        httpGlobalConfig = HttpClient.config();

        if (httpGlobalConfig.getBaseUrl() == null) {
            httpGlobalConfig.baseUrl(ApiHost.getHost());
        }
        HttpClient.getRetrofitBuilder().baseUrl(httpGlobalConfig.getBaseUrl());

        if (httpGlobalConfig.getConverterFactory() == null) {
            httpGlobalConfig.converterFactory(GsonConverterFactory.create());
        }
        HttpClient.getRetrofitBuilder().addConverterFactory(httpGlobalConfig.getConverterFactory());

        if (httpGlobalConfig.getCallAdapterFactory() == null) {
            httpGlobalConfig.callAdapterFactory(RxJava2CallAdapterFactory.create());
        }
        HttpClient.getRetrofitBuilder().addCallAdapterFactory(httpGlobalConfig.getCallAdapterFactory());

        if (httpGlobalConfig.getCallFactory() != null) {
            HttpClient.getRetrofitBuilder().callFactory(httpGlobalConfig.getCallFactory());
        }

        if (httpGlobalConfig.getHostnameVerifier() == null) {
            httpGlobalConfig.hostnameVerifier(new SSL.UnSafeHostnameVerifier(httpGlobalConfig.getBaseUrl()));
        }
        HttpClient.getOkHttpBuilder().hostnameVerifier(httpGlobalConfig.getHostnameVerifier());

        if (httpGlobalConfig.getSslSocketFactory() == null) {
            httpGlobalConfig.SSLSocketFactory(SSL.getSslSocketFactory(null, null, null));
        }
        HttpClient.getOkHttpBuilder().sslSocketFactory(httpGlobalConfig.getSslSocketFactory());

        if (httpGlobalConfig.getConnectionPool() == null) {
            httpGlobalConfig.connectionPool(new ConnectionPool(AppConfig.DEFAULT_MAX_IDLE_CONNECTIONS,
                    AppConfig.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS));
        }
        HttpClient.getOkHttpBuilder().connectionPool(httpGlobalConfig.getConnectionPool());

        if (httpGlobalConfig.isCookie() && httpGlobalConfig.getApiCookie() == null) {
            httpGlobalConfig.apiCookie(new ApiCookie(HttpClient.getContext()));
        }
        if (httpGlobalConfig.isCookie()) {
            HttpClient.getOkHttpBuilder().cookieJar(httpGlobalConfig.getApiCookie());
        }

        if (httpGlobalConfig.getHttpCacheDirectory() == null) {
            httpGlobalConfig.setHttpCacheDirectory(new File(HttpClient.getContext().getCacheDir(), AppConfig.CACHE_HTTP_DIR));
        }
        if (httpGlobalConfig.isHttpCache()) {
            try {
                if (httpGlobalConfig.getHttpCache() == null) {
                    httpGlobalConfig.httpCache(new Cache(httpGlobalConfig.getHttpCacheDirectory(), AppConfig.CACHE_MAX_SIZE));
                }
                httpGlobalConfig.cacheOnline(httpGlobalConfig.getHttpCache());
                httpGlobalConfig.cacheOffline(httpGlobalConfig.getHttpCache());
            } catch (Exception e) {
                Log.e("","Could not create http cache" + e);
            }
        }
        if (httpGlobalConfig.getHttpCache() != null) {
            HttpClient.getOkHttpBuilder().cache(httpGlobalConfig.getHttpCache());
        }
        HttpClient.getOkHttpBuilder().connectTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        HttpClient.getOkHttpBuilder().writeTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        HttpClient.getOkHttpBuilder().readTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * 获取第一级type
     *
     * @param t
     * @param <T>
     * @return
     */
    protected <T> Type getType(T t) {
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
    protected <T> Type getSubType(T t) {
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

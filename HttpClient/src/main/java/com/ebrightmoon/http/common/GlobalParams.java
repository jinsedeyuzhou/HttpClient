package com.ebrightmoon.http.common;

import com.ebrightmoon.http.core.ApiCookie;
import com.ebrightmoon.http.interceptor.GzipRequestInterceptor;
import com.ebrightmoon.http.interceptor.OfflineCacheInterceptor;
import com.ebrightmoon.http.interceptor.OnlineCacheInterceptor;
import com.ebrightmoon.http.mode.ApiHost;
import com.ebrightmoon.http.restrofit.HttpClient;

import java.io.File;
import java.net.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.Call.Factory;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;

/**
 *  请求全局配置
 * @author:
 * @date: 2017-04-28 17:17
 */
public class GlobalParams {
    private CallAdapter.Factory callAdapterFactory;//Call适配器工厂
    private Converter.Factory converterFactory;//转换工厂
    private Factory callFactory;//Call工厂
    private SSLSocketFactory sslSocketFactory;//SSL工厂
    private HostnameVerifier hostnameVerifier;//主机域名验证
    private ConnectionPool connectionPool;//连接池
    private Map<String, String> GlobalParams = new LinkedHashMap<>();//请求参数
    private Map<String, String> globalHeaders = new LinkedHashMap<>();//请求头
    private boolean isHttpCache;//是否使用Http缓存
    private File httpCacheDirectory;//Http缓存路径
    private Cache httpCache;//Http缓存对象
    private boolean isCookie;//是否使用Cookie
    private ApiCookie apiCookie;//Cookie配置
    private String baseUrl;//基础域名
    private int retryDelayMillis;//请求失败重试间隔时间
    private int retryCount;//请求失败重试次数

    private static GlobalParams instance;

    private GlobalParams() {
    }

    public static GlobalParams getInstance() {
        if (instance == null) {
            synchronized (GlobalParams.class) {
                if (instance == null) {
                    instance = new GlobalParams();
                }
            }
        }
        return instance;
    }

    /**
     * 设置CallAdapter工厂
     *
     * @param factory
     * @return
     */
    public GlobalParams callAdapterFactory(CallAdapter.Factory factory) {
        this.callAdapterFactory = factory;
        return this;
    }

    /**
     * 设置转换工厂
     *
     * @param factory
     * @return
     */
    public GlobalParams converterFactory(Converter.Factory factory) {
        this.converterFactory = factory;
        return this;
    }

    /**
     * 设置Call的工厂
     *
     * @param factory
     * @return
     */
    public GlobalParams callFactory(Factory factory) {
        this.callFactory = checkNotNull(factory, "factory == null");
        return this;
    }

    /**
     * 设置SSL工厂
     *
     * @param sslSocketFactory
     * @return
     */
    public GlobalParams SSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    /**
     * 设置主机验证机制
     *
     * @param hostnameVerifier
     * @return
     */
    public GlobalParams hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    /**
     * 设置连接池
     *
     * @param connectionPool
     * @return
     */
    public GlobalParams connectionPool(ConnectionPool connectionPool) {
        this.connectionPool = checkNotNull(connectionPool, "connectionPool == null");
        return this;
    }

    /**
     * 设置请求头部
     *
     * @param globalHeaders
     * @return
     */
    public GlobalParams globalHeaders(Map<String, String> globalHeaders) {
        if (globalHeaders != null) {
            this.globalHeaders = globalHeaders;
        }
        return this;
    }

    /**
     * 设置请求参数
     *
     * @param GlobalParams
     * @return
     */
    public GlobalParams GlobalParams(Map<String, String> GlobalParams) {
        if (GlobalParams != null) {
            this.GlobalParams = GlobalParams;
        }
        return this;
    }

    /**
     * 设置是否添加HTTP缓存
     *
     * @param isHttpCache
     * @return
     */
    public GlobalParams setHttpCache(boolean isHttpCache) {
        this.isHttpCache = isHttpCache;
        return this;
    }

    /**
     * 设置HTTP缓存路径
     *
     * @param httpCacheDirectory
     * @return
     */
    public GlobalParams setHttpCacheDirectory(File httpCacheDirectory) {
        this.httpCacheDirectory = httpCacheDirectory;
        return this;
    }

    /**
     * 设置HTTP缓存
     *
     * @param httpCache
     * @return
     */
    public GlobalParams httpCache(Cache httpCache) {
        this.httpCache = httpCache;
        return this;
    }

    /**
     * 设置是否添加Cookie
     *
     * @param isCookie
     * @return
     */
    public GlobalParams setCookie(boolean isCookie) {
        this.isCookie = isCookie;
        return this;
    }

    /**
     * 设置Cookie管理
     *
     * @param cookie
     * @return
     */
    public GlobalParams apiCookie(ApiCookie cookie) {
        this.apiCookie = checkNotNull(cookie, "cookieManager == null");
        return this;
    }

    /**
     * 设置请求baseUrl
     *
     * @param baseUrl
     * @return
     */
    public GlobalParams baseUrl(String baseUrl) {
        this.baseUrl = checkNotNull(baseUrl, "baseUrl == null");
        ApiHost.setHost(this.baseUrl);
        return this;
    }

    /**
     * 设置请求失败重试间隔时间
     *
     * @param retryDelayMillis
     * @return
     */
    public GlobalParams retryDelayMillis(int retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
        return this;
    }

    /**
     * 设置请求失败重试次数
     *
     * @param retryCount
     * @return
     */
    public GlobalParams retryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    /**
     * 设置代理
     *
     * @param proxy
     * @return
     */
    public GlobalParams proxy(Proxy proxy) {
        HttpClient.getOkHttpBuilder().proxy(checkNotNull(proxy, "proxy == null"));
        return this;
    }

    /**
     * 设置连接超时时间（秒）
     *
     * @param timeout
     * @return
     */
    public GlobalParams connectTimeout(int timeout) {
        return connectTimeout(timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置读取超时时间（秒）
     *
     * @param timeout
     * @return
     */
    public GlobalParams readTimeout(int timeout) {
        return readTimeout(timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置写入超时时间（秒）
     *
     * @param timeout
     * @return
     */
    public GlobalParams writeTimeout(int timeout) {
        return writeTimeout(timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置连接超时时间
     *
     * @param timeout
     * @param unit
     * @return
     */
    public GlobalParams connectTimeout(int timeout, TimeUnit unit) {
        if (timeout > -1) {
            HttpClient.getOkHttpBuilder().connectTimeout(timeout, unit);
        } else {
            HttpClient.getOkHttpBuilder().connectTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
        return this;
    }

    /**
     * 设置写入超时时间
     *
     * @param timeout
     * @param unit
     * @return
     */
    public GlobalParams writeTimeout(int timeout, TimeUnit unit) {
        if (timeout > -1) {
            HttpClient.getOkHttpBuilder().writeTimeout(timeout, unit);
        } else {
            HttpClient.getOkHttpBuilder().writeTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
        return this;
    }

    /**
     * 设置读取超时时间
     *
     * @param timeout
     * @param unit
     * @return
     */
    public GlobalParams readTimeout(int timeout, TimeUnit unit) {
        if (timeout > -1) {
            HttpClient.getOkHttpBuilder().readTimeout(timeout, unit);
        } else {
            HttpClient.getOkHttpBuilder().readTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
        return this;
    }

    /**
     * 设置拦截器
     *
     * @param interceptor
     * @return
     */
    public GlobalParams interceptor(Interceptor interceptor) {
        HttpClient.getOkHttpBuilder().addInterceptor(checkNotNull(interceptor, "interceptor == null"));
        return this;
    }

    /**
     * 设置网络拦截器
     *
     * @param interceptor
     * @return
     */
    public GlobalParams networkInterceptor(Interceptor interceptor) {
        HttpClient.getOkHttpBuilder().addNetworkInterceptor(checkNotNull(interceptor, "interceptor == null"));
        return this;
    }

    /**
     * 使用POST方式是否需要进行GZIP压缩，服务器不支持则不设置
     *
     * @return
     */
    public GlobalParams postGzipInterceptor() {
        interceptor(new GzipRequestInterceptor());
        return this;
    }

    /**
     * 设置在线缓存，主要针对网路请求过程进行缓存
     *
     * @param httpCache
     * @return
     */
    public GlobalParams cacheOnline(Cache httpCache) {
        networkInterceptor(new OnlineCacheInterceptor());
        this.httpCache = httpCache;
        return this;
    }

    /**
     * 设置在线缓存，主要针对网路请求过程进行缓存
     *
     * @param httpCache
     * @param cacheControlValue
     * @return
     */
    public GlobalParams cacheOnline(Cache httpCache, final int cacheControlValue) {
        networkInterceptor(new OnlineCacheInterceptor(cacheControlValue));
        this.httpCache = httpCache;
        return this;
    }

    /**
     * 设置离线缓存，主要针对网路请求过程进行缓存
     *
     * @param httpCache
     * @return
     */
    public GlobalParams cacheOffline(Cache httpCache) {
        networkInterceptor(new OfflineCacheInterceptor(HttpClient.getContext()));
        interceptor(new OfflineCacheInterceptor(HttpClient.getContext()));
        this.httpCache = httpCache;
        return this;
    }

    /**
     * 设置离线缓存，主要针对网路请求过程进行缓存
     *
     * @param httpCache
     * @param cacheControlValue
     * @return
     */
    public GlobalParams cacheOffline(Cache httpCache, final int cacheControlValue) {
        networkInterceptor(new OfflineCacheInterceptor(HttpClient.getContext(), cacheControlValue));
        interceptor(new OfflineCacheInterceptor(HttpClient.getContext(), cacheControlValue));
        this.httpCache = httpCache;
        return this;
    }

    public CallAdapter.Factory getCallAdapterFactory() {
        return callAdapterFactory;
    }

    public Converter.Factory getConverterFactory() {
        return converterFactory;
    }

    public Factory getCallFactory() {
        return callFactory;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public Map<String, String> getGlobalParams() {
        return GlobalParams;
    }

    public Map<String, String> getGlobalHeaders() {
        return globalHeaders;
    }

    public boolean isHttpCache() {
        return isHttpCache;
    }

    public boolean isCookie() {
        return isCookie;
    }

    public ApiCookie getApiCookie() {
        return apiCookie;
    }

    public Cache getHttpCache() {
        return httpCache;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public int getRetryDelayMillis() {
        if (retryDelayMillis < 0) {
            retryDelayMillis = AppConfig.DEFAULT_RETRY_DELAY_MILLIS;
        }
        return retryDelayMillis;
    }

    public int getRetryCount() {
        if (retryCount < 0) {
            retryCount = AppConfig.DEFAULT_RETRY_COUNT;
        }
        return retryCount;
    }

    public File getHttpCacheDirectory() {
        return httpCacheDirectory;
    }

    private <T> T checkNotNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }
}

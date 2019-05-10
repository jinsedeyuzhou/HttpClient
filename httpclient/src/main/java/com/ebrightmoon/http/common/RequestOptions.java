package com.ebrightmoon.http.common;

import android.content.Context;

import com.ebrightmoon.http.callback.UCallback;
import com.ebrightmoon.http.core.ApiCache;
import com.ebrightmoon.http.mode.HttpHeaders;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class RequestOptions <R extends RequestOptions> {
    protected Context mContext;
    protected OkHttpClient.Builder okHttpBuilder;
    protected Retrofit retrofit;
    protected Retrofit.Builder retrofitBuilder;
    protected ApiCache.Builder apiCacheBuilder;
    protected OkHttpClient okHttpClient;
    protected Object tag;//请求标签
    protected List<Interceptor> interceptors = new ArrayList<>();//局部请求的拦截器
    protected List<Interceptor> networkInterceptors = new ArrayList<>();//局部请求的网络拦截器
    public LinkedHashMap<String, String> headers=new LinkedHashMap<>();
    protected UCallback uploadCallback;//上传进度回调
    protected long readTimeOut;//读取超时时间
    protected long writeTimeOut;//写入超时时间
    protected long connectTimeOut;//连接超时时间
    protected boolean isHttpCache;//是否使用Http缓存
    private String baseUrl;


    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public UCallback getUploadCallback() {
        return uploadCallback;
    }

    public void setUploadCallback(UCallback uploadCallback) {
        this.uploadCallback = uploadCallback;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }


    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public List<Interceptor> getNetworkInterceptors() {
        return networkInterceptors;
    }

    public void setNetworkInterceptors(List<Interceptor> networkInterceptors) {
        this.networkInterceptors = networkInterceptors;
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
        this.headers.putAll(headers);
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
    public R headers(LinkedHashMap<String, String>  headers) {
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

    public LinkedHashMap<String, String> getHeaders() {
        return headers;
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
}

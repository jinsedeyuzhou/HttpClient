package com.ebrightmoon.http.common;


import com.ebrightmoon.http.callback.UCallback;
import com.ebrightmoon.http.mode.CacheMode;
import com.ebrightmoon.http.mode.HttpHeaders;
import com.ebrightmoon.http.mode.MediaTypes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public final class Request {

    //请求配置
    private Object tag;//请求标签
    private List<Interceptor> interceptors;//局部请求的拦截器
    private List<Interceptor> networkInterceptors;//局部请求的网络拦截器
    private LinkedHashMap<String, String> headers;
    private long readTimeOut;//读取超时时间
    private long writeTimeOut;//写入超时时间
    private long connectTimeOut;//连接超时时间
    private boolean isHttpCache;//是否使用Http缓存
    private String baseUrl;


    private String suffixUrl = "";//链接后缀
    private int retryDelayMillis;//请求失败重试间隔时间
    private int retryCount;//重试次数
    private boolean isLocalCache;//是否使用本地缓存
    private CacheMode cacheMode;//本地缓存类型
    private String cacheKey;//本地缓存Key
    private long cacheTime;//本地缓存时间
    private Map<String, String> params;//请求参数
    // 请求参数配置
    private Map<String, Object> forms = new LinkedHashMap<>();
    private StringBuilder stringBuilder = new StringBuilder();
    private RequestBody requestBody;
    private MediaType mediaType;
    private String content;
    private UCallback uploadCallback;//上传进度回调

    public Request(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.tag = builder.tag;
        this.interceptors = builder.interceptors;
        this.networkInterceptors = builder.networkInterceptors;
        this.headers = builder.headers;
        this.readTimeOut = builder.readTimeOut;
        this.writeTimeOut = builder.writeTimeOut;
        this.connectTimeOut = builder.connectTimeOut;
        this.isHttpCache = builder.isHttpCache;
        this.suffixUrl=builder.suffixUrl;
        this.retryDelayMillis=builder.retryDelayMillis;
        this.retryCount=builder.retryCount;
        this.isLocalCache=builder.isLocalCache;
        this.cacheMode=builder.cacheMode;
        this.cacheKey=builder.cacheKey;
        this.cacheTime=builder.cacheTime;
        this.params=builder.params;
        this.forms=builder.forms;
        this.stringBuilder=builder.stringBuilder;
        this.requestBody=builder.requestBody;
        this.mediaType=builder.mediaType;
        this.content=builder.content;
        this.uploadCallback=builder.uploadCallback;

    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody=requestBody;
    }

    public static class Builder {
        private Object tag;//请求标签
        private List<Interceptor> interceptors;//局部请求的拦截器
        private List<Interceptor> networkInterceptors;//局部请求的网络拦截器
        private LinkedHashMap<String, String> headers;
        private long readTimeOut;//读取超时时间
        private long writeTimeOut;//写入超时时间
        private long connectTimeOut;//连接超时时间
        private boolean isHttpCache;//是否使用Http缓存
        private String baseUrl;
        // 请求参数
        protected String suffixUrl = "";//链接后缀
        private int retryDelayMillis;//请求失败重试间隔时间
        private int retryCount;//重试次数
        private boolean isLocalCache;//是否使用本地缓存
        private CacheMode cacheMode;//本地缓存类型
        private String cacheKey;//本地缓存Key
        private long cacheTime;//本地缓存时间
        private Map<String, String> params;//请求参数

        // 请求参数配置
        private Map<String, Object> forms = new LinkedHashMap<>();
        private StringBuilder stringBuilder = new StringBuilder();
        private RequestBody requestBody;
        private MediaType mediaType;
        protected String content;
        private UCallback uploadCallback;//上传进度回调

        public Builder() {
            this.interceptors = new ArrayList<>();
            this.networkInterceptors = new ArrayList<>();
            this.headers = new LinkedHashMap<>();
            this.readTimeOut = AppConfig.DEFAULT_TIMEOUT;
            this.writeTimeOut = AppConfig.DEFAULT_TIMEOUT;
            this.connectTimeOut = AppConfig.DEFAULT_TIMEOUT;
            this.isHttpCache = false;
            this.baseUrl = AppConfig.BASE_URL;
            params= new LinkedHashMap<>();
        }

        public Builder(Request request) {
            this.baseUrl = request.baseUrl;
            this.interceptors = request.interceptors;
            this.networkInterceptors = request.networkInterceptors;
            this.headers = request.headers;
            this.readTimeOut = request.readTimeOut;
            this.writeTimeOut = request.writeTimeOut;
            this.connectTimeOut = request.connectTimeOut;
            this.isHttpCache = request.isHttpCache;
            this.tag = request.tag;
            this.suffixUrl = request.suffixUrl;
            this.retryCount = request.retryCount;
            this.retryDelayMillis = request.retryDelayMillis;
            this.isLocalCache = request.isLocalCache;
            this.cacheMode = request.cacheMode;
            this.cacheKey = request.cacheKey;
            this.cacheTime = request.cacheTime;
            this.forms = request.forms;
            this.stringBuilder = request.stringBuilder;
            this.requestBody = request.requestBody;
            this.mediaType = request.mediaType;
            this.content = request.content;
        }

        public Builder setSuffixUrl(String suffixUrl) {
            this.suffixUrl = suffixUrl;
            return this;
        }

        public Builder setRetryDelayMillis(int retryDelayMillis) {
            this.retryDelayMillis = retryDelayMillis;
            return this;
        }

        public Builder setRetryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder setLocalCache(boolean localCache) {
            isLocalCache = localCache;
            return this;
        }

        public Builder setCacheMode(CacheMode cacheMode) {
            this.cacheMode = cacheMode;
            return this;
        }

        public Builder setUCallback(UCallback uploadCallback) {
            this.uploadCallback = uploadCallback;
            return this;
        }


        public Builder setCacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
            return this;
        }

        public Builder setCacheTime(long cacheTime) {
            this.cacheTime = cacheTime;
            return this;
        }

        public Builder setForms(Map<String, Object> forms) {
            this.forms = forms;
            return this;
        }

        public Builder setParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder setStringBuilder(StringBuilder stringBuilder) {
            this.stringBuilder = stringBuilder;
            return this;
        }

        public Builder setRequestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public Builder setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            mediaType=MediaTypes.APPLICATION_JSON_TYPE;
            return this;
        }

        public Builder setTag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }

        public Builder addNetworkInterceptor(Interceptor networkInterceptor) {
            this.networkInterceptors.add(networkInterceptor);
            return this;
        }

        public Builder addHeader(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder removeHeader(String name) {
            this.headers.remove(name);
            return this;
        }

        public Builder setReadTimeOut(long readTimeOut) {
            this.readTimeOut = readTimeOut;
            return this;
        }

        public Builder setWriteTimeOut(long writeTimeOut) {
            this.writeTimeOut = writeTimeOut;
            return this;
        }

        public Builder setConnectTimeOut(long connectTimeOut) {
            this.connectTimeOut = connectTimeOut;
            return this;
        }

        public Builder setHttpCache(boolean httpCache) {
            isHttpCache = httpCache;
            return this;
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Request build() {
            if (baseUrl == null) throw new IllegalStateException("url == null");
            return new Request(this);
        }
    }

    public UCallback getUploadCallback() {
        return uploadCallback;
    }



    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }


    public List<Interceptor> getNetworkInterceptors() {
        return networkInterceptors;
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


    public String getBaseUrl() {
        return baseUrl;
    }



    public String getSuffixUrl() {
        if (stringBuilder.length()>0) {
            suffixUrl = suffixUrl + stringBuilder.toString();
        }
        return suffixUrl;
    }


    public int getRetryDelayMillis() {
        return retryDelayMillis;
    }



    public int getRetryCount() {
        return retryCount;
    }


    public boolean isLocalCache() {
        return isLocalCache;
    }



    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public String getCacheKey() {
        return cacheKey;
    }



    public long getCacheTime() {
        return cacheTime;
    }



    public Map<String, Object> getForms() {
        return forms;
    }

    public Map<String, String> getParams() {
        return params;
    }



    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }



    public RequestBody getRequestBody() {
        return requestBody;
    }



    public MediaType getMediaType() {
        return mediaType;
    }


    public String getContent() {
        return content;
    }


}

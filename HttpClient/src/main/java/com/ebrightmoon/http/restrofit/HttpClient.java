package com.ebrightmoon.http.restrofit;

import android.content.Context;

import com.ebrightmoon.http.callback.UCallback;
import com.ebrightmoon.http.common.GlobalParams;
import com.ebrightmoon.http.core.ApiCache;
import com.ebrightmoon.http.core.ApiManager;
import com.ebrightmoon.http.request.BaseHttpRequest;
import com.ebrightmoon.http.request.DeleteRequest;
import com.ebrightmoon.http.request.DownloadRequest;
import com.ebrightmoon.http.request.GetRequest;
import com.ebrightmoon.http.request.HeadRequest;
import com.ebrightmoon.http.request.OptionsRequest;
import com.ebrightmoon.http.request.PatchRequest;
import com.ebrightmoon.http.request.PostRequest;
import com.ebrightmoon.http.request.PutRequest;
import com.ebrightmoon.http.request.RetrofitRequest;
import com.ebrightmoon.http.request.UploadRequest;

import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Time: 2019/5/26
 * Author:wyy
 * Description:
 */
public class HttpClient {

    private static Context context;
    private static OkHttpClient.Builder okHttpBuilder;
    private static Retrofit.Builder retrofitBuilder;
    private static ApiCache.Builder apiCacheBuilder;
    private static OkHttpClient okHttpClient;

    private static final GlobalParams NET_GLOBAL_CONFIG = GlobalParams.getInstance();

    public static GlobalParams config() {
        return NET_GLOBAL_CONFIG;
    }

    public static void init(Context appContext) {
        if (context == null && appContext != null) {
            context = appContext.getApplicationContext();
            okHttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            apiCacheBuilder = new ApiCache.Builder(context);
        }
    }

    public static Context getContext() {
        if (context == null) {
            throw new IllegalStateException("Please call HttpClient.init(this) in Application to initialize!");
        }
        return context;
    }

    public static OkHttpClient.Builder getOkHttpBuilder() {
        if (okHttpBuilder == null) {
            throw new IllegalStateException("Please call HttpClient.init(this) in Application to initialize!");
        }
        return okHttpBuilder;
    }

    public static Retrofit.Builder getRetrofitBuilder() {
        if (retrofitBuilder == null) {
            throw new IllegalStateException("Please call HttpClient.init(this) in Application to initialize!");
        }
        return retrofitBuilder;
    }

    public static ApiCache.Builder getApiCacheBuilder() {
        if (apiCacheBuilder == null) {
            throw new IllegalStateException("Please call HttpClient.init(this) in Application to initialize!");
        }
        return apiCacheBuilder;
    }

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = getOkHttpBuilder().build();
        }
        return okHttpClient;
    }

    public static ApiCache getApiCache() {
        return getApiCacheBuilder().build();
    }

    /**
     * 通用请求，可传入自定义请求
     *
     * @param request
     * @return
     */
    public static BaseHttpRequest base(BaseHttpRequest request) {
        if (request != null) {
            return request;
        } else {
            return new GetRequest("");
        }
    }

    /**
     * 可传入自定义Retrofit接口服务的请求类型
     *
     * @return
     */
    public static <T> RetrofitRequest retrofit() {
        return new RetrofitRequest();
    }

    /**
     * GET请求
     *
     * @param suffixUrl
     * @return
     */
    public static GetRequest get(String suffixUrl) {
        return new GetRequest(suffixUrl);
    }

    /**
     * POST请求
     *
     * @param suffixUrl
     * @return
     */
    public static PostRequest post(String suffixUrl) {
        return new PostRequest(suffixUrl);
    }

    /**
     * HEAD请求
     *
     * @param suffixUrl
     * @return
     */
    public static HeadRequest head(String suffixUrl) {
        return new HeadRequest(suffixUrl);
    }

    /**
     * PUT请求
     *
     * @param suffixUrl
     * @return
     */
    public static PutRequest put(String suffixUrl) {
        return new PutRequest(suffixUrl);
    }

    /**
     * PATCH请求
     *
     * @param suffixUrl
     * @return
     */
    public static PatchRequest path(String suffixUrl) {
        return new PatchRequest(suffixUrl);
    }

    /**
     * OPTIONS请求
     *
     * @param suffixUrl
     * @return
     */
    public static OptionsRequest options(String suffixUrl) {
        return new OptionsRequest(suffixUrl);
    }

    /**
     * DELETE请求
     *
     * @param suffixUrl
     * @return
     */
    public static DeleteRequest delete(String suffixUrl) {
        return new DeleteRequest(suffixUrl);
    }

    /**
     * 上传
     *
     * @param suffixUrl
     * @return
     */
    public static UploadRequest upload(String suffixUrl) {
        return new UploadRequest(suffixUrl);
    }

    /**
     * 上传（包含上传进度回调）
     *
     * @param suffixUrl
     * @return
     */
    public static UploadRequest upload(String suffixUrl, UCallback uCallback) {
        return new UploadRequest(suffixUrl, uCallback);
    }

    /**
     * 下载（回调DownProgress）
     *
     * @param suffixUrl
     * @return
     */
    public static DownloadRequest down(String suffixUrl) {
        return new DownloadRequest(suffixUrl);
    }

    /**
     * 添加请求订阅者
     *
     * @param tag
     * @param disposable
     */
    public static void addDisposable(Object tag, Disposable disposable) {
        ApiManager.get().add(tag, disposable);
    }

    /**
     * 根据Tag取消请求
     */
    public static void cancelTag(Object tag) {
        ApiManager.get().cancel(tag);
    }

    /**
     * 取消所有请求请求
     */
    public static void cancelAll() {
        ApiManager.get().cancelAll();
    }

    /**
     * 清除对应Key的缓存
     *
     * @param key
     */
    public static void removeCache(String key) {
        getApiCache().remove(key);
    }

    /**
     * 清楚所有缓存并关闭缓存
     *
     * @return
     */
    public static Disposable clearCache() {
        return getApiCache().clear();
    }

}

package com.ebrightmoon.http.restrofit;


import android.content.Context;
import android.os.Environment;
import android.util.Log;


import com.ebrightmoon.http.api.ApiService;
import com.ebrightmoon.http.body.UploadProgressRequestBody;
import com.ebrightmoon.http.callback.ACallback;
import com.ebrightmoon.http.callback.UCallback;
import com.ebrightmoon.http.common.AppConfig;
import com.ebrightmoon.http.common.HttpApp;
import com.ebrightmoon.http.common.HttpUtils;
import com.ebrightmoon.http.common.Request;
import com.ebrightmoon.http.core.ApiCache;
import com.ebrightmoon.http.core.ApiCookie;
import com.ebrightmoon.http.core.ApiManager;
import com.ebrightmoon.http.core.ApiTransformer;
import com.ebrightmoon.http.func.ApiFunc;
import com.ebrightmoon.http.func.ApiRetryFunc;
import com.ebrightmoon.http.interceptor.GzipRequestInterceptor;
import com.ebrightmoon.http.interceptor.HeadersInterceptor;
import com.ebrightmoon.http.interceptor.HttpResponseInterceptor;
import com.ebrightmoon.http.interceptor.LoggingInterceptor;
import com.ebrightmoon.http.interceptor.OfflineCacheInterceptor;
import com.ebrightmoon.http.interceptor.OnlineCacheInterceptor;
import com.ebrightmoon.http.interceptor.UploadProgressInterceptor;
import com.ebrightmoon.http.mode.ApiHost;
import com.ebrightmoon.http.mode.CacheResult;
import com.ebrightmoon.http.mode.DownProgress;
import com.ebrightmoon.http.mode.MediaTypes;
import com.ebrightmoon.http.mode.Method;
import com.ebrightmoon.http.recycle.ActivityLifeCycleEvent;
import com.ebrightmoon.http.recycle.RxHelper;
import com.ebrightmoon.http.subscriber.ApiCallbackSubscriber;
import com.ebrightmoon.http.subscriber.DownCallbackSubscriber;
import com.ebrightmoon.http.util.GsonUtil;
import com.ebrightmoon.http.util.SSL;


import org.reactivestreams.Publisher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ebrightmoon.http.common.HttpUtils.getSubType;
import static com.ebrightmoon.http.common.HttpUtils.writeFile;
import static com.ebrightmoon.http.mode.MediaTypes.APPLICATION_JSON_TYPE;


/**
 * Created by wyy on 2017/9/6.
 * 无论get post put 请求参数均参与校验
 */

public class AppClient {

    private static int DEFAULT_TIMEOUT = 120;
    private Retrofit retrofit;
    private ApiService apiService;

    // ================    请求类      ==============
    private Context mContext;
    private OkHttpClient.Builder okHttpBuilder;
    private Retrofit.Builder retrofitBuilder;

    //-----------------全局参数设置------------------------
    private CallAdapter.Factory callAdapterFactory;//Call适配器工厂
    private Converter.Factory converterFactory;//转换工厂
    private Call.Factory callFactory;//Call工厂
    private SSLSocketFactory sslSocketFactory;//SSL工厂
    private HostnameVerifier hostnameVerifier;//主机域名验证
    private ConnectionPool connectionPool;//连接池
    private Map<String, String> glabalParams = new LinkedHashMap<>();//请求参数
    private LinkedHashMap<String, String> globalHeaders = new LinkedHashMap<>();//请求头
    private boolean isHttpCache;//是否使用Http缓存
    private File httpCacheDirectory;//Http缓存路径
    private Cache httpCache;//Http缓存对象
    private boolean isCookie;//是否使用Cookie
    private ApiCache.Builder apiCacheBuilder;
    private ApiCookie apiCookie;//Cookie配置
    private String baseUrl;//基础域名
    private int retryDelayMillis;//请求失败重试间隔时间
    private int retryCount;//请求失败重试次数
    private Request httpRequest;// 局部参数设置
    private Request.Builder requestBuilder;// 局部参数设置

    private static AppClient instance;

    public static AppClient getInstance() {
        if (instance == null) {
            synchronized (AppClient.class) {
                if (instance == null) {
                    instance = new AppClient();
                }
            }
        }
        return instance;
    }

    private AppClient() {
        okHttpBuilder = new OkHttpClient.Builder();
        retrofitBuilder = new Retrofit.Builder();
        httpRequest = new Request.Builder().build();
        if (mContext == null) {
            mContext = HttpApp.getApp();
            apiCacheBuilder = new ApiCache.Builder(mContext);
        }

    }


    public AppClient createApiService() {
        apiService = create(ApiService.class);
        return this;
    }


    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        initGlobalParams();
        initLocalParams();
        return retrofit.create(service);
    }

    /**
     * 通用 请求
     *
     * @param request
     * @param callback
     * @param <T>
     */
    public <T> void request(Request.Builder request, ACallback<T> callback) {
        if (request != null) {
            httpRequest = request.build();
        }
        initGlobalParams();
        initLocalParams();
        execute(callback);
    }

    /**
     * 通用get 请求
     *
     * @param request
     * @param callback
     * @param <T>
     */
    public <T> void get(Request.Builder request, ACallback<T> callback) {
        if (request != null) {
            request.setMethod(Method.GET);
            httpRequest = request.build();
        }
        initGlobalParams();
        initLocalParams();
        execute(callback);
    }

    /**
     * post 请求
     *
     * @param request
     * @param callback
     * @param <T>
     */
    public <T> void post(Request.Builder request, ACallback<T> callback) {
        if (request != null) {
            request.setMethod(Method.POST);
            httpRequest = request.build();
        }
        initGlobalParams();
        initLocalParams();
        execute(callback);
    }

    /**
     * 下载 请求
     *
     * @param request
     * @param callback
     * @param <T>
     */
    public <T> void download(Request.Builder request, ACallback<T> callback) {
        if (request != null) {
            request.setMethod(Method.DOWNLOAD);
            httpRequest = request.build();
        }
        initGlobalParams();
        initLocalParams();
        execute(callback);
    }

    /**
     * 上传 请求
     *
     * @param request
     * @param callback
     * @param <T>
     */
    public <T> void upload(Request.Builder request, ACallback<T> callback) {
        if (request != null) {
            request.setMethod(Method.UPLOAD);
            httpRequest = request.build();
        }
        initGlobalParams();
        initLocalParams();
        execute(callback);
    }


    /**
     * 默认为Get请求
     *
     * @param type
     * @param <T>
     * @return
     */
    private <T> Observable<T> execute(Type type) {
        apiService = retrofit.create(ApiService.class);
        if (httpRequest.getMethodType() == Method.GET) {
            return apiService.get(httpRequest.getSuffixUrl(), httpRequest.getParams())
                    .compose(RxHelper.bindUntilEvent(ActivityLifeCycleEvent.DESTROY, httpRequest.getLifecycleSubject()))
                    .compose(ApiTransformer.<T>norTransformer(type, httpRequest.getRetryCount(), httpRequest.getRetryDelayMillis()));
        } else if (httpRequest.getMethodType() == Method.POST) {
            if (httpRequest.getForms() != null && httpRequest.getForms().size() > 0) {
                if (httpRequest.getParams() != null && httpRequest.getParams().size() > 0) {
                    Iterator<Map.Entry<String, String>> entryIterator = httpRequest.getParams().entrySet().iterator();
                    Map.Entry<String, String> entry;
                    while (entryIterator.hasNext()) {
                        entry = entryIterator.next();
                        if (entry != null) {
                            httpRequest.getForms().put(entry.getKey(), entry.getValue());
                        }
                    }
                }
                return apiService.postForm(httpRequest.getSuffixUrl(), httpRequest.getForms())
                        .compose(RxHelper.bindUntilEvent(ActivityLifeCycleEvent.DESTROY, httpRequest.getLifecycleSubject()))
                        .compose(ApiTransformer.<T>norTransformer(type, httpRequest.getRetryCount(), httpRequest.getRetryDelayMillis()));
            }
            if (httpRequest.getRequestBody() != null) {
                return apiService.postBody(httpRequest.getSuffixUrl(), httpRequest.getRequestBody())
                        .compose(RxHelper.bindUntilEvent(ActivityLifeCycleEvent.DESTROY, httpRequest.getLifecycleSubject()))
                        .compose(ApiTransformer.<T>norTransformer(type, httpRequest.getRetryCount(), httpRequest.getRetryDelayMillis()));
            }
            if (httpRequest.getContent() != null && httpRequest.getMediaType() != null) {
                httpRequest.setRequestBody(RequestBody.create(httpRequest.getMediaType(), httpRequest.getContent()));
                return apiService.postBody(httpRequest.getSuffixUrl(), httpRequest.getRequestBody())
                        .compose(RxHelper.bindUntilEvent(ActivityLifeCycleEvent.DESTROY, httpRequest.getLifecycleSubject()))
                        .compose(ApiTransformer.<T>norTransformer(type, httpRequest.getRetryCount(), httpRequest.getRetryDelayMillis()));
            }
            return apiService.post(httpRequest.getSuffixUrl(), httpRequest.getParams())
                    .compose(RxHelper.bindUntilEvent(ActivityLifeCycleEvent.DESTROY, httpRequest.getLifecycleSubject()))
                    .compose(ApiTransformer.<T>norTransformer(type, httpRequest.getRetryCount(), httpRequest.getRetryDelayMillis()));

        } else if (httpRequest.getMethodType() == Method.UPLOAD) {
            if (httpRequest.getParams() != null && httpRequest.getParams().size() > 0) {
                Iterator<Map.Entry<String, String>> entryIterator = httpRequest.getParams().entrySet().iterator();
                Map.Entry<String, String> entry;
                while (entryIterator.hasNext()) {
                    entry = entryIterator.next();
                    if (entry != null) {
                        httpRequest.getMultipartBodyParts().add(MultipartBody.Part.createFormData(entry.getKey(), entry.getValue()));
                    }
                }
            }
            return apiService.uploadFiles(httpRequest.getSuffixUrl(), httpRequest.getMultipartBodyParts()).compose(ApiTransformer.<T>norTransformer(type, httpRequest.getRetryCount(), httpRequest.getRetryDelayMillis()));

        } else if (httpRequest.getMethodType() == Method.DOWNLOAD) {
            return (Observable<T>) apiService
                    .downFile(httpRequest.getSuffixUrl(), httpRequest.getParams())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .toFlowable(BackpressureStrategy.LATEST)
                    .flatMap(new Function<ResponseBody, Publisher<?>>() {
                        @Override
                        public Publisher<?> apply(final ResponseBody responseBody) throws Exception {
                            return Flowable.create(new FlowableOnSubscribe<DownProgress>() {
                                @Override
                                public void subscribe(FlowableEmitter<DownProgress> subscriber) throws Exception {
                                    File dir = getDiskCacheDir(httpRequest.getDirName());
                                    if (!dir.exists()) {
                                        dir.mkdirs();
                                    }
                                    File file = new File(dir.getPath() + File.separator + httpRequest.getFileName());
                                    saveFile(subscriber, file, responseBody);
                                }
                            }, BackpressureStrategy.LATEST);
                        }
                    })
                    .sample(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .toObservable()
                    .retryWhen(new ApiRetryFunc(retryCount, retryDelayMillis));
        } else {
            return apiService.get(httpRequest.getSuffixUrl(), httpRequest.getParams()).compose(ApiTransformer.<T>norTransformer(type, httpRequest.getRetryCount(), httpRequest.getRetryDelayMillis()));
        }

    }

    private <T> Observable<CacheResult<T>> cacheExecute(Type type) {
        if (httpRequest.getMethodType() == Method.GET) {
            return this.<T>execute(type).compose(RxHelper.bindUntilEvent(ActivityLifeCycleEvent.DESTROY, httpRequest.getLifecycleSubject())).compose(apiCacheBuilder.build().<T>transformer(httpRequest.getCacheMode(), type));
        } else if (httpRequest.getMethodType() == Method.POST) {
            return this.<T>execute(type).compose(RxHelper.bindUntilEvent(ActivityLifeCycleEvent.DESTROY, httpRequest.getLifecycleSubject())).compose(apiCacheBuilder.build().<T>transformer(httpRequest.getCacheMode(), type));
        } else {
            return this.<T>execute(type).compose(RxHelper.bindUntilEvent(ActivityLifeCycleEvent.DESTROY, httpRequest.getLifecycleSubject())).compose(apiCacheBuilder.build().<T>transformer(httpRequest.getCacheMode(), type));
        }
    }

    private <T> void execute(ACallback<T> callback) {
        DisposableObserver disposableObserver;
        if (httpRequest.getMethodType() == Method.DOWNLOAD) {
            disposableObserver = new DownCallbackSubscriber(callback);
        } else if (httpRequest.getMethodType() == Method.UPLOAD) {
            disposableObserver = new ApiCallbackSubscriber(callback);
        } else {
            disposableObserver = new ApiCallbackSubscriber(callback);
        }
        if (httpRequest.getTag() != null) {
            ApiManager.get().add(httpRequest.getTag(), disposableObserver);
        }
        if (httpRequest.isLocalCache() && httpRequest.getMethodType() != Method.DOWNLOAD && httpRequest.getMethodType() != Method.UPLOAD) {
            this.cacheExecute(getSubType(callback)).subscribe(disposableObserver);
        } else {
            this.execute(HttpUtils.getType(callback)).subscribe(disposableObserver);
        }
    }

    /**
     * 初始化局部参数
     */
    private void initLocalParams() {

        OkHttpClient.Builder newBuilder = okHttpBuilder.build().newBuilder();

        if (globalHeaders != null) {
            httpRequest.getHeaders().putAll(globalHeaders);
        }

        if (!httpRequest.getInterceptors().isEmpty()) {
            for (Interceptor interceptor : httpRequest.getInterceptors()) {
                newBuilder.addInterceptor(interceptor);
            }
        }
        newBuilder.addInterceptor(new LoggingInterceptor());

        if (!httpRequest.getNetworkInterceptors().isEmpty()) {
            for (Interceptor interceptor : httpRequest.getNetworkInterceptors()) {
                newBuilder.addNetworkInterceptor(interceptor);
            }
        }
        newBuilder.addNetworkInterceptor(new HttpResponseInterceptor());


        if (httpRequest.getHeaders().size() > 0) {
            newBuilder.addInterceptor(new HeadersInterceptor(httpRequest.getHeaders()));
        }

        if (httpRequest.getUploadCallback() != null) {
            newBuilder.addNetworkInterceptor(new UploadProgressInterceptor(httpRequest.getUploadCallback()));
        }

        if (httpRequest.getReadTimeOut() > 0) {
            newBuilder.readTimeout(httpRequest.getReadTimeOut(), TimeUnit.SECONDS);
        }

        if (httpRequest.getWriteTimeOut() > 0) {
            newBuilder.readTimeout(httpRequest.getWriteTimeOut(), TimeUnit.SECONDS);
        }

        if (httpRequest.getConnectTimeOut() > 0) {
            newBuilder.readTimeout(httpRequest.getConnectTimeOut(), TimeUnit.SECONDS);
        }

        if (httpRequest.isHttpCache()) {
            try {
                if (httpCache == null) {
                    httpCache = new Cache(httpCacheDirectory, AppConfig.CACHE_MAX_SIZE);
                }
                cacheOnline(httpCache);
                cacheOffline(httpCache);
            } catch (Exception e) {
                Log.e("", "Could not create http cache" + e);
            }
            newBuilder.cache(httpCache);
        }

        if (httpRequest.getBaseUrl() != null) {
            Retrofit.Builder newRetrofitBuilder = new Retrofit.Builder();
            newRetrofitBuilder.baseUrl(httpRequest.getBaseUrl());
            if (converterFactory != null) {
                newRetrofitBuilder.addConverterFactory(converterFactory);
            }
            if (callAdapterFactory != null) {
                newRetrofitBuilder.addCallAdapterFactory(callAdapterFactory);
            }
            if (callFactory != null) {
                newRetrofitBuilder.callFactory(callFactory);
            }
            newBuilder.hostnameVerifier(new SSL.UnSafeHostnameVerifier(httpRequest.getBaseUrl()));
            newRetrofitBuilder.client(newBuilder.build());
            retrofit = newRetrofitBuilder.build();
        } else {
            retrofitBuilder.client(newBuilder.build());
            retrofit = retrofitBuilder.build();
        }

        if (glabalParams != null) {
            httpRequest.getParams().putAll(glabalParams);
        }
        if (httpRequest.getRetryCount() > 0) {
            retryCount = httpRequest.getRetryCount();
        }
        if (httpRequest.getRetryDelayMillis() > 0) {
            retryDelayMillis = httpRequest.getRetryDelayMillis();
        }
        if (httpRequest.isLocalCache()) {
            if (httpRequest.getCacheKey() != null) {
                apiCacheBuilder.cacheKey(httpRequest.getCacheKey());
            } else {
                apiCacheBuilder.cacheKey(ApiHost.getHost());
            }
            if (httpRequest.getCacheTime() > 0) {
                apiCacheBuilder.cacheTime(httpRequest.getCacheTime());
            } else {
                apiCacheBuilder.cacheTime(AppConfig.CACHE_NEVER_EXPIRE);
            }
        }
        if (httpRequest.getBaseUrl() != null && httpRequest.isLocalCache() && httpRequest.getCacheKey() == null) {
            apiCacheBuilder.cacheKey(httpRequest.getBaseUrl());
        }


    }

    /**
     * 初始化全局参数
     */
    private void initGlobalParams() {

        if (baseUrl == null) {
            baseUrl = ApiHost.getHost();
        }
        retrofitBuilder.baseUrl(baseUrl);

        if (converterFactory == null) {
            converterFactory = GsonConverterFactory.create();
        }
        retrofitBuilder.addConverterFactory(converterFactory);

        if (callAdapterFactory == null) {
            callAdapterFactory = RxJava2CallAdapterFactory.create();
        }
        retrofitBuilder.addCallAdapterFactory(callAdapterFactory);

        if (callFactory != null) {
            retrofitBuilder.callFactory(callFactory);
        }

        if (hostnameVerifier == null) {
            hostnameVerifier = new SSL.UnSafeHostnameVerifier(baseUrl);
        }
        okHttpBuilder.hostnameVerifier(hostnameVerifier);

        if (sslSocketFactory == null) {
            sslSocketFactory = SSL.getSslSocketFactory(null, null, null);
        }
        okHttpBuilder.sslSocketFactory(sslSocketFactory);

        if (connectionPool == null) {
            connectionPool = new ConnectionPool(AppConfig.DEFAULT_MAX_IDLE_CONNECTIONS,
                    AppConfig.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS);
        }
        okHttpBuilder.connectionPool(connectionPool);

        if (isCookie && apiCookie == null && mContext != null) {
            apiCookie = new ApiCookie(mContext);
        }
        if (isCookie) {
            okHttpBuilder.cookieJar(apiCookie);
        }

        if (httpCacheDirectory == null && mContext != null) {
            httpCacheDirectory = new File(mContext.getCacheDir(), AppConfig.CACHE_HTTP_DIR);
        }
        if (isHttpCache) {
            try {
                if (httpCache == null) {
                    httpCache = new Cache(httpCacheDirectory, AppConfig.CACHE_MAX_SIZE);
                }
                cacheOnline(httpCache);
                cacheOffline(httpCache);
            } catch (Exception e) {
                Log.e("", "Could not create http cache" + e);
            }
        }
        if (httpCache != null) {
            okHttpBuilder.cache(httpCache);
        }
        okHttpBuilder.connectTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }


    /**
     * 不带参数校验通用get请求
     *
     * @param url
     * @param params
     * @param callback
     * @param <T>
     */
    public <T> void get(String url, Map<String, String> params, ACallback<T> callback) {
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        create(ApiService.class).get(url, params)
                .map(new ApiFunc<T>(getSubType(callback)))
                .compose(ApiTransformer.<T>norTransformer())
                .subscribe(disposableObserver);

    }


    /**
     * 不带参数校验通用post请求 无header    返回数据无数据模型
     *
     * @param url
     * @param params
     * @param callback
     * @param <T>
     */
    public <T> void post(String url, Map<String, String> params, ACallback<T> callback) {
        RequestBody body = RequestBody.create(APPLICATION_JSON_TYPE, GsonUtil.gson().toJson(params));
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        create(ApiService.class).post(url, body)
                .map(new ApiFunc<T>(getSubType(callback)))
                .compose(ApiTransformer.<T>norTransformer())
                .subscribe(disposableObserver);
    }


    //================================设置全局参数==================================

    /**
     * 设置CallAdapter工厂
     *
     * @param factory
     * @return
     */
    public AppClient callAdapterFactory(CallAdapter.Factory factory) {
        this.callAdapterFactory = factory;
        return this;
    }

    /**
     * 设置转换工厂
     *
     * @param factory
     * @return
     */
    public AppClient converterFactory(Converter.Factory factory) {
        this.converterFactory = factory;
        return this;
    }

    /**
     * 设置Call的工厂
     *
     * @param factory
     * @return
     */
    public AppClient callFactory(Call.Factory factory) {
        this.callFactory = checkNotNull(factory, "factory == null");
        return this;
    }

    /**
     * 设置SSL工厂
     *
     * @param sslSocketFactory
     * @return
     */
    public AppClient SSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    /**
     * 设置主机验证机制
     *
     * @param hostnameVerifier
     * @return
     */
    public AppClient hostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    /**
     * 设置连接池
     *
     * @param connectionPool
     * @return
     */
    public AppClient connectionPool(ConnectionPool connectionPool) {
        this.connectionPool = checkNotNull(connectionPool, "connectionPool == null");
        return this;
    }

    /**
     * 设置请求头部
     *
     * @param globalHeaders
     * @return
     */
    public AppClient globalHeaders(LinkedHashMap<String, String> globalHeaders) {
        if (globalHeaders != null) {
            this.globalHeaders = globalHeaders;
        }
        return this;
    }

//    /**
//     * 设置请求参数
//     *
//     * @param AppClient
//     * @return
//     */
//    public AppClient AppClient(Map<String, String> AppClient) {
//        if (AppClient != null) {
//            this.AppClient = AppClient;
//        }
//        return this;
//    }

    /**
     * 设置是否添加HTTP缓存
     *
     * @param isHttpCache
     * @return
     */
    public AppClient setHttpCache(boolean isHttpCache) {
        this.isHttpCache = isHttpCache;
        return this;
    }

    /**
     * 设置HTTP缓存路径
     *
     * @param httpCacheDirectory
     * @return
     */
    public AppClient setHttpCacheDirectory(File httpCacheDirectory) {
        this.httpCacheDirectory = httpCacheDirectory;
        return this;
    }

    /**
     * 设置HTTP缓存
     *
     * @param httpCache
     * @return
     */
    public AppClient httpCache(Cache httpCache) {
        this.httpCache = httpCache;
        return this;
    }

    /**
     * 设置是否添加Cookie
     *
     * @param isCookie
     * @return
     */
    public AppClient setCookie(boolean isCookie) {
        this.isCookie = isCookie;
        return this;
    }

    /**
     * 设置Cookie管理
     *
     * @param cookie
     * @return
     */
    public AppClient apiCookie(ApiCookie cookie) {
        this.apiCookie = checkNotNull(cookie, "cookieManager == null");
        return this;
    }

    /**
     * 设置请求baseUrl
     *
     * @param baseUrl
     * @return
     */
    public AppClient baseUrl(String baseUrl) {
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
    public AppClient retryDelayMillis(int retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
        return this;
    }

    /**
     * 设置请求失败重试次数
     *
     * @param retryCount
     * @return
     */
    public AppClient retryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    /**
     * 设置代理
     *
     * @param proxy
     * @return
     */
    public AppClient proxy(Proxy proxy) {
        okHttpBuilder.proxy(checkNotNull(proxy, "proxy == null"));
        return this;
    }

    /**
     * 设置连接超时时间（秒）
     *
     * @param timeout
     * @return
     */
    public AppClient connectTimeout(int timeout) {
        return connectTimeout(timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置读取超时时间（秒）
     *
     * @param timeout
     * @return
     */
    public AppClient readTimeout(int timeout) {
        return readTimeout(timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置写入超时时间（秒）
     *
     * @param timeout
     * @return
     */
    public AppClient writeTimeout(int timeout) {
        return writeTimeout(timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置连接超时时间
     *
     * @param timeout
     * @param unit
     * @return
     */
    public AppClient connectTimeout(int timeout, TimeUnit unit) {
        if (timeout > -1) {
            okHttpBuilder.connectTimeout(timeout, unit);
        } else {
            okHttpBuilder.connectTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
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
    public AppClient writeTimeout(int timeout, TimeUnit unit) {
        if (timeout > -1) {
            okHttpBuilder.writeTimeout(timeout, unit);
        } else {
            okHttpBuilder.writeTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
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
    public AppClient readTimeout(int timeout, TimeUnit unit) {
        if (timeout > -1) {
            okHttpBuilder.readTimeout(timeout, unit);
        } else {
            okHttpBuilder.readTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
        return this;
    }

    /**
     * 设置拦截器
     *
     * @param interceptor
     * @return
     */
    public AppClient interceptor(Interceptor interceptor) {
        okHttpBuilder.addInterceptor(checkNotNull(interceptor, "interceptor == null"));
        return this;
    }

    /**
     * 设置网络拦截器
     *
     * @param interceptor
     * @return
     */
    public AppClient networkInterceptor(Interceptor interceptor) {
        okHttpBuilder.addNetworkInterceptor(checkNotNull(interceptor, "interceptor == null"));
        return this;
    }

    /**
     * 使用POST方式是否需要进行GZIP压缩，服务器不支持则不设置
     *
     * @return
     */
    public AppClient postGzipInterceptor() {
        interceptor(new GzipRequestInterceptor());
        return this;
    }

    /**
     * 设置在线缓存，主要针对网路请求过程进行缓存
     *
     * @param httpCache
     * @return
     */
    public AppClient cacheOnline(Cache httpCache) {
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
    public AppClient cacheOnline(Cache httpCache, final int cacheControlValue) {
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
    public AppClient cacheOffline(Cache httpCache) {
        networkInterceptor(new OfflineCacheInterceptor(mContext));
        interceptor(new OfflineCacheInterceptor(mContext));
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
    public AppClient cacheOffline(Cache httpCache, final int cacheControlValue) {
        networkInterceptor(new OfflineCacheInterceptor(mContext, cacheControlValue));
        interceptor(new OfflineCacheInterceptor(mContext));
        this.httpCache = httpCache;
        return this;
    }

    public CallAdapter.Factory getCallAdapterFactory() {
        return callAdapterFactory;
    }

    public Converter.Factory getConverterFactory() {
        return converterFactory;
    }

    public Call.Factory getCallFactory() {
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

    public Map<String, String> getParams() {
        return glabalParams;
    }


    public AppClient setParams(Map<String, String> params) {
        this.glabalParams = params;
        return this;
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


    /**
     * 添加请求订阅者
     *
     * @param tag
     * @param disposable
     */
    public void addDisposable(Object tag, Disposable disposable) {
        ApiManager.get().add(tag, disposable);
    }

    /**
     * 根据Tag取消请求
     */
    public void cancelTag(Object tag) {
        ApiManager.get().cancel(tag);
    }

    /**
     * 取消所有请求请求
     */
    public void cancelAll() {
        ApiManager.get().cancelAll();
    }

    /**
     * 清除对应Key的缓存
     *
     * @param key
     */
    public void removeCache(String key) {
        apiCacheBuilder.build().remove(key);
    }

    /**
     * 清楚所有缓存并关闭缓存
     *
     * @return
     */
    public Disposable clearCache() {
        return apiCacheBuilder.build().clear();
    }


    private void saveFile(FlowableEmitter<? super DownProgress> sub, File saveFile, ResponseBody resp) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            try {
                int readLen;
                int downloadSize = 0;
                byte[] buffer = new byte[8192];

                DownProgress downProgress = new DownProgress();
                inputStream = resp.byteStream();
                outputStream = new FileOutputStream(saveFile);

                long contentLength = resp.contentLength();
                downProgress.setTotalSize(contentLength);

                while ((readLen = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readLen);
                    downloadSize += readLen;
                    downProgress.setDownloadSize(downloadSize);
                    sub.onNext(downProgress);
                }
                outputStream.flush();
                sub.onComplete();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (resp != null) {
                    resp.close();
                }
            }
        } catch (IOException e) {
            sub.onError(e);
        }
    }

    private File getDiskCacheDir(String dirName) {
        String rootName = getDiskCachePath(mContext);
        return new File(rootName + File.separator + dirName);
    }

    private String getDiskCachePath(Context context) {
        String cachePath;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
}

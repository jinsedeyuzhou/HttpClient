package com.ebrightmoon.http.restrofit;


import android.content.Context;
import android.text.TextUtils;


import com.ebrightmoon.http.api.ApiService;
import com.ebrightmoon.http.body.UploadProgressRequestBody;
import com.ebrightmoon.http.callback.ACallback;
import com.ebrightmoon.http.callback.UCallback;
import com.ebrightmoon.http.common.AppConfig;
import com.ebrightmoon.http.common.HttpGlobalConfig;
import com.ebrightmoon.http.common.HttpUtils;
import com.ebrightmoon.http.common.RequestOptions;
import com.ebrightmoon.http.core.ApiCache;
import com.ebrightmoon.http.core.ApiCookie;
import com.ebrightmoon.http.core.ApiManager;
import com.ebrightmoon.http.core.ApiTransformer;
import com.ebrightmoon.http.func.ApiDownloadFunc;
import com.ebrightmoon.http.func.ApiResultFunc;
import com.ebrightmoon.http.func.ApiRetryFunc;
import com.ebrightmoon.http.interceptor.HeadersInterceptor;
import com.ebrightmoon.http.interceptor.HttpResponseInterceptor;
import com.ebrightmoon.http.interceptor.LoggingInterceptor;
import com.ebrightmoon.http.interceptor.OfflineCacheInterceptor;
import com.ebrightmoon.http.interceptor.OnlineCacheInterceptor;
import com.ebrightmoon.http.interceptor.UploadProgressInterceptor;
import com.ebrightmoon.http.mode.ApiHost;
import com.ebrightmoon.http.mode.MediaTypes;
import com.ebrightmoon.http.response.ResponseResult;
import com.ebrightmoon.http.subscriber.ApiCallbackSubscriber;
import com.ebrightmoon.http.subscriber.DownCallbackSubscriber;
import com.ebrightmoon.http.util.GsonUtil;
import com.ebrightmoon.http.util.SSL;
import com.ebrightmoon.http.util.SSLUtils;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by wyy on 2017/9/6.
 * 利用Recycle 管理生命周期  利用返回DisposableObserver 和RxApiManager，取消网络请求
 */

public class AppClient {
    private List<MultipartBody.Part> multipartBodyParts;
    private Map<String, RequestBody> params;
    private OkHttpClient.Builder okHttpBuilder;
    private Retrofit retrofit;
    private Context mContext;
    private ApiService apiService;
    private Retrofit.Builder retrofitBuilder;
    private ApiCache.Builder apiCacheBuilder;
    private HttpGlobalConfig httpConfig;

    private Map<String, String> headers = new LinkedHashMap<>();//请求头


    private static AppClient instance;

    private AppClient(Context appContext) {
        if (mContext == null && appContext != null) {
            mContext = appContext.getApplicationContext();
            okHttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            apiCacheBuilder = new ApiCache.Builder(mContext);
        }
    }

    public static AppClient getInstance(Context context) {
        if (instance == null) {
            synchronized (AppClient.class) {
                if (instance == null) {
                    instance = new AppClient(context);
                }
            }
        }

        return instance;
    }

    public static AppClient getInstance() {
        return getInstance(null);
    }


    public OkHttpClient.Builder getOkHttpBuilder() {
        return okHttpBuilder;
    }

    public void setOkHttpBuilder(OkHttpClient.Builder okHttpBuilder) {
        this.okHttpBuilder = okHttpBuilder;
    }

    public Retrofit.Builder getRetrofitBuilder() {
        return retrofitBuilder;
    }

    public void setRetrofitBuilder(Retrofit.Builder retrofitBuilder) {
        this.retrofitBuilder = retrofitBuilder;
    }

    public ApiService CreateApiService() {
        return apiService = create(ApiService.class);
    }


    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    /**
     * 初始化全局参数
     */
    private void initGlobalConfig() {
        httpConfig = HttpGlobalConfig.getInstance();

        if (httpConfig.getBaseUrl() == null) {
            httpConfig.baseUrl(ApiHost.getHost());
        }
        retrofitBuilder.baseUrl(httpConfig.getBaseUrl());

        if (httpConfig.getConverterFactory() == null) {
            httpConfig.converterFactory(GsonConverterFactory.create());
        }
        retrofitBuilder.addConverterFactory(httpConfig.getConverterFactory());

        if (httpConfig.getCallAdapterFactory() == null) {
            httpConfig.callAdapterFactory(RxJava2CallAdapterFactory.create());
        }
        retrofitBuilder.addCallAdapterFactory(httpConfig.getCallAdapterFactory());

        if (httpConfig.getCallFactory() != null) {
            retrofitBuilder.callFactory(httpConfig.getCallFactory());
        }

        if (httpConfig.getHostnameVerifier() == null) {
            httpConfig.hostnameVerifier(new SSL.UnSafeHostnameVerifier(httpConfig.getBaseUrl()));
        }
        okHttpBuilder.hostnameVerifier(httpConfig.getHostnameVerifier());

        if (httpConfig.getSslSocketFactory() == null) {
            httpConfig.SSLSocketFactory(SSL.getSslSocketFactory(null, null, null));
        }
        okHttpBuilder.sslSocketFactory(httpConfig.getSslSocketFactory());

        if (httpConfig.getConnectionPool() == null) {
            httpConfig.connectionPool(new ConnectionPool(AppConfig.DEFAULT_MAX_IDLE_CONNECTIONS,
                    AppConfig.DEFAULT_KEEP_ALIVE_DURATION, TimeUnit.SECONDS));
        }
        okHttpBuilder.connectionPool(httpConfig.getConnectionPool());

        if (httpConfig.isCookie() && httpConfig.getApiCookie() == null) {
            httpConfig.apiCookie(new ApiCookie(mContext));
        }
        if (httpConfig.isCookie()) {
            okHttpBuilder.cookieJar(httpConfig.getApiCookie());
        }

        if (httpConfig.getHttpCacheDirectory() == null) {
            httpConfig.setHttpCacheDirectory(new File(mContext.getCacheDir(), AppConfig.CACHE_HTTP_DIR));
        }
        if (httpConfig.isHttpCache()) {
            try {
                if (httpConfig.getHttpCache() == null) {
                    httpConfig.httpCache(new Cache(httpConfig.getHttpCacheDirectory(), AppConfig.CACHE_MAX_SIZE));
                }
                okHttpBuilder.addNetworkInterceptor(new OnlineCacheInterceptor());
                if (mContext != null)
                    okHttpBuilder.addNetworkInterceptor(new OfflineCacheInterceptor(mContext));
            } catch (Exception e) {
            }
        }
        if (httpConfig.getHttpCache() != null) {
            okHttpBuilder.cache(httpConfig.getHttpCache());
        }
        okHttpBuilder.connectTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(AppConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * @param requestOptions
     */
    private void initLocalConfig(RequestOptions requestOptions) {
        if (requestOptions != null) {
            if (requestOptions.getHeaders() != null) {
                headers.putAll(requestOptions.getHeaders());
            }

            if (!requestOptions.getInterceptors().isEmpty()) {
                List<Interceptor> interceptors = requestOptions.getInterceptors();
                for (Interceptor interceptor : interceptors) {
                    okHttpBuilder.addInterceptor(interceptor);
                }
            }

            if (!requestOptions.getNetworkInterceptors().isEmpty()) {
                List<Interceptor> interceptors = requestOptions.getInterceptors();
                for (Interceptor interceptor : interceptors) {
                    okHttpBuilder.addNetworkInterceptor(interceptor);
                }
            }

            if (headers.size() > 0) {
                okHttpBuilder.addInterceptor(new HeadersInterceptor(headers));
            }

            if (requestOptions.getUploadCallback() != null) {
                okHttpBuilder.addNetworkInterceptor(new UploadProgressInterceptor(requestOptions.getUploadCallback()));
            }

            if (requestOptions.getReadTimeOut() > 0) {
                okHttpBuilder.readTimeout(requestOptions.getReadTimeOut(), TimeUnit.SECONDS);
            }

            if (requestOptions.getWriteTimeOut() > 0) {
                okHttpBuilder.readTimeout(requestOptions.getWriteTimeOut(), TimeUnit.SECONDS);
            }

            if (requestOptions.getConnectTimeOut() > 0) {
                okHttpBuilder.readTimeout(requestOptions.getConnectTimeOut(), TimeUnit.SECONDS);
            }

            if (requestOptions.isHttpCache()) {
                try {
                    if (httpConfig.getHttpCache() == null) {
                        httpConfig.httpCache(new Cache(httpConfig.getHttpCacheDirectory(), AppConfig.CACHE_MAX_SIZE));
                    }
                    okHttpBuilder.addNetworkInterceptor(new OnlineCacheInterceptor());
                    if (mContext != null) {
                        okHttpBuilder.addNetworkInterceptor(new OfflineCacheInterceptor(mContext));
                    }
                } catch (Exception e) {
                }
                okHttpBuilder.cache(httpConfig.getHttpCache());
            }

            if (requestOptions.getBaseUrl() != null) {
                Retrofit.Builder newRetrofitBuilder = new Retrofit.Builder();
                newRetrofitBuilder.baseUrl(requestOptions.getBaseUrl());
                if (httpConfig.getConverterFactory() != null) {
                    newRetrofitBuilder.addConverterFactory(httpConfig.getConverterFactory());
                }
                if (httpConfig.getCallAdapterFactory() != null) {
                    newRetrofitBuilder.addCallAdapterFactory(httpConfig.getCallAdapterFactory());
                }
                if (httpConfig.getCallFactory() != null) {
                    newRetrofitBuilder.callFactory(httpConfig.getCallFactory());
                }
                okHttpBuilder.hostnameVerifier(new SSL.UnSafeHostnameVerifier(requestOptions.getBaseUrl()));
                newRetrofitBuilder.client(okHttpBuilder.build());
                retrofit = newRetrofitBuilder.build();
            }else
            {
                retrofitBuilder.client(okHttpBuilder.build());
                retrofit = retrofitBuilder.build();
            }
        } else {
            retrofitBuilder.client(okHttpBuilder.build());
            retrofit = retrofitBuilder.build();
        }

    }


    public <T> void get(String url, RequestOptions requestOptions, ACallback<T> callback) {
        initGlobalConfig();
        initLocalConfig(requestOptions);
    }

    /**
     * Api通用Get  返回模型中data数据
     *
     * @param url
     * @param params
     * @param callback
     * @param <T>
     */
    public <T> DisposableObserver get(String url, Map<String, String> params, ACallback<T> callback) {
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        ApiManager.get().add(url, disposableObserver);
        CreateApiService().get(url, params, params)
                .map(new ApiResultFunc<T>(getSubType(callback)))
                .compose(ApiTransformer.<T>apiTransformer())
                .subscribe(disposableObserver);

        return disposableObserver;

    }

    /**
     * Api通用post  返回模型中T  data数据
     *
     * @param url
     * @param params
     * @param callback
     * @param <T>
     */
    public <T> DisposableObserver post(String url, Map<String, String> params, ACallback<T> callback) {
        RequestBody body = RequestBody.create(MediaTypes.APPLICATION_JSON_TYPE, GsonUtil.gson().toJson(params));
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        ApiManager.get().add(url, disposableObserver);
        CreateApiService().post(url, params, body)
                .map(new ApiResultFunc<T>(getSubType(callback)))
                .compose(ApiTransformer.<T>apiTransformer())
                .subscribe(disposableObserver);

        return disposableObserver;
    }

    public <T> T execute(Observable<T> observable, ACallback<T> callback) {
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        observable.compose(ApiTransformer.<T>norTransformer())
                .subscribe(disposableObserver);
        return null;
    }


    /**
     * Api通用put  返回模型中 T data数据
     *
     * @param url
     * @param params
     * @param callback
     * @param <T>
     */
    public <T> DisposableObserver put(String url, Map<String, String> params, ACallback<T> callback) {
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        ApiManager.get().add(url, disposableObserver);
        CreateApiService().put(url, params, params)
                .map(new ApiResultFunc<T>(getSubType(callback)))
                .compose(ApiTransformer.<T>apiTransformer())
                .subscribe(disposableObserver);

        return disposableObserver;
    }


    /**
     * 上传文件不带参数校验 返回数据 可以控制文件上传进度
     *
     * @param url
     * @param <T>
     */
    public <T> void uploadFiles(String url, ACallback<T> callback) {
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        ApiManager.get().add(url, disposableObserver);
        CreateApiService().uploadFiles(url, multipartBodyParts)
                .compose(ApiTransformer.<T>Transformer(getSubType(callback)))
                .subscribe(disposableObserver);
    }


    /**
     * 上传文件不带参数校验  返回数据模型 ResponseResult中 T数据
     *
     * @param url
     * @param <T>
     */
    public <T> void uploadFilesV(String url, ACallback<T> callback) {
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        ApiManager.get().add(url, disposableObserver);
        CreateApiService().uploadFiles(url, multipartBodyParts)
                .map(new ApiResultFunc<T>(getSubType(callback)))
                .compose(ApiTransformer.<T>apiTransformer())
                .subscribe(disposableObserver);

    }


    /**
     * 上传文件 无头文件 返回 返回ResponseResult  数据 不能监控文件上传进度
     *
     * @param url
     * @param <T>
     */
    public <T> void uploadFiles(String url, Map<String, File> files, ACallback<T> callback) {
        params = new HashMap<>();
        for (Map.Entry<String, File> entry : files.entrySet()) {
            RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_FORM_URLENCODED_TYPE, entry.getValue());
            params.put("file\"; filename=\"" + entry.getValue() + "", requestBody);
        }
        DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
        ApiManager.get().add(url, disposableObserver);
        CreateApiService().uploadFiles(url, params)
                .map(new ApiResultFunc<T>(getSubType(callback)))
                .compose(ApiTransformer.<T>apiTransformer())
                .subscribe(disposableObserver);


    }

    /**
     * 下载文件
     *
     * @param url
     * @param params
     * @param fileName
     * @param context
     * @param callback
     */
    public <T> void downloadFile(String url, Map<String, String> params, String fileName, Context context, ACallback<T> callback) {
        DisposableObserver disposableObserver = new DownCallbackSubscriber(callback);
        ApiManager.get().add(url, disposableObserver);
        CreateApiService().downFile(url, params)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .toFlowable(BackpressureStrategy.LATEST)
                .flatMap(new ApiDownloadFunc(context, fileName))
                .sample(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .retryWhen(new ApiRetryFunc(0, 60))
                .subscribe(disposableObserver);


    }

    /**
     * 下载文件
     *
     * @param url
     * @param params
     */
    public <T> void downloadFile(String url, Map<String, String> params, Context context, ACallback<T> callback) {
        DisposableObserver disposableObserver = new DownCallbackSubscriber(callback);
        ApiManager.get().add(url, disposableObserver);
        CreateApiService().downFile(url, params)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .toFlowable(BackpressureStrategy.LATEST)
                .flatMap(new ApiDownloadFunc(context))
                .sample(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .retryWhen(new ApiRetryFunc(0, 60))
                .subscribe(disposableObserver);


    }


    /**
     * 下载文件
     *
     * @param url
     * @param xml
     */
    public <T> void downloadFile(String url, String xml, String fileName, Context context, ACallback<T> callback) {
        RequestBody body = RequestBody.create(MediaTypes.APPLICATION_XML_TYPE, xml);
        DisposableObserver disposableObserver = new DownCallbackSubscriber(callback);
        ApiManager.get().add(url, disposableObserver);
        CreateApiService().downFile(url, body)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .toFlowable(BackpressureStrategy.LATEST)
                .flatMap(new ApiDownloadFunc(context, fileName))
                .sample(1, TimeUnit.SECONDS)  //如果碰见小文件一秒之内下载完成会看不到进度，可以注掉也可以改变发送次数
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .retryWhen(new ApiRetryFunc(0, 60))
                .subscribe(disposableObserver);


    }


    /**
     * @param fileMap
     * @return
     */
    public AppClient addFiles(Map<String, File> fileMap) {
        if (fileMap == null) {
            return this;
        }
        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
            addFile(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * @param key
     * @param file
     * @return
     */
    public AppClient addFile(String key, File file) {
        return addFile(key, file, null);
    }

    /**
     * 返回ResponseResult  数据
     *
     * @param key
     * @param file
     * @param callback
     * @return
     */
    public AppClient addFile(String key, File file, UCallback callback) {
        if (key == null || file == null) {
            return this;
        }
        if (multipartBodyParts == null) {
            multipartBodyParts = new ArrayList<>();
        }
        RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM_TYPE, file);
        if (callback != null) {
            UploadProgressRequestBody uploadProgressRequestBody = new UploadProgressRequestBody(requestBody, callback);
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), uploadProgressRequestBody);
            multipartBodyParts.add(part);
        } else {
            MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), requestBody);
            multipartBodyParts.add(part);
        }
        return this;
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

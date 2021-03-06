package com.ebrightmoon.http.core;



import com.ebrightmoon.http.common.AppConfig;
import com.ebrightmoon.http.func.ApiDataFunc;
import com.ebrightmoon.http.func.ApiFunc;
import com.ebrightmoon.http.func.ApiResultFunc;
import com.ebrightmoon.http.func.ApiRetryFunc;
import com.ebrightmoon.http.response.ResponseResult;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


/**
 * 根据不同返回数据要求定制相关转换方法
 */
public class ApiTransformer {

    private ApiTransformer() {
        /** cannot be instantiated **/
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static <T> ObservableTransformer<ResponseResult<T>, T> apiTransformer() {
        return new ObservableTransformer<ResponseResult<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<ResponseResult<T>> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new ApiDataFunc<T>())
                        .retryWhen(new ApiRetryFunc(AppConfig.DEFAULT_RETRY_COUNT,
                                AppConfig.DEFAULT_RETRY_DELAY_MILLIS));
            }
        };
    }


    public static  <T> ObservableTransformer<ResponseBody, T> norTransformer(final Type type,final  int retryCount,final int retryDelayMillis ) {
        return new ObservableTransformer<ResponseBody, T>() {
            @Override
            public ObservableSource<T> apply(Observable<ResponseBody> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .map(new ApiFunc<T>(type))
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(new ApiRetryFunc(retryCount, retryDelayMillis));
            }
        };
    }

    public static <T> ObservableTransformer<T, T> norTransformer() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(new ApiRetryFunc(AppConfig.DEFAULT_RETRY_COUNT,
                                AppConfig.DEFAULT_RETRY_DELAY_MILLIS));
            }
        };
    }


//    /**
//     * 未测试暂时不要用
//     * @param retryCount
//     * @param retryDelayMillis
//     * @param event
//     * @param <T>
//     * @return
//     */
//    public static <T> ObservableTransformer<T, T> norTransformer(final int retryCount, final int retryDelayMillis,final ActivityLifeCycleEvent event) {
//        return new ObservableTransformer<T, T>() {
//            @Override
//            public ObservableSource<T> apply(Observable<T> apiResultObservable) {
//                ObservableSource<ActivityLifeCycleEvent> compareLifecycleObservable =
//                        RecycleBaseActivity.lifecycleSubject.filter(new Predicate<ActivityLifeCycleEvent>() {
//                            @Override
//                            public boolean test(ActivityLifeCycleEvent activityLifeCycleEvent) throws Exception {
//                                return activityLifeCycleEvent.equals(event);
//                            }
//                        }).firstElement().toObservable().publish();
//                return apiResultObservable
//                        .takeUntil(compareLifecycleObservable)
//                        .subscribeOn(Schedulers.io())
//                        .unsubscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .retryWhen(new ApiRetryFunc(retryCount, retryDelayMillis));
//            }
//        };
//    }
//

    public static  <T> ObservableTransformer<ResponseBody, T> Transformer(final Type type) {
        return new ObservableTransformer<ResponseBody, T>() {
            @Override
            public ObservableSource<T> apply(Observable<ResponseBody> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new ApiFunc<T>(type))
                        .retryWhen(new ApiRetryFunc(AppConfig.DEFAULT_RETRY_COUNT,
                                AppConfig.DEFAULT_RETRY_DELAY_MILLIS));
            }
        };
    }


//
//    public static <T> ObservableTransformer<T, T> bindUntilEvent(@NonNull final ActivityLifeCycleEvent event) {
//        return new ObservableTransformer<T, T>() {
//            @Override
//            public ObservableSource<T> apply(Observable<T> upstream) {
//                Observable<ActivityLifeCycleEvent> compareLifecycleObservable =
//                        RecycleBaseActivity.lifecycleSubject.filter(new Predicate<ActivityLifeCycleEvent>() {
//                            @Override
//                            public boolean test(ActivityLifeCycleEvent activityLifeCycleEvent) throws Exception {
//                                return activityLifeCycleEvent.equals(event);
//                            }
//                        }).firstElement().toObservable().publish();
//
//
//                return upstream.takeUntil(compareLifecycleObservable);
//            }
//
//
//        };
//    }


    public static  <T> ObservableTransformer<ResponseBody, ResponseResult<T>> RRTransformer(final Type type) {
        return new ObservableTransformer<ResponseBody, ResponseResult<T>>() {
            @Override
            public ObservableSource<ResponseResult<T>> apply(Observable<ResponseBody> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new ApiResultFunc<T>(type))
                        .retryWhen(new ApiRetryFunc(AppConfig.DEFAULT_RETRY_COUNT,
                                AppConfig.DEFAULT_RETRY_DELAY_MILLIS));
            }
        };
    }



    public  static  <T> ObservableTransformer<T,T> downTransformer(Type type) {
        return new ObservableTransformer<T,T>(){

            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
              return   upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .toFlowable(BackpressureStrategy.LATEST)
                        .sample(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .toObservable()
                        .retryWhen(new ApiRetryFunc(AppConfig.DEFAULT_RETRY_COUNT,
                                AppConfig.DEFAULT_RETRY_DELAY_MILLIS));
            }
        };

    }









}

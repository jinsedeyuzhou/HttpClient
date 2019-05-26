package com.ebrightmoon.http.request;


import com.ebrightmoon.http.func.ApiDataFunc;
import com.ebrightmoon.http.func.ApiRetryFunc;
import com.ebrightmoon.http.response.ResponseResult;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @Description: 返回APIResult的基础请求类
 * @author:
 * @date: 17/5/28 15:46.
 */
public abstract class ApiBaseRequest<R extends ApiBaseRequest> extends BaseHttpRequest<R> {
    public ApiBaseRequest(String suffixUrl) {
        super(suffixUrl);
    }

    protected <T> ObservableTransformer<ResponseResult<T>, T> apiTransformer() {
        return new ObservableTransformer<ResponseResult<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<ResponseResult<T>> apiResultObservable) {
                return apiResultObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .map(new ApiDataFunc<T>())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retryWhen(new ApiRetryFunc(retryCount, retryDelayMillis));
            }
        };
    }
}

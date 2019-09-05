package com.ebrightmoon.http.request;


import com.ebrightmoon.http.callback.ACallback;
import com.ebrightmoon.http.core.ApiManager;
import com.ebrightmoon.http.mode.CacheResult;
import com.ebrightmoon.http.restrofit.HttpClient;
import com.ebrightmoon.http.subscriber.ApiCallbackSubscriber;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 *  Patch请求
 * @author:
 * @date: 17/5/14 20:29.
 */
public class PatchRequest extends BaseHttpRequest<PatchRequest> {
    public PatchRequest(String suffixUrl) {
        super(suffixUrl);
    }

    @Override
    protected <T> Observable<T> execute(Type type) {
        return apiService.patch(suffixUrl, params).compose(this.<T>norTransformer(type));
    }

    @Override
    protected <T> Observable<CacheResult<T>> cacheExecute(Type type) {
        return this.<T>execute(type).compose(HttpClient.getApiCache().<T>transformer(cacheMode, type));
    }

    @Override
    protected <T> void execute(ACallback<T> callback) {
        DisposableObserver disposableObserver = new ApiCallbackSubscriber(callback);
        if (super.tag != null) {
            ApiManager.get().add(super.tag, disposableObserver);
        }
        if (isLocalCache) {
            this.cacheExecute(getSubType(callback)).subscribe(disposableObserver);
        } else {
            this.execute(getType(callback)).subscribe(disposableObserver);
        }
    }
}

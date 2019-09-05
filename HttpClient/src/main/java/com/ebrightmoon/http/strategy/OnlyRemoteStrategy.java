package com.ebrightmoon.http.strategy;


import com.ebrightmoon.http.core.ApiCache;
import com.ebrightmoon.http.mode.CacheResult;

import java.lang.reflect.Type;

import io.reactivex.Observable;

/**
 *  缓存策略--只取网络
 */
public class OnlyRemoteStrategy<T> extends CacheStrategy<T> {
    @Override
    public <T> Observable<CacheResult<T>> execute(ApiCache apiCache, String cacheKey, Observable<T> source, Type type) {
        return loadRemote(apiCache, cacheKey, source);
    }
}

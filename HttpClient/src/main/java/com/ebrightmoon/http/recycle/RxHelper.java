package com.ebrightmoon.http.recycle;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.BehaviorSubject;

/**
 * 作者：create by  Administrator on 2018/9/26
 * 邮箱：
 *
 *     public <T> void get(String url, Map<String, String> params, ACallback<T> callback) {
 DisposableObserver disposableObserver = new ApiCallbackSubscriber<T>(callback);
 CreateApiService().get(url, params)
 .compose(ApiTransformer.<T>Transformer(getSubType(callback)))
 //                .compose(RxHelper.<T>bindUntilEvent(recycle))
 .subscribe(disposableObserver);
 }
 */
public class RxHelper {


    /**
     * 利用Observable.takeUntil()停止网络请求
     *
     * @param event
     * @param lifecycleSubject
     * @param <T>
     * @return
     */
    @NonNull
    public static <T> ObservableTransformer<T, T> bindUntilEvent(@NonNull final ActivityLifeCycleEvent event, BehaviorSubject<ActivityLifeCycleEvent> lifecycleSubject) {
        final Observable<ActivityLifeCycleEvent> compareLifecycleObservable =
                lifecycleSubject.filter(new Predicate<ActivityLifeCycleEvent>() {
                    @Override
                    public boolean test(ActivityLifeCycleEvent activityLifeCycleEvent) throws Exception {
                        return event.equals(activityLifeCycleEvent);
                    }
                }).take(1);

        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.takeUntil(compareLifecycleObservable);
            }
        };


    }


}

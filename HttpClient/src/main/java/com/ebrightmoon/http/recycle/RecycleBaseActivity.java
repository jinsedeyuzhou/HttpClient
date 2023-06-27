package com.ebrightmoon.http.recycle;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import io.reactivex.subjects.BehaviorSubject;


/**
 * Created by wyy on 2016/9/11.
 * 所有模块通用的可以在这里面设置
 */
public abstract  class RecycleBaseActivity extends AppCompatActivity implements View.OnClickListener{

    protected BehaviorSubject<ActivityLifeCycleEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityLifeCycleEvent.CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityLifeCycleEvent.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityLifeCycleEvent.RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lifecycleSubject.onNext(ActivityLifeCycleEvent.PAUSE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(ActivityLifeCycleEvent.DESTROY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lifecycleSubject.onNext(ActivityLifeCycleEvent.STOP);
    }





}

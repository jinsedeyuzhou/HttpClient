package com.ebrightmoon.demo.http;

/**
 *  请求接口回调
 */
public abstract class Callback<T> {
    public abstract void onSuccess(T data);
    public abstract void onFail(int errCode, String errMsg);
}

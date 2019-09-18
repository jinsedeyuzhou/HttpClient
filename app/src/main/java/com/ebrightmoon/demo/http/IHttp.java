package com.ebrightmoon.demo.http;

import android.content.Context;

/**
 * Time: 2019-09-18
 * Author:wyy
 * Description:
 */
public interface IHttp {

   <T> void get(Context context, RequestParams requestParams,Callback<T> callback);

    <T>  void post(Context context, RequestParams requestParams,Callback<T> callback);

    <T>   void download(Context context, RequestParams requestParams,Callback<T> callback);

    <T>  void upload(Context context, RequestParams requestParams,Callback<T> callback);

}

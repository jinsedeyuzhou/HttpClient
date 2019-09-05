package com.ebrightmoon.demo;

import android.app.Application;

import com.ebrightmoon.http.common.GlobalParams;
import com.ebrightmoon.http.common.HttpApp;
import com.ebrightmoon.http.mode.ApiHost;
import com.ebrightmoon.http.restrofit.HttpClient;

/**
 * Time: 2019/5/26
 * Author:wyy
 * Description:
 */
public class MyApplication extends HttpApp {

    @Override
    public void onCreate() {
        super.onCreate();

        GlobalParams.getInstance().baseUrl(ApiHost.getHost());
        HttpClient.init(this);

    }
}

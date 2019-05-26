package com.ebrightmoon.demo;

import android.app.Application;

import com.ebrightmoon.http.common.HttpGlobalConfig;
import com.ebrightmoon.http.mode.ApiCode;
import com.ebrightmoon.http.mode.ApiHost;
import com.ebrightmoon.http.restrofit.HttpClient;

/**
 * Time: 2019/5/26
 * Author:wyy
 * Description:
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        HttpGlobalConfig.getInstance().baseUrl(ApiHost.getHost());
        HttpClient.init(this);

    }
}

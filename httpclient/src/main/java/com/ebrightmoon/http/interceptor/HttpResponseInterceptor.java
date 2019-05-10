package com.ebrightmoon.http.interceptor;

import android.support.annotation.NonNull;

import com.ebrightmoon.retrofitrx.temp.DefaultResponseState;
import com.ebrightmoon.retrofitrx.temp.IResponseState;
import com.ebrightmoon.retrofitrx.temp.Utils;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Http响应拦截
 */
public class HttpResponseInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private IResponseState responseState;

    public HttpResponseInterceptor() {
        this(new DefaultResponseState());
    }

    public HttpResponseInterceptor(IResponseState responseState) {
        this.responseState = responseState;
        Utils.checkIllegalArgument(responseState, "this responseState is null.");
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        return process(chain);
    }

    private Response process(Chain chain) throws IOException {


        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.code() == 406) {
            return response.newBuilder().code(200)
                    .header("Cache-Control", "max-age=60")
                    .build();
        }
        return response;
    }

}

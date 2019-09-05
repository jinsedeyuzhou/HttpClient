package com.ebrightmoon.http.interceptor;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

import static com.squareup.okhttp.internal.Internal.logger;


/**
 * Created by Administrator on 2017/11/17.
 */

public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        if (request.body() == null) {
            Logger.e(String.format("Sending request %s on %s%n%s%n%s",
                    request.url(), chain.connection(), request.headers(), request.body()));
        } else {
            Logger.e(String.format("Sending request %s on %s%n%s%n%s",
                    request.url(), chain.connection(), request.headers(), getParam(request.body())));
        }
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        logger.info(String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        ResponseBody responseBody = response.peekBody(1024 * 1024);
        Logger.e(String.format("Received Data: [%s] %njson:%s %n耗时: %.1fms%n%s",
                response.request().url(),
                responseBody.string(),
                (t2 - t1) / 1e6d,
                response.headers()));
        Logger.json(responseBody.string());
        return response;
    }

    /**
     * 读取参数
     *
     * @param requestBody
     * @return
     */
    private String getParam(RequestBody requestBody) {
        Buffer buffer = new Buffer();
        String logparm;
        try {
            requestBody.writeTo(buffer);
            logparm = buffer.readUtf8();
            String s = logparm
                    .replaceAll("%(?![0-9a-fA-F]{2})", "%25")
                    .replaceAll("\\+", "%2B");
            logparm = URLDecoder.decode(s, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return logparm;
    }
}

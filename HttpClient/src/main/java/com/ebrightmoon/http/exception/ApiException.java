package com.ebrightmoon.http.exception;

import android.net.ParseException;

import com.ebrightmoon.http.mode.ApiCode;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;


public class ApiException extends Exception {

    private final int code;
    private String message;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ApiException setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getDisplayMessage() {
        return message + "(code:" + code + ")";
    }

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ApiException(e, ApiCode.Request.HTTP_ERROR);
            switch (httpException.code()) {
                case ApiCode.Http.UNAUTHORIZED:
                case ApiCode.Http.FORBIDDEN:
                case ApiCode.Http.NOT_FOUND:
                case ApiCode.Http.REQUEST_TIMEOUT:
                case ApiCode.Http.GATEWAY_TIMEOUT:
                case ApiCode.Http.INTERNAL_SERVER_ERROR:
                case ApiCode.Http.BAD_GATEWAY:
                case ApiCode.Http.SERVICE_UNAVAILABLE:
                default:
                    ex.message = "NETWORK_ERROR";
                    break;
            }
            return ex;
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            ex = new ApiException(e, ApiCode.Request.PARSE_ERROR);
            ex.message = "PARSE_ERROR";
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException(e, ApiCode.Request.NETWORK_ERROR);
            ex.message = "NETWORK_ERROR";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(e, ApiCode.Request.SSL_ERROR);
            ex.message = "SSL_ERROR";
            return ex;
        } else if (e instanceof SocketTimeoutException) {
            ex = new ApiException(e, ApiCode.Request.TIMEOUT_ERROR);
            ex.message = "TIMEOUT_ERROR";
            return ex;
        }else if (e instanceof  ApiException)
        {
            ex = (ApiException) e;
            return ex;
        }
        else {
            ex = new ApiException(e, ApiCode.Request.UNKNOWN);
            ex.message = "UNKNOWN";
            return ex;
        }
    }


    /**
     * //条件不符合
     * public static final int NOT_CONDITION = 299;
     * //操作失败
     * public static final int FAILED_OPERATE = 298;
     * //账号或密码不正确
     * public static final int LOGIN_FAILED = 297;
     * //手势密码不正确
     * public static final int LOGIN_GESTURE = 296;
     * // 账号已经退出登录
     * public static final int LOGIN_EXIT = 295;
     * //  在其他设备登录
     * public static final int OTHER_LOGIN = 294;
     * //登录过期
     * public static final int LOGIN_TIME_OUT = 293;
     *
     * @param code
     * @return
     */
    public static ApiException newApiException(int code, String msg) {
        ApiException apiException = new ApiException(new Throwable(), code);
        apiException.message=msg;
        return apiException;
    }
}

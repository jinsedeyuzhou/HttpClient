package com.ebrightmoon.http.func;

import android.text.TextUtils;


import com.ebrightmoon.http.exception.ApiException;
import com.ebrightmoon.http.response.ResponseCode;
import com.ebrightmoon.http.response.ResponseResult;
import com.ebrightmoon.http.util.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;


public class ApiResultFunc<T> implements Function<ResponseBody, ResponseResult<T>> {
    protected Type type;

    public ApiResultFunc(Type type) {
        this.type = type;
    }

    /**
     * @param responseBody
     * @return
     * @throws Exception
     */
    @Override
    public ResponseResult<T> apply(ResponseBody responseBody) throws Exception {
        ResponseResult<T> apiResult = new ResponseResult<>();
        apiResult.setCode(-1);
        try {
            String json = responseBody.string();
            ResponseResult result = parseApiResult(json, apiResult);
            if (result != null) {
                apiResult = result;
                if (apiResult.getData() != null&&apiResult.getCode()== ResponseCode.HTTP_SUCCESS) {
                    T data = GsonUtil.gson().fromJson(apiResult.getData().toString(), type);
                    apiResult.setData(data);
                    apiResult.setCode(ResponseCode.HTTP_SUCCESS);
                } else {
                    throw ApiException.newApiException(apiResult.getCode(),apiResult.getMsg());
                }
            } else {
                apiResult.setMsg("json is null");
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            apiResult.setMsg(e.getMessage());
        } finally {
            responseBody.close();
        }
        return apiResult;
    }

    /**
     * 其中code data message 需要和ResponseResult中字段保持一致
     * @param json
     * @param apiResult
     * @return
     * @throws JSONException
     */
    private ResponseResult parseApiResult(String json, ResponseResult apiResult) throws JSONException {
        if (TextUtils.isEmpty(json)) return null;
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("Code")) {
            apiResult.setCode(jsonObject.getInt("Code"));
        }
        if (jsonObject.has("Data")) {
            apiResult.setData(jsonObject.getString("Data"));
        }
        if (jsonObject.has("Message")) {
            apiResult.setMsg(jsonObject.getString("Message"));
        }
        return apiResult;
    }
}

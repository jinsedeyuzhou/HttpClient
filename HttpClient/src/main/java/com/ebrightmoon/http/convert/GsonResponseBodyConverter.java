package com.ebrightmoon.http.convert;

import androidx.annotation.NonNull;

import com.ebrightmoon.http.common.ResponseHelper;
import com.ebrightmoon.http.response.ResponseResult;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.net.UnknownServiceException;

import okhttp3.ResponseBody;
import retrofit2.Converter;


final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(@NonNull ResponseBody value) throws IOException {
        if (adapter != null && gson != null) {
//            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            try {
//                T data = adapter.read(jsonReader);
                T data = (T) gson.fromJson(value.string(), ResponseResult.class);
                if (data == null) {
                    throw new UnknownServiceException("server back data is null");
                }
                if (data instanceof ResponseResult) {
                    ResponseResult apiResult = (ResponseResult) data;
                    if (!ResponseHelper.isSuccess(apiResult)) {
                        throw new UnknownServiceException(apiResult.getMsg() == null ? "unknow error" : apiResult.getMsg());
                    }
                }
                return data;
            } finally {
                value.close();
            }
        } else {
            return null;
        }
    }
}

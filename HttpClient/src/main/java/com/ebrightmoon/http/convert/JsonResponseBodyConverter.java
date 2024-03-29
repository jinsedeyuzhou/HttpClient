package com.ebrightmoon.http.convert;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;


final class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    JsonResponseBodyConverter() {
    }

    @Override
    public T convert(@NonNull ResponseBody value) throws IOException {
        return (T) value.string();
    }
}

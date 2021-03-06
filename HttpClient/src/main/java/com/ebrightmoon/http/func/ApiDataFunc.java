package com.ebrightmoon.http.func;


import com.ebrightmoon.http.common.ResponseHelper;
import com.ebrightmoon.http.response.ResponseResult;

import io.reactivex.functions.Function;


public class ApiDataFunc<T> implements Function<ResponseResult<T>, T> {
    public ApiDataFunc() {
    }

    @Override
    public T apply(ResponseResult<T> response) throws Exception {
        if (ResponseHelper.isSuccess(response)) {
            return response.getData();
        }
        return null;
    }
}

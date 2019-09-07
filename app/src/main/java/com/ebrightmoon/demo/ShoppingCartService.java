package com.ebrightmoon.demo;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Time: 2019-09-06
 * Author:wyy
 * Description:
 */
public interface ShoppingCartService {

    @FormUrlEncoded
    @POST("api/mobile/cart/updateCartCount")
    Observable<Shopping> getAuthor(@FieldMap Map<String,Object> maps);
}

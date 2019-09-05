package com.ebrightmoon.http.api;



import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.OPTIONS;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by wyy on 2017/9/6.
 */

public interface ApiService {


    /**
     * 通用get请求
     *
     * @param url
     * @param maps
     * @return
     */
    @GET()
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, String> maps);

    /**
     * post 请求
     *
     * @param url
     * @param requestBody
     * @return
     */
    @POST()
    Observable<ResponseBody> post(@Url() String url, @Body RequestBody requestBody);


    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> post(@Url() String url, @FieldMap Map<String, String> maps);

    /**
     * @param url
     * @param maps
     * @return
     */
    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> postForm(@Url() String url, @FieldMap Map<String, Object> maps);


    @POST()
    Observable<ResponseBody> postBody(@Url() String url, @Body RequestBody requestBody);

    /**
     * @param url
     * @param maps
     * @return
     */
    @HEAD()
    Observable<ResponseBody> head(@Url String url, @QueryMap Map<String, String> maps);

    /**
     * @param url
     * @param maps
     * @return
     */
    @OPTIONS()
    Observable<ResponseBody> options(@Url String url, @QueryMap Map<String, String> maps);

    /**
     * @param url
     * @param maps
     * @return
     */
    @FormUrlEncoded
    @PUT()
    Observable<ResponseBody> put(@Url() String url, @FieldMap Map<String, String> maps);

    /**
     * @param url
     * @param maps
     * @return
     */
    @FormUrlEncoded
    @PATCH()
    Observable<ResponseBody> patch(@Url() String url, @FieldMap Map<String, String> maps);

    /**
     * @param url
     * @param maps
     * @return
     */
    @FormUrlEncoded
    @DELETE()
    Observable<ResponseBody> delete(@Url() String url, @FieldMap Map<String, String> maps);

    /**
     * 文件下载
     *
     * @param url
     * @param maps
     * @return
     */
    @Streaming
    @GET()
    Observable<ResponseBody> downFile(@Url() String url, @QueryMap Map<String, String> maps);

    /**
     * @param url
     * @param responseBody
     * @return
     */
    @Streaming
    @POST()
    Observable<ResponseBody> downFile(@Url() String url, @Body RequestBody responseBody);
    /**
     * 单个文件上传
     *
     * @param url
     * @param file
     * @return
     */
    @Multipart
    @POST()
    Observable<ResponseBody> uploadFile(@Url() String url, MultipartBody.Part file);

    /**
     * 多个文件上传
     *
     * @param url
     * @param params
     * @return
     */
    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url() String url, @PartMap() Map<String, RequestBody> params);

    /**
     * 多个文件上传
     *
     * @param url
     * @param params
     * @return
     */
    @Multipart
    @POST()
    Observable<ResponseBody> uploadFiles(@Url() String url, @Part() List<MultipartBody.Part> params);

    //==================================== 带请求头应用 可通过拦截请求头设置 =============================================

//    /**
//     * 通用get请求
//     *
//     * @param url
//     * @param maps
//     * @return
//     */
//
//    @GET()
//    Observable<ResponseBody> get(@Url String url, @HeaderMap Map<String, String> headers, @QueryMap(encoded = true) Map<String, String> maps);
//
//
//    /**
//     * post 请求
//     *
//     * @param url
//     * @param requestBody
//     * @return
//     */
//    @POST()
//    Observable<ResponseBody> post(@Url() String url, @HeaderMap Map<String, String> headers, @Body RequestBody requestBody);
//
//  /**
//     * post 请求
//     *
//     * @param url
//     * @param  maps
//     * @return
//     */
//    @POST()
//    Observable<ResponseBody> postForm(@Url() String url, @HeaderMap Map<String, String> headers, @FieldMap Map<String, Object> maps);
//
//
//
//    /**
//     * put 提交表单数据
//     *
//     * @param url
//     * @param maps
//     * @return
//     */
//    @FormUrlEncoded
//    @PUT()
//    Observable<ResponseBody> put(@Url() String url, @HeaderMap Map<String, String> header, @FieldMap Map<String, String> maps);
//
//
//    /**
//     * 文件下载
//     *
//     * @param url
//     * @param maps
//     * @return
//     */
//    @Streaming
//    @GET()
//    Observable<ResponseBody> downFile(@Url() String url, @HeaderMap Map<String, String> header, @QueryMap Map<String, String> maps);
//
//
//    /**
//     * 多个文件上传
//     *
//     * @param url
//     * @param params
//     * @return
//     */
//    @Multipart
//    @POST()
//    Observable<ResponseBody> uploadFiles(@Url() String url, @HeaderMap Map<String, String> header, @PartMap() Map<String, RequestBody> params);
//
//    /**
//     * 多个文件上传
//     *
//     * @param url
//     * @param params
//     * @return
//     */
//    @Multipart
//    @POST()
//    Observable<ResponseBody> uploadFiles(@Url() String url, @HeaderMap Map<String, String> header,
//                                               @Part() List<MultipartBody.Part> params);
//


    /**
     * 下载
     * @param url
     * @return
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);

}

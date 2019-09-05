package com.ebrightmoon.demo;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ebrightmoon.http.callback.ACallback;
import com.ebrightmoon.http.common.Request;
import com.ebrightmoon.http.mode.CacheMode;
import com.ebrightmoon.http.mode.CacheResult;
import com.ebrightmoon.http.restrofit.AppClient;
import com.ebrightmoon.http.restrofit.HttpClient;
import com.ebrightmoon.http.util.GsonUtil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * catSort : 0
 * cityId : 1
 * currentPage : 1
 * customerId :
 * memberId : 1149
 * timeSort : 1
 * warehouseId : 1
 * platformType : 2
 * token : 63e000663ba54343ee374811ac6d50bc
 * accountType : 20
 * loginAccount :
 * device_platform : mobile
 * sign : 20748eb8f360ac9c08a2e1e242d00393
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ShoppingCart shoppingCart;
    private String json;
    private Button btn_post_json;
    private Button btn_post_form;
    private Button btn_get;
    private Button btn_upload;
    private Button btn_download;
    private Button btn_post_cache;
    private Button btn_get_cache;
    private Map<String, Object> forms;
    private Map<String, String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shoppingCart = new ShoppingCart();
        shoppingCart.setCatSort(0);
        shoppingCart.setCityId(1);
        shoppingCart.setCurrentPage(1);
        shoppingCart.setMemberId("1149");
        shoppingCart.setWarehouseId(1);
        shoppingCart.setTimeSort(1);
        shoppingCart.setPlatformType(2);
        shoppingCart.setToken("63e000663ba54343ee374811ac6d50bc");
        shoppingCart.setAccountType("20");
        shoppingCart.setLoginAccount("");
        shoppingCart.setDevice_platform("mobile");
        shoppingCart.setSign("20748eb8f360ac9c08a2e1e242d00393");
        json = "{\"catSort\":0,\"cityId\":1,\"currentPage\":1,\"customerId\":\"\",\"memberId\":\"1149\",\"timeSort\":1,\"warehouseId\":1,\"platformType\":2,\"token\":\"63e000663ba54343ee374811ac6d50bc\",\"accountType\":\"20\",\"loginAccount\":\"\",\"device_platform\":\"mobile\",\"sign\":\"20748eb8f360ac9c08a2e1e242d00393\"}";

        initView();
        bindEvent();
    }

    private void bindEvent() {

    }

    private void initView() {

        params = new LinkedHashMap<>();
        forms = new LinkedHashMap<>();
        params.put("member_id","1502");
        params.put("loginAccount","hetong001");
        params.put("account_type","10");
        params.put("device_id","b25e8eb903401c72e0175589");
        params.put("cartId","16126");
        params.put("os_version","8.0.0");
        params.put("version_code","17");
        params.put("channel","anzhi");
        params.put("productCount","3");
        params.put("token","1f41c45931205bb9d8b65f945ba0d811");
        params.put("network","wifi");
        params.put("device_brand","Xiaomi");
        params.put("device_platform","android");
        params.put("timestamp",System.currentTimeMillis()+"");
        forms.putAll(params);
        btn_post_json = findViewById(R.id.btn_post_json);
        btn_post_json.setOnClickListener(this);
        btn_post_form = findViewById(R.id.btn_post_form);
        btn_post_form.setOnClickListener(this);
        btn_post_cache = findViewById(R.id.btn_post_cache);
        btn_post_cache.setOnClickListener(this);
        btn_get_cache = findViewById(R.id.btn_get_cache);
        btn_get_cache.setOnClickListener(this);
        btn_get = findViewById(R.id.btn_get);
        btn_get.setOnClickListener(this);
        btn_upload = findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(this);
        btn_download = findViewById(R.id.btn_download);
        btn_download.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_post_json:
                postJson();
                break;
            case R.id.btn_post_form:
                postForm();
                break;
            case R.id.btn_post_cache:
                postCache();
                break;
            case R.id.btn_get_cache:
                getCache();
                break;
            case R.id.btn_get:
                getRequest();
                break;
            case R.id.btn_upload:
                upload();
                break;
            case R.id.btn_download:
                download();
                break;
        }
    }

    private void download() {

    }

    private void upload() {

    }

    private void getRequest() {
        Request request=new Request.Builder()
                .setSuffixUrl("api/mobile/cart/updateCartCount")
                .setParams(params)
                .build();
        AppClient.getInstance().get(request,new ACallback<String>(){

            @Override
            public void onSuccess(String data) {
                Toast.makeText(MainActivity.this,data,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(int errCode, String errMsg) {

            }
        });

    }

    private void getCache() {
        Request request=new Request.Builder()
                .setSuffixUrl("api/mobile/cart/updateCartCount")
                .setParams(params)
                .setLocalCache(true)
                .setCacheMode(CacheMode.FIRST_CACHE)
                .setCacheTime(30000)
                .build();
        AppClient.getInstance().get(request,new ACallback<CacheResult<String>>(){
            @Override
            public void onSuccess(CacheResult<String> data) {
                if (data == null || data.getCacheData() == null) {
                    return;
                }
                Toast.makeText(MainActivity.this,data.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(int errCode, String errMsg) {

            }
        });
    }

    private void postCache() {

    }

    private void postForm() {
        Request request=new Request.Builder()
                .setSuffixUrl("api/mobile/cart/updateCartCount")
                .setForms(forms)
                .build();
        AppClient.getInstance().post(request,new ACallback<String>(){

            @Override
            public void onSuccess(String data) {
                Toast.makeText(MainActivity.this,data,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(int errCode, String errMsg) {

            }
        });
    }

    private void postJson() {
        Request request=new Request.Builder()
                .setSuffixUrl("api/mobile/cart/getShoppingCartList")
                .setContent(json)
                .build();
        AppClient.getInstance().post(request,new ACallback<String>(){

            @Override
            public void onSuccess(String data) {
                Toast.makeText(MainActivity.this,data,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(int errCode, String errMsg) {

            }
        });
    }
}

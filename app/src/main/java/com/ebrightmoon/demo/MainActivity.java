package com.ebrightmoon.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ebrightmoon.http.callback.ACallback;
import com.ebrightmoon.http.restrofit.HttpClient;
import com.ebrightmoon.http.util.GsonUtil;

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

public class MainActivity extends AppCompatActivity {

    private ShoppingCart shoppingCart;
    private String json;

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

        init();
    }

    private void init() {
        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpClient.post("api/mobile/cart/getShoppingCartList")
                        .setJson(GsonUtil.gson().toJson(shoppingCart))
                        .request(new ACallback<String>() {
                            @Override
                            public void onSuccess(String data) {
                                System.out.println(data);
                            }

                            @Override
                            public void onFail(int errCode, String errMsg) {

                            }
                        });
            }
        });
    }
}

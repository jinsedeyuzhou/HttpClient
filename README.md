# HttpClient
基于retrofit，Rxjava 封装的请求库，支持网络请求生命周期管理等


### 集成
如果你的项目 Gradle 配置是在 7.0 以下，需要在 build.gradle 文件中加入
```
allprojects {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```
如果你的 Gradle 配置是 7.0 及以上，则需要在 settings.gradle 文件中加入
```
dependencyResolutionManagement {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```
配置完远程仓库后，在项目 app 模块下的 build.gradle 文件中加入远程依赖
```

dependencies {
    // 网络请求框架：
    implementation 'com.github.jinsedeyuzhou:HttpClient:1.0.6'
}
```
### 使用

**Request**
```
params = new LinkedHashMap<>();
        forms = new LinkedHashMap<>();
        params.put("member_id", "1502");
        params.put("loginAccount", "hetong001");
        params.put("account_type", "10");
        params.put("device_id", "b25e8eb903401c72e0175589");
        params.put("cartId", "16126");
        params.put("os_version", "8.0.0");
        params.put("version_code", "17");
        params.put("channel", "anzhi");
        params.put("productCount", "1");
        params.put("token", "1f41c45931205bb9d8b65f945ba0d811");
        params.put("network", "wifi");
        params.put("device_brand", "Xiaomi");
        params.put("device_platform", "android");
        params.put("timestamp", System.currentTimeMillis() + "");
        forms.putAll(params);

 Request.Builder request = new Request.Builder()
                .setSuffixUrl("api/mobile/cart/updateCartCount")
                .setParams(params)
                .setHttpCache(true);

        AppClient.getInstance().get(request, new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null ) {
                    return;
                }
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(int errCode, String errMsg) {

            }
        });

```
**Post**
```
 Request.Builder request = new Request.Builder()
                .setSuffixUrl("api/mobile/cart/updateCartCount")
                .setBaseUrl("https://t3.fsyuncai.com/")
                .setForms(forms)
                .setHttpCache(true)
                ;
        AppClient.getInstance().post(request, new ACallback<String>() {

            @Override
            public void onSuccess(String data) {
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(int errCode, String errMsg) {

            }
        });
```
**Json**
```
 Request.Builder request = new Request.Builder()
                .setSuffixUrl("api/mobile/cart/getShoppingCartList")
                .setContent(json)
                ;
        AppClient.getInstance().post(request, new ACallback<String>() {

            @Override
            public void onSuccess(String data) {
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(int errCode, String errMsg) {

            }
        });
```
**download**
```
 Request.Builder request = new Request.Builder()
                .setSuffixUrl("16891/1A8EA15110A5DA113EBD2F955615C7EC.apk?fsname=com.moji.mjweather_7.0103.02_7010302.apk&csr=1bbd")
                .setBaseUrl("http://imtt.dd.qq.com/")
                .setFileName("weixin.apk")
                .setDirName("");

        upload_progress.setMax(100);
        AppClient.getInstance().download(request, new ACallback<DownProgress>() {
            @Override
            public void onSuccess(DownProgress downProgress) {
                if (downProgress == null) {
                    return;
                }
                Logger.e("down progress currentLength:" + downProgress.getDownloadSize() + ",totalLength:" + downProgress.getTotalSize());
                upload_progress.setProgress((int) (downProgress.getDownloadSize() * 100 / downProgress.getTotalSize()));
                download_progress_desc.setText(downProgress.getPercent());
            }

            @Override
            public void onFail(int errCode, String errMsg) {

            }
        });
```
**upload**
```
 Request.Builder request = new Request.Builder()
                .setSuffixUrl("16891/1A8EA15110A5DA113EBD2F955615C7EC.apk?fsname=com.moji.mjweather_7.0103.02_7010302.apk&csr=1bbd")
                .setBaseUrl("http://imtt.dd.qq.com/")
                .setFileName("weixin.apk")
                .setDirName("");

        upload_progress.setMax(100);
        AppClient.getInstance().download(request, new ACallback<DownProgress>() {
            @Override
            public void onSuccess(DownProgress downProgress) {
                if (downProgress == null) {
                    return;
                }
                Logger.e("down progress currentLength:" + downProgress.getDownloadSize() + ",totalLength:" + downProgress.getTotalSize());
                upload_progress.setProgress((int) (downProgress.getDownloadSize() * 100 / downProgress.getTotalSize()));
                download_progress_desc.setText(downProgress.getPercent());
            }

            @Override
            public void onFail(int errCode, String errMsg) {

            }
        });
```
### License
Copyright (C) jinsedeyuzhou, The Framework Open Source Project

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

(Frequently Asked Questions)FAQ

package com.ebrightmoon.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ebrightmoon.demo.adapter.GridImageAdapter;
import com.ebrightmoon.http.callback.ACallback;
import com.ebrightmoon.http.callback.UCallback;
import com.ebrightmoon.http.common.Request;
import com.ebrightmoon.http.mode.DownProgress;
import com.ebrightmoon.http.restrofit.AppClient;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Time: 2019-09-07
 * Author:wyy
 * Description:
 */
public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private ImageView left_back;
    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private int themeId;
    private ProgressBar upload_progress;
    private Button btn_upload;
    private Button btn_download;
    private Request.Builder request;
    private Map<String, String> params;
    private TextView download_progress_desc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        themeId = R.style.picture_default_style;
        initView();
        initData();
    }

    private void initData() {
    }

    private void initView() {
        params = new LinkedHashMap<>();
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
        params.put("catalog", "13");
        params.put("timestamp", System.currentTimeMillis() + "");
        left_back = (ImageView) findViewById(R.id.left_back);
        upload_progress = findViewById(R.id.upload_progress);
        download_progress_desc = findViewById(R.id.download_progress_desc);
        btn_upload = findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(this);
        btn_download = findViewById(R.id.btn_download);
        btn_download.setOnClickListener(this);
        left_back.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(UploadActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(10);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(UploadActivity.this).themeStyle(themeId).openExternalPreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(UploadActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(UploadActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });

        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(UploadActivity.this);
                } else {
                    Toast.makeText(UploadActivity.this,
                            getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_back:
                finish();
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
    }

    private void upload() {
        Request.Builder request = new Request.Builder()
                .setSuffixUrl("baseservice/upload")
                .setBaseUrl("https://t3.fsyuncai.com/api/mobile/")
                .addParam("catalog", "13")
                .setUCallback(new UCallback() {
                    @Override
                    public void onProgress(long currentLength, long totalLength, float percent) {
                        Logger.e("percent:" + percent);
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {

                    }
                });

        for (LocalMedia localMedia : selectList) {
            File file = new File(localMedia.getPath());
            if (!file.exists()) {
                continue;
            }
            request.addImageFile("file", file);

        }

        AppClient.getInstance().upload(request, new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
                Toast.makeText(UploadActivity.this, data, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(int errCode, String errMsg) {

            }
        });

    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(UploadActivity.this)
                    .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .maxSelectNum(10)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(true)// 是否可预览视频
                    .enablePreviewAudio(true) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                    //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                    .enableCrop(false)// 是否裁剪
                    .compress(true)// 是否压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    //.compressSavePath(getPath())//压缩图片保存地址
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .withAspectRatio(16, 9)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                    .isGif(true)// 是否显示gif图片
                    .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                    .circleDimmedLayer(false)// 是否圆形裁剪
                    .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .openClickSound(false)// 是否开启点击声音
                    .selectionMedia(selectList)// 是否传入已选图片
                    //.isDragFrame(false)// 是否可拖动裁剪框(固定)
//                        .videoMaxSecond(15)
//                        .videoMinSecond(10)
                    //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    //.rotateEnabled(true) // 裁剪是否可旋转图片
                    //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()//显示多少秒以内的视频or音频也可适用
                    //.recordVideoSecond()//录制视频秒数 默认60s
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : selectList) {
                        Log.i("图片-----》", media.getPath());
                    }
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}

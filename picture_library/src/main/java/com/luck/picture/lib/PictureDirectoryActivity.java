package com.luck.picture.lib;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luck.picture.lib.adapter.PictureAlbumDirectoryAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.decoration.RecycleViewDivider;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.tools.ToastManage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Time: 2019/6/24
 * Author:wyy
 * Description:
 */
public class PictureDirectoryActivity extends PictureBaseActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private PictureAlbumDirectoryAdapter adapter;
    private LinearLayout id_ll_root;
    private TextView picture_title;
    private RelativeLayout rl_picture_title;
    private ImageView picture_left_back;
    private TextView picture_right;
    private LocalMediaLoader mediaLoader;
    private List<LocalMediaFolder> foldersList = new ArrayList<>();
    private RxPermissions rxPermissions;
    private List<LocalMedia> selectImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_directory);
        rxPermissions = new RxPermissions(this);
        initView(savedInstanceState);
    }

   private void initView(Bundle savedInstanceState)
   {
       selectImages = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_SELECT_LIST);
       rl_picture_title = (RelativeLayout) findViewById(R.id.rl_picture_title);
       picture_left_back = (ImageView) findViewById(R.id.picture_left_back);
       picture_title = (TextView) findViewById(R.id.picture_title);
       picture_right = (TextView) findViewById(R.id.picture_right);
       id_ll_root = (LinearLayout) findViewById(R.id.id_ll_root);
       adapter = new PictureAlbumDirectoryAdapter(this);
       recyclerView = (RecyclerView) findViewById(R.id.folder_list);
//       recyclerView.getLayoutParams().height = (int) (ScreenUtils.getScreenHeight(this) * 0.6);
       recyclerView.addItemDecoration(new RecycleViewDivider(
               this, LinearLayoutManager.HORIZONTAL, ScreenUtils.dip2px(this, 1), ContextCompat.getColor(this, R.color.color_E8E8E8)));
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
       recyclerView.setAdapter(adapter);
       picture_title.setText("相册");

       picture_title.setCompoundDrawables(null,null,null,null);
       picture_left_back.setOnClickListener(this);
       picture_right.setOnClickListener(this);

       mediaLoader = new LocalMediaLoader(this, config.mimeType, config.isGif, config.videoMaxSecond, config.videoMinSecond);

       rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
               .subscribe(new Observer<Boolean>() {
                   @Override
                   public void onSubscribe(Disposable d) {
                   }

                   @Override
                   public void onNext(Boolean aBoolean) {
                       if (aBoolean) {
                           readLocalMedia();
                       } else {
                           ToastManage.s(mContext, getString(R.string.picture_jurisdiction));
                       }
                   }

                   @Override
                   public void onError(Throwable e) {
                   }

                   @Override
                   public void onComplete() {
                   }
               });

       adapter.setOnItemClickListener(new PictureAlbumDirectoryAdapter.OnItemClickListener() {
           @Override
           public void onItemClick(String folderName, List<LocalMedia> images) {
               boolean camera = StringUtils.isCamera(folderName);
               camera = config.isCamera ? camera : false;
               Bundle bundle=new Bundle();
               bundle.putString(PictureConfig.PICTURE_TITLE,folderName);
               bundle.putSerializable(PictureConfig.DIRECTORY_LIST, (Serializable) images);
               bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectImages);               startActivity(PictureSelectorActivity.class,bundle);
               finish();
           }
       });

   }

    /**
     * get LocalMedia s
     */
    protected void readLocalMedia() {
        mediaLoader.loadAllMedia(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                if (folders.size() > 0) {
                    foldersList = folders;
                    LocalMediaFolder folder = folders.get(0);
//                    folder.setChecked(true);
//                    List<LocalMedia> localImg = folder.getImages();
                    // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                    // 因为onActivityResult里手动添加拍照后的照片，
                    // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的

                    // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                    // 因为onActivityResult里手动添加拍照后的照片，
                    // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的
//                    bindFolder(folders);
                    notifyDataCheckedStatus(selectImages);

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if (id== R.id.picture_left_back||id== R.id.picture_right)
        {
            closeActivity();
        }
    }

    public void bindFolder(List<LocalMediaFolder> folders) {
        adapter.setMimeType(config.mimeType);
        adapter.bindFolderData(folders);
    }

    public void setPictureTitleView(TextView picture_title) {
        this.picture_title = picture_title;
    }


    /**
     * 设置选中状态
     */
    public void notifyDataCheckedStatus(List<LocalMedia> medias) {
        try {
            // 获取选中图片
            for (LocalMediaFolder folder : foldersList) {
                folder.setCheckedNum(0);
            }
            if (medias.size() > 0) {
                for (LocalMediaFolder folder : foldersList) {
                    int num = 0;// 记录当前相册下有多少张是选中的
                    List<LocalMedia> images = folder.getImages();
                    for (LocalMedia media : images) {
                        String path = media.getPath();
                        for (LocalMedia m : medias) {
                            if (path.equals(m.getPath())) {
                                num++;
                                folder.setCheckedNum(num);
                            }
                        }
                    }
                }
            }
            bindFolder(foldersList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

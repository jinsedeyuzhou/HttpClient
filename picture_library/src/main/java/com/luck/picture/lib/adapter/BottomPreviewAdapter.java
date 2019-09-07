package com.luck.picture.lib.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.EventEntity;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.rxbus2.RxBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Time: 2019/6/26
 * Author:wyy
 * Description:
 */
public class BottomPreviewAdapter extends RecyclerView.Adapter<BottomPreviewAdapter.ViewHolder> {

    private Context mContext;
    private List<LocalMedia> selectImages = new ArrayList<LocalMedia>();

    public BottomPreviewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_bottom_preview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final LocalMedia localMedia = selectImages.get(position);
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .sizeMultiplier(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
//                .override(120, 120);
        Glide.with(holder.itemView.getContext())
                .asBitmap()
                .load(localMedia.getPath())
                .apply(options)
                .into(new BitmapImageViewTarget(holder.ivPreviewPhoto) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.
                                        create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(4);
                        holder.ivPreviewPhoto.setImageDrawable(circularBitmapDrawable);
                    }
                });

        holder.ivPreviewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImages.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged(selectImages);
                EventEntity obj = new EventEntity(PictureConfig.UPDATE_FLAG_PREVIEW, selectImages, position);
                RxBus.getDefault().post(obj);
                if (onDeleteClickListener!=null)
                    onDeleteClickListener.onDeleteClick(v,localMedia);

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null)
                    onItemClickListener.onItemClick(holder.itemView,position);
            }
        });

    }

    public void notifyDataSetChanged(List<LocalMedia> localMediaList)
    {
        selectImages=localMediaList;
        notifyDataSetChanged();
    }

    public void insertPreviewLastPosition(LocalMedia localMedia)
    {

        selectImages.add(selectImages.size(),localMedia);
        notifyItemInserted(selectImages.size()-1);
    }

    public void removePreviewPosition(LocalMedia localMedia)
    {
        for (int i=0;i<selectImages.size();i++)
        {
            if (selectImages.get(i).getPath().equals(localMedia.getPath()))
            {
                notifyItemRemoved(i);
                break;
            }
        }
    }
    @Override
    public int getItemCount() {
        return selectImages.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivPreviewDelete;
        private final ImageView ivPreviewPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPreviewDelete = itemView.findViewById(R.id.ivPreviewDelete);
            ivPreviewPhoto = itemView.findViewById(R.id.ivPreviewPhoto);
        }
    }

    private OnDeleteClickListener onDeleteClickListener;

    public void setOnOnDeleteClickListener(OnDeleteClickListener onItemClickListener) {
        this.onDeleteClickListener = onItemClickListener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(View  view, LocalMedia localMedia);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener=onItemClickListener;
    }

    public interface  OnItemClickListener
    {
        void onItemClick(View v,int position);
    }


}

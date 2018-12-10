package com.example.qiaoguan.photo;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.qiaoguan.photo.Http.Network;

import java.util.List;

public class PhotoAdapter extends BaseQuickAdapter<PhotoItem,BaseViewHolder> {

    private ThumbnailDownLoader<BaseViewHolder> imageDownloader;

    public PhotoAdapter(@NonNull List<PhotoItem> items, @NonNull ThumbnailDownLoader<BaseViewHolder> imageDownloader) {
        super(R.layout.image_item,items);
        this.imageDownloader = imageDownloader;
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoItem item) {
        imageDownloader.queueThumbnail(helper,item.getmUrl());
    }

    public void bindDrawable(BaseViewHolder helper,Drawable drawable) {
        helper.setImageDrawable(R.id.photo_image,drawable);
    }

    private void downLoadImage() {
        Network.getPhotoApis().
    }
}

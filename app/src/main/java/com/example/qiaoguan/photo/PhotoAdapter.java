package com.example.qiaoguan.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;


public class PhotoAdapter extends BaseQuickAdapter<PhotoItem,BaseViewHolder> {
    private static final String TAG = "PhotoAdapter";
    private CompositeDisposable mCompositeDisposable;
    private PhotoDataManger manger;
    private Handler mainHandler;
    public ItemLoadErrorCallback callback;

    public PhotoAdapter(@NonNull List<PhotoItem> items, @NonNull CompositeDisposable compositeDisposable) {
        super(R.layout.image_item,items);
        this.mCompositeDisposable = compositeDisposable;
        manger = PhotoDataManger.getINSTANCE();
        mainHandler = new Handler();
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoItem item) {
        loadImage(helper,item);
    }

    private void loadImage(final BaseViewHolder helper, PhotoItem item) {
        manger.getImageBitmap(item.getmUrl(), new PhotoDataManger.GetImageCallback() {
            @Override
            public void onImageLoad(Drawable drawable) {
                mainHandler.post(()->helper.setImageDrawable(R.id.photo_image,drawable));
            }

            @Override
            public void onError(String msg) {
                Log.i(TAG, "onError: " + msg );
                mainHandler.post(()->helper.setImageResource(R.id.photo_image,R.mipmap.ic_launcher));
                if (callback != null){
                    callback.onItemError(item,helper.getLayoutPosition());
                }
            }
        });
    }

    public interface ItemLoadErrorCallback{
        void onItemError(PhotoItem item,int position);
    }

    public void setItemLoadErrorCallback(ItemLoadErrorCallback callback){
        this.callback = callback;
    }

}

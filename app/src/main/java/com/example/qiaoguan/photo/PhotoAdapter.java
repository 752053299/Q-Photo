package com.example.qiaoguan.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.qiaoguan.photo.Http.Network;
import com.example.qiaoguan.photo.Utils.AppUtil;
import com.example.qiaoguan.photo.Utils.MyLog;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class PhotoAdapter extends BaseQuickAdapter<PhotoItem,BaseViewHolder> {

    private CompositeDisposable mCompositeDisposable;
    private PhotoDataManger manger;
    private DiskLruCache mDiskLruCache;

    public PhotoAdapter(@NonNull List<PhotoItem> items, @NonNull CompositeDisposable compositeDisposable) {
        super(R.layout.image_item,items);
        this.mCompositeDisposable = compositeDisposable;
        manger = PhotoDataManger.getINSTANCE();
        mDiskLruCache = manger.getmDiskLruCache();
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoItem item) {
        loadImage(helper,item.getmUrl());
    }

    private void loadImage(final BaseViewHolder helper, final String url) {

        //优先从内存读取缓存
        Bitmap bitmap = manger.getBitMapFromMemory(url);
        if (bitmap != null) {
            Drawable drawable = new  BitmapDrawable(getRecyclerView().getResources(),bitmap);
            helper.setImageDrawable(R.id.photo_image,drawable);
            return;
        }
        //其次从磁盘读取缓存
        Observable<Bitmap> cachedOb = Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                FileDescriptor fileDescriptor = null;
                FileInputStream fileInputStream = null;
                DiskLruCache.Snapshot snapshot = null;

                final String key = AppUtil.hashKeyForDisk(url);
                snapshot = mDiskLruCache.get(key);
                if (snapshot == null) {

                }
                snapshot = mDiskLruCache.get(key);
            }
        });


        Disposable disposable = Network.getPhotoApis().getOnePhotoRx(url)
                .map(new Function<ResponseBody, Drawable>() {
                    @Override
                    public Drawable apply(ResponseBody responseBody) throws Exception {
                        if (responseBody==null)
                            throw new RuntimeException("body = null");
                        byte[] bitMapBytes = responseBody.bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bitMapBytes,0,bitMapBytes.length);
                        return new BitmapDrawable(getRecyclerView().getResources(),bitmap);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Drawable>() {
                    @Override
                    public void accept(Drawable drawable) throws Exception {
                        helper.setImageDrawable(R.id.photo_image,drawable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        MyLog.i(TAG, "error: ");
                        helper.setImageResource(R.id.photo_image,R.mipmap.ic_launcher);
                    }
                });
        mCompositeDisposable.add(disposable);
    }
}

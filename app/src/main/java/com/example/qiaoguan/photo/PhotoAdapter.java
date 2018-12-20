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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            public void subscribe(final ObservableEmitter<Bitmap> emitter) throws Exception {

                final String key = AppUtil.hashKeyForDisk(url);
                DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);

                if (snapshot == null) {
                    Network.getPhotoApis().getOnePhoto(url).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                ResponseBody responseBody = response.body();
                                if (responseBody==null)
                                    throw new RuntimeException("body = null");
                                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                                InputStream byteStream = responseBody.byteStream();
                                if (editor != null) {
                                    if(readImageFromNet(byteStream,editor.newOutputStream(0))){
                                        editor.commit();
                                    }else {
                                        editor.abort();
                                        throw new RuntimeException("editor read error");
                                    }
                                }

                                DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                                if (snapshot != null) {
                                    readImageFromDisk(snapshot,emitter,url);
                                }else {
                                    throw new RuntimeException("editor read error");
                                }

//                                byte[] bitMapBytes = responseBody.bytes();
//                                Bitmap bitmap = BitmapFactory.decodeByteArray(bitMapBytes,0,bitMapBytes.length);

                            } catch (Exception e) {
                                emitter.onError(e);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }else {
                    readImageFromDisk(snapshot,emitter,url);
                }
            }
        });


        Disposable disposable = cachedOb
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        MyLog.i(TAG, "error: ");
                        helper.setImageResource(R.id.photo_image, R.mipmap.ic_launcher);
                    }
                });
        mCompositeDisposable.add(disposable);
    }



    private boolean readImageFromNet(InputStream inputStream , OutputStream outputStream) {
        BufferedInputStream in = new BufferedInputStream(inputStream);
        BufferedOutputStream out = new BufferedOutputStream(outputStream);
        try {
            int r;
            while ((r = in.read()) != -1) {
                out.write(r);
            }
            return true;
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void readImageFromDisk(DiskLruCache.Snapshot snapshot,ObservableEmitter<Bitmap> emitter,String url) {
        try {
            FileInputStream fi = (FileInputStream) snapshot.getInputStream(0);
            FileDescriptor fd = fi.getFD();
            Bitmap btMap = null;
            if (fd != null) {
                btMap = BitmapFactory.decodeFileDescriptor(fd);
            }
            if (btMap != null) {
                // 将Bitmap对象添加到内存缓存当中
                manger.addBitmapToMemoryCache(url,btMap);
                emitter.onNext(btMap);
            }else {
                emitter.onError(new Exception("get bitmap error"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            emitter.onError(new Exception("get bit map error"));
        }
    }
}

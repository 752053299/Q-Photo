package com.example.qiaoguan.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.qiaoguan.photo.Http.Network;
import com.example.qiaoguan.photo.Utils.MyLog;

import org.reactivestreams.Publisher;

import java.util.List;

import io.reactivex.Scheduler;
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

    public PhotoAdapter(@NonNull List<PhotoItem> items, @NonNull CompositeDisposable compositeDisposable) {
        super(R.layout.image_item,items);
        this.mCompositeDisposable = compositeDisposable;
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoItem item) {
        downLoadImage(helper,item.getmUrl());
    }

    private void downLoadImage(final BaseViewHolder helper, String url) {
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

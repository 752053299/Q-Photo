package com.example.qiaoguan.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;

import com.example.qiaoguan.photo.Http.Network;
import com.example.qiaoguan.photo.Utils.AppUtil;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PhotoDataManger {
    private static final String TAG = "PhotoDataManger";
    private LruCache<String ,Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    private static class PhotoDataHolder {
        private static final PhotoDataManger INSTANCE = new PhotoDataManger();
    }
    private PhotoDataManger() {
    }

    public void init(Context applicationContext) {
        Log.i(TAG, "init: ");
        try {
            File cacheDir = AppUtil.getDiskCacheDir(applicationContext,"photoGirls");
            if (!cacheDir.exists()){
                cacheDir.mkdirs();
            }
            Log.i(TAG, "initFile: " + cacheDir.getAbsolutePath());
            Log.i(TAG, "initFile: " + cacheDir.getCanonicalPath());
            Log.i(TAG, "initFile: " + cacheDir.getPath());

            mDiskLruCache = DiskLruCache.open(cacheDir,AppUtil.getAppVersion(applicationContext),1,100*1024*1024);
        } catch (IOException e) {
            Log.e(TAG, "error: "  + e.getMessage());
            e.printStackTrace();
        }
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        mMemoryCache = new LruCache<String, Bitmap>(maxMemory/8){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

    }

    public static PhotoDataManger getINSTANCE() {
        return PhotoDataHolder.INSTANCE;
    }


    public void addBitmapToMemoryCache(String key,Bitmap bitmap) {
        if (getBitMapFromMemory(key) == null) {
            mMemoryCache.put(key,bitmap);
        }
    }

    public Bitmap getBitMapFromMemory(String key) {
        return mMemoryCache.get(key);
    }

    public DiskLruCache getmDiskLruCache() {
        return mDiskLruCache;
    }

    public interface GetImageCallback{
        void onImageLoad(Drawable drawable);
        void onError(String msg);
    }

    private Drawable bitMapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(MyApplication.getINSTANCE().getContext().getResources(),bitmap);
    }


    public void getImageBitmap(@NonNull final String url, final GetImageCallback callback) {
        //先从内存中获取
        final Bitmap bitmap = getBitMapFromMemory(url);
        if (bitmap != null) {
            Log.i(TAG, "*从内存中获取" + url);
            callback.onImageLoad(bitMapToDrawable(bitmap));
            return;
        }
        //再从sdCard中获取
        final String key = AppUtil.hashKeyForDisk(url);
        Log.i(TAG, "key: " + key);
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (snapshot == null){
                //从网络获取
                Network.getPhotoApis().getOnePhoto(url).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        ResponseBody responseBody = response.body();
                        if (responseBody==null){
                            callback.onError("body = null");
                            return;
                        }

                        try {
                            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                            InputStream byteStream = responseBody.byteStream();
                            if (editor != null){
                                if (readImageStream(byteStream,editor.newOutputStream(0))){
                                    editor.commit();
                                }else {
                                    editor.abort();
                                    callback.onError("stream transForm error");
                                    return;
                                }
                            }

                            //存储完毕后，读取出来
                            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                            if (snapshot != null){
                                Log.i(TAG, "*从网络中获取" + url);
                                readImageFromDisk(snapshot,callback,url);
                            }

                        } catch (IOException e) {
                            Log.e(TAG, "diskLruCache error: " + e.getMessage());
                            e.printStackTrace();
                            callback.onError(e.getMessage());
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });

            }else {
                //sdCard中有数据
                Log.i(TAG, "*从disk中获取" + url);
                readImageFromDisk(snapshot,callback,url);
            }

        } catch (IOException e) {
            Log.e(TAG, "getImageBitmap error : " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * inputStream -> outPutStream
     * @param inputStream
     * @param outputStream
     * @return
     */
    private boolean readImageStream(InputStream inputStream , OutputStream outputStream) {
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

    /**
     * 从disk中读取图片，并且存储到内存中
     * @param snapshot
     * @param callback
     * @param url
     */
    private void readImageFromDisk(DiskLruCache.Snapshot snapshot,final GetImageCallback callback,String url) {
        try {
            FileInputStream fi = (FileInputStream) snapshot.getInputStream(0);
            FileDescriptor fd = fi.getFD();
            Bitmap btMap = null;
            if (fd != null) {
                btMap = BitmapFactory.decodeFileDescriptor(fd);
            }
            if (btMap != null) {
                // 将Bitmap对象添加到内存缓存当中
                addBitmapToMemoryCache(url,btMap);
                callback.onImageLoad(bitMapToDrawable(btMap));
            }else {
                callback.onError("get bitmap error");
            }

        } catch (IOException e) {
            e.printStackTrace();
            callback.onError("get bit map error" + e.getMessage());
        }
    }

}


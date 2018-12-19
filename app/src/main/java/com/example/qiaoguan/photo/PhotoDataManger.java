package com.example.qiaoguan.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.example.qiaoguan.photo.Utils.AppUtil;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;


public class PhotoDataManger {

    private LruCache<String ,Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    private static class PhotoDataHolder {
        private static final PhotoDataManger INSTANCE = new PhotoDataManger();
    }
    private PhotoDataManger() {
    }

    public void init(Context applicationContext) {
        try {
            File cacheDir = AppUtil.getDiskCacheDir(applicationContext,"photo");
            if (!cacheDir.exists()){
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir,AppUtil.getAppVersion(applicationContext),1,100*1024*1024);
        } catch (IOException e) {
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

}

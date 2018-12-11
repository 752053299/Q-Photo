package com.example.qiaoguan.photo;

import android.graphics.Bitmap;
import android.util.LruCache;

import okhttp3.internal.cache.DiskLruCache;

public class PhotoDataManger {

    private LruCache<String ,Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    private static class PhotoDataHolder {
        private static final PhotoDataManger INSTANCE = new PhotoDataManger();
    }
    private PhotoDataManger() {}

    public static PhotoDataManger getINSTANCE() {
        return PhotoDataHolder.INSTANCE;
    }




}

package com.example.qiaoguan.photo;

import android.os.HandlerThread;

import com.chad.library.adapter.base.BaseViewHolder;
import com.example.qiaoguan.photo.Utils.MyLog;

public class ThumbnailDownLoader<T extends BaseViewHolder> extends HandlerThread {
    private static final String TAG = "ThumbnailDownLoader";
    private Boolean mHasQuit = false;

    public ThumbnailDownLoader() {
        super(TAG);
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target, String url) {
        MyLog.i(TAG, "Got a URL: " + url);
    }
}

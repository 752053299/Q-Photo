package com.example.qiaoguan.photo;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class PhotoItem {
    @SerializedName("_id")
    private String mId;
    private String publishedAt;
    @SerializedName("url")
    private String mUrl;
    private String mCaption;

    @NonNull
    @Override
    public String toString() {
        return mCaption;
    }


    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmCaption() {
        return mCaption;
    }

    public void setmCaption(String mCaption) {
        this.mCaption = mCaption;
    }
}

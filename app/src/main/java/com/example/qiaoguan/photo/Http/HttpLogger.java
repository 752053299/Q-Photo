package com.example.qiaoguan.photo.Http;



import com.example.qiaoguan.photo.Utils.MyLog;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by 乔冠 on 2017/12/25. 网络请求的日志
 */

public class HttpLogger implements HttpLoggingInterceptor.Logger {

    public static final String LOG_TAG = "GuiWuApi";

    @Override
    public void log(String message) {
        MyLog.i(LOG_TAG, message);
    }
}

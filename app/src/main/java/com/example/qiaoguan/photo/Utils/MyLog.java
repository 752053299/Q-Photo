package com.example.qiaoguan.photo.Utils;

import android.util.Log;

import com.example.qiaoguan.photo.BuildConfig;


/**
 * Created by 乔冠 on 2018/2/2. 日志
 */

public class MyLog {
    private static int LOG_MAXLENGTH = 2000;

    public static int v(String tag, String msg) {
        if (BuildConfig.IS_SHOW_LOG) {
            return Log.v(tag, msg);
        } else {
            return -1;
        }
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (BuildConfig.IS_SHOW_LOG) {
            return Log.v(tag, msg, tr);
        } else {
            return -1;
        }
    }

    public static int d(String tag, String msg) {
        if (BuildConfig.IS_SHOW_LOG) {
            return Log.d(tag, msg);
        } else {
            return -1;
        }
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (BuildConfig.IS_SHOW_LOG) {
            return Log.d(tag, msg, tr);
        } else {
            return -1;
        }
    }

//    public static int i(String tag, String msg) {
//        if (BuildConfig.IS_SHOW_LOG) {
//            return Log.i(tag, msg);
//        } else {
//            return -1;
//        }
//    }

    public static int i(String tag, String msg, Throwable tr) {
        if (BuildConfig.IS_SHOW_LOG) {
            return Log.i(tag, msg, tr);
        } else {
            return -1;
        }
    }

    public static int w(String tag, String msg) {
        if (BuildConfig.IS_SHOW_LOG) {
            return Log.w(tag, msg);
        } else {
            return -1;
        }
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (BuildConfig.IS_SHOW_LOG) {
            return Log.w(tag, msg, tr);
        } else {
            return -1;
        }
    }

    public static int w(String tag, Throwable tr) {
        if (BuildConfig.IS_SHOW_LOG) {
            return Log.w(tag, tr);
        } else {
            return -1;
        }
    }

    public static int e(String tag, String msg) {
        if (BuildConfig.IS_SHOW_LOG) {
            return Log.e(tag, msg);
        } else {
            return -1;
        }
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (BuildConfig.IS_SHOW_LOG) {
            return Log.e(tag, msg, tr);
        } else {
            return -1;
        }
    }


    public static void i(String tagName, String msg) {
        if (BuildConfig.IS_SHOW_LOG) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.i(tagName + i, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.i(tagName + i, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }
}

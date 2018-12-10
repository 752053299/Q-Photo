package com.example.qiaoguan.photo.Http;

import android.os.Handler;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {
    private static final String TAG = "GuiwuNetwork";
    private static PhotoApis photoApis;
    private static boolean sDebug =true;
    private static final String GUIWU_BASE_URL = "http://gank.io/";
    private static OkHttpClient okHttpClient;
    private static Converter.Factory gsonConverterFactory;


    public static PhotoApis getPhotoApis(){
        if (photoApis == null){

            OkHttpClient.Builder okhttpClientBuilder = new OkHttpClient.Builder();

            if (sDebug) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                okhttpClientBuilder.addNetworkInterceptor(loggingInterceptor);
            }

            gsonConverterFactory = GsonConverterFactory.create();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okhttpClientBuilder.build())
                    .baseUrl(GUIWU_BASE_URL)
                    .addConverterFactory(gsonConverterFactory)
                    .build();
            photoApis = retrofit.create(PhotoApis.class);
        }
        return photoApis;
    }


}

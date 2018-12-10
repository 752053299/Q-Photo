package com.example.qiaoguan.photo.Http;

import com.example.qiaoguan.photo.Http.Response.GetPhotosResponse;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface PhotoApis {

    @GET("/api/random/data/福利/{num}")
    Call<GetPhotosResponse> getPhtots(@Path("num")int num);

    @GET()
    Call<ResponseBody> getOnePhoto(@Url String fileUrl);

    @GET()
    Flowable<ResponseBody> getOnePhotoRx(@Url String fileUrl);

}

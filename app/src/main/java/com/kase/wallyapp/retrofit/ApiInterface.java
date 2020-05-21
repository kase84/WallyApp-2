package com.kase.wallyapp.retrofit;

import com.kase.wallyapp.retrofit.model.Download;
import com.kase.wallyapp.retrofit.model.Photo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("photos")
    Call<List<Photo>> getPhotos(@Query("page") Integer page, @Query("per_page") Integer perPage, @Query("order_by") String orderBy);

    @GET("photos/{id}/download")
    Call<Download> getPhotoDownloadLink(@Path("id") String id);
}

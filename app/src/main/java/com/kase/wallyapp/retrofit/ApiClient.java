package com.kase.wallyapp.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new ApiInterceptor("38Z9radb6R2526wvaF5kk05gmCjm1yEGdQyhpuj8jos")).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.unsplash.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}

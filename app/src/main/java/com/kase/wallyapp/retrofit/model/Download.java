package com.kase.wallyapp.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Download {
    public String getUrl() {
        return url;
    }

    @SerializedName("url")
    @Expose
    private String url;
}
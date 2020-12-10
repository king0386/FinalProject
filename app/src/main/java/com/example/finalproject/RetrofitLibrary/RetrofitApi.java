package com.example.finalproject.RetrofitLibrary;

import com.example.finalproject.Model.BasicResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitApi {


    @GET(".")
    Call<BasicResponseModel> searchAlbum(@Query("s") String s);


    @GET(".")
    Call<BasicResponseModel> albumTrack(@Query("m") String m);

}


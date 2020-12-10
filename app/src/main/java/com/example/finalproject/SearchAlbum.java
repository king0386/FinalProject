package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Adapter.AlbumAdapter;
import com.example.finalproject.Model.AlbumModel;
import com.example.finalproject.Model.BasicResponseModel;
import com.example.finalproject.RetrofitLibrary.RetrofitApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchAlbum extends AppCompatActivity {
    String artistName;
    RecyclerView albumRecyclerView;
    LinearLayoutManager linearLayoutManager ;
    ArrayList<AlbumModel> list;
    Call<BasicResponseModel> call;
    ProgressBar progressBar;
    public boolean runningService = false;
    AlbumAdapter albumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_album);

        getArtistNameFromActivity();
        initiate();
        getAlbumSearchResult();
    }

    public void getArtistNameFromActivity()
    {
        Intent intent = getIntent();
        artistName = intent.getStringExtra("artistName");

        Log.e("tag" , "artist name is : "+artistName);
    }

 

    public void initiate()
    {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        albumRecyclerView = (RecyclerView) findViewById(R.id.albumRecyclerView);
        albumRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(com.example.finalproject.SearchAlbum.this , LinearLayoutManager.VERTICAL , false);
        albumRecyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();

    }



    private void getAlbumSearchResult() {

        //The gson builder
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();

        //creating retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.theaudiodb.com/api/v1/json/1/searchalbum.php/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        progressBar.setVisibility(View.VISIBLE);
        runningService = true;
        //creating our api
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);

        //creating a call and calling the upload image method
        call = retrofitApi.searchAlbum(artistName);

        //finally performing the call
        call.enqueue(new Callback<BasicResponseModel>() {
            @Override
            public void onResponse(Call<BasicResponseModel> call, Response<BasicResponseModel> response) {
                progressBar.setVisibility(View.GONE);
                runningService = false;
                if(response.isSuccessful())
                {
                        list = response.body().getAlbum();

                    if(list != null)
                    {
                        Log.e("tag" , "album list size is : "+list.size());


                        albumAdapter = new AlbumAdapter(com.example.finalproject.SearchAlbum.this , list , "SearchAlbum");
                        albumRecyclerView.setAdapter(albumAdapter);

                    }
                    else
                    {
                        Toast.makeText(com.example.finalproject.SearchAlbum.this, getResources().getString(R.string.artistalbumnotfound), Toast.LENGTH_SHORT).show();
                    }


                }
                else
                {
                    Toast.makeText(com.example.finalproject.SearchAlbum.this, getResources().getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<BasicResponseModel> call, Throwable t) {
                if(call.isCanceled())
                {
                    Log.e("tag" , "request is cancelled");
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    runningService = false;
                    Toast.makeText(com.example.finalproject.SearchAlbum.this , getResources().getString(R.string.serviceError), Toast.LENGTH_LONG).show();
                    Log.e("tag", "on failure error : " + t.getMessage());

                }
            }
        });
    }




}
package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Adapter.TrackAdapter;
import com.example.finalproject.Database.AlbumData;
import com.example.finalproject.Model.BasicResponseModel;
import com.example.finalproject.Model.TrackModel;
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

/**
 * The class is used as a track for the albums
 * @author Saad Abdullah
 */
public class AlbumTrack extends AppCompatActivity {
    /**
     * string storing album id
     */
    String albumId;
    /**
     * string storing album name
     */
    String albumName;
    /**
     * button that saves the album
     */
    Button saveAlbum;
    /**
     * object of recyclerview
     */
    RecyclerView trackRecyclerView;
    /**
     * object of the linear layout manager
     */
    LinearLayoutManager linearLayoutManager ;
    /**
     * arraylist for the tracks
     */
    ArrayList<TrackModel> list;
    /**
     * arraylist object of call class for http requests
     */
    Call<BasicResponseModel> call;
    /**
     * progressbar object
     */
    ProgressBar progressBar;
    /**
     * boolean to check wether service is run/not
     */
    public boolean runningService = false;
    /**
     * object of Track Adapter class
     */
    TrackAdapter trackAdapter;
    /**
     * object of Album Data class
     */
    private AlbumData albumData;

    /**
     * Function that creates the application
     * @param savedInstanceState saves current state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_track);

        getDataFromAdapter();
        initiate();
        clickListener();
        getAlbumSearchResult();
    }

    /**
     * provides data logs for adapter use
     */
    public void getDataFromAdapter()
    {
        Intent intent = getIntent();
        albumId = intent.getStringExtra("albumId");
        albumName = intent.getStringExtra("albumName");

        Log.e("tag" , "album id is : "+albumId);
        Log.e("tag" , "album name is : "+albumName);
    }

    /**
     * initialize and set values for progress bar and views
     */
    public void initiate()
    {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        saveAlbum = (Button) findViewById(R.id.saveAlbum);
        saveAlbum.setVisibility(View.GONE);
        trackRecyclerView = (RecyclerView) findViewById(R.id.trackRecyclerView);
        trackRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(AlbumTrack.this , LinearLayoutManager.VERTICAL , false);
        trackRecyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        albumData = new AlbumData(AlbumTrack.this);

    }

    /**
     * listens for a click from user on a button or item and provides toast messages
     */
    public void clickListener()
    {

        saveAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                albumData.open();
                long id = albumData.insertDataOnNewIndex(albumId , albumName , list);
                albumData.close();


                Log.e("tag" , "save album id is : "+albumId);


                if(id == 1){
                    Toast.makeText(AlbumTrack.this, getResources().getString(R.string.alreadysaved), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(AlbumTrack.this, getResources().getString(R.string.albumsaved), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


    /**
     * provides the synced search results after user requests an artist name
     */
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
                .baseUrl("https://www.theaudiodb.com/api/v1/json/1/track.php/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        progressBar.setVisibility(View.VISIBLE);
        runningService = true;
        //creating our api
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);

        //creating a call and calling the upload image method
        call = retrofitApi.albumTrack(albumId);

        //finally performing the call
        call.enqueue(new Callback<BasicResponseModel>() {
            @Override
            public void onResponse(Call<BasicResponseModel> call, Response<BasicResponseModel> response) {
                progressBar.setVisibility(View.GONE);
                runningService = false;
                if(response.isSuccessful())
                {
                    list = response.body().getTrack();

                    if(list != null)
                    {
                        Log.e("tag" , "track list size is : "+list.size());

                        saveAlbum.setVisibility(View.VISIBLE);
                        trackAdapter = new TrackAdapter(AlbumTrack.this , list);
                        trackRecyclerView.setAdapter(trackAdapter);
                    }
                    else
                    {
                        Toast.makeText(AlbumTrack.this, getResources().getString(R.string.tracknotfound), Toast.LENGTH_SHORT).show();
                    }



                    //Toast.makeText(Categories.this ,  response.body().getMessage(), Toast.LENGTH_LONG).show();

                }
                else
                {
                    Toast.makeText(AlbumTrack.this, getResources().getString(R.string.tryagain), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AlbumTrack.this , getResources().getString(R.string.serviceError), Toast.LENGTH_LONG).show();
                    Log.e("tag", "on failure error : " + t.getMessage());

                }
            }
        });
    }

}
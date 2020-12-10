package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Adapter.TrackAdapter;
import com.example.finalproject.Database.AlbumData;
import com.example.finalproject.Model.TrackModel;

import java.util.ArrayList;

public class SavedTrack extends AppCompatActivity {
    String albumId;
    String albumName;
    Button deleteAlbum;
    RecyclerView savedTrackRecyclerView;
    LinearLayoutManager linearLayoutManager ;
    ArrayList<TrackModel> list;
    private AlbumData albumData;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_track);

        getDataFromAdapter();
        initiate();
        clickListener();
        getTrackFromDB();
    }


    public void getDataFromAdapter()
    {
        Intent intent = getIntent();
        albumId = intent.getStringExtra("albumId");
        albumName = intent.getStringExtra("albumName");

        Log.e("tag" , "album id is : "+albumId);
        Log.e("tag" , "album name is : "+albumName);
    }


    public void initiate()
    {

        deleteAlbum = (Button) findViewById(R.id.deleteAlbum);
        savedTrackRecyclerView = (RecyclerView) findViewById(R.id.savedTrackRecyclerView);
        savedTrackRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(SavedTrack.this , LinearLayoutManager.VERTICAL , false);
        savedTrackRecyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        albumData = new AlbumData(SavedTrack.this);


    }

    public void clickListener()
    {

        deleteAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                albumData.open();
                long result = albumData.deleteAlbum(albumId);
                albumData.close();

                if(result == -1)
                {
                    Toast.makeText(SavedTrack.this, getResources().getString(R.string.albumnotdeleted), Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(SavedTrack.this, getResources().getString(R.string.albumdeleted), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    public void getTrackFromDB()
    {
        albumData.open();
        list = albumData.getAlbumTrackData(albumId);
        albumData.close();

        Log.e("tag" , "album list size from db : "+list.size());

        TrackAdapter trackAdapter = new TrackAdapter(SavedTrack.this , list);
        savedTrackRecyclerView.setAdapter(trackAdapter);

    }

}
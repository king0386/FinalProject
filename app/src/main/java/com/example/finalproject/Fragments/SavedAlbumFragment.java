package com.example.finalproject.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Adapter.AlbumAdapter;
import com.example.finalproject.Database.AlbumData;
import com.example.finalproject.Model.AlbumModel;
import com.example.finalproject.R;

import java.util.ArrayList;

public class SavedAlbumFragment extends Fragment {
    TextView noAlbum;
    RecyclerView savedAlbumRecyclerView;
    LinearLayoutManager linearLayoutManager ;
    ArrayList<AlbumModel> list;
    AlbumAdapter albumAdapter;
    private AlbumData albumData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_saved_album, container, false);
        initiate(root);
        return root;
    }

    public void initiate(View root)
    {

        noAlbum = (TextView) root.findViewById(R.id.noAlbum);
        savedAlbumRecyclerView = (RecyclerView) root.findViewById(R.id.savedAlbumRecyclerView);
        savedAlbumRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL , false);
        savedAlbumRecyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        albumData = new AlbumData(getActivity());

    }

    public void getAlbumFromDB()
    {
        albumData.open();
        list = albumData.getAlbumData();
        albumData.close();

        Log.e("tag" , "album list size from db : "+list.size());


        if(list.size() > 0)
        {
            noAlbum.setVisibility(View.GONE);
        }
        else
        {
            list.clear();
            noAlbum.setVisibility(View.VISIBLE);
        }
        albumAdapter = new AlbumAdapter(getActivity() , list , "SavedAlbum");
        savedAlbumRecyclerView.setAdapter(albumAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        getAlbumFromDB();

    }
}
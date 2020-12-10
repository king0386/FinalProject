package com.example.finalproject.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.finalproject.R;
import com.example.finalproject.SearchAlbum;

public class ArtistNameFragment extends Fragment {
    EditText artistName;
    String artistNameStr;
    Button searchBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_artist_name, container, false);
        initiate(root);
        clickListener();
        return root;
    }

    public void initiate(View root)
    {
        artistName = (EditText) root.findViewById(R.id.artistName);
        searchBtn = (Button) root.findViewById(R.id.searchBtn);
    }

    public void clickListener()
    {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                artistNameStr = artistName.getText().toString();
                if(artistNameStr.equals(""))
                {
                    artistName.setError(getString(R.string.enterartistname));
                }
                else
                {
                    Intent intent = new Intent(getActivity() , SearchAlbum.class);
                    intent.putExtra("artistName" , artistNameStr);
                    startActivity(intent);
                }
            }
        });
    }

}
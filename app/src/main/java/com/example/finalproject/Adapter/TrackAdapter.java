package com.example.finalproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Model.TrackModel;
import com.example.finalproject.R;

import java.util.List;


public class TrackAdapter extends RecyclerView.Adapter<com.example.finalproject.Adapter.TrackAdapter.ViewHolder>  {

    private List<TrackModel> list;
    private Context mContext;


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView trackName;

        public ViewHolder(final View view) {
            super(view);

            trackName = (TextView) view.findViewById(R.id.trackName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    String url = "http://www.google.com/search?q="+list.get(pos).getStrArtist() + " " +list.get(pos).getStrTrack();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    mContext.startActivity(i);

                }
            });
        }
    }


    public TrackAdapter(Context context , List<TrackModel> list) {
        this.list = list;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_custom_row, parent, false);
        
        return new ViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TrackModel model = list.get(position);

        holder.trackName.setText(model.getStrTrack());

    }



    @Override
    public int getItemCount() {
        return list.size();
    }
    @Override

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



}


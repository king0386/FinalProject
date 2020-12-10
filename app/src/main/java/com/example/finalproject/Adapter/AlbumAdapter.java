package com.example.finalproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.AlbumTrack;
import com.example.finalproject.Model.AlbumModel;
import com.example.finalproject.R;
import com.example.finalproject.SavedTrack;

import java.util.List;

/**
 * The class is used as an Adapter for the Artist Albums
 * @author Saad Abdullah
 */
public class AlbumAdapter extends RecyclerView.Adapter<com.example.finalproject.Adapter.AlbumAdapter.ViewHolder>  {

    /**
     * arraylist for storing albums
     */
    private List<AlbumModel> list;
    /**
     * defines context
     */
    private Context mContext;
    /**
     * string storing album's artist
     */
    private String from;


    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * textview for name of album
         */
        TextView albumName;
        /**
         * constructor of Viewholder which checks adapter properties
         * @param view
         */
        public ViewHolder(final View view) {
            super(view);

            albumName = (TextView) view.findViewById(R.id.albumName);
            
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    if(from.equals("SavedAlbum"))
                    {
                        Intent yourIntent = new Intent(mContext, SavedTrack.class);
                        yourIntent.putExtra("albumId", list.get(pos).getIdAlbum());
                        yourIntent.putExtra("albumName", list.get(pos).getStrAlbum());
                        mContext.startActivity(yourIntent);
                    }
                    else if(from.equals("SearchAlbum"))
                    {
                        Intent yourIntent = new Intent(mContext, AlbumTrack.class);
                        yourIntent.putExtra("albumId", list.get(pos).getIdAlbum());
                        yourIntent.putExtra("albumName", list.get(pos).getStrAlbum());
                        mContext.startActivity(yourIntent);
                    }
                }
            });
        }
    }

    /**
     * Constructor of this class to initalize default values
     * @param context
     * @param list album model
     * @param from artist name
     */
    public AlbumAdapter(Context context , List<AlbumModel> list , String from) {
        this.list = list;
        this.mContext = context;
        this.from = from;
    }

    /**
     * Used to initialize the viewholders
     * @param parent
     * @param viewType type of view
     * @return layout id resource
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_custom_row, parent, false);
        
        return new ViewHolder(itemView);

    }

    /**
     * Called for each viewholder to bind it to the adapter
     * @param holder to pass data into viewholder
     * @param position  where to pass the data
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        AlbumModel model = list.get(position);

        holder.albumName.setText(model.getStrAlbum());

    }


    /**
     * Returns count of items within the list
     * @return size of the list
     */
    @Override
    public int getItemCount() {
        return list.size();
    }
    /**
     * Get row id of specific location on list
     * @param position of the specific item
     * @return value of the id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns the type of view being utilized
     * @param position where the view is
     * @return type of view
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }



}


package com.example.finalproject.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.finalproject.Model.AlbumModel;
import com.example.finalproject.Model.TrackModel;

import java.util.ArrayList;


public class AlbumData {




    final static String DATABASE_NAME = "AlbumDB";
    final static int DATABASE_VERSION = 1;
    final static String CREATE_ALBUM_TABLE = "create table album_table(id integer primary key autoincrement," + "albumId text , albumName text);";
    final static String CREATE_TRACK_TABLE = "create table track_table(id integer primary key autoincrement," + "albumTableId text , trackId text , trackName text , artistName text);";


    SQLiteDatabase db;;
    final DatabaseHelper dbHelper;
    final Context context;
    int totalPrice;
    int totalQuantity;

    public AlbumData(Context con) {
        this.context = con;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }


    public long insertDataOnNewIndex(String albumId, String albumName, ArrayList<TrackModel> list)
    {
        Log.e("tag" , "insertDataOnNewIndex called : ");


        String checkQuery ="SELECT * FROM album_table WHERE albumId='"+albumId+"'";
        Cursor checkCursor= db.rawQuery(checkQuery,null);

        if(checkCursor.getCount()>0){
            return 1;

        }else{

            ContentValues album_value = new ContentValues();

            album_value.put("albumId", albumId);
            album_value.put("albumName", albumName);

            long albumTableId = db.insert("album_table", null, album_value);

            Log.e("tag" , "album table id in insert data : "+albumTableId);


            for(int j = 0 ; j < list.size() ; j++)
            {

                ContentValues track_values = new ContentValues();
                track_values.put("albumTableId" ,albumId) ;
                track_values.put("trackId" , list.get(j).getIdTrack());
                track_values.put("trackName" , list.get(j).getStrTrack());
                track_values.put("artistName" , list.get(j).getStrArtist());

                long optionTableId = db.insert("track_table", null, track_values);

                Log.e("tag" , "track table id in insert data : "+optionTableId);

            }

            return 0;

        }






    }

    public ArrayList<AlbumModel> getAlbumData()
    {
        ArrayList<AlbumModel> albumList = new ArrayList<>();


        String albumQuery = "SELECT * FROM " + "album_table";
        Cursor albumCursor = db.rawQuery(albumQuery, null);


        if (albumCursor.moveToFirst()) {

            do {

                albumList.add(new AlbumModel(albumCursor.getString(albumCursor.getColumnIndex("albumId")) , albumCursor.getString(albumCursor.getColumnIndex("albumName"))));

            } while (albumCursor.moveToNext());

        }

        albumCursor.close();
        db.close();
        return albumList;

    }



    public ArrayList<TrackModel> getAlbumTrackData(String albumId)
    {
        ArrayList<TrackModel> tackList = new ArrayList<>();


        String trackQuery ="SELECT * FROM track_table WHERE albumTableId='"+albumId+"'";
        Cursor trackCursor = db.rawQuery(trackQuery, null);


        if (trackCursor.moveToFirst()) {

            do {

                tackList.add(new TrackModel(trackCursor.getString(trackCursor.getColumnIndex("trackId")) , trackCursor.getString(trackCursor.getColumnIndex("trackName")), trackCursor.getString(trackCursor.getColumnIndex("artistName"))));

            } while (trackCursor.moveToNext());

        }

        trackCursor.close();
        db.close();
        return tackList;

    }




    public long deleteAlbum(String id)
    {
        long result = db.delete("album_table", "albumId" + "='" + id+"'", null);

        Log.e("tag" , "delete result is : "+result);

        return result;
    }




    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            try {
                database.execSQL(CREATE_ALBUM_TABLE);
                database.execSQL(CREATE_TRACK_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS CREATE_ALBUM_TABLE");
            db.execSQL("DROP TABLE IF EXISTS CREATE_TRACK_TABLE");

            onCreate(db);
        }
    }
}

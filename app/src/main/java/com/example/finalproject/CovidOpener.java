package com.example.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CovidOpener extends SQLiteOpenHelper {
    /**
     * SQLi Database Columns
     */
    protected final static String DATABASE_NAME = "covidDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "covidTable";
    public final static String COL_DATE = "date";
    public final static String COL_COUNTRY = "country";
    public final static String COL_PROVINCE = "province";
    public final static String COL_CASE = "cases";
    public final static String COL_ID = "_id";

    public CovidOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     * Gets called if there's no database already
     * @param db the db that gets created
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DATE + " TEXT,"
                + COL_COUNTRY + " TEXT,"
                + COL_PROVINCE + " TEXT,"
                +COL_CASE +" INTEGER);");  // add or remove columns
    }


    /**
     * Upgrade existing database if local version is lower than VERSION_NUM
     * @param db the database
     * @param oldVersion old database version
     * @param newVersion new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        //Create the new table:
        onCreate(db);
    }

    /**
     * Downgrades existing database if local version is higher than VERSION_NUM
     * @param db the actual database
     * @param oldVersion old database version
     * @param newVersion new database version
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        //Create the new table:
        onCreate(db);
    }
}


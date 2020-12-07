package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * CovidSearchActivity to search covid data
 *
 * @author Stewart King
 */
public class CovidSearchActivity extends AppCompatActivity {
    /**
     * The ArrayList to store search result
     */
    ArrayList<CovidSearchResult> covidResultList = new ArrayList<>();
    /**
     * Adapter for use of listview
     */
    MyAdapter myAdapter= new MyAdapter();;
    /**
     * ProgressBar to show download progress
     */
    ProgressBar progressBar;
    /**
     * The fQuery to get data from website
     */
    CovidQuery fQuery = new CovidQuery();
    /**
     * Country, province, date, caseNumber for search
     */
    private String country,province,date;
    private int caseNumber;
    /**
     * newSearch to store the object of a search result
     */
    CovidSearchResult newSearch;
    /**
     * The listView
     */
    ListView listView;
    /**
     * to connect database and execute sql
     */
    SQLiteDatabase db;


    public static final String INFOSTRING = "Click on the buttons to go through each app, or use the hamburger icon " +
            "(Navigation Drawer) to navigate through the app.";
    public static final String CREDITSTRING = "Stewart King \n040793799";


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainActivity.handleMenuClicks(this, CREDITSTRING, INFOSTRING, item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    /**
     * onCreate equivalent main method
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_search);

        country = getIntent().getStringExtra("country");
        date = getIntent().getStringExtra("date");

        listView = findViewById(R.id.listView);
        TextView headerView=findViewById(R.id.header);

        String urlString = "https://api.covid19api.com/country/" + country + "/status/confirmed/live?from=" + date + "T00:00:00Z&to=" + date + "T23:59:59Z";
        fQuery.execute(urlString);

        progressBar = findViewById(R.id.bar);
        progressBar.setVisibility(View.VISIBLE);
        Button saveButton = findViewById(R.id.save);

        saveButton.setOnClickListener(clk -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.shared_fav))
                    //What is the message:
                    .setPositiveButton("Yes", (click, arg) -> {
                        ContentValues newRowValues = new ContentValues();
                        for(int i=0;i<covidResultList.size();i++) {
                            newRowValues.put(CovidOpener.COL_DATE, date);
                            newRowValues.put(CovidOpener.COL_COUNTRY, country);
                            newRowValues.put(CovidOpener.COL_PROVINCE, covidResultList.get(i).getProvince());
                            newRowValues.put(CovidOpener.COL_CASE, covidResultList.get(i).getCase());
                            //Insert in the database:
                            CovidOpener covidOpener = new CovidOpener(this);
                            try {
                                db = covidOpener.getWritableDatabase();
                                db.insert(CovidOpener.TABLE_NAME, null, newRowValues);
                            }finally {
                                db.close();
                            }

                        }

                        //  favorites.add(country + ":" +date);
                        Toast.makeText(this, R.string.shared_add, Toast.LENGTH_SHORT).show();

                    })
                    .setNegativeButton(R.string.shared_no, (click, arg) -> {
                        Snackbar.make(headerView,R.string.shared_error,BaseTransientBottomBar.LENGTH_LONG).show();
                    })
                    .setView(getLayoutInflater().inflate(R.layout.alert_layout, null))
                    .create().show();
        });

    }
    /**
     * Inner class to JSON from site
     */
    private class CovidQuery extends AsyncTask< String, Integer, String> {

        /**
         * Loads JSON in background
         * @param args
         * @return value of result
         */
        @Override
        public String doInBackground(String ... args)
        {
            try {
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string
                JSONArray json = new JSONArray(result);
                if (json.length() > 0) {
                    for (int i=0;i<json.length();i++) {
                        JSONObject job = json.getJSONObject(i);
                        province = job.getString("Province");
                        caseNumber=job.getInt("Cases");
                        newSearch= new CovidSearchResult(province,caseNumber);
                        covidResultList.add(newSearch);
                        publishProgress(i*100/json.length());
                    }
                    publishProgress(100);
                }
            }
            catch (Exception e) {  e.printStackTrace();}
            return "done";
        }

        /**
         * onProgressUpdate shows progress
         * @param values the progress
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }


        /**
         * onPostExecute after executing doInBackground
         * @param fromDoInBackground result from doInBackground
         */
        @Override
        protected void onPostExecute(String fromDoInBackground) {
            Log.i("HTTP", fromDoInBackground);
            listView.setAdapter(myAdapter);
        }
    }
    /**
     * Inner class for listView
     */
    protected class MyAdapter extends BaseAdapter {
        /**
         * Get count of items
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return covidResultList.size();
        }


        /**
         * Gets data from specified position
         * @param position the specific position
         * @return value of data from position
         */
        @Override
        public CovidSearchResult getItem(int position){
            return covidResultList.get(position);
        }

        /**
         * Gets row id of specific position in list
         * @param position of the specific item
         * @return if of the item
         */
        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        /**
         * Gets a view that shows data from a specific position
         * @param position the position of the specified item
         * @param old the old view
         * @param parent the parent this view will get attached to
         * @return view with the value of the data
         */
        @Override
        public View getView(int position, View old, ViewGroup parent) {
            CovidSearchResult sr = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.activity_covid_search_result, parent, false);
            if (sr != null) {
                TextView searchView = view.findViewById(R.id.CovidSearchResult);
                searchView.setText(sr.getProvince()+":"+sr.getCase());
            }
            return view;
        }

    }
}
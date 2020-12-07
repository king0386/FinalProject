
package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Main CovidActivity
 * @author Stewart King
 */
public class CovidMain extends AppCompatActivity {
    /**
     * SharedPref
     */
    SharedPreferences prefs=null;
    /**
     * Arraylist of favourited list
     */
    ArrayList<String> dateList= new ArrayList<>();
    /**
     * SQLi DB
     */
    private SQLiteDatabase db;
    /**
     * ListView
     */
    SavedAdapter savedAdapter=new SavedAdapter();
    ListView savedView;
    /**
     * New fragment
     */
    CovidDetailsFragment dFragment = new CovidDetailsFragment();

    private static final String CREDIT_STRING = "Developer: Stewart King\nStudent Number: 040793799\nVersion 1.0";
    private static final String INFO_STRING = "Welcome to the Covid search page. Enter a country name and date to search. To save the " +
            "result, press the 'favourite' button. To delete a saved record, press and hold the record you want deleted.";

//    String creditString = getResources().getString(R.string.covid_credits);
//    String infoString = getResources().getString(R.string.CovidWelcome);

    /**
     * For toolbar
     * @param item that is selected
     * @return true if item was selected, false to continue navigation
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainActivity.handleMenuClicks(this, CREDIT_STRING, INFO_STRING, item);
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.covid_main_toolbar, menu);
        return true;
    }



    /**
     * onCreate/Main method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_main);
        MainActivity.registerToolbar(this, INFO_STRING, CREDIT_STRING, R.string.covidAuthor);

        loadSavedDataFromDatabase();

        boolean isTablet = findViewById(R.id.fragment) != null;
        //Get data from the saved file
        prefs=getSharedPreferences("favouriteRecord", Context.MODE_PRIVATE);
        String savedString1 = prefs.getString("country", "");
        String savedString2= prefs.getString("date","");

        EditText country1 = findViewById(R.id.country);
        EditText date1 = findViewById(R.id.date);
        country1.setText(savedString1);
        date1.setText(savedString2);

        savedView=findViewById(R.id.savedData);
        savedView.setOnItemLongClickListener((p, b, pos, id)->{
            String selectedRecord = dateList.get(pos);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.shared_delete_confirm)
                    //AlertDialog message
                    .setMessage(R.string.countryIs + selectedRecord.substring(11) + "\n"+R.string.dateIs + selectedRecord.substring(0,10))
                    .setPositiveButton(R.string.shared_yes, (click, arg) -> {
                        dateList.remove(pos);
                        deleteRecord(selectedRecord);
                        getSupportFragmentManager().beginTransaction().remove(dFragment).commit();
                        savedAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(R.string.shared_no, (click, arg) -> {
                    })
                    .setView(getLayoutInflater().inflate(R.layout.row_layout, null))
                    .create().show();
            return true;
        });

        savedView.setOnItemClickListener((list, view, position, id) -> {
            //get the detailed list data
            String s=dateList.get(position);
            String [] columns1 = {CovidOpener.COL_PROVINCE,CovidOpener.COL_CASE};

            Cursor detailResults=db.query(true,CovidOpener.TABLE_NAME,columns1, CovidOpener.COL_DATE + "= ? and "+CovidOpener.COL_COUNTRY+" =?",
                    new String[]{s.substring(0,10),s.substring(11)},null,null,null,null);
            int provinceColIndex = detailResults.getColumnIndex(CovidOpener.COL_PROVINCE);
            int caseColIndex = detailResults.getColumnIndex(CovidOpener.COL_CASE);

            ArrayList<String> detailList= new ArrayList<>();

            while(detailResults.moveToNext()) {
                String s1 = detailResults.getString(provinceColIndex);
                String s2 = detailResults.getString(caseColIndex);
                detailList.add(s1 + ":" + s2);
            }
            //bundle to transfer data
            Bundle dataToPass = new Bundle();
            dataToPass.putSerializable("ARRAYLIST", (Serializable) detailList);

            if (isTablet) {
                dFragment.setArguments(dataToPass);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, dFragment).commit();
            }
            //for Phone:
            else {
                Intent nextActivity = new Intent(this, CovidEmptyActivity.class);
                nextActivity.putExtra("BUNDLE", dataToPass);
                startActivity(nextActivity);
            }
        });

        Button searchButton = findViewById(R.id.entrySearch);

        searchButton.setOnClickListener(clk -> {
            String country=country1.getText().toString();
            String date=date1.getText().toString();
            if(country.trim().equals("")||date.trim().equals("")||!date.matches("\\d{4}[-.]\\d{1,2}[-.]\\d{1,2}")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getResources().getString(R.string.CovidErrTitle))
                        .setMessage(getResources().getString(R.string.CovidErrMsg))
                        .setNeutralButton(R.string.shared_ok,(click,args)->{})
                        .create().show();
            }else {
                Intent searchIntent = new Intent(this, CovidSearchActivity.class);
                searchIntent.putExtra("country", country);
                searchIntent.putExtra("date", date);

                saveSharedPrefs(country, date);
                startActivity(searchIntent);
            }
        });
        Button favButton = findViewById(R.id.showFavs);
        favButton.setOnClickListener(clk -> {
            dateList.clear();
            loadSavedDataFromDatabase();
            savedView.setAdapter(savedAdapter);
        });


    }

    /**
     * Save input to SharedPref
     * @param s1 country to ave
     * @param s2 date to save
     */
    private void saveSharedPrefs(String s1,String s2) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("country", s1);
        editor.putString("date",s2);
        editor.commit();
    }

    /**
     * Loads saved data from Database.
     */
    private void loadSavedDataFromDatabase() {
        //get a database connection:
        CovidOpener covidOpener = new CovidOpener(this);
        db = covidOpener.getWritableDatabase();
        String [] columns = { CovidOpener.COL_DATE,CovidOpener.COL_COUNTRY};
        Cursor results = db.query(true, CovidOpener.TABLE_NAME, columns, null, null, null, null,CovidOpener.COL_DATE , null);
        //find the column index:
        int dateColIndex = results.getColumnIndex(CovidOpener.COL_DATE);
        int countryColIndex = results.getColumnIndex(CovidOpener.COL_COUNTRY);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext()){
            String  s1 = results.getString(dateColIndex);
            String s2=results.getString(countryColIndex);
            dateList.add(s1+" "+s2);
        }
    }
    /**
     * Deletes records from Database
     */
    protected void deleteRecord(String s) {
        db.delete(CovidOpener.TABLE_NAME, CovidOpener.COL_DATE + "= ? and "+CovidOpener.COL_COUNTRY+" =?",
                new String[]{s.substring(0,10),s.substring(11)});
    }
    /**
     * The inner class for listView
     */
    protected class SavedAdapter extends BaseAdapter {
        /**
         * GetCount of items
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return dateList.size();
        }

        /**
         * Get Item of the specific position
         * @param position of specified item
         * @return value of data at the specific position
         */
        @Override
        public String getItem(int position){
            return dateList.get(position);
        }


        /**
         * Get View shows data from specific position.
         * @param position Position of the item
         * @param old Old view to resuse
         * @param parent that this view will be attached to
         * @return view with value of data for specific position
         */
        @Override
        public View getView(int position, View old, ViewGroup parent) {
            String sr = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.activity_covid_search_result, parent, false);
            if (sr != null) {
                TextView savedView = view.findViewById(R.id.CovidSearchResult);
                savedView.setText(sr);
            }
            return view;
        }

        /**
         * Get Item ID of specific row
         * @param position of item
         * @return id of the item at specific row
         */
        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
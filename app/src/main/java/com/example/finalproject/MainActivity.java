package com.example.finalproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    /**
     * The method is the entry of execute,it equivalent to main method
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar tBar = (Toolbar)findViewById(R.id.hometoolbar);
        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);
        DrawerLayout drawer = findViewById(R.id.homedrawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.shared_open, R.string.shared_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.homenav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    @Override
    /**
     * Starts the Activity's options menu.
     * @param menu The options menu.
     * @return You must return true for the menu to be displayed.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    /**
     * On Options Item Selected called when you select an item in the menu
     * @param item in the menu that was selected
     * @return True to continue
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.item1:
                startActivity(new Intent(this, TicketMain.class));
                break;
            case R.id.item2:
                startActivity(new Intent(this,RecipeFavourites.class));
                break;
            case R.id.item3:
                startActivity(new Intent(this,CovidMain.class));
                break;
            case R.id.item4:
                startActivity(new Intent(this, AudioMain.class));
                break;
        }
        return true;
    }
    @Override
    /**
     * NavigationDrawer menu selection
     * @param item The selected item
     * @return true to go to selected item
     */
    public boolean onNavigationItemSelected( MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.item1:
                startActivity(new Intent(this, TicketMain.class));
                break;
            case R.id.item2:
                startActivity(new Intent(this,RecipeFavourites.class));
                break;
            case R.id.item3:
                startActivity(new Intent(this,CovidMain.class));
                break;
            case R.id.item4:
                startActivity(new Intent(this,AudioMain.class));
                break;

        }

        DrawerLayout drawerLayout = findViewById(R.id.homedrawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}
package com.example.finalproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

/**
 * The class represents the Navigation Drawer menu
 * @author Saad Abdullah
 */
public class NavigationDrawer extends AppCompatActivity {
    /**
     * configuration options for Navigation UI methods
     */
    private AppBarConfiguration mAppBarConfiguration;
    /**
     * object of nav controller class
     */
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        /** Passing each menu ID as a set of Ids because each
         *  menu should be considered as top level destinations.
         **/
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_artist_name , R.id.nav_saved_album, R.id.nav_credit, R.id.nav_help)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.nav_artist_name){


                    navController.navigate(R.id.nav_artist_name);

                }
                if(id == R.id.nav_saved_album){


                    navController.navigate(R.id.nav_saved_album);

                }
                if(id == R.id.nav_credit){

                    new AlertDialog.Builder(NavigationDrawer.this)
                            .setTitle(getString(R.string.credits))
                            .setMessage(getString(R.string.author) + "\n" + getString(R.string.title) + "\n" + getString(R.string.version))
                            .setNeutralButton(R.string.shared_close, (dialogInterface, i) -> dialogInterface.dismiss())
                            .show();
                }
                else if(id == R.id.nav_help)
                {
                    new AlertDialog.Builder(NavigationDrawer.this)
                            .setTitle(getString(R.string.help))
                            .setMessage(getString(R.string.help_alert))
                            .setNeutralButton(R.string.shared_close, (dialogInterface, i) -> dialogInterface.dismiss())
                            .show();
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.toolbar_help) {
            new AlertDialog.Builder(NavigationDrawer.this)
                    .setTitle(getString(R.string.help))
                    .setMessage(getString(R.string.help_alert))
                    .setNeutralButton(R.string.shared_close, (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        }
        else if(id == R.id.toolbar_credit)
        {
            new AlertDialog.Builder(NavigationDrawer.this)
                    .setTitle(getString(R.string.credits))
                    .setMessage(getString(R.string.author) + "\n" + getString(R.string.title) + "\n" + getString(R.string.version))
                    .setNeutralButton(R.string.shared_close, (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        }  else if(id == R.id.toolbar_saved)
        {
            navController.navigate(R.id.nav_saved_album);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
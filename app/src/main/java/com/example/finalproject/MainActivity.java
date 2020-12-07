package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    public static final String INFO = "Click on the buttons to go through each app, or use the hamburger icon " +
            "(Navigation Drawer) to navigate through the app.";
    public static final String CREDITS = "TBA";

    /// Changes activity easier.
    /// Use this method if you want to set some extra for any of the main buttons.
    private void changeActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        // TODO: Add extra data to intent.

        startActivity(intent);
    }

    private static void changeActivityFromToolbar(AppCompatActivity app, Class<?> cls) {
        Intent intent = new Intent(app, cls);

        app.startActivity(intent);
    }

    public static void registerToolbar(AppCompatActivity app, String credit, String info, int title) {
        app.setSupportActionBar((Toolbar)app.findViewById(R.id.toolbar_top));

        app.getSupportActionBar().setTitle(getTitle(app, title));

        Toolbar toolbar = ((RelativeLayout)app.findViewById(R.id.toolbar)).findViewById(R.id.toolbar_top);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                app,
                (DrawerLayout)app.findViewById(R.id.drawer_layout),
                toolbar,
                R.string.shared_open,
                R.string.shared_close
        );

        ((DrawerLayout) app.findViewById(R.id.drawer_layout)).addDrawerListener(toggle);
        toggle.syncState();

        ((NavigationView)app.findViewById(R.id.navigation_view)).setNavigationItemSelectedListener(item -> handleMenuClicks(app, CREDITS, INFO, item));
    }

    public static void registerToolbar(AppCompatActivity app, String credit, String info) {
        registerToolbar(app, credit, info, R.string.mainmenu_audio);

        app.getSupportActionBar().setTitle(getTitle(app));
    }

    public static boolean handleMenuClicks(AppCompatActivity app, String credit, String info, MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.navbar_audio:
            case R.id.toolbar_audio:
                changeActivityFromToolbar(app, AudioMain.class);
                break;
            case R.id.navbar_covid:
            case R.id.toolbar_covid:
                changeActivityFromToolbar(app, CovidMain.class);
                break;
            case R.id.navbar_recipe:
            case R.id.toolbar_recipe:
                changeActivityFromToolbar(app, RecipeMain.class);
                break;
            case R.id.navbar_ticket:
            case R.id.toolbar_ticket:
                changeActivityFromToolbar(app, TicketMain.class);
                break;
            case R.id.navbar_credit:
            case R.id.toolbar_credit:
                new AlertDialog.Builder(app)
                        .setTitle(R.string.shared_credits)
                        .setMessage(credit)
                        .setNeutralButton(R.string.shared_close, (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
                break;
            case R.id.navbar_help:
            case R.id.toolbar_help:
                new AlertDialog.Builder(app)
                        .setTitle(R.string.shared_help)
                        .setMessage(info)
                        .setNeutralButton(R.string.shared_close, (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
                break;
            default:
            case R.id.navbar_exit:
                app.finishAndRemoveTask();
                break;
        }

        return true;
    }


    /**
     * Returns a title according to the class. (Example: Recipe)
     * @param context Context, use "this" on each activity.
     * @return String title header.
     */
    public static String getTitle(Context context) {
        String text = context.getClass().getSimpleName().toLowerCase();
        String returning = "";

        if (text.startsWith("recipe")) {
            returning += context.getString(R.string.mainmenu_recipe);
        } else if (text.startsWith("audio")) {
            returning += context.getString(R.string.mainmenu_audio);
        } else if (text.startsWith("ticket")) {
            returning += context.getString(R.string.mainmenu_ticketmaster);
        } else if (text.startsWith("covid")) {
            returning += context.getString(R.string.mainmenu_covid);
        } else {
            returning += context.getString(R.string.app_name);
        }

        return returning;
    }

    /**
     * Returns a title according to the class. (Example: Recipe - Ingredients)
     * @param context Context, use "this" on each activity.
     * @param extra_title Additional title to add.
     * @return String title header.
     */
    public static String getTitle(Context context, int extra_title) {
        return getTitle(context) + " - " + context.getString(extra_title);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainActivity.handleMenuClicks(this, CREDITS, INFO, item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerToolbar(this, CREDITS, INFO);

        Button audio_main_btn = findViewById(R.id.mainmenu_audio);
        Button covid_main_btn = findViewById(R.id.mainmenu_covid);
        Button recipe_main_btn = findViewById(R.id.mainmenu_recipe);
        Button ticket_main_btn = findViewById(R.id.mainmenu_ticket);

        audio_main_btn.setOnClickListener((View v) -> changeActivity(AudioMain.class));
        covid_main_btn.setOnClickListener((View v) -> changeActivity(CovidMain.class));
        recipe_main_btn.setOnClickListener((View v) -> changeActivity(RecipeMain.class));
        ticket_main_btn.setOnClickListener((View v) -> changeActivity(TicketMain.class));
    }
}
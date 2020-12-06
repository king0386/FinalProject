package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

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

    public static void registerToolbar(AppCompatActivity app, int title) {
        app.setSupportActionBar((Toolbar)app.findViewById(R.id.toolbar_top));

        app.getSupportActionBar().setTitle(getTitle(app, title));
    }

    public static void registerToolbar(AppCompatActivity app) {
        registerToolbar(app, R.string.mainmenu_audio);

        app.getSupportActionBar().setTitle(getTitle(app));
    }

    public static boolean handleMenuClicks(AppCompatActivity app, String credit, String info, MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.toolbar_audio:
                changeActivityFromToolbar(app, AudioMain.class);
                break;
            case R.id.toolbar_covid:
                changeActivityFromToolbar(app, CovidMain.class);
                break;
            case R.id.toolbar_recipe:
                changeActivityFromToolbar(app, RecipeMain.class);
                break;
            case R.id.toolbar_ticket:
                changeActivityFromToolbar(app, TicketMain.class);
                break;
            case R.id.toolbar_help:
                new AlertDialog.Builder(app)
                        .setTitle(R.string.shared_info)
                        .setMessage(credit)
                        .setNeutralButton(R.string.shared_close, (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
                break;
            case R.id.toolbar_info:
                new AlertDialog.Builder(app)
                        .setTitle(R.string.shared_help)
                        .setMessage(info)
                        .setNeutralButton(R.string.shared_close, (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
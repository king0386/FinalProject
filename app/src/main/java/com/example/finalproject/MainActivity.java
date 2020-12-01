package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
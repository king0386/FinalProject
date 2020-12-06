package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

public class RecipeItem extends AppCompatActivity {
    String thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_item);

        Bundle data = getIntent().getExtras();

        if (data == null)
            finish();

        int id = data.getInt("ItemID");
        boolean from_favourites = data.getBoolean("FavouriteList");

        RecipeObject item =
                from_favourites ? RecipeMain.Recipes.get(id) : RecipeMain.QueryRecipes.get(id);

        thumbnail = item.Thumbnail;

        ((TextView)findViewById(R.id.recipe_item_id)).setText(getString(R.string.shared_id_column) + " " + item.URL);
        findViewById(R.id.recipe_item_id).setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.URL));

            startActivity(browserIntent);
        });

        ((TextView)findViewById(R.id.recipe_item_title)).setText(getString(R.string.shared_title) + ": " + item.Title);

        String ingredients_text = "";

        for (String text : item.Ingredients) {
            ingredients_text += text + ", ";
        }

        ingredients_text = ingredients_text.substring(0, ingredients_text.length() - 2);

        ((TextView)findViewById(R.id.recipe_item_ingredients)).setText(getString(R.string.recipe_ingredients) + ": " + ingredients_text);


        ((Switch)findViewById(R.id.recipe_item_favourites)).setChecked(item.IsFavourite);
        ((Switch)findViewById(R.id.recipe_item_favourites)).setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                RecipeMain.addRecipe(this, item);
            else
                RecipeMain.removeRecipe(this, item);

            Snackbar.make(compoundButton,
                    "\"" + item.Title + "\" " + (item.IsFavourite ? getString(R.string.recipe_favourite_in) : getString(R.string.recipe_favourite_out)),
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.shared_undo, view -> compoundButton.toggle())
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        new RecipeItemTaskHandler().execute("run");
    }

    private class RecipeItemTaskHandler extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ((ProgressBar)findViewById(R.id.recipe_item_progressbar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);

            ((ProgressBar)findViewById(R.id.recipe_item_progressbar)).setVisibility(View.INVISIBLE);

            if (image != null)
                ((ImageView)findViewById(R.id.recipe_item_image)).setImageBitmap(image);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            ((ProgressBar)findViewById(R.id.recipe_item_progressbar)).setProgress(values[0]);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            publishProgress(0);

            try {
                String filename = URLEncoder.encode(thumbnail, "UTF-8");
                publishProgress(20);

                File fs = getBaseContext().getFileStreamPath(filename);
                publishProgress(30);

                if (fs == null || !fs.exists()) {
                    URL uri = new URL(thumbnail);

                    InputStream is = uri.openConnection().getInputStream();
                    publishProgress(50);

                    Bitmap image = BitmapFactory.decodeStream(is);
                    publishProgress(70);

                    FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    publishProgress(80);

                    fos.flush();
                    fos.close();

                    Log.i("Item", "Bitmap was downloaded from URL, and saved successfully.");


                    return image;
                }

                FileInputStream fis = openFileInput(filename);
                publishProgress(80);

                Log.i("Item", "Bitmap was cached and loaded successfully.");

                return BitmapFactory.decodeStream(fis);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
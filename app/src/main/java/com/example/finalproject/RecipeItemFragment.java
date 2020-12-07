package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLEncoder;
import java.util.function.Function;

/**
 * create an instance of this fragment.
 */
public class RecipeItemFragment extends Fragment {
    public Bundle data = null;
    public Context baseContext;
    public BaseAdapter parentAdapter;

    String thumbnail;

    private View fgmt_view;

    <T extends View> T findViewById(int id) {
        return fgmt_view.findViewById(id);
    }

    Context getBaseContext() {
        return baseContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fgmt_view = inflater.inflate(R.layout.activity_recipe_item, container, false);

        fgmt_view.findViewById(R.id.navigation_view).setVisibility(View.GONE);
        fgmt_view.findViewById(R.id.toolbar).setVisibility(View.GONE);

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
                RecipeMain.addRecipe(getBaseContext(), item);
            else
                RecipeMain.removeRecipe(getBaseContext(), item);

            Snackbar.make(compoundButton,
                    "\"" + item.Title + "\" " + (item.IsFavourite ? getString(R.string.recipe_favourite_in) : getString(R.string.recipe_favourite_out)),
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.shared_undo, view -> compoundButton.toggle())
                    .show();

            parentAdapter.notifyDataSetChanged();
        });

        (findViewById(R.id.recipe_item_close)).setOnClickListener(view -> getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit());

        return fgmt_view;
    }

    @Override
    public void onStart() {
        super.onStart();

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

                    FileOutputStream fos = getBaseContext().openFileOutput(filename, Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    publishProgress(80);

                    fos.flush();
                    fos.close();

                    Log.i("Item", "Bitmap was downloaded from URL, and saved successfully.");


                    return image;
                }

                FileInputStream fis = getBaseContext().openFileInput(filename);
                publishProgress(80);

                Log.i("Item", "Bitmap was cached and loaded successfully.");

                return BitmapFactory.decodeStream(fis);
            } catch (Exception ex) {
                return null;
            }
        }
    }

}
package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RecipeMain extends AppCompatActivity {

    public static String PREFERENCE_NAME = "recipe";

    public static List<String> Ingredients;
    public static List<RecipeObject> Recipes;
    public static List<RecipeObject> QueryRecipes;

    ProgressBar progress_bar;
    RecipeMainAdapter listadapter;

    private void changeActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);

        startActivity(intent);
    }

    private static void refreshIngredients(Context context) {
        RecipeDatabase.getInstance(context).refreshIngredients(Ingredients);
    }


    public static void addIngredient(Context context, String ingredient) {
        Ingredients.add(ingredient);

        refreshIngredients(context);
    }

    public static void removeIngredient(Context context, int index) {
        Ingredients.remove(index);

        refreshIngredients(context);
    }

    public static void addRecipe(Context context, RecipeObject recipe) {
        RecipeDatabase.getInstance(context).addRecipe(recipe);

        Recipes.add(recipe);
    }

    public static void removeRecipe(Context context, RecipeObject recipe) {
        RecipeDatabase.getInstance(context).removeRecipe(recipe);

        Recipes.remove(recipe);
    }

    public static void getUpdatePreference(Context context, EditText textBox, String title) {
        textBox.setText(
                context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
                        .getString(title, "")
        );
    }

    public static void setPreference(Context context, EditText textBox, String title) {
        context.getSharedPreferences(RecipeMain.PREFERENCE_NAME, MODE_PRIVATE)
                .edit()
                .putString(title,
                        textBox.getText().toString()
                )
                .apply();
    }

    public static String getPreferenceRaw(Context context, String title) {
        return context.getSharedPreferences(RecipeMain.PREFERENCE_NAME, MODE_PRIVATE)
                .getString(title, "");
    }

    public static void setPreferenceRaw(Context context, String title, String value) {
        context.getSharedPreferences(RecipeMain.PREFERENCE_NAME, MODE_PRIVATE)
                .edit()
                .putString(title,
                        value
                )
                .apply();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);
        setTitle(MainActivity.getTitle(this));

        Ingredients = new ArrayList<>();
        Recipes = new ArrayList<>();
        QueryRecipes = new ArrayList<>();
        listadapter = new RecipeMainAdapter();


        RecipeDatabase.getInstance(this).loadIngredients(Ingredients);
        RecipeDatabase.getInstance(this).loadRecipes(Recipes);




        findViewById(R.id.recipe_main_ingredients).setOnClickListener((View v) -> changeActivity(RecipeIngredients.class));
        findViewById(R.id.recipe_main_favourites).setOnClickListener((View v) -> changeActivity(RecipeFavourites.class));


        EditText query_box = findViewById(R.id.recipe_main_query);
        getUpdatePreference(this, query_box, "main_query");

        ((ListView)findViewById(R.id.recipe_main_list)).setAdapter(listadapter);


        findViewById(R.id.recipe_main_searchconfirm).setOnClickListener((View v) -> {
            String text = query_box.getText().toString();

            if (!text.isEmpty()) {
                setPreference(this, query_box, "main_query");

                QueryRecipes.clear();
                listadapter.notifyDataSetChanged();

                new RecipeTaskHandler().execute("new");
            }
        });

        // Adapter.
        progress_bar = findViewById(R.id.recipe_main_progressbar);

    }

    @Override
    protected void onResume() {
        super.onResume();

        new RecipeTaskHandler().execute("cache");
    }

    @Override
    protected void onPause() {
        super.onPause();

        EditText query_box = findViewById(R.id.recipe_main_query);

        if (!query_box.getText().toString().isEmpty())
            setPreference(this, query_box, "main_query");
    }

    class RecipeMainAdapter extends BaseAdapter {
        private final LayoutInflater inflater;

        public RecipeMainAdapter() {
            super();

            inflater = LayoutInflater.from(RecipeMain.this);
        }

        @Override
        public int getCount() {
            return QueryRecipes.size();
        }

        @Override
        public Object getItem(int i) {
            return QueryRecipes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            RecipeObject recipe = (RecipeObject) getItem(i);

            View v = inflater.inflate(R.layout.activity_recipe_main_item, null);

            ((TextView)v.findViewById(R.id.recipe_main_item_title)).setText(recipe.Title);

            v.findViewById(R.id.recipe_main_item_favourite).setOnClickListener((View vs) -> {
                recipe.IsFavourite = !recipe.IsFavourite;

                if (recipe.IsFavourite) {
                    addRecipe(RecipeMain.this, recipe);
                } else {
                    removeRecipe(RecipeMain.this, recipe);
                }
                //((ImageButton)vs).setImageResource(recipe.IsFavourite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

                notifyDataSetChanged();
            });

            ((ImageButton)v.findViewById(R.id.recipe_main_item_favourite))
                    .setImageResource(recipe.IsFavourite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

            return v;
        }
    }

    /// Task
    private class RecipeTaskHandler extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress_bar.setVisibility(View.VISIBLE);
            QueryRecipes.clear();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            progress_bar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            progress_bar.setVisibility(View.INVISIBLE);
            listadapter.notifyDataSetChanged();
        }

        @Override
        protected Boolean doInBackground(String... objective) {
            String task = objective[0];

            String json = "";

            publishProgress(20);


            // Cache found.
            if (task.equalsIgnoreCase("cache")) {
                json = getPreferenceRaw(RecipeMain.this, "recipe_json");
            } else {
                try {
                    String ingredients = "";

                    for (String val : Ingredients) {
                        ingredients += val + ",";
                    }

                    ingredients = ingredients.substring(0, ingredients.length() - 1);
                    ingredients = URLEncoder.encode(ingredients, StandardCharsets.UTF_8.toString())
                            .toLowerCase();

                    String query = ((EditText)RecipeMain.this.findViewById(R.id.recipe_main_query))
                            .getText()
                            .toString()
                            .toLowerCase();

                    URL url = new URL("http://www.recipepuppy.com/api/?i=" + ingredients + "&q=" + query + "&format=json");

                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                    publishProgress(70);

                    String line;
                    while((line = br.readLine()) != null)
                        json += line + "\n";

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            try {
                JSONObject json_obj = new JSONObject(json);

                publishProgress(90);

                JSONArray data = json_obj.getJSONArray("results");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject query = data.getJSONObject(i);

                    RecipeObject obj = new RecipeObject(
                            query.getString("href"),
                            query.getString("title"),
                            query.getString("ingredients"),
                            query.getString("thumbnail")
                    );

                    // Object that is loaded from JSON is in favourites.
                    if (Recipes.contains(obj)) {
                        obj.IsFavourite = true;
                    }

                    QueryRecipes.add(obj);

                }

                publishProgress(100);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            setPreferenceRaw(RecipeMain.this, "recipe_json", json);

            return true;
        }
    }
}
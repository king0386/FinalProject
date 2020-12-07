package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

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

    public static final String CREDITS = "Developer: Ali Deym\nStudent Number: 040990616\nFall 2020 CST2335.";
    private static final String INFO = "Navigate through the menus by clicking them.\n" +
            "After searching for an item, you can click the favourite icon to save it.\n" +
            "By clicking a recipe, you will have more information, as well as the picture of recipe.\n" +
            "Holding long click on a recipe will give you database information.\n" +
            "Press the Ingredients button to see the ingredients you are searching for.\n" +
            "Press the Favourite button (Star) to see your saved recipes.";

    public static String PREFERENCE_NAME = "recipe";

    public static List<String> Ingredients;
    public static List<RecipeObject> Recipes;
    public static List<RecipeObject> QueryRecipes;

    ProgressBar progress_bar;
    RecipeMainAdapter listadapter;

    RecipeItemFragment previousFragment;

    private void changeActivity(Class<?> cls, Bundle data) {
        Intent intent = new Intent(this, cls);

        if (data != null)
            intent.putExtras(data);

        startActivity(intent);
    }

    private void changeActivity(Class<?> cls) {
        changeActivity(cls, null);
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

        recipe.IsFavourite = true;
        Recipes.add(recipe);
    }

    public static void removeRecipe(Context context, RecipeObject recipe) {
        RecipeDatabase.getInstance(context).removeRecipe(recipe);

        recipe.IsFavourite = false;

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
        setContentView(R.layout.activity_recipe_main);
        MainActivity.registerToolbar(this, CREDITS, INFO);

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

        ((ListView)findViewById(R.id.recipe_main_list)).setOnItemClickListener((adapterView, view, i, l) -> {
            Bundle data = new Bundle();

            data.putInt("ItemID", i);
            data.putBoolean("FavouriteList", false);

            if (((FrameLayout)findViewById(R.id.recipe_main_framelayout)) != null) {
                RecipeItemFragment fgmt = new RecipeItemFragment();

                fgmt.parentAdapter = listadapter;
                fgmt.baseContext = getBaseContext();
                fgmt.data = data;

                previousFragment = fgmt;

                getSupportFragmentManager().beginTransaction().replace(R.id.recipe_main_framelayout, fgmt).commit();
            } else
                changeActivity(RecipeItem.class, data);
        });

        ((ListView)findViewById(R.id.recipe_main_list)).setOnItemLongClickListener((adapterView, view, i, l) -> {
            RecipeObject recipe = QueryRecipes.get(i);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.recipe_database_information)
                    .setMessage(
                            getString(R.string.shared_id_column) + " " + recipe.URL + "\n" +
                            "Index: " + i + "\n" +
                            getString(R.string.shared_title) + ": " + recipe.Title + "\n" +
                            getString(R.string.recipe_ingredients) + ": " + recipe.Ingredients + "\n" +
                            getString(R.string.recipe_thumbnail) + ": " + recipe.Thumbnail + "\n" +
                            getString(R.string.recipe_favourite_toggle) + ": " +
                                    (recipe.IsFavourite ? getString(R.string.shared_yes) : getString(R.string.shared_no)) + "\n"
                    )
                    .setNeutralButton(R.string.shared_close, (dialogInterface, i1) -> dialogInterface.dismiss())
                    .show();

            return true;
        });


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

                Toast.makeText(RecipeMain.this,
                        "\"" + recipe.Title + "\" " + (recipe.IsFavourite ? getString(R.string.recipe_favourite_in) : getString(R.string.recipe_favourite_out)),
                        Toast.LENGTH_LONG)
                        .show();

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

            if (previousFragment != null)
                getSupportFragmentManager().beginTransaction().remove(previousFragment).commit();
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
                    ingredients = URLEncoder.encode(ingredients,"UTF-8")
                            .toLowerCase();

                    String query = URLEncoder.encode(((EditText)RecipeMain.this.findViewById(R.id.recipe_main_query))
                            .getText()
                            .toString()
                            .toLowerCase(),
                            "UTF-8");

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
                            query.getString("title").trim(),
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
package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class RecipeFavourites extends AppCompatActivity {

    RecipeFavouritesAdapter adapter;

    private void changeActivity(Class<?> cls, Bundle data) {
        Intent intent = new Intent(this, cls);

        if (data != null)
            intent.putExtras(data);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_favourites);

        adapter = new RecipeFavouritesAdapter();

        ((ListView)findViewById(R.id.recipe_favourites_list)).setAdapter(adapter);

        ((ListView)findViewById(R.id.recipe_favourites_list)).setOnItemClickListener((adapterView, view, i, l) -> {
            Bundle data = new Bundle();

            data.putInt("ItemID", i);
            data.putBoolean("FavouriteList", true);

            changeActivity(RecipeItem.class, data);
        });


        ((ListView)findViewById(R.id.recipe_favourites_list)).setOnItemLongClickListener((adapterView, view, i, l) -> {
            RecipeObject recipe = RecipeMain.Recipes.get(i);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.recipe_database_information)
                    .setMessage(
                            getString(R.string.shared_id_column) + " " + recipe.URL + "\n" +
                                    "Index: " + i + "\n" +
                                    getString(R.string.shared_title) + ": " + recipe.Title + "\n" +
                                    getString(R.string.recipe_ingredients) + ": " + recipe.Ingredients + "\n" +
                                    getString(R.string.recipe_favourite_toggle) + ": " +
                                    getString(R.string.recipe_thumbnail) + ": " + recipe.Thumbnail + "\n" +
                                    (recipe.IsFavourite ? getString(R.string.shared_yes) : getString(R.string.shared_no)) + "\n"
                    )
                    .setNeutralButton(R.string.shared_close, (dialogInterface, i1) -> dialogInterface.dismiss())
                    .show();

            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();
    }

    class RecipeFavouritesAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public RecipeFavouritesAdapter() {
            super();

            inflater = LayoutInflater.from(RecipeFavourites.this);
        }

        @Override
        public int getCount() {
            return RecipeMain.Recipes.size();
        }

        @Override
        public Object getItem(int i) {
            return RecipeMain.Recipes.get(i);
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

                Toast.makeText(RecipeFavourites.this,
                        "\"" + recipe.Title + "\" " + (recipe.IsFavourite ? getString(R.string.recipe_favourite_in) : getString(R.string.recipe_favourite_out)),
                        Toast.LENGTH_LONG)
                        .show();

                RecipeMain.removeRecipe(RecipeFavourites.this, recipe);

                notifyDataSetChanged();
            });

            ((ImageButton)v.findViewById(R.id.recipe_main_item_favourite))
                    .setImageResource(android.R.drawable.btn_star_big_on);

            return v;
        }
    }
}
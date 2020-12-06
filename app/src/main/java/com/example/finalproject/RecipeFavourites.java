package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class RecipeFavourites extends AppCompatActivity {

    RecipeFavouritesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_favourites);

        adapter = new RecipeFavouritesAdapter();

        ((ListView)findViewById(R.id.recipe_favourites_list)).setAdapter(adapter);
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

                RecipeMain.removeRecipe(RecipeFavourites.this, recipe);

                notifyDataSetChanged();
            });

            ((ImageButton)v.findViewById(R.id.recipe_main_item_favourite))
                    .setImageResource(android.R.drawable.btn_star_big_on);

            return v;
        }
    }
}
package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RecipeIngredients extends AppCompatActivity {
    private static final String INFO = "To add an ingredient, simply type it in the edit box and press \"Add\" button.\n" +
            "To remove an ingredient, you can use the following options:\n" +
            "\t1- Long click on it, and confirm.\n" +
            "\t2- Click on remove icon, and confirm.\n\n" +
            "To read an ingredient in bigger font/size or to know the database ID, simply tap the title of that Ingredient.";

    IngredientsAdapter adapter;

    private ListView ingredient_list;
    private EditText ingredient_box;
    private Button ingredient_add;

    private void initViews() {
        ingredient_list = findViewById(R.id.recipe_ingredients_list);
        ingredient_box = findViewById(R.id.recipe_ingredients_query);
        ingredient_add = findViewById(R.id.recipe_ingredients_add);
    }

    private boolean deleteIngredient(int index) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.recipe_ingredient_delete_title)
                .setMessage(getString(R.string.recipe_ingredient_delete) + " \"" + RecipeMain.Ingredients.get(index) + "\"?")
                .setPositiveButton(R.string.shared_yes, (dialogInterface, i1) -> {
                    String title = RecipeMain.Ingredients.get(index);

                    RecipeMain.removeIngredient(this, index);

                    adapter.notifyDataSetChanged();

                    // Show toast.
                    Toast
                            .makeText(this, (CharSequence)(getString(R.string.shared_removed) + " " + title + "."),
                                    Toast.LENGTH_SHORT)
                            .show();
                })
                .setNegativeButton(R.string.shared_no, (dialogInterface, i1) -> dialogInterface.dismiss())
                .create()
                .show();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return MainActivity.handleMenuClicks(this, RecipeMain.CREDITS, INFO, item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_ingredients);
        MainActivity.registerToolbar(this, RecipeMain.CREDITS, INFO, R.string.recipe_ingredients);

        adapter = new IngredientsAdapter();

        initViews();

        // List view set the adapter.
        ingredient_list.setAdapter(adapter);

        // Load preference for query box.
        RecipeMain.getUpdatePreference(this, ingredient_box, "ingredient_text");

        // Ingredients add click listener.
        ingredient_add.setOnClickListener((View v) -> {
            String text = ((EditText)findViewById(R.id.recipe_ingredients_query)).getText().toString();

            if (!text.isEmpty()) {
                RecipeMain.addIngredient(this, text);
                ingredient_box.setText("");

                Toast.makeText(this,
                        getString(R.string.recipe_ingredient_added) + " \"" + text + "\".",
                        Toast.LENGTH_SHORT)
                        .show();

                adapter.notifyDataSetChanged();
            }
        });

        ingredient_list.setOnItemClickListener((adapterView, view, i, l) -> {
            new AlertDialog.Builder(this)
                    .setTitle(RecipeMain.Ingredients.get(i))
                    .setNeutralButton(R.string.shared_close, (dialogInterface, i1) -> dialogInterface.dismiss())
                    .create()
                    .show();
        });

        ingredient_list.setOnItemLongClickListener((adapterView, view, i, l) -> deleteIngredient(i));
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Ingredients text save.
        RecipeMain.setPreference(this, ingredient_box, "ingredient_text");
    }

    class IngredientsAdapter extends BaseAdapter {
        private class ViewHolder {
            public TextView ingredient;
            public Button remove;
        }

        private final List<String> elements;
        private final LayoutInflater inflater;

        public IngredientsAdapter() {
            super();

            elements = RecipeMain.Ingredients;
            inflater = LayoutInflater.from(RecipeIngredients.this);
        }

        @Override
        public int getCount() {
            return elements.size();
        }

        @Override
        public Object getItem(int i) {
            return elements.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            String ingredient = (String) getItem(i);

            View v = inflater.inflate(R.layout.activity_recipe_ingredients_item, null);

            ((TextView)v.findViewById(R.id.recipe_item_ingredient_text)).setText(ingredient);

            v.findViewById(R.id.recipe_item_ingredient_remove).setOnClickListener((vs) -> deleteIngredient(i));

            return v;
        }
    }
}
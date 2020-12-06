package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class RecipeDatabase extends SQLiteOpenHelper {
    final static int VERSION = 1;
    final static String DATABASE = "Recipe";
    final static String DATABASE_INGREDIENTS = "Ingredients";

    public final static String COL_URL = "uri";
    public final static String COL_Title = "title";
    public final static String COL_Ingredients = "ingredients";
    public final static String COL_Thumbnail = "thumbnail";


    private static RecipeDatabase _instance;

    public static synchronized RecipeDatabase getInstance(Context context) {
        if (_instance == null)
            _instance = new RecipeDatabase(context.getApplicationContext());

        return _instance;
    }

    public RecipeDatabase(Context ctx) {
        super(ctx, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DATABASE + "(" +
                COL_URL + " TEXT PRIMARY KEY NOT NULL," +
                COL_Title + " TEXT NOT NULL," +
                COL_Ingredients + " TEXT," +
                COL_Thumbnail + " TEXT" +
                ");"
        );

        db.execSQL("CREATE TABLE " + DATABASE_INGREDIENTS + " (" +
                COL_Title + " TEXT NOT NULL" +
                ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_INGREDIENTS + ";");

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion); // Totally safe to do because we are going to do the same thing anyway.
    }


    private ContentValues createRecipeValues(RecipeObject recipe) {
        ContentValues values = new ContentValues();

        values.put(COL_URL, recipe.URL);
        values.put(COL_Title, recipe.Title);

        String ingredients_value = "";

        for (String ingredient : recipe.Ingredients) {
            ingredients_value += ingredient + ",";
        }

        ingredients_value = ingredients_value.substring(0, ingredients_value.length() - 1);

        values.put(COL_Ingredients, ingredients_value);
        values.put(COL_Thumbnail, recipe.Thumbnail);

        return values;
    }

    private ContentValues createIngredientValues(String title) {
        ContentValues values = new ContentValues();

        values.put(COL_Title, title);

        return values;
    }

    public void addRecipe(RecipeObject recipe) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(
                DATABASE,
                null,
                createRecipeValues(recipe)
        );
    }

    public int removeRecipe(RecipeObject recipe) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = COL_URL + " = ?";
        String selection_args[] = {recipe.URL};

        return db.delete(
                DATABASE,
                selection,
                selection_args
        );
    }

    public void loadRecipes(List<RecipeObject> recipes) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor query = db.query(
                DATABASE,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (query.moveToFirst()) {
            while (!query.isAfterLast()) {
                String URL = query.getString(query.getColumnIndex(COL_URL));
                String Title = query.getString(query.getColumnIndex(COL_Title)).trim();
                String Ingredients = query.getString(query.getColumnIndex(COL_Ingredients));
                String Thumbnail = query.getString(query.getColumnIndex(COL_Thumbnail));

                RecipeObject recipe = new RecipeObject(URL, Title, Ingredients, Thumbnail, true);

                recipes.add(recipe);

                query.moveToNext();
            }
        }
    }

    public void loadIngredients(List<String> ingredients) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor query = db.query(
                DATABASE_INGREDIENTS,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (query.moveToFirst()) {
            while (!query.isAfterLast()) {
                ingredients.add(query.getString(query.getColumnIndex(COL_Title)));

                query.moveToNext();
            }
        }
    }

    public void refreshIngredients(List<String> ingredients) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + DATABASE_INGREDIENTS + ";");

        db.beginTransaction();

        for (String vals :
                ingredients) {
            db.insert(
                    DATABASE_INGREDIENTS,
                    null,
                    createIngredientValues(vals)
            );
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }
}

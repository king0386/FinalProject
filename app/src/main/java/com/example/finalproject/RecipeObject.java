package com.example.finalproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeObject {
    public String URL;
    public String Title;
    public List<String> Ingredients;
    public String Thumbnail;
    public boolean IsFavourite;

    public RecipeObject(String url, String title, String ingredients, String thumbnail) {
        URL = url;
        Title = title;

        Ingredients = new ArrayList<>();

        for (String ingredient :
                ingredients.split(",")) {
            Ingredients.add(ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1));
        }

        Thumbnail = thumbnail;

        IsFavourite = false;
    }

    public RecipeObject(String url, String title, String ingredients, String thumbnail, boolean isFavourite) {
        this(url, title, ingredients, thumbnail);

        IsFavourite = isFavourite;
    }

    // Auto generated equals and hashCode.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeObject that = (RecipeObject) o;
        return URL.equals(that.URL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(URL);
    }
}

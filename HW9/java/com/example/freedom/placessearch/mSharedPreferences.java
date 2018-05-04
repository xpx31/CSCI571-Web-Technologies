package com.example.freedom.placessearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mSharedPreferences {
    public static final String PREFS_NAME = "FAV";
    public static final String FAVORITES = "Favorite";
    public mSharedPreferences(){
        super();
    }

    public  void saveFavorites(Context context, List<displayBasics> fav){
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String favJson = gson.toJson(fav);
        editor.putString(FAVORITES, favJson);
        editor.commit();
    }

    public void addFavorite(Context context, displayBasics entry) {
        List<displayBasics> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<displayBasics>();
        favorites.add(entry);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, displayBasics entry) {
        List<displayBasics> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(entry);
            saveFavorites(context, favorites);
        }
    }

    public List<displayBasics> getFavorites(Context context) {
        SharedPreferences settings;
        List<displayBasics> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            displayBasics[] favoriteItems = gson.fromJson(jsonFavorites,
                    displayBasics[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<displayBasics>(favorites);
        } else
            return null;

        return (List<displayBasics>) favorites;
    }
}

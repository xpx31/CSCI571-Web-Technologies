package com.example.freedom.placessearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreference {
    public static final String PREFS_NAME = "FAV";
    public static final String FAVORITES = "Favorite";
    public SharedPreference(){
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
        Log.v("addFavorite", "true");
        List<displayBasics> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<displayBasics>();
        favorites.add(entry);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, displayBasics entry) {
        List<displayBasics> favorites = getFavorites(context);
        if (favorites != null) {

//            favorites.remove(entry);

            for (int i = 0 ; i < favorites.size() ; i++) {
                displayBasics product = favorites.get(i);
                try {
                    if (product.getPlaceID().equals(entry.getPlaceID())) {
                        favorites.remove(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.v("removeFavorite", "true");
        saveFavorites(context, favorites);
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

    public boolean isInFav(JSONObject entry, Context context){
        boolean isInFav = false;
        List<displayBasics> favorites = getFavorites(context);
        if (favorites != null) {
            for (displayBasics product : favorites) {
                try {
                    if (product.getName().equals(entry.getString("name"))) {
                        if (product.getAddress().equals(entry.getString("vicinity"))){
                            isInFav = true;
                            break;
                        }
                        else{isInFav = false;}
                    }
                    else{isInFav = false;}
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return isInFav;
    }



}

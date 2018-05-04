package com.example.freedom.placessearch;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class displayBasics {

    private String mapLat, mapLon, name, vicinity, icon, placeID;
    // TODO implement favorite
    private boolean fav = false;
    private SharedPreference sharedPreference;

    public displayBasics(JSONObject nearByEntry, int i, Context context){
        sharedPreference = new SharedPreference();
        try {
            if(i < 20) {
                this.mapLat = nearByEntry.getJSONObject(String.valueOf(i)).getString("mapLat");
                this.mapLon = nearByEntry.getJSONObject(String.valueOf(i)).getString("mapLon");
                this.name = nearByEntry.getJSONObject(String.valueOf(i)).getString("name");
                this.vicinity = nearByEntry.getJSONObject(String.valueOf(i)).getString("vicinity");
                this.icon = nearByEntry.getJSONObject(String.valueOf(i)).getString("icon");
                this.placeID = nearByEntry.getJSONObject(String.valueOf(i)).getString("place_id");
                this.fav = sharedPreference.isInFav(nearByEntry.getJSONObject(String.valueOf(i)), context);
            }
        }
        catch (JSONException err){
            Log.v("No Record", String.valueOf(err));
        }
    }


    public String getMapLat(){
        return mapLat;
    }
    public String getMapLon(){
        return mapLon;
    }
    public String getName(){
        return name;
    }
    public String getIcon(){
        return icon;
    }
    public String getAddress(){
        return vicinity;
    }
    public String getPlaceID(){
        return placeID;
    }
    public boolean getFav(){ return fav;}
    public void setFav(){this.fav = true;}
    public void setUnFav() {this.fav = false;}
}


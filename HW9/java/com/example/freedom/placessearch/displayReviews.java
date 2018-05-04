package com.example.freedom.placessearch;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class displayReviews{

    private String[] attr = {"author_name", "profile_photo_url", "author_url", "rating", "text", "time"};
    private String[] yelpReviewAttr = {"url", "rating", "text", "time_created"};
    private String[] yelpReviewAuthor = {"name", "image_url"};
    private List<String> reviewData = new ArrayList<String>();

    public displayReviews(JSONObject reviewObj, int platform){
        try {
            // Google Platform
            if(platform == 0){
                Log.v("displayReview", "google platform");
                for(int i = 0; i < attr.length; i++){
                    reviewData.add(i, reviewObj.getString(attr[i]));
                }
            }
//            Yelp Platform
            else{
                Log.v("displayReview", "yelp platform");
                for(int i = 0; i < attr.length; i++){
                    if(i < 2){
                        reviewData.add(i, reviewObj.getJSONObject("user").getString(yelpReviewAuthor[i]));
                    }
                    else{
                        reviewData.add(i, reviewObj.getString(yelpReviewAttr[i - 2]));
                    }
                }
            }
        }
        catch (JSONException err){
            Log.v("json parse err", String.valueOf(err));
        }
    }

    public String getAuthorName(){return reviewData.get(0);}
    public String getProfileURL(){return reviewData.get(1);}
    public String getAuthorURL(){return reviewData.get(2);}
    public String getReviewRating(){return reviewData.get(3);}
    public String getReviewText(){return reviewData.get(4);}
    public String getReviewTime(){return reviewData.get(5);}

}


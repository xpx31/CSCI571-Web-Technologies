package com.example.freedom.placessearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class getDetails {
    public List<displayBasics> entryList = new ArrayList<>();
    private Context context;
    final private String awsURL = "http://place.pmx3jxpte5.us-west-1.elasticbeanstalk.com";
    int position;
    public int detailPageReadyCount = 0;
    private int photoIndex = 0;
    private int photoCount = 0;
    private Intent detailIntent;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private ProgressDialog pd;
    private String[] placeAttr = {"name", "formatted_address", "formatted_phone_number", "price_level", "rating", "url", "website"};
    private String[] entryAttr = {"name", "address", "phone", "price", "rating", "gPage", "websiteURL"};
    private String[] yelpAttr = {"name", "addr", "city", "state", "country", "latitude", "longtitude"};


    public getDetails(List<displayBasics> entryList, int position, Context context){
        Log.v("getDetails", "getting details");
        this.position = position;
        this.entryList = entryList;
        this.context = context;
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(context, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(context, null);
        displayBasics clickedItem = entryList.get(position);
        String entryMapLatStr = clickedItem.getMapLat();
        String entryMapLonStr = clickedItem.getMapLon();
        String entryPlaceIDStr = clickedItem.getPlaceID();
        getAllDetails(entryPlaceIDStr, entryMapLatStr, entryMapLonStr);
    }


    public void getAllDetails(String placeID, String mapLat, String mapLon) {
        RequestQueue rq = Volley.newRequestQueue(context);
        String rqStr = awsURL + "/details?" + "placeId=" + placeID;

        // Show Progress Dialog
        pd = new ProgressDialog(context);
        pd.setMessage("Fetching results");
        pd.show();

        //                            intent from this activity to the next
        detailIntent = new Intent(context, detailResult.class);
        // Get Info Tab Details
        getPhotoDetails(placeID);
        getInfoDetails(rq, rqStr); // getting info and google reviews
        initMap(mapLat, mapLon);
    }

    public void getInfoDetails(String placeId) {
        Log.v("getInforDetails", "Getting Info details");
        mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
//                    intent from this activity to the next
                    Intent detailIntent = new Intent(context, detailResult.class);

                    PlaceBufferResponse places = task.getResult();
                    Place myPlace = places.get(0);

                    Log.v("get info", "Place found: " + myPlace.getName());
                    Log.v("get info", "Place found: " + myPlace.getAddress());
                    Log.v("get info", "Place found: " + myPlace.getPhoneNumber());
                    Log.v("get info", "Place found: " + myPlace.getWebsiteUri());
                    Log.v("get info", "Place found: " + myPlace.getPriceLevel());
                    Log.v("get info", "Place found: " + myPlace.getRating());

                    detailIntent.putExtra("name", myPlace.getName().toString());
                    detailIntent.putExtra("address", myPlace.getAddress().toString());
                    detailIntent.putExtra("phone", myPlace.getPhoneNumber().toString());
                    detailIntent.putExtra("websiteURL", myPlace.getWebsiteUri().toString());
                    detailIntent.putExtra("price", myPlace.getPriceLevel());
                    detailIntent.putExtra("rating", myPlace.getRating());
                    context.startActivity(detailIntent);
                    places.release();
                } else {
                    Log.v("get info", "Place not found.");
                }
            }
        });
    }

    public void getInfoDetails(final RequestQueue rq, String rqStr){
        Log.v("get info", "Reqeust String: " + rqStr);
        JsonObjectRequest jq = new JsonObjectRequest(Request.Method.GET, rqStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.v("detail data", "data: " + String.valueOf(response));
                            JSONObject placeResults = response.getJSONObject("result");
                            String statusStr = response.getString("status");
                            Log.v("detail status", "status: " + statusStr);

                            if(!statusStr.equals("OK")){
                                throw new JSONException("SERVER ERROR");
                            }
                            else{
                                String entryTemp;
                                // put strings onto intent
                                for(int i = 0 ; i < placeAttr.length; i++){
                                    try{
                                        entryTemp = placeResults.getString(placeAttr[i]);
                                    }
                                    catch (JSONException err){
                                        entryTemp = null;
                                    }
                                    detailIntent.putExtra(entryAttr[i], entryTemp);
                                }

                                // Get google reviews
                                getGoogleReviews(response);
                                getYelpReviews(rq, response);
                            }
                        } catch (JSONException err) { // JSON Parsing Error
                            err.printStackTrace();
                            Log.v("detail data", "err: " + String.valueOf(err));
                            Toast.makeText(context, String.valueOf(err), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse (VolleyError error){ // Server Response Error
                        Log.v("request err", String.valueOf(error));
                    }
                }
        );
        rq.add(jq);
    }

    public void getPhotoDetails(String placeId){
        photoCount = 0;
        Log.v("get photo details", "entered in method");
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        // Add Completion listener
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // 0 based
                photoIndex = 0;
                try{
                    while (true){
                        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(photoIndex);
                        getAllPhotos(photoMetadata, photoIndex);
                        photoIndex++;
                    }
                }
                catch (IllegalStateException err){
                    Log.v("get photo", "photo number " + String.valueOf(photoIndex));
                    if(photoIndex == 0){
                        updatePageReadyCount(detailIntent);
                    }
                }

                photoMetadataBuffer.release();
            }
        });
    }

    public void getAllPhotos(PlacePhotoMetadata photoMetadata, final int pCount){
        // Get a full-size bitmap for the photo.
        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                // Get Photo
                PlacePhotoResponse photo = task.getResult();
                Bitmap bitmap = photo.getBitmap();
                // Save to local storage
                String filename = String.valueOf(pCount) + ".png";
                Log.v("get photo", "image name: " + filename);
                ContextWrapper cw = new ContextWrapper(context);
                File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File path = new File(dir, filename);
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(path);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                }
                catch (Exception e){
                    Log.v("get photo", "cannot save at local storage");
                }
                Log.v("get photo", "photo page ready");
                String imagePath = dir.getAbsolutePath();
                // Put on intent
                updatePhotoCount(detailIntent, imagePath , pCount);
            }
        });
    }

    public void updatePhotoCount(Intent detailIntent, String imagePath, int pCount){
        // 0 based
        photoCount++;
        if(photoCount < photoIndex){
            Log.v("photo count", String.valueOf(photoCount));
        }
        else{
            Log.v("get photo", "Photo path: " + imagePath);
            detailIntent.putExtra("photo", imagePath);
            detailIntent.putExtra("photoCount", pCount + 1);
            updatePageReadyCount(detailIntent);
        }
    }

    public void initMap(String mapLat, String mapLon){
        detailIntent.putExtra("mapLat", mapLat);
        detailIntent.putExtra("mapLon", mapLon);
        Log.v("initMap", "map geo-location ready" + mapLat + "," + mapLon);
        updatePageReadyCount(detailIntent);
    }

    public void getGoogleReviews(JSONObject response){
        try {
            String statusStr = response.getString("status");
            if(!statusStr.equals("OK")){
                //TODO toast
                throw new JSONException("SERVER ERROR");
            }
            else{
                JSONArray googleReviews = response.getJSONObject("result").getJSONArray("reviews");
                int googleReviewLength = googleReviews.length();
                for(int i = 0 ; i < googleReviewLength; i++){
                    detailIntent.putExtra("gReview"+String.valueOf(i), googleReviews.getJSONObject(i).toString());
                    Log.v("send gReview", googleReviews.getJSONObject(i).toString());
                }
            }
        }
        catch (JSONException err){
            Log.v("getGoogleReviews", err.getMessage());
        }
    }

    public void getYelpReviews(RequestQueue rq, JSONObject response){
        try {
            String statusStr = response.getString("status");

            if(!statusStr.equals("OK")){
                // TODO toast
                throw new JSONException("SERVER ERROR");
            }
            else{
                Log.v("getYelpReviews", "getting yelp reviews key");
                JSONObject resultObj = response.getJSONObject("result");
                JSONArray addressComp = resultObj.getJSONArray("address_components");
                Log.v("addressComp", addressComp.toString());
                String[] yelpRstKeys = new String[yelpAttr.length];
                // name
                yelpRstKeys[0] = (resultObj.getString("name")).replaceAll("\\s+", "+");
                Log.v("getYelpReviews", yelpRstKeys[0]);
                // address
                yelpRstKeys[1] = (resultObj.getString("formatted_address").split(",")[0]).replaceAll("\\s+", "_");

                for(int i = 0; i < addressComp.length(); i++){
                    // city
                    if(addressComp.getJSONObject(i).getJSONArray("types").getString(0).equals("locality")){
                        yelpRstKeys[2] = (addressComp.getJSONObject(i).getString("long_name")).replaceAll("\\s+", "+");
                    }
                    // state
                    else if(addressComp.getJSONObject(i).getJSONArray("types").getString(0).equals("administrative_area_level_1")){
                        yelpRstKeys[3] = (addressComp.getJSONObject(i).getString("short_name")).replaceAll("\\s+", "+");
                    }
                    // country
                    else if(addressComp.getJSONObject(i).getJSONArray("types").getString(0).equals("country")){
                        yelpRstKeys[4] = (addressComp.getJSONObject(i).getString("short_name")).replaceAll("\\s+", "+");
                    }
                }
                // Lat and Lon
                yelpRstKeys[5] = resultObj.getJSONObject("geometry").getJSONObject("location").getString("lat");
                yelpRstKeys[6] = resultObj.getJSONObject("geometry").getJSONObject("location").getString("lng");

                sendYelpRequest(rq, yelpRstKeys);
            }
        }
        catch (JSONException err){
            Log.v("getYelpReviews", err.getMessage());
            Toast.makeText(context, String.valueOf(err), Toast.LENGTH_SHORT).show();
        }
    }

    public void sendYelpRequest(RequestQueue rq, String[] yelpRstKeys){
        String rqStr = awsURL + "/bestMatch?" + yelpAttr[0] + "=" + yelpRstKeys[0];
        for(int i = 1 ; i < yelpRstKeys.length; i++){
            rqStr += "&" + yelpAttr[i] + "=" + yelpRstKeys[i];
        }

        Log.v("send yelp request", "Reqeust String: " + rqStr);
        JsonArrayRequest jq = new JsonArrayRequest(Request.Method.GET, rqStr, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.v("detailYelp", "yelp: " + String.valueOf(response));

                            if(response == null){
                                Log.v("detailYelp", "no yelp reviews");
                                throw new JSONException("No Yelp Review Record, or encounter Yelp server error");
                            }
                            else{
                                int yelpReviewLength = response.length();
                                for(int i = 0 ; i < yelpReviewLength; i++){
                                    Log.v("detailYelp", "entry: " + String.valueOf(i));
                                    detailIntent.putExtra("yReview"+String.valueOf(i), (response.getJSONObject(i)).toString());
                                    Log.v("send yReview", (response.getJSONObject(i)).toString());
                                }

                                Log.v("infoAndReview", "Info page and Review Page ready");
                                updatePageReadyCount(detailIntent);
                            }
                        } catch (JSONException err) { // JSON Parsing Error
                            err.printStackTrace();
                            Log.v("receiving yelp review", "err: " + String.valueOf(err));
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse (VolleyError error){ // Server Response Error
                        Log.v("yelp request", String.valueOf(error));
//                        Toast.makeText(getApplicationContext(), "No Yelp Review", Toast.LENGTH_SHORT).show();
                        Log.v("infoAndReview", "Info page and Review Page ready");
                        updatePageReadyCount(detailIntent);
                    }
                }
        );
        rq.add(jq);
    }

    // updates detail page count and send intent if all pages are ready
    public void updatePageReadyCount(Intent detailIntent){
        detailPageReadyCount++;
        if(detailPageReadyCount < 3){
            Log.v("page ready count", String.valueOf(detailPageReadyCount));
        }
        else{ // detailPageReadyCount == 3
            context.startActivity(detailIntent);
            pd.dismiss();
            detailPageReadyCount = 0;
            Log.v("reset page ready count", String.valueOf(detailPageReadyCount));
        }
    }

}

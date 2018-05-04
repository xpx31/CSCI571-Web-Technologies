package com.example.freedom.placessearch;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class basicsResult extends AppCompatActivity implements displayBasicsAdapter.OnItemClickListener {
    final private String awsURL = "http://place.pmx3jxpte5.us-west-1.elasticbeanstalk.com";
    public List<displayBasics> entryList = new ArrayList<>();
    public int detailPageReadyCount = 0;
    private int photoIndex = 0;
    private int photoCount = 0;
    private int maxPage = 0;
    private int currentPage = 0;
    private int maxEntry = 20;
    private String nextPageToken;
    private Intent detailIntent;
    private ProgressDialog pd;
    private Button preButton;
    private Button nxtButton;
    private int[] hearts = {R.drawable.heart_fill_white_with_black_outline, R.drawable.heart_fill_red};
    private String[] placeAttr = {"name", "formatted_address", "formatted_phone_number", "price_level", "rating", "url", "website"};
    private String[] entryAttr = {"name", "address", "phone", "price", "rating", "gPage", "websiteURL"};
    private String[] yelpAttr = {"name", "addr", "city", "state", "country", "latitude", "longtitude"};
    private String[] entryYelpAttr = {"name", "formatted_address", "locality", "administrative_area_level_1", "country", "lat", "lng"};
    private String[] googleAttr = {"author_name", "author_url", "profile_photo_url","rating", "text", "time"};
    //    private String[] entryInfo = new String[placeAttr.length];
//    private byte[] entryPhoto;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private RecyclerView recyclerView;
    private displayBasicsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView favRecyclerView;
    private displayBasicsListAdapter fAdapter;
    private RecyclerView.LayoutManager favLayoutManager;

    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set detail page count to 0 for receiving new detail data
        entryList.clear();
        detailPageReadyCount = 0;
        photoCount = 0;
        currentPage = 0;
        maxPage = 0;
        setTitle("Search results");
        setContentView(R.layout.activity_basics_result);
        preButton = findViewById(R.id.preBtn);
        nxtButton = findViewById(R.id.nxtBtn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreference = new SharedPreference();

        // Construct a GeoDataClient.
//        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        // Disable page buttons
        preButton.setEnabled(false);
        nxtButton.setEnabled(false);

        Intent basicsIntent = getIntent();
        String geoLocStr = basicsIntent.getStringExtra("geoLoc");
        String nearByStr = basicsIntent.getStringExtra("nearBy");

        try {
            JSONObject geoLoc = new JSONObject(geoLocStr.trim());
            JSONObject nearBy = new JSONObject(nearByStr.trim());
            Log.v("geoLoc", String.valueOf(geoLoc));
            Log.v("nearBy", String.valueOf(nearBy));

            if(nearBy.has("0")){
                findViewById(R.id.no_record).setVisibility(View.GONE);
                findViewById(R.id.basicsList).setVisibility(View.VISIBLE);
                getPageData(nearBy);
                // show first page
                showPage(currentPage);
                setPageBtnListeners();
            }
            else{
                findViewById(R.id.no_record).setVisibility(View.VISIBLE);
                findViewById(R.id.basicsList).setVisibility(View.GONE);
            }
        } catch (JSONException err) {
            // TODO show no record case
            Log.v("json parse err", String.valueOf(err));
            findViewById(R.id.no_record).setVisibility(View.VISIBLE);
            findViewById(R.id.basicsList).setVisibility(View.GONE);
        }
    }

    public void showPage(int pageNum){
        togglePageBtn();
        List<displayBasics> list = new ArrayList<>();

        for(int i = pageNum * maxEntry; i < (pageNum + 1) * maxEntry; i++){
            try {
                list.add(entryList.get(i));
            }
            catch (IndexOutOfBoundsException e){
            }
        }


        // Set up the fist page
        recyclerView = (RecyclerView) findViewById(R.id.basicsList);
        mAdapter = new displayBasicsAdapter(list, this);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        // Set on click listener
        mAdapter.setOnItemClickListener(basicsResult.this);
    }

    public void togglePageBtn(){
        Log.v("togglePageBtn", "current page number: " + String.valueOf(currentPage) + ", max page: " + String.valueOf(maxPage));
        Log.v("togglePageBtn", "nextPageToken = " + nextPageToken);

        if((!nextPageToken.equals("null") && currentPage == maxPage) || (currentPage < maxPage)){
            nxtButton.setEnabled(true);
        }
        else{
            nxtButton.setEnabled(false);
        }

        if(currentPage == 0){
            preButton.setEnabled(false);
        }
        else{
            preButton.setEnabled(true);
        }
    }

    public void setPageBtnListeners(){
        if((!nextPageToken.equals("null") && currentPage == maxPage) || (currentPage < maxPage)){
            nxtButton.setEnabled(true);
            setNextBtnListener();
            setPreBtnListener();
        }
    }

    public void setNextBtnListener(){
        nxtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextBtnPressed();
            }
        });
    }

    public void onNextBtnPressed() {
        Log.v("onNextBtnPressed", nextPageToken);
        currentPage++;
        if(currentPage <= maxPage){
            showPage(currentPage);
        }
        else { // getting new page
            maxPage = currentPage;
            String rqStr = awsURL + "/page?" + "pageToken=" + nextPageToken;
            RequestQueue rq = Volley.newRequestQueue(this);

            // Show Progress Dialog
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Fetching results");
            pd.show();

            JsonObjectRequest jq = new JsonObjectRequest(Request.Method.GET, rqStr, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("nextPage", "data: " + String.valueOf(response));
                            getPageData(response);
                            pd.dismiss();
                            showPage(currentPage);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { // Server Response Error
                            Log.v("request err", String.valueOf(error));
                        }
                    }
            );
            rq.add(jq);
        }
    }

    public void setPreBtnListener(){
        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreBtnPressed();
            }
        });
    }

    public void onPreBtnPressed() {
        currentPage--;
        showPage(currentPage);
    }

    public void getPageData(JSONObject nearBy){
        // Add entries to list
        int nearByLength = nearBy.length();

        try {
            nextPageToken = nearBy.getString("nextPage");
        }
        catch (JSONException e){
            Log.v("nextPageToken", "parsing error");
        }


        for (int i = 0; i < nearByLength; i++) {
            displayBasics entry = new displayBasics(nearBy, i, this);
            try{
                Log.v("basicResult", entry.getName());
                entryList.add(entry);
            }
            catch (NullPointerException e){
                Log.v("basicResult", "index stop at " + String.valueOf(i));
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.v("Basics Result", "Item " + String.valueOf(position) + " clicked.");
//        detailPageReadyCount = 0;
//        displayBasics clickedItem = entryList.get(position);
//        String entryMapLatStr = clickedItem.getMapLat();
//        String entryMapLonStr = clickedItem.getMapLon();
//        String entryPlaceIDStr = clickedItem.getPlaceID();
//        getAllDetails(entryPlaceIDStr, entryMapLatStr, entryMapLonStr);
        getDetails getDetails = new getDetails(entryList, position, this);
    }


    public void getAllDetails(String placeID, String mapLat, String mapLon) {
        RequestQueue rq = Volley.newRequestQueue(basicsResult.this);
        String rqStr = awsURL + "/details?" + "placeId=" + placeID;

        // Show Progress Dialog
        pd = new ProgressDialog(basicsResult.this);
        pd.setMessage("Fetching results");
        pd.show();

        //                            intent from this activity to the next
        detailIntent = new Intent(basicsResult.this, detailResult.class);
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
                    Intent detailIntent = new Intent(basicsResult.this, detailResult.class);

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
                    startActivity(detailIntent);
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
                            Toast.makeText(getApplicationContext(), String.valueOf(err), Toast.LENGTH_SHORT).show();
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
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
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
            Toast.makeText(getApplicationContext(), String.valueOf(err), Toast.LENGTH_SHORT).show();
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
            startActivity(detailIntent);
            pd.dismiss();
            detailPageReadyCount = 0;
            Log.v("reset page ready count", String.valueOf(detailPageReadyCount));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}


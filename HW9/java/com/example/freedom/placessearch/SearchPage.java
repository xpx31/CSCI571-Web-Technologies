package com.example.freedom.placessearch;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchPage extends Fragment implements View.OnClickListener, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    final private String awsURL = "http://place.pmx3jxpte5.us-west-1.elasticbeanstalk.com";
    final private int MIN_TIME_BW_UPDATES = 1000;
    final private int MIN_DISTANCE_BW_UPDATES = 500;
    private Context searchContext;
    private int PERMISSION_FINE_LOCATION = 0;
    private int PERMISSION_COASE_LOCATION = 1;
    private View view;
    private EditText keywordText;
    private String keyword;
    private TextView keywordWarning;
    private Spinner categoryText;
    private String category;
    private EditText distanceText;
    private int distance;
    private RadioGroup fromOption;
    private int fromSelected;
    private AutoCompleteTextView locationText;
    private String location;
    private TextView locationWarning;
    private Location loc;
    private GoogleApiClient googleApiClient;
    private AddressAutoCompleteAdapter AACAdapter;
    private LatLngBounds lat_lon_bounds = new LatLngBounds(new LatLng(-85, 180), new LatLng(85, -180));

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchPage.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchPage newInstance(String param1, String param2) {
        SearchPage fragment = new SearchPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loc = getHereLoc();
        view = inflater.inflate(R.layout.search_page_layout, container, false);

        Button searchButton = (Button) view.findViewById(R.id.searchBtn);
        searchButton.setOnClickListener(this);
        Button clearButton = (Button) view.findViewById(R.id.clearBtn);
        clearButton.setOnClickListener(this);

        googleApiClient = new GoogleApiClient
                .Builder(getActivity().getApplicationContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        // define all form variables
        defineVar(view);
        searchContext = getActivity().getApplicationContext();
        return view;
    }

    public boolean checkNetworkConnection(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if(isConnected == false){
            Toast.makeText(view.getContext(), "No Network Connection", Toast.LENGTH_SHORT).show();
        }
        else{
            Log.v("SearchPage", "Network Found, Connected");
        }

        return isConnected;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(view.getContext(), "Google API Client Connection Error", Toast.LENGTH_SHORT).show();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void defineVar(View view){
        keywordText = (EditText) view.findViewById(R.id.search_keyword);
        keywordWarning = (TextView) view.findViewById(R.id.keyword_warning);
        categoryText = (Spinner) view.findViewById(R.id.category_spinner);
        distanceText = (EditText) view.findViewById(R.id.search_distance);
        fromOption = (RadioGroup) view.findViewById(R.id.fromOptions);
        fromSelected = fromOption.getCheckedRadioButtonId();

        fromOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioBtn_Spc){
                    locationText.setEnabled(true);
                }
                else{
                    locationText.setEnabled(false);
                }
            }
        });

        locationText = (AutoCompleteTextView) view.findViewById(R.id.search_location);
        setLocationTextListener();
        Log.v("location Text", "text: " + locationText.getText().toString());
        locationWarning = (TextView) view.findViewById(R.id.location_warning);
    }

    private AdapterView.OnItemClickListener autoCompleteListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            hideSoftKeyboard();
            Log.v("autoCompleteListener", "onItemClick");

            AutocompletePrediction item = AACAdapter.getItem(i);
            String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeId);
        }
    };

    public void setLocationTextListener (){
        locationText.setOnItemClickListener(autoCompleteListener);


        AACAdapter = new AddressAutoCompleteAdapter(getContext(), googleApiClient, lat_lon_bounds, null);
        locationText.setAdapter(AACAdapter);
        locationText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.searchBtn:
                if (validateForm(view) == true){
                    Log.v("Form Validate ", "true");
                    getBasics();
                };
                break;
            case R.id.clearBtn:
                Toast.makeText(v.getContext(), "Clicked on ClearBtn", Toast.LENGTH_SHORT).show();
//                reset warnings
                keywordWarning.setVisibility(v.GONE);
                locationWarning.setVisibility(v.GONE);
//                reset text fields
                keywordText.setText("");
                categoryText.setSelection(0); // Default Case
                distanceText.setText("");
                fromOption.check(R.id.radioBtn_Here);
                locationText.setText("");
                break;
        }
    }

    public boolean validateForm(View v){
        boolean validKeyword = false;
        boolean validLocation = false;
        Pattern p = Pattern.compile("^\\s*$");
        // Get All Variables
        getVar(v, p);

        // Validating Form
        if (p.matcher(keyword).matches()) { // Check keyword
            keywordWarning.setVisibility(v.VISIBLE);
            Toast.makeText(v.getContext(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
            Log.v("keyword: ", "empty");
            validKeyword = false;
        } else {
            validKeyword = true;
        }

        if (fromSelected == R.id.radioBtn_Spc && (p.matcher(locationText.getText().toString()).matches())) {
            locationWarning.setVisibility(v.VISIBLE);
            Toast.makeText(v.getContext(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
            Log.v("location: ", "empty");
            validLocation = false;
        } else {
            validLocation = true;
        }

        return validKeyword && validLocation;
    }

    public void getVar(View v, Pattern p){
        double hereLat = 0.0;
        double hereLon = 0.0;
        defineVar(v);
        // Get Here Location
        Log.v("loc", String.valueOf(loc));
        if(loc != null){
            hereLat = loc.getLatitude();
            hereLon = loc.getLongitude();
        }

        // Get keyword
        keyword = keywordText.getText().toString();
        category = categoryText.getSelectedItem().toString();
        distance = (p.matcher(distanceText.getText().toString()).matches()) ? 10 : Integer.parseInt(distanceText.getText().toString());
        location = (p.matcher(locationText.getText().toString()).matches()) ?
                "%7B%22lat%22:%22"+String.format("%.4f", hereLat)+"%22,%22lon%22:%22"+String.format("%.4f", hereLon)+"%22%7D" :
                locationText.getText().toString();

        Log.v("keyword", "keyword Entered: " + keyword);
        Log.v("category", "category Selected: " + category);
        Log.v("distance", "distance Entered: " + String.valueOf(distance));
        Log.v("location", "location Entered: " + location);
    }

    public Location getHereLoc(){
        Location loc = null;
        // Get Permission
        if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_COASE_LOCATION);
        }
        else {
            Log.v("location permission ", "granted");
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetWorkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // Get Here Location based on network and GPS
            if (isGPSEnabled && isNetWorkEnabled) {
                if (isGPSEnabled) {
                    Log.v("getting loc from ", "gps");
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_BW_UPDATES, this);
                    loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                else if (isNetWorkEnabled) {
                    Log.v("getting loc from ", "network");
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_BW_UPDATES, this);
                    loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            } else {
                Log.v("here location is ", "undefined");
                Toast.makeText(view.getContext(), "Not Able to Get Location", Toast.LENGTH_SHORT).show();
            }
        }

        Log.v("loc", String.valueOf(loc));

        return loc;
    }

    public void getBasics(){
        String qryStr = "";
        String keywordStr = keyword.replaceAll("\\s+", "_");
        String categoryStr = category.replaceAll("\\s+", "_");
        String distanceStr = String.valueOf(distance);
        String locationStr = location.replaceAll("\\s+", "_");
        qryStr = "/basics?" + "keyword=" + keywordStr + "&category=" + categoryStr +"&distance=" + distanceStr + "&location=" + locationStr;

        Log.v("qryStr", qryStr);

        if(checkNetworkConnection()){
            final ProgressDialog pd = new ProgressDialog(getContext());

            RequestQueue rq = Volley.newRequestQueue(searchContext);
            String rqStr = awsURL + qryStr;

            pd.setMessage("Fetching results");
            pd.show();
            JsonObjectRequest jq = new JsonObjectRequest(Request.Method.GET, rqStr, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
//                            intent from this activity to the next
                                Intent in = new Intent(getActivity(), basicsResult.class);
                                Log.v("basics data", "data: " + String.valueOf(response));
                                JSONObject geoLoc = response.getJSONObject("geoLoc");
                                JSONObject nearBy = response.getJSONObject("nearBy");

                                in.putExtra("geoLoc", geoLoc.toString());
                                in.putExtra("nearBy", nearBy.toString());
                                startActivity(in);
                                pd.dismiss();
                            } catch (JSONException err) { // JSON Parsing Error
                                err.printStackTrace();
                                Log.v("basics data", "err: " + String.valueOf(err));
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) { // Server Response Error
                            Log.v("request err", String.valueOf(error));
                            pd.dismiss();
                            Toast.makeText(view.getContext(), "No Network Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            rq.add(jq);
        }

    }


    @Override
    public void onLocationChanged(Location location) {
        this.loc = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Log.v("Location Listener", "Status Changed " + String.valueOf(status));
    }

    @Override
    public void onProviderEnabled(String provider) {
//        Log.v("Location Listener", "Provider on " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
//        Log.v("Location Listener", "Provider off " + provider);
    }
}

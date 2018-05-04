package com.example.freedom.placessearch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapPage extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private String directionURL = "https://maps.googleapis.com/maps/api/directions/json?";
    private String API_KEY = "AIzaSyDyx3qieF5kh-szhbjas2gKeRM-pE3OPEY";
    private View view;
    private MapView mapView;
    private GoogleMap googleMap;
    private int PERMISSION_FINE_LOCATION = 0;
    private int PERMISSION_COASE_LOCATION = 1;
    private GoogleApiClient googleApiClient;
    private AddressAutoCompleteAdapter AACAdapter;
    private AutoCompleteTextView originText;
    private Spinner sp;
    private Polyline pl;
    private String currentString;
    Float mapLat;
    Float mapLon;
    String entryName;
    // Covers the Entire Earth
    private LatLngBounds lat_lon_bounds = new LatLngBounds(new LatLng(-85, 180), new LatLng(85, -180));

    private OnFragmentInteractionListener mListener;

    public MapPage() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map_page, container, false);

        sp = (Spinner)view.findViewById(R.id.travelMode);
        originText = (AutoCompleteTextView) view.findViewById(R.id.originText);
        Log.v("originText", "onCreate: " + originText.getText().toString());

        if(getArguments() != null){
            entryName = getArguments().getString("name");
            mapLat = getArguments().getFloat("mapLat");
            mapLon = getArguments().getFloat("mapLon");
            Log.v("map page", "received mapLat = " + String.valueOf(mapLat) + " mapLon= " + String.valueOf(mapLon));

//            lat_lon_bounds = getLatLonBounds(mapLat, mapLon);
            initMap(entryName, mapLat, mapLon, savedInstanceState);
            initMapInput();
        }
        else{
            Log.v("map page", "did not receive map geolocation");
        }

        return view;
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


    public void initMap(final String entryName, final float mapLat, final float mapLon, Bundle savedInstanceState){
        Log.v("initMap", "initialize map");
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_COASE_LOCATION);
                } else {
//                    // For showing a move to my location button
//                    googleMap.setMyLocationEnabled(true);

                    // For dropping a marker at a point on the Map
                    LatLng entryLoc = new LatLng(mapLat,mapLon);
                    Log.v("map page", "title: " + entryName);
                    Log.v("map page", "LatLng: " + String.valueOf(entryLoc));
                    googleMap.addMarker(new MarkerOptions().position(entryLoc).title(entryName));

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(entryLoc).zoom(14).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    public void initMapInput(){
        Log.v("map page", "init map inputs");
        googleApiClient = new GoogleApiClient
                .Builder(getActivity().getApplicationContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        setSpinnerListener();

        originText.setOnItemClickListener(autoCompleteListener);
        AACAdapter = new AddressAutoCompleteAdapter(getActivity().getApplicationContext(), googleApiClient, lat_lon_bounds, null);
        originText.setAdapter(AACAdapter);

        originText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    selectGeoLoc();
                    return true;
                }

                return false;
            }
        });
    }

    public void setSpinnerListener(){
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("setSpinnerListener", "spinner item selected");
                selectGeoLoc();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void selectGeoLoc(){
        Log.v("selectGeoLoc", "selecting geolocation");
        currentString = originText.getText().toString();

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();

        try{
            list = geocoder.getFromLocationName(currentString, 1);
        }catch (IOException e){
            Log.v("selectGeoLoc", String.valueOf(e));
        }

        if(list.size() > 0){
            String travelMode = getTravelMode();
            Address originTmpAddress = list.get(0);
            Log.v("selectGeoLoc", "Location: " + originTmpAddress.toString());
            Log.v("originText", originText.getText().toString());
            Log.v("travelMode", travelMode);
            if(originTmpAddress.hasLatitude() && originTmpAddress.hasLatitude()){
                double originLat = originTmpAddress.getLatitude();
                double originLon = originTmpAddress.getLongitude();

                getDirection(originLat, originLon, travelMode);
                Log.v("using geoLoc", "geoLoc");
            }
            else{
                Log.v("using geoLoc","failed");
//                String address = originTmpAddress.getAddressLine(0).replaceAll("\\s+", "+");
//                Log.v("using address" , address);
//                getDirection(address, travelMode);
            }
        }
    }

    public String getTravelMode(){
        return sp.getSelectedItem().toString().toLowerCase();
    }

    public void getDirection(final double originLat, final double originLon, String mode){
        String originQryStr = "origin=" + String.valueOf(originLat) + "," + String.valueOf(originLon);
        String destinationQryStr = "&destination=" + String.valueOf(mapLat) + "," + String.valueOf(mapLon);
        String modeQryStr = "&mode=" + mode;
        String keyQryStr = "&key=" + API_KEY;
        String qryStr = directionURL + originQryStr + destinationQryStr + modeQryStr + keyQryStr;

        RequestQueue rq = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jq = new JsonObjectRequest(Request.Method.GET, qryStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.v("getDirection", "data: " + String.valueOf(response));
//                            Log.v("getDirection", "status: " + response.getString("status"));
                            JSONArray routes = response.getJSONArray("routes");
                            Log.v("getDirection", String.valueOf(routes.length()));
                            // TODO check length of routes
                            String polyline = routes.getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                            Log.v("polyline", polyline);
                            drawMap(decodePoly(polyline), originLat, originLon);
                        } catch (JSONException err) { // JSON Parsing Error
                            err.printStackTrace();
                            Log.v("getDirection", "err: " + String.valueOf(err));
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


    public void drawMap(List<LatLng> waypoints, double originLat, double originLon){
        Log.v("drawMap", waypoints.toString());
        // Clear polyline
        if(pl != null) {
            pl.remove();
        }

        pl = googleMap.addPolyline(new PolylineOptions()
                                        .addAll(waypoints)
                                        .color(Color.BLUE)
                                        .width(15));
        LatLngBounds newBound = LatLngBounds.builder()
                .include(new LatLng(originLat, originLon)).include(new LatLng(mapLat, mapLon))
                .build();

        googleMap.addMarker(new MarkerOptions().position(new LatLng(originLat, originLon)).title(currentString));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(newBound, 150));
    }

    /**
     * Method to decode polyline points
     * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("Google Map API", "connection failure");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {}

    private AdapterView.OnItemClickListener autoCompleteListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            hideSoftKeyboard();
            Log.v("autoCompleteListener", "onItemClick");

            AutocompletePrediction item = AACAdapter.getItem(i);
            String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeId);
            selectGeoLoc();
        }
    };
}

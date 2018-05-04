package com.example.freedom.placessearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReviewPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReviewPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewPage extends Fragment implements displayReviewsAdapter.OnItemClickListener, displayYelpReviewsAdapter.OnItemClickListener{
    private View view;
    private List<displayReviews> currentReview = new ArrayList<>();
    private List<displayReviews> googleReview = new ArrayList<>();
    private List<displayReviews> yelpReview = new ArrayList<>();
    private List<displayReviews> default_review_list_google = new ArrayList<>();
    private List<displayReviews> default_review_list_yelp = new ArrayList<>();
    private RecyclerView recyclerView;
    private displayReviewsAdapter mAdapter;
    private displayYelpReviewsAdapter yAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Spinner platformSP, orderSP;
    private int platformPosition, orderPosition;
    private boolean yelpNoRewview, googleNoReview;
    final private int GOOGLE_PLATFORM = 0;
    final private int YELP_PLATFORM = 1;
    final private boolean ASCENDING = true;
    final private boolean DECENDING = false;
    final private boolean MOST_RECENT = true;
    final private boolean LEAST_RECENT = false;

    private OnFragmentInteractionListener mListener;

    public ReviewPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewPage.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewPage newInstance(String param1, String param2) {
        ReviewPage fragment = new ReviewPage();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_review_page, container, false);
        yelpNoRewview = false;
        googleNoReview = false;
        googleReview = new ArrayList<>();
        yelpReview = new ArrayList<>();
        platformSP = (Spinner)view.findViewById(R.id.platformSP);
        orderSP = (Spinner)view.findViewById(R.id.orderSP);

        if(getArguments() != null){
            try{
                for(int i = 0 ; i < 5; i++){
                    Log.v("review page", "getting review " + String.valueOf(i));
                    // Google Review
                    if(getArguments().getString("gReview"+String.valueOf(i)) != null){
                        JSONObject gReviewTemp = new JSONObject(getArguments().getString("gReview"+String.valueOf(i)));
                        // add to recyclerview
                        displayReviews entry = new displayReviews(gReviewTemp, GOOGLE_PLATFORM);
                        default_review_list_google.add(entry);
                        googleReview.add(entry);
                    }
                    else{
                        if(i == 0){
                            googleNoReview = true;
                        }
                    }

                    // Yelp Review
                    if(getArguments().getString("yReview"+String.valueOf(i)) != null){
                        JSONObject yReviewTemp = new JSONObject(getArguments().getString("yReview"+String.valueOf(i)));
                        Log.v("reviewPage", "yelp: " + yReviewTemp.toString());
                        displayReviews entry = new displayReviews(yReviewTemp, YELP_PLATFORM);
                        default_review_list_yelp.add(entry);
                        yelpReview.add(entry);
                    }
                    else{
                        if(i == 0){
                            yelpNoRewview = true;
                        }
                    }
                }

                showGoogleReviews(default_review_list_google);
                setSpinnerListener();
            }
            catch (JSONException e) {
                // TODO no review
                Log.v("review page", "JSON parsing error");
                e.printStackTrace();
            }
        }
        else{
            Log.v("review page", "no review");
        }

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void noReview(){
        view.findViewById(R.id.gReview_scroll).setVisibility(View.GONE);
        view.findViewById(R.id.yReview_scroll).setVisibility(View.GONE);
        view.findViewById(R.id.no_review).setVisibility(View.VISIBLE);
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

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
        void onFragmentInteraction(Uri uri);
    }

    public void showReview(){
        int position = platformSP.getSelectedItem().equals("Google reviews") ? 0 : 1;
        showReviews(position);
    }

    public void showReviews(int position){
        List<displayReviews> showingGoogleReview = googleReview;
        List<displayReviews> showingYelpReview = yelpReview;
        if(orderPosition == 0){
            showingGoogleReview = default_review_list_google;
            showingYelpReview = default_review_list_yelp;
        }

        if(position == 0){
            showGoogleReviews(showingGoogleReview);
        }
        else{
            showYelpReviews(showingYelpReview);
        }
    }

    public void showGoogleReviews(List<displayReviews> reviewsList){
        if(googleNoReview){
            noReview();
        }
        else {
            // Set up the fist page
            Log.v("Show reviews", "show google reviews");
            view.findViewById(R.id.gReview_scroll).setVisibility(View.VISIBLE);
            view.findViewById(R.id.yReview_scroll).setVisibility(View.GONE);
            currentReview = googleReview;

            recyclerView = (RecyclerView) view.findViewById(R.id.gReview_list);
            mAdapter = new displayReviewsAdapter(reviewsList);
            mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            // Set on click listener
            mAdapter.setOnItemClickListener(this);
        }
    }

    public void showYelpReviews(List<displayReviews> reviewsList){
        if(yelpNoRewview){
            noReview();
        }
        else {
            Log.v("Show reviews", "show yelp reviews");
            view.findViewById(R.id.gReview_scroll).setVisibility(View.GONE);
            view.findViewById(R.id.yReview_scroll).setVisibility(View.VISIBLE);
            currentReview = yelpReview;

            recyclerView = (RecyclerView) view.findViewById(R.id.yReview_list);
            yAdapter = new displayYelpReviewsAdapter(reviewsList);
            mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(yAdapter);
            // Set on click listener
            yAdapter.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.v("Review", "Item " + String.valueOf(position) + " clicked.");
        displayReviews clickedItem = currentReview.get(position);
        Log.v("Review Page", clickedItem.getAuthorURL());
        Uri url = Uri.parse(clickedItem.getAuthorURL());
        startActivity(new Intent(Intent.ACTION_VIEW, url));
    }

    public void setSpinnerListener(){
        platformSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("platformSP", "spinner item selected: " + platformSP.getSelectedItem().toString());
                showReviews(position);
                platformPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        orderSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("orderSP", "spinner item selected: " + orderSP.getSelectedItem().toString());
                sortReviews(position);
                orderPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void sortReviews(int position){
        Log.v("sort review", String.valueOf(position));
        switch (position){
            case 0:
                googleReview = default_review_list_google;
                yelpReview = default_review_list_yelp;
                break;
            case 1:
                orderByRating(ASCENDING);
                break;
            case 2:
                orderByRating(DECENDING);
                break;
            case 3:
                orderByTime(MOST_RECENT);
                break;
            case 4:
                orderByTime(LEAST_RECENT);
                break;
            default:
                googleReview = default_review_list_google;
                yelpReview = default_review_list_yelp;
                break;
        }

        showReview();
    }

    public void orderByRating(final boolean isAscending){
        Collections.sort(googleReview, new Comparator<displayReviews>(){
            @Override
            public int compare(displayReviews o1, displayReviews o2) {
                int i = (Float.parseFloat(o1.getReviewRating()) - Float.parseFloat(o2.getReviewRating()) >= 0) ? -1 : 1;
                if(!isAscending){
                    i = -i;
                }
                return i;
            }
        });
        Collections.sort(yelpReview, new Comparator<displayReviews>(){
            @Override
            public int compare(displayReviews o1, displayReviews o2) {
                int i = (Float.parseFloat(o1.getReviewRating()) - Float.parseFloat(o2.getReviewRating()) >= 0) ? -1 : 1;
                if(!isAscending){
                    i = -i;
                }
                return i;
            }
        });
    }

    public void orderByTime(final boolean isMostRecentFirst){
        Collections.sort(googleReview, new Comparator<displayReviews>(){
            @Override
            public int compare(displayReviews o1, displayReviews o2) {
                long o1Time = Long.parseLong(o1.getReviewTime());
                Date o1Date = new java.util.Date(o1Time * 1000L);
                long o2Time = Long.parseLong(o2.getReviewTime());
                Date o2Date = new java.util.Date(o2Time * 1000L);

                int i = (o1Date.before(o2Date)) ? 1 : -1;
                if(!isMostRecentFirst){
                    i = -i;
                }
                return i;
            }
        });
        Collections.sort(yelpReview, new Comparator<displayReviews>(){
            @Override
            public int compare(displayReviews o1, displayReviews o2) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String o1Time = o1.getReviewTime();
                String o2Time = o2.getReviewTime();
                Date o1Date = null;
                Date o2Date = null;
                try {
                    o1Date = dateFormat.parse(o1Time);
                    o2Date = dateFormat.parse(o2Time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                int i = (o1Date.before(o2Date)) ? 1 : -1;
                if(!isMostRecentFirst){
                    i = -i;
                }
                return i;
            }
        });
    }
}

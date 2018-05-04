package com.example.freedom.placessearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoPage extends Fragment {
    private String[] entryAttr = {"name", "address", "phone", "price", "rating", "gPage", "websiteURL"};
    private String[] entryDetail = new String[entryAttr.length];
    View view;

    private OnFragmentInteractionListener mListener;

    public InfoPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoPage.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoPage newInstance(String param1, String param2) {
        InfoPage fragment = new InfoPage();
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
        view = inflater.inflate(R.layout.fragment_info_page, container, false);

        if(getArguments() != null){
            for(int i = 0; i < entryAttr.length; i++){
                entryDetail[i] = getArguments().getString(entryAttr[i]) == null ? "" : getArguments().getString(entryAttr[i]);
                Log.v("Info Page", entryAttr[i] + " " + entryDetail[i]);
                showString(i);
            }
        }
        else{
            Log.v("Info Page", "did not receive data");
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public void showString(int i){
        switch (i){
            // Address
            case 1:
                TextView detailAddress = (TextView) view.findViewById(R.id.detail_address);
                if (entryDetail[i].equals("")){
                    detailAddress.setText("No Address");
                }
                else{
                    detailAddress.setText(entryDetail[i]);
                }
                break;
            // Phone
            case 2:
                TextView detailPhone = (TextView) view.findViewById(R.id.detail_phone);
                if (entryDetail[i].equals("")){
                    detailPhone.setText("No Phone Number");
                }
                else{
                    detailPhone.setText(entryDetail[i]);
                    Linkify.addLinks(detailPhone, Linkify.PHONE_NUMBERS);
                }
                break;
            // Price
            case 3:
                TextView detailPrice = (TextView) view.findViewById(R.id.detail_price);
                if (entryDetail[i].equals("")){
                    detailPrice.setText("No Price Level");
                }
                else{
                    int pLevel = Integer.parseInt(entryDetail[i]);
                    String pLevelStr = "";
                    for(int j = 0; j < pLevel; j++){
                        pLevelStr += "$";
                    }

                    detailPrice.setText(pLevelStr);
                }
                break;
            // Rating
            case 4:
                RelativeLayout detailHasRating = (RelativeLayout) view.findViewById(R.id.detail_hasRating);
                TextView detailEmptyRating = (TextView) view.findViewById(R.id.detail_emptyRating);
                RatingBar detailRating = (RatingBar) view.findViewById(R.id.detail_rating);
                if (entryDetail[i].equals("")){
                    detailEmptyRating.setVisibility(View.VISIBLE);
                    detailHasRating.setVisibility(View.GONE);
                }
                else{
                    detailEmptyRating.setVisibility(View.GONE);
                    detailHasRating.setVisibility(View.VISIBLE);

                    detailRating.setRating(Float.parseFloat(entryDetail[i]));
                    Log.v("Rating", "numStars " + detailRating.getNumStars());
                }
                break;
            // Google Page
            case 5:
                TextView detailGPage = (TextView) view.findViewById(R.id.detail_gPage);
                if (entryDetail[i].equals("")){
                    detailGPage.setText("No Google Page");
                }
                else{
                    detailGPage.setText(entryDetail[i]);
                    Linkify.addLinks(detailGPage, Linkify.WEB_URLS);
                }
                break;
            // Website
            case 6:
                TextView detailWebsite = (TextView) view.findViewById(R.id.detail_website);
                if (entryDetail[i].equals("")){
                    detailWebsite.setText("No Website");
                }
                else{
                    detailWebsite.setText(entryDetail[i]);
                    Linkify.addLinks(detailWebsite, Linkify.WEB_URLS);
                }
                break;
                // Default
            default:
                Log.v("displaying info page", "default case");
                break;
        }
    }
}

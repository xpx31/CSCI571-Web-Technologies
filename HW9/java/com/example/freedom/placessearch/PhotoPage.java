package com.example.freedom.placessearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotoPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoPage extends Fragment {
    private OnFragmentInteractionListener mListener;
    private View view;

    public PhotoPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoPage.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoPage newInstance(String param1, String param2) {
        PhotoPage fragment = new PhotoPage();
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
        view = inflater.inflate(R.layout.fragment_photo_page, container, false);
        int photoIndex = 0;
        int marginX = 40;
        int marginY = marginX / 2;

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.detail_photos);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(marginX, marginY, marginX, marginY);

        if(getArguments() != null){
            Log.v("Photo Page", "recieved photo.");
            String imagePath = getArguments().getString("photo");
            int photoCount = getArguments().getInt("photoCount");
            if(photoCount == 0){
                view.findViewById(R.id.photo_scroll).setVisibility(View.GONE);
                view.findViewById(R.id.no_photo).setVisibility(View.VISIBLE);
            }
            else {
                try {
                    view.findViewById(R.id.photo_scroll).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.no_photo).setVisibility(View.GONE);
                    for(photoIndex = 0; photoIndex < photoCount; photoIndex++) {
                        // Get Image
                        File f = new File(imagePath, String.valueOf(photoIndex) + ".png");
                        Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f));
                        // Set Image
                        ImageView photoTemp = new ImageView(getContext());
                        photoTemp.setImageBitmap(bmp);
                        photoTemp.setAdjustViewBounds(true);
                        ll.addView(photoTemp, lp);
                        photoIndex++;
                    }
                } catch (FileNotFoundException e) {
                    if (photoIndex == 0) {
                        view.findViewById(R.id.photo_scroll).setVisibility(View.GONE);
                        view.findViewById(R.id.no_photo).setVisibility(View.VISIBLE);
                    }
                    Log.v("photo page", String.valueOf(e));
                }
            }
        }
        else{
            Log.v("Photo Page", "did not receive data");
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
//
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

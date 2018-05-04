package com.example.freedom.placessearch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

public class FavPage extends Fragment implements displayFavBasicsAdapter.OnItemClickListener {
    View view;
    private RecyclerView recyclerView;
    private displayFavBasicsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedPreference sharedPreference;
    private List<displayBasics> favList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreference = new SharedPreference();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fav_page_laylout, container, false);
        Log.v("fav page", "in fav page");
        updateView();
        return view;
    }


    public void updateView(){
        favList = sharedPreference.getFavorites(getContext());
        Log.v("fav page", String.valueOf(favList.size()));
        if(favList == null || favList.size() == 0){
            view.findViewById(R.id.no_record).setVisibility(View.VISIBLE);
            view.findViewById(R.id.favList).setVisibility(View.GONE);
        }
        else {
            view.findViewById(R.id.no_record).setVisibility(View.GONE);
            view.findViewById(R.id.favList).setVisibility(View.VISIBLE);

            recyclerView = (RecyclerView) view.findViewById(R.id.favList);
            mAdapter = new displayFavBasicsAdapter(favList, getContext());
            mAdapter.setID(this);
            mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
//            // Set on click listener
            mAdapter.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.v("Fav Page", "Item " + String.valueOf(position) + " clicked.");
        getDetails getDetails = new getDetails(favList, position, view.getContext());
        updateView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.wtf("onResume", "here");
        updateView();
    }
}


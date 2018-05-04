package com.example.freedom.placessearch;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class displayBasicsAdapter extends RecyclerView.Adapter<displayBasicsAdapter.mViewHolder> {
    private int[] hearts = {R.drawable.heart_outline_black, R.drawable.heart_fill_red};
    private OnItemClickListener mListener;
    private List<displayBasics> displayBasicsList;
    private SharedPreference sharedPreference;
    private SharedPreferences sharedPreferences;
    private displayBasics entry;
    final private String FAV = "fav";
    final private String UNFAV = "unfav";
    private Context context;
    private FavPage fp;


    public displayBasicsAdapter(List<displayBasics> list, Context context) {
        this.displayBasicsList = list;
        this.context = context;
        sharedPreference = new SharedPreference();
    }

    public void setOnItemClickListener(FavPage favPage) {
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView entryIcon, entryFav;
        public TextView entryName, entryAddress;

        public mViewHolder(View view){
            super(view);
            entryIcon = (ImageView) view.findViewById(R.id.entry_icon);
            entryName = (TextView) view.findViewById(R.id.entry_name);
            entryAddress = (TextView) view.findViewById(R.id.entry_address);
            entryFav= (ImageView) view.findViewById(R.id.entry_fav);
            entryFav.setOnClickListener(this);

            // Item click listener
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Log.v("viewID", String.valueOf(v));
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.entry_fav:
                    Log.v("basicsAdapater", "fav clicked");
                    int position = getAdapterPosition();
                    Log.v("basicsAdapter", "position " + position);
                    ImageView heart = v.findViewById(R.id.entry_fav);
                    displayBasics selectedEntry = displayBasicsList.get(position);
                    String name = selectedEntry.getName();
                    boolean fav = selectedEntry.getFav();
                    if(fav == false){
                        selectedEntry.setFav();
                        heart.setImageResource(hearts[1]);
                        sharedPreference.addFavorite(v.getContext(), selectedEntry);
                        Toast.makeText(heart.getContext(), name + " was added to favorites" , Toast.LENGTH_SHORT).show();
//                        notifyDataSetChanged();

                    }
                    else{
                        selectedEntry.setUnFav();
                        heart.setImageResource(hearts[0]);
                        sharedPreference.removeFavorite(v.getContext(), selectedEntry);
                        Toast.makeText(heart.getContext(), name + " was removed from favorites" , Toast.LENGTH_SHORT).show();
//                        fp.onResume();
//                        notifyDataSetChanged();
                    }
                    break;
                default:
                    Log.v("basicsAdapater", "other clicked");
                    break;
            }
        }
    }


    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_basics_entry, parent, false);

        return new mViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        entry = displayBasicsList.get(position);
        Picasso.get().load(entry.getIcon()).resize(100, 100).into(holder.entryIcon);
        holder.entryName.setText(entry.getName());
        holder.entryAddress.setText(entry.getAddress());
        //TODO change up on fav
        int heartIndex = entry.getFav() == true ? 1 : 0;
        holder.entryFav.setImageResource(hearts[heartIndex]);
    }

    @Override
    public int getItemCount() {
        return displayBasicsList.size();
    }

    public void setID(FavPage fp){
        this.fp = fp;
    }
}

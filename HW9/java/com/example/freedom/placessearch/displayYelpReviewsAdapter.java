package com.example.freedom.placessearch;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class displayYelpReviewsAdapter extends RecyclerView.Adapter<displayYelpReviewsAdapter.mViewHolder>{
    private List<displayReviews> displayReviewsList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public class mViewHolder extends RecyclerView.ViewHolder{
        public ImageView profilePic;
        public TextView authorName, reviewTime, reviewText;
        public RatingBar reviewRating;

        public mViewHolder(View view){
            super(view);
            // did not change id tags to yReview, stays for now
            profilePic = (ImageView) view.findViewById(R.id.gReview_authorPic);
            authorName = (TextView) view.findViewById(R.id.gReview_author);
            reviewTime = (TextView) view.findViewById(R.id.gReview_time);
            reviewRating = (RatingBar) view.findViewById(R.id.gReview_rating);
            reviewText = (TextView) view.findViewById(R.id.gReview_text);

            // Item click listener
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public displayYelpReviewsAdapter(List<displayReviews> displayReviewsList) {
        this.displayReviewsList = displayReviewsList;

    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.display_reviews_table, parent, false);
        return new mViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, int position) {
        displayReviews entry = displayReviewsList.get(position);
        Picasso.get().load(entry.getProfileURL()).resize(100, 100).into(holder.profilePic);
        holder.authorName.setText(entry.getAuthorName());
        holder.reviewTime.setText(entry.getReviewTime());
        holder.reviewRating.setRating(Float.parseFloat(entry.getReviewRating()));
        holder.reviewText.setText(entry.getReviewText());
    }

//    public String convertTime(String time){
//        long unixTime = Long.parseLong(time);
//        Date date = new java.util.Date(unixTime * 1000L);
//        SimpleDateFormat dateSDF = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        return dateSDF.format(date);
//    }

    @Override
    public int getItemCount() {
        return displayReviewsList.size();
    }
}

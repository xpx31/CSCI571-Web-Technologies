package com.example.freedom.placessearch;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class displayBasicsListAdapter extends ArrayAdapter<displayBasics> {
    private Context context;
    List<displayBasics> displayBasicsList;
    SharedPreference sharedPreference;

    public displayBasicsListAdapter(Context context, List<displayBasics> entry) {
        super(context, R.layout.display_basics_entry, entry);
        this.context = context;
        this.displayBasicsList = entry;
        sharedPreference = new SharedPreference();
    }

    private class ViewHolder {
        ImageView entryIcon, entryFav;
        TextView entryName, entryAddress;
    }

    @Override
    public int getCount() {
        return displayBasicsList.size();
    }

    @Override
    public displayBasics getItem(int position) {
        return displayBasicsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.display_basics_entry, null);
            holder = new ViewHolder();
            holder.entryName = (TextView) convertView
                    .findViewById(R.id.entry_name);
            holder.entryAddress = (TextView) convertView
                    .findViewById(R.id.entry_address);
            holder.entryIcon = (ImageView) convertView
                    .findViewById(R.id.entry_icon);
            holder.entryFav = (ImageView) convertView
                    .findViewById(R.id.entry_fav);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        displayBasics displayBasics = (displayBasics) getItem(position);
        holder.entryName.setText(displayBasics.getName());
        holder.entryAddress.setText(displayBasics.getAddress());
        Picasso.get().load(displayBasics.getIcon()).resize(100, 100).into(holder.entryIcon);

        /*If a displayBasics exists in shared preferences then set heart_red drawable
         * and set a tag*/
        if (checkFavoriteItem(displayBasics)) {
            holder.entryFav.setImageResource(R.drawable.heart_fill_red);
            holder.entryFav.setTag("fav");
        } else {
            holder.entryFav.setImageResource(R.drawable.heart_fill_white_with_black_outline);
            holder.entryFav.setTag("unFav");
        }

        return convertView;
    }

    /*Checks whether a particular displayBasics exists in SharedPreferences*/
    public boolean checkFavoriteItem(displayBasics checkdisplayBasics) {
        boolean check = false;
        List<displayBasics> favorites = sharedPreference.getFavorites(context);
        if (favorites != null) {
            for (displayBasics displayBasics : favorites) {
                if (displayBasics.equals(checkdisplayBasics)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    @Override
    public void add(displayBasics displayBasics) {
        super.add(displayBasics);
        displayBasicsList.add(displayBasics);
        notifyDataSetChanged();
    }

    @Override
    public void remove(displayBasics displayBasics) {
        super.remove(displayBasics);
        displayBasicsList.remove(displayBasics);
        notifyDataSetChanged();
    }
}

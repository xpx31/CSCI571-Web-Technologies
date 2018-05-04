package com.example.freedom.placessearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.text.IDNA;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class detailResult extends AppCompatActivity{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter detailSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    final private String twitterURL = "https://twitter.com/intent/tweet/?";
    private String tabTitles[] = {"INFO", "PHOTOS", "MAP", "REVIEWS"};
    private int tabIcons[] = {R.drawable.info_outline, R.drawable.photos,
            R.drawable.maps, R.drawable.review};
    private Fragment infoPage = new InfoPage();
    private Fragment photoPage = new PhotoPage();
    private Fragment mapPage = new MapPage();
    private Fragment reviewPage = new ReviewPage();
    private Toolbar toolbar;
    private String[] entryAttr = {"name", "address", "phone", "price", "rating", "gPage", "websiteURL"};
    private String[] entryDetail = new String[entryAttr.length];
    private String entryName;
    private String entryAddr;
    private float entryMapLat;
    private float entryMapLon;
    private JSONObject entryList;
    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_result);
        toolbar = (Toolbar) findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreference = new SharedPreference();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        detailSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), detailResult.this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.detailContainer);
        mViewPager.setAdapter(detailSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.detailTabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        getDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.twitter_share) {
            shareTwitter();
            return true;
        }

        if (id == R.id.fav){
            boolean isInFav = sharedPreference.isInFav(entryList, this);
            // TODO add / remove from share
            if(isInFav == true){
                item.setIcon(ContextCompat.getDrawable(this, R.drawable.heart_fill_red));
                Toast.makeText(getApplicationContext(), entryName + "  was added to favorites", Toast.LENGTH_SHORT).show();
            }
            else{
                item.setIcon(ContextCompat.getDrawable(this, R.drawable.heart_fill_white));
                Toast.makeText(getApplicationContext(), entryName + "  was removed from favorites", Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void shareTwitter(){
//        {"name", "address", "phone", "price", "rating", "gPage", "websiteURL"};
        String qrtStr = "text=Check out " + entryDetail[0];
        qrtStr += " located at " + entryDetail[1] + ". ";

        String url = null;
        try {
            url = entryDetail[6];
            url.replaceAll("///g", "%2F");
            url.replaceAll("/:/g", "%3A");
        }
        catch (NullPointerException e) {
            try {
                url = entryDetail[5];
                url.replaceAll("///g", "%2F");
                url.replaceAll("/:/g", "%3A");
            } catch (NullPointerException err) {
                Log.v("shareTwitter", "no website or google page");
            }
        }

        qrtStr += "Website: " + url;
        qrtStr += " &hashtags=TravelAndEntertainmentSearch%2C";
        qrtStr.replaceAll("/\\s/g", "%20");

        Uri twitter_url = Uri.parse(twitterURL + qrtStr);
        startActivity(new Intent(Intent.ACTION_VIEW, twitter_url));
    }

//    public void

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Context context;

        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.v("Detail Results", "Get Item " + String.valueOf(position));
            Fragment frag = null;

            if(position == 0){
                frag = infoPage;
            }
            else if(position == 1){
                frag = photoPage;
            }
            else if(position == 2){
                frag = mapPage;
            }
            else{
                frag = reviewPage;
            }

            return frag;
        }

        @Override
        public int getCount() { return 4; }

        // Align Icon with Text
        @Override
        public CharSequence getPageTitle(int position){
            Log.v("Detail Result", "Getting Page Title " + String.valueOf(position));
            Drawable icon = context.getResources().getDrawable(tabIcons[position]);
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            int icon_mid_height = icon.getIntrinsicHeight() / 2;
            Log.v("icon_mid_height", String.valueOf(icon_mid_height));
            SpannableString ss = new SpannableString("  " + tabTitles[position]);
            ImageSpan is = new ImageSpan(icon,icon_mid_height);
            ss.setSpan(is, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ss;
        }
    }


    //    get all the detail information of the selected entry
    public void getDetails(){
        Intent detailsIntent = getIntent();
        Log.v("detailResult", "received results");
//        reDirect info detail
        reDirectInfo(detailsIntent);
//        reDirect photo detail
        reDirectPhoto(detailsIntent);
        // initialize map detail
        initMap(detailsIntent);
        // reDirect review detail
        reDirectReview(detailsIntent);
    }

    // send to InfoPage
    public void reDirectInfo(Intent detailsIntent) {
        Log.v("detailResult", "redirecting info");
        Bundle infoBundle = new Bundle();
        entryList = new JSONObject();

        for(int i = 0 ; i < entryAttr.length; i++){
            entryDetail[i] = detailsIntent.getStringExtra(entryAttr[i]);
            try {
                entryList.put(entryAttr[i], entryDetail[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            infoBundle.putString(entryAttr[i], entryDetail[i]);
        }

        // detail place name
        toolbar.setTitle(entryDetail[0]);
        entryName = entryDetail[0];
        entryAddr = entryDetail[1];


        // send to info fragment
        infoPage.setArguments(infoBundle);
    }

    // send to PhotoPage
    public void reDirectPhoto(Intent detailsIntent){
        Log.v("detailResult", "redirecting photo");
        Bundle photoBundle = new Bundle();
        photoBundle.putString("photo", detailsIntent.getStringExtra("photo"));
        photoBundle.putInt("photoCount", detailsIntent.getIntExtra("photoCount", 0));
        photoPage.setArguments(photoBundle);
    }

    public void initMap(Intent detailsIntent){
        Log.v("detailResult", "initializing map");
        Bundle mapBundle = new Bundle();
        entryMapLat = Float.parseFloat(detailsIntent.getStringExtra("mapLat"));
        entryMapLon = Float.parseFloat(detailsIntent.getStringExtra("mapLon"));
        mapBundle.putString("name", detailsIntent.getStringExtra(entryAttr[0]));
        mapBundle.putFloat("mapLat", entryMapLat);
        mapBundle.putFloat("mapLon", entryMapLon);
        mapPage.setArguments(mapBundle);
    }

    public void reDirectReview(Intent detailsIntent){
        Log.v("detailResult", "redirecting reviews");
        Bundle reviewBundle = new Bundle();
        for(int i = 0; i < 5; i++){
            Log.v("detailResult", "i = " + String.valueOf(i));
            // Google Review
            try{
                reviewBundle.putString("gReview"+String.valueOf(i), detailsIntent.getStringExtra("gReview"+String.valueOf(i)));
                Log.v("reDirectReview", detailsIntent.getStringExtra("gReview"+String.valueOf(i)));
            }
            catch (NullPointerException e){
                Log.v("gReview", "has " + String.valueOf(i + 1) + "objects");
            }
            // Yelp Review
            try{
                reviewBundle.putString("yReview"+String.valueOf(i), detailsIntent.getStringExtra("yReview"+String.valueOf(i)));
                Log.v("reDirectReview", detailsIntent.getStringExtra("yReview"+String.valueOf(i)));
            }
            catch (NullPointerException e){
                Log.v("yReview", "has " + String.valueOf(i + 1) + "objects");
            }
        }
        reviewPage.setArguments(reviewBundle);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}


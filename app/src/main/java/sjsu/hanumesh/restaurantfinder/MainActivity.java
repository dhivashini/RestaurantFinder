package sjsu.hanumesh.restaurantfinder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Call;
import retrofit.Response;


public class MainActivity extends ListActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog pDialog;
    List list = new ArrayList();
    ArrayList<Business> businesses;
    String address;
    String placeName;
    Double latitude;
    Double longitude;
    String searchBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button searchButton = (Button) findViewById(R.id.search_button);

        //Button favButton = (Button) findViewById(R.id.fav_button);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                new searchYelp().execute();
            }
        });

        Button placePicker = (Button) findViewById(R.id.location_picker);
        placePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(MainActivity.this);
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.restaurantfinder2);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == 1
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this,data);
            placeName = place.getName().toString();
            address = place.getAddress().toString();
            if (placeName.charAt(0)=='('){
            String[] parts=placeName.split(",");

                latitude=Double.parseDouble(parts[0].replace("(",""));
                longitude=Double.parseDouble(parts[1].replace(")",""));
                Log.d("lat",latitude.toString());
                Log.d("long",longitude.toString());
                searchBy="latandlong";
            }


            //mName.setText(name);


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.search) {
            // Handle the camera action
            Intent in = new Intent(getApplicationContext(),
                    MainActivity.class);
            startActivity(in);
        } else if (id == R.id.favorites) {
            Intent in = new Intent(getApplicationContext(),
                    FavoritesActivity.class);
            startActivity(in);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void yelpSearch() throws IOException {
        YelpAPIFactory apiFactory = new YelpAPIFactory("ub76ciHSTk15NOehQ8M5fg", "MmCUGJ6UwiwRUX3PnaT4uxYMRQg", "Im8YjjHAWbERxPs_G5XotIrxcmtp2eXG", "s1GPPs5JYWSdX3k2GEbQEkUke4c");
        YelpAPI yelpAPI = apiFactory.createAPI();
        Map<String, String> params = new HashMap<>();

        EditText searchFor = (EditText) findViewById(R.id.search_for);
        Log.d("searchfor", searchFor.getText().toString());

        RadioGroup sort = (RadioGroup)findViewById(R.id.sort);
        int selectedId = sort.getCheckedRadioButtonId();
        RadioButton relevance = (RadioButton) findViewById(R.id.relevance);
        RadioButton distance = (RadioButton) findViewById(R.id.distance);
        if (selectedId==relevance.getId())
            params.put("sort","0");
        else
            params.put("sort","1");
// general params
        params.put("term", searchFor.getText().toString());
        params.put("limit", "20");
        params.put("radius_filter", "16093");
        //Log.d("address",address.toString());
        Call<SearchResponse> call;
        if (placeName == null && address==null)
        {
            call = yelpAPI.search("san jose", params);
        }
        else {
            Log.d("entry", "elseentry");
            if (searchBy!="latandlong"){
                Log.d("else if","place name not null");
                call = yelpAPI.search(address.toString(), params);
            }

            else {
                Log.d("else if","place name  null");
                Geocoder geocoder;
                List<Address> addresses = null;
                String addressFromLoc;
                geocoder = new Geocoder(MainActivity.this,Locale.getDefault());
                addresses = geocoder.getFromLocation(
                        latitude, longitude,
                        // In this sample, get just a single address.
                        1);
                Log.d("geocoder",addresses.toArray().toString());
                try {
                    addresses = geocoder.getFromLocation(
                            latitude, longitude,
                            // In this sample, get just a single address.
                            1);
                } catch (IOException ioException) {
                    // Catch network or other I/O problems.

                    Log.e("ioException",ioException.toString());
                } catch (IllegalArgumentException illegalArgumentException) {
                    // Catch invalid latitude or longitude values.

                }

                // Handle case where no address was found.


                    Address address = addresses.get(0);
                    ArrayList<String> addressFragments = new ArrayList<String>();
                    addressFromLoc="";
                    // Fetch the address lines using getAddressLine,
                    // join them, and send them to the thread.
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        addressFragments.add(address.getAddressLine(i));
                        addressFromLoc+=address.getAddressLine(i);
                    }

                Log.d("addressFromLoc",addressFromLoc);
                call = yelpAPI.search(addressFromLoc, params);
            }
        }
        //String result = yelpAPI.search("San Jose", params);
//        Toast.makeText(this,"Yelp",Toast.LENGTH_LONG).show();

/*        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();*/

        try {
            Response<SearchResponse> response = call.execute();
            SearchResponse searchResponse = response.body();
            Log.d("test", searchResponse.toString());


            ListView lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // Starting single contact activity
                    Intent in = new Intent(getApplicationContext(),
                            SingleBusinessActivity.class);
                    in.putExtra("business", businesses.get(position));
                    startActivity(in);

                }
            });


            // if (searchResponse != null) {
            //   try {

            businesses = searchResponse.businesses();
            for (Business business : businesses) {
                HashMap<String, String> hmap = new HashMap<String, String>();
                //System.out.println(business.name());
                //System.out.println(business.rating());
                //System.out.println(business.imageUrl());
                //System.out.println(business.location());
                hmap.put("name", business.name());
                hmap.put("rating", business.rating().toString());
                hmap.put("imageUrl", business.imageUrl());
                hmap.put("location", business.location().displayAddress().toString().replace("[","").replace("]",""));
                list.add(hmap);

                // System.out.println(business);
            }


            //   }
            // }
            //  System.out.println(list);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Dismiss the progress dialog

        if (pDialog.isShowing())
            pDialog.dismiss();

/**
 * Updating parsed JSON data into ListView
 * *//*

        ListAdapter adapter = new SimpleAdapter(
                MainActivity.this, list,
                R.layout.rowlayout, new String[] { "name", "rating",
                "location" }, new int[] { R.id.name,
                R.id.rating, R.id.location });

        setListAdapter(adapter);
*/

    }

    private class searchYelp extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                yelpSearch();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ListAdapter adapter = new MySimpleArrayAdapter(MainActivity.this, list);

            setListAdapter(adapter);
        }
    }


}

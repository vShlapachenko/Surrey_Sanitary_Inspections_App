package com.example.cmpt276_project_iron.ui;

import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Restaurant;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 *  Attains and sets the necessary information for the restaurant's details
 */
public class RestaurantList extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener {
    private Manager manager;
    private FrameLayout mapContainer;

    final MapFragment mapFragment = MapFragment.newInstance();;
    final FragmentManager fragManager = getSupportFragmentManager();
    Fragment active = mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayCorrectLayout();
        setUpBackButton();
        setUpNavigationBar();
        manager = Manager.getInstance(this);

        //Used for launching the map fragment
        inflateRestaurantList();

        //Will get required permissions for services, wait and then launch activity, but also
        //check if the necessary services are already provided, then launch instantly
        //Fixes bug with invalid service permissions resulting in map related exceptions
        safeLaunchMap();

        //Only after the necessary processing of the first activity has been completed should the
        //map be displayed (as default first screen)
        //setUpMapOpen(getWindow().getDecorView().getRootView());
    }

    //Made public so it can be launched from xml (non-dynamic)
    public void setUpMapOpen(View view) {
        mapContainer = findViewById(R.id.mapContainer);

        //Checks if the user has Google Play Services that is required for maps, if not, a message
        //will appear when the user tries to click on the map button
        final int NO_GOOGLE_PLAY = 9001; //Value returned by method we're using if no Google play
        SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(RestaurantList.this)
                == NO_GOOGLE_PLAY) {
            Log.i("invalid_google_services", "User does not have the required " +
                    "Google Play Services to launch map");
            Toast.makeText(RestaurantList.this, "Invalid Google Play SDK",
                    Toast.LENGTH_SHORT).show();

            //Since this check should not be repeated (as is the case with the second activity), we
            //can check once and then store into preferences

            //Note: Would want to start the second activity for result but in the current structure
            //it would not provide any advantages as the second activity is launched from the list adapter
            //and this same code would have to be relayed there as well (thus, again duplicate)
            //Also, we don't want to block access to the second activity as it still serves different
            //purposes so it is stored into data and then the boolean is checked before launching maps
            editor.putBoolean("goog_services", false);

        } else {
            FragmentTransaction transactor = fragManager.beginTransaction();
            /**
             * Note: When a toolbar is set up for the map, it must be tasked with making the
             *       button reappear
             */
            //First two parameters are for entry, the last two are for exit (animations)
            transactor.setCustomAnimations(R.anim.swipe_left, R.anim.swipe_right,
                    R.anim.swipe_left, R.anim.swipe_right);
            //Only want the fragment to close (not the activity), therefore
            //explicitly add it to the stack
            transactor.addToBackStack("fragInstance");
            transactor.add(R.id.mapContainer, mapFragment, "mapFrag").commit();
            editor.putBoolean("goog_services", true);
        }
        editor.apply();
    }

    private void inflateRestaurantList(){
        List<Restaurant> restaurants = manager.getRestaurantList();

        if(restaurants == null){
            TextView emptyListText = findViewById(R.id.noRestaurantsText);
            emptyListText.setText(getResources().getString(R.string.no_restaurants_text));
        } else {
            RecyclerView restaurantList = findViewById(R.id.restaurantList);
            RestaurantListAdapter adapter = new RestaurantListAdapter(this, restaurants);
            adapter.notifyDataSetChanged();
            restaurantList.setAdapter(adapter);
            restaurantList.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void setUpBackButton(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void setUpNavigationBar() {
        BottomNavigationView navMenu = findViewById(R.id.navigationView);
        navMenu.setSelectedItemId(R.id.navigation_map);
        navMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.navigation_resList):
                        fragManager.beginTransaction().hide(active).hide(mapFragment).commit();
                        return true;

                    case (R.id.navigation_map):
                        fragManager.beginTransaction().hide(active).show(mapFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }

    public static Intent getIntent(Context context, int restaurantIndex){
        Intent intent = new Intent(context, RestaurantDetails.class);
        intent.putExtra("restaurantIndex", restaurantIndex);
        return intent;
    }

    private void displayCorrectLayout(){
        Display dimensions = getWindowManager().getDefaultDisplay();
        Point dimension = new Point();
        dimensions.getSize(dimension);
        int width = dimension.x;
        int height = dimension.y;
        float density = getResources().getDisplayMetrics().density;

        //Checking if it's not a MDPI type screen, used to distinguish between same resolution phones that are of different sizes
        double MDPI_SCREEN_SIZE = 1.0;
        if(width == 480 && height == 800 && density != MDPI_SCREEN_SIZE) {
            setContentView(R.layout.activity_restaurant_list_custom);
        } else if(width == 1440 && height == 2560) {
            setContentView(R.layout.activity_restaurant_list_custom_one);
        } else{
            setContentView(R.layout.activity_restaurant_list);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //In accordance with the user stories, any one of the selections of the map or restaurant
        //will result in an exit of the application
        android.app.FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            finish();
        } else {
            //If no fragments -> on restaurant screen -> exit application -> normal behaviour
            super.onBackPressed();
        }
    }

    private void safeLaunchMap(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                setUpMapOpen(getWindow().getDecorView().getRootView());
            } else{
                Log.i("lengthened_launch", "Map is launching as default screen for the first time");
                getRequiredPermissions();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        setUpMapOpen(getWindow().getDecorView().getRootView());
                    }
                }, 3000);
            }
        } else{
            Log.i("lengthened_launch", "Map is launching as default screen for the first time");
            getRequiredPermissions();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    setUpMapOpen(getWindow().getDecorView().getRootView());
                }
            }, 3000);
        }
    }

    private void getRequiredPermissions() {
        String[] req_permissons = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

        //Once the permissions have been displayed, check what the user's selection was
        //More specifically, make sure that it is correct
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //Do nothing
            } else {
                //If the permissions were not granted already (via the settings), ask for them
                ActivityCompat.requestPermissions(this, req_permissons, 0);
            }
        } else {
            ActivityCompat.requestPermissions(this, req_permissons, 0);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Do nothing
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        //Do nothing
    }

}

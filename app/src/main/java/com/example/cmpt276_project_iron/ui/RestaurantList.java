package com.example.cmpt276_project_iron.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpt276_project_iron.FilterOptions;
import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.database.DatabaseHelper;
import com.example.cmpt276_project_iron.model.FilterSettings;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Restaurant;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  Attains and sets the necessary information for the restaurant's details
 */
public class RestaurantList extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener {
    private Manager manager;
    private FrameLayout mapContainer;

    private MapFragment fragment;
    private Fragment active;
    private FragmentManager fragManager;
    private List<Restaurant> restaurants;
    private RestaurantListAdapter adapter;
    private final int REQUEST = 0;
    private FilterSettings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayCorrectLayout();
        setUpBackButton();
        setUpNavigationBar();
        //Used for launching the map fragment
        settings = FilterSettings.getInstance(this);
        inflateRestaurantList();
        //Will get required permissions for services, wait and then launch activity, but also
        //check if the necessary services are already provided, then launch instantly
        //Fixes bug with invalid service permissions resulting in map related exceptions
        safeLaunchMap();
        setActionBar();
        showFavouritesFragment();
    }

    private void showFavouritesFragment() {
        FragmentManager fragManager = getSupportFragmentManager();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<String> trackingNumbers = new ArrayList<>();
        List<Integer> numberOfInspections = new ArrayList<>();
        for (Restaurant r : restaurants) {
            if (r.isFavourite()) {
                trackingNumbers.add(r.getTrackingNumber());
                List<Inspection> inspections = manager.getInspectionMap().get(r.getTrackingNumber());
                if (inspections == null || inspections.isEmpty()) {
                    numberOfInspections.add(0);
                } else {
                    numberOfInspections.add(inspections.size());
                }
            }
        }
        List<String> trackingNumbersUpdatedRestaurants = databaseHelper.getUpdatedRestaurants(trackingNumbers, numberOfInspections);
        List<Restaurant> updatedRestaurants = new ArrayList<>();
        numberOfInspections.clear();
        for (Restaurant r : restaurants) {
            if (trackingNumbersUpdatedRestaurants.contains(r.getTrackingNumber())) {
                updatedRestaurants.add(r);
                List<Inspection> inspections = manager.getInspectionMap().get(r.getTrackingNumber());
                numberOfInspections.add(inspections.size());
            }
        }
        if (!(updatedRestaurants.isEmpty())) {
            UpdatedFavouritesFragment dialog = new UpdatedFavouritesFragment(this, updatedRestaurants);
            dialog.show(fragManager, "MessageDialog");
        }
        databaseHelper.updateAllRestaurants(trackingNumbersUpdatedRestaurants, numberOfInspections);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar detailsBar = getSupportActionBar();
        detailsBar.setSubtitle("Filter: ");

        MenuInflater inflater = getMenuInflater();
        //Setting search bar in place of provided menu
        inflater.inflate(R.menu.filter_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.filter_search);
        EditText searchView = (EditText) searchItem.getActionView();


        searchView.setFocusable(true);
        searchView.setHint(getString(R.string.restaurant_hint));

        //Change the go button in the keyboard to something more appropriate for live search
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setSingleLine();
        //Listener for the text change in the search bar - Using EditText such that the keyboard
        //can be closed upon empty input
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Empty on purpose
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("restaurant_filtered", "restaurant list filtered via search, filter: " + s);
                //Get filtered results using the logic defined in the RestaurantListAdapter.java
                adapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {
               //Empty
            }
        });


        return true;
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
            //Check if this was a coordinates launch request or a standard request
            boolean coord_launch = data.getBoolean("coord_launch", false);

            if(coord_launch){
                fragment = MapFragment.newInstance(getIntent().getDoubleExtra("latitude", 0.0),
                        getIntent().getDoubleExtra("longitude", 0.0),
                        getIntent().getBooleanExtra("coordinateFlag", true),
                        getIntent().getIntExtra("restaurantIndex", 0));

                //Consume the indicator once launched for coordinates
                editor.putBoolean("coord_launch", false);
            }else {
                fragment = MapFragment.newInstance();
            }

            FragmentTransaction transactor = getSupportFragmentManager().beginTransaction();
            //First two parameters are for entry, the last two are for exit (animations)
            transactor.setCustomAnimations(R.anim.swipe_left, R.anim.swipe_right,
                    R.anim.swipe_left, R.anim.swipe_right);
            //Only want the fragment to close (not the activity), therefore
            //explicitly add it to the stack
            transactor.addToBackStack("fragInstance");
            transactor.add(R.id.mapContainer, fragment, "mapFrag").commit();
            editor.putBoolean("goog_services", true);
        }
        editor.apply();
        fragManager = getSupportFragmentManager();
        active = fragment;
    }

    private void inflateRestaurantList(){
        manager = Manager.getInstance(this);
        settings = FilterSettings.getInstance(this);
        Log.e("boolean value", settings.isHasBeenFiltered() + "");
        if(settings.isHasBeenFiltered() == true) {
            restaurants = filterRestaurants();
        }
        else {
            restaurants = manager.getRestaurantList();
        }
        adapter = new RestaurantListAdapter(this, restaurants);

        if(restaurants == null){
            TextView emptyListText = findViewById(R.id.noRestaurantsText);
            emptyListText.setText(getResources().getString(R.string.no_restaurants_text));
        } else {
            DatabaseHelper db = new DatabaseHelper(this);
            Cursor data = db.getData();
            List<String> listData = new ArrayList<>();
            while (data.moveToNext()) {
                listData.add(data.getString(0));
            }
            for (String s : listData) {
                for (Restaurant r : restaurants) {
                    if (s.equals(r.getTrackingNumber())) {
                        r.setFavourite(true);
                    }
                }
            }
            restaurants = manager.getRestaurantList();
//            restaurants = manager.getRestaurantList();
            adapter = new RestaurantListAdapter(this, restaurants);
            RecyclerView restaurantList = findViewById(R.id.restaurantList);
            adapter.notifyDataSetChanged();
            restaurantList.setAdapter(adapter);
            restaurantList.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private List<Restaurant> filterRestaurants() {
        settings = FilterSettings.getInstance(this);
        manager = Manager.getInstance(this);

        Log.e("size of original list ", manager.getRestaurantList().size() + "");

        List<Restaurant> result = new ArrayList<>();
        Log.e("settings in res list", "" + settings.getHazLevel());
        Log.e("settings in res list", "" + settings.getFavourite());

        if((!settings.getFavourite()) && (settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() == -1)) { // default case where user hasnt inputted any new settings
            result = manager.getRestaurantList();
        }
        else if((settings.getHazLevel().equals("all")) && (settings.getFavourite()) && (settings.getCriticalIssues() == -1)){ // case where only favourites is changed
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);
                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if (curRestaurant.isFavourite()) {
                        result.add(curRestaurant);
                    }
                }
            }
        }

        else if((!settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() == -1)){ // case where only haz level and maybe favourites is changed
            Log.e("settings in res list", "In here!");
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);
                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);
                    if (curRestaurant.isFavourite() == settings.getFavourite() && (mostRecentInspection.getHazardLevel().equalsIgnoreCase(settings.getHazLevel()))) {
                        Log.e("settings in res list", "Haz level in loop " +  mostRecentInspection.getHazardLevel());
                        Log.e("ResList", "" + curRestaurant.getName());
                        result.add(curRestaurant);
                    }
                }
            }

        }
        // need most recent inspection for haz level and crit issues
        else if((settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (settings.getGreaterThenInput() && settings.getLowerThenInput())) { // case where critical issues filter is inputted and user wants restaurants greater then that number
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);

                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite()) && (mostRecentInspection.getNumCritical() >= settings.getCriticalIssues())){
                        result.add(curRestaurant);
                    }
                }


            }

        }
        else if((!settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (settings.getGreaterThenInput() && !settings.getLowerThenInput())) { // crit issues filter + haz level filter
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);
                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite()) && (mostRecentInspection.getNumCritical() >= settings.getCriticalIssues()) &&
                            (mostRecentInspection.getHazardLevel().equalsIgnoreCase(settings.getHazLevel()))){
                        result.add(curRestaurant);
                    }
                }


            }
        }
        else if((settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (settings.getGreaterThenInput() && !settings.getLowerThenInput())) { // case where critical issues filter is inputted and user wants restaurants greater then that number
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);

                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite()) && (mostRecentInspection.getNumCritical() >= settings.getCriticalIssues())){
                        result.add(curRestaurant);
                    }
                }


            }

        }
        else if((!settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (!settings.getGreaterThenInput() && settings.getLowerThenInput())) { // crit issues filter with lower then crit issues true + haz level filter
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);
                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite()) && (mostRecentInspection.getNumCritical() <= settings.getCriticalIssues()) &&
                            (mostRecentInspection.getHazardLevel().equalsIgnoreCase(settings.getHazLevel()))){
                        result.add(curRestaurant);
                    }
                }


            }
        }
        else if((settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (!settings.getGreaterThenInput() && settings.getLowerThenInput())) { // case where critical issues filter is inputted and user wants restaurants greater then that number
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);

                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite()) && (mostRecentInspection.getNumCritical() <= settings.getCriticalIssues())){
                        result.add(curRestaurant);
                    }
                }
            }
        }
        else if((settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (!settings.getGreaterThenInput() && !settings.getLowerThenInput())) {
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);

                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite())) {
                        result.add(curRestaurant);
                    }

                }

            }
        }
        else if((!settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (!settings.getGreaterThenInput() && !settings.getLowerThenInput())) {
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);

                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite()) && mostRecentInspection.getHazardLevel().equalsIgnoreCase(settings.getHazLevel())) {
                        result.add(curRestaurant);
                    }
                }

            }
        }

        Log.e("size of result", "" + result.size());

        settings = FilterSettings.getInstance(this);
        settings.setFilteredRestaurants(result);
        settings.setHasBeenFiltered(true);

        return result;
    }


    private void setUpBackButton(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void setUpNavigationBar() {
        fragManager = getSupportFragmentManager();
        BottomNavigationView navMenu = findViewById(R.id.navigationView);
        navMenu.setSelectedItemId(R.id.navigation_map);
        navMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.navigation_resList):
                        if (fragManager.getBackStackEntryCount() > 0) {
                            fragManager.popBackStack();
                            fragManager.beginTransaction().hide(active).hide(fragment).commit();

                            //Reset the toolbar sub tittle (would apply to the case in which
                            //the coordinates are clicked and it is changed to "location")
                            ActionBar detailsBar = getSupportActionBar();
                            detailsBar.setSubtitle(R.string.filter_tittle);

                            return true;
                        }
                        return false;

                    case (R.id.navigation_map):
                        if (fragManager.getBackStackEntryCount() == 0) {
                            setUpMapOpen(getWindow().getDecorView().getRootView());
                            fragManager.beginTransaction().hide(active).show(fragment).commit();
                            return true;
                        }
                        return false;
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
    public static Intent getIntent(Context context){
        Intent intent = new Intent(context, RestaurantDetails.class);
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
            setContentView(R.layout.activity_restaurant_list_custom); //NEXUS S Specific
        } else if(width == 1440 && height == 2560) {
            setContentView(R.layout.activity_restaurant_list_custom_one);
        } else{
            setContentView(R.layout.activity_restaurant_list);
        }

        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#252525"));
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
                    //Lengthened for accommodation of slower processes
                }, 4500);
            }
        } else{
            Log.i("lengthened_launch", "Map is launching as default screen for the first time");
            getRequiredPermissions();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    setUpMapOpen(getWindow().getDecorView().getRootView());
                }
            }, 4500);
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

    //Used to redirect map launch from coordinates to restaurantList
    public static Intent getIntent(Context context, double latitude, double longitude, boolean coordinateFlag, int restaurantIndex){
        Intent intent = new Intent(context, RestaurantList.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("coordinateFlag", coordinateFlag);
        intent.putExtra("restaurantIndex", restaurantIndex);
        return intent;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Do nothing
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        //Do nothing
    }

    public void setActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.reduced_option);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Back button of toolbar clicked
        if (item.getItemId() == android.R.id.home) {
            Log.e("options", "testing");
            Intent I = new Intent(this, FilterOptions.class);
            startActivity(I);
            finish();

        }
        return true;
    }

}

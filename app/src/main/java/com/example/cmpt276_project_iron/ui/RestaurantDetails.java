package com.example.cmpt276_project_iron.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Restaurant;

import java.util.List;

/**
 *  Attains and sets the necessary information for the restaurant's details
 */

public class RestaurantDetails extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener {

    private Restaurant curRestaurant;
    private Manager manager;
    private boolean gServicesFlag;
    private int restaurantIndex;

    // private FrameLayout mapContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayCorrectLayout();
        setUpBackButton();

        getIntentData();
        placeRestaurantNameText();
        placeRestaurantIcon();
        placeAddressText();
        placeGPScoords();
        setUpGPScoordsClick();

        //Creates and fills the list of inspections with necessary data
        inflateInspectionList();

    }

    private void placeRestaurantNameText(){
        ActionBar detailsBar = getSupportActionBar();
        String restaurantTitle = curRestaurant.getName();

        detailsBar.setTitle(restaurantTitle);
        detailsBar.setSubtitle(getResources().getString(R.string.restaurantExtension));

    }

    private void placeRestaurantIcon(){
        ImageView restaurantIcon = findViewById(R.id.restaurantIcon);
        restaurantIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        restaurantIcon.setImageResource(R.drawable.restaurant_icon);
    }

    private void placeAddressText(){
        TextView address = findViewById(R.id.restaurantAddress);
        String restaurantAddress = getResources().getString(R.string.restaurant_address,
                curRestaurant.getPhysicalAddress(),curRestaurant.getPhysicalCity());
        address.setText(restaurantAddress);
    }

    private void placeGPScoords(){
        TextView coordinates = findViewById(R.id.restaurantCoords);

        //Add an underline to make it apparent that it is clickable
        coordinates.setText(HtmlCompat.fromHtml(getString(R.string.restaurant_coordinates,
                curRestaurant.getLatitude(), curRestaurant.getLongitude()), HtmlCompat.FROM_HTML_MODE_LEGACY));

    }

    private void setUpGPScoordsClick() {
        TextView coordinates = findViewById(R.id.restaurantCoords);
        final int NO_GOOGLE_PLAY = 9001;

        coordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("coordinates_clicked", "Coordinates: " + curRestaurant.getLatitude()
                        + ", " + curRestaurant.getLongitude());
                if (gServicesFlag) {
                    //Once the coordinates are clicked, open the fragment with the necessary data being
                    //passed in
                    //Storing an indicator in sharedPreferences such that the intent values are not accessed
                    //in cases they shouldn't be
                    SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = data.edit();
                    editor.putBoolean("coord_launch", true);
                    editor.apply();

                    //Putting the necessary data in intent since this activity must be finished
                    //for correct flow
                    Intent intent = RestaurantList.getIntent(v.getContext(), curRestaurant.getLatitude(),
                            curRestaurant.getLongitude(), true, restaurantIndex);
                    //Clearing the above stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    v.getContext().startActivity(intent);

                    //finish();

                    /*MapFragment fragment = MapFragment.newInstance(curRestaurant.getLatitude(),
                            curRestaurant.getLongitude(), true, restaurantIndex);*/
                    /*FragmentTransaction transactor = getSupportFragmentManager().beginTransaction();
                    transactor.setCustomAnimations(R.anim.swipe_left, R.anim.swipe_right,
                            R.anim.swipe_left, R.anim.swipe_right);
                    //Adding to stack will be used to exit the fragment
                    transactor.addToBackStack("fragInstance");
                    transactor.add(R.id.mapContainer, fragment, "mapFrag").commit();*/
                } else {
                    Toast.makeText(getApplicationContext(), "Must have Google Play Services to " +
                            "launch map", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Do nothing
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        //Do nothing
    }

    private void inflateInspectionList(){
        List<Inspection> inspections = manager.getInspectionMap().get(curRestaurant.getTrackingNumber());

        if(inspections == null){
            TextView emptyListText = findViewById(R.id.noInspectionsText);
            emptyListText.setText(getResources().getString(R.string.no_inspection_text));
        } else {
            RecyclerView inspectionList = findViewById(R.id.inspectionList);
            DetailsListAdapter adapter = new DetailsListAdapter(this, inspections);
            adapter.notifyDataSetChanged();
            inspectionList.setAdapter(adapter);
            inspectionList.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void getIntentData(){

        //Using sharedPreferences to address bug where the def value would be called if coming back from third activity
        //which would set the cur restaurant to the def value. By using SharedPreferences, this data is only changed coming
        //from the first activity
        SharedPreferences data = this.getSharedPreferences("data", MODE_PRIVATE);
        restaurantIndex = getIntent().getIntExtra("restaurantIndex", data.getInt("cur_restaurant", 2));
        manager = Manager.getInstance(this);
        gServicesFlag = data.getBoolean("goog_services", false);


        manager = Manager.getInstance(this);
        curRestaurant = manager.getRestaurantList().get(restaurantIndex);
    }

    public static Intent getIntent(Context context, int restaurantIndex){
        Intent intent = new Intent(context, RestaurantDetails.class);
        intent.putExtra("restaurantIndex", restaurantIndex);
        return intent;
    }

    private void setUpBackButton(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //For the toolbar back button, since a fragment will be launched from this activity which will share
    //the same toolbar, when back is pressed we want it to go back and remove the fragment, NOT close the
    //activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //If the back button of the action bar is clicked
        if (item.getItemId() == android.R.id.home) {
            //if the FragmentManager is != null and consist of some fragments, it will close those
            //first, then when it's clicked again, it will close this activity
            FragmentManager manager = getFragmentManager();
            if (manager.getBackStackEntryCount() > 0) {
                manager.popBackStack();

                //Since the toolbar subtitle is changed when launching the map fragment, it has to be reset when coming
                //back
                ActionBar detailsBar = getSupportActionBar();
                detailsBar.setSubtitle(getResources().getString(R.string.restaurantExtension));
            } else {

                ActionBar detailsBar = getSupportActionBar();
                detailsBar.setSubtitle(getResources().getString(R.string.restaurantExtension));

                //If there are no fragments, act as normal
                super.onBackPressed();
            }
        }

        return true;
    }

    private void displayCorrectLayout(){
        Display dimensions = getWindowManager().getDefaultDisplay();
        Point dimension = new Point();
        dimensions.getSize(dimension);
        int width = dimension.x;
        int height = dimension.y;
        float density = getResources().getDisplayMetrics().density;


        /*
         * Android will automatically choose best layout in accordance to normal/large/xlarge (already custom xmls),
         * however, phones such as the Nexus S do not choose this correctly and therefore setting a special case
         */
        //Checking if it's not a MDPI type screen, used to distinguish between same resolution phones that are of different sizes
        double MDPI_SCREEN_SIZE = 1.0;
        if (width == 480 && height == 800 && density != MDPI_SCREEN_SIZE) {
            setContentView(R.layout.activity_restaurant_details_custom);
        } else if (width == 1440 && height == 2560) {
            setContentView(R.layout.activity_restaurant_details_custom_one);
        } else{
            setContentView(R.layout.activity_restaurant_details);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //To prevent from changing when going to the previous activity (once the fragment has been closed)
        //Check if any fragment currently open
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() == 0) {
            manager.popBackStack();

            ActionBar detailsBar = getSupportActionBar();
            detailsBar.setSubtitle(getResources().getString(R.string.restaurantExtension));
        } else {
            //If there are no fragments, act as normal
            super.onBackPressed();
        }
    }
}

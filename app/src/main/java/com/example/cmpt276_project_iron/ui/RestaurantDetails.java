package com.example.cmpt276_project_iron.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Restaurant;

import java.util.List;

/**
 *  Attains and sets the necessary information for the restaurant's details
 */

public class RestaurantDetails extends AppCompatActivity {

    private Restaurant curRestaurant;
    private Manager manager;

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

        //setUpMapOpen()

        //Creates and fills the list of inspections with necessary data
        inflateInspectionList();

    }


    /*//Used for opening map <--- Rework once certain point reached
    private void setUpMapOpen(){
        Button mapButton = findViewById(R.id.temporaryButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment fragment = new MapFragment(); //****
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.swipe_left, R.anim.swipe_right,
                        R.anim.swipe_left, R.anim.swipe_right);

                //Only want the fragment to close (Not the activity), therefore
                //explicitly add it to the stack
                ft.addToBackStack(null);
                ft.add(R.id.mapContainer, fragment, "mapFrag").commit();
            }
        });
    }*/

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
        String restaurantCoords = getResources().getString(R.string.restaurant_coordinates,
                curRestaurant.getLatitude(), curRestaurant.getLongitude());
        coordinates.setText(restaurantCoords);
    }

    private void inflateInspectionList(){
        List<Inspection> inspections = manager.getInspectionMap().get(curRestaurant.getTrackingNumber());
        if(inspections == null){
            TextView emptyListText = findViewById(R.id.noInspectionsText);
            emptyListText.setText(getResources().getString(R.string.no_inspection_text));
        } else {
            ListView inspectionList = findViewById(R.id.inspectionList);
            CustomListAdapter adapter = new CustomListAdapter(this, R.layout.inspection_list_item, inspections);
            adapter.notifyDataSetChanged();
            inspectionList.setAdapter(adapter);
        }
    }

    private void getIntentData(){

        //Using sharedPreferences to address bug where the def value would be called if coming back from third activity
        //which would set the cur restaurant to the def value. By using SharedPreferences, this data is only changed coming
        //from the first activity
        SharedPreferences data = this.getSharedPreferences("data", MODE_PRIVATE);
        int index = getIntent().getIntExtra("restaurantIndex", data.getInt("cur_restaurant", 2));
        manager = Manager.getInstance();
        curRestaurant = manager.getRestaurantList().get(index);
    }

    public static Intent getIntent(Context context, int restaurantIndex){
        Intent intent = new Intent(context, RestaurantDetails.class);
        intent.putExtra("restaurantIndex", restaurantIndex);
        return intent;
    }

    private void setUpBackButton(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}

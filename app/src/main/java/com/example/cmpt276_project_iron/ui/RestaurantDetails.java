package com.example.cmpt276_project_iron.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

        /**
         * Creates and fills the list of inspections with necessary data
         */
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
        String restaurantAddress = curRestaurant.getPhysicalCity() + ", " + curRestaurant.getPhysicalAddress();

        address.setText(restaurantAddress);
    }

    private void placeGPScoords(){
        TextView coordinates = findViewById(R.id.restaurantCoords);
        String restaurantCoords = curRestaurant.getLatitude() + ", " + curRestaurant.getLongitude();
        coordinates.setText(restaurantCoords);
    }

    private void inflateInspectionList(){
        List<Inspection> inspections = manager.getInspectionMap().get(curRestaurant.getTrackingNumber());

        if(inspections == null){
            TextView emptyListText = findViewById(R.id.noInspectionsText);
            emptyListText.setText(getResources().getString(R.string.no_inspection_text));
        }
        else {
            ListView inspectionList = findViewById(R.id.inspectionList);
            CustomListAdapter adapter = new CustomListAdapter(this, R.layout.inspection_list_item, inspections);
            adapter.notifyDataSetChanged();

            inspectionList.setAdapter(adapter);
        }
    }

    private void getIntentData(){
        int index = getIntent().getIntExtra("restaurantIndex", 0);
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

        /**
         * Android will automatically choose best layout in accordance to normal/large/xlarge (already custom xmls),
         * however, phones such as the Nexus S do not choose this correctly and therefore setting a special case
         */
        if(width == 480 && height == 800) {
            setContentView(R.layout.activity_restaurant_details_custom);
        }
        else if(width == 1440 && height == 2560) {
            setContentView(R.layout.activity_restaurant_details_custom_one);
        }
        else{
            setContentView(R.layout.activity_restaurant_details);
        }
    }
}

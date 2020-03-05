package com.example.cmpt276_project_iron.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Restaurant;

import java.util.List;

/*
    Attains and sets the necessary information for the restaurant's details
 */

public class RestaurantDetails extends AppCompatActivity {

    //Stores the restaurant that is passed through intent when one is clicked in the 1st activity
    private Restaurant curRestaurant;
    private Manager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        //Attain info from intent so it can be accessed with ease for more details
        getIntentData();

        //Set title of screen to pertain to the current restaurant in format: <restaurant's name> details
        placeRestaurantNameText();

        placeRestaurantIcon();

        placeAddressText();

        placeGPScoords();

        //Creates and fills the list of inspections with necessary data
        inflateInspectionList();

    }

    private void placeRestaurantNameText(){
        ActionBar detailsBar = getSupportActionBar();
        String restaurantTitle = curRestaurant.getName() + getResources().getString(R.string.restaurantExtension);

        detailsBar.setTitle(restaurantTitle);
    }

    private void placeRestaurantIcon(){
        ImageView restaurantIcon = findViewById(R.id.restaurantIcon);
        restaurantIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        restaurantIcon.setImageResource(R.drawable.restaurant_icon);
    }

    private void placeAddressText(){
        TextView address = findViewById(R.id.restaurantAddress);
        String restaurantAddress = curRestaurant.getPhysicalAddress();

        address.setText(restaurantAddress);
    }

    private void placeGPScoords(){
        TextView coordinates = findViewById(R.id.restaurantCoords);
        //Combined in a standard format of <latitude> , <longitude>
        String restaurantCoords = curRestaurant.getLatitude() + ", " + curRestaurant.getLongitude();
        coordinates.setText(restaurantCoords);
    }

    private void inflateInspectionList(){
        //attain the inspections pertaining to the restaurant to use with arrayAdapter to display
        List<Inspection> inspections = manager.getInspectionMap().get(curRestaurant.getTrackingNumber());

        Log.i("inspections_amount", "# inspections: " + inspections.size());

        if(inspections == null){
            //If there are no inspections for the restaurant then set a text to indicate for the user
            TextView emptyListText = findViewById(R.id.noInspectionsText);
            emptyListText.setText(getResources().getString(R.string.no_inspection_text));
        }
        else {
            //'move' elements from list to ListView via the custom adapter
            ListView inspectionList = findViewById(R.id.inspectionList);
            CustomListAdapter adapter = new CustomListAdapter(this, R.layout.inspection_list_item, inspections);
            adapter.notifyDataSetChanged();

            inspectionList.setAdapter(adapter);
        }
    }

    private void getIntentData(){

        //curRestaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");

        //Safest to have a default value of 0
        int index = getIntent().getIntExtra("restaurantIndex", 0);
        manager = Manager.getInstance();
        curRestaurant = manager.getRestaurantList().get(index);
    }

    /*Intent via pass object
    public static Intent getIntent(Context context, Restaurant restaurant){
        // .class (not .this) as it is being created when launched
        Intent intent = new Intent(context, RestaurantDetails.class);

        //To enable, related classes implement the serializable interface
        intent.putExtra("restaurant", restaurant);

        return intent;
    }*/

    //Intent via pass index
    public static Intent getIntent(Context context, int restaurantIndex){
        // .class (not .this) as it is being created when launched
        Intent intent = new Intent(context, RestaurantDetails.class);

        //To enable, related classes implement the serializable interface
        intent.putExtra("restaurantIndex", restaurantIndex);

        return intent;
    }
}

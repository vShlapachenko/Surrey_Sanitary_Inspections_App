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
import android.widget.Toast;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.InspectionManager;
import com.example.cmpt276_project_iron.model.Restaurant;
import com.example.cmpt276_project_iron.model.RestaurantManager;

import java.util.List;

public class RestaurantDetails extends AppCompatActivity {

    //Stores the restaurant that is passed through intent when one is clicked in the 1st activity
    private Restaurant curRestaurant = null;
    private InspectionManager inspectionManager = null;
    private List<Inspection> inspections = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        //Attain info from intent so it can be accessed with ease for more details      ************************************ CHANGE BACK
        RestaurantManager manager = RestaurantManager.getInstance();
        List<Restaurant> a = manager.getRestaurantList();
        //curRestaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        curRestaurant = a.get(0);

        //Set title of screen to pertain to the current restaurant in format: <restaurant's name> details
        setRestaurantName();

        setRestaurantIcon();

        setAddress();

        setGPScoords();

        //Creates and fills the list of inspections with necessary data
        inflateInspectionList();

    }

    private void setRestaurantName(){
        ActionBar detailsBar = getSupportActionBar();
        String restaurantTitle = curRestaurant.getName() + "'s Details";

        detailsBar.setTitle(restaurantTitle);
    }

    private void setRestaurantIcon(){
        ImageView restaurantIcon = findViewById(R.id.restaurantIcon);
        restaurantIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        restaurantIcon.setImageResource(R.drawable.restaurant_icon);
    }

    private void setAddress(){
        TextView address = findViewById(R.id.restaurantAddress);
        String restaurantAddress = curRestaurant.getPhysicalAddress();

        address.setText(restaurantAddress);
    }

    private void setGPScoords(){
        TextView coordinates = findViewById(R.id.restaurantCoords);
        //Combined in a standard format of <latitude> , <longitude>
        String restaurantCoords = curRestaurant.getLatitude() + ", " + curRestaurant.getLongitude();
        coordinates.setText(restaurantCoords);
    }

    private void inflateInspectionList(){
        //attain the inspections pertaining to the restaurant to use with arrayAdapter to display
        //Rework once map figured
        inspectionManager = InspectionManager.getInstance();
        inspectionManager.filterList(curRestaurant.getTrackingNumber());
        inspections = inspectionManager.getFilteredList();
        Toast.makeText(this,"# inspections: " + inspections.size(), Toast.LENGTH_LONG).show();

        //'move' elements from list to ListView via the custom adapter
        ListView inspectionList = findViewById(R.id.inspectionList);
        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.inspection_list_item, inspections);

        inspectionList.setAdapter(adapter);

    }

    public static Intent getIntent(Context context, Restaurant restaurant){
        // .class (not .this) as it is being created when launched
        Intent intent = new Intent(context, RestaurantDetails.class);

        //To enable, related classes implement the serializable interface
        intent.putExtra("restaurant", restaurant);

        return intent;
    }
}

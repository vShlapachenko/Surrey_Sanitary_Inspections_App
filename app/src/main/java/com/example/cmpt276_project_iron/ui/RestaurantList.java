package com.example.cmpt276_project_iron.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Restaurant;

import java.util.List;


/**
 *  Attains and sets the necessary information for the restaurant's details
 */
public class RestaurantList extends AppCompatActivity {
    private Manager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayCorrectLayout();
        setUpBackButton();

        manager = Manager.getInstance();

        inflateRestaurantList();
    }

    private void inflateRestaurantList(){
        List<Restaurant> restaurants = manager.getRestaurantList();

        if(restaurants == null){
            TextView emptyListText = findViewById(R.id.noRestaurantsText);
            emptyListText.setText(getResources().getString(R.string.no_restaurants_text));
        } else {
            ListView restaurantList = findViewById(R.id.restaurantList);
            RestaurantListAdapter adapter = new RestaurantListAdapter(this,
                    R.layout.restaurant_list_item, restaurants);
            adapter.notifyDataSetChanged();
            restaurantList.setAdapter(adapter);
        }
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

        /*
         * Android will automatically choose best layout in accordance to normal/large/xlarge (already custom xmls),
         * however, phones such as the Nexus S do not choose this correctly and therefore setting a special case
         */
        if (width == 480 && height == 800)
        {
            setContentView(R.layout.activity_restaurant_list);
        } else {
            setContentView(R.layout.activity_restaurant_list);
        }
    }
}

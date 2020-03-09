package com.example.cmpt276_project_iron.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Manager;

public class RestaurantList extends AppCompatActivity {
    private Manager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayCorrectLayout();
        setUpBackButton();


        manager = Manager.getInstance();

        placeRestaurantNameText();

        placeRestaurantIcon();

        placeAddressText();

        placeGPScoords();

        /**
         * Creates and fills the list of inspections with necessary data
         */
        inflateInspectionList();

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
        if (width == 480 && height == 800)
        {
            setContentView(R.layout.activity_restaurant_details_custom);
        }
        else{
            setContentView(R.layout.activity_restaurant_details);
        }
    }
}

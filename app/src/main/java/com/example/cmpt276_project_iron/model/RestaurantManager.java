package com.example.cmpt276_project_iron.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates sorted List of restaurants by date
 */

public class RestaurantManager{

    private static RestaurantManager instance;
    private List<Restaurant> restaurantList = new ArrayList<>();

    public static RestaurantManager getInstance() {
        if (instance == null){
            instance = new RestaurantManager();
            CSVConverterForRestaurant converter = new CSVConverterForRestaurant();
            converter.convertRestaurantCSVToList();
        }
        return instance;
    }

    public List<Restaurant> getRestaurantList(){
        return restaurantList;
    }

    public void add(String trackingNumber, String name, String physicalAddress, String physicalCity,
                    String facType, double latitude, double longitude){
        restaurantList.add(new Restaurant(trackingNumber, name, physicalAddress, physicalCity,
                facType, latitude, longitude));
    }

}

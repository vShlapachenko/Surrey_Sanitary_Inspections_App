package com.example.cmpt276_project_iron.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RestaurantManager{

    private static RestaurantManager instance;
    private static List<Restaurant> restaurantList = new ArrayList<>();

    public static RestaurantManager getInstance() {
        if (instance == null){
            instance = new RestaurantManager();
            CSVConverterForRestaurant converter = new CSVConverterForRestaurant();
            converter.convertRestaurantCSVToList();
            Collections.sort(restaurantList);
        }
        return instance;
    }

    @Override
    public String toString() {
        return "RestaurantManager{" +
                "restaurantList=" + restaurantList +
                '}';
    }

    private List<Restaurant> getRestaurantList(){
        return restaurantList;
    }

    public void add(String trackingNumber, String name, String physicalAddress, String physicalCity,
                    String facType, double latitude, double longitude){
        restaurantList.add(new Restaurant(trackingNumber, name, physicalAddress, physicalCity,
                facType, latitude, longitude));
    }

}

package com.example.cmpt276_project_iron.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantManager{

    private static RestaurantManager instance;
    private static List<Restaurant> restaurantList;

    public static RestaurantManager getInstance() {
        if (instance == null){
            instance = new RestaurantManager();
        }
        return instance;
    }

    @Override
    public String toString() {
        return "RestaurantManager{" +
                "restaurantList=" + restaurantList +
                '}';
    }

    public static List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    /*

   Arrays.asList(
                new Restaurant("SDFO-8HKP7E","Pattullo A&W","12808 King George Blvd",
                        "Surrey","Restaurant",49.20610961,-122.8668064),
                new Restaurant("SHEN-B7BNSR","Lee Yuen Seafood Restaurant","1812 152 St",
                        "Surrey","Restaurant",49.03508252,-122.80086843),
                new Restaurant("NOSU-CHNUM","The Unfindable Bar","12345 67 Ave",
                        "Surrey","Restaurant",49.14214908,-122.86815856),
                new Restaurant("SWOD-AHZUMF","Lee Yuen Seafood Restaurant","14755 104 Ave",
                        "Surrey","Restaurant",49.19166808,-122.8136896),
                new Restaurant("SDFO-8GPUJX","Top in Town Pizza","12788 76A Ave",
                        "Surrey","Restaurant",49.14218908,-122.86855856),
                new Restaurant("SWOD-APSP3X","104 Sushi & Co.","10422 168 St",
                        "Surrey","Restaurant",49.19205936,-122.75625586),
                new Restaurant("SHEN-ANSLB4","Top In Town Pizza","14330 64 Ave",
                        "Surrey","Restaurant",49.11851305,-122.82521495),
                new Restaurant("SPLH-9NEUHG","Zugba Flame Grilled Chicken","14351 104 Ave",
                        "Surrey","Restaurant",49.19172759,-122.82418348)
        );
     */
}

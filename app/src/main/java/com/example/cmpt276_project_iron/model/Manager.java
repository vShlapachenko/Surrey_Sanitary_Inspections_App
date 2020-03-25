package com.example.cmpt276_project_iron.model;

import android.content.Context;

import java.util.List;
import java.util.Map;

/**
 * Creates Map where key is Restaurant's trackingNumber
 * Value in map = List<Inspection> (List is sorted by date in descending order) newer - > older
 */

public class Manager {

    private static Manager instance;
    private static final String INSPECTION_FILE_PATH = "res/raw/inspectionreports_itr1.csv";
    private static final String RESTAURANT_FILE_PATH = "res/raw/restaurants_itr1.csv";
    private static final String RESTAURANTS_FILE_FROM_ONLINE = "restaurants.csv";
    private static final String INSPECTION_FILE_FROM_ONLINE = "inspections.csv";

    private final Map<String, List<Inspection>> inspectionMap;
    private final List<Restaurant> restaurantList;

    private Context context;


    private Manager(Map<String, List<Inspection>> inspectionMap,
                    List<Restaurant> restaurantList,
                    Context context) {
        this.inspectionMap = inspectionMap;
        this.restaurantList = restaurantList;
        this.context = context;
    }

    public static Manager getInstance(Context context) {
        if (instance == null) {
            CsvInspectionReader inspectionReader = new CsvInspectionReader(context, INSPECTION_FILE_FROM_ONLINE);
            Map<String, List<Inspection>> inspectionMap = inspectionReader.read();
            CsvRestaurantReader restaurantReader = new CsvRestaurantReader(context, RESTAURANTS_FILE_FROM_ONLINE);
            List<Restaurant> restaurantList = restaurantReader.read();
            instance = new Manager(inspectionMap, restaurantList, context);
        }
        return instance;
    }

    public Map<String, List<Inspection>> getInspectionMap() {
        return inspectionMap;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }
}

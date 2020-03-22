package com.example.cmpt276_project_iron.model;

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

    private final Map<String, List<Inspection>> inspectionMap;
    private final List<Restaurant> restaurantList;

    private Manager(Map<String, List<Inspection>> inspectionMap, List<Restaurant> restaurantList) {
        this.inspectionMap = inspectionMap;
        this.restaurantList = restaurantList;
    }

    public static Manager getInstance() {
        if (instance == null) {
            CsvInspectionReader inspectionReader = new CsvInspectionReader(INSPECTION_FILE_PATH);
            Map<String, List<Inspection>> inspectionMap = inspectionReader.read(INSPECTION_FILE_PATH);
            CsvRestaurantReader restaurantReader = new CsvRestaurantReader();
            List<Restaurant> restaurantList = restaurantReader.read(RESTAURANT_FILE_PATH);
            instance = new Manager(inspectionMap, restaurantList);
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

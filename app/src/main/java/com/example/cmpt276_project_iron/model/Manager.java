package com.example.cmpt276_project_iron.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Creates Map where key is Restaurant's trackingNumber
 * Value in map = List<Inspection> (List is sorted by date in descending order) newer - > older
 */

public class Manager {

    private static Manager instance;

    private Map<String, List<Inspection>> inspectionMap;
    private List<Restaurant> restaurantList = new ArrayList<>();

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
            CSVConverterForInspection inspectionConverter = new CSVConverterForInspection();
            inspectionConverter.convertInspectionCSVToMap();
            CSVConverterForRestaurant restaurantConverter = new CSVConverterForRestaurant();
            restaurantConverter.convertRestaurantCSVToList();
        }
        return instance;
    }

    public Map<String, List<Inspection>> getInspectionMap() {
        return inspectionMap;
    }

    void setInspectionMap(Map<String, List<Inspection>> inspectionMap) {
        this.inspectionMap = inspectionMap;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void addRestaurant(String trackingNumber, String name, String physicalAddress, String physicalCity,
                              String facType, double latitude, double longitude) {
        restaurantList.add(new Restaurant(trackingNumber, name, physicalAddress, physicalCity,
                facType, latitude, longitude));
    }
}

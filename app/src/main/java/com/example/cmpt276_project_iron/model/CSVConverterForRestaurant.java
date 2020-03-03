package com.example.cmpt276_project_iron.model;

import java.util.Collections;

/**
 * in future will convert CSV file to restaurantList
 * Right now hard codes Dr.Brian's sample
 */

public class CSVConverterForRestaurant {
    private RestaurantManager manager = RestaurantManager.getInstance();

    public void convertRestaurantCSVToList() {
        createList();
        Collections.sort(manager.getRestaurantList());
    }

    private void createList() {
        manager.add("SDFO-8HKP7E", "Pattullo A&W", "12808 King George Blvd",
                "Surrey", "Restaurant", 49.20610961, -122.8668064);
        manager.add("SHEN-B7BNSR", "Lee Yuen Seafood Restaurant", "1812 152 St",
                "Surrey", "Restaurant", 49.03508252, -122.80086843);
        manager.add("NOSU-CHNUM", "The Unfindable Bar", "12345 67 Ave",
                "Surrey", "Restaurant", 49.14214908, -122.86815856);
        manager.add("SWOD-AHZUMF", "Lee Yuen Seafood Restaurant", "14755 104 Ave",
                "Surrey", "Restaurant", 49.19166808, -122.8136896);
        manager.add("SDFO-8GPUJX", "Top in Town Pizza", "12788 76A Ave",
                "Surrey", "Restaurant", 49.14218908, -122.86855856);
        manager.add("SWOD-APSP3X", "104 Sushi & Co.", "10422 168 St",
                "Surrey", "Restaurant", 49.19205936, -122.75625586);
        manager.add("SHEN-ANSLB4", "Top In Town Pizza", "14330 64 Ave",
                "Surrey", "Restaurant", 49.11851305, -122.82521495);
        manager.add("SPLH-9NEUHG", "Zugba Flame Grilled Chicken", "14351 104 Ave",
                "Surrey", "Restaurant", 49.19172759, -122.82418348);
    }
}

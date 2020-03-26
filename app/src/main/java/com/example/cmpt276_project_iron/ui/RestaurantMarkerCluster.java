package com.example.cmpt276_project_iron.ui;

import com.example.cmpt276_project_iron.model.Restaurant;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class RestaurantMarkerCluster implements ClusterItem {
    private String name;
    private LatLng latLng;
    public RestaurantMarkerCluster(Restaurant restaurant) {
        this.name = restaurant.getName();
        this.latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());

    }
    @Override
    public LatLng getPosition() {  // 1
        return latLng;
    }
    @Override
    public String getTitle() {  // 2
        return name;
    }
    @Override
    public String getSnippet() {
        return "";
    }

}

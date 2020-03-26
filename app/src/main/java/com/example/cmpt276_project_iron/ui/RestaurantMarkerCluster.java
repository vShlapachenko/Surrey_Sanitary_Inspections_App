package com.example.cmpt276_project_iron.ui;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Restaurant;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class RestaurantMarkerCluster implements ClusterItem {
    private String name;
    private LatLng latLng;
    private Restaurant restaurant;
    private Manager manager;
    private int id;
    public RestaurantMarkerCluster(Restaurant restaurant, Manager manager, int id) {
        this.name = restaurant.getName();
        this.latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
        this.restaurant = restaurant;
        this.manager = manager;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
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
        if(!(this.getManager().getInspectionMap().get(this.getRestaurant().getTrackingNumber()) == null)) {
            return restaurant.getPhysicalAddress() + ", " + manager.getInspectionMap().get(restaurant.getTrackingNumber()).get(0).getHazardLevel();
        }
        else {
            return restaurant.getPhysicalAddress() + ", " + "No Inspection available";
        }

    }

}

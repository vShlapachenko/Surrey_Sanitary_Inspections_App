package com.example.cmpt276_project_iron.model;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FilterSettings {
    private static FilterSettings instance;
    private String hazLevel;
    private Boolean isFavourite;
    private Boolean isGreaterThenInput;
    private Boolean isLowerThenInput;
    private int criticalIssues;
    private List<Restaurant> filteredRestaurants;
    private boolean hasBeenFiltered;
    private Manager manager;

    private Context context;

    public List<Restaurant> getFilteredRestaurants() {
        return filteredRestaurants;
    }

    public void setFilteredRestaurants(List<Restaurant> filteredRestaurants) {
        this.filteredRestaurants = filteredRestaurants;
    }

    public boolean isHasBeenFiltered() {
        return hasBeenFiltered;
    }

    public void setHasBeenFiltered(boolean hasBeenFiltered) {
        this.hasBeenFiltered = hasBeenFiltered;
    }

    private FilterSettings(Context c) {
        this.context = c;
        this.manager = Manager.getInstance(context);
        this.hazLevel = "all";
        this.isFavourite = false;
        this.isGreaterThenInput = false;
        this.isLowerThenInput = false;
        this.criticalIssues = -1;
//        this.filteredRestaurants = new ArrayList<>();
        this.filteredRestaurants = manager.getRestaurantList();
        this.hasBeenFiltered = false;
    }

    public static FilterSettings getInstance(Context c) {
        if(instance == null) {
            instance = new FilterSettings(c);
        }
        return instance;
    }

    public static void setInstance(FilterSettings instance) {
        FilterSettings.instance = instance;
    }

    public String getHazLevel() {
        return hazLevel;
    }

    public void setHazLevel(String hazLevel) {
        this.hazLevel = hazLevel;
    }

    public Boolean getFavourite() {
        return isFavourite;
    }

    public void setFavourite(Boolean favourite) {
        isFavourite = favourite;
    }

    public Boolean getGreaterThenInput() {
        return isGreaterThenInput;
    }

    public void setGreaterThenInput(Boolean greaterThenInput) {
        isGreaterThenInput = greaterThenInput;
    }

    public Boolean getLowerThenInput() {
        return isLowerThenInput;
    }

    public void setLowerThenInput(Boolean lowerThenInput) {
        isLowerThenInput = lowerThenInput;
    }

    public int getCriticalIssues() {
        return criticalIssues;
    }

    public void setCriticalIssues(int input) {
        this.criticalIssues = input;
    }

    public FilterSettings(String hazLevel, Boolean isFavourite, Boolean isGreaterThenInput, Boolean isLowerThenInput, int input) {
        this.hazLevel = hazLevel;
        this.isFavourite = isFavourite;
        this.isGreaterThenInput = isGreaterThenInput;
        this.isLowerThenInput = isLowerThenInput;
        this.criticalIssues = input;
    }
}


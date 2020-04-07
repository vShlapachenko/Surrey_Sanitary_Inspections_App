package com.example.cmpt276_project_iron.model;

public class FilterSettings {
    private static FilterSettings instance;
    private String hazLevel;
    private Boolean isFavourite;
    private Boolean isGreaterThenInput;
    private Boolean isLowerThenInput;
    private int criticalIssues;

    public FilterSettings() {
        this.hazLevel = "all";
        this.isFavourite = false;
        this.isGreaterThenInput = false;
        this.isLowerThenInput = false;
        this.criticalIssues = -1;
    }

    public static FilterSettings getInstance() {
        if(instance == null) {
            instance = new FilterSettings();
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


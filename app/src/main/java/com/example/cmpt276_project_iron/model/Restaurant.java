package com.example.cmpt276_project_iron.model;

import java.io.Serializable;

/**
 * Restaurant (immutable) object with all getters
 */
//Implements Serializable interface for passing data through intent for UI
public class Restaurant implements Comparable<Restaurant>, Serializable {
    private final String trackingNumber;
    private final String name;
    private final String physicalAddress;
    private final String physicalCity;
    private final String facType;
    private final double latitude;
    private final double longitude;

    public Restaurant(String trackingNumber, String name, String physicalAddress, String physicalCity,
                      String facType, double latitude, double longitude) {
        this.trackingNumber = trackingNumber;
        this.name = name;
        this.physicalAddress = physicalAddress;
        this.physicalCity = physicalCity;
        this.facType = facType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public String getPhysicalCity() {
        return physicalCity;
    }

    public String getFacType() {
        return facType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int compareTo(Restaurant other) {
        return this.name.compareTo(other.name);
    }
}

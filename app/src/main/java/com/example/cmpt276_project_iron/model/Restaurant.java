package com.example.cmpt276_project_iron.model;

public class Restaurant implements Comparable<Restaurant>{
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

    @Override
    public String toString() {
        return "Restaurant{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", name='" + name + '\'' +
                ", physicalAddress='" + physicalAddress + '\'' +
                ", physicalCity='" + physicalCity + '\'' +
                ", facType='" + facType + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public int compareTo(Restaurant other) {
        return this.name.compareTo(other.name);
    }
}

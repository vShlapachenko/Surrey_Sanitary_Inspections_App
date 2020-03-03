package com.example.cmpt276_project_iron.model;

/**
 * Inspection object with all getters
 */

public class Inspection implements Comparable<Inspection>{
    private final String trackingNumber;
    private final int inspectionDate;
    private final String inspectionType;
    private final int numCritical;
    private final int namNonCritical;
    private final String hazardLevel;
    private final String violLump;

    public Inspection(String trackingNumber, int inspectionDate, String inspectionType,
                      int numCritical, int namNonCritical, String hazardLevel, String violLump) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
        this.numCritical = numCritical;
        this.namNonCritical = namNonCritical;
        this.hazardLevel = hazardLevel;
        this.violLump = violLump;
    }

    public String getTrackingNumber(){
        return trackingNumber;
    }

    public int getInspectionDate() {
        return inspectionDate;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public int getNamNonCritical() {
        return namNonCritical;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public String getViolLump() {
        return violLump;
    }

    @Override
    public int compareTo(Inspection other) {
        return -(this.inspectionDate - other.inspectionDate);
    }
}

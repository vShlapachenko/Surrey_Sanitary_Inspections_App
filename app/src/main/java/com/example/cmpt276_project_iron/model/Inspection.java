package com.example.cmpt276_project_iron.model;

import java.util.Calendar;
import java.util.List;

/**
 * Inspection (immutable) object with all getters
 */

public class Inspection implements Comparable<Inspection> {
    private final String trackingNumber;
    private final Calendar inspectionDate;
    private final String inspectionType;
    private final int numCritical;
    private final int numNonCritical;
    private final String hazardLevel;
    private final List<Violation> violationList;

    public Inspection(String trackingNumber, Calendar inspectionDate, String inspectionType, int numCritical,
                      int numNonCritical, String hazardLevel, List<Violation> violationList) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.inspectionType = inspectionType;
        this.numCritical = numCritical;
        this.numNonCritical = numNonCritical;
        this.hazardLevel = hazardLevel;
        this.violationList = violationList;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Calendar getInspectionDate() {
        return inspectionDate;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;

    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public List<Violation> getViolationList() {
        return violationList;
    }

    @Override
    public int compareTo(Inspection other) {
        return other.inspectionDate.compareTo(this.inspectionDate);
    }
}

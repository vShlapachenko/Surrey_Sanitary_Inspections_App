package com.example.cmpt276_project_iron.model;

public class Inspection implements Comparable<Inspection>{
    final String trackingNumber;
    final int inspectionDate;
    final String inspectionType;
    final int numCritical;
    final int namNonCritical;
    final String hazardLevel;
    final String violLump;

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

    @Override
    public int compareTo(Inspection o) {
        return 0;
    }
}

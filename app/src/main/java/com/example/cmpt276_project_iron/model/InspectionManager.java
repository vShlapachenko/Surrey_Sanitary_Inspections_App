package com.example.cmpt276_project_iron.model;

import java.util.ArrayList;
import java.util.List;

public class InspectionManager {

    private static InspectionManager instance;

    private static List<Inspection> inspectionList = new ArrayList<>();

    public static InspectionManager getInstance() {
        if (instance == null){
            instance = new InspectionManager();
            CSVConverterForInspection converter = new CSVConverterForInspection();
            converter.convertInspectionCSVToList();

        }
        return instance;
    }

    public void add(String trackingNumber, int inspectionDate, String inspectionType, int numCritical,
                    int namNonCritical, String hazardLevel, String violLump){
        inspectionList.add(new Inspection(trackingNumber, inspectionDate, inspectionType, numCritical,
                namNonCritical, hazardLevel, violLump));
    }
}

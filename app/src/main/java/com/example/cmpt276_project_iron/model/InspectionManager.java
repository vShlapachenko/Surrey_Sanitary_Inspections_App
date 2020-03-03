package com.example.cmpt276_project_iron.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Creates Map where key is Restaurant's trackingNumber
 * Value in map = List<Inspection> (List is sorted by date in descending order) newer - > older
 */

public class InspectionManager {

    private static InspectionManager instance;

    private List<Inspection> inspectionList = new ArrayList<>();
    private Map<String, List<Inspection>> inspectionMap;

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

    List<Inspection> getList(){
        return inspectionList;
    }

    public Map<String, List<Inspection>> getInspectionMap(){
        return inspectionMap;
    }

    void setInspectionMap(Map<String, List<Inspection>> inspectionMap) {
        this.inspectionMap = inspectionMap;
    }

}

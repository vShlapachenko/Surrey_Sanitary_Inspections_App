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

    private List<Inspection> filteredList;

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


    public List<Inspection> getList(){
        return inspectionList;
    }

    public void filterList(String trackingNum){
        //Creating a new arrayList each time the filter function is called such that it can be filtered multiple times for a single one
        filteredList =  new ArrayList<>();
        for(Inspection i : inspectionList){
            if(i.getTrackingNumber().equalsIgnoreCase(trackingNum)){
                filteredList.add(i);
            }
        }
    }
    //^^ Temporary, needs to be removed if committed by mistake

    public List<Inspection> getFilteredList(){
        return filteredList;
    }

    public Map<String, List<Inspection>> getInspectionMap(){
        return inspectionMap;
    }

    void setInspectionMap(Map<String, List<Inspection>> inspectionMap) {
        this.inspectionMap = inspectionMap;
    }

}

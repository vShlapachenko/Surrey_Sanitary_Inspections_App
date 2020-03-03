package com.example.cmpt276_project_iron.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * in future will convert CSV file to inspectionList
 * Right now hard codes Dr.Brian's sample
 */

public class CSVConverterForInspection {
    private InspectionManager manager = InspectionManager.getInstance();

    public void convertInspectionCSVToList() {
        addAllToList();
        convertListToMap();
    }

    private void convertListToMap() {
        manager.setInspectionMap(manager.getList()
                .stream()
                .collect(Collectors.groupingBy(Inspection::getTrackingNumber)));
        for(Map.Entry<String, List<Inspection>> entry : manager.getInspectionMap().entrySet()){
            Collections.sort(entry.getValue());
        }
        manager.getList().clear();
    }

    private void addAllToList() {
        manager.add("SDFO-8HKP7E",20181101,"Follow-Up",0,1,"Low","308,Not Critical,Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)],Not Repeat");
        manager.add("SHEN-ANSLB4",20190227,"Routine",1,0,"Low","301,Critical,Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17(1)],Not Repeat");
        manager.add("SPLH-9NEUHG",20190410,"Routine",1,1,"Low","206,Critical,Hot potentially hazardous food stored/displayed below 60 °C. [s. 14(2)],Not Repeat|501,Not Critical,Operator does not have FOODSAFE Level 1 or Equivalent [s. 10(1)],Not Repeat");
        manager.add("SWOD-AHZUMF",20191105,"Routine",2,5,"High","203,Critical,Food not cooled in an acceptable manner [s. 12(a)],Not Repeat|205,Critical,Cold potentially hazardous food stored/displayed above 4 °C. [s. 14(2)],Not Repeat|209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|211,No");
        manager.add("SHEN-B7BNSR",20190115,"Routine",2,3,"High","209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|210,Not Critical,Food not thawed in an acceptable manner [s. 14(2)],Not Repeat|301,Critical,Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17(1");
        manager.add("SWOD-AHZUMF",20200122,"Routine",2,3,"Moderate","205,Critical,Cold potentially hazardous food stored/displayed above 4 °C. [s. 14(2)],Not Repeat|209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|306,Not Critical,Food premises not maintained in a sanitary condition [s. 17(1)],");
        manager.add("SWOD-AHZUMF",20181030,"Follow-Up",0,0,"Low", null);
        manager.add("SWOD-AHZUMF",20180319,"Routine",6,8,"High","201,Critical,Food contaminated or unfit for human consumption [s. 13],Not Repeat|203,Critical,Food not cooled in an acceptable manner [s. 12(a)],Not Repeat|205,Critical,Cold potentially hazardous food stored/displayed above 4 °C. [s. 14(2)],Not Repeat|20");
        manager.add("SDFO-8GPUJX",20191001,"Follow-Up",1,0,"Low","302,Critical,Equipment/utensils/food contact surfaces not properly washed and sanitized [s. 17(2)],Not Repeat");
        manager.add("SDFO-8GPUJX",20190926,"Follow-Up",0,0,"Low", null);
        manager.add("SDFO-8GPUJX",20180718,"Routine",1,4,"Low","305,Not Critical,Conditions observed that may allow entrance/harbouring/breeding of pests [s. 26(b)(c)],Not Repeat|306,Not Critical,Food premises not maintained in a sanitary condition [s. 17(1)],Not Repeat|307,Not Critical,Equipment/utensils/food contact");
        manager.add("SDFO-8HKP7E",20181019,"Routine",0,1,"Low","308,Not Critical,Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)],Not Repeat");
        manager.add("SHEN-B7BNSR",20190924,"Follow-Up",3,2,"High","209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|210,Not Critical,Food not thawed in an acceptable manner [s. 14(2)],Not Repeat|301,Critical,Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17(1");
        manager.add("SWOD-APSP3X",20190305,"Routine",0,2,"Low","306,Not Critical,Food premises not maintained in a sanitary condition [s. 17(1)],Not Repeat|403,Not Critical,Employee lacks good personal hygiene clean clothing and hair control [s. 21(1)],Not Repeat");
        manager.add("SHEN-ANSLB4",20180305,"Routine",0,0,"Low", null);
        manager.add("SHEN-B7BNSR",20190130,"Follow-Up",1,1,"Low","308,Not Critical,Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)],Not Repeat|401,Critical,Adequate handwashing stations not available for employees [s. 21(4)],Not Repeat");
        manager.add("SDFO-8GPUJX",20190924,"Routine",2,4,"High","205,Critical,Cold potentially hazardous food stored/displayed above 4 °C. [s. 14(2)],Not Repeat|302,Critical,Equipment/utensils/food contact surfaces not properly washed and sanitized [s. 17(2)],Not Repeat|304,Not Critical,Premises not free of pests [s. ");
        manager.add("SWOD-AHZUMF",20190301,"Follow-Up",0,0,"Low", null);
        manager.add("SWOD-APSP3X",20180625,"Routine",2,3,"Moderate","209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|303,Critical,Equipment/facilities/hot & cold water for sanitary maintenance not adequate [s. 17(3); s. 4(1)(f)],Not Repeat|308,Not Critical,Equipment/utensils/food contact surfac");
        manager.add("SHEN-ANSLB4",20190624,"Routine",0,0,"Low", null);
        manager.add("SHEN-B7BNSR",20190827,"Follow-Up",1,1,"Low","210,Not Critical,Food not thawed in an acceptable manner [s. 14(2)],Not Repeat|301,Critical,Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17(1)],Not Repeat");
        manager.add("SHEN-B7BNSR",20190312,"Follow-Up",0,1,"Low","306,Not Critical,Food premises not maintained in a sanitary condition [s. 17(1)],Not Repeat");
        manager.add("SWOD-AHZUMF",20180626,"Routine",3,4,"High","205,Critical,Cold potentially hazardous food stored/displayed above 4 °C. [s. 14(2)],Not Repeat|209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|301,Critical,Equipment/utensils/food contact surfaces not maintained in sanitary ");
        manager.add("SWOD-AHZUMF",20190131,"Follow-Up",3,1,"High","201,Critical,Food contaminated or unfit for human consumption [s. 13],Not Repeat|306,Not Critical,Food premises not maintained in a sanitary condition [s. 17(1)],Not Repeat|401,Critical,Adequate handwashing stations not available for employees [s. 21(4)],");
        manager.add("SHEN-B7BNSR",20191105,"Routine",1,1,"Low","209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|301,Critical,Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17(1)],Not Repeat");
        manager.add("SHEN-ANSLB4",20180831,"Follow-Up",0,0,"Low", null);
        manager.add("SDFO-8HKP7E",20180508,"Follow-Up",0,0,"Low", null);
        manager.add("SDFO-8HKP7E",20190410,"Routine",0,0,"Low", null);
        manager.add("SDFO-8GPUJX",20190320,"Routine",2,4,"Low","209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|302,Critical,Equipment/utensils/food contact surfaces not properly washed and sanitized [s. 17(2)],Not Repeat|305,Not Critical,Conditions observed that may allow entrance/harbour");
        manager.add("SHEN-ANSLB4",20191216,"Routine",0,0,"Low", null);
        manager.add("SDFO-8HKP7E",20191002,"Routine",0,0,"Low", null);
        manager.add("SDFO-8HKP7E",20181024,"Follow-Up",0,1,"Low","308,Not Critical,Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)],Not Repeat");
        manager.add("SDFO-8GPUJX",20180312,"Routine",0,2,"Low","308,Not Critical,Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)],Not Repeat|309,Not Critical,Chemicals cleansers & similar agents stored or labeled improperly [s. 27],Not Repeat");
        manager.add("SDFO-8HKP7E",20181106,"Follow-Up",0,0,"Low", null);
        manager.add("SHEN-B7BNSR",20190703,"Routine",3,2,"High","203,Critical,Food not cooled in an acceptable manner [s. 12(a)],Not Repeat|209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|301,Critical,Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17(1)],N");
        manager.add("SPLH-9NEUHG",20180530,"Routine",1,1,"Low","302,Critical,Equipment/utensils/food contact surfaces not properly washed and sanitized [s. 17(2)],Not Repeat|306,Not Critical,Food premises not maintained in a sanitary condition [s. 17(1)],Not Repeat");
        manager.add("SWOD-AHZUMF",20190624,"Routine",2,6,"High","209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|302,Critical,Equipment/utensils/food contact surfaces not properly washed and sanitized [s. 17(2)],Not Repeat|304,Not Critical,Premises not free of pests [s. 26(a)],Not Repeat|30");
        manager.add("SWOD-AHZUMF",20190201,"Follow-Up",0,0,"Low", null);
        manager.add("SWOD-AHZUMF",20181207,"Follow-Up",0,0,"Low", null);
        manager.add("SWOD-AHZUMF",20181029,"Routine",5,6,"High","201,Critical,Food contaminated or unfit for human consumption [s. 13],Not Repeat|209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|301,Critical,Equipment/utensils/food contact surfaces not maintained in sanitary condition [s. 17");
        manager.add("SWOD-AHZUMF",20180320,"Follow-Up",0,0,"Low", null);
        manager.add("SWOD-AHZUMF",20190708,"Follow-Up",0,0,"Low", null);
        manager.add("SWOD-AHZUMF",20181030,"Follow-Up",1,0,"High","201,Critical,Food contaminated or unfit for human consumption [s. 13],Not Repeat");
        manager.add("SDFO-8HKP7E",20180502,"Routine",0,2,"Low","308,Not Critical,Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)],Not Repeat|315,Not Critical,Refrigeration units and hot holding equipment lack accurate thermometers [s. 19(2)],Not Repeat");
        manager.add("SHEN-B7BNSR",20191001,"Follow-Up",0,1,"Low","209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat");
        manager.add("SPLH-9NEUHG",20191001,"Routine",0,1,"Low","209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat");
        manager.add("SPLH-9NEUHG",20181029,"Routine",1,2,"Low","206,Critical,Hot potentially hazardous food stored/displayed below 60 °C. [s. 14(2)],Not Repeat|209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|501,Not Critical,Operator does not have FOODSAFE Level 1 or Equivalent [s. 10(1)]");
        manager.add("SWOD-AHZUMF",20190729,"Follow-Up",0,0,"Low", null);
        manager.add("SWOD-AHZUMF",20180629,"Follow-Up",0,0,"Low", null);
        manager.add("SWOD-AHZUMF",20190624,"Follow-Up",0,0,"Low", null);
        manager.add("SHEN-B7BNSR",20190318,"Follow-Up",0,0,"Low", null);
        manager.add("SHEN-ANSLB4",20180823,"Routine",1,1,"Low","302,Critical,Equipment/utensils/food contact surfaces not properly washed and sanitized [s. 17(2)],Not Repeat|306,Not Critical,Food premises not maintained in a sanitary condition [s. 17(1)],Not Repeat");
        manager.add("SWOD-AHZUMF",20190103,"Follow-Up",1,4,"High","201,Critical,Food contaminated or unfit for human consumption [s. 13],Not Repeat|210,Not Critical,Food not thawed in an acceptable manner [s. 14(2)],Not Repeat|304,Not Critical,Premises not free of pests [s. 26(a)],Not Repeat|305,Not Critical,Conditions o");
        manager.add("SWOD-APSP3X",20190830,"Routine",1,0,"Low","205,Critical,Cold potentially hazardous food stored/displayed above 4 °C. [s. 14(2)],Not Repeat");
        manager.add("SWOD-AHZUMF",20190226,"Routine",7,6,"High","201,Critical,Food contaminated or unfit for human consumption [s. 13],Not Repeat|202,Critical,Food not processed in a manner that makes it safe to eat [s. 14(1)],Not Repeat|205,Critical,Cold potentially hazardous food stored/displayed above 4 °C. [s. 14(");
    }
}

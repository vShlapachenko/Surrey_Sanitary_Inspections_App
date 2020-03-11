package com.example.cmpt276_project_iron.model;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Converts CSV file to Inspection Map
 * where Tracking Number is the Key
 */

public class CSVConverterForInspection {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    private static final int NUM_FIELDS_IN_VIOL = 4;

    private List<Inspection> inspectionList = new ArrayList<>();
    private Manager manager = Manager.getInstance();

    void convertInspectionCSVToMap() {
        makeListFromFile();
        convertListToMap();
    }

    private void makeListFromFile() {
        String file = "res/raw/inspectionreports_itr1.csv";
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
        Scanner scanner = new Scanner(in);
        scanner.nextLine();
        while (scanner.hasNext()) {
            List<String> line = parseLine(scanner.nextLine());
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            try {
                cal.setTime(sdf.parse(line.get(1)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            inspectionList.add(new Inspection(line.get(0), cal, line.get(2), Integer.parseInt(line.get(3)),
                    Integer.parseInt(line.get(4)), line.get(5), convertToViolList(line.get(6))));
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
    }

    private List<Violation> convertToViolList(String violLump) {
        if (violLump.equals("")) {
            return null;
        }
        List<Violation> violationList = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        StringBuffer oneField = new StringBuffer();
        char[] charArr = violLump.toCharArray();
        for (char c : charArr) {
            if (c == ',') {
                strings.add(oneField.toString());
                oneField = new StringBuffer();
            } else if (c == '|') {
                strings.add(oneField.toString());
                oneField = new StringBuffer();
                violationList.add(new Violation(Integer.parseInt(strings.get(0)), isCritical(strings.get(1))
                        , strings.get(2), isRepeat(strings.get(3))));
                strings = new ArrayList<>();
            } else {
                oneField.append(c);
            }
        }
        strings.add(oneField.toString());
        if (strings.size() == NUM_FIELDS_IN_VIOL) {
            violationList.add(new Violation(Integer.parseInt(strings.get(0)), isCritical(strings.get(1))
                    , strings.get(2), isRepeat(strings.get(3))));
        }
        return violationList;
    }

    private boolean isRepeat(String s) {
        return s.equalsIgnoreCase("repeat");
    }

    private boolean isCritical(String s) {
        return s.equalsIgnoreCase("critical");
    }

    private static List<String> parseLine(String cvsLine) {
        List<String> result = new ArrayList<>();
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }
        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        char[] chars = cvsLine.toCharArray();
        for (char ch : chars) {
            if (inQuotes) {
                if (ch == DEFAULT_QUOTE) {
                    inQuotes = false;
                } else {
                    curVal.append(ch);
                }
            } else {
                if (ch == DEFAULT_QUOTE) {
                    inQuotes = true;
                } else if (ch == DEFAULT_SEPARATOR) {
                    result.add(curVal.toString());
                    curVal = new StringBuffer();
                } else if (ch == '\n') {
                    break;
                } else {
                    curVal.append(ch);
                }
            }
        }
        result.add(curVal.toString());
        return result;
    }

    private void convertListToMap() {
        Map<String, List<Inspection>> map = new HashMap<>();
        Collections.sort(inspectionList, new Comparator<Inspection>() {
            @Override
            public int compare(Inspection o1, Inspection o2) {
                return o1.getTrackingNumber().compareToIgnoreCase(o2.getTrackingNumber());
            }
        });
        List <Inspection> inspections = new ArrayList<>();
        inspections.add(inspectionList.get(0));
        for (int i = 0; i < inspectionList.size(); i++) {
            Inspection inspection = inspectionList.get(i);
            if (i + 1 != inspectionList.size() && inspection.getTrackingNumber().equals(inspectionList.get(i + 1).getTrackingNumber())){
                inspections.add(inspectionList.get(i + 1));
            } else if (i + 1 != inspectionList.size() && !inspection.getTrackingNumber().equals(inspectionList.get(i + 1).getTrackingNumber())){
                map.put(inspection.getTrackingNumber(), inspections);
                inspections = new ArrayList<>();
            }
        }
        manager.setInspectionMap(map);
        for (Map.Entry<String, List<Inspection>> entry : manager.getInspectionMap().entrySet()) {
            Collections.sort(entry.getValue());
        }
        inspectionList.clear();
    }
//private void convertListToMap() {
//    manager.setInspectionMap(inspectionList
//            .stream()
//            .collect(Collectors.groupingBy(Inspection::getTrackingNumber)));
//    for (Map.Entry<String, List<Inspection>> entry : manager.getInspectionMap().entrySet()) {
//        Collections.sort(entry.getValue());
//    }
//    inspectionList.clear();
//}

}

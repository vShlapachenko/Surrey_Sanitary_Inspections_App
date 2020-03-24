package com.example.cmpt276_project_iron.model;

import android.content.Context;
import android.util.Log;

import com.example.cmpt276_project_iron.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Converts CSV file to Inspection Map
 * where Tracking Number is the Key
 */

public class CsvInspectionReader {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    private static final int NUM_FIELDS_IN_VIOL = 4;
    private int violLump;
    private int hazardRating;
    private Context context;
    private final String downloadedFile;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");


    public CsvInspectionReader(Context context, String downloadedFile) {
        this.downloadedFile = downloadedFile;
        this.context = context;
        try (BufferedReader reader = getReader()) {
            String line = reader.readLine();
            String[] fields = line.split(",");
            for (int i = 0; i < fields.length; i++) {
                fields[i] = fields[i].replace("\"", "");
            }
            if (fields[6].equalsIgnoreCase("ViolLump")) {
                this.hazardRating = 5;
                this.violLump = 6;
            } else if (fields[5].equalsIgnoreCase("ViolLump")) {
                Log.e("FIELDS [6] : ", fields[6]);
                this.hazardRating = 6;
                this.violLump = 5;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<Inspection>> read() {
        try (BufferedReader reader = getReader()) {
            return reader.lines()
                    .skip(1)
                    .map(this::convertLine)
                    .filter(Objects::nonNull)
                    .collect(groupingBy(Inspection::getTrackingNumber))
                    .entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                            .sorted()
                            .collect(toList())));
        } catch (IOException e) {
            throw new IllegalArgumentException("In native CSV file" +
                    " : File was not provided correctly", e);
        }
    }

    private Inspection convertLine(String line) {
        List<String> lineParameters = parseLine(line);

        if (lineParameters.get(0).equals("")) {
            return null;
        }
        Calendar cal = convertToCalendar(lineParameters.get(1));
        if (violLump == 6) {
            return new Inspection(
                    lineParameters.get(0),
                    cal,
                    lineParameters.get(2),
                    Integer.parseInt(lineParameters.get(3)),
                    Integer.parseInt(lineParameters.get(4)),
                    lineParameters.get(hazardRating),
                    convertToViolList(lineParameters.get(violLump)));
        } else if (violLump == 5) {
            return new Inspection(
                    lineParameters.get(0),
                    cal,
                    lineParameters.get(2),
                    Integer.parseInt(lineParameters.get(3)),
                    Integer.parseInt(lineParameters.get(4)),
                    lineParameters.get(hazardRating),
                    convertToViolList(lineParameters.get(violLump)));
        }
        return null;
    }

    private Calendar convertToCalendar(String csvDate) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(DATE_FORMAT.parse(csvDate));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Wrong Date was given : " + csvDate, e);
        }
        return cal;
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
                violationList.add(new Violation(Integer.parseInt(strings.get(0)), isCritical(strings.get(1)),
                        strings.get(2), isRepeat(strings.get(3))));
                strings = new ArrayList<>();
            } else {
                oneField.append(c);
            }
        }
        strings.add(oneField.toString());
        if (strings.size() == NUM_FIELDS_IN_VIOL) {
            violationList.add(new Violation(Integer.parseInt(strings.get(0)), isCritical(strings.get(1)),
                    strings.get(2), isRepeat(strings.get(3))));
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

    private BufferedReader getReader() {
        File file = context.getFileStreamPath(downloadedFile);
        if (file.exists()) {
            try {
                Log.e("In ger reader", "inspection");
                return new BufferedReader(new InputStreamReader(context.openFileInput(downloadedFile)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new BufferedReader(new InputStreamReader(context
                .getResources()
                .openRawResource(R.raw.inspectionreports_itr1)));
    }
}


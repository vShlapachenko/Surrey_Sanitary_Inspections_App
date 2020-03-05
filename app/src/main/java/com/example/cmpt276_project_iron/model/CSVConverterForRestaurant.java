package com.example.cmpt276_project_iron.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Converts Restaurant CSV to sorted RestaurantList
 */

public class CSVConverterForRestaurant {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    private Manager manager = Manager.getInstance();

    public void convertRestaurantCSVToList() {
        convertToList();
        Collections.sort(manager.getRestaurantList());
    }

    private void convertToList() {
        String file = "res/raw/restaurants_itr1.csv";
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
        Scanner scanner = new Scanner(in);
        scanner.nextLine();
        while (scanner.hasNext()) {
            List<String> line = parseLine(scanner.nextLine());
            manager.addRestaurant(line.get(0), line.get(1), line.get(2), line.get(3), line.get(4),
                    Double.parseDouble(line.get(5)), Double.parseDouble(line.get(6)));
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
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
}

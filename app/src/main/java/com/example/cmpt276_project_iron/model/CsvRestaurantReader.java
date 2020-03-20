package com.example.cmpt276_project_iron.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Converts Restaurant CSV to sorted RestaurantList
 */

public class CsvRestaurantReader {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    public List<Restaurant> read(String restaurantFilePath) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(restaurantFilePath)))) {
            return reader.lines()
                    .skip(1)
                    .map(this::convertLine)
                    .sorted()
                    .collect(toList());
        } catch (IOException e) {
            throw new IllegalArgumentException(restaurantFilePath +
                    " : File was not provided correctly", e);
        }
    }

    private Restaurant convertLine(String line1) {
        List<String> lineParameters = parseLine(line1);
        return new Restaurant(
                lineParameters.get(0),
                lineParameters.get(1),
                lineParameters.get(2),
                lineParameters.get(3),
                lineParameters.get(4),
                Double.parseDouble(lineParameters.get(5)),
                Double.parseDouble(lineParameters.get(6)));
    }

    private static List<String> parseLine(String cvsLine) {
        List<String> result = new ArrayList<>();
        if (cvsLine == null || cvsLine.isEmpty()) {
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

package com.example.cmpt276_project_iron.model;

import android.content.Context;
import com.example.cmpt276_project_iron.R;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class DateConversionCalculator {

    private static final int MONTHS_IN_A_YEAR = 12;
    private static final int DAYS_THRESHOLD = 30;

    public static String getFormattedDate(Context context, Calendar givenDate){
        Calendar today = Calendar.getInstance();
        int inspectionYear = givenDate.get(Calendar.YEAR);
        int inspectionMonth = givenDate.get(Calendar.MONTH);
        int inspectionDay = givenDate.get(Calendar.DATE);
        long daysBetween = ChronoUnit.DAYS.between(givenDate.toInstant(), today.toInstant());
        int yearsDiff = today.get(Calendar.YEAR) - inspectionYear;
        int monthDiff = today.get(Calendar.MONTH) - inspectionMonth;
        int monthsBetween = yearsDiff * MONTHS_IN_A_YEAR + monthDiff;
        if (daysBetween <= DAYS_THRESHOLD){
            return Long.toString(daysBetween);
        } else if(monthsBetween < MONTHS_IN_A_YEAR
                || (monthsBetween == MONTHS_IN_A_YEAR && inspectionDay - today.get(Calendar.DATE) < 0)) {
            String[] monthArray = context.getResources().getStringArray(R.array.months);
            return context.getResources().getString(R.string.inspection_date,
                    monthArray[inspectionMonth], Integer.toString(inspectionDay));
        } else {
            String[] monthArray = context.getResources().getStringArray(R.array.months);
            return context.getResources().getString(R.string.inspection_date,
                    monthArray[inspectionMonth], Integer.toString(inspectionYear));
        }
    }

    public static String getFullFormattedDate(Context context, Calendar givenDate){
        int inspectionYear = givenDate.get(Calendar.YEAR);
        int inspectionMonth = givenDate.get(Calendar.MONTH);
        int inspectionDay = givenDate.get(Calendar.DATE);
        String[] monthArray = context.getResources().getStringArray(R.array.months);
        return context.getResources().getString(R.string.inspection_date_full,
                monthArray[inspectionMonth], inspectionDay, inspectionYear);
    }
}

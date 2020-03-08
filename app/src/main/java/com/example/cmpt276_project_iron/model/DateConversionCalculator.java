package com.example.cmpt276_project_iron.model;

import android.content.Context;

import com.example.cmpt276_project_iron.R;

import java.util.Calendar;

public class DateConversionCalculator {

    private static final int DAYS_THRESHOLD = 30;
    private static final int MONTHS_IN_A_YEAR = 12;
    private static final int HOURS_IN_DAY = 24;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MILLISECONDS_IN_SECOND = 1000;

    public static String getFormattedDate(Context context, Calendar givenDate){
        Calendar today = Calendar.getInstance();
        int inspectionYear = givenDate.get(Calendar.YEAR);
        int inspectionMonth = givenDate.get(Calendar.MONTH);
        int inspectionDay = givenDate.get(Calendar.DATE);
        long daysWithinLastDayExclusive = (today.getTimeInMillis() - givenDate.getTimeInMillis())
                / getMillisecondsInADay();
        long daysWithin = daysWithinLastDayExclusive + 1;
        int yearsDiff = today.get(Calendar.YEAR) - inspectionYear;
        int monthDiff = today.get(Calendar.MONTH) - inspectionMonth;
        int monthsBetween = yearsDiff * MONTHS_IN_A_YEAR + monthDiff;
        if (daysWithin <= DAYS_THRESHOLD){
            return Long.toString(daysWithin);
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

    private static int getMillisecondsInADay() {
        return MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE * MINUTES_IN_HOUR * HOURS_IN_DAY;
    }
}
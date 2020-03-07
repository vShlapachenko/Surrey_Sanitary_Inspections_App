package com.example.cmpt276_project_iron.model;

import android.content.Context;
import android.util.Log;

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
        long daysBetween = ChronoUnit.DAYS.between(givenDate.toInstant(), today.toInstant());
        int yearsDiff = today.get(Calendar.YEAR) - inspectionYear;
        int monthDiff = today.get(Calendar.MONTH) - inspectionMonth;
        int monthsBetween = yearsDiff * MONTHS_IN_A_YEAR + monthDiff;
        if (daysBetween <= DAYS_THRESHOLD){
            return Long.toString(daysBetween);
        } else if(monthsBetween <= MONTHS_IN_A_YEAR) {
            String[] monthArray = context.getResources().getStringArray(R.array.months);
            int inspectionDay = givenDate.get(Calendar.DATE);
            return context.getResources().getString(R.string.inspection_date,
                    monthArray[inspectionMonth], Integer.toString(inspectionDay));
        } else {
            String[] monthArray = context.getResources().getStringArray(R.array.months);
            return context.getResources().getString(R.string.inspection_date,
                    monthArray[inspectionMonth], Integer.toString(inspectionYear));
        }
    }
}

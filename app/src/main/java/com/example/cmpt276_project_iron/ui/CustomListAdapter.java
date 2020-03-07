package com.example.cmpt276_project_iron.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Restaurant;

import java.util.Calendar;
import java.util.List;
/*
Custom adapter for the list of inspections for the second screen, required for formatting with multiple data in a single item
 */
public class CustomListAdapter extends ArrayAdapter<Inspection> {

    private Context context;
    private int resource;
    private List<Inspection> inspections;


    public CustomListAdapter(Context context, int resource, List<Inspection> inspections){
        //Must call constructor of ArrayAdapter
        super(context, resource, inspections);

        this.context = context;
        this.resource = resource;
        this.inspections = inspections;
    }

    //Returns the view of each item (main functionality)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.inspection_list_item, null);
        view.setClickable(true);

        //Get inspection n in accordance to position n
        Inspection inspection = inspections.get(position);

        //Set the data in accordance with the list of inspections
        //Set the inspection number
        TextView inspectionNum = view.findViewById(R.id.inspectionNum);
        inspectionNum.setText(Integer.toString(position + 1));

        //set number of critical issues
        TextView critIssues = view.findViewById(R.id.numCritIssues);
        critIssues.setText(String.valueOf(inspection.getNumCritical()));

        //set number of non-critical issues
        TextView nonCritIssues = view.findViewById(R.id.numNonCritIssues);
        nonCritIssues.setText(String.valueOf(inspection.getNumNonCritical()));


        //set the inspection's date by processing the Calendar type from Inspection and the current date
        TextView inspectionDate = view.findViewById(R.id.inspection_date);

        //Extracting the required portions to compare with the current date's year/month which should yield the necessary data
        int year = inspection.getInspectionDate().get(Calendar.YEAR);
        int month = inspection.getInspectionDate().get(Calendar.MONTH);
        int day = inspection.getInspectionDate().get(Calendar.DAY_OF_MONTH);

        Calendar curCal = Calendar.getInstance();
        int curYear = curCal.get(Calendar.YEAR);
        int curMonth = curCal.get(Calendar.MONTH);
        int curDay = curCal.get(Calendar.DAY_OF_MONTH);

        //Determing which month the inspection took place for printing
        String monthName = "";
        switch(month){
            case Calendar.JANUARY:
                monthName = context.getResources().getString(R.string.jan_month);
                break;

            case Calendar.FEBRUARY:
                monthName = context.getResources().getString(R.string.feb_month);
                break;

            case Calendar.MARCH:
                monthName = context.getResources().getString(R.string.mar_month);
                break;

            case Calendar.APRIL:
                monthName = context.getResources().getString(R.string.apr_month);
                break;

            case Calendar.MAY:
                monthName = context.getResources().getString(R.string.may_month);
                break;

            case Calendar.JUNE:
                monthName = context.getResources().getString(R.string.jun_month);
                break;

            case Calendar.JULY:
                monthName = context.getResources().getString(R.string.jul_month);
                break;

            case Calendar.AUGUST:
                monthName = context.getResources().getString(R.string.aug_month);
                break;

            case Calendar.SEPTEMBER:
                monthName = context.getResources().getString(R.string.sep_month);
                break;

            case Calendar.OCTOBER:
                monthName = context.getResources().getString(R.string.oct_month);
                break;

            case Calendar.NOVEMBER:
                monthName = context.getResources().getString(R.string.nov_month);
                break;

            case Calendar.DECEMBER:
                monthName = context.getResources().getString(R.string.dec_month);

            //No default case as all the value ranges are covered, assuming that Calendar returns correclty
        }
        //Attaining the max days in the month of the inspection, used for case in which it is a seperate month but still less than 30 days
        int daysInMonth = inspection.getInspectionDate().getActualMaximum(Calendar.DAY_OF_MONTH);


        //Making comparisons between the date of inspection and curDate to determine which case to display in regards to
        //First case - within 30 days
        if(curYear == year && curMonth == month){
            //If same year and date, then it will always be in the past, so error checked in condition
            int dayDifference = curDay - day;
            inspectionDate.setText(context.getResources().getString(R.string.inspection_date,
                    Integer.toString(dayDifference), context.getResources().getString(R.string.days_text)));
        }

        //Alternative first case scenario + second case scenario one
        if(curYear == year){
            if(curMonth - month == 1){
                int dayDifference = curDay + (daysInMonth - day);
                if(dayDifference <= 30) {
                    inspectionDate.setText(context.getResources().getString(R.string.inspection_date,
                            Integer.toString(dayDifference), context.getResources().getString(R.string.days_text)));
                }
            }
            //Second case - less than a year ago
            else{
                inspectionDate.setText(context.getResources().getString(R.string.inspection_date,
                        monthName, Integer.toString(day)));
            }
        }
        //Second case - second scenario + third case first scenario
        if(curYear - year == 1){
            int monthDifference = curMonth - month;
            if(monthDifference < 0){ //It is within a year if the month difference is a negative number
                inspectionDate.setText(context.getResources().getString(R.string.inspection_date,
                        monthName, Integer.toString(day)));
            }
            else if(monthDifference == 0){
                int dayDifference = curDay - day;
                if(dayDifference < 0){
                    inspectionDate.setText(context.getResources().getString(R.string.inspection_date,
                            monthName, Integer.toString(day)));
                }
                else{
                    inspectionDate.setText(context.getResources().getString(R.string.inspection_date,
                            monthName, Integer.toString(year)));
                }
            }
            else{
                inspectionDate.setText(context.getResources().getString(R.string.inspection_date,
                        monthName, Integer.toString(year)));
            }
        }
        //Third case - more than a year ago , second scenario
        if(curYear - year > 1){
            inspectionDate.setText(context.getResources().getString(R.string.inspection_date,
                    monthName, Integer.toString(year)));
        }


        //Processing the hazard level so the appropriate hazard icon is assigned and a complementing background color
        String hazardLevel = inspection.getHazardLevel();
        ImageView hazardIcon = view.findViewById(R.id.hazardIcon);

        if(hazardLevel.equalsIgnoreCase("Low")){
            hazardIcon.setImageResource(R.drawable.low_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            //Setting background of the item along with tinting it such that the icon is more apparent
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLowHazard));

        }
        else if(hazardLevel.equalsIgnoreCase("Moderate")){
            hazardIcon.setImageResource(R.drawable.moderate_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorModerateHazard));
        }
        else if(hazardLevel.equalsIgnoreCase("High")){
            hazardIcon.setImageResource(R.drawable.high_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorHighHazard));

        }


        //Set an onclick listener for the individual buttons such that the third activity can be launched with the necessary info
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //currently disabled, however, once the third activity is up and running re-enable to have the data
                //of the clicked inspection (passed as the Inspection itself) passed into the third activity (rename as needed)
                //passing index/position as requested

                Intent intent = InspectionDetails.getIntent(view.getContext(), position, inspections.get(position).getTrackingNumber());
//                Intent intent = InspectionDetails.getIntent(this, position);


                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                Log.i("Position_clicked", position + " ");
            }
        });

        return view;
    }
}

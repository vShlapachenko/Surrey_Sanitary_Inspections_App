package com.example.cmpt276_project_iron.UI;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Inspection;

import java.util.Calendar;
import java.util.List;

//Custom adapter for the list of inspections for the second screen, required for formatting with multiple data in a single item
public class CustomListAdapter extends ArrayAdapter<Inspection> {

    private Context context;
    private int resource;
    private List<Inspection> inspections;

    //Used to label the inspections in format of <inspection n>
    private int counter = 0;

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
        inspectionNum.setText(Integer.toString(counter));


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
        int month = inspection.getInspectionDate().get(Calendar.MONTH) + 1; //Incrementing as starting at 0, but will use in calculations
        int day = inspection.getInspectionDate().get(Calendar.DAY_OF_MONTH);

        Calendar curCal = Calendar.getInstance();
        int curYear = curCal.get(Calendar.YEAR);
        int curMonth = curCal.get(Calendar.MONTH) + 1;
        int curDay = curCal.get(Calendar.DAY_OF_MONTH);

        String monthName = "";
        if(month == 1){
            monthName = context.getResources().getString(R.string.jan_month);
        }
        else if(month == 2){
            monthName = context.getResources().getString(R.string.feb_month);
        }
        else if(month == 3){
            monthName = context.getResources().getString(R.string.mar_month);
        }
        else if(month == 4){
            monthName = context.getResources().getString(R.string.apr_month);
        }
        else if(month == 5){
            monthName = context.getResources().getString(R.string.may_month);
        }
        else if(month == 6){
            monthName = context.getResources().getString(R.string.jun_month);
        }
        else if(month == 7){
            monthName = context.getResources().getString(R.string.jul_month);
        }
        else if(month == 8){
            monthName = context.getResources().getString(R.string.aug_month);
        }
        else if(month == 9){
            monthName = context.getResources().getString(R.string.sep_month);
        }
        else if(month == 10){
            monthName = context.getResources().getString(R.string.oct_month);
        }
        else if(month == 11){
            monthName = context.getResources().getString(R.string.nov_month);
        }
        else if(month == 12){
            monthName = context.getResources().getString(R.string.dec_month);
        }

        //Making comparisons between the date of inspection and curDate to determine which case to display in regards to
        //First case - within 30 days
        if(curYear == year && curMonth == month){
            //If same year and date, then it will always be in the past, so error checked in condition
            int dayDifference = curDay - day;
            inspectionDate.setText(dayDifference + context.getResources().getString(R.string.days_text));
        }
        //Alternative first case scenario + second case scenario one
        if(curYear == year){
            if(curMonth - month == 1){
                int dayDifference = curDay + (30 - day);
                if(dayDifference <= 30) {
                    inspectionDate.setText(dayDifference + context.getResources().getString(R.string.days_text));
                }
            }
            //Second case - less than a year ago
            else{
                inspectionDate.setText(monthName + " " + day);
            }
        }
        //Second case - second scenario + third case first scenario
        if(curYear - year == 1){
            int monthDifference = curMonth - month;
            if(monthDifference < 0){ //It is within a year if the month difference is a negative number
                inspectionDate.setText(monthName + " " + day);
            }
            else if(monthDifference == 0){
                int dayDifference = curDay - day;
                if(dayDifference < 0){
                    inspectionDate.setText(monthName + " " + day);
                }
                else{
                    inspectionDate.setText(monthName + " " + year);
                }
            }
            else{
                inspectionDate.setText(monthName + " " + year);
            }
        }
        //Third case - more than a year ago , second scenario
        if(curYear - year > 1){
            inspectionDate.setText(monthName + " " + year);
        }


        //Processing the hazard level so the appropriate hazard icon is assigned and a complementing background color
        String hazardLevel = inspection.getHazardLevel();
        ImageView hazardIcon = view.findViewById(R.id.hazardIcon);

        if(hazardLevel.equalsIgnoreCase("Low")){
            hazardIcon.setImageResource(R.drawable.low_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            //Setting background of the item along with tinting it such that the icon is more apparent
            view.setBackgroundColor(view.getResources().getColor(R.color.colorLowHazard));


        }
        else if(hazardLevel.equalsIgnoreCase("Moderate")){
            hazardIcon.setImageResource(R.drawable.moderate_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setBackgroundColor(view.getResources().getColor(R.color.colorModerateHazard));

        }
        else if(hazardLevel.equalsIgnoreCase("High")){
            hazardIcon.setImageResource(R.drawable.high_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setBackgroundColor(view.getResources().getColor(R.color.colorHighHazard));

        }


        //Set an onclick listener for the individual buttons such that the third activity can be launched with the necessary info
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //currently disabled, however, once the third activity is up and running re-enable to have the data
                //of the clicked inspection (passed as the Inspection itself) passed into the third activity (rename as needed)
                //passing index/position as requested
                /*
                Intent intent = InspectionDetails.getIntent(this, position);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                 */
                Log.i("Position_clicked", position + " ");
            }
        });

        return view;
    }
}

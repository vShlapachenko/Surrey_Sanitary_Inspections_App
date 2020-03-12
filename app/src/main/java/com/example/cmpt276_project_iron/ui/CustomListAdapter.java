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
import com.example.cmpt276_project_iron.model.DateConversionCalculator;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Restaurant;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.List;

/**
 * Custom adapter for the list of inspections for the second screen, required for formatting with multiple data in a single item
 */
public class CustomListAdapter extends ArrayAdapter<Inspection> {

    private Context context;
    private int resource;
    private List<Inspection> inspections;


    public CustomListAdapter(Context context, int resource, List<Inspection> inspections){
        super(context, resource, inspections);

        this.context = context;
        this.resource = resource;
        this.inspections = inspections;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.inspection_list_item, null);
        view.setClickable(true);


        Inspection inspection = inspections.get(position);

        TextView inspectionNum = view.findViewById(R.id.inspectionNum);
        inspectionNum.setText(Integer.toString(position + 1));

        TextView critIssues = view.findViewById(R.id.numCritIssues);
        critIssues.setText(String.valueOf(inspection.getNumCritical()));

        TextView nonCritIssues = view.findViewById(R.id.numNonCritIssues);
        nonCritIssues.setText(String.valueOf(inspection.getNumNonCritical()));


        TextView inspectionDate = view.findViewById(R.id.inspection_date);
        inspectionDate.setText(DateConversionCalculator.getFormattedDate(view.getContext(), inspection.getInspectionDate()));


        /**
         * Processing the hazard level so the appropriate hazard icon is assigned and a complementing background color
         */
        String hazardLevel = inspection.getHazardLevel();
        ImageView hazardIcon = view.findViewById(R.id.hazardIcon);

        view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorHazard));

        if(hazardLevel.equalsIgnoreCase("Low")){
            hazardIcon.setImageResource(R.drawable.low_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);

        }
        else if(hazardLevel.equalsIgnoreCase("Moderate")){
            hazardIcon.setImageResource(R.drawable.moderate_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);

        }
        else if(hazardLevel.equalsIgnoreCase("High")){
            hazardIcon.setImageResource(R.drawable.high_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);

        }


        /**
         * Set an onclick listener for the individual items such that the third activity can be launched with the necessary info.
         */
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = InspectionDetails.getIntent(v.getContext(), position, inspections.get(position).getTrackingNumber());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                Log.i("Position_clicked", position + " ");
            }
        });

        return view;
    }
}

package com.example.cmpt276_project_iron.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.DateConversionCalculator;
import com.example.cmpt276_project_iron.model.Inspection;

import java.util.List;

/**
 * Custom adapter for the list of inspections for the second screen, required for formatting with multiple data in a single item
 */
public class DetailsListAdapter extends RecyclerView.Adapter<DetailsListAdapter.ViewHolder> {
    private Context context;
    private List<Inspection> inspections;


    public DetailsListAdapter(Context context, List<Inspection> inspections) {
        this.context = context;
        this.inspections = inspections;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context)
                        .inflate(R.layout.inspection_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inspection inspection = inspections.get(position);

        holder.inspectionNum.setText(Integer.toString(position + 1));

        holder.critIssues.setText(String.valueOf(inspection.getNumCritical()));

        holder.nonCritIssues.setText(String.valueOf(inspection.getNumNonCritical()));

        holder.inspectionDate.setText(DateConversionCalculator.
                getFormattedDate(context, inspection.getInspectionDate()));

        //Processing the hazard level so the appropriate hazard icon is assigned and a complementing background color
        String hazardLevel = inspection.getHazardLevel();
        initializeHazardIcon(hazardLevel, holder.hazardIcon, holder.error_text);

        holder.parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorHazard));

        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = InspectionDetails.getIntent(v.getContext(), position, inspections.get(position).getTrackingNumber());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    private void initializeHazardIcon(String hazardLevel, ImageView hazardIcon, TextView errorText) {
        if(hazardLevel.equalsIgnoreCase("Low")){
            hazardIcon.setImageResource(R.drawable.low_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        } else if (hazardLevel.equalsIgnoreCase("Moderate")) {
            hazardIcon.setImageResource(R.drawable.moderate_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        } else if (hazardLevel.equalsIgnoreCase("High")) {
            hazardIcon.setImageResource(R.drawable.high_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        } else{
            hazardIcon.setImageResource(R.drawable.missing_info);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);

            //Providing context if a hazard level is not found
            errorText.setText(R.string.hazard_not_found_message);
        }
    }

    @Override
    public int getItemCount() {
        return inspections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView inspectionNum;
        private TextView critIssues;
        private TextView nonCritIssues;
        private TextView inspectionDate;
        private ImageView hazardIcon;
        private TextView error_text;
        private View parentView;


        public ViewHolder(@NonNull View parentView) {
            super(parentView);
            this.parentView = parentView;
            inspectionNum = parentView.findViewById(R.id.inspectionNum);
            critIssues = parentView.findViewById(R.id.numCritIssues);
            nonCritIssues = parentView.findViewById(R.id.numNonCritIssues);
            inspectionDate = parentView.findViewById(R.id.inspection_date);
            hazardIcon = parentView.findViewById(R.id.hazardIcon);
            error_text = parentView.findViewById(R.id.error_text);
        }
    }
}

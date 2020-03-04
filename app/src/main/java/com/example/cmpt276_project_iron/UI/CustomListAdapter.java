package com.example.cmpt276_project_iron.UI;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Inspection;

import org.w3c.dom.Text;

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
        counter++;

        //set number of critical issues
        TextView critIssues = view.findViewById(R.id.numCritIssues);
        critIssues.setText(inspection.getNumCritical());

        //set number of non-critical issues
        TextView nonCritIssues = view.findViewById(R.id.numNonCritIssues);
        nonCritIssues.setText(inspection.getNumNonCritical());

        //set the inspection's date
        TextView inspectionDate = view.findViewById(R.id.inspection_date);
        inspectionDate.setText(inspection.getInspectionDate());


        //Processing the hazard level so the appropriate hazard icon is assigned and a complementing background color
        String hazardLevel = inspection.getHazardLevel();
        ImageView hazardIcon = view.findViewById(R.id.hazardIcon);

        if(hazardLevel.equalsIgnoreCase("Low")){
            hazardIcon.setImageResource(R.drawable.low_hazard);
            //Setting background of the item along with tinting it such that the icon is more apparent
            view.setBackgroundColor(view.getResources().getColor(R.color.colorLowHazard));
            view.setBackgroundTintList(ColorStateList.valueOf(view.getResources().getColor(R.color.colorTint)));

        }
        else if(hazardLevel.equalsIgnoreCase("Moderate")){
            hazardIcon.setImageResource(R.drawable.moderate_hazard);
            view.setBackgroundColor(view.getResources().getColor(R.color.colorModerateHazard));
            view.setBackgroundTintList(ColorStateList.valueOf(view.getResources().getColor(R.color.colorTint)));
        }
        else if(hazardLevel.equalsIgnoreCase("High")){
            hazardIcon.setImageResource(R.drawable.high_hazard);
            view.setBackgroundColor(view.getResources().getColor(R.color.colorHighHazard));
            view.setBackgroundTintList(ColorStateList.valueOf(view.getResources().getColor(R.color.colorTint)));
        }

        //Set an onclick listener for the individual buttons such that the third activity can be launched with the necessary info
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CurImageView.ScaleType.CENTER_INSIDErently disabled, however, once the third activity is up and running re-enable to have the data
                //of the clicked inspection (passed as the Inspection itself) passed into the third activity (rename as needed)
                /*
                    InspectionDetails.getIntent(this, inspections.get(position));
                 */
            }
        });

        return view;
    }
}

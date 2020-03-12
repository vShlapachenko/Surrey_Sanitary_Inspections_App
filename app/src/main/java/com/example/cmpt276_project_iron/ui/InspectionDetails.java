package com.example.cmpt276_project_iron.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.DateConversionCalculator;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Violation;
/*activity that deals with the inspection details such as date, hazard level, critical
and non critical violations and has a list of violations
 */

public class InspectionDetails extends AppCompatActivity {
    private final String TAG = "InspectActivity";
    private Inspection restaurantInspection;

    private final int maxPermitViolationNum = 200;
    private final int maxTemperatureViolationNum = 300;
    private final int maxEquipmentViolationNum = 401;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);
        getInspectionIndex();

        ActionBar detailsBar = getSupportActionBar();
        detailsBar.setTitle("Inspection Details");
        setText();

        if(restaurantInspection.getNumCritical() > 0 || restaurantInspection.getNumNonCritical() > 0) {
            setViolationIcons();
            populateListView();
            setHazardIcons();
        }
        else if(restaurantInspection.getViolationList() == null){ // do later breaking thing right now
            noViolations();
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void setText() {

        TextView inspectionDate = findViewById(R.id.inspection_number);
        inspectionDate.setText(DateConversionCalculator.getFullFormattedDate(this, restaurantInspection.getInspectionDate()));

        TextView criticalIssues = findViewById(R.id.num_critical_issues);
        criticalIssues.setText(String.valueOf(restaurantInspection.getNumCritical())); // switch to actaul issues when thing is passed in

        TextView nonCriticalIssues = findViewById(R.id.num_non_critical_issues);
        nonCriticalIssues.setText(String.valueOf(restaurantInspection.getNumNonCritical())); // switch to actaul issues when thing is passed in

        TextView hazardLevel = findViewById(R.id.haz);
        hazardLevel.setText(String.valueOf(restaurantInspection.getHazardLevel())); // switch to actaul issues when thing is passed in
    }

    private void noViolations() {
        Toast.makeText(getApplicationContext(), "No Violations available", Toast.LENGTH_LONG).show();
    }

    private void setViolationIcons() {
        for(Violation cur : restaurantInspection.getViolationList()) {
            if(cur.getViolationNum() < maxPermitViolationNum) {
                cur.setIconId(R.drawable.permit);
            }
            else if(cur.getViolationNum() > maxPermitViolationNum && cur.getViolationNum() < maxTemperatureViolationNum) {
                cur.setIconId(R.drawable.thermometer);
            }
            else if(cur.getViolationNum() > maxTemperatureViolationNum && cur.getViolationNum() <= maxEquipmentViolationNum && !isPestViolation(cur)) {
                cur.setIconId(R.drawable.equipment);
            }
            else if(isSanitaryViolation(cur)) {
                cur.setIconId(R.drawable.handwash);
            }
            else if(isPestViolation(cur)) {
                cur.setIconId(R.drawable.pest);
            }
            else {
                cur.setIconId(R.drawable.permit);
            }

        }
    }

    private boolean isSanitaryViolation(Violation v) {
        if(v.getViolationNum() == 402 || v.getViolationNum() == 403 || v.getViolationNum() == 404) {
            return true;
        }
        else {
            return false;
        }
    }

    private void setHazardIcons() {
        for(Violation cur : restaurantInspection.getViolationList()) {
            if(cur.isCritical()) {
                cur.setHazIconId(R.drawable.high_hazard);
            }
            else {
                cur.setHazIconId(R.drawable.low_hazard);
            }
        }
    }
    private boolean isPestViolation(Violation v) {
        if(v.getViolationNum() == 304 || v.getViolationNum() == 313) {
            return true;
        }
        else {
            return false;
        }
    }

    private void populateListView() {
        ArrayAdapter<Violation> adapter = new ViolationAdapter();
        ListView vList = findViewById(R.id.listof_violations);
        vList.setAdapter(adapter);
    }
    private class ViolationAdapter extends ArrayAdapter<Violation> {
        private Context context = getContext();
        public ViolationAdapter() {
            super(InspectionDetails.this, R.layout.violation_item_list, restaurantInspection.getViolationList());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.violation_item_list, parent, false);
            }


            final Violation violation = restaurantInspection.getViolationList().get(position);
            ImageView vImage = itemView.findViewById(R.id.item_violation_image);
            vImage.setImageResource(violation.getIconId());

            String viol = "violation";
            int id = getResources().getIdentifier(viol + violation.getViolationNum(), "string", getPackageName());
            String setString = getString(id);

            TextView summary = itemView.findViewById(R.id.summary);

            summary.setText(setString);
            ImageView vImageHazard = itemView.findViewById(R.id.item_hazard);
            vImageHazard.setImageResource(violation.getHazIconId());

            if(violation.isCritical()) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorHazard));
            }
            else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorHazard));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), violation.getProblemDescription(), Toast.LENGTH_LONG).show();
                }
            });

            return itemView;
        }

    }

    public void getInspectionIndex(){
        int index = getIntent().getIntExtra("curInspection", 0);
        String tNumber = getIntent().getStringExtra("curInspectionTNumber");
        Manager manager = Manager.getInstance();
        restaurantInspection = manager.getInspectionMap().get(tNumber).get(index);

    }


    public static Intent getIntent(Context context, int index, String tNumber){
        Intent intent = new Intent(context, InspectionDetails.class);

        intent.putExtra("curInspection", index);
        intent.putExtra("curInspectionTNumber", tNumber);

        return intent;

    }
    private void displayCorrectLayout(){
        Display dimensions = getWindowManager().getDefaultDisplay();
        Point dimension = new Point();
        dimensions.getSize(dimension);
        int width = dimension.x;
        int height = dimension.y;
        /**
         * Android will automatically choose best layout in accordance to normal/large/xlarge (already custom xmls),
         * however, phones such as the Nexus S do not choose this correctly and therefore setting a special case
         */
        if(width == 480 && height == 800) {
            setContentView(R.layout.activity_restaurant_details_custom);
        }
        else if(width == 1440 && height == 2560) {
            setContentView(R.layout.activity_inspection_details_custom2);
        }
        else{
            setContentView(R.layout.activity_inspection_details);
        }
    }
}
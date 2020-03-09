package com.example.cmpt276_project_iron.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Calendar;
/*activity that deals with the inspection details such as date, hazard level, critical
and non critical violations and has a list of violations
 */

public class InspectionDetails extends AppCompatActivity {
    private final String TAG = "InspectActivity";
    private Inspection restaurantInspection;

    // index to find inspection in inspection manager must be passed in
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);
        getInspectionIndex();

        ActionBar detailsBar = getSupportActionBar();
        detailsBar.setTitle("Inspection Menu");


        TextView inspectionDate = findViewById(R.id.inspection_number);
        DateConversionCalculator date = new DateConversionCalculator();
        inspectionDate.setText(date.getFormattedDate(this, restaurantInspection.getInspectionDate()) + "  " + restaurantInspection.getInspectionDate().get(Calendar.YEAR));

        TextView criticalIssues = findViewById(R.id.num_critical_issues);
        criticalIssues.setText("" + restaurantInspection.getNumCritical()); // switch to actaul issues when thing is passed in

        TextView nonCriticalIssues = findViewById(R.id.num_nc);
        nonCriticalIssues.setText("" + restaurantInspection.getNumNonCritical()); // switch to actaul issues when thing is passed in

        TextView hazardLevel = findViewById(R.id.haz);
        hazardLevel.setText("" + restaurantInspection.getHazardLevel()); // switch to actaul issues when thing is passed in
        if(restaurantInspection.getNumCritical() > 0 || restaurantInspection.getNumNonCritical() > 0) {
            setViolationIcons();
            populateListView();
            setHazardIcons();
        }
//        else if(restaurantInspection.getViolationList() == null){ // do later breaking thing right now
//            noViolations();
//        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    private void noViolations() {
//        TextView summary = (TextView) findViewById(R.id.no_violations);
//        summary.setText(getResources().getString(R.string.no_violation_text));
//    }

    private void setViolationIcons() {
        for(Violation cur : restaurantInspection.getViolationList()) {
            if(cur.getViolationNum() < 200) {
                // set operational icon
                cur.setIconId(R.drawable.permit);
            }
            if(cur.getViolationNum() > 200 && cur.getViolationNum() < 300) {
                // set temperature icon
                cur.setIconId(R.drawable.thermometer);
            }
            if(cur.getViolationNum() > 300 && cur.getViolationNum() <= 315 && !isPestViolation(cur)) {
                // set equipment icon
                cur.setIconId(R.drawable.equipment);
            }
            if(isPestViolation(cur)) {
                cur.setIconId(R.drawable.pest);
            }

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
        if(v.getViolationNum() == 304 || v.getViolationNum() == 315) {
            return true;
        }
        else {
            return false;
        }
    }

    private void populateListView() {
        ArrayAdapter<Violation> adapter = new ViolationAdapter();
        ListView vList = (ListView) findViewById(R.id.listof_violations);
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

            // finding the violation
            Violation violation = restaurantInspection.getViolationList().get(position);
            // fill the view
            ImageView vImage = (ImageView)itemView.findViewById(R.id.item_violation_image);
            vImage.setImageResource(violation.getIconId());

            String viol = "violation";
            int id = getResources().getIdentifier(viol + violation.getViolationNum(), "string", getPackageName());
            String setString = getString(id);

            TextView summary = (TextView) itemView.findViewById(R.id.summary);
            summary.setText(setString); // switch to summary

            ImageView vImageHazard = (ImageView)itemView.findViewById(R.id.item_hazard);
            vImageHazard.setImageResource(violation.getHazIconId());

            if(violation.isCritical()) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorHighHazard));
            }
            else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLowHazard));
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
}
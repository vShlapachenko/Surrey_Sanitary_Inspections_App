package com.example.cmpt276_project_iron.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Violation;

import java.util.Calendar;


public class InspectionDetails extends AppCompatActivity {
    private final String TAG = "InspectActivity";
    private Inspection restaurantInspection;
    // index to find inspection in inspection manager must be passed in
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);
        getInspectionIndex();

        Log.e(TAG, "crit issues is " + restaurantInspection.getNumCritical());
        Log.e(TAG, "non crit issues is " + restaurantInspection.getNumNonCritical());

        TextView inspectionDate = findViewById(R.id.inspection_number);
        inspectionDate.setText("" + restaurantInspection.getInspectionDate().get(Calendar.YEAR)); // switch to date when inspection is passed in

        TextView criticalIssues = findViewById(R.id.num_critical_issues);
        criticalIssues.setText("" + restaurantInspection.getNumCritical()); // switch to actaul issues when thing is passed in

        TextView nonCriticalIssues = findViewById(R.id.num_nc);
        nonCriticalIssues.setText("" + restaurantInspection.getNumNonCritical()); // switch to actaul issues when thing is passed in

        TextView hazardLevel = findViewById(R.id.haz);
        hazardLevel.setText("" + restaurantInspection.getHazardLevel()); // switch to actaul issues when thing is passed in

        populateListView();
    }
    private void setViolationIcons() {

    }

    private void populateListView() {
        ArrayAdapter<Violation> adapter = new ViolationAdapter();
        ListView vList = (ListView) findViewById(R.id.listof_violations);
        vList.setAdapter(adapter);
    }
    private class ViolationAdapter extends ArrayAdapter<Violation> {
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
            ImageView vImage = (ImageView)itemView.findViewById(R.id.item_hazard);
            vImage.setImageResource(violation.getIconId());

            TextView summary = (TextView) itemView.findViewById(R.id.summary);
            summary.setText(violation.getProblemDescription()); // switch to summary

//            ImageView vImageHazard = (ImageView)itemView.findViewById(R.id.item_violation_image);
//            vImageHazard.setImageResource(violation.getIconId());




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
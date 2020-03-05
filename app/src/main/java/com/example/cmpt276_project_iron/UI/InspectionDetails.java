package com.example.cmpt276_project_iron.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Manager;


public class InspectionDetails extends AppCompatActivity {
    private Inspection restaurantInspection;
    // index to find inspection in inspection manager must be passed in
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);
        getInspectionIndex();

        TextView inspectionDate = findViewById(R.id.inspection_number);
        inspectionDate.setText("6"); // switch to date when inspection is passed in

        TextView criticalIssues = findViewById(R.id.num_critical_issues);
        criticalIssues.setText("7"); // switch to actaul issues when thing is passed in

        TextView nonCriticalIssues = findViewById(R.id.num_nc);
        nonCriticalIssues.setText("8"); // switch to actaul issues when thing is passed in
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
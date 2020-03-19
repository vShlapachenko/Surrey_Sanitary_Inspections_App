package com.example.cmpt276_project_iron.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.DateConversionCalculator;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Manager;
/*activity that deals with the inspection details such as date, hazard level, critical
and non critical violations and has a list of violations
 */

public class InspectionDetails extends AppCompatActivity {

    public static final String CUR_INSPECTION_KEY = "curInspection";
    public static final String CUR_INSPECTION_T_NUMBER_KEY = "curInspectionTNumber";
    private final int maxPermitViolationNum = 200;
    private final int maxTemperatureViolationNum = 300;
    private final int maxEquipmentViolationNum = 401;

    private Inspection restaurantInspection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);
        getInspectionIndex();

        setText();

        if(restaurantInspection.getNumCritical() > 0 || restaurantInspection.getNumNonCritical() > 0) {
            initRecyclerView();
        }
        else if(restaurantInspection.getViolationList() == null){
            noViolations();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.listof_violations);
        ViolationListAdapter adapter = new ViolationListAdapter(this, restaurantInspection);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(this, R.drawable.horizontal_divider_violations);
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);

    }

    private void setText() {

        TextView inspectionDate = findViewById(R.id.inspection_number);
        inspectionDate.setText(DateConversionCalculator.getFullFormattedDate(this, restaurantInspection.getInspectionDate()));

        TextView criticalIssues = findViewById(R.id.num_critical_issues);
        criticalIssues.setText(String.valueOf(restaurantInspection.getNumCritical()));

        TextView nonCriticalIssues = findViewById(R.id.num_non_critical_issues);
        nonCriticalIssues.setText(String.valueOf(restaurantInspection.getNumNonCritical())); // switch to actaul issues when thing is passed in

        TextView hazardLevel = findViewById(R.id.haz);
        hazardLevel.setText(restaurantInspection.getHazardLevel());
    }

    private void noViolations() {
        String noViolations = getResources().getString(R.string.no_violations_available);
        Toast.makeText(getApplicationContext(), noViolations, Toast.LENGTH_LONG).show();
    }


    public void getInspectionIndex(){
        int index = getIntent().getIntExtra(CUR_INSPECTION_KEY, 0);
        String tNumber = getIntent().getStringExtra(CUR_INSPECTION_T_NUMBER_KEY);
        Manager manager = Manager.getInstance();
        restaurantInspection = manager.getInspectionMap().get(tNumber).get(index);
    }


    public static Intent getIntent(Context context, int index, String tNumber){
        Intent intent = new Intent(context, InspectionDetails.class);
        intent.putExtra(CUR_INSPECTION_KEY, index);
        intent.putExtra(CUR_INSPECTION_T_NUMBER_KEY, tNumber);
        return intent;

    }

    private void displayCorrectLayout() {
        Display dimensions = getWindowManager().getDefaultDisplay();
        Point dimension = new Point();
        dimensions.getSize(dimension);
        int width = dimension.x;
        int height = dimension.y;
        /**
         * Android will automatically choose best layout in accordance to normal/large/xlarge (already custom xmls),
         * however, phones such as the Nexus S do not choose this correctly and therefore setting a special case
         */
        if (width == 480 && height == 800) {
            setContentView(R.layout.activity_restaurant_details_custom);
        } else if (width == 1440 && height == 2560) {
            setContentView(R.layout.activity_inspection_details_custom2);
        } else {
            setContentView(R.layout.activity_inspection_details);
        }
    }
}
package com.example.cmpt276_project_iron.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Inspection;

public class InspectionDetails extends AppCompatActivity {
    private Inspection restaurantInspection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);
    }
}

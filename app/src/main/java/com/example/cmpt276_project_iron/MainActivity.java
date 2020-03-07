package com.example.cmpt276_project_iron;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cmpt276_project_iron.ui.InspectionDetails;

public class MainActivity extends AppCompatActivity {
    private final int PICK_CONTACT_REQUEST = 8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivityForResult(InspectionDetails.getIntent(MainActivity.this, 0, "SWOD-AHZUMF"), PICK_CONTACT_REQUEST);
    }
}

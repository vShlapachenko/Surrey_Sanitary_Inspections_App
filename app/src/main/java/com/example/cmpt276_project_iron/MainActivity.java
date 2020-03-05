package com.example.cmpt276_project_iron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.cmpt276_project_iron.UI.InspectionDetails;
import com.example.cmpt276_project_iron.model.InspectionManager;
import com.example.cmpt276_project_iron.model.Restaurant;
import com.example.cmpt276_project_iron.model.RestaurantManager;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int PICK_CONTACT_REQUEST = 8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivityForResult(InspectionDetails.getIntent(MainActivity.this, 0), PICK_CONTACT_REQUEST);
    }
}

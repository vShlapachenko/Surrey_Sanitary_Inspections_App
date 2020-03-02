package com.example.cmpt276_project_iron;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.cmpt276_project_iron.model.Restaurant;
import com.example.cmpt276_project_iron.model.RestaurantManager;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RestaurantManager manager = RestaurantManager.getInstance();
    }
}

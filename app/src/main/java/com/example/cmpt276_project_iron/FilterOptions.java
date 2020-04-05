package com.example.cmpt276_project_iron;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.FilterSettings;
import com.example.cmpt276_project_iron.ui.RestaurantDetails;

public class FilterOptions extends AppCompatActivity {
    RadioGroup hazardGroup;
    RadioGroup favouriteGroup;
    RadioButton hazardButton;
    RadioButton favouriteButton;
    RadioGroup rangeGroup;
    RadioButton rangeButton;
    FilterSettings settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_options);

        hazardButtons();
        favouriteButton();
        getRange();
        settings = FilterSettings.getInstance();

        Button apply = findViewById(R.id.save);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hazId = hazardGroup.getCheckedRadioButtonId();

                hazardButton = findViewById(hazId);

                settings.setHazLevel(hazardButton.getText().toString());

                Log.e("Input", "" + hazardButton.getText());

                int favId = favouriteGroup.getCheckedRadioButtonId();
                favouriteButton = findViewById(favId);

                setFavourite(favouriteButton);

                int rangeId = rangeGroup.getCheckedRadioButtonId();
                rangeButton = findViewById(rangeId);

                setRange(rangeButton);

                int criticalIssuesSelected = getCriticalIssuesNumber();

                setCriticalIssues(criticalIssuesSelected);

                Log.e("Input", "" + favouriteButton.getText());
                Log.e("Input", "" + rangeButton.getText());
                Log.e("edit text is ", "" + criticalIssuesSelected);
            }
        });

    }

    private void setCriticalIssues(int criticalIssuesSelected) {
        settings.setInput(criticalIssuesSelected);
    }

    private void setRange(RadioButton rangeButton) {
        if(rangeButton.getText().toString().equals("Greater then or equal to critical issues")) {
            settings.setGreaterThenInput(true);
            settings.setLowerThenInput(false);
        }
        else if(rangeButton.getText().toString().equals("Less then or equal to critical issues")) {
            settings.setGreaterThenInput(false);
            settings.setLowerThenInput(true);
        }
    }

    private void setFavourite(RadioButton favouriteButton) {
        if(favouriteButton.getText().toString().equals("Favourites")) {
            settings.setFavourite(true);
        }
        else if(favouriteButton.getText().toString().equals("No favourites")) {
            settings.setFavourite(false);
        }
    }

    private int getCriticalIssuesNumber() {
        EditText criticalIssues = findViewById(R.id.get_crit_issue_num);

        return Integer.parseInt(criticalIssues.getText().toString());

    }

    public void findRange(View v) {
        int radioId = rangeGroup.getCheckedRadioButtonId();

        rangeButton = findViewById(radioId);
    }

    private void hazardButtons() {
        hazardGroup = findViewById(R.id.hazardGroup);

    }
    private void favouriteButton() {
        favouriteGroup = findViewById(R.id.favouriteGroup);
    }

    public void getInputHazard(View v) {
        int radioId = hazardGroup.getCheckedRadioButtonId();

        hazardButton = findViewById(radioId);
//        Log.e("Input", "" + radioButton.getText());
    }
    public void getInputFavourite(View v) {
        int radioId = favouriteGroup.getCheckedRadioButtonId();

        favouriteButton = findViewById(radioId);
//        Log.e("Input", "" + radioButton.getText());
    }
    private void getRange() {
        rangeGroup = findViewById(R.id.range);
    }
    public static Intent getIntent(Context context){
        Intent intent = new Intent(context, FilterOptions.class);
        return intent;
    }
}

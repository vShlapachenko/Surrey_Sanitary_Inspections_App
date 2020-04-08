package com.example.cmpt276_project_iron;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.FileObserver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.FilterSettings;
import com.example.cmpt276_project_iron.ui.RestaurantDetails;
import com.example.cmpt276_project_iron.ui.RestaurantList;

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

        settings = FilterSettings.getInstance(this);

        EditText criticalIssues = findViewById(R.id.get_crit_issue_num);
        criticalIssues.setTextColor(Color.WHITE);

        hazardButtons();
        favouriteButton();
        getRange();

        Button apply = findViewById(R.id.save);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hazId = hazardGroup.getCheckedRadioButtonId();

                hazardButton = findViewById(hazId);

                if(hazardButton != null) {

                    settings.setHazLevel(hazardButton.getText().toString());
                }

                int favId = favouriteGroup.getCheckedRadioButtonId();
                favouriteButton = findViewById(favId);

                if(favouriteButton != null) {
                    setFavourite(favouriteButton);
                }

                int rangeId = rangeGroup.getCheckedRadioButtonId();
                rangeButton = findViewById(rangeId);

                if(rangeButton != null) {
                    setRange(rangeButton);
                }

                int criticalIssuesSelected = getCriticalIssuesNumber();

                Log.e("issues", criticalIssuesSelected + "");
                setCriticalIssues(criticalIssuesSelected);

                settings.setHasBeenFiltered(true);

                Intent I = new Intent(v.getContext(), RestaurantList.class);
                startActivity(I);
                finish();

            }
        });

    }

    private void setCriticalIssues(int criticalIssuesSelected) {
        settings.setCriticalIssues(criticalIssuesSelected);
    }

    private void setRange(RadioButton rangeButton) {
        if(rangeButton.getText().toString().equals("Greater then or equal to critical issues")) {
            settings.setGreaterThenInput(true);
            settings.setLowerThenInput(false);
        } else if(rangeButton.getText().toString().equals("Less then or equal to critical issues")) {
            settings.setGreaterThenInput(false);
            settings.setLowerThenInput(true);
        }
    }

    private void setFavourite(RadioButton favouriteButton) {
        if(favouriteButton.getText().toString().equals("Favourites")) {
            settings.setFavourite(true);
        } else if(favouriteButton.getText().toString().equals("No favourites")) {
            settings.setFavourite(false);
        }
    }

    private int getCriticalIssuesNumber() {
        EditText criticalIssues = findViewById(R.id.get_crit_issue_num);

        if(criticalIssues.getText().toString().equals("")) {
            return settings.getCriticalIssues();
        } else {
            return Integer.parseInt(criticalIssues.getText().toString());
        }

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

    }
    public void getInputFavourite(View v) {
        int radioId = favouriteGroup.getCheckedRadioButtonId();

        favouriteButton = findViewById(radioId);
    }
    private void getRange() {
        rangeGroup = findViewById(R.id.range);
    }
    public static Intent getIntent(Context context){
        Intent intent = new Intent(context, FilterOptions.class);
        return intent;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("back", "button");
        Intent I = new Intent(this, RestaurantList.class);
        startActivity(I);
        finish();
    }
}

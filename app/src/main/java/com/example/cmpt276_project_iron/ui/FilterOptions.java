package com.example.cmpt276_project_iron.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.FilterSettings;
import com.example.cmpt276_project_iron.model.Manager;

public class FilterOptions extends AppCompatActivity {
    RadioGroup hazardGroup;
    RadioGroup favouriteGroup;
    RadioButton hazardButton;
    RadioButton favouriteButton;
    RadioGroup rangeGroup;
    RadioButton rangeButton;
    RadioGroup defaultGroup;
    RadioButton defaultButton;

    Manager manager;
    FilterSettings settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Selects the correct custom layout if the dimensions match
        displayCorrectLayout();

        settings = FilterSettings.getInstance(this);
        manager = Manager.getInstance(this);

        EditText criticalIssues = findViewById(R.id.get_crit_issue_num);
        criticalIssues.setTextColor(Color.WHITE);

        hazardButtons();
        favouriteButton();
        getRange();
        defualtSetter();

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

                setCriticalIssues(criticalIssuesSelected);

                if(defaultButton == null) {
                    settings.setHasBeenFiltered(true);
                }
                else {  // resetting to default case
                    settings.setHasBeenFiltered(false);
                    settings.setCriticalIssues(-1);
                    settings.setLowerThenInput(false);
                    settings.setGreaterThenInput(false);
                    settings.setFavourite(false);
                    settings.setHazLevel("all");
                    settings.setFilteredRestaurants(manager.getRestaurantList());
                }

                Intent I = new Intent(v.getContext(), RestaurantList.class);
                startActivity(I);
                finish();

            }
        });

    }

    private void defualtSetter() {
        defaultGroup = findViewById(R.id.default_group);
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
    public void getDefault(View v) {
        int radioId = defaultGroup.getCheckedRadioButtonId();

        defaultButton = findViewById(radioId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent I = new Intent(this, RestaurantList.class);
        startActivity(I);
        finish();
    }

    private void displayCorrectLayout(){
        Display dimensions = getWindowManager().getDefaultDisplay();
        Point dimension = new Point();
        dimensions.getSize(dimension);
        int width = dimension.x;
        int height = dimension.y;
        float density = getResources().getDisplayMetrics().density;

        //Checking if it's not a MDPI type screen, used to distinguish between same resolution phones that are of different sizes
        double MDPI_SCREEN_SIZE = 1.0;
        if(width == 480 && height == 800 && density != MDPI_SCREEN_SIZE) {
            setContentView(R.layout.activity_filter_options_custom); //NEXUS S Specific
        } else{
                setContentView(R.layout.activity_filter_options);
        }

    }
}

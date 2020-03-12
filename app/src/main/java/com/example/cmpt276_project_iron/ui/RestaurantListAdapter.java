package com.example.cmpt276_project_iron.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.DateConversionCalculator;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Restaurant;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 *  List adapter used for the restaurant list activity
 */
public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {
    private Context context;
    private int resource;
    private List<Restaurant> restaurants;
    private Manager manager;


    public RestaurantListAdapter(Context context, int resource, List<Restaurant> restaurants){
        super(context, resource, restaurants);

        this.context = context;
        this.resource = resource;
        this.restaurants = restaurants;

        this.manager = Manager.getInstance();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        final View view = inflater.inflate(R.layout.restaurant_list_item, null);
        view.setClickable(true);

        Restaurant restaurant = restaurants.get(position);

        TextView restaurantName = view.findViewById(R.id.restaurantName);
        restaurantName.setText(String.valueOf(restaurant.getName()));

        if (manager.getInspectionMap().get(restaurant.getTrackingNumber()) == null) {
            TextView critIssues = view.findViewById(R.id.numCritIssues);
            critIssues.setText(getContext().getString(R.string.not_applicable_text));

            TextView nonCritIssues = view.findViewById(R.id.numNonCritIssues);
            nonCritIssues.setText(getContext().getString(R.string.not_applicable_text));

            TextView inspectionDate = view.findViewById(R.id.inspectionDate);
            inspectionDate.setText(getContext().getString(R.string.not_applicable_text));

            ImageView restaurantIcon = view.findViewById(R.id.restaurantIcon);
            restaurantIcon.setImageResource(R.drawable.restaurant_icon);
            restaurantIcon.setScaleType(ImageView.ScaleType.FIT_XY);

            ImageView hazardIcon = view.findViewById(R.id.hazardIcon);
            hazardIcon.setVisibility(View.INVISIBLE);

        }
        else {
            Inspection recentInspection = manager.getInspectionMap().get(restaurant.getTrackingNumber()).get(0);

            TextView critIssues = view.findViewById(R.id.numCritIssues);
            critIssues.setText(String.valueOf(recentInspection.getNumCritical()));

            TextView nonCritIssues = view.findViewById(R.id.numNonCritIssues);
            nonCritIssues.setText(String.valueOf(recentInspection.getNumNonCritical()));

            TextView inspectionDate = view.findViewById(R.id.inspectionDate);
            inspectionDate.setText(DateConversionCalculator.getFormattedDate(view.getContext(), recentInspection.getInspectionDate()));

            ImageView restaurantIcon = view.findViewById(R.id.restaurantIcon);
            restaurantIcon.setImageResource(R.drawable.restaurant_icon);
            restaurantIcon.setScaleType(ImageView.ScaleType.FIT_XY);

            /**
             * Processing the hazard level so the appropriate hazard icon is assigned and a complementing background color
             */
            String hazardLevel = recentInspection.getHazardLevel();
            ImageView hazardIcon = view.findViewById(R.id.hazardIcon);

            if(hazardLevel.equalsIgnoreCase("Low")){
                hazardIcon.setImageResource(R.drawable.low_hazard);
                hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            else if(hazardLevel.equalsIgnoreCase("Moderate")){
                hazardIcon.setImageResource(R.drawable.moderate_hazard);
                hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            else if(hazardLevel.equalsIgnoreCase("High")){
                hazardIcon.setImageResource(R.drawable.high_hazard);
                hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
            }

        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.isVerticalScrollBarEnabled() == false) {
                    Intent intent = new Intent(context, RestaurantDetails.class);
                    intent.putExtra("restaurantIndex", position);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    //Using shared preferences to retain the index of the restaurant which is used to address activity two's
                    //bug with a changing toolbar tittle - Jas

                    SharedPreferences data = context.getSharedPreferences("data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = data.edit();
                    editor.putInt("cur_restaurant", position);
                    editor.apply();



                    Log.i("Position_clicked", position + " ");
                }
            }
        });

        return view;
    }
}

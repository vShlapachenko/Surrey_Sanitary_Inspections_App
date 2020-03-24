package com.example.cmpt276_project_iron.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder> {
    private Context context;
    private List<Restaurant> restaurants;
    private Manager manager;

    public RestaurantListAdapter(Context context, List<Restaurant> restaurants){
        this.context = context;
        this.restaurants = restaurants;
        this.manager = Manager.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context)
                        .inflate(R.layout.restaurant_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);

        holder.restaurantName.setText(String.valueOf(restaurant.getName()));

        if (manager.getInspectionMap().get(restaurant.getTrackingNumber()) == null) {
            initializeLayoutNoInspection(holder);
        }
        else {
            initializeLayoutWithInspection(restaurant, holder);
        }

        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
    }

    private void initializeLayoutNoInspection(ViewHolder viewHolder) {
        viewHolder.critIssues.setText(context.getString(R.string.not_applicable_text));

        viewHolder.nonCritIssues.setText(context.getString(R.string.not_applicable_text));

        viewHolder.inspectionDate.setText(context.getString(R.string.not_applicable_text));

        viewHolder.restaurantIcon.setImageResource(R.drawable.restaurant_icon);
        viewHolder.restaurantIcon.setScaleType(ImageView.ScaleType.FIT_XY);

        viewHolder.hazardIcon.setVisibility(View.INVISIBLE);
    }

    private void initializeLayoutWithInspection(Restaurant restaurant, ViewHolder viewHolder) {
        Inspection recentInspection = manager.getInspectionMap().get(restaurant.getTrackingNumber()).get(0);

        viewHolder.critIssues.setText(String.valueOf(recentInspection.getNumCritical()));

        viewHolder.nonCritIssues.setText(String.valueOf(recentInspection.getNumNonCritical()));

        viewHolder.inspectionDate.setText(DateConversionCalculator.
                getFormattedDate(context, recentInspection.getInspectionDate()));

        viewHolder.restaurantIcon.setImageResource(R.drawable.restaurant_icon);
        viewHolder.restaurantIcon.setScaleType(ImageView.ScaleType.FIT_XY);

        initializeHazardIcon(recentInspection.getHazardLevel(), viewHolder.hazardIcon);
    }

    private void initializeHazardIcon(String hazardLevel, ImageView hazardIcon) {
        if (hazardLevel.equalsIgnoreCase("Low")) {
            hazardIcon.setImageResource(R.drawable.low_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        } else if (hazardLevel.equalsIgnoreCase("Moderate")) {
            hazardIcon.setImageResource(R.drawable.moderate_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        } else if (hazardLevel.equalsIgnoreCase("High")) {
            hazardIcon.setImageResource(R.drawable.high_hazard);
            hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView restaurantName;
        private TextView critIssues;
        private TextView nonCritIssues;
        private TextView inspectionDate;
        private ImageView restaurantIcon;
        private ImageView hazardIcon;
        private View parentView;

        public ViewHolder(@NonNull View view) {
            super(view);
            parentView = view;
            restaurantName = view.findViewById(R.id.restaurantName);
            critIssues = view.findViewById(R.id.numCritIssues);
            nonCritIssues = view.findViewById(R.id.numNonCritIssues);
            inspectionDate = view.findViewById(R.id.inspectionDate);
            restaurantIcon = view.findViewById(R.id.restaurantIcon);
            hazardIcon = view.findViewById(R.id.hazardIcon);
        }
    }
}

package com.example.cmpt276_project_iron.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmpt276_project_iron.FilterOptions;
import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.DateConversionCalculator;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 *  List adapter used for the restaurant list activity
 */
public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Restaurant> restaurants; //Data set list
    private List<Restaurant> completeRestaurants; //Duplicate data set list for filtering
    private Manager manager;

    public RestaurantListAdapter(Context context, List<Restaurant> restaurants){
        this.context = context;
        this.restaurants = restaurants;
        this.completeRestaurants = new ArrayList<>(restaurants);  //Copy of list for maintaing data while filtering
        this.manager = Manager.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.restaurant_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);

        holder.restaurantName.setText(String.valueOf(restaurant.getName()));

        if ((manager.getInspectionMap().get(restaurant.getTrackingNumber())) == null) {
            initializeLayoutNoInspection(restaurant, holder);
        } else {
            initializeLayoutWithInspection(restaurant, holder);
        }

        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = RestaurantList.getIntent(context, position);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);


                SharedPreferences data = context.getSharedPreferences("data", MODE_PRIVATE);
                SharedPreferences.Editor editor = data.edit();
                editor.putInt("cur_restaurant", position);
                editor.apply();

                Log.i("Position_clicked", position + " ");
            }
        });
    }

    private void initializeLayoutNoInspection(Restaurant restaurant, ViewHolder viewHolder) {
        viewHolder.critIssues.setText(context.getString(R.string.not_applicable_text));

        viewHolder.nonCritIssues.setText(context.getString(R.string.not_applicable_text));

        viewHolder.inspectionDate.setText(context.getString(R.string.not_applicable_text));

        initializeRestaurantIconImage(restaurant, viewHolder.restaurantIcon);
        viewHolder.restaurantIcon.setScaleType(ImageView.ScaleType.FIT_XY);

        viewHolder.hazardIcon.setVisibility(View.INVISIBLE);
    }

    private void initializeLayoutWithInspection(Restaurant restaurant, ViewHolder viewHolder) {
        Inspection recentInspection = manager.getInspectionMap().get(restaurant.getTrackingNumber()).get(0);

        viewHolder.critIssues.setText(String.valueOf(recentInspection.getNumCritical()));

        viewHolder.nonCritIssues.setText(String.valueOf(recentInspection.getNumNonCritical()));

        viewHolder.inspectionDate.setText(DateConversionCalculator.
                getFormattedDate(context, recentInspection.getInspectionDate()));

        initializeRestaurantIconImage(restaurant, viewHolder.restaurantIcon);
        viewHolder.restaurantIcon.setScaleType(ImageView.ScaleType.FIT_XY);

        initializeHazardIcon(recentInspection.getHazardLevel(), viewHolder.hazardIcon);

    }


    private void initializeRestaurantIconImage(Restaurant restaurant, ImageView restaurantIcon) {
        String[] resNameArray = context.getResources().getStringArray(R.array.custom_icon_restaurants);
        boolean customImageApplied = false;
        for (String resName : resNameArray) {
            if (restaurant.getName().contains(resName)) {
                assignCustomRestaurantIcon(resName, restaurantIcon);
                customImageApplied = true;
            }
        }
        if (!customImageApplied) {
            restaurantIcon.setImageResource(R.drawable.animated_restaurant_icon);
        }
    }

    private void assignCustomRestaurantIcon(String resName, ImageView restaurantIcon) {
        if (resName.contains("Blenz")) {
            restaurantIcon.setImageResource(R.drawable.blenz);
        } else if (resName.contains("Freshii")) {
            restaurantIcon.setImageResource(R.drawable.freshii);
        } else if (resName.contains("Freshslice")) {
            restaurantIcon.setImageResource(R.drawable.freshslice);
        } else if (resName.contains("McDonald")) {
            restaurantIcon.setImageResource(R.drawable.mcdonalds);
        } else if (resName.contains("Panago")) {
            restaurantIcon.setImageResource(R.drawable.panago);
        } else if (resName.contains("Starbucks")) {
            restaurantIcon.setImageResource(R.drawable.starbucks);
        } else if (resName.contains("Subway")) {
            restaurantIcon.setImageResource(R.drawable.subway);
        } else if (resName.contains("Tim Hortons")) {
            restaurantIcon.setImageResource(R.drawable.timbos);
        } else if (resName.contains("KFC")) {
            restaurantIcon.setImageResource(R.drawable.kfc);
        } else if (resName.contains("Booster")) {
            restaurantIcon.setImageResource(R.drawable.boosterjuice);
        }
    }

    private void initializeHazardIcon(String hazardLevel, ImageView hazardIcon) {
        hazardIcon.setVisibility(View.VISIBLE);
        if (hazardLevel.equalsIgnoreCase("Low")) {
            hazardIcon.setImageResource(R.drawable.low_hazard);
        } else if (hazardLevel.equalsIgnoreCase("Moderate")) {
            hazardIcon.setImageResource(R.drawable.moderate_hazard);
        } else if (hazardLevel.equalsIgnoreCase("High")) {
            hazardIcon.setImageResource(R.drawable.high_hazard);
        } else {
            hazardIcon.setImageResource(R.drawable.missing_info);
        }
        hazardIcon.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }


    @Override //Filter for filtering the list of restaurants in real time
    public Filter getFilter() {
        return restaurantFilter;
    }

    private Filter restaurantFilter = new Filter() {
        @Override
        //performFiltering will perform filtering in the background therefore no delay
        protected FilterResults performFiltering(CharSequence constraint) {
            //constraint argument is used to define the filter logic
            List<Restaurant> filteredRestaurantList = new ArrayList<>();

            //If the specified filter (constraint, based on search bar) is empty then we want to show the full
            //set of results
            if (constraint == null || constraint.length() == 0){
               filteredRestaurantList.addAll(completeRestaurants);
            }
            //In the other case, if there was a specification made by the user
            else{
                //filter specification == user's filter specification
                String filterSpecification = constraint.toString().toLowerCase().trim();

                //Iterate through our complete restaurant list to check which ones meet this specification
                //Current filter: based on restaurant name in regards to .contains()
                for(Restaurant restaurant : completeRestaurants){
                    /**
                     * ADD ANY OTHER NECESSARY FILTERS HERE, HOWEVER, NEED TO KNOW WHAT OPTIONS WERE TOGGLED
                     * USE ->> SHARED PREFERENCES
                     *
                     */
                    if(restaurant.getName().toLowerCase().contains(filterSpecification)){
                        //If the restaurant name contains the specified filter text then add it
                        //to the list of filtered restaurants
                        filteredRestaurantList.add(restaurant);
                    }
                }
            }

            FilterResults filteredResults = new FilterResults();
            filteredResults.values = filteredRestaurantList;
            return filteredResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //Remove the contents of the list of restaurants in order to replace with the filtered ones
            restaurants.clear();

            //Add the filtered results to the list that will be adapted
            restaurants.addAll((List) results.values);
            //Once the data has changed, it must be relayed, so the adapter is notified of this change
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView restaurantName;
        private TextView critIssues;
        private TextView nonCritIssues;
        private TextView inspectionDate;
        private ImageView restaurantIcon;
        private ImageView hazardIcon;
        private View parentView;

        public ViewHolder(@NonNull View parentView) {
            super(parentView);
            this.parentView = parentView;
            restaurantName = parentView.findViewById(R.id.restaurantName);
            critIssues = parentView.findViewById(R.id.numCritIssues);
            nonCritIssues = parentView.findViewById(R.id.numNonCritIssues);
            inspectionDate = parentView.findViewById(R.id.inspectionDate);
            restaurantIcon = parentView.findViewById(R.id.restaurantIcon);
            hazardIcon = parentView.findViewById(R.id.hazardIcon);
        }
    }
}

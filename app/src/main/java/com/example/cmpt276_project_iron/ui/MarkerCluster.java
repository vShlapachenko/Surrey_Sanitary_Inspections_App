package com.example.cmpt276_project_iron.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Restaurant;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MarkerCluster extends DefaultClusterRenderer<RestaurantMarkerCluster> {   // 1
    private static final int MARKER_DIMENSION = 48;  // 2
    private final IconGenerator iconGenerator;
    private final ImageView markerImageView;


    public MarkerCluster(Context context, GoogleMap map, ClusterManager<RestaurantMarkerCluster> clusterManager) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context);  // 3
        markerImageView = new ImageView(context);
        markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION));
        iconGenerator.setContentView(markerImageView);  // 4
    }

    @Override
    protected void onBeforeClusterItemRendered(RestaurantMarkerCluster item, MarkerOptions markerOptions) {
        if(!(item.getManager().getInspectionMap().get(item.getRestaurant().getTrackingNumber()) == null)) {
            Inspection mostRecentInspection = item.getManager().getInspectionMap().get(item.getRestaurant().getTrackingNumber()).get(0);
            if (mostRecentInspection.getHazardLevel().equalsIgnoreCase("Low")) {
                markerImageView.setImageResource(R.drawable.low_hazard);
            } else if (mostRecentInspection.getHazardLevel().equalsIgnoreCase("Moderate")) {
                markerImageView.setImageResource(R.drawable.moderate_hazard);
            } else if (mostRecentInspection.getHazardLevel().equalsIgnoreCase("High")) {
                markerImageView.setImageResource(R.drawable.high_hazard);
            } else {
                markerImageView.setImageResource(R.drawable.not_found);
            }
        }
        else {
            markerImageView.setImageResource(R.drawable.not_found);
        }


        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        markerOptions.title(item.getTitle());
    }
}


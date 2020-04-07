package com.example.cmpt276_project_iron.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.widget.EditText;

import com.example.cmpt276_project_iron.R;
import com.example.cmpt276_project_iron.model.FilterSettings;
import com.example.cmpt276_project_iron.model.Inspection;
import com.example.cmpt276_project_iron.model.Manager;
import com.example.cmpt276_project_iron.model.Restaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


//Note: Depending on from where this activity is launched, pass data accordingly from which extra
//features will be deployed
//Parameters are passed via the .add method casted upon the fragment AND/OR the newInstance method

//Note: Location needs to be tested on a real phone <-------

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener, LocationSource, ClusterManager.OnClusterClickListener<RestaurantMarkerCluster>, ClusterManager.OnClusterItemClickListener<RestaurantMarkerCluster>, ClusterManager.OnClusterItemInfoWindowClickListener<RestaurantMarkerCluster> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LAT = "lat";
    private static final String LONG = "long";
    private static final String COORDLAUNCH = "coordinate_launch";
    private static final String RINDEX = "restaurant_index";
    private EditText searchText;

    //By default the recentering is enabled, however, once an action such as a forceful movement of the screen
    //is imposed, then will be set to false until the map recenter button is clicked
    private boolean recenterEnabled = true;
    //private EditText mSearchText;

    // TODO: Rename and change types of parameters

    @Override
    public void onClusterItemInfoWindowClick(RestaurantMarkerCluster restaurantMarkerCluster) {
        Log.e("Cluster", "Clicked at top code!");
        int index = restaurantMarkerCluster.getId();

        Intent gotoRestaurant = RestaurantDetails.getIntent(getContext(), index);

        startActivity(gotoRestaurant);
    }

    private double inLAT = 0.0;
    private double inLONG = 0.0;
    private boolean coordLaunch = false;
    private int restaurantIndex = 0;

    private boolean permissionsGrantedFlag = false;

    private FusedLocationProviderClient locationProvider;
    private LocationRequest locationRequest;
    private GoogleMap map;
    private LocationSource.OnLocationChangedListener gpsChangeListener;
    private LocationManager locationManger;

    private SupportMapFragment mapFragment;
    private OnFragmentInteractionListener mListener;

    //Retains all markers such that no data is lost
    private List<RestaurantMarkerCluster> markersFull;
    private List<RestaurantMarkerCluster> markers = new ArrayList<>();
    private ClusterManager<RestaurantMarkerCluster> clusterManager;

    private static final float ZOOM_AMNT = 17f;

    private final String TAG = "Maps";
    private Manager manager;
    Dialog popUp;
    Marker coordinateClickedMarker;
    private FilterSettings settings;

    public MapFragment() {
        // Required empty public constructor
    }

    private void coordinateTappedByUser(boolean coordLaunch) {
        if(coordLaunch) {
            Restaurant restaurantSelected = manager.getRestaurantList().get(restaurantIndex);
            LatLng latLng = new LatLng(restaurantSelected.getLatitude(), restaurantSelected.getLongitude());
            coordinateClickedMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(restaurantSelected.getName())
                    .snippet(restaurantSelected.getPhysicalAddress() + ", " + "No Hazard level")
            );
            if((manager.getInspectionMap().get(restaurantSelected.getTrackingNumber()) != null)) {
                Inspection mostRecentInspection = manager.getInspectionMap().get(restaurantSelected.getTrackingNumber()).get(0);

                if (mostRecentInspection.getHazardLevel().equalsIgnoreCase("Low")) {
                    coordinateClickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    coordinateClickedMarker.setSnippet(restaurantSelected.getPhysicalAddress() + ", " + mostRecentInspection.getHazardLevel());
                } else if (mostRecentInspection.getHazardLevel().equalsIgnoreCase("Moderate")) {
                    coordinateClickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    coordinateClickedMarker.setSnippet(restaurantSelected.getPhysicalAddress() + ", " + mostRecentInspection.getHazardLevel());
                } else if (mostRecentInspection.getHazardLevel().equalsIgnoreCase("High")) {
                    coordinateClickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    coordinateClickedMarker.setSnippet(restaurantSelected.getPhysicalAddress() + ", " + mostRecentInspection.getHazardLevel());
                } else {
                    coordinateClickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    coordinateClickedMarker.setSnippet(restaurantSelected.getPhysicalAddress() + ", " + mostRecentInspection.getHazardLevel());
                }
            } else {
                coordinateClickedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }

            coordinateClickedMarker.setTag(restaurantIndex);

            coordinateClickedMarker.showInfoWindow();

        }
    }

    private void makeMarkerTextClickable() {
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker arg0) {
                arg0.remove();
                Intent gotoRestaurant = RestaurantDetails.getIntent(getContext(), restaurantIndex);
                startActivity(gotoRestaurant);
                coordinateClickedMarker.remove();
            }
        });
    }

    //Used if incoming from coords click
    public static MapFragment newInstance(double latitude, double longitude, boolean coordinateFlag,
                                          int restaurantIndex) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putDouble(LAT, latitude);
        args.putDouble(LONG, longitude);
        args.putBoolean(COORDLAUNCH, coordinateFlag);
        args.putInt(RINDEX, restaurantIndex);
        fragment.setArguments(args);
        return fragment;
    }

    //Used for first screen / regular switch
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Indicates to the fragment that a toolbar is in effect (used so that searches can be made
        //for the map)
        setHasOptionsMenu(true);
        //Prevents orientation in fragments
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Explicitly asks the user for permission for the required services
        getRequiredPermissions();

        //Tracks the user's location
        updateGPSPosition();

        if (getArguments() != null) {
            inLAT = getArguments().getDouble(LAT);
            inLONG = getArguments().getDouble(LONG);
            coordLaunch = getArguments().getBoolean(COORDLAUNCH);
            restaurantIndex = getArguments().getInt(RINDEX);
        }

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

    }

    private void setUpClusterManager(GoogleMap googleMap){
        clusterManager = new ClusterManager<RestaurantMarkerCluster>(getContext(), googleMap);
        clusterManager.setRenderer(new MarkerCluster(getContext(), googleMap, clusterManager));
        googleMap.setOnCameraIdleListener(clusterManager);
        clusterManager.addItems(markers);
        clusterManager.cluster();

        googleMap.setOnMarkerClickListener(clusterManager);
        googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        googleMap.setOnInfoWindowClickListener(clusterManager);
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);
    }


    @Override
    public boolean onClusterClick(Cluster<RestaurantMarkerCluster> cluster) {

        if (cluster == null) {return false;}
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (RestaurantMarkerCluster restaurant : cluster.getItems())
            builder.include(restaurant.getPosition());
        LatLngBounds bounds = builder.build();
        try {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public boolean onClusterItemClick(RestaurantMarkerCluster restaurantMarkerCluster) {
        Log.e("Cluster", "Clicked!");
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        //Getting a map into the fragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        //It could be possible that it could be null, in that case create it
        if (mapFragment == null) {
            FragmentManager manager = getFragmentManager();
            FragmentTransaction fragmentT = manager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentT.replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {

        //Not inflated in fragment as it retains the life cycle of its parent which already
        //inflates;
        super.onPrepareOptionsMenu(menu);

        //Just getting access to the options menu to process the input for the maps
        MenuItem searchItem = menu.findItem(R.id.filter_search);
        EditText searchView = (EditText) searchItem.getActionView();

        //Prevents automatic lock-on on search bar
        searchView.setFocusable(true);

        //Will ensure that the keyboard is closed on enter
        searchView.setSingleLine();
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        //Listener for the text change in the search bar
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing as programmed to procession actively, however, hide the keyboard
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("Map_searched", "map search completed, filter: " + s);

                //Clear up all items, and just add those that pertain to the filer
                clusterManager.clearItems();
                for(RestaurantMarkerCluster marker : markers){
                    if(marker.getRestaurant().getName().toLowerCase().trim().contains(s)){
                        //Using full data set of markersFull to reference what should be added or removed
                        clusterManager.addItem(marker);
                    }
                }
                clusterManager.cluster();
                map.setOnMarkerClickListener(clusterManager);
                map.setInfoWindowAdapter(clusterManager.getMarkerManager());
                map.setOnInfoWindowClickListener(clusterManager);

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Empty on purpose
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //Shows the user's current location on the map
        placeGPSPosition();
        setMapFeatures();
        Log.e("boolean", "" + coordLaunch);

        Log.i("data_check", "latitude: " + inLAT + " | " + "longitude: " + inLONG);
        //If launched from the second activity (via coordinates), the toolbar subtitle is changed to
        //confine to the map fragment context
        if (inLAT != 0.0 && inLONG != 0.0) {
            changeToolbarText();
        }

        manager = Manager.getInstance(getContext());
//        List<Restaurant> restaurantList = manager.getRestaurantList();
        List<Restaurant> restaurantList = filterRestaurants();


        for(int i=0; i<restaurantList.size(); i++) {
            placePeg(restaurantList.get(i),ZOOM_AMNT, i);
        }
        markersFull = new ArrayList<>(markers);

        setUpClusterManager(map);
        clusterManager.getMarkerCollection();

        coordinateTappedByUser(coordLaunch);
        makeMarkerTextClickable();

        //Once the map detects movement, the re-centering will be disabled
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                recenterEnabled = false;
                Log.i("recenter_disabled", "recentering disabled through movement");
            }
        });
        map.setPadding(0,150,0,150);

    }

    private void placePeg(Restaurant restaurant, float zoom, int index) {

        RestaurantMarkerCluster newMarker = new RestaurantMarkerCluster(restaurant, manager, index);
        markers.add(newMarker);

    }

    private void placeGPSPosition() {

        //Below needs to be tested for functionality
        locationProvider = LocationServices.getFusedLocationProviderClient(this.getContext());

        try {
            if (permissionsGrantedFlag) {
                Task location = locationProvider.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.i("device_location", "placed user's gps location");

                            //Gets the location, which is then used to move the view to
                            Location curLocation = (Location) task.getResult();

                            //If the map fragment was launched from the second activity (via the coordinates click)
                            //Then the view will move to that restaurants location

                            //First check if launched from second activity
                            if (inLAT != 0.0 && inLONG != 0.0) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng
                                                (inLAT, inLONG),
                                        ZOOM_AMNT));
                                Log.i("recenter_enabled", "recentering disabled through coordinate launch");
                                recenterEnabled = false;

                            } else {
                                if (curLocation != null) {
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng
                                                    (curLocation.getLatitude(), curLocation.getLongitude()),
                                            ZOOM_AMNT));
                                }

                            }

                            //When the user clicks the my location button, it will bring back the view and also
                            //turn on recentering again
                            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){
                                @Override
                                public boolean onMyLocationButtonClick() {
                                    //Not animated as onCameraMoveListener also detects non-user movement
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng
                                                    (curLocation.getLatitude(), curLocation.getLongitude()),
                                                    ZOOM_AMNT));
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            recenterEnabled = true;
                                            Log.i("recenter_enabled", "recentering enabled through my location button");
                                        }
                                    }, 1500);
                                    return false;
                                }
                            });

                        } else {
                            Toast.makeText(getContext(), "Unable to track user",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //Places marker on the user's position
                map.setMyLocationEnabled(true);
            } else{
                //If location services are not provided (!permissionGrantedFlag), other functionality should
                //still be allowed so the user is able to geolocate restaurants
                if (inLAT != 0.0 && inLONG != 0.0) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng
                                    (inLAT, inLONG),
                            ZOOM_AMNT));
                }
            }

        } catch (SecurityException e) {
            Log.i("servicesClientException", "Security exception: " + e.getMessage());
        }
    }
    private List<Restaurant> filterRestaurants() {
        settings = FilterSettings.getInstance();
        manager = Manager.getInstance(getContext());

        List<Restaurant> result = new ArrayList<>();
        Log.e("settings in res list", "" + settings.getHazLevel());
        Log.e("settings in res list", "" + settings.getFavourite());

        if((!settings.getFavourite()) && (settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() == -1)) { // default case where user hasnt inputted any new settings
            result = manager.getRestaurantList();
        }
        else if((settings.getHazLevel().equals("all")) && (settings.getFavourite()) && (settings.getCriticalIssues() == -1)){ // case where only favourites is changed
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);
                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if (curRestaurant.isFavourite()) {
                        result.add(curRestaurant);
                    }
                }
            }
        }

        else if((!settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() == -1)){ // case where only haz level and maybe favourites is changed
            Log.e("settings in res list", "In here!");
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);
                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);
                    if (curRestaurant.isFavourite() == settings.getFavourite() && (mostRecentInspection.getHazardLevel().equalsIgnoreCase(settings.getHazLevel()))) {
                        Log.e("settings in res list", "Haz level in loop " +  mostRecentInspection.getHazardLevel());
                        Log.e("ResList", "" + curRestaurant.getName());
                        result.add(curRestaurant);
                    }
                }
            }

        }
        // need most recent inspection for haz level and crit issues
        else if((settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (settings.getGreaterThenInput() && settings.getLowerThenInput())) { // case where critical issues filter is inputted and user wants restaurants greater then that number
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);

                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite()) && (mostRecentInspection.getNumCritical() >= settings.getCriticalIssues())){
                        result.add(curRestaurant);
                    }
                }


            }

        }
        else if((!settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (settings.getGreaterThenInput() && !settings.getLowerThenInput())) { // crit issues filter + haz level filter
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);
                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite()) && (mostRecentInspection.getNumCritical() >= settings.getCriticalIssues()) &&
                            (mostRecentInspection.getHazardLevel().equalsIgnoreCase(settings.getHazLevel()))){
                        result.add(curRestaurant);
                    }
                }


            }
        }
        else if((settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (settings.getGreaterThenInput() && !settings.getLowerThenInput())) { // case where critical issues filter is inputted and user wants restaurants greater then that number
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);

                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite()) && (mostRecentInspection.getNumCritical() >= settings.getCriticalIssues())){
                        result.add(curRestaurant);
                    }
                }


            }

        }
        else if((!settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (!settings.getGreaterThenInput() && settings.getLowerThenInput())) { // crit issues filter with lower then crit issues true + haz level filter
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);
                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite()) && (mostRecentInspection.getNumCritical() <= settings.getCriticalIssues()) &&
                            (mostRecentInspection.getHazardLevel().equalsIgnoreCase(settings.getHazLevel()))){
                        result.add(curRestaurant);
                    }
                }


            }
        }
        else if((settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (!settings.getGreaterThenInput() && settings.getLowerThenInput())) { // case where critical issues filter is inputted and user wants restaurants greater then that number
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);

                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite()) && (mostRecentInspection.getNumCritical() <= settings.getCriticalIssues())){
                        result.add(curRestaurant);
                    }
                }
            }
        }
        else if((settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (!settings.getGreaterThenInput() && !settings.getLowerThenInput())) {
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);

                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite())) {
                        result.add(curRestaurant);
                    }

                }

            }
        }
        else if((!settings.getHazLevel().equals("all")) && (settings.getCriticalIssues() > -1) && (!settings.getGreaterThenInput() && !settings.getLowerThenInput())) {
            for(int i=0; i<manager.getRestaurantList().size(); i++) {
                Restaurant curRestaurant = manager.getRestaurantList().get(i);

                if((manager.getInspectionMap().get(curRestaurant.getTrackingNumber()) != null)) {
                    Inspection mostRecentInspection = manager.getInspectionMap().get(curRestaurant.getTrackingNumber()).get(0);

                    if((curRestaurant.isFavourite() == settings.getFavourite()) && mostRecentInspection.getHazardLevel().equalsIgnoreCase(settings.getHazLevel())) {
                        result.add(curRestaurant);
                    }
                }

            }
        }

        Log.e("size of result", "" + result.size());

        return result;
    }


    private void updateGPSPosition() {

        locationManger = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        //Since permissions are asked beforehand, error handling at this point would be redundant
        //However, required by the 'requestLocationUpdates' method
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
        locationManger.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10f, this);
        //map.setLocationSource(this);

        locationProvider = LocationServices.getFusedLocationProviderClient(this.getContext());
        //Higher priority == greater accuracy of coords on map
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //To shorten the amount of battery usage and taking into account the usage of the application,
        //the interval is set t0 4 seconds for the regular interval
        locationRequest.setFastestInterval(2000);
        locationRequest.setInterval(3000);

        locationProvider.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        }, Looper.getMainLooper()); //Looper passes the  main thread which is processed*/

    }

    @Override
    public void onLocationChanged(Location location) {
        if (gpsChangeListener != null) {
            gpsChangeListener.onLocationChanged(location);
        }

        //Only if the user clicks the recenter toggle will it start re-centering
        if(recenterEnabled && map != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng
                            (location.getLatitude(), location.getLongitude()),
                    ZOOM_AMNT));
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        gpsChangeListener = onLocationChangedListener;
    }


    private void setMapFeatures() {
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
    }


    private void getRequiredPermissions() {

        //Once the permissions have been displayed, check what the user's selection was
        //More specifically, make sure that it is correct
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permissionsGrantedFlag = true;
            } else{
                Toast.makeText(this.getContext(), "Some services may be unavailable", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsGrantedFlag = false;

        switch (requestCode) {
            case 0:
                //If the grantResults length is > 0, it implies that some permission (at least)
                //was granted
                if (grantResults.length > 0) {

                    //Since there may be multiple grant results, we loop to make sure that they're
                    //all true
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            permissionsGrantedFlag = false;

                            Log.i("invalid permissions", "required permissions not granted");
                            //If one of the permissions is not granted, then the functioning may
                            //be reduced to a point where we just exit the map
                            Toast.makeText(this.getContext(), "Required permission(s) not granted",
                                    Toast.LENGTH_SHORT).show();
                            getExitTransition(); //
                            return;
                        }

                        permissionsGrantedFlag = true;
                    }
                }
        }
    }



    private void changeToolbarText() {
        ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        toolbar.setSubtitle("Location");
    }

    @Override
    public void deactivate() {
        gpsChangeListener = null;
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Do nothing
    }

    @Override
    public void onProviderEnabled(String provider) {
        //Do nothing
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Do nothing
    }

}

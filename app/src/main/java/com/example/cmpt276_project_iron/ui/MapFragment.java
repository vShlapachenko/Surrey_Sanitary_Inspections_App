package com.example.cmpt276_project_iron.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cmpt276_project_iron.R;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener, LocationSource {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LAT = "lat";
    private static final String LONG = "long";

    // TODO: Rename and change types of parameters
    private Double inLAT = null;
    private Double inLONG = null;

    private boolean permissionsGrantedFlag = false;

    private FusedLocationProviderClient locationProvider;
    private LocationRequest locationRequest;
    private GoogleMap map;
    private LocationSource.OnLocationChangedListener gpsChangeListener;
    private LocationManager locationManger;

    private SupportMapFragment mapFragment;
    private OnFragmentInteractionListener mListener;

    private static final float ZOOM_AMNT = 17f;

    public MapFragment() {
        // Required empty public constructor
    }

    //Used if incoming from coords click
    public static MapFragment newInstance(double latitude, double longitude) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putDouble(LAT, latitude);
        args.putDouble(LONG, longitude);
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

        //Explicitly asks the user for permission for the required services
        getRequiredPermissions();

        //Tracks the user's location
        updateGPSPosition();


        if (getArguments() != null) {
            inLAT = getArguments().getDouble(LAT);
            inLONG = getArguments().getDouble(LONG);
        }
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Shows the user's current location on the map
        placeGPSPosition();
        setMapFeatures();

        Log.i("data_check", "latitude: " + inLAT + " | " + "longitude: " + inLONG);
        //If launched from the second activity (via coordinates), the toolbar subtitle is changed to
        //confine to the map fragment context
        if (inLAT != 0.0 && inLONG != 0.0) {
            changeToolbarText();
        }
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

                            } else {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng
                                                (curLocation.getLatitude(), curLocation.getLongitude()),
                                        ZOOM_AMNT));
                            }

                        } else {
                            Toast.makeText(getContext(), "Unable to track user",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.i("servicesClientException", "Security exception: " + e.getMessage());
        }

        //Places marker on the user's position
        map.setMyLocationEnabled(true);
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

        /**
         * Two ways need to be tested: Current deployment of locationListener+ and the
         * below sequence
         */
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

            //Move the camera to the user's location once it's available!
            //map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
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
        String[] req_permissons = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

        //Once the permissions have been displayed, check what the user's selection was
        //More specifically, make sure that it is correct
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permissionsGrantedFlag = true;
            } else {
                //If the permissions were not granted already (via the settings), ask for them
                ActivityCompat.requestPermissions(this.getActivity(), req_permissons, 0);
            }
        } else {
            ActivityCompat.requestPermissions(this.getActivity(), req_permissons, 0);
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

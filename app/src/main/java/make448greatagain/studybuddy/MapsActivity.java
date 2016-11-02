package make448greatagain.studybuddy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;

/**
 * Map Activity, Display Map and its Components
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /**
     * Map Object
     */
    private GoogleMap mMap;

    /**
     * API Client for location Updates
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Create the activity
     * @param savedInstanceState Android instance state
     * Auto Generated by Android Studio
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new LocationUpdater(this);
    }

    /**
     * Create the API Client for GoogleMaps and start it.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //mMap.setOnMarkerClickListener( new markerListener(this));
       // mMap.setOnInfoWindowClickListener(listener);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else
        {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        new NearbyClientsPlotter(mMap).start();


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();

            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            Log.d("MapsActivity","Start API Services");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else
        {
            Log.e("MapsActivity","API Services Start Error");
        }

    }

    /**
     * Do nothing, will revisit if needed
     * @param i Status code
     */
    @Override
    public void onConnectionSuspended(int i) {

    }
    /**
     * Do nothing, will revisit if needed
     * @param connectionResult Status Code
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Marker showing this user
     */
    Marker mCurrentLocationMarker = null;
    /**
     * Flag to ensure we only move the map once.
     */
    boolean firstUpdate = true;

    /**
     * Update the map to reflect a new user location.
     * @param location new Location
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d("MapsActivity","LOC CHANGE");
        Location mLastLocation = location;
       
        if (mCurrentLocationMarker != null) {
                mCurrentLocationMarker.remove();
            }

        //place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.snippet(UserManager.getUser().courseID + " " + UserManager.getUser().courseName + " " +UserManager.getUser().comments );
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mCurrentLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        if(firstUpdate){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            firstUpdate = false;
        }


        //stop location updates
        //if (mGoogleApiClient != null) {
           //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        //}
    }




    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    /**
     * Handle response to request permissions popup
     * @param requestCode type of request
     * @param permissions List of permissions
     * @param grantResults Result of permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    android.widget.Toast.makeText(this, "permission denied", android.widget.Toast.LENGTH_LONG).show();
                }
                break;
            }

            // other 'case' lines to check for other permissions this app might request.
        }
    }

    /**
     * Background process to add nearby markers to the map
     */
    private class NearbyClientsPlotter extends Thread{
        private GoogleMap mMap;
        final private LinkedList<Marker> markerLinkedList;
        NearbyClientsPlotter(GoogleMap mMap)
        {
            this.mMap = mMap;
            this.markerLinkedList = new LinkedList<>();
        }

        /**
         * Run the thread, looping forever with 10 second pauses.
         */
        public void run()
        {
            while(true)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (markerLinkedList) {
                            for (int i = 0; i < markerLinkedList.size(); i++) {
                                markerLinkedList.get(i).remove();
                            }
                        }
                    }
                });

                synchronized (NearbyClients.locations) {
                    for (int i = 0; i < NearbyClients.locations.size(); i++) {
                        LocationObject locationObject = NearbyClients.locations.get(i);
                        if(locationObject.user.equalsIgnoreCase(UserManager.getUser().username)){
                            continue;
                        }
                        Log.d("NearbyClientsPlotter", "Lat="+locationObject.lat + " Lng="+locationObject.lng);
                        LatLng latLng = new LatLng(locationObject.lat, locationObject.lng);
                        final MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(locationObject.user+"'s Position");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (markerLinkedList) {
                                    markerLinkedList.add(mMap.addMarker(markerOptions));
                                }
                            }
                        });
                    }
                }
                try{
                    sleep(10000);
                }catch(InterruptedException e)
                {
                    Log.e("NearbyClientsPlotter",e.getMessage());
                }
            }
        }
    }
}



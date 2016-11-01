package make448greatagain.studybuddy;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.ExecutionException;

/**
 * Background task for updating location in the mySQL database.
 * Created by Michael on 10/31/2016.
 */
class LocationUpdater implements  GoogleApiClient.ConnectionCallbacks,
        LocationListener {


    private GoogleApiClient mGoogleApiClient;
    private Context parentContext = null;

    LocationUpdater(Context context)
    {
        parentContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    public void onConnectionSuspended(int val)
    {}

    public void onLocationChanged(Location location){

        try {
            new DBLocationManager().execute(new LocationWrapper(location.getLatitude(), location.getLongitude())).get();
        }catch(InterruptedException | ExecutionException e){

        }
    }

    public void onConnected(Bundle bundle)
    {
        LocationRequest mLocationRequest = new LocationRequest();
        // 1 Minute intervals
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(parentContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }
}

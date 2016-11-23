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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Background task for updating location in the mySQL database.
 * Created by Michael on 10/31/2016.
 */
class LocationUpdater implements  GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    /**
     * Client to connect for API Calls
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Parent Context
     */
    private Context parentContext = null;

    /**
     * Create and start LocationUpdater
     * @param context Parent Context
     */
    private LocationUpdater(Context context)
    {
        parentContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private static LocationUpdater locationUpdater;
    public static void subscribeServices(Context context){
        locationUpdater = new LocationUpdater(context);
    }

    /**
     * Does nothing currently, will revisit if we see errors arising from this.
     * @param val Connection Code
     */
    public void onConnectionSuspended(int val)
    {}

    private  ExecutorService mThreadPool = Executors.newSingleThreadExecutor();

    /**
     * Upon Changing locations, store it in the DB
     * @param location New Location
     */
    public void onLocationChanged(final Location location){
        if(UserManager.getUser() != null) {
            UserManager.getUser().location = location;
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    update(new LocationWrapper(location.getLatitude(), location.getLongitude()));
                }
            });
        }
    }
    private Void update(final LocationWrapper loc){

        String username = loc.user.username;
        double lat = loc.lat;
        double lng = loc.lng;
        if(ConnectivityReceiver.isDataconnected()) {
            long time = System.currentTimeMillis();

            try {
                URL url = new URL("https://people.eecs.ku.edu/~mnavicka/Android/updateLocation.php");
                HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                postData(url, httpcon, username, lat, lng);

                httpcon.getInputStream().close();

                httpcon.disconnect();

            } catch (IOException e) {
                Log.e("DBLocationManager", e.getMessage());
            }
            time = System.currentTimeMillis()-time;
            Log.e(this.getClass().getSimpleName(),"Execution Time: "+time);
        }
        return null;
    }


    /**
     * Execute the Request
     * @param url URL to request from
     * @param httpcon HTTPConnection Instance
     * @param user UserName data to post
     * @param lat Latitude data to post
     * @param lng Longitude Data to post.
     * @throws IOException
     */
    private void postData(URL url, final HttpURLConnection httpcon, String user, double lat, double lng) throws IOException
    {
        Log.d("DBLocationManager", "URL " + url.toString());
        httpcon.setRequestMethod("POST");
        httpcon.setDoOutput(true);
        httpcon.setDoInput(true);
        OutputStream outputStream = httpcon.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        long date = Calendar.getInstance(timeZone).getTime().getTime();
        String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8")
                + "&" +
                URLEncoder.encode("lat", "UTF-8") + "=" + lat
                + "&" +
                URLEncoder.encode("lng", "UTF-8") + "=" + lng
                + "&" +
                URLEncoder.encode("time", "UTF-8") + "=" + date;
        bufferedWriter.write(post_data);
        bufferedWriter.flush();
        bufferedWriter.close();
        outputStream.close();
    }

    /**
     * On connecting to API, initialize location requests.
     * @param bundle Associated Bundle
     */
    public void onConnected(Bundle bundle)
    {
        LocationRequest mLocationRequest = new LocationRequest();
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

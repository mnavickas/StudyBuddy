package make448greatagain.studybuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Michael on 11/3/2016.
 * Listen for changes in data and gps connections
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    /**
     * Is data Connected?
     */
    private static volatile boolean dataconnected = true;
    /**
     * Is gps Connected?
     */
    private static volatile boolean gpsconnected = true;

    /**
     * Set data connection status
     * @param d data connection status
     */
    public static void setDataConnected(boolean d)
    {
        dataconnected = d;
    }

    /**
     * Set gps connection status
     * @param d gps connection status
     */
    public static void setGpsConnected(boolean d)
    {
        gpsconnected = d;
    }

    /**
     *  get Data Connection Status
     * @return data connection status
     */
    public static boolean isDataconnected(){
        return dataconnected;
    }

    /**
     * get gps Connection status
     * @return gps connection status
     */
    public static boolean isGpsconnected(){
        return gpsconnected;
    }

    /**
     * Handle changes from broadcast receiver
     * @param context parent context
     * @param intent intent containing the action
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(ConnectivityReceiver.class.getSimpleName(), "action: "
                + intent.getAction());
       ConnectivityManager cm = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if(cm != null ) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null) {
                dataconnected = ni.isConnected();
            } else {
                dataconnected = false;
            }
        }
        Log.e(ConnectivityReceiver.class.getSimpleName(), "Data action: "
                + (dataconnected?"Data Connected":"Data Not Connected"));

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(lm != null)
        {
            gpsconnected = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        Log.e(ConnectivityReceiver.class.getSimpleName(), "GPS action: "
                + (gpsconnected?"GPS Connected":"GPS Not Connected"));
    }
}